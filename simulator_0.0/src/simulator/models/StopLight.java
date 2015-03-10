package simulator.models;

import java.util.ArrayList;
import java.util.Iterator;

import simulator.Simulator;
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
		
		Iterator<CarManager> lane1Iter = lane1.getIterable();
		Iterator<CarManager> lane2Iter = lane2.getIterable();
		CarManager lane1Car = null;
		CarManager lane2Car = null;
		
		ArrayList<CarManager> lane1Removes = new ArrayList<CarManager>();
		ArrayList<CarManager> lane2Removes = new ArrayList<CarManager>();
		
		ArrayList<CarManager> finishingCars = new ArrayList<CarManager>();
		
		while(lane1Iter.hasNext() || lane2Iter.hasNext()) {
			
			if(lane1Iter.hasNext()) {
				lane1Car = lane1Iter.next();
				handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
			}
			
			if(lane2Iter.hasNext()) {
				lane2Car = lane2Iter.next();
				handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
			}	
		}
		
		for(CarManager c : finishingCars) {
			phase.handlePotentialCarFinish(c,  this);
		}
		
		for(CarManager c : lane1Removes) {
			if(!this.lane1.removeCar(c))
				throw new Error("Unable to remove car from lane.");
		}
		
		for(CarManager c : lane2Removes) {
			if(!this.lane2.removeCar(c))
				throw new Error("Unable to remove car from lane.");
		}
	}
	
	public void handleCar(PhaseHandler phase, CarManager car, Lane lane, ArrayList<CarManager> removeList, ArrayList<CarManager> finishingCars) {
		phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(car, this);
		if(car.hasFinished()) {
			finishingCars.add(car);
		}
//		phase.handlePotentialCarFinish(car, this, finishingCars);
		
		if(nextLight != null && !car.hasFinished() && car.getPosition() >= getPosition()) {
//			System.out.println("Jumping lights! " + car.getPosition() + " - " + nextLight.getPosition());
			//add the car to the next lane 
			// 	phase handler might have changed the lane so we need to check
			if(car.getLane() == 1) {
				nextLight.getLane1().addCar(car);
				car.setLane(1, nextLight.getLane1());
			} else if(car.getLane() == 2) {
				nextLight.getLane2().addCar(car);
				car.setLane(2, nextLight.getLane2());
			}
			
			//add the car to the remove list
			removeList.add(car);
		}
	}

	/**
	 * Should only be called when a car reaches it's destination
	 * @param car
	 */
	public void removeCarFromLane(CarManager car) {
		assert car.getPosition() >= car.getDestination() : "Car hasn't finished traveling...";
		
		car.getLaneObject().removeCar(car);
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
		return this.position;
	}

	public Color getCurrentColor() {
		// TODO Auto-generated method stub
		return this.currentColor;
	}

	public void setColor(Color color) {
		// TODO Auto-generated method stub
		this.currentColor = color;
		
	}
	
	public boolean isLightGreenAtTime(double time) {
		boolean willBeGreen = false;
		if(this.currentColor == Color.GREEN){
			willBeGreen = true;
		}
		double newTimeUntilChange = timeUntilColorChange;
		while(time > newTimeUntilChange){
			time -= newTimeUntilChange;
			if(willBeGreen) {	//switch to red count
				newTimeUntilChange = timeAsRed;
			}
			else{	//switch to green count
				newTimeUntilChange = timeAsGreen;
			}
			willBeGreen = !willBeGreen;	//change future light state
		}
		return willBeGreen;
	}

	public void setPrevLight(StopLight stopLight) {
		this.prevLight = stopLight;
	}
	
	public void setNextLight(StopLight stopLight) {
		this.nextLight = stopLight;
	}

	public double getTimeUntilChange() {
		return this.timeUntilColorChange;
	}

	public boolean justChangedColor() {
		return this.timeUntilColorChange == this.timeAsGreen || this.timeUntilColorChange == this.timeAsRed;
	}
}
