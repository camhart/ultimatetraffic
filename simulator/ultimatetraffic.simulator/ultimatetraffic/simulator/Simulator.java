package ultimatetraffic.simulator;

import java.io.File;
import java.util.ArrayList;

import ultimatetraffic.simulator.models.Car;
import ultimatetraffic.simulator.models.StopLight;

public class Simulator {
	
	//time per iteration in seconds
	private static final double TIME_PER_ITERATION = 0.1;
	
	//Cars that have yet to arrive
	ArrayList<Car> arrivingCars;
	//Cars that have reached their destination
	ArrayList<Car> finishedCars;
	//Cars currently traveling
	ArrayList<Car> travelingCars;
	
	//Stop lights
	ArrayList<StopLight> stopLights;
	
	int currentIteration;
	
	public Simulator() {
		arrivingCars = new ArrayList<Car>();
		stopLights = new ArrayList<StopLight>();
		finishedCars = new ArrayList<Car>();
		travelingCars = new ArrayList<Car>();
	}
	
	public void buildStopLights(File stopLightConfigFile) {
		
	}
	
	public void buildCars(File carArrivalConfigFile) {
		
	}
	
	public void runSimulation(int numberOfIterations) {
		currentIteration = 0;
		
		while(currentIteration < numberOfIterations) {
			
			currentIteration++;
		}
	}
	
	public void saveSimulationOutput(File fileToSave) {
		
	}
	
	public static void main(String[] args) {
		int iterationCount = Integer.parseInt(args[0]);
		
		File carsFile = new File(args[1]);
		File stopLightFile = new File(args[2]);
		File outputFile = new File(args[3]);
		
		
		
	}
	
}
