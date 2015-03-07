package simulator.models.car;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

public class VelocityMap {
	
	//Pair<initial velocity,target velocity> <==> Pair<distance, time>
	private HashMap<Pair, Pair> map;
	
	private void load(String filename){
		try {
			Scanner scan = new Scanner(new File(filename));
			scan.nextLine();
			while(scan.hasNext()){
				//Get the four values on the line
				double initial_velocity = scan.nextDouble();
				double target_velocity  = scan.nextDouble();
				double distance		    = scan.nextDouble();
				double time			    = scan.nextDouble();
				//Create the key and value pairs
				Pair key   = new Pair(initial_velocity, target_velocity);
				Pair value = new Pair(distance, time);
				//Insert Pairs into the map
				map.put(key, value);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public VelocityMap(String filename) {
		map = new HashMap<Pair, Pair>();
		load(filename);
	}
	
	public Pair getAccelerationInfo(double current_velocity, double target_velocity){
		return map.get(new Pair(current_velocity, target_velocity));
	}
	
	public Pair getAccelerationInfo(Pair key){
		return map.get(key);
	}
	
	HashMap<Pair, Pair> getMap(){
		return map;
	}
}
