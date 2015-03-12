package gui.listeners;

import gui.data.LightData;

import java.util.ArrayList;
import java.util.Collection;

public interface LightDataListener {
	public void lightDataChanged(Collection<LightData> collection);
}
