package simulator.models.stoplights;

import java.util.ArrayList;
import java.util.Iterator;

import simulator.models.CarManager;
import simulator.models.Lane;
import simulator.outputter.Outputter;
import simulator.phases.PhaseHandler;

public class StopLight {
	protected StopLight nextLight;
	protected StopLight prevLight;
	
	protected static int MAX_EARNED_TIME = 12;
	
	protected Lane lane1;
	protected Lane lane2;
	
	protected Color currentColor;
	protected double timeAsRed;
	protected double timeAsGreen;
	protected double timeUntilColorChange;
	protected double position;
	protected String lightType;
	protected double initialOffset;
	protected int id;
	protected int greenTimesEarned;
	//private int[] lightTimes;
	protected ArrayList<Integer> lightTimes;
	
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
	protected void setTimeUntilColorChange() {
//		if(this.lightType.equals("phase2")){
//			lightTimes.set(0, lightTimes.get(0)-1);
//			if(lightTimes.get(0) <= 0){//change color
//				this.lightTimes.remove(0);
//				if(this.lightTimes.size() < 1){ //This means no cars have requested Green in the future, so we leave the light red
//					this.lightTimes.add(1);
//					this.currentColor = Color.RED;
//					this.timeUntilColorChange = this.timeAsRed;
//					addGreenTime(1);
//				}
//				else{
//					if(this.currentColor == Color.GREEN){
//						this.currentColor = Color.RED;
//						this.timeUntilColorChange = this.timeAsRed;
//					}
//					else{
//						this.currentColor = Color.GREEN;
//						this.timeUntilColorChange = this.timeAsGreen;
//					}
//				}
//			}
//			else{//don't change
//				if(this.currentColor == Color.GREEN){
//					this.timeUntilColorChange = this.timeAsGreen;
//				}
//				else{
//					this.timeUntilColorChange = this.timeAsRed;
//				}
//			}
//		}
//		else{ //Phases 0 and 1
			if(this.currentColor == Color.GREEN){
				this.currentColor = Color.RED;
				this.timeUntilColorChange = this.timeAsRed;
			}
			else{
				this.currentColor = Color.GREEN;
				this.timeUntilColorChange = this.timeAsGreen;
			}
//		}
	}
	
	public void addGreenTime(int greens){
		if(this.greenTimesEarned + greens < this.MAX_EARNED_TIME){
			greenTimesEarned += greens;
		}
		else{
			greenTimesEarned = MAX_EARNED_TIME;
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
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			} else {
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			}
		}
	}
	
	public void CallIntermediateAlgorithmOnAllCars(PhaseHandler phase){
		Iterator<CarManager> lane1Iter = lane1.getIterable();
		Iterator<CarManager> lane2Iter = lane2.getIterable();
		CarManager lane1Car = null;
		CarManager lane2Car = null;
		
		while(lane1Iter.hasNext() || lane2Iter.hasNext()) {
			
//			if(lane1Iter.hasNext()) {
//				lane1Car = lane1Iter.next();
//				phase.intermediateAlgorithm(lane1Car, this);
//			}
//			
//			if(lane2Iter.hasNext()) {
//				lane2Car = lane2Iter.next();
//				phase.intermediateAlgorithm(lane2Car, this);
//			}
			
			if(lane1Iter.hasNext()){
				lane1Car = lane1Iter.next();
			}
			if(lane2Iter.hasNext()){
				lane2Car = lane2Iter.next();
			}
			double car1Position = 0;
			double car2Position = 0;
			if(lane1Car != null && lane2Car != null){//there are cars in both lanes
				car1Position = lane1Car.getPosition();
				car2Position = lane2Car.getPosition();
				while(car1Position > car2Position){
					phase.intermediateAlgorithm(lane1Car, this);
					if(lane1Iter.hasNext()){
						lane1Car = lane1Iter.next();
						car1Position = lane1Car.getPosition();
					}
					else{
						lane1Car = null;
						car1Position = -1;
					}
				}
				while(car2Position > car1Position){
					phase.intermediateAlgorithm(lane2Car, this);
					if(lane2Iter.hasNext()){
						lane2Car = lane2Iter.next();
						car2Position = lane2Car.getPosition();
					}
					else{
						lane2Car = null;
						car2Position = -1;
					}
				}
			}
			else if(lane1Car != null){//we only have a car in lane 1
				phase.intermediateAlgorithm(lane1Car, this);
				lane1Car = null;
			}
			else if(lane2Car != null){//we only have a car in lane 2
				phase.intermediateAlgorithm(lane2Car, this);
				lane2Car = null;
			}
			
			while(lane1Car != null || lane2Car != null){ //this means we picked up a car in the first 'if' that hasn't had an algorithm call
				if(car1Position > car2Position){
					phase.intermediateAlgorithm(lane1Car, this);
					car1Position = -1;
					lane1Car = null;
				}
				else{
					phase.intermediateAlgorithm(lane2Car, this);
					car2Position = -1;
					lane2Car = null;
				}
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
			
//			if(lane1Iter.hasNext()) {
//				lane1Car = lane1Iter.next();
//				handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
//			}
//			
//			if(lane2Iter.hasNext()) {
//				lane2Car = lane2Iter.next();
//				handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
//			}
			
			if(lane1Iter.hasNext()){
				lane1Car = lane1Iter.next();
			}
			if(lane2Iter.hasNext()){
				lane2Car = lane2Iter.next();
			}
			double car1Position = 0;
			double car2Position = 0;
			if(lane1Car != null && lane2Car != null){//there are cars in both lanes
				car1Position = lane1Car.getPosition();
				car2Position = lane2Car.getPosition();
				while(car1Position > car2Position){
					handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
//					phase.intermediateAlgorithm(lane1Car, this);
					if(lane1Iter.hasNext()){
						lane1Car = lane1Iter.next();
						car1Position = lane1Car.getPosition();
					}
					else{
						lane1Car = null;
						car1Position = -1;
					}
				}
				while(car2Position > car1Position){
					handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
//					phase.intermediateAlgorithm(lane2Car, this);
					if(lane2Iter.hasNext()){
						lane2Car = lane2Iter.next();
						car2Position = lane2Car.getPosition();
					}
					else{
						lane2Car = null;
						car2Position = -1;
					}
				}
			}
			else if(lane1Car != null){//we only have a car in lane 1
				handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
//				phase.intermediateAlgorithm(lane1Car, this);
				lane1Car = null;
			}
			else if(lane2Car != null){//we only have a car in lane 2
				handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
//				phase.intermediateAlgorithm(lane2Car, this);
				lane2Car = null;
			}
			
			while(lane1Car != null || lane2Car != null){ //this means we picked up a car in the first 'if' that hasn't had an algorithm call
				if(car1Position > car2Position){
					handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
//					phase.intermediateAlgorithm(lane1Car, this);
					car1Position = -1;
					lane1Car = null;
				}
				else{
					handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
//					phase.intermediateAlgorithm(lane2Car, this);
					car2Position = -1;
					lane2Car = null;
				}
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
		//ArrayList<Integer> tempArray = this.lightTimes;//this doesn't actually create anything new... oops.
		//tempArray.set(0, tempArray.get(0)-1);
		if(time > 0){
			int i;
			int timesUsedPerSection = 0;
			for(i=0;i<size && time > 0;i++){ //subtract off planned light timing until planned time is up or time is found
				int timesPlannedPerSection = lightTimes.get(i);
				timesUsedPerSection = 0;
				if(i == 0){
					timesPlannedPerSection--;
					timesUsedPerSection++;
				}
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
							lightTimes = appendTimes(lightTimes, redsToAdd, 1, greenLight);
							//time = -1;
							this.greenTimesEarned--;
							return true;
						}
						else{
							lightTimes = appendTimes(lightTimes, redsToAdd+1,0,greenLight);
							return false;
						}
					}
					else{
						redsToAdd++;
						//addGreenTime();
						time -= this.timeAsRed;
						if(time < 0){//our red interval is bigger than the green interval, so we need multiple greens here
							//reverse what just happened
							time +=this.timeAsRed;
							redsToAdd--;
							//get needed green light times
							int greensNeeded = 1;
							while(time > 0){
								time -= this.timeAsGreen;
								greensNeeded++;
							}
							if(this.greenTimesEarned >= greensNeeded){
								this.greenTimesEarned -= greensNeeded;
								lightTimes = appendTimes(lightTimes, redsToAdd, greensNeeded, greenLight);
								return true;
							}
							else{
								lightTimes = appendTimes(lightTimes, redsToAdd,0,greenLight);
								return false;
							}
						}
					}
				}
				
			}
			else{
				if(!greenLight){
					int greensNeeded = 0;
					while(time < 0){
						time += this.timeAsGreen;
						greensNeeded++;
					}
					if(this.greenTimesEarned > greensNeeded){
						this.greenTimesEarned -= greensNeeded;
						int timeToSplit = this.lightTimes.get(i);
						int timeFirst = timeToSplit - (timesUsedPerSection);
						int timeAfter = timeToSplit - (timeFirst + greensNeeded);
						if(timeFirst > 0){
							lightTimes.set(i,timeFirst);
							if(timeAfter > 0){
								lightTimes.add(i+1, timeAfter);
								lightTimes.add(i+1, greensNeeded);
							}
							else{
								lightTimes.set(i+1, lightTimes.get(i+1) + greensNeeded);
							}
						}
						else{
							if(timeAfter > 0){
								lightTimes.set(i, timeAfter);
							}
							if(i > 0)
								lightTimes.set(i-1, lightTimes.get(i-1) + greensNeeded);
							else{
								lightTimes.add(i, greensNeeded);
								System.out.println("WHAAAAAAAT? THIS SHOULD NEVER HAPPEN!");
							}
						}
//						int timeToSplit = tempArray.get(i);
//						int timeFirst = timeToSplit - (timesUsedPerSection);
//						int timeAfter = timeToSplit - timeFirst;
//						tempArray.set(i,timeFirst);
//						tempArray.add(i+1, timeAfter);
//						tempArray.add(i+1, 1);
//						tempArray.set(0, tempArray.get(0)+1);
//						this.lightTimes = tempArray;
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
	
	public ArrayList<Integer> appendTimes(ArrayList<Integer> a, int reds, int greens, boolean lightStatus){
		if(reds > 0){//there are reds to add
			addGreenTime(reds);
			if(lightStatus){//the last light was green, so we can just append the red and green
				a.add(reds);
				//a.add(greens);
			}
			else{//last light was red, so we need to add the new reds to the last value and append green
				a.set(a.size()-1, a.get(a.size()-1)+reds);
				//a.add(greens);
			}
		}
//		else{//just add green light
//			if(lightStatus){//last light was green, so add to last value
//				a.set(a.size()-1, a.get(a.size()-1)+1);
//			}
//			else{//last light was red, so we can just append the new green
//				a.add(greens);
//			}
//		}
		if(greens > 0){
			if(lightStatus){
				a.set(a.size()-1, a.get(a.size()-1)+greens);
			}
			else{
				a.add(greens);
			}
		}
		return a;
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
