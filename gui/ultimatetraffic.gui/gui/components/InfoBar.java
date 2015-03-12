package gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoBar extends JPanel implements MouseMotionListener {
	JLabel mouseX;
	JLabel mouseY;
	JLabel currentIteration;
	
	public InfoBar() {
		super();
		
//		this.setLayout(new BoxLayout(, BoxLayout.X_AXIS));
		
		mouseX = new JLabel();
		mouseY = new JLabel();
		currentIteration = new JLabel("Current Iteration: 0");
		
		this.add(mouseX);
		this.add(mouseY);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add(currentIteration);
		
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
}
