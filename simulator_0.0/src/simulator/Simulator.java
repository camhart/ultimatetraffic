package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import simulator.outputter.Outputter;
import simulator.phases.Phase0;
import simulator.phases.Phase1;
import simulator.phases.Phase;
import simulator.models.Car;
import simulator.models.StopLight;

public class Simulator {
	
	private static Simulator sim;
	public static Simulator getSimulator() {
		if(sim == null) {
			sim = new Simulator();
		}
		return sim;
	}
	
	public static final double TIME_PER_ITERATION = 0.1;
	int numberOfIterations = -1;
	int currentIteration = 0;
	
	StopLight lastLight;
	Phase phase;
	
	HashMap<Double, ArrayList<Car>> carArrivalMap;
	
	private Simulator() {
		carArrivalMap = new HashMap<Double, ArrayList<Car>>();
		Outputter.getOutputter().initialize();
	}
	
	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}
	
	public void loadCars(File carsFile) {
		try {
			Scanner scanner = new Scanner(carsFile);
			Car curCar = null;
			ArrayList<Car> curList = null;
			while(scanner.hasNextLine()) {
				curCar = new Car(scanner.nextLine());
				Double key = (Double)(curCar.getArrivalTime() / Simulator.TIME_PER_ITERATION);
				curList = carArrivalMap.get(key);
				if(curList == null)
					curList = new ArrayList<Car>();
				curList.add(curCar);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadLights(File lightsFile) {
		ArrayList<StopLight> lights = new ArrayList<StopLight>();
		try {
			Scanner scanner = new Scanner(lightsFile);
			
			while(scanner.hasNextLine()) {
				lights.add(new StopLight(scanner.nextLine()));
			}
			lastLight = lights.get(lights.size() - 1);
			lastLight.setPrevLight(lights.get(lights.size() - 2));
			for(int c = lights.size() - 2; c > -1; c--) {
				if(c - 1 > -1)
					lights.get(c).setPrevLight(lights.get(c - 1));
				
				lights.get(c).setNextLight(lights.get(c + 1));
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
		while(currentIteration < numberOfIterations) {
			
			StopLight curLight = this.lastLight;
			
			while(curLight != null) {
				
				//iterate handles everything for the light and the cars within each lane
				//	approaching the light
				curLight.iterate(this.phase, Simulator.TIME_PER_ITERATION);

				curLight = curLight.getPrevLight();
			}
			
			//increment iteration
			currentIteration++;
			System.out.println(currentIteration);
		}
		
		Outputter.getOutputter().close();
		
	}
	

	private void setPhase(Phase phase) {
		this.phase = phase;
	}

	public static void main(String[] args) {
		
		Phase phase = Phase.buildPhase(Integer.parseInt(args[0]));
		
		int iterationCount = Integer.parseInt(args[1]);
		
		File carsFile = new File(args[2]);
		File stopLightFile = new File(args[3]);
		
		Simulator simulator = Simulator.getSimulator();
		
		simulator.setNumberOfIterations(iterationCount);
		
		simulator.loadLights(stopLightFile);
		simulator.loadCars(carsFile);
		
		simulator.setPhase(phase);
		
		simulator.run();
		
	}

	public int getCurrentIteration() {
		return this.currentIteration;
	}
}
