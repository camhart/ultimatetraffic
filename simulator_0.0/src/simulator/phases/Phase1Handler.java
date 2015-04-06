package simulator.phases;

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
		
		Phase1StopLight nextLight = (Phase1StopLight)currentLight.getNextLight();
		
		if(nextLight == null) { //we just drove past the last light
			car.giveChangeSpeedCommand(MAX_SPEED, Command.CHANGE_SPEED);
			return;
		}
		
		double distanceToLight = nextLight.getPosition() - car.getPosition();
		double theoreticalTimeToLight = car.getTimeTo(newSpeed, distanceToLight);
		
		assert distanceToLight >= 0 : "distanceToLight should be >= 0 (" + distanceToLight + ")";
		assert theoreticalTimeToLight >= 0 : "theoreticalTimeToLight is < 0 (" + theoreticalTimeToLight + ")";
		
		while(!nextLight.isLightGreenAtTime(theoreticalTimeToLight) || car.hitNextCar(theoreticalTimeToLight, nextLight.getPosition())){
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
