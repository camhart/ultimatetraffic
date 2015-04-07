package simulator.phases;

import simulator.models.CarManager;
import simulator.models.car.Car.Command;
import simulator.models.stoplights.Phase1StopLight;
import simulator.models.stoplights.Phase2StopLight;
import simulator.models.stoplights.StopLight;
import simulator.outputter.Outputter;




public class Phase2Handler extends PhaseHandler  {
	
	private static final int PHASE_NUMBER = 2;

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
		//move the car, then check for speed change.  It has to happen this way
		//	because after this function runs, then the car is put onto the next light
		//	(if it's crossed over).
		car.moveCarForward();
		
		if(car.getPosition() >= light.getPosition()){
			algorithm(car, light);
		}	
		else {
			this.intermediateAlgorithm(car, light);
		}
		
		Outputter.getOutputter().addCarOutput(car);
	}
	
	public void algorithm(CarManager car, StopLight currentLight){
		double newSpeed = MAX_SPEED;
		
		assert currentLight != null : "currentLight is null?";
		
		Phase2StopLight nextLight = (Phase2StopLight)currentLight.getNextLight();
		
		if(nextLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED, Command.CHANGE_SPEED);
			return;
		}
		
		double distanceToLight = nextLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		while(!nextLight.canLightBeGreenAtTime(theoreticalTimeToLight) || car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
			newSpeed = getDeceleratedSpeed(newSpeed);
			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		}
		
		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
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
	
	public void intermediateAlgorithm(CarManager car, StopLight currentLightArg){
		double newSpeed = MAX_SPEED;
		
		assert currentLightArg != null : "currentLight is null?";
		assert currentLightArg.getClass() == Phase2StopLight.class : "wrong light type";
		
		double distanceToLight = currentLightArg.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "this shouldn't be happening... we should be calling algorithm instead";
		
		Phase2StopLight currentLight = (Phase2StopLight)currentLightArg;
				
		//find the proper speed and theoreticalTimeToLight
		while(!(currentLight.canLightBeGreenAtTime(theoreticalTimeToLight) &&
				!car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition()))){
			
			newSpeed = getDeceleratedSpeed(newSpeed);
			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
			
			if(theoreticalTimeToLight < 0) {
				//no matter how fast or slow we go, we're going to hit the light red
				newSpeed = 0;
				theoreticalTimeToLight = Double.MAX_VALUE;
				break;
			}
			
		}
			
//		while(car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition())) {
//			System.out.println("b " + newSpeed + " car " + car.getId());
//			
//			newSpeed = getDeceleratedSpeed(newSpeed);
//			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//		}
		
		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
	}

	@Override
	public StopLight buildStopLight(String configString) {
		// TODO Auto-generated method stub
		return new Phase2StopLight(configString);
	}

}












//public class Phase2Handler extends PhaseHandler  {
//	
//	private static final int PHASE_NUMBER = 2;
//
//	@Override
//	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
//			CarManager car, StopLight light) {
//		
//	
//		//move the car, then check for speed change.  It has to happen this way
//		//	because after this function runs, then the car is put onto the next light
//		//	(if it's crossed over).
//		car.moveCarForward(); 
//		
//		double carPosition = car.getPosition();
//		
//		if(carPosition >= light.getPosition()){	//TODO: (eventually) adjust for other direction later ( will be <= )
//			if(carPosition < car.getDestination()){
//				algorithm(car, light);
//			}
//		}
//		
//		Outputter.getOutputter().addCarOutput(car);
//	}
//	
//	public void algorithm(CarManager car, StopLight currentLight){
//		double newSpeed = MAX_SPEED;
//		
//		assert currentLight != null : "currentLight is null?";
//		assert currentLight.getClass() == Phase2StopLight.class : "Wrong type of light";
//		
//		Phase2StopLight nextLight = (Phase2StopLight)currentLight.getNextLight();
//		
//		if(nextLight == null) { //we just drove past the last light
//			car.giveChangeSpeedCommand(MAX_SPEED, Command.CHANGE_SPEED);
//			return;
//		}
//		
//		StopLight prevLight = currentLight;
//		if(currentLight.getPosition() > 2){	//This is to catch a bug when calling algorithm on the first light
//			prevLight = currentLight.getPrevLight();
//		}
//		
//		double distanceToLight = nextLight.getPosition() - car.getPosition();
//		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//		
//		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
//		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
//		
//		int laneNum = car.getLane();
//		if(laneNum == 1){
//			car.setLane(laneNum, nextLight.getLane1());
//		}
//		else{
//			car.setLane(laneNum, nextLight.getLane2());
//		}
//		
//		while(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){	//TODO: Function must be created
//			int otherLane = car.getOtherLane();
//			
//			boolean changedLanes = false;
//			if(car.getLane() == 1) {
//				if(nextLight.getLane2().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
//					//can change lanes
//					car.setLane(otherLane, nextLight.getLane2());
//					changedLanes = true;
//				}
//			} else {
//				if(nextLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
//					car.setLane(otherLane, nextLight.getLane1());
//					changedLanes = true;
//				}
//			}
//			
//			if(changedLanes) {
//				if(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
//					if(otherLane == 1)
//						car.setLane(laneNum, nextLight.getLane2());
//					else
//						car.setLane(laneNum, nextLight.getLane1());
//					//These instances of setLane is called because changing lanes didn't work, so we switch back and reduce speed (and try it all again)
//					if(newSpeed > DECELERATION){
//						newSpeed -= DECELERATION;
//						if(newSpeed == 0.5){
//							System.out.println("WHAAAAAAAT? (1)");
//						}
//					}
//					else{
//						newSpeed = newSpeed*0.9;
//					}
//					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//				}
//				else{break;}	//unneeded, but saves a function call
//			} else {
//				
//				//if the car doesn't change lanes we still need to decelerate right?
//				
//				if(newSpeed > DECELERATION){
//					newSpeed -= DECELERATION;
//					if(newSpeed == 0.5){
//						System.out.println("WHAAAAAAAT?");
//					}
//				}
//				else{
//					newSpeed = newSpeed*0.9;
//				}
//				theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//				
//				assert newSpeed != 0 : "We shouldn't ever be setting the speed to 0...";
//			}		
//		}
//		
//		while(!nextLight.canLightBeGreenAtTime(theoreticalTimeToLight)){
//			if(newSpeed > DECELERATION){
//				newSpeed -= DECELERATION;
//			}
//			else{
//				newSpeed = newSpeed*0.9;
//			}
//			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//			
//		}
//		
//		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
//	}
//	
//	public void intermediateAlgorithm(CarManager car, StopLight currentLightArg){
//		double newSpeed = MAX_SPEED;
//		
//		assert currentLightArg != null : "currentLight is null?";
//		
//		Phase2StopLight currentLight = (Phase2StopLight)currentLightArg;
//				
//		StopLight prevLight = currentLight.getPrevLight();
//		
//		double distanceToLight = currentLight.getPosition() - car.getPosition();
//		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//		
//		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
//		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
//		
//		int laneNum = car.getLane();
//		if(laneNum == 1){
//			car.setLane(laneNum, currentLight.getLane1());
//		}
//		else{
//			car.setLane(laneNum, currentLight.getLane2());
//		}
//		
//		while(car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition())){	//TODO: Function must be created
//			int otherLane = car.getOtherLane();
//			
//			boolean changedLanes = false;
//			if(car.getLane() == 1) {
//				if(currentLight.getLane2().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
//					//can change lanes
//					car.setLane(otherLane, currentLight.getLane2());
//					changedLanes = true;
//				}
//			} else {
//				if(currentLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
//					car.setLane(otherLane, currentLight.getLane1());
//					changedLanes = true;
//				}
//			}
//			
//			if(changedLanes) {
//				if(car.hitNextCar(theoreticalTimeToLight, currentLight.getPosition())){
//					if(otherLane == 1)
//						car.setLane(laneNum, currentLight.getLane2());
//					else
//						car.setLane(laneNum, currentLight.getLane1());
//					//These instances of setLane is called because changing lanes didn't work, so we switch back and reduce speed (and try it all again)
//					if(newSpeed > DECELERATION){
//						newSpeed -= DECELERATION;
//						if(newSpeed == 0.5){
//							System.out.println("WHAAAAAAAT? (1)");
//						}
//					}
//					else{
//						newSpeed = newSpeed*0.9;
//					}
//					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//				}
//				else{break;}	//unneeded, but saves a function call
//			} else {
//				
//				//if the car doesn't change lanes we still need to decelerate right?
//				
//				if(newSpeed > DECELERATION){
//					newSpeed -= DECELERATION;
//					if(newSpeed == 0.5){
//						System.out.println("WHAAAAAAAT?");
//					}
//				}
//				else{
//					newSpeed = newSpeed*0.9;
//				}
//				theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//				
//				assert newSpeed != 0 : "We shouldn't ever be setting the speed to 0...";
//			}		
//		}
//		
//		while(!currentLight.canLightBeGreenAtTime(theoreticalTimeToLight)){
//			if(newSpeed > DECELERATION){
//				newSpeed -= DECELERATION;
//			}
//			else{
//				newSpeed = newSpeed*0.9;
//			}
//			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//		}
//		
//		car.giveChangeSpeedCommand(newSpeed, Command.CHANGE_SPEED);
//	}
//
//	@Override
//	public StopLight buildStopLight(String configString) {
//		// TODO Auto-generated method stub
//		return new Phase2StopLight(configString);
//	}
//
//}
