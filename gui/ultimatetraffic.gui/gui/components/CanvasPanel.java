package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import gui.SimulatorGui;
import gui.data.CarData;
import gui.data.LightData;
import gui.data.StateData;
import gui.listeners.DataListener;

import javax.swing.JPanel;
import javax.swing.JRootPane;

public class CanvasPanel extends JPanel implements DataListener, MouseListener {
	
	public class Car {
		
		public int getY1() {
			return getHeight() / 2 + ((this.lane == 1) ? -CarData.CarHeight * 2 : CarData.CarHeight);
		}
		
		public int getY2() {
			return getHeight() / 2 + ((this.lane == 1) ? -CarData.CarHeight * 2 : CarData.CarHeight) + CarData.CarHeight / 2;
		}
		
		public int getY3() {
			return getHeight() / 2 + ((this.lane == 1) ? -CarData.CarHeight * 2 : CarData.CarHeight) + CarData.CarHeight;
		}
		
		public boolean clicked(int x, int y) {
			return x >= x1 && x <= x2 && y >= y1 && y <= y3;
		}
		
		public Car(CarData cd) {
			this.id = cd.getId();
			this.lane = cd.getLane();
			this.position = cd.getPosition();
			this.velocity = cd.getVelocity();
			this.acceleration = cd.getAcceleration();
			this.lightId = cd.getLightId();
			this.x1 = (int) (this.position - CarData.CarLength / 2);
			this.x2 = (int) (this.position + CarData.CarLength / 2);
			
			this.y1 = getY1();
			this.y2 = getY2();
			this.y3 = getY3();
		}
		
		public Car() {
		}

		int id;
		int lane;
		int x1;
		int x2;
		int y1;
		int y2;
		int y3;
		
		double position;
		double velocity;
		double acceleration;
		int lightId;
	}
	
	class Light {
		
		private double timeUntilChange;
		public Light(LightData ld) {
			this.id = ld.getId();
			this.position = ld.getPosition();
			this.color = ld.getColor();
			this.timeUntilChange = ld.getTimeUntilChange();
		}
		int id;
		double position;
		LightData.Color color;
		
		
		public double getTimeUntilChange() {
			return this.timeUntilChange;
		}
	}
	
	HashMap<Integer, Light> lights;
	ArrayList<Car> cars;

	public CanvasPanel(int width, int height) {
		lights = new HashMap<Integer, Light>();
		cars = new ArrayList<Car>();
		
		this.addMouseListener(this);
		this.setPreferredSize(new Dimension(width, height));
	}
	
	public void clearData() {
		cars = new ArrayList<Car>();
		lights = new HashMap<Integer, Light>();
	}

	@Override
	public void dataChanged(StateData data) {
		
		cars = new ArrayList<Car>();
		
		for(CarData cd : data.getCarData().values()) {
			cars.add(new Car(cd));
		}
		
		data.iterate();
		for(LightData ld : data.getLightData()) {
			lights.put(ld.getId(), new Light(ld));
		}
		
		repaint();
	}	
	
	private void drawCar(Car car, Graphics2D g) {
		Color carColor = car.acceleration > 0.001 ? Color.GREEN : ((car.acceleration < -0.001) ? Color.RED : Color.YELLOW);
		g.setColor(carColor);
		
		g.fillPolygon(new int[]{car.x1, car.x2, car.x1}, new int[]{car.y1, car.y2, car.y3}, 3);
		
		g.setColor(Color.BLACK);
		
		g.drawString(Integer.toString(car.id), (int) car.position, (car.lane == 1) ? car.y1 - (22 - CarData.CarHeight) : car.y2 + 25);
		
	}
	
	private static int lightSize = 10;
	
	private void drawLight(Light l, Graphics2D g) {
		Color lightColor;
		if(l.getTimeUntilChange() < 5.0 && l.color.toString().equals("GREEN")) {
			lightColor = Color.YELLOW;
		} else {
			lightColor = (l.color.toString().equals("GREEN")) ? Color.GREEN : Color.RED;	
		}		
		
		g.setColor(lightColor);
		
		int y = this.getHeight() / 2 + 35;
		
		g.fillOval((int) l.position,  y, lightSize, lightSize);
		
		g.setColor(Color.BLACK);
		
		g.drawString(String.format("Id: %d", l.id), (int) (l.position - 31),  y + 10);
		
		//keep "0.0 +" in there, it prevents it frome showing up as negative zero (-0.0)
		g.drawString(String.format("%.2f s", 0.0 + (l.getTimeUntilChange() - SimulatorGui.getInstance().getState())), (int) ((int) l.position + lightSize * 1.5), y + 10);
		
	}
	
	static int RoadWidth = 40;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		 
		 
		 //draw the road
		 g.setColor(Color.BLACK);		 
		 g.fillRect(0, this.getHeight() / 2 - RoadWidth / 2, this.getWidth(), RoadWidth);
		 
		 for(Car c : this.cars) {
			 this.drawCar(c,  g2);
		 }
		 
		 for(Light l : this.lights.values()) {
			 this.drawLight(l, g2);
		 }	 
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(Car c : cars) {
			if(c.clicked(e.getX(), e.getY())) {
				SimulatorGui.getInstance().getCarDebugPanel().setCar(c);
				break;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
