package simulator.models;

import java.util.Iterator;
import java.util.TreeSet;

public class Lane {
	
	Lane otherLane;
	
	private TreeSet<Car> cars;
	
	public Lane() {
		cars = new TreeSet<Car>();
	}
	
	public boolean addCar(Car car) {
		
		return cars.add(car);
	}
	
	public boolean canChangeLane(Car car) {
//		Iterator<Car> iter = cars.iterator();
//		Car curCar = null;
//		while(iter.hasNext()) {
//			curCar = iter.next();
//			if(curCar.getPosition() - car.getPosition() < ) {
//				
//			}
//		}
		return true;
	}
	
	public void changeLane(Car car) {
		throw new Error("unimplemented");
	}
	
	public int getCarsInLane() {
		return this.cars.size();
	}

	public boolean removeCar(Car car) {
		return cars.remove(car);
	}

	public Iterator<Car> getIterable() {
		return cars.iterator();
	}
}
