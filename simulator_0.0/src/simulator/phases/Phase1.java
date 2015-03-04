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
			Car car, StopLight light) {
		//TODO: Fix this to move cars according to physics!
		double carPosition = car.getPosition()+10; //replace this with an actual physics call
		car.setPosition(carPosition, car.getLane() == 1 ? light.getLane1()
				: light.getLane2());
		//TODO: this needs to go somewhere--probably in the other file: Outputter.getOutputter().addCarOutput(car);
		double lightPosition = light.getPosition();
		if(carPosition >= lightPosition){	//TODO: adjust for other direction later ( will be <= )
			algorithm(car, light.getNextLight());
		}
		
	}
	
	public void algorithm(Car car, StopLight light){
		double newSpeed = MAX_SPEED;
		
		double distanceToLight = light.getPosition() - car.getPosition();
		
		while(car.hitRedLight(newSpeed, distanceToLight)){	//TODO: Must be defined and work...
			if(newSpeed > DECELERATION){
				newSpeed -= DECELERATION;
			}
			else{
				newSpeed = newSpeed*0.9;
			}
		}
		
		int laneNum = car.getLane();
		
		while(car.hitNextCar(newSpeed, laneNum)){
			laneNum = otherLane;//TODO: fix this
			if(car.hitNextCar(newSpeed, laneNum)){
				laneNum = car.getLane();
				if(newSpeed > DECELERATION){
					newSpeed -= DECELERATION;
				}
				else{
					newSpeed = newSpeed*0.9;
				}
			}
			else{break;}	//unneeded, but saves a function call
		}
		
		//TODO: updateSpeed(newSpeed);
		//TODO: updateLane(laneNum);
	}

}
