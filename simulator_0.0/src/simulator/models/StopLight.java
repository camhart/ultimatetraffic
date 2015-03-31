package simulator.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import simulator.Simulator;
import simulator.models.StopLight.Color;
import simulator.outputter.Outputter;
import simulator.phases.Phase1Handler;
import simulator.phases.PhaseHandler;

public class StopLight {
	private StopLight nextLight;
	private StopLight prevLight;
	
	private static int MAX_EARNED_TIME = 12;
	
	
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
	private int greenTimesEarned;
	//private int[] lightTimes;
	private ArrayList<Integer> lightTimes;
	
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
		this.currentColor = Color.valueOf(values[5].toUpperCase());
		lane1 = new Lane(this);
		lane2 = new Lane(this);
		lane1.otherLane = lane2;
		lane2.otherLane = lane1;
		
		this.greenTimesEarned = 3;
		this.lightTimes = new ArrayList<Integer>();
		this.lightTimes.add(1);
		
		//setTimeUntilColorChange(); //I'm removing this, because it doesn't take any advantage of creating offsets on timing.
		if(this.currentColor == Color.GREEN)
			this.timeUntilColorChange = this.timeAsGreen - this.initialOffset;
		else
			this.timeUntilColorChange = this.timeAsRed - this.initialOffset;
		
		this.id = LightIdGenerator.generateId();
	}
	
	/**
	 * Will set timeUntilColorChange value according to CURRENT color.  So
	 * 	ensure the color gets changed prior to calling this.
	 */
	private void setTimeUntilColorChange() {
		if(this.lightType == "phase2"){
			lightTimes.set(0, lightTimes.get(0)-1);
			if(lightTimes.get(0) <= 0){//change color
				this.lightTimes.remove(0);
				if(this.lightTimes.size() < 1){ //This means no cars have requested Green in the future, so we leave the light red
					this.lightTimes.add(1);
					this.currentColor = Color.RED;
					this.timeUntilColorChange = this.timeAsRed;
					addGreenTime();
				}
				else{
					if(this.currentColor == Color.GREEN){
						this.currentColor = Color.RED;
						this.timeUntilColorChange = this.timeAsRed;
					}
					else{
						this.currentColor = Color.GREEN;
						this.timeUntilColorChange = this.timeAsGreen;
					}
				}
			}
			else{//don't change
				if(this.currentColor == Color.GREEN){
					this.timeUntilColorChange = this.timeAsGreen;
				}
				else{
					this.timeUntilColorChange = this.timeAsRed;
				}
			}
		}
		else{ //Phases 0 and 1
			if(this.currentColor == Color.GREEN){
				this.currentColor = Color.RED;
				this.timeUntilColorChange = this.timeAsRed;
			}
			else{
				this.currentColor = Color.GREEN;
				this.timeUntilColorChange = this.timeAsGreen;
			}
		}
	}
	
	public void addGreenTime(){
		if(this.greenTimesEarned < this.MAX_EARNED_TIME){
			greenTimesEarned++;
		}
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
	public void handleLightColors(double timePassed, PhaseHandler phase) {
		timeUntilColorChange-=timePassed;
		if(timeUntilColorChange < 0) {
			if(this.currentColor == Color.GREEN) {
				//this.currentColor = Color.RED;
				//this.timeUntilColorChange = this.timeAsRed;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			} else {
				//this.currentColor = Color.GREEN;
				//this.timeUntilColorChange = this.timeAsGreen;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
				//Call the algorithm on current cars to catch rounding errors on cars approaching the newly green light
				if(phase.getPhase() == 1){
					this.CallIntermediateAlgorithmOnAllCars(phase);
				}
			}
		}
	}
	
	public void CallIntermediateAlgorithmOnAllCars(PhaseHandler phase){
		Iterator<CarManager> lane1Iter = lane1.getIterable();
		Iterator<CarManager> lane2Iter = lane2.getIterable();
		CarManager lane1Car = null;
		CarManager lane2Car = null;
		
		while(lane1Iter.hasNext() || lane2Iter.hasNext()) {
			
			if(lane1Iter.hasNext()) {
				lane1Car = lane1Iter.next();
				phase.intermediateAlgorithm(lane1Car, this);
			}
			
			if(lane2Iter.hasNext()) {
				lane2Car = lane2Iter.next();
				phase.intermediateAlgorithm(lane2Car, this);
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
		
		this.handleLightColors(timePerIteration, phase);
		//TODO: Changed the .getIterable() to a ListIterator... so we can remove while traversing now.
		//	Wait until after presentation to make change
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
	
	public boolean canLightBeGreenAtTime(double time){
		boolean greenLight = false;
		if(this.currentColor == Color.GREEN)
			greenLight = true;
		int size = lightTimes.size();
		time -= this.timeUntilColorChange;
		if(time > 0){
			int i;
			int timesUsedPerSection = 0;
			for(i=0;i<size && time > 0;i++){ //subtract off planned light timing until planned time is up or time is found
				int timesPlannedPerSection = lightTimes.get(i);
				timesUsedPerSection = 0;
				while(time > 0 && timesPlannedPerSection > 0){
					time -= getTimeChunk(greenLight);
					timesUsedPerSection++;
					timesPlannedPerSection--;
				}
				if(timesPlannedPerSection == 0 && i < size-1 && time > 0){
					greenLight = !greenLight;
				}
				if(time < 0){
					break;
				}
			}
			if(time > 0){ //after subtracting, if we still have unplanned future times for the light, let's add them in
				int redsToAdd = 0;
				while(time > 0){
					if(time - this.timeAsGreen < 0){
						if(this.greenTimesEarned > 0){
							appendTimes(redsToAdd, 1, greenLight);
							time = -1;
							this.greenTimesEarned--;
							return true;
						}
						else{
							appendTimes(redsToAdd+1,0,greenLight);
							return false;
						}
					}
					else{
						redsToAdd++;
						addGreenTime();
						time -= this.timeAsRed;
					}
				}
			}
			else{
				if(!greenLight){
					if(this.greenTimesEarned > 0){
						this.greenTimesEarned--;
						int timeToSplit = this.lightTimes.get(i);
						int timeFirst = timeToSplit - timesUsedPerSection;
						int timeAfter = timeToSplit - timeFirst;
						lightTimes.set(i,timeFirst);
						lightTimes.add(i+1, timeAfter);
						lightTimes.add(i+1, 1);
						return true;
					}
					return false;
				}
				else{
					return greenLight; //the light is already planning to be green at that time
				}
			}
		}
		return greenLight;
	}
	
	public void appendTimes(int reds, int greens, boolean lightStatus){
		if(reds > 0){//there are reds to add
			if(lightStatus){//the last light was green, so we can just append the red and green
				lightTimes.add(reds);
				lightTimes.add(greens);
			}
			else{//last light was red, so we need to add the new reds to the last value and append green
				lightTimes.set(lightTimes.size()-1, lightTimes.get(lightTimes.size()-1)+reds);
				lightTimes.add(greens);
			}
		}
		else{//just add green light
			if(lightStatus){//last light was green, so add to last value
				lightTimes.set(lightTimes.size()-1, lightTimes.get(lightTimes.size()-1)+1);
			}
			else{//last light was red, so we can just append the new green
				lightTimes.add(greens);
			}
		}
	}
	
	public double getTimeChunk(boolean greenLight){
		if(greenLight){
			return this.timeAsGreen;
		}
		else{
			return this.timeAsRed;
		}
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

	public boolean justChangedGreen() {
		return this.timeUntilColorChange == this.timeAsGreen;
	}
	
	public boolean justChangedRed() {
		return this.timeUntilColorChange == this.timeAsRed;
	}
}
