package simulator.models;

import java.util.ArrayList;
import java.util.Iterator;

import simulator.models.StopLight.Color;
import simulator.outputter.Outputter;
import simulator.phases.PhaseHandler;

public class StopLight {
	private StopLight nextLight;
	private StopLight prevLight;
	
	
	private Lane lane1;
	private Lane lane2;
	
	private Color currentColor;
	private double timeAsRed;
	private double timeAsGreen;
	private double timeUntilColorChange;
	private double position;
	private String lightType;
	private double initialOffset;
	private int id;
	
	public enum Color {
		GREEN, RED
	}
	
	/**
	 * Builds a stoplight object based on a config string.  For
	 * 	config string format see loadStopLights method in simulator.
	 * @param configString
	 */
	public StopLight(String configString) {
		String[] values = configString.split(",");
		this.position = Double.parseDouble(values[0]);
		this.lightType = values[1];
		this.timeAsGreen = Double.parseDouble(values[2]);
		this.timeAsRed = Double.parseDouble(values[3]);
		this.initialOffset = Double.parseDouble(values[4]);
		lane1 = new Lane();
		lane2 = new Lane();
		lane1.otherLane = lane2;
		lane2.otherLane = lane1;
		
		setTimeUntilColorChange();
		
		this.id = LightIdGenerator.generateId();
	}
	
	/**
	 * Will set timeUntilColorChange value according to CURRENT color.  So
	 * 	ensure the color gets changed prior to calling this.
	 */
	private void setTimeUntilColorChange() {
		if(this.currentColor == Color.GREEN)
			this.timeUntilColorChange = this.timeAsGreen;
		else
			this.timeUntilColorChange = this.timeAsRed;
	}
	
	/**
	 * Gets the next stop light
	 * @return
	 */
	public StopLight getNextLight() {
		return nextLight;
	}
	
	/**
	 * Gets the previous stop light
	 * @return
	 */
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
	 * This handles all of the timing portions of the stoplight.
	 * 	It decrements the timeUntilColorChange variable.  It
	 * 	changes the color if necessary.  It will write output
	 * 	to the outputter when colors change.
	 * 	
	 * @param timePassed
	 */
	public void handleLightColors(double timePassed) {
		timeUntilColorChange-=timePassed;
		if(timeUntilColorChange < 0) {
			if(this.currentColor == Color.GREEN) {
				this.currentColor = Color.RED;
				this.timeUntilColorChange = this.timeAsRed;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			} else {
				this.currentColor = Color.GREEN;
				this.timeUntilColorChange = this.timeAsGreen;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			}
		}
	}

	/**
	 * This handles a single iteration.  It will take care of everything
	 * 	within the stopLight's realm of knowledge including:
	 * 		the stop lights timing, it's associated lanes,
	 * 		the cars within each lane.
	 * @param phase
	 * @param timePerIteration
	 */
	public void iterate(PhaseHandler phase, double timePerIteration) {
		
		this.handleLightColors(timePerIteration);
		
		Iterator<Car> lane1Iter = lane1.getIterable();
		Iterator<Car> lane2Iter = lane2.getIterable();
		Car lane1Car = null;
		Car lane2Car = null;
		
		ArrayList<Car> lane1Removes = new ArrayList<Car>();
		ArrayList<Car> lane2Removes = new ArrayList<Car>();
		
		while(lane1Iter.hasNext() || lane2Iter.hasNext()) {
			
			if(lane1Iter.hasNext()) {
				lane1Car = lane1Iter.next();
				phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(lane1Car, this);
				
				if(nextLight != null && lane1Car.getPosition() >= nextLight.getPosition()) {
					
					//add the car to the next lane 
					// 	phase handler might have changed the lane so we need to check
					if(lane1Car.getLane() == 1) {
						nextLight.getLane1().addCar(lane1Car);
					} else if(lane1Car.getLane() == 2) {
						nextLight.getLane2().addCar(lane1Car);
					}
					
					//add the car to the remove list
					lane1Removes.add(lane1Car);
				}
				
			}
			
			if(lane2Iter.hasNext()) {
				lane2Car = lane2Iter.next();
				phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(lane2Car, this);
				
				if(nextLight != null && lane2Car.getPosition() >= nextLight.getPosition()) {
					
					//add the car to the next lane 
					// 	phase handler might have changed the lane so we need to check
					if(lane2Car.getLane() == 1) {
						nextLight.getLane1().addCar(lane2Car);
					} else if(lane2Car.getLane() == 2) {
						nextLight.getLane2().addCar(lane2Car);
					}
					
					//add the car to the remove list
					lane2Removes.add(lane2Car);
				}
			}	
		}
		
		for(Car c : lane1Removes) {
			this.lane1.removeCar(c);
		}
		for(Car c : lane2Removes) {
			this.lane2.removeCar(c);
		}
	}

	/**
	 * Should only be called when a car reaches it's destination
	 * @param car
	 */
	public void removeCarFromLane(Car car) {
		assert car.getPosition() >= car.getDestination() : "Car hasn't finished traveling...";
		
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
	
	private static class LightIdGenerator {
		private static int currentValue = 0;
		public static int generateId() {
			return currentValue++;
		}
	}

	public int getId() {
		return this.id;
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
