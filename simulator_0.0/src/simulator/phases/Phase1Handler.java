package simulator.phases;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase1Handler extends PhaseHandler  {
	
	private static final int PHASE_NUMBER = 1;

	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
		double carPosition = car.getPosition();
		
		//move the car, then check for speed change.  It has to happen this way
		//	because after this function runs, then the car is put onto the next light
		//	(if it's crossed over).
		car.moveCarForward(); 
		
		if(carPosition >= light.getPosition()){	//TODO: (eventually) adjust for other direction later ( will be <= )
			if(carPosition < car.getDestination()){
				algorithm(car, light);
			}
		}
		
		Outputter.getOutputter().addCarOutput(car);
	}
	
	public void algorithm(CarManager car, StopLight currentLight){
		double newSpeed = MAX_SPEED;
		
		if(currentLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED);
			return;
		}
		
		StopLight prevLight = currentLight.getPrevLight();
		
		double distanceToLight = currentLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight is < 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		while(!currentLight.isLightGreenAtTime(theoreticalTimeToLight)){
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
				if(currentLight.getLane2().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					//can change lanes
					car.setLane(otherLane, currentLight.getLane2());
					changedLanes = true;
				}
			} else {
				if(currentLight.getLane1().canChangeLane(car) && prevLight.getLane1().canChangeLane(car)) {
					car.setLane(otherLane, currentLight.getLane1());
					changedLanes = true;
				}
			}
			
			if(changedLanes) {
				if(car.hitNextCar(theoreticalTimeToLight, distanceToLight)){
					if(otherLane == 1)
						car.setLane(laneNum, currentLight.getLane2());
					else
						car.setLane(laneNum, currentLight.getLane1());
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

	@Override
	public int getPhase() {
		// TODO Auto-generated method stub
		return this.PHASE_NUMBER;
	}

}
