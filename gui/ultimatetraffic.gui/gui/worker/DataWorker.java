package gui.worker;

import java.util.ArrayList;
import java.util.List;

import gui.SimulatorGui;
import gui.data.LightData;
import gui.data.StateData;
import gui.sqlite.SQLiteAccessor;

import javax.swing.SwingWorker;

public class DataWorker extends SwingWorker<Boolean, StateData> {
	private int iterations;
	private int currentIteration;
	private int totalIterations;
	private boolean paused;

	public DataWorker(int currentIteration, int iterations) {
		this.iterations = iterations;
		this.totalIterations = this.currentIteration + this.iterations;
		this.currentIteration = currentIteration;
		
		this.addPropertyChangeListener(SimulatorGui.getInstance());
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		long curTime;

		try {			
			StateData sd = new StateData();
			while(currentIteration < this.totalIterations && !this.isCancelled()) {
				curTime = System.currentTimeMillis();
				sd.setLightData(SQLiteAccessor.getSQLite().getLightData(currentIteration));
				sd.setCarData(SQLiteAccessor.getSQLite().getCarData(currentIteration));
				
				this.publish(sd);
				
				currentIteration++;
				
				long sleepVal = (long) (SimulatorGui.getInstance().getTimePerIteration() * 1000 - (System.currentTimeMillis() - curTime));
				if(sleepVal > 0) {
					Thread.sleep(sleepVal);
				}
				while((this.paused && !this.isCancelled())) {
					Thread.sleep(50);
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * GUI can be safely updated from this
	 */
	@Override
	protected void done() {
		
	}
	
	/**
	 * Runs after stuff gets published
	 * 	input will be arbitrary number of published items since last run
	 */
	@Override
	protected void process(List<StateData> list) {
		assert list.size() == 1 : "Missing publish values... GUI cannot keep up with the backend";
		SimulatorGui.getInstance().incrementIterationCounter();
		SimulatorGui.setState(list.get(list.size() - 1));
	}

	public void setPause(boolean b) {
		this.paused = b;
	}
}
