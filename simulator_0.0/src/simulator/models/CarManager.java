package simulator.models;

import java.util.ListIterator;

import simulator.Simulator;
import simulator.models.car.Car;
import simulator.models.car.Car.Command;
import simulator.models.stoplights.StopLight;

public class CarManager implements Comparable {
	
	public static final double CAR_CUSHION = 10.0; //in meters
	public static final double CAR_STOP_CUSHION = 7.0; //in meters
	public static final double TIME_CUSHION = 1.5; // was 0.5
	public static final double ACCELERATION_DELAY_MIN = 2.0; //adjust this to change the minimum delay between cars accelerating for phase 0
	public static final double ACCELERATION_DELAY_MAX = 2.6; //adjust this to change the maximum delay between cars accelerating for phase 1 
	
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
	
	private int accelerateDelay = 0;
	
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
			return -1;
		else {
			CarManager oCar = (CarManager)other;
//			return Double.compare(oCar.getPosition(), this.getPosition());
			if(this.getPosition() > oCar.getPosition())
				return -1;
			else if(this.getPosition() < oCar.getPosition())
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
		if(nextCar != null) {
			//car in front of us
			double nextCarsTimeToLight = nextCar.getTimeTo(nextCar.targetSpeed, lightPosition - nextCar.getPosition());
			if(nextCarsTimeToLight > theoreticalTimeToLight - TIME_CUSHION) {
				//we will hit them
				return true;
			}
		}
		
		return false;
	}

	public void giveDelayedChangeSpeedCommand(double newSpeed, Command command) {
		if(accelerateDelay <= 0) {
			this.car.giveChangeSpeedCommand(newSpeed, command);
			targetSpeed = newSpeed;
		}
		else {
			accelerateDelay--;
		}
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
		
		if(this.getPosition() >= light.getPosition())
			return Double.MAX_VALUE;
		
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
//		value = value - 1.0;
//		assert value > -2.5 : "Crash! car: " + this.getId() + " " + value + ((this.getLaneObject().getParentLight().getClass() == StopLight.class) ? "\n Consider adjusting Phase0Handler.RUN_YELLOW_LIGHT_DISTANCE" : " no clue what's going on...");
		
		if(value < 0)
			value = 0;
		
		// (light position - length of all cars stopped in front of me) - car position
		return value;		
	}

	private Car getCar() {
		return this.car;
	}

	public int getIterations() {
		return this.totalIterations;
	}

	/**
	 * Should occur only with phase 0.
	 */
	public void giveStopCommand(double distance) {
		assert this.getLaneObject().getParentLight().getClass() == StopLight.class : "Calling stop in something other than phase 0";
		assert distance >= 0 : "telling us to stop behind us";
		
		resetAccelerationDelay();
		
		this.car.giveStopCommand(distance);
	}
	
	public double getStopPosition() {
		//assert this.getCommand() == Command.STOP : "getting the stop position when we aren't stopped";
		return car.getStopPosition();
	}
	
	/**
	 * Should occur only with phase 0.
	 */
	public void giveGoCommand() {
		assert this.getLaneObject().getParentLight().getClass() == StopLight.class : "Calling stop in something other than phase 0";
		if(accelerateDelay <= 0) {
			this.car.giveGoCommand();
		}
		else {
			accelerateDelay--;
		}
	}

	@Override
	public String toString() {
		return "CarManager [arrivalTime=" + arrivalTime + ", car=" + car
				+ ", arrivalPosition=" + arrivalPosition + ", destination="
				+ destination + ", currentLane=" + currentLane
				+ ", currentSpeed=" + currentSpeed + ", direction=" + direction
				+ ", currentLaneObj=" + currentLaneObj + ", id=" + id
				+ ", targetSpeed=" + targetSpeed + ", totalIterations="
				+ totalIterations + ", accelerateDelay=" + accelerateDelay
				+ "]";
	}

	public Command getCommand() {
		return this.car.getCommand();
	}
	
	public double getTotalEnergyUsed() {
		return this.car.getEnergyUsed();
	}

	public double getTargetVelocity() {
		// TODO Auto-generated method stub
		return car.getTargetVelocity();
	}

	public void resetAccelerationDelay() {
		int min = (int) (ACCELERATION_DELAY_MIN / Simulator.TIME_PER_ITERATION);
		int max = (int) (ACCELERATION_DELAY_MAX / Simulator.TIME_PER_ITERATION);
		
		accelerateDelay = min + (int)(Math.random() * (max - min));
	}
	

}
