package simulator.models;

//import java.util.Comparator;
//Comparator<Car>
public class Car implements Comparable {
	
	
	public static final int CAR_CUSHION = 30; 
	
	private double arrivalTime;
	private double position;

	private double arrivalPosition;
	private double destination;
	private int currentLane;
	private double currentSpeed;
	private int direction;
	private Lane currentLaneObj;
	private int id;
	
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
	public Car(String configString) {
		String[] values = configString.split(",");
		this.arrivalTime = Double.parseDouble(values[0]);
		this.currentLane = Integer.parseInt(values[1]);
		this.currentSpeed = Double.parseDouble(values[2]);
		this.position = Double.parseDouble(values[3]);
		this.arrivalPosition = this.position;
		this.destination = Double.parseDouble(values[4]);
		this.direction = Integer.parseInt(values[5]);
		
		this.id = CarIdGenerator.generateId();
	
	}
	
	
	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getPosition() {
		return position;
	}
	
	public void setPosition(double position, Lane lane) {
		this.position = position;
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
	public void setLane(int lane) {
		this.currentLane = lane;
//		this.currentLaneObj = laneObj;
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
			Car oCar = (Car)other;
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
		// TODO Auto-generated method stub
		return 0;
	}


	public double getTimeTo(double newSpeed, double distanceToLight) {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean hitNextCar(double theoreticalTimeToLight) {
		// TODO Auto-generated method stub
		return false;
	}


	public void giveChangeSpeedCommand(double newSpeed) {
		// TODO Auto-generated method stub
		
	}

}
