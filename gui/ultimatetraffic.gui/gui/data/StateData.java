package gui.data;

import gui.SimulatorGui;
import gui.listeners.CarDataListener;
import gui.listeners.LightDataListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class StateData {
	
//	private static class Holder {
//		static final StateData INSTANCE = new StateData();
//	}
//		
//	public static StateData getStateData() {
//		return Holder.INSTANCE;
//	}
	
	private HashMap<Integer, CarData> carData;
	private HashMap<Integer, LightData> lightData;
//	private int currentIteration;
	private ArrayList<CarDataListener> carListeners;
	private ArrayList<LightDataListener> lightListeners;
	private double iterationTime;
	
	public StateData() {
		carData = new HashMap<Integer, CarData>();
		lightData = new HashMap<Integer, LightData>();
		this.iterationTime = 0.1;
		
		this.carListeners = new ArrayList<CarDataListener>();
		this.lightListeners = new ArrayList<LightDataListener>();
	}
	
	public void setIterationTime(double time) {
		this.iterationTime = time;
	}
	
	/**
	 * Used only by SimIterator (background worker)
	 * @param carData
	 */
	public void setCarData(HashMap<Integer, CarData> carData) {
		this.carData = carData;
		
		for(CarDataListener c : this.carListeners) {
			c.carDataChanged(this.carData);
		}
	}
	
	/**
	 * Used only by SimIterator (background worker)
	 * @param lightData
	 */
	public void setLightData(ArrayList<LightData> lData) {
		
		for(LightData l : lData) {
			this.lightData.put(l.getId(), l);
		}
		
		for(LightDataListener c : this.lightListeners) {
			c.lightDataChanged(this.lightData.values());
		}
	}
	
	/**
	 * Used only by SimulatorGui
	 */
	public void iterate() {
//		this.currentIteration++;
		for(LightData l : this.lightData.values()) {
			l.setTimeUntilChange(l.getTimeUntilChange() - 0.1);
		}
	}
	
//	/**
//	 * Used only by SimIterator (background worker)
//	 * @return
//	 */
//	public int getCurrentIteration() {
//		return this.currentIteration;
//	}
	
	/**
	 * Used only by SimulatorGui
	 */
	public HashMap<Integer, CarData> getCarData() {
		return this.carData;
	}
	
	/**
	 * Used only by SimulatorGui
	 */
	public Collection<LightData> getLightData() {
		return this.lightData.values();
	}
	
	public HashMap<Integer, LightData> getLightDataMap() {
		return this.lightData;
	}

	public double getIterationTime() {
		return this.iterationTime;
	}

//	public void setCurrentIteration(int i) {
//		this.currentIteration = i;
//	}
}
