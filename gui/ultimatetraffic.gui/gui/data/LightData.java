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
}
