package simulator.models.stoplights;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import simulator.models.CarManager;
import simulator.models.Lane;
import simulator.outputter.Outputter;
import simulator.phases.PhaseHandler;

public class StopLight implements Iterable<CarManager>{
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
		lane1 = new Lane(this, 1);
		lane2 = new Lane(this, 2);
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
	
	public Lane getLane(int lane) {
		if(lane == 1) {
			return getLane1();
		} else {
			return getLane2();
		}
	}
	
	public Lane getOtherLane(int lane) {
		if(lane == 2) {
			return getLane1();
		} else {
			return getLane2();
		}
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
	
	private ArrayList<CarManager> lane1Removes;
	private ArrayList<CarManager> lane2Removes;
	
	public boolean greaterCarPosition(CarManager c1, CarManager c2) {
		assert c1 != null || c2 != null : "what to do here?";
		
		if(c1 == null)
			return false;
		if(c2 == null)
			return true;
		return c1.getPosition() >= c2.getPosition();
	}
	
//	public boolean canOptimizeCar(CarManager car, ArrayList<Double> list) {
//		for(Double d : list) {
//			if(Math.abs(car.getPosition() - d) < 6.0) {
//				return false;
//			}
//		}
//		return true;
//	}
	
	public void optimizeLanes() {
//		ArrayList<Double> positionsThatChangedLanes = new ArrayList<Double>();
		
		boolean laneChangeOccured = true;
		while(laneChangeOccured) {
			
			laneChangeOccured = false;
			ListIterator<CarManager> lane1Iter = this.getLane1().getIterable();
			ListIterator<CarManager> lane2Iter = this.getLane2().getIterable();
			
			CarManager car1 = null;
			CarManager car2 = null;
			
			if(lane1Iter.hasNext())
				car1 = lane1Iter.next();
			
			if(lane2Iter.hasNext())
				car2 = lane2Iter.next();
			
			while(!laneChangeOccured && (lane1Iter.hasNext() || lane2Iter.hasNext()) && (car1 != null || car2 != null)) {
				
				if(greaterCarPosition(car1, car2)) {
					if(getOtherLane(car1.getLane()).canChangeLane(car1)) {						
						CarManager nextCar = this.getLane(car1.getLane()).getNextCar(car1);						
						if(nextCar != null) {
							double distanceToNextCar = nextCar.getPosition() - car1.getPosition();
							double distanceToNextCarOther = getOtherLane(car1.getLane()).getDistanceToNextCarFrom(car1.getPosition());
							
//							if(distanceToNextCarOther > distanceToNextCar && canOptimizeCar(car1, positionsThatChangedLanes)) {
							if(distanceToNextCarOther > distanceToNextCar) {
								//change lanes
								getLane1().removeCar(car1);
								car1.setLane(2, getLane2());
								getLane2().addCar(car1);
								laneChangeOccured = true;
//								positionsThatChangedLanes.add(car1.getPosition());
							}	
						}
					}	
					if(!laneChangeOccured) {
						if(lane1Iter.hasNext()) {
							car1 = lane1Iter.next();
						} else car1 = null;
					}
				} else {
					if(getOtherLane(car2.getLane()).canChangeLane(car2)) {					
						CarManager nextCar = this.getLane(car2.getLane()).getNextCar(car2);						
						if(nextCar != null) {			
							double distanceToNextCar = nextCar.getPosition() - car2.getPosition();
							double distanceToNextCarOther = getOtherLane(car2.getLane()).getDistanceToNextCarFrom(car2.getPosition());
							
							if(distanceToNextCarOther > distanceToNextCar) {
								//change lanes
								getLane2().removeCar(car2);
								car2.setLane(1, getLane1());
								getLane1().addCar(car2);
								laneChangeOccured = true;
							}							
						}
					}
					if(!laneChangeOccured) {
						if(lane2Iter.hasNext()) {
							car2 = lane2Iter.next();
						} else car2 = null;
					}
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
		
//		handleLaneChanges();

		
		
		Iterator<CarManager> carIter = this.iterator();
		CarManager curCar = null;
		
		lane1Removes = new ArrayList<CarManager>();
		lane2Removes = new ArrayList<CarManager>();
		
		ArrayList<CarManager> finishingCars = new ArrayList<CarManager>();
		
		while(carIter.hasNext()) {
			curCar = carIter.next();
			
			
//			if(phase == null || curCar == null || lane1Removes == null || lane2Removes == null || finishingCars == null)
//				System.out.println("curCar is null");			
//			if(curCar != null)
				handleCar(phase, curCar, curCar.getLaneObject(), lane1Removes, lane2Removes, finishingCars);
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
	
	public void moveCarToNextLight(CarManager car, ArrayList<CarManager> removeList) {
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
	
	public void handleCar(PhaseHandler phase, CarManager car, Lane lane, ArrayList<CarManager> lane1RemoveList, ArrayList<CarManager> lane2RemoveList, ArrayList<CarManager> finishingCars) {
		phase.handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(car, this);
		
		if(car.hasFinished()) {
			finishingCars.add(car);
		}
		
		if(nextLight != null && !car.hasFinished() && car.getPosition() >= getPosition()) {
			moveCarToNextLight(car, car.getLane() == 1 ? lane1RemoveList : lane2RemoveList);
		}
	}

	/**
	 * Should only be called when a car reaches it's destination
	 * @param car
	 */
	public void removeCarFromLane(CarManager car) {
		assert car.getPosition() >= car.getDestination() : "Car hasn't finished traveling...";
		
		if(!car.getLaneObject().removeCar(car))
			throw new Error("couldn't remove car from lane after it had finished");
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

	/**
	 * Iterator will (untested) iterate over all cars from both lanes within the StopLight
	 * 	in order from lowest position to highest 
	 */
	@Override
	public ListIterator<CarManager> iterator() {
		ListIterator<CarManager> iter = new ListIterator<CarManager>() {
			private int count = 0;
			private Iterator<CarManager> lane1Iter = lane1.getIterable();
			private Iterator<CarManager> lane2Iter = lane2.getIterable();
			private CarManager lane1CurCar = lane1Iter.hasNext() ? lane1Iter.next() : null;			
			private CarManager lane2CurCar = lane2Iter.hasNext() ? lane2Iter.next() : null;
			private Iterator<CarManager> lastIteratorUsed = null;
			
			private void updateLane1Car() {
				if(lane1Iter.hasNext())
					lane1CurCar = lane1Iter.next();
				else 
					lane1CurCar = null;
			}
			
			private void updateLane2Car() {
				if(lane2Iter.hasNext())
					lane2CurCar = lane2Iter.next();
				else
					lane2CurCar = null;
			}
			
			@Override
			public boolean hasNext() {
				return count < (lane1.getNumberCarsInLane() + lane2.getNumberCarsInLane());
			}

			@Override
			public CarManager next() {
				CarManager retCar = null;
				count++;

				if(lane1CurCar != null && lane2CurCar != null) { //both lanes have cars
					if(lane1CurCar.getPosition() < lane2CurCar.getPosition()) {
						retCar = lane1CurCar;
						lastIteratorUsed = lane1Iter;
						updateLane1Car();
					} else {
						retCar = lane2CurCar;
						lastIteratorUsed = lane2Iter;
						updateLane2Car();
					}
				} else if(lane1CurCar != null) { //ONLY lane1 has cars left
					retCar = lane1CurCar;
					lastIteratorUsed = lane1Iter;
					updateLane1Car();
				} else if(lane2CurCar != null) { //ONLY lane2 has cars left
					retCar = lane2CurCar;
					lastIteratorUsed = lane2Iter;
					updateLane2Car();
				}
				return retCar;
			}

			@Override
			public boolean hasPrevious() {
				throw new Error("not implemented");
			}

			@Override
			public CarManager previous() {
				throw new Error("not implemented");
			}

			@Override
			public int nextIndex() {
				throw new Error("not implemented");
			}

			@Override
			public int previousIndex() {
				throw new Error("not implemented");
			}

			@Override
			public void remove() {
				this.lastIteratorUsed.remove();
			}

			@Override
			public void set(CarManager e) {
				throw new Error("not implemented");
			}

			@Override
			public void add(CarManager e) {
				throw new Error("not implemented");
			}
			
		};
		return iter;
	}
}
