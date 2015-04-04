package simulator.validator;

import gui.data.CarData;
import gui.data.LightData;
import gui.sqlite.SQLiteAccessor;

import java.util.ArrayList;
import java.util.HashMap;

public class StopLightValidator extends Validator {
	
	public StopLightValidator(SQLiteAccessor sqlite) {
		this.sqlite = sqlite;
	}
	
	@Override
	public void validateData(int totalIterations) {
		int iteration = 0;
		ArrayList<LightData> lightData = sqlite.getLightData(iteration);
		while(iteration < totalIterations) {
			for(LightData ld : lightData) {
				
				//make sure the time isn't negative
				if(ld.getTimeUntilChange() < 0) {
					LOG.severe(String.format("StopLight validator failed: light time is negative\n\t" +
							"iteration: %d timeUntilChange: %f", iteration, ld.getTimeUntilChange()));
				}
				
				//make sure no cars are running the red light
				if(ld.getColor() == LightData.Color.RED) {
					HashMap<Integer, CarData> carData = sqlite.getCarData(iteration);
					for(int c = 0; c < carData.values().size(); c++) {
						CarData curCar = (CarData) carData.values().toArray()[c];
						
						//check to see if a car is straddling a light
						//	this will break if the car moves more than a whole car length in a single iteration
						if(curCar.getPosition() > ld.getPosition() && curCar.getPosition() - CarData.CarLength < ld.getPosition()) {
							LOG.severe(String.format("StopLight validator failed: car ran a red light\n\t" +
								"iteration: %d carPosition: %f lightPosition: %f (%f) lightColor: %s timeUntilChange: %f",
								iteration,
								curCar.getPosition(),
								ld.getPosition(),
								curCar.getPosition() - ld.getPosition(),
								ld.getColor().toString(),
								ld.getTimeUntilChange())
							);
						}
					}
				}
			}
			lightData = sqlite.getLightData(iteration);
			iteration++;
		}
	}
}
