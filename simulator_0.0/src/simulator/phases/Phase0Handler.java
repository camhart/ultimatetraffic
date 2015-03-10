package simulator.phases;

import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	private static final double YELLOW_LIGHT_TIME_LEFT = 5.0;
	private static final double RUN_YELLOW_LIGHT_DISTANCE = 30.0;
	
	private static final int PHASE_NUMBER = 0;
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
		car.moveCarForward(); //move car
		
		if(light.getCurrentColor() == StopLight.Color.GREEN &&
				light.getTimeUntilChange() == Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
			//the light is green and about to change red (yellow).  Tell car to stop.
			
			//determine if GO or STOP according to my position and the light position?
			if(light.getPosition() - car.getPosition() < RUN_YELLOW_LIGHT_DISTANCE) {
				//run light
				
			} else {
				//stop
				car.stop();
			}
			
		} else if(light.getCurrentColor() == StopLight.Color.RED &&
				light.justChangedColor()) {
			//the light is red but changing green
			car.go();
			//go!
		}		
		
		Outputter.getOutputter().addCarOutput(car);
	}

	@Override
	public int getPhase() {
		// TODO Auto-generated method stub
		return this.PHASE_NUMBER;
	}
}
