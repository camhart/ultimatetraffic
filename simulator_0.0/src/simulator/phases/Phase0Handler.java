package simulator.phases;

import simulator.models.CarManager;
import simulator.models.car.Car;
import simulator.models.stoplights.StopLight;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	public static final double YELLOW_LIGHT_TIME_LEFT = 4.0;
	public static final double RUN_YELLOW_LIGHT_DISTANCE = 50.0;
	public static final int PHASE_NUMBER = 0;
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {

		car.moveCarForward(); // move car
		if (light.getCurrentColor() == StopLight.Color.RED) {
			car.giveStopCommand(car.getStopDistance(light));
		}
		if (light.getCurrentColor() == StopLight.Color.GREEN
				&& light.getTimeUntilChange() <= Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
			// the light is green and about to change red (yellow). Tell car to
			// stop.

			if (car.getCar().getCommand() == Car.Command.GO
					&& car.getPosition() < (light.getPosition() - RUN_YELLOW_LIGHT_DISTANCE)
					&& car.canRunLight(light)) {
				// stop
				car.giveStopCommand(car.getStopDistance(light));
			}
			// else run light

		} else if (car.getCar().getCommand() == Car.Command.STOP
				&& light.getCurrentColor() == StopLight.Color.GREEN) {

			CarManager nextCar = car.getLaneObject().getNextCar(car);
			if (nextCar != null) {
				if (nextCar.getCar().getCommand() == Car.Command.GO) {
					// creates a slight delay between car's being given the go
					// command
					car.giveGoCommand();
				}
				// else wait
			} else {
				car.giveGoCommand();
			}
			// go!
		}

		Outputter.getOutputter().addCarOutput(car);
	}
	
	public void intermediateAlgorithm(CarManager car, StopLight light){
		throw new Error("Phase0 is trying to call the algorithm!");
	}

	@Override
	public StopLight buildStopLight(String configString) {
		return new StopLight(configString);
	}
}
