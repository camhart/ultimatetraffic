package simulator.phases;

import simulator.models.CarManager;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight currentLight) {
		
		
		//delete all of this stuff
		car.setPosition(car.getPosition() + 20, car.getLane() == 1 ? currentLight.getLane1()
				: currentLight.getLane2());		
		
		Outputter.getOutputter().addCarOutput(car);
	}
}
