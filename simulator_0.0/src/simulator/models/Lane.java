package simulator.models;

import java.util.Iterator;
//import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Lane {
	
	Lane otherLane;
	
	private LinkedList<CarManager> cars;
	private StopLight light;
	public Lane(StopLight light) {
		this.light = light;
		cars = new LinkedList<CarManager>();
	}
	
	/**
	 * Adds a car to the lane
	 * @param car
	 * @return
	 */
	public boolean addCar(CarManager car) {
//		cars.addLast(car);
		//cars.add already adds to the end of this list
		//according to http://docs.oracle.com/javase/7/docs/api/java/util/LinkedList.html#add(E)
		// this will ALWAYS return true
		return cars.add(car);
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
	
	/**
	 * returns null if no other car in front
	 * @param curCar
	 * @return
	 */
	public CarManager getNextCar(CarManager curCar){
		Iterator<CarManager> iter = cars.iterator();
		CarManager car = null;
		//this should only be called on lanes that already have the current car in the lane
		while(iter.hasNext()){
			car = iter.next();
			if(car == curCar) {
				if(iter.hasNext())
					return iter.next();
				else return null;
			}
		}
		return car;
	}
	
	public boolean removeCar(CarManager car) {
		
		//this.cars.remove(car); //I don't know if I trust this...  even if we go back to it consider using removeLastOccurance
		
		return this.cars.removeLastOccurrence(car);
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
}
