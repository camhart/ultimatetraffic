package gui.components;

import gui.SimulatorGui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InfoBar extends JPanel implements MouseMotionListener {
	JLabel mouseX;
	JLabel mouseY;
	JLabel currentIteration;
	
	JLabel iterationTime;
	JLabel iterationsPerIteration;
	
	JButton changeIterationTime;
	
	JSlider slider;
	
	private int incrementValue = 1;
	
	public InfoBar() {
		super();
		
		InfoBar self = this;
		
//		this.setLayout(new BoxLayout(, BoxLayout.X_AXIS));
		
		mouseX = new JLabel();
		mouseY = new JLabel();
		currentIteration = new JLabel("Current iteration: 0");
		
		iterationTime = new JLabel(String.format("Time per iteration: %.3fs", SimulatorGui.getInstance().getTimePerIteration()));
		
		changeIterationTime = new JButton("Change iteration time");
		changeIterationTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String data = JOptionPane.showInputDialog(SimulatorGui.getInstance(), "Enter a new time per iteration (in seconds):");
				if(data != null) {
					try {
						setTimePerIteration(Double.parseDouble(data));
					} catch (NumberFormatException e) {
						System.out.println("Invalid value (must be a number like 0.1).");
					}
				}
			}
			
		});
		
		iterationsPerIteration = new JLabel("Iterations per iteration: 1");
		
		slider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 1);
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				incrementValue = ((JSlider)e.getSource()).getValue();
				iterationsPerIteration.setText(String.format("Iterations per iteration: %d", incrementValue));
			}
		});
		
		
		this.add(iterationsPerIteration);
		this.add(slider);
		this.add(mouseX);
		this.add(mouseY);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add(currentIteration);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add(currentIteration);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add(iterationTime);
		this.add(changeIterationTime);
		

		this.setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX.setText(String.format("X: %d",e.getX()));
		mouseY.setText(String.format("Y: %d",e.getY()));
	}
	
	public void setIteration(int i) {
		currentIteration.setText(String.format("Current Iteration: %d", i));
	}
	
	public void setTimePerIteration(double newVal) {
		
		SimulatorGui.getInstance().setTimePerIteration(newVal);
		
		iterationTime.setText(String.format("Time per iteration: %.3f", SimulatorGui.getInstance().getTimePerIteration()));
	}
	
	public int getIterateIncrementValue() {
		return incrementValue;
	}
}
