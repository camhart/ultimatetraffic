package simulator.phases;

import simulator.models.Car;
import simulator.models.StopLight;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			Car car, StopLight currentLight) {
		
		
		//delete all of this stuff
		car.setPosition(car.getPosition() + 20, car.getLane() == 1 ? currentLight.getLane1()
				: currentLight.getLane2());
		
		//if the car moved, have it add output to the outputter
		Outputter.getOutputter().addCarOutput(car);
		
	}
}
