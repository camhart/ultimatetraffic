package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import simulator.outputter.Outputter;
import simulator.phases.Phase0Handler;
import simulator.phases.Phase1Handler;
import simulator.phases.PhaseHandler;
import simulator.models.CarManager;
import simulator.models.StopLight;

public class Simulator {
	
	private static Object[] outputterParams;
	private static Simulator sim;
	/**
	 * This is a singleton object.  Always use this to gain access to the simulator.
	 * @return
	 */
	public static Simulator getSimulator() {
		if(sim == null) {
			sim = new Simulator(outputterParams);
		}
		return sim;
	}
	
	public static void setOutputterConfig(Object... params) {
		Simulator.outputterParams = params;
	}
	
	//this value must always be a lot of 0's, and a single ending 1.  
	//	No other value will work with the car arrival map.
	//	Examples: 0.1, 0.000000001, 0.0001
	public static final double TIME_PER_ITERATION = 1.0;
	
	
	int numberOfIterations = -1;
	int currentIteration = 0;
	
	StopLight lastLight;
	StopLight firstLight;
	PhaseHandler phase;
	
	HashMap<Double, ArrayList<CarManager>> carArrivalMap;
	
	/**
	 * Private constructor
	 */
	private Simulator(Object... outputterParams) {
		carArrivalMap = new HashMap<Double, ArrayList<CarManager>>();
		Outputter.getOutputter().initialize(outputterParams);
	}
	
	/**
	 * Setter to set total number of iterations the simulator should run
	 * @param numberOfIterations
	 */
	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}
	
	/**
	 * Loads cars from the cars file, and places them into the carArrivalMap.
	 * The map's keys are the iteration number the car arrives on.  The maps
	 * value is an arraylist of cars that arrive at that iteration.
	 * 
	 * Note that the input car file gives a timestamp that the cars arrive at.
	 * 	This method converts that to iteration number doing timestamp / TIME_PER_ITERATION
	 * 
	 * Car file is formatted where each line represents data for a car.  Each
	 * 	line has comma seperated values in the following order:
	 * 		timestamp	lane	startspeed	startposition	endposition direction
	 * 
	 * 	Example:
	 * 		4.731366995,1,19.25124403,0,5956.8,0
	 * 		5.897902291,2,19.25687255,0,5956.8,0
	 * 
	 * @param carsFile
	 */
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
			CarManager curCar = null;
			ArrayList<CarManager> curList = null;
			while(scanner.hasNextLine()) {
				curCar = new CarManager(scanner.nextLine());
				
				//ghetto hax way of forcing specific precision on a double...
				//cars will only show up at precision equivalent to TIME_PER_ITERATION
				Double key = Double.parseDouble(
						new DecimalFormat(precisionString).format(curCar.getArrivalTime() / 
								Simulator.TIME_PER_ITERATION));

				curList = carArrivalMap.get(key);
				if(curList == null) {
					curList = new ArrayList<CarManager>();
					carArrivalMap.put(key,  curList);
				}
				curList.add(curCar);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the lights file.  Builds the double linked list and sets
	 * 	firstLight and lastLight values.
	 * 
	 * Within the light file each line represents data for a light.
	 * 	The data within each line is comma seperated values in the
	 * 	following order:
	 * 		lightPosition, lightType, timeAsGreen, timeAsRed, initialOffset, initialColor
	 * 
	 * 	Examples:
	 * 		636.6,timed,40,30,0,green
	 *		995.8,timed,30,20,0,green
	 *		1409.9,timed,30,20,0,green
	 * @param lightsFile
	 */
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
	
	/**
	 * When a car is first arriving into our system, it needs to be put
	 * 	onto the appropriate stoplight's lane object.  This method will
	 * 	find the appropriate stop light by comparing the car's starting
	 * 	position with the stopLights position.  It will then return that
	 * 	light.
	 * 
	 * 	This method allows us to have cars enter the system at random
	 * 	spots along the road (as opposed to just the beginning of the
	 * 	corridor).
	 * @param car
	 * @return
	 */
	private StopLight findStopLightForArrivingCar(CarManager car) {
		StopLight curLight = this.firstLight;
		while(curLight.getPosition() < car.getArrivalPosition()) {
			curLight = curLight.getNextLight();
		}
		return curLight;
	}
	
	
	/**
	 * This method takes cars from the carArrivalMap and places them into
	 * the appropriate lane of the stop light they're supposed to be on.
	 * @param currentIteration
	 */
	public void handleArrivingCars(double currentIteration) {
		ArrayList<CarManager> cars = this.carArrivalMap.get(currentIteration);
		if(cars != null) {
			CarManager c;
			for(int i = 0; i < cars.size(); i++) {
				c = cars.get(i);
				if(c.getLane() == 1) {
					findStopLightForArrivingCar(c).getLane1().addCar(c);
				}
				else if(c.getLane() == 2) {
					findStopLightForArrivingCar(c).getLane2().addCar(c);
				}
				else
					throw new Error("this shouldn't be happening : " + c.getLane());
			}
		}
	}
	
	/**
	 * Runs the simulator for specified number of iterations.
	 */
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
	
	/**
	 * Sets the phase for the simulator.
	 * @param phase
	 */
	public void setPhase(PhaseHandler phase) {
		this.phase = phase;
	}
	
	/**
	 * get the current iteration from the simulator
	 * @return
	 */
	public int getCurrentIteration() {
		return this.currentIteration;
	}

	/**
	 * Main method to run our simulator, loading files from prompt
	 * args should be
	 * 	phase number, number of iterations to run,
	 * 	 path to cars file, path to lights file
	 * 
	 *  Exmaple:
	 *  	0 60000 config/cars.csv config/lights.csv
	 * @param args
	 */
	public static void main(String[] args) {
		
		PhaseHandler phase = PhaseHandler.buildPhase(Integer.parseInt(args[0]));
		
		int iterationCount = Integer.parseInt(args[1]);
		
		File carsFile = new File(args[2]);
		File stopLightFile = new File(args[3]);
		
		Simulator.setOutputterConfig("db_phase_0.sqlite");
		
		Simulator simulator = Simulator.getSimulator();
		
		simulator.setNumberOfIterations(iterationCount);
		
		simulator.loadLights(stopLightFile);
		simulator.loadCars(carsFile);
		
		simulator.setPhase(phase);
		
		simulator.run();
		
	}
}
