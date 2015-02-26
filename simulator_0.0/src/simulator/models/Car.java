package simulator.models;

public class Car {
	
	private double arrivalTime;
	private double position;
	private double destination;
	private int currentLane;
	private int lane;
	private double currentSpeed;
	private int direction;

	public Car(String configString) {
		String[] values = configString.split(",");
		this.arrivalTime = Double.parseDouble(values[0]);
		this.lane = Integer.parseInt(values[1]);
		this.currentSpeed = Double.parseDouble(values[2]);
		this.position = Double.parseDouble(values[3]);
		this.destination = Double.parseDouble(values[4]);
		this.direction = Integer.parseInt(values[5]);
	
	}
	
	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getPosition() {
		return position;
	}

	public boolean hasFinished() {
		//returns true if the car has made it to it's end position
		return false;
	}
	
	public void setLane(int lane) {
		this.currentLane = lane;
	}

	public int getLane() {
		return this.currentLane;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
