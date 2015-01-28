package ultimatetraffic.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	File outputFile;
	
	int currentIteration;
	double nextCarArrivalTime;
	
	double totalTime;
	
	private static StringBuilder outputData;
	
	public static StringBuilder getOutputStringBuilder() {
		if(outputData == null) {
			outputData = new StringBuilder();
			return outputData;
		} else {
			return outputData;
		}
	}
	
	public Simulator(File outputFile) {
		this.outputFile = outputFile;
		
		outputData = new StringBuilder();
		arrivingCars = new ArrayList<Car>();
		stopLights = new ArrayList<StopLight>();
		finishedCars = new ArrayList<Car>();
		travelingCars = new ArrayList<Car>();
	}
	
	public void buildStopLights(File stopLightConfigFile) {
		
	}
	
	public void buildCars(File carArrivalConfigFile) {
		//must set nextCarArrivesAt as first arrival
	}
	
	public void runSimulation(int numberOfIterations) {
		currentIteration = 0;
		
		while(currentIteration < numberOfIterations) {
			totalTime+=TIME_PER_ITERATION;	
			currentIteration++;
			
			//update the stop lights
			for(StopLight light : stopLights) {
				if(light.changed(totalTime)) {
					
				}
				
			}
			
			//allow cars to travel
			ArrayList<Car> carsToRemove = new ArrayList<Car>();
			
			for(Car car : travelingCars){
				car.updateCarPosition(TIME_PER_ITERATION);
				
				//update the output data string
				outputData.append(car.getCarIntervalString(totalTime));
				
				if(car.reachedDestination()) {
					carsToRemove.add(car);
				}
			}
			
			//remove finished cars
			for(Car car : carsToRemove) {
				travelingCars.remove(car);
				finishedCars.add(car);
			}
			
			//allow new cars to arrive
			while(arrivingCars.size() > 0 && totalTime >= arrivingCars.get(0).getArrivalTime()) {
				Car arrivingCar = arrivingCars.remove(0);
				travelingCars.add(arrivingCar);
			}		
			
			//write output to file
			if(this.currentIteration > this.writeOutputAtIteration) {
				try {
					this.saveOutput(outputFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static int IterationsPerFileWrite = 100;
	private int writeOutputAtIteration = IterationsPerFileWrite;
	
	public void saveOutput(File outputFile) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(outputFile);
	
		pw.write(outputData.toString());
		pw.close();
		
		outputData = new StringBuilder();
		
		this.writeOutputAtIteration += Simulator.IterationsPerFileWrite;
	}
	
	public static void main(String[] args) {
		int iterationCount = Integer.parseInt(args[0]);
		
		File carsFile = new File(args[1]);
		File stopLightFile = new File(args[2]);
		File outputFile = new File(args[3]);
		
		//create the output file if it doesn't exist
		if(!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Simulator sim = new Simulator(outputFile);
		
		sim.buildStopLights(stopLightFile);
		sim.buildCars(carsFile);
		
		sim.runSimulation(iterationCount);
	}
}
