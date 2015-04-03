package simulator.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gui.data.CarData;
import gui.data.LightData;
import gui.sqlite.SQLiteAccessor;

public class DatabaseCompare {
	
	public static final Logger LOG = Logger.getLogger(DatabaseCompare.class.getName());
	
	static {
		LOG.setLevel(Level.ALL);
//		LOG.setUseParentHandlers(false);
	}
	
	private SQLiteAccessor db1;
	private SQLiteAccessor db2;
	private int db1Iterations;
	private int db2Iterations;

	public DatabaseCompare(String db1path, String db2path) {
		db1 = SQLiteAccessor.getSQLite();
		db1.init(db1path);
		
		db2 = SQLiteAccessor.getSQLite2();
		db2.init(db2path);
		
		db1Iterations = db1.getTotalIteration();
		db2Iterations = db2.getTotalIteration();
	}
	
	public boolean compareIterationCounts() {
		LOG.severe("now comparing iteration maxes");
		if(db1Iterations != db2Iterations) {
			LOG.severe(String.format("iteration mismatch db1 (%d) != db2 (%d)" + 
					"\n\tThe compares won't show all mismatched values", db1Iterations, db2Iterations));
			return false;
		}
		return true;
	}
	
	public boolean compareLights() {
		LOG.severe("now comparing lights");
		boolean ret = true;
		for(int c = 0; c < db1Iterations; c++) {
			ArrayList<LightData> l2Data = db2.getLightData(c);
			for(LightData ld1 : db1.getLightData(c)) {
				
				if(!l2Data.remove(ld1)) {
					LOG.severe(String.format("light data mismatch: %s not found in db2", ld1));
					ret = false;
				}
			}
			if(l2Data.size() > 0) {
				LOG.severe(String.format("light data mismatch: %d light data still left in db2 at iteration %d", l2Data.size(), c));
				ret = false;
			}
		}
		return ret;
	}
	
	public boolean compareCars() {
		LOG.severe("now comparing cars");
		boolean ret = true;
		
		for(int c = 0; c < db1Iterations; c++) {
//			if(c % 100 == 0)
				System.out.println(String.format("%d/%d", c, db1Iterations));
			
			
			HashMap<Integer, CarData> c1Map = db1.getCarData(c);
			Set<Integer> keySet =  c1Map.keySet();
			HashMap<Integer, CarData> c2Map = db2.getCarData(c);
			
			for(Integer key : keySet) {
				CarData cd1 = c1Map.get(key);
				
				if(!cd1.equals(c2Map.remove(key))) {
					LOG.severe(String.format("car data mismatch: %s not found in db2", cd1));
					ret = false;
				}
			}
		}
		return ret;
	}
	
	public void compare() {
		compareIterationCounts();
		compareLights();
		compareCars();
		LOG.info("done");
	}
	
	public static void main(String[] args) {
		String db1 = "db_phase_" + args[0] + ".sqlite";
		String db2 = "db_phase_" + args[1] + ".sqlite";
		DatabaseCompare dc = new DatabaseCompare(db1, db2);
		dc.compare();
	}

}
