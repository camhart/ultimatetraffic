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

		double carPosition = car.moveCarForward(); //TODO: Write function according to Josh's code
		
		Outputter.getOutputter().addCarOutput(car);
		
		double lightPosition = light.getPosition();
		if(carPosition >= lightPosition){	//TODO: (eventually) adjust for other direction later ( will be <= )
			if(carPosition < car.getDestination()){
				algorithm(car, light.getNextLight(), light);
			}
			else{
				//TODO: Somehow quickly output that this car is done so we can verify all the cars finished...
				light.removeCarFromLane(car);
			}
		}
	}
	
	public void algorithm(CarManager car, StopLight light, StopLight prevLight){
		double newSpeed = MAX_SPEED;
		
		double distanceToLight = light.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
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
		
		while(car.hitNextCar(theoreticalTimeToLight, distanceToLight)){	//TODO: Function must be created
			int otherLane = car.getOtherLane();
			
			boolean changedLanes = false;
			if(car.getLane() == 1) {
				if(light.getLane2().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					//can change lanes
					car.setLane(otherLane, light.getLane2());
					changedLanes = true;
				}
			} else {
				if(light.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					car.setLane(otherLane, light.getLane1());
					changedLanes = true;
				}
			}
			
			if(changedLanes) {
				if(car.hitNextCar(theoreticalTimeToLight, distanceToLight)){
					if(otherLane == 1)
						car.setLane(laneNum, light.getLane2());
					else
						car.setLane(laneNum, light.getLane1());
					//why are we calling setLane here?  Isn't that already taken care of?
					//This is called because changing lanes didn't work, so we switch back and reduce speed (and try it all again)
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
		
		car.giveChangeSpeedCommand(newSpeed);
		
		// Why call this here?  Um... we shouldn't...
//		car.setLane(laneNum);
	}

}
