package simulator.models;

import java.util.Iterator;
//import java.util.Iterator;
import java.util.LinkedList;

public class Lane {
	
	Lane otherLane;
	
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
	
	public CarManager getNextCar(){
		Iterator<CarManager> iter = cars.iterator();
		CarManager car = null;
		//this should only be called on lanes that already have the current car in the lane
//		car = iter.next();
		if(iter.hasNext()){
			car = iter.next();
		}
		return car;
	}
	
	public boolean removeCar(CarManager car) {
		
//		CarManager last = this.cars.removeLast();
//		System.out.println("Removed Car: " + last.getId() + " from lane " + toString());
//		if(last != car) {
//			System.out.println(String.format("Car id=%d position=%f", car.getId(), car.getPosition()));
//			System.out.println(String.format("Car id=%d position=%f", last.getId(), last.getPosition()));
//			
//			System.out.println("wtg");
//			
//			throw new Error("last car removed wasn't the right car");
//		}
//		
//		return true;
		
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
	public Iterator<CarManager> getIterable() {
		return cars.iterator();
	}
}
