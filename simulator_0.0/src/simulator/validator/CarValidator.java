package simulator.validator;

import gui.data.CarData;
import gui.sqlite.SQLiteAccessor;

import java.util.HashMap;
import java.util.Iterator;

import simulator.Simulator;
import simulator.models.CarManager;

public class CarValidator extends Validator {
	
	public CarValidator(SQLiteAccessor sqlite) {
		this.sqlite = sqlite;
	}
	
	@Override
	public void validateData(int totalIterations) {
		int iteration = 0;
		HashMap<Integer, CarData> carData = sqlite.getCarData(iteration++);
		while(iteration < totalIterations) {
			for(int c = 0; c < carData.values().size(); c++) {
				CarData cd1 = (CarData) carData.values().toArray()[c];
				
				for(int d = c + 1; d < carData.values().size(); d++) {
					CarData cd2 = (CarData) carData.values().toArray()[d];
				
					if(cd1 != cd2 && cd1.getLane() == cd2.getLane()) {
						if(Math.abs(cd1.getPosition() - cd2.getPosition()) < CarManager.CAR_CUSHION) {
							//problems... cars hitting eachother
							LOG.severe(String.format("Car validator failed:\n " +
									"\titeration: %d car 1 position: %f car 2 position: %f difference: %f", iteration, cd1.getPosition(), cd2.getPosition(), Math.abs(cd1.getPosition() - cd2.getPosition())));
						}
					}
				}
			}
			
			carData = sqlite.getCarData(iteration++);
		}
	}
}
