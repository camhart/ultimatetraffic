package simulator.phases;

import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase1Handler extends PhaseHandler  {

	public double setTargetSpeed(CarManager car, StopLight light) {
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
	
	public void moveCar(CarManager car) {
		//update cars position
	}

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		//TODO: Fix this to move cars according to physics!
		double carPosition = car.moveCarForward(); //TODO: Could we use 'changeSpeed' from Josh's code here?
		
		Outputter.getOutputter().addCarOutput(car);
		
		double lightPosition = light.getPosition();
		if(carPosition >= lightPosition){	//TODO: (eventually) adjust for other direction later ( will be <= )
			algorithm(car, light.getNextLight());
			//TODO: move car to the next light's lane list here!
		}
		
	}
	
	public void algorithm(CarManager car, StopLight light){
		double newSpeed = MAX_SPEED;
		
		double distanceToLight = light.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);	//TODO: Function must be created
		
		while(!light.isLightGreenAtTime(theoreticalTimeToLight)){
			if(newSpeed > DECELERATION){
				newSpeed -= DECELERATION;
			}
			else{
				newSpeed = newSpeed*0.9;
			}
			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		}
		
		int laneNum = car.getLane();
		
		while(car.hitNextCar(theoreticalTimeToLight)){	//TODO: Function must be created
			int otherLane = car.getOtherLane();
			
			boolean changedLanes = false;
			if(car.getLane() == 1) {
				if(light.getLane2().canChangeLane(car)) {
					//can change lanes
					car.setLane(otherLane);	//TODO: Does this work as the code is currently written or do we need to actually mess with the 'Lane' object?
					changedLanes = true;
				}
			} else {
				if(light.getLane1().canChangeLane(car)) {
					car.setLane(otherLane);	//TODO: Does this work as the code is currently written or do we need to actually mess with the 'Lane' object?
					changedLanes = true;
				}
			}
			
			if(changedLanes) {
//				I ask because 'setPosition' sets the lane differently
				if(car.hitNextCar(theoreticalTimeToLight)){
					car.setLane(laneNum);
					if(newSpeed > DECELERATION){
						newSpeed -= DECELERATION;
					}
					else{
						newSpeed = newSpeed*0.9;
					}
					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
				}
				else{break;}	//unneeded, but saves a function call
			}
		}
		
		car.giveChangeSpeedCommand(newSpeed);	//TODO: Merge Josh's code--this exists in Josh's Car class
		car.setLane(laneNum);
	}

}
