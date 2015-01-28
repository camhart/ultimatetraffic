package ultimatetraffic.simulator.models;

public class StopLight {
	double timeOfNextChange;
	Color currentLightColor;
	int lightId;
	
	public enum Color {
		GREEN, RED
	}

	public boolean changed(double timeStamp) {
		if(timeStamp >= timeOfNextChange) {
			if(currentLightColor == Color.GREEN) {
				currentLightColor = Color.RED;
			} else {
				currentLightColor = Color.GREEN;
			}
			return true;
		}
		return false;
	}
}
