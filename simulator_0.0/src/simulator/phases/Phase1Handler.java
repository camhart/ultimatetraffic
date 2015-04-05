package simulator.phases;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.Lane;
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
		
		if(car.getPosition() >= light.getPosition()){
			algorithm(car, light);
		} else {
			this.intermediateAlgorithm(car, light);
		}
		
		Outputter.getOutputter().addCarOutput(car);
	}
	
	protected enum AlgorithmState {
		CHANGE_LANE, DECELERATE;
	}
	
	public void algorithm(CarManager car, StopLight currentLight){
		double newSpeed = MAX_SPEED;
		
		System.out.println("wtf");
		
		assert currentLight != null : "currentLight is null?";
		
		Phase1StopLight nextLight = (Phase1StopLight)currentLight.getNextLight();
		
		if(nextLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED, Command.CHANGE_SPEED);
			return;
		}
		
		double distanceToLight = nextLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
//		while(!nextLight.isLightGreenAtTime(theoreticalTimeToLight)){
//			newSpeed = getDeceleratedSpeed(newSpeed);
//			theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//		}
		
		int originalLane = car.getLane();
		
		AlgorithmState state = AlgorithmState.CHANGE_LANE;
		boolean changedLanes = false;
		while(!nextLight.isLightGreenAtTime(theoreticalTimeToLight) || car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
			
			switch(state) {
				case CHANGE_LANE:
					Lane currentLane = car.getLaneObject();
					Lane otherLane = currentLane.getOtherLane();
					
					Lane nextLightOtherLane = (car.getLane() == 1) ?
							otherLane.getParentLight().getNextLight().getLane2() :
							otherLane.getParentLight().getNextLight().getLane1();
					
					if(otherLane.canChangeLane(car) && (nextLightOtherLane == null || nextLightOtherLane.canChangeLane(car))) {
						car.setLane(car.getLane() == 1 ? 2 : 1, otherLane);
						otherLane.addCar(car);
//						currentLane.removeCar(car);

						changedLanes = true;
					}
					
					state = AlgorithmState.DECELERATE;
					break;
				case DECELERATE:
					changedLanes = false;
					newSpeed = getDeceleratedSpeed(newSpeed);
					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
					state = AlgorithmState.CHANGE_LANE;
					break;
			}
		}
		
		if(changedLanes) {
			currentLight.addCarToLaneRemove(originalLane, car);
			System.out.println("Changed lanes...");
		}
		
		
//		while(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){	//TODO: Function must be created
//			System.out.println("a");
//			if(car.getLane() == 1) {
//				if(nextLight.getLane2().canChangeLane(car) &&  (prevLight != null && prevLight.getLane2().canChangeLane(car))) {
//					//can change lanes
//					car.setLane(2, nextLight.getLane2());
//					changedLanes = true;
//				}
//			} else {
//				if(nextLight.getLane1().canChangeLane(car) && (prevLight != null && prevLight.getLane1().canChangeLane(car))) {
//					car.setLane(1, nextLight.getLane1());
//					changedLanes = true;
//				}
//			}
//			
//			if(changedLanes) {
//				if(car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
//					
////					These instances of setLane is called because changing lanes
////					didn't work, so we switch back and reduce speed (and try it all again)
//					if(car.getOtherLane() == 1)
//						car.setLane(2, nextLight.getLane2());
//					else
//						car.setLane(1, nextLight.getLane1());
//					
//					newSpeed = getDeceleratedSpeed(newSpeed);
//					theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//					changedLanes = false;
//				} else {
//					System.out.println("CHANING LANES !@#$%");
//					//we're sticking with the lane change
//					break; //unneeded, but saves a function call
//				}	
//			} else {
//				
//				//if the car doesn't change lanes we still need to decelerate
//				// because we're still hitting the next car
//				
//				newSpeed = getDeceleratedSpeed(newSpeed);
//				theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
//			}
//		}
		
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
		assert currentLightArg.getClass() == Phase1StopLight.class : "wrong light type";
		
		double distanceToLight = currentLightArg.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "this shouldn't be happening... we should be calling algorithm instead";
		
		Phase1StopLight currentLight = (Phase1StopLight)currentLightArg;
				
		//find the proper speed and theoreticalTimeToLight
		while(!(currentLight.isLightGreenAtTime(theoreticalTimeToLight) &&
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
		return new Phase1StopLight(configString);
	}

}
