package simulator.validator;

import gui.data.LightData;
import gui.sqlite.SQLiteAccessor;

import java.util.ArrayList;

import simulator.Simulator;

public class StopLightValidator extends Validator {
	
	public StopLightValidator(SQLiteAccessor sqlite) {
		this.sqlite = sqlite;
	}
	
	@Override
	public void validateData(int totalIterations) {
		int iteration = 0;
		ArrayList<LightData> lightData = sqlite.getLightData(iteration++);
		while(iteration < totalIterations) {
			for(LightData ld : lightData) {
				if(ld.getTimeUntilChange() < 0) {
					LOG.severe(String.format("StopLight validator failed:\n\t" +
							"iteration: %d timeUntilChange: %f", iteration, ld.getTimeUntilChange()));
				}
			}
			lightData = sqlite.getLightData(iteration++);
		}
	}
}
