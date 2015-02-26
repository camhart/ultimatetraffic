package simulator.outputter;

import simulator.models.Car;
import simulator.models.StopLight;

public interface OutputterInterface {

	public void initialize();
	
	public void addCarOutput(Car car);
	
	public void addLightOutput(StopLight light);

	public void close();
}
