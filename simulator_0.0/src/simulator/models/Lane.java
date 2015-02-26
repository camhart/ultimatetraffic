package simulator.models;

import java.util.Iterator;
import java.util.TreeSet;

public class Lane {
	
	Lane otherLane;
	
	private TreeSet<Car> cars;
	
	public Lane() {
		cars = new TreeSet<Car>();
	}
	
	/**
	 * Adds a car to the lane
	 * @param car
	 * @return
	 */
	public boolean addCar(Car car) {
		
		return cars.add(car);
	}
	
	/**
	 * Determines whether or not a car can change lanes
	 *  based on it's current position
	 * @param car
	 * @return
	 */
	public boolean canChangeLane(Car car) {
		Iterator<Car> iter = cars.iterator();
		Car curCar = null;
		while(iter.hasNext()) {
			curCar = iter.next();
			if(Math.abs(curCar.getPosition() - car.getPosition()) < Car.CAR_CUSHION) {
				return false;
			}
		}
		return true;
	}
	
	public boolean removeCar(Car car) {
		return this.cars.remove(car);
	}
	
	/**
	 * Have everything in here for the car to change lanes
	 * @param car
	 */
	public void changeLane(Car car) {
		throw new Error("unimplemented");
	}
	
	public int getNumberCarsInLane() {
		return this.cars.size();
	}

	public Iterator<Car> getIterable() {
		return cars.iterator();
	}
}
