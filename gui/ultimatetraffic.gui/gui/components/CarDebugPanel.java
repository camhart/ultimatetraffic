package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.components.CanvasPanel.Car;
import gui.data.CarData;

public class CarDebugPanel extends JPanel {
	private JTable table;
	
	private DefaultTableModel tableModel;
	
	public CarDebugPanel() {
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		tableModel = new DefaultTableModel(new Object[][]{
				{"Id", ""},
				{"Position", ""},
				{"Velocity", ""},
				{"Acceleration", ""},
				{"Lane", ""},
				{"LightId", ""}
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
		this.setVisible(true);
		this.setPreferredSize(new Dimension(200, 0));
	}
}
