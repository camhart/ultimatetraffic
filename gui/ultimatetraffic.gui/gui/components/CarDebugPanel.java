package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.SimulatorGui;
import gui.components.CanvasPanel.Car;
import gui.data.CarData;

public class CarDebugPanel extends JPanel {
	private JTable table;
	
	private DefaultTableModel tableModel;
	private JCheckBox snapToCar;
	private JCheckBox updateData;
	
	public CarDebugPanel() {
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		tableModel = new DefaultTableModel(new Object[][]{
				{"Id", ""},
				{"Position", ""},
				{"Velocity", ""},
				{"Acceleration", ""},
				{"Lane", ""},
				{"LightId", ""},
				{"Command", ""},
				{"Stop Position", ""}
		}, new Object[]{"Car Property", "Value"});
		
		table = new JTable(7, 2);
		table.setEnabled(false);
		table.setModel(tableModel);
		JLabel label = new JLabel("Car Values");
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, 18));
		this.add(label);
		this.add(table);
		
		JButton hidePanelButton = new JButton("Hide");
		hidePanelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				hideMe();
			}
			
		});
		this.add(hidePanelButton);
		
		snapToCar = new JCheckBox();
		
		snapToCar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int position = (int)Double.parseDouble((String)tableModel.getValueAt(1,  1));
				SimulatorGui.getInstance().setScrollPosition(position);
			}
			
		});
		
		this.add(Box.createRigidArea(new Dimension(0, 5)));
		this.add(new JLabel("Follow Car:"));
		this.add(snapToCar);
		
		updateData = new JCheckBox();
		
		this.add(Box.createRigidArea(new Dimension(0, 5)));
		this.add(new JLabel("Update Data:"));
		this.add(updateData);
		
		hideMe();
		
	}
	
	private void hideMe() {
		this.setVisible(false);
		this.setPreferredSize(new Dimension(0, 0));
	}
	

	public void setCar(Car c) {
		tableModel.setValueAt(Integer.toString(c.id), 0, 1);
		tableModel.setValueAt(String.format("%.5f", c.position), 1, 1);
		tableModel.setValueAt(String.format("%.5f", c.velocity), 2, 1);
		tableModel.setValueAt(String.format("%.5f", c.acceleration), 3, 1);
		tableModel.setValueAt(Integer.toString(c.lane), 4, 1);
		tableModel.setValueAt(Integer.toString(c.lightId), 5, 1);
		tableModel.setValueAt(c.command, 6, 1);
		tableModel.setValueAt(String.format("%.5f", c.stopPosition), 7,  1);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(200, 0));
	}

	public void setCar(CarData c) {
		tableModel.setValueAt(Integer.toString(c.getId()), 0, 1);
		tableModel.setValueAt(String.format("%.5f", c.getPosition()), 1, 1);
		tableModel.setValueAt(String.format("%.5f", c.getVelocity()), 2, 1);
		tableModel.setValueAt(String.format("%.5f", c.getAcceleration()), 3, 1);
		tableModel.setValueAt(Integer.toString(c.getLane()), 4, 1);
		tableModel.setValueAt(Integer.toString(c.getLightId()), 5, 1);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(200, 0));
	}

	public boolean followingCar() {
		return this.snapToCar.isSelected();
	}
	
	public void unFollowCar() {
		this.snapToCar.setSelected(false);
	}
	
	public boolean updatingCarData() {
		return this.updateData.isSelected() && this.isVisible();
	}

	public int getSelectedCarId() {
		if(((String)tableModel.getValueAt(0,  1)).length() > 0)
			return Integer.parseInt((String)tableModel.getValueAt(0,  1));
		return 0;
	}
}
