package gui.data;

public class LightData {
	private int id;
	private int iterationCount;
	private double position;
	private Color color;
	private double timeUntilChange;
	
	public enum Color {
		GREEN, RED, YELLOW
	}

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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setTimeUntilChange(double time) {
		this.timeUntilChange = time;
	}

	public double getTimeUntilChange() {
		return this.timeUntilChange;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof LightData))
//            return false;
//        if (obj == this)
//            return true;
//        
//        LightData other = (LightData)obj;
//        
//        if(other.color != color || other.id != id ||
//        		other.iterationCount != iterationCount ||
//        		other.position != position || other.timeUntilChange != timeUntilChange)
//        	return false;
//        
//		return true;
//	}
	
//	@Override
//	public int hashCode() {
//		return new String(String.format("%s %d %d %f %f", color, id, iterationCount, position, timeUntilChange)).hashCode();
//	}

	@Override
	public String toString() {
		return "LightData [id=" + id + ", iterationCount=" + iterationCount
				+ ", position=" + position + ", color=" + color
				+ ", timeUntilChange=" + timeUntilChange + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + id;
		result = prime * result + iterationCount;
		long temp;
		temp = Double.doubleToLongBits(position);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(timeUntilChange);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LightData other = (LightData) obj;
		if (color != other.color)
			return false;
		if (id != other.id)
			return false;
		if (iterationCount != other.iterationCount)
			return false;
		if (Double.doubleToLongBits(position) != Double
				.doubleToLongBits(other.position))
			return false;
		if (Double.doubleToLongBits(timeUntilChange) != Double
				.doubleToLongBits(other.timeUntilChange))
			return false;
		return true;
	}
	
	
}
