package simulator.models;

import java.util.ListIterator;

import simulator.Simulator;
import simulator.models.car.Car;
import simulator.models.car.Car.Command;
import simulator.models.stoplights.StopLight;
import simulator.phases.Phase0Handler;

//import java.util.Comparator;
//Comparator<Car>
public class CarManager implements Comparable {
	
	public static final double CAR_CUSHION = 10.0; //in meters
	public static final double CAR_STOP_CUSHION = 7.5; //in meters
	public static final double TIME_CUSHION = 0.5;
	
	private double arrivalTime;
	private Car car;

	private double arrivalPosition;
	private double destination;
	private int currentLane;
	private double currentSpeed;
	private int direction;
	private Lane currentLaneObj;
	private int id;
	private double targetSpeed;
	private int totalIterations = 0;
	
	private static class CarIdGenerator {
		private static int currentValue = 0;
		public static int generateId() {
			return currentValue++;
		}
	}

	/**
	 * Takes a config string that matches the formate of a
	 * car input file and creates a car object out of it.
	 * @param configString
	 */
	public CarManager(String configString) {
		String[] values = configString.split(",");
		this.arrivalTime = Double.parseDouble(values[0]);
		this.currentLane = Integer.parseInt(values[1]);
		this.currentSpeed = Double.parseDouble(values[2]);
		this.arrivalPosition = Double.parseDouble(values[3]);
		this.destination = Double.parseDouble(values[4]);
		this.direction = Integer.parseInt(values[5]);
		
		this.id = CarIdGenerator.generateId();
		
		this.car = new Car(this.currentSpeed, Simulator.TIME_PER_ITERATION, this.arrivalPosition);
	
	}
	
	public void iterate() {
		this.totalIterations++;
	}
	
	
	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getPosition() {
		return this.car.getPosition();
	}
	
	public double getAcceleration() {
		return this.car.getAcceleration();
	}
	
	public double getVelocity() {
		return this.car.getVelocity();
	}
	
	public void setPosition(double position, Lane lane) {
		this.car.setPosition(position);
	}

	public boolean hasFinished() {
		return this.getPosition() >= this.destination;
	}
	
	public int getOtherLane(){
		if(this.currentLane == 1)
			return 2;
		else
			return 1;
	}
	
	
	/**
	 * Should be set whenever a car changes lanes
	 * @param lane
	 */
	public void setLane(int lane, Lane laneObj) {
		this.currentLane = lane;
		this.currentLaneObj = laneObj;
	}
	
	
	public int getLane() {
		return this.currentLane;
	}
	
	public Lane getLaneObject() {
		return this.currentLaneObj;
	}

	public int getId() {
		return this.id;
	}

	public double getArrivalPosition() {
		return this.arrivalPosition;
	}

	@Override
	public int compareTo(Object other) {
		if(other == null )
			return -1;
		else if(other.getClass() != this.getClass())
			return 1;
		else {
			CarManager oCar = (CarManager)other;
			if(this.getPosition() < oCar.getPosition())
				return -1;
			else if(this.getPosition() > oCar.getPosition())
				return 1;
			else
				return 0;
		}
	}


	public double getDestination() {
		return this.destination;
	}


	public double moveCarForward() {
		iterate();
		return car.moveCarForward();
	}


	public double getTimeTo(double newSpeed, double distanceToLight) {
		return this.car.getTimeTo(newSpeed, distanceToLight);
	}


	public boolean hitNextCar(double theoreticalTimeToLight, double lightPosition) {
		CarManager nextCar = getLaneObject().getNextCar(this);
		if(nextCar != null && nextCar.getPosition() == this.car.getPosition()){//You're the only car on the road!
			return false;
		}
		else if(nextCar != null && nextCar.getTimeTo(nextCar.targetSpeed, lightPosition - nextCar.getPosition()) > theoreticalTimeToLight - TIME_CUSHION){
			return true;
		}
		//if there is no next car return false? The above if() checks if you're the only car on the stretch of road
		
		return false;
	}


	public void giveChangeSpeedCommand(double newSpeed, Command command) {
		this.car.giveChangeSpeedCommand(newSpeed, command);
		targetSpeed = newSpeed;
	}
	
	public boolean canRunLight(StopLight light) {
		assert (this.getLane() == 1 ? light.getLane1() : light.getLane2()) == this.getLaneObject() : "Lane objects don't match";
		
//		if(light.getPosition() - Phase0Handler.RUN_YELLOW_LIGHT_DISTANCE < this.getPosition()) {
//			return true;
//		} 
		
		boolean foundMe = false;
		ListIterator<CarManager> iter = this.getLaneObject().getIterable();
		CarManager curCar;
		while(iter.hasNext()) {
			curCar = iter.next();
			if(curCar == this) {
				foundMe = true;
			} else if(foundMe) {
				if(curCar.getCar().getCommand() == Car.Command.STOP) {
					return false;
				}
			}			
		}
		return true;
	}
	
	public double getStopDistance(StopLight light) {
		
		assert (this.getLane() == 1 ? light.getLane1() : light.getLane2()) == this.getLaneObject() : "Lane objects don't match";
		assert (this.getPosition() < light.getPosition()) : "Car is ahead of light";
		
		int stoppingCarsInFrontOfMe = 0;
		boolean foundMe = false;
		ListIterator<CarManager> iter = this.getLaneObject().getReverseIterable();
		CarManager curCar;
		
		while(iter.hasPrevious()) {
			curCar = iter.previous();
			if(curCar.getId() == this.getId()) {
				foundMe = true;
			} else if(foundMe) {
				Car.Command command = curCar.getCar().getCommand();
				
				if(curCar.getCar().getCommand() == Car.Command.STOP && curCar.getCar().getPosition() < light.getPosition()) {
					stoppingCarsInFrontOfMe++;
				}
//				else 
//					car not stopping
			}			
		}
		
		double value = ((light.getPosition() - (stoppingCarsInFrontOfMe * CarManager.CAR_STOP_CUSHION)) - this.getPosition());
//		System.out.println(String.format("(%f - (%d * %f)) - %f = %f", light.getPosition(), stoppingCarsInFrontOfMe, CarManager.CAR_STOP_CUSHION, this.getPosition(), value));
		assert value > 0 : "Crash! " + value + ((this.getLaneObject().getParentLight().getClass() == StopLight.class) ? "\n Consider adjusting Phase0Handler.RUN_YELLOW_LIGHT_DISTANCE" : " no clue what's going on...");
		
		// (light position - length of all cars stopped in front of me) - car position
		return value - 1.0;		
	}

	public Car getCar() {
		// TODO Auto-generated method stub
		return this.car;
	}

	public int getIterations() {
		return this.totalIterations;
	}

	/**
	 * Should occur only with phase 0.
	 */
	public void giveStopCommand(double distance) {
//		assert Simulator.getSimulator().getPhase() == 0 : "Calling stop in something other than phase 0";
		assert this.getLaneObject().getParentLight().getClass() == StopLight.class : "Calling stop in something other than phase 0";
		
//		System.out.println(String.format("Iteration: %d, Car: %d (%.2f), Light %d (%.2f), TotalDistance: %f (%f)", Simulator.getSimulator().getCurrentIteration(), 
//				this.id, this.car.getPosition(), this.getLaneObject().getParentLight().getId(),
//				this.getLaneObject().getParentLight().getPosition() , distance, this.getLaneObject().getParentLight().getPosition() - this.car.getPosition()));
		this.car.giveStopCommand(distance);
	}
	
	/**
	 * Should occur only with phase 0.
	 */
	public void giveGoCommand() {
		assert this.getLaneObject().getParentLight().getClass() == StopLight.class : "Calling stop in something other than phase 0";
		this.car.giveGoCommand();
	}

}
