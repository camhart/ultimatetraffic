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
	
	private void printCarPositions() {
		for(CarManager cara : cars) {
			System.out.print(cara.getPosition());
			System.out.print(", ");
		}
		System.out.println("");
	}
	
	/**
	 * returns null if no other car in front
	 * @param curCar
	 * @return
	 */
	public CarManager getNextCar(CarManager car){
		
		Collections.sort(this.cars);
				
//		ListIterator<CarManager> iter = this.cars.listIterator(); //cars.listIterator(cars.size());
//		CarManager curCar;
//		
//		System.out.println("iteration: " + Simulator.getSimulator().getCurrentIteration());
//		if(Simulator.getSimulator().getCurrentIteration() == 290) {
//			System.out.println(cars);	
//			System.out.println("size " + this.cars.size());
//		}
//		
//		while(iter.hasNext()) {
//			curCar = iter.next();
//			if(curCar.getId() == car.getId()) {
//				if(iter.hasNext())
//					return iter.next();
//				else
//					return null;
//			}
//		}
//		
//		assert false : "This shouldn't be happening";
//		
//		return null;
		
		
		ListIterator<CarManager> iter = this.getReverseIterable(); //cars.listIterator(cars.size());
		CarManager curCar;
		
		
//		this.printCarPositions();
		
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
		
		
//		Iterator<CarManager> iter = cars.iterator();
//		CarManager curCar = null;
//		CarManager prevCar = null;
//		
//		//this should only be called on lanes that already have the current car in the lane
//		assert this.cars.contains(car) : "Lane doesn't contain car";
//		
//		while(iter.hasNext()){
//			prevCar = curCar;
//			curCar = iter.next();
//			if(car.getId() == curCar.getId()) {
//				return prevCar;
//			}
//		}
////		throw new Error("This shouldn't be happening.... current car not found.");
//		return null;
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
	
//	public LinkedList<CarManager> getCars() {
//		return this.cars;
//	}
}
