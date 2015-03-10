package simulator.phases;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase1Handler extends PhaseHandler  {

//	public double setTargetSpeed(CarManager car, StopLight light) {
//		//set the cars target speed
//		// car.getCurrentSpeed() should give you the cars currentSpeed
//		// car.getPosition() will give you the car's position
//		// light.getPosition() - car.getPosition will give you the distance to the light
//		// light.getStopPosition(car) will give you the car's stop position
//		// light.hitRedLight(adjustedSpeed, distanceToNextLight) - boolean returns 
//		//		if you'll hit a red traveling at adjustedSpeed
//		// light.getLane().getCarIterable() - gives you an iterator that you can go through the car with
//		//		the iterator will get cars for you in ascending order...
//		return 0;		
//	}
	
//	public void moveCar(CarManager car) {
//		//update cars position
//	}

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
		double carPosition = car.getPosition();
		
		double lightPosition = light.getPosition();
		if(carPosition >= lightPosition){	//TODO: (eventually) adjust for other direction later ( will be <= )
			
			if(carPosition < car.getDestination()){
				
				algorithm(car, light.getNextLight(), light);
			}
			
			//I pulled this up to StopLight's iterate function.
			//	search for 'Simulator.getSimulator().finishCar(lane2Car);'
			//	and
			//  Simulator.getSimulator().finishCar(lane1Car);
//			else{
//				//TODO: Somehow quickly output that this car is done so we can verify all the cars finished...
//				light.removeCarFromLane(car);
//			}
		}
		
		
		car.moveCarForward(); //TODO: Write function according to Josh's code
		
		Outputter.getOutputter().addCarOutput(car);
	}
	
	public void algorithm(CarManager car, StopLight nextLight, StopLight prevLight){
		double newSpeed = MAX_SPEED;
		
		if(nextLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED);
			return;
		}
		
		double distanceToLight = nextLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight is < 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		while(!nextLight.isLightGreenAtTime(theoreticalTimeToLight)){
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
				if(nextLight.getLane2().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					//can change lanes
					car.setLane(otherLane, nextLight.getLane2());
					changedLanes = true;
				}
			} else {
				if(nextLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					car.setLane(otherLane, nextLight.getLane1());
					changedLanes = true;
				}
			}
			
			if(changedLanes) {
				if(car.hitNextCar(theoreticalTimeToLight, distanceToLight)){
					if(otherLane == 1)
						car.setLane(laneNum, nextLight.getLane2());
					else
						car.setLane(laneNum, nextLight.getLane1());
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
			} else {
				
				//if the car doesn't change lanes we still need to decelerate right?
				
				if(newSpeed > DECELERATION){
					newSpeed -= DECELERATION;
				}
				else{
					newSpeed = newSpeed*0.9;
				}
				
				assert newSpeed != 0 : "We shouldn't ever be setting the speed to 0...";
			}		
		}
		
		car.giveChangeSpeedCommand(newSpeed);
	}

}
