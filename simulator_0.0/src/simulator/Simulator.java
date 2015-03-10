package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
	public static final Logger LOG = Logger.getLogger(Simulator.class.getName());
	
	static {
		LOG.addHandler(new ConsoleHandler());
		LOG.setLevel(Level.WARNING);
	}
	
	
	public static void setOutputterConfig(Object... params) {
		Simulator.outputterParams = params;
	}
	
	//  Changing this could break things...
	public static final double TIME_PER_ITERATION = 0.1;
	
	
	int numberOfIterations = -1;
	int currentIteration = 0;
	
	StopLight lastLight;
	StopLight firstLight;
	PhaseHandler phase;
	
	ArrayList<CarManager> finishedCars;
	
	HashMap<Integer, ArrayList<CarManager>> carArrivalMap;
	private int carsLeftToArrive;
	
	/**
	 * Private constructor
	 */
	private Simulator(Object... outputterParams) {
		carArrivalMap = new HashMap<Integer, ArrayList<CarManager>>();
		finishedCars = new ArrayList<CarManager>();
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
		int precision = 0;
		String pstr = "" + Simulator.TIME_PER_ITERATION;
		char[] pstrArr = pstr.toCharArray();
		boolean decimalFound = false;
		for(char c : pstrArr) {
			if(decimalFound)
				precision++;
			if(c == '.') {
				decimalFound = true;
			}
		}
		
		try {
			Scanner scanner = new Scanner(carsFile);
			CarManager curCar = null;
			ArrayList<CarManager> curList = null;
			while(scanner.hasNextLine()) {
				curCar = new CarManager(scanner.nextLine());

				
				int key = (int)(new BigDecimal(curCar.getArrivalTime()).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue() / Simulator.TIME_PER_ITERATION);
				
				curList = carArrivalMap.get(key);
				if(curList == null) {
					curList = new ArrayList<CarManager>();
					carArrivalMap.put(key,  curList);
				}
				carsLeftToArrive++;
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
	public void handleArrivingCars(int currentIteration) {
//		ArrayList<CarManager> cars = this.carArrivalMap.remove(currentIteration * Simulator.TIME_PER_ITERATION);
		ArrayList<CarManager> cars = this.carArrivalMap.remove(currentIteration);
		if(cars != null) {
			CarManager c;
			for(int i = 0; i < cars.size(); i++) {
				c = cars.get(i);
				if(c.getLane() == 1) {
					StopLight light = findStopLightForArrivingCar(c);
					light.getLane1().addCar(c);
					c.setLane(1,  light.getLane1());
				}
				else if(c.getLane() == 2) {
					StopLight light = findStopLightForArrivingCar(c);
					light.getLane2().addCar(c);
					c.setLane(2,  light.getLane2());
				}
				else
					throw new Error("this shouldn't be happening : " + c.getLane());
				carsLeftToArrive--;
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
			
			LOG.info(String.format("%s (%.1f s), iteration %d / %d, cars left %d, cars finished %d",
					getTime(), currentIteration * Simulator.TIME_PER_ITERATION,
					currentIteration, numberOfIterations, carsLeftToArrive,
					this.finishedCars.size()));
		}
		
		Outputter.getOutputter().close();
	}
	
	static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS");
	static {
		DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	private String getTime() {
  	    return DATE_FORMATTER.format(currentIteration * Simulator.TIME_PER_ITERATION * 1000);
	}
	
	/**
	 * Sets the phase for the simulator.
	 * @param phase
	 */
	public void setPhase(PhaseHandler phase) {
		this.phase = phase;
	}
	
	public int getPhase() {
		return this.phase.getPhase();
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
	 *  	1 10000 config/cars.csv config/lights.csv
	 * @param args
	 */
	public static void main(String[] args) {
		
		PhaseHandler phase = PhaseHandler.buildPhase(Integer.parseInt(args[0]));
		
		int iterationCount = Integer.parseInt(args[1]);
		
		File carsFile = new File(args[2]);
		File stopLightFile = new File(args[3]);
		
		Simulator.setOutputterConfig("db_phase_" + args[0] + ".sqlite");
		
		Simulator simulator = Simulator.getSimulator();
		
		simulator.setNumberOfIterations(iterationCount);
		
		simulator.loadLights(stopLightFile);
		simulator.loadCars(carsFile);
		
		simulator.setPhase(phase);
		
		simulator.run();
		
		simulator.printStuff();
		
	}

	private void printStuff() {
		long totalIterations = 0;
		for(CarManager c : this.finishedCars) {
			totalIterations += c.getIterations();
		}
		LOG.info(String.format("Total travel time: %.1f seconds (%s)", totalIterations * this.TIME_PER_ITERATION, Simulator.DATE_FORMATTER.format(totalIterations * this.TIME_PER_ITERATION * 1000)));
	}

	public void finishCar(CarManager car) {
		this.finishedCars.add(car);
		car.getLaneObject().removeCar(car);
	}
}
