package simulator.models;

import simulator.Simulator;
import simulator.models.car.Car;

//import java.util.Comparator;
//Comparator<Car>
public class CarManager implements Comparable {
	 
	
	public static final double CAR_CUSHION = 30.0;
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


	public boolean hitNextCar(double theoreticalTimeToLight, double distanceToLight) {
		CarManager nextCar = getLaneObject().getNextCar();
		if(nextCar != null && nextCar.getPosition() == this.car.getPosition()){//You're the only car on the road!
			return false;
		}
		else if(nextCar != null && nextCar.getTimeTo(nextCar.targetSpeed, distanceToLight) < theoreticalTimeToLight + TIME_CUSHION){
			return true;
		}
		//if there is no next carr return false?
		
		return false;
	}


	public void giveChangeSpeedCommand(double newSpeed) {
		System.out.println("speed set to" + newSpeed);
		this.car.giveChangeSpeedCommand(newSpeed);
		targetSpeed = newSpeed;
	}

	public int getIterations() {
		return this.totalIterations;
	}

	/**
	 * Should occur only with phase 0.
	 */
	public void giveStopCommand(double distance) {
		assert Simulator.getSimulator().getPhase() == 0 : "Calling stop in something other than phase 0"; 
		this.car.giveStopCommand(distance);
	}
	
	/**
	 * Should occur only with phase 0.
	 */
	public void giveGoCommand() {
		assert Simulator.getSimulator().getPhase() == 0 : "Calling stop in something other than phase 0";
//		this.car.go();
		this.car.giveGoCommand();
	}

}
