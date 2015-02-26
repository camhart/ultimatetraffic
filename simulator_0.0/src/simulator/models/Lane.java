package simulator.models;

import java.util.TreeSet;

public class Lane {
	
	Lane otherLane;
	
	private TreeSet<Car> cars;
	
	public Lane() {
		
	}
	
	public void addCar(Car car) {
		cars.add(car);
	}
	
	public Car removeLastCar() {
		return cars.last();
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
		
	}

	public Car getCar(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeCar(Car car) {
		return cars.remove(car);
	}
}
