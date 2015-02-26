package simulator.phases;

import simulator.models.Car;
import simulator.error.Error;
import simulator.models.StopLight;

public abstract class PhaseHandler {
	/**
	 * This needs to do EVERYTHING with the car... does it have all the knowledge it needs?
	 * 
	 * @param car
	 * @param upcomingLight
	 */
	public abstract void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(Car car, StopLight currentLight);
	
	public static PhaseHandler buildPhase(int phaseNumber) {
		if(phaseNumber == 0)
			return new Phase0Handler();
		else if(phaseNumber == 1)
			return new Phase1Handler();
		else {
			throw new Error("");
		}
	}
		
	public void handlePotentialCarFinish(Car car, StopLight light) {
		//I imagine this method will be shared between phase0 and phase1, so I put it here
		if(car.hasFinished())
			light.removeCarFromLane(car);
	}
}
