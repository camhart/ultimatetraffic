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

	/**
	 * This handles a single iteration.  It will take care of everything
	 * 	within the stopLight's realm of knowledge including:
	 * 		the stop lights timing, it's associated lanes,
	 * 		the cars within each lane.
	 * @param phase
	 * @param timePerIteration
	 */
	public final void iterate(PhaseHandler phase, double timePerIteration) {
		
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
				lane1Car = null;
			}
			else if(lane2Car != null){//we only have a car in lane 2
				handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
				lane2Car = null;
			}
			
			while(lane1Car != null || lane2Car != null){ //this means we picked up a car in the first 'if' that hasn't had an algorithm call
				if(car1Position > car2Position){
					handleCar(phase, lane1Car, lane1, lane1Removes, finishingCars);
					car1Position = -1;
					lane1Car = null;
				}
				else{
					handleCar(phase, lane2Car, lane2, lane2Removes, finishingCars);
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
