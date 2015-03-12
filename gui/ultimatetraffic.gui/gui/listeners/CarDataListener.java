package gui.listeners;

import gui.data.CarData;

import java.util.ArrayList;
import java.util.HashMap;

public interface CarDataListener {
	public void carDataChanged(HashMap<Integer, CarData> carData);
}
