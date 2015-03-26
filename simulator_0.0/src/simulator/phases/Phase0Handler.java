package simulator.phases;

import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.models.car.Car;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	public static final double YELLOW_LIGHT_TIME_LEFT = 4.0;
	public static final double RUN_YELLOW_LIGHT_DISTANCE = 50.0;
	public static final int PHASE_NUMBER = 0;
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {
		
		car.moveCarForward(); //move car
		
		if(light.getCurrentColor() == StopLight.Color.GREEN &&
				light.getTimeUntilChange() <= Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
			//the light is green and about to change red (yellow).  Tell car to stop.
			
			//determine if GO or STOP according to my position and the light position?
//			if(car.getCar().getCommand() == Car.Command.GO && car.getPosition() < light.getPosition() && 
//					((light.getPosition() - car.getPosition() > RUN_YELLOW_LIGHT_DISTANCE) || !car.canRunLight(light))) {
			
			
			if(car.getCar().getCommand() == Car.Command.GO && car.getPosition() < light.getPosition() - RUN_YELLOW_LIGHT_DISTANCE &&
					car.canRunLight(light)) {
				//stop
				car.giveStopCommand(car.getStopDistance(light));
			}
			//else run light
			
		} else if(car.getCar().getCommand() == Car.Command.STOP && 
				light.getCurrentColor() == StopLight.Color.GREEN &&
				light.justChangedGreen()) {
			//the light is green and just changed
			car.giveGoCommand();
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
