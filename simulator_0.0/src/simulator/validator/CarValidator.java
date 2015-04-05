package simulator.validator;

import gui.data.CarData;
import gui.sqlite.SQLiteAccessor;

import java.util.HashMap;

public class CarValidator extends Validator {
	
	public CarValidator(SQLiteAccessor sqlite) {
		this.sqlite = sqlite;
	}
	
	@Override
	public void validateData(int totalIterations) {
		int iteration = 0;
		HashMap<Integer, CarData> carData = sqlite.getCarData(iteration++);
		while(iteration < totalIterations) {
			
			StringBuilder output = new StringBuilder();
			String initalString = String.format("Car validator failed: cars are ontop of each other\niteration: %d\n", iteration);
			output.append(initalString);
			
			for(int c = 0; c < carData.values().size(); c++) {
				CarData cd1 = (CarData) carData.values().toArray()[c];
				
				for(int d = c + 1; d < carData.values().size(); d++) {
					CarData cd2 = (CarData) carData.values().toArray()[d];
				
					if(cd1 != cd2 && cd1.getLane() == cd2.getLane()) {
						if(Math.abs(cd1.getPosition() - cd2.getPosition()) < CarData.CarLength) { // divide by 2 just to leave wiggle room
							//problems... cars hitting eachother
							output.append(String.format("\tcar%dPosition: %f car%dPosition: %f difference: %f\n", cd1.getId(), cd1.getPosition(), cd2.getId(), cd2.getPosition(), Math.abs(cd1.getPosition() - cd2.getPosition())));
						}
					}
				}
			}
			if(output.length() > initalString.length())
				LOG.severe(output.toString());
			carData = sqlite.getCarData(iteration++);
		}
	}
}
