package simulator.phases;

import simulator.models.Car;
import simulator.models.StopLight;

public class Phase1 extends Phase  {

	public double setTargetSpeed(Car car, StopLight light) {
		//set the cars target speed
		// car.getCurrentSpeed() should give you the cars currentSpeed
		// car.getPosition() will give you the car's position
		// light.getPosition() - car.getPosition will give you the distance to the light
		// light.getStopPosition(car) will give you the car's stop position
		// light.hitRedLight(adjustedSpeed, distanceToNextLight) - boolean returns 
		//		if you'll hit a red traveling at adjustedSpeed
		// light.getLane().getCarIterable() - gives you an iterator that you can go through the car with
		//		the iterator will get cars for you in ascending order...
		return 0;		
	}
	
	public void moveCar(Car car) {
		//update cars position
	}

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			Car car, StopLight upcomingLight) {
		// TODO Auto-generated method stub
		
	}

}
