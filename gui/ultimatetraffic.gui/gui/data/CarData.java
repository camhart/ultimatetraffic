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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(acceleration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		result = prime * result + iterationCount;
		result = prime * result + lane;
		result = prime * result + lightId;
		temp = Double.doubleToLongBits(position);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(velocity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
//		return toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarData other = (CarData) obj;
//		if(other.hashCode() != hashCode()) {
//			return false;
//		}
		if (Double.doubleToLongBits(acceleration) != Double
				.doubleToLongBits(other.acceleration))
			return false;
		if (id != other.id)
			return false;
		if (iterationCount != other.iterationCount)
			return false;
		if (lane != other.lane)
			return false;
		if (lightId != other.lightId)
			return false;
		if (Double.doubleToLongBits(position) != Double
				.doubleToLongBits(other.position))
			return false;
		if (Double.doubleToLongBits(velocity) != Double
				.doubleToLongBits(other.velocity))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CarData [id=" + id + ", iterationCount=" + iterationCount
				+ ", position=" + position + ", lane=" + lane + ", velocity="
				+ velocity + ", acceleration=" + acceleration + ", lightId="
				+ lightId + "]";
	}
}
