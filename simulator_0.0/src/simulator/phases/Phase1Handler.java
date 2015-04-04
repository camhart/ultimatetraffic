package simulator.phases;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.car.Car.Command;
import simulator.models.stoplights.Phase1StopLight;
import simulator.models.stoplights.StopLight;
import simulator.outputter.Outputter;

public class Phase1Handler extends PhaseHandler  {
	
	private static final int PHASE_NUMBER = 1;

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
	
		//move the car, then check for speed change.  It has to happen this way
		//	because after this function runs, then the car is put onto the next light
		//	(if it's crossed over).
		car.moveCarForward(); 
		
		double carPosition = car.getPosition();
		
		if(carPosition >= light.getPosition()){	//TODO: (eventually) adjust for other direction later ( will be <= )
			if(carPosition < car.getDestination()){
				algorithm(car, light);
			}
		}
		
		Outputter.getOutputter().addCarOutput(car);
	}
	
	public boolean algorithm(CarManager car, StopLight currentLight){
		double newSpeed = MAX_SPEED;
		
		assert currentLight != null : "currentLight is null?";
		
		Phase1StopLight nextLight = (Phase1StopLight)currentLight.getNextLight();
		
		if(nextLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED, Command.CHANGE_SPEED);
			return false;
		}
		
		StopLight prevLight = currentLight;
		if(currentLight.getPosition() > 2){	//This is to catch a bug when calling algorithm on the first light
			prevLight = currentLight.getPrevLight();
		}
		
		double distanceToLight = nextLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		while(!nextLight.isLightGreenAtTime(theoreticalTimeToLight)){
			newSpeed = getDeceleratedSpeed(newSpeed);
			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
			
		}
		
		boolean changedLanes = false;
		
		while(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){	//TODO: Function must be created
			
			if(car.getLane() == 1) {
				if(nextLight.getLane2().canChangeLane(car) && prevLight.getLane2().canChangeLane(car)) {
					//can change lanes
					car.setLane(2, nextLight.getLane2());
					changedLanes = true;
				}
			} else {
				if(nextLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					car.setLane(1, nextLight.getLane1());
					changedLanes = true;
				}
			}
			
			if(changedLanes) {
				if(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
					
//					These instances of setLane is called because changing lanes
//					didn't work, so we switch back and reduce speed (and try it all again)
					if(car.getOtherLane() == 1)
						car.setLane(2, nextLight.getLane2());
					else
						car.setLane(1, nextLight.getLane1());
					
					
					
					newSpeed = getDeceleratedSpeed(newSpeed);
					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
					changedLanes = false;
				} else {
					//we're sticking with the lane change
					break; //unneeded, but saves a function call
				}	
			} else {
				
				//if the car doesn't change lanes we still need to decelerate
				// because we're still hitting the next car
				
				newSpeed = getDeceleratedSpeed(newSpeed);
				theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
			}
		}
		
		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
		
		return changedLanes;
	}
	
	public double getDeceleratedSpeed(double newSpeed) {
		if(newSpeed > DECELERATION){
			newSpeed -= DECELERATION;
			if(newSpeed <= DECELERATION){
				
				//force newSpeed to enter the else loop a few lines
				//down the next time through this while loop
				newSpeed = DECELERATION - 0.1;
			}
		}
		else{
			newSpeed = newSpeed*0.9;
		}
		return newSpeed;
	}
	
	public boolean intermediateAlgorithm(CarManager car, StopLight currentLightArg){
		double newSpeed = MAX_SPEED;
		
		assert currentLightArg != null : "currentLight is null?";
		assert currentLightArg.getClass() == Phase1StopLight.class : "wrong light type";
				
		Phase1StopLight currentLight = (Phase1StopLight)currentLightArg;
		
		Phase1StopLight prevLight = (Phase1StopLight)currentLight.getPrevLight();
		
		double distanceToLight = currentLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		
		//find the proper speed and theoreticalTimeToLight
		while(!currentLight.isLightGreenAtTime(theoreticalTimeToLight)){
			newSpeed = getDeceleratedSpeed(newSpeed);
			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
			
		}
		
		boolean changedLanes = false;
		
		while(car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition())) {
			int otherLane = car.getOtherLane();
			
//			if(car.getLane() == 1) {
//				if(currentLight.getLane2().canChangeLane(car) && prevLight.getLane2().canChangeLane(car)) {
//					//can change lanes
//					currentLight.getLane2().addCar(car);
//					car.setLane(2, null);
//					changedLanes = true;
//				}
//			} else {
//				if(currentLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
//					currentLight.getLane1().addCar(car);
//					car.setLane(1, null);
//					changedLanes = true;
//				}
//			}
//			
//			if(changedLanes) {
//				if(car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition())){
//					changedLanes = false;
//					if(otherLane == 1){
//						currentLight.getLane2().addCar(car);
//						car.setLane(2, null);
//					}
//					else{
//						currentLight.getLane1().addCar(car);
//						car.setLane(1, null);
//					}
//					//These instances of setLane is called because changing lanes didn't work, so we switch back and reduce speed (and try it all again)
//					newSpeed = getDeceleratedSpeed(newSpeed);
//					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//				} else{
//					break;
//				}	//unneeded, but saves a function call
//			} else {
				
				//if the car doesn't change lanes we still need to decelerate right?
				
				newSpeed = getDeceleratedSpeed(newSpeed);
				theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//			}		
		}
		
		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
		
		return changedLanes;
	}

	@Override
	public StopLight buildStopLight(String configString) {
		// TODO Auto-generated method stub
		return new Phase1StopLight(configString);
	}

}
