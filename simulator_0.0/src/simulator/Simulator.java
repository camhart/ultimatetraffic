package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
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
	
	//this value must always be a lot of 0's, and a single ending 1.  
	//	No other value will work with the car arrival map.
	//	Examples: 0.1, 0.000000001, 0.0001
	public static final double TIME_PER_ITERATION = 1.0;
	
	
	int numberOfIterations = -1;
	int currentIteration = 0;
	
	StopLight lastLight;
	StopLight firstLight;
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
		StringBuffer precision = new StringBuffer();
		char[] chars = ("" + this.TIME_PER_ITERATION).toCharArray();
		int count = 0;
		for(char a : chars) {
			if(count == 1)
				precision.append('.');
			else
				precision.append('0');
			count++;
		}
		String precisionString = precision.toString();

		
		try {
			Scanner scanner = new Scanner(carsFile);
			Car curCar = null;
			ArrayList<Car> curList = null;
			while(scanner.hasNextLine()) {
				curCar = new Car(scanner.nextLine());
				
				//ghetto hax way of forcing specific precision on a double...
				//cars will only show up at precision equivalent to TIME_PER_ITERATION
				Double key = Double.parseDouble(
						new DecimalFormat(precisionString).format(curCar.getArrivalTime() / 
								Simulator.TIME_PER_ITERATION));

				curList = carArrivalMap.get(key);
				if(curList == null) {
					curList = new ArrayList<Car>();
					carArrivalMap.put(key,  curList);
				}
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
			firstLight = lights.get(0);
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
	
	public StopLight findStopLightForArrivingCar(Car car) {
		StopLight curLight = this.firstLight;
		while(curLight.getPosition() < car.getArrivalPosition()) {
			curLight = curLight.getNextLight();
		}
		return curLight;
	}
	
	public void handleArrivingCars(double currentIteration) {
		ArrayList<Car> cars = this.carArrivalMap.get(currentIteration);
		if(cars != null) {
			Car c;
			for(int i = 0; i < cars.size(); i++) {
				c = cars.get(i);
				if(c.getLane() == 0) {
					findStopLightForArrivingCar(c).getLane1().addCar(c);
				}
				else if(c.getLane() == 1) {
					findStopLightForArrivingCar(c).getLane2().addCar(c);
				}
				else
					throw new Error("this shouldn't be happening");
				System.out.println(findStopLightForArrivingCar(c).getLane1().getCarsInLane());
			}
		}
	}
	
	public void run() {
		
		while(currentIteration < numberOfIterations) {
			
			//lights
			StopLight curLight = this.lastLight;
			
			while(curLight != null) {
				
				//iterate handles everything for the light and the cars within each lane
				//	approaching the light
				curLight.iterate(this.phase, Simulator.TIME_PER_ITERATION);

				curLight = curLight.getPrevLight();
			}
			
			//arriving cars
			this.handleArrivingCars(currentIteration);
			
			
			//increment iteration
			currentIteration++;
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
