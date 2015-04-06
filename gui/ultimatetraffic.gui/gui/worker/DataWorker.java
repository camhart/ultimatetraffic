package gui.worker;

import java.util.List;

import gui.SimulatorGui;
import gui.data.StateData;
import gui.sqlite.SQLiteAccessor;

import javax.swing.SwingWorker;

public class DataWorker extends SwingWorker<Boolean, StateData> {
	private int iterations;
	private int totalIterations;
	private boolean paused;

	public DataWorker(int iterations) {
		this.iterations = iterations;
		this.totalIterations = SimulatorGui.getInstance().getCurrentIteration() + this.iterations;
		
		this.addPropertyChangeListener(SimulatorGui.getInstance());
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		long curTime;

		try {			
			StateData sd = new StateData();
			
			if(SimulatorGui.getInstance().getCurrentIteration() > 0) {
				sd.setLightData(SQLiteAccessor.getSQLite().getLightData(0));
				this.publish(sd);
			}
			
			while(SimulatorGui.getInstance().getCurrentIteration() < this.totalIterations && !this.isCancelled()) {
				curTime = System.currentTimeMillis();
				sd.setLightData(SQLiteAccessor.getSQLite().getLightData(SimulatorGui.getInstance().getCurrentIteration()));
				sd.setCarData(SQLiteAccessor.getSQLite().getCarData(SimulatorGui.getInstance().getCurrentIteration()));
				
				this.publish(sd);
				
				
				long sleepVal = (long) (SimulatorGui.getInstance().getTimePerIteration() * 1000 - (System.currentTimeMillis() - curTime));
				long startedAt = System.currentTimeMillis();
				while(sleepVal > 0 && startedAt > lastPause) {
					Thread.sleep((sleepVal > 100) ? 100 : sleepVal);
					sleepVal -= 100;
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

	private static long lastPause;
	public void setPause(boolean b) {
		lastPause = System.currentTimeMillis();
		this.paused = b;
	}
}
