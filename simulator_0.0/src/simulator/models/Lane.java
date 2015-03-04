package simulator.models;

import java.util.Iterator;
//import java.util.Iterator;
import java.util.LinkedList;

public class Lane {
	
	Lane otherLane;
	
//	private TreeSet<Car> cars;
	private LinkedList<CarManager> cars;
	
	public Lane() {
		cars = new LinkedList<CarManager>();
	}
	
	/**
	 * Adds a car to the lane
	 * @param car
	 * @return
	 */
	public boolean addCar(CarManager car) {
		
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
	
	public boolean removeCar(CarManager car) {
		return this.cars.remove(car);
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
	public Iterator<CarManager> getIterable() {
		return cars.iterator();
	}
}
