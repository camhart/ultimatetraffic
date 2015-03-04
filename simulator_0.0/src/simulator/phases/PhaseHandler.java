package simulator.phases;

import simulator.models.Car;
import simulator.error.Error;
import simulator.models.StopLight;

public abstract class PhaseHandler {
	
	
	final public double MAX_SPEED = 25;
	final public double DECELERATION = 0.5;
	
	/**
	 * This needs to do EVERYTHING with the car... does it have all the knowledge it needs?
	 * 
	 * @param car - car that needs something done to it
	 * @param currentLight - light the car is approaching (and who's lane the car is currently in)
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
		
	/**
	 * This could be a good spot to crunch numbers of cars finishing.
	 * @param car - car that we need to check if it's finished
	 * @param light - light the car might be removed from if it's finished
	 */
	public void handlePotentialCarFinish(Car car, StopLight light) {
		//I imagine this method will be shared between phase0 and phase1, so I put it here
		if(car.hasFinished())
			light.removeCarFromLane(car);
	}
}
