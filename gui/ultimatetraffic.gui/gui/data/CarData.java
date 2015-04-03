package gui.data;

public class CarData {
	
	public static final int CarLength = 5;
	public static final int CarHeight = 3;
	
	private int id;
	private int iterationCount;
	private double position;
	private int lane;
	private double velocity;
	private double acceleration;
	private int lightId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIterationCount() {
		return iterationCount;
	}
	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}
	public double getPosition() {
		return position;
	}
	public void setPosition(double position) {
		this.position = position;
	}
	public int getLane() {
		return lane;
	}
	public void setLane(int lane) {
		this.lane = lane;
	}
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	public double getAcceleration() {
		return acceleration;
	}
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
	public void setLightId(int lightId) {
		this.lightId = lightId;
	}
	public int getLightId() {
		return this.lightId;
	}
}
