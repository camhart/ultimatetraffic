package simulator.models;

import java.util.Collections;
import java.util.Iterator;
//import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import simulator.Simulator;
import simulator.models.stoplights.StopLight;

public class Lane {
	
	public Lane otherLane;
	private int laneNumber;
	
	private LinkedList<CarManager> cars;
	private StopLight light;
	public Lane(StopLight light, int laneNumber) {
		this.light = light;
		cars = new LinkedList<CarManager>();
		this.laneNumber = laneNumber;
	}
	
	/**
	 * Adds a car to the lane
	 * @param car
	 * @return
	 */
	public boolean addCar(CarManager car) {
//		cars.addLast(car);
		cars.add(car);
		
		Collections.sort(cars); //overkill
		
		for(int c = 0; c < this.cars.size() - 1; c++) {
			if(cars.get(c).getPosition() < cars.get(c + 1).getPosition()) {
				System.out.println(cars);
				System.out.println(this.getParentLight().getId());
				assert false : "shouldn't be happening";
			}
		}
		
		//cars.add already adds to the end of this list
		//according to http://docs.oracle.com/javase/7/docs/api/java/util/LinkedList.html#add(E)
		// this will ALWAYS return true
//		double position = car.getPosition();
//		int size = cars.size();
//		int i;
//		if(size > 0){
//			for(i = 0; i < size; i++){
//				if(cars.get(i).getPosition() < position){
//					cars.add(i, car); //add mid road
//				}
//			}
//		}
//		else{
//			cars.add(car);//add to be the only car
//		}
//		if(size == cars.size()){//the car hasn't been added, so now's the time
//			cars.add(car);
//		}
		return true;
//		return cars.add(car);
	}
	
	/**
	 * Determines whether or not a car can change lanes
	 *  based on it's current position
	 * @param car
	 * @return
	 */
	public boolean canChangeLane(CarManager car) {
		Iterator<CarManager> iter = cars.iterator();
		CarManager curCar = null;
		while(iter.hasNext()) {
			curCar = iter.next();
			if(Math.abs(curCar.getPosition() - car.getPosition()) < CarManager.CAR_CUSHION) {
				return false;
			}
		}
		return true;
	}
	
//	private void printCarPositions() {
//		for(CarManager cara : cars) {
//			System.out.print(cara.getPosition());
//			System.out.print(", ");
//		}
//		System.out.println("");
//	}
	
	/**
	 * returns null if no other car in front
	 * @param curCar
	 * @return
	 */
	public CarManager getNextCar(CarManager car){
		
//		Collections.sort(this.cars);
				
		ListIterator<CarManager> iter = this.getReverseIterable(); //cars.listIterator(cars.size());
		CarManager curCar;
		
		while(iter.hasPrevious()) {
			curCar = iter.previous();
			if(curCar.getId() == car.getId()) {
				if(iter.hasPrevious())
					return iter.previous();
				else
					return null;
			}
		}
		
		assert false : "This shouldn't be happening";
		
		return null;
	}
	
	public boolean removeCar(CarManager car) {
		
		//this.cars.remove(car); //I don't know if I trust this...  even if we go back to it consider using removeLastOccurance
		boolean ret = this.cars.removeLastOccurrence(car);
		
//		Collections.sort(cars);
		
		return ret;
	}
	
	/**
	 * Have everything in here for the car to change lanes
	 * @param car
	 */
	public void changeLane(CarManager car) {
		throw new Error("unimplemented");
	}
	
	public int getNumberCarsInLane() {
		return this.cars.size();
	}

	/**
	 * Retrieve an iterator object used to go through the cars in
	 * this lane.
	 * @return
	 */
	public ListIterator<CarManager> getIterable() {
		return cars.listIterator();
	}
	
	/**
	 * Retrieve an iterator object used to go through the cars in
	 * this lane.
	 * @return
	 */
	public ListIterator<CarManager> getReverseIterable() {
		return cars.listIterator(cars.size());
	}

	public StopLight getParentLight() {
		return this.light;
	}

	public Lane getOtherLane() {
		return this.otherLane;
	}

	public double getDistanceToNextCarFrom(double position) {
		ListIterator<CarManager> iter = this.getReverseIterable();
		CarManager curCar = null;
		while(iter.hasPrevious()) {
			curCar = iter.previous();
			if(curCar.getPosition() > position) {
				return curCar.getPosition() - position;
			}
		}
		
		StopLight nextLight = this.getParentLight().getNextLight();
		if(nextLight != null) {
			return nextLight.getLane(this.laneNumber).getDistanceToNextCarFrom(position);
		}
		
		return Double.MAX_VALUE;
	}

	public int getLaneNumber() {
		return this.laneNumber;
	}
}
