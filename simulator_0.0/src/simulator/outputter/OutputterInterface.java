package simulator.outputter;

import simulator.models.CarManager;
import simulator.models.StopLight;

public interface OutputterInterface {

	public void initialize(Object... newParam);
	
	public void addCarOutput(CarManager car);
	
	public void addLightOutput(StopLight light);

	public void close();

	void addConfigOutput(String databasePath, int roadLength,
			double iterationTime, String description);
}
