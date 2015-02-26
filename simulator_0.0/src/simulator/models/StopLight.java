package simulator.models;

import simulator.models.StopLight.Color;
import simulator.outputter.Outputter;
import simulator.phases.Phase;

public class StopLight {
	private StopLight nextLight;
	private StopLight prevLight;
	
	
	private Lane lane1;
	private Lane lane2;
	
	private Color currentColor;
	private double timeAsRed;
	private double timeAsGreen;
	private double timeUntilChange;
	private double position;
	private String lightType;
	private double initialOffset;
	
	public enum Color {
		GREEN, RED
	}
	
	public StopLight(String configString) {
		String[] values = configString.split(",");
		this.position = Double.parseDouble(values[0]);
		this.lightType = values[1];
		this.timeAsGreen = Double.parseDouble(values[2]);
		this.timeAsRed = Double.parseDouble(values[3]);
		this.initialOffset = Double.parseDouble(values[4]);
		lane1 = new Lane();
		lane2 = new Lane();
		
		setTimeUntilChange();
	}
	
	/**
	 * Will set time until change value according to CURRENT color.
	 */
	private void setTimeUntilChange() {
		if(this.currentColor == Color.GREEN)
			this.timeUntilChange = this.timeAsGreen;
		else
			this.timeUntilChange = this.timeAsRed;
	}
	
	public StopLight getNextLight() {
		return nextLight;
	}
	public StopLight getPrevLight() {
		return prevLight;
	}
	public Lane getLane1() {
		return lane1;
	}
	public Lane getLane2() {
		return lane2;
	}
	
	
	/**
	 * 
	 * @param timePassed
	 */
	public void handleLightColors(double timePassed) {
		timeUntilChange-=timePassed;
		if(timeUntilChange < 0) {
			if(this.currentColor == Color.GREEN) {
				this.currentColor = Color.RED;
				this.timeUntilChange = this.timeAsRed;
				setTimeUntilChange();
				Outputter.getOutputter().addLightOutput(this);
			} else {
				this.currentColor = Color.GREEN;
				this.timeUntilChange = this.timeAsGreen;
				setTimeUntilChange();
				Outputter.getOutputter().addLightOutput(this);
			}
		}
	}

	public void iterate(Phase phase, double timePerIteration) {
		
		this.handleLightColors(timePerIteration);
		
		int carIndex = 0;
		Car lane1Car = lane1.getCar(carIndex);
		Car lane2Car = lane2.getCar(carIndex);
		
		while(lane1Car != null || lane2Car != null) {
			carIndex++;
			
			if(lane1Car != null) {
				phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(lane1Car, nextLight);
				lane1Car = lane1.getCar(carIndex);
			}
			
			if(lane2Car != null) {
				phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(lane2Car, nextLight);
				lane2Car = lane2.getCar(carIndex);
			}	
		}
	}

	public void removeCarFromLane(Car car) {
		if(car.getLane() == 0) {
			if(!this.lane1.removeCar(car)) {
				throw new Error();
			}
		}
		else {
			if(!this.lane2.removeCar(car))
				throw new Error();
		}
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Color getCurrentColor() {
		// TODO Auto-generated method stub
		return this.currentColor;
	}

	public void setColor(Color color) {
		// TODO Auto-generated method stub
		this.currentColor = color;
		
	}

	public void setPrevLight(StopLight stopLight) {
		this.prevLight = stopLight;
	}
	
	public void setNextLight(StopLight stopLight) {
		this.nextLight = stopLight;
	}
}
