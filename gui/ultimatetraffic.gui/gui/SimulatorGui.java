package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import gui.components.ButtonBar;
import gui.components.CanvasPanel;
import gui.components.CanvasPanel.Car;
import gui.components.CarDebugPanel;
import gui.components.InfoBar;
import gui.data.CarData;
import gui.data.StateData;
import gui.listeners.DataListener;
import gui.worker.DataWorker;

/**
 * Config file info
 * 	- road length (CanvasPanel)
 *  - iterationTime (DataWorker)
 *  - dbpath - databasePath
 * @author Cam
 *
 */
public class SimulatorGui extends JFrame implements DataListener, PropertyChangeListener {
	
	private static StateData stateData;
	
	private ArrayList<DataListener> dataListeners;

	private int currentIteration;
	
	private static class Holder {
		private static final SimulatorGui INSTANCE = new SimulatorGui();
	}
	
	public static SimulatorGui getInstance() {
		return Holder.INSTANCE;
	}	
	
	private SimulatorGui() {
		this.stateData = new StateData();
		this.dataListeners = new ArrayList<DataListener>();
		this.dataListeners.add(this);
		this.workerDone = true;
		this.currentIteration = 0;
		this.timePerIteration = 0.1;
	}
	
	private JScrollPane scrollPane;
	
	
//	@Override
//	public void lightDataChanged(ArrayList<LightData> data) {
//		// TODO Auto-generated method stub
//		this.lightData = data;
//		
//		//paint()?
//	}

//	@Override
//	public void carDataChanged(ArrayList<CarData> data) {
//		this.carData = data;
//		
//		//paint()?
//	}
	
	public int getCurrentIteration() {
		return currentIteration;
	}

	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
		this.infoBar.setIteration(this.currentIteration);
	}

	CanvasPanel canvasPanel;

	private ButtonBar buttonBar;
	private InfoBar infoBar;
	
	protected void createAndShow(int roadLength) {

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1200,  600);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		canvasPanel = new CanvasPanel(roadLength, 400);
		this.addDataListener(canvasPanel);
		scrollPane = new JScrollPane(canvasPanel);
		
		infoBar = new InfoBar();
		
		canvasPanel.addMouseMotionListener(infoBar);
		
		this.add(infoBar, BorderLayout.SOUTH);
		
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		this.buttonBar = new ButtonBar();
		
		this.getContentPane().add(this.buttonBar,  BorderLayout.NORTH);
		
		JPanel debugPanel = new JPanel();
		debugPanel.setLayout(new GridLayout());
		
		this.carDebugPanel = new CarDebugPanel();
		
		debugPanel.add(this.carDebugPanel);
		
		this.getContentPane().add(debugPanel, BorderLayout.EAST);
		
		this.setVisible(true);
		
		this.buttonBar.chooseDb();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SimulatorGui sg = SimulatorGui.getInstance();
				sg.setTitle("Ultimate Traffic Simulator GUI");
				sg.createAndShow(6000);
			}
		});
	}



	public static void setState(StateData stateData) {
		SimulatorGui.getInstance().stateData = stateData;
		SimulatorGui.getInstance().notifyDataListeners();
	}
	

	public void addDataListener(DataListener dl) {
		this.dataListeners.add(dl);
	}

	private void notifyDataListeners() {
		for(DataListener dl : this.dataListeners) {
			dl.dataChanged(stateData);
		}
	}

	public StateData getStateData() {
		return this.stateData;
	}
	
	public void incrementIterationCounter() {
		this.currentIteration+=this.infoBar.getIterateIncrementValue();
		this.infoBar.setIteration(this.currentIteration);
	}

	@Override
	public void dataChanged(StateData data) {
		int carid = this.carDebugPanel.getSelectedCarId();
		CarData car = data.getCarData().get(carid);
		if(car != null) {
			if(this.carDebugPanel.updatingCarData()) {
				this.carDebugPanel.setCar(car);
			}
			if(this.carDebugPanel.followingCar()) {
				setScrollPosition((int)car.getPosition());
			}
		}
		this.repaint();
		canvasPanel.repaint();
	}
	
	private boolean workerDone = true;

	private CarDebugPanel carDebugPanel;

	private double timePerIteration;
	
	public void setWorkerDone(boolean status) {
		this.workerDone = status;
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getSource().getClass() == DataWorker.class &&
				e.getPropertyName().equals("state")) {
			
			if(e.getNewValue() == SwingWorker.StateValue.DONE)
				workerDone = true;
			else if(e.getNewValue() == SwingWorker.StateValue.STARTED)
				workerDone = false;
			else
				System.out.println("property value" + e.getNewValue() + " type " + e.getNewValue().getClass().getName());
			this.buttonBar.setWorkerDone(workerDone);
		} else {
			System.out.println(String.format("class = %s, propertyName = %s", e.getSource().getClass(), e.getPropertyName()));
		}
//		System.out.println(e.getPropertyName());
//		System.out.println(e.getNewValue());
//		System.out.println(e.getOldValue());
//		System.out.println(e.getSource());
//		System.out.println();
	}

	public CanvasPanel getCanvas() {
		return canvasPanel;
	}

	public CarDebugPanel getCarDebugPanel() {
		return this.carDebugPanel;
		
	}
	
	public void setTimePerIteration(double newTime) {
		this.timePerIteration = newTime;
	}

	public double getTimePerIteration() {
		return this.timePerIteration;
	}
	
	public void setScrollPosition(int position) {
		this.scrollPane.getHorizontalScrollBar().setValue(position - getWidth() / 2);
	}

	public InfoBar getInfoBar() {
		return this.infoBar;
	}
}
