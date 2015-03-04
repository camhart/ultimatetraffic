package simulator.models;

public class Car {
	
	public static double KP = 134;
	public static double MASS = 1270;
	public static double DAMPING = 5;
	public static double FMAX_MULTIPLIER = 2.85;
	public static double FMIN_MULTIPLIER = -0.938;
	
	
	
	private double position;
	private double velocity;
	private double velocity_delayed;
	private double acceleration;
	private double acceleration_delayed;
	private double Kp;
	private double mass;
	private double damping;
	private double Fmax;
	private double Fmin;
	private double step;
	
	public Car(double initialVelocity, double timeStep, double initialPosition) {
		position = initialPosition;
		velocity = initialVelocity;
		velocity_delayed = 0;
		acceleration = 0;
		acceleration_delayed = 0;
		Kp = Car.KP;
		mass = Car.MASS;
		damping = Car.DAMPING;
		Fmax = Car.FMAX_MULTIPLIER * mass;
		Fmin = Car.FMIN_MULTIPLIER * mass;
		step = timeStep;
	}
	
	public double command(double command, double time){
		
		double error = command - velocity;
		double control = error * Kp;
		
		if(control > Fmax)
			control = Fmax;
		else if(control < Fmin)
			control = Fmin;
		
		velocity_delayed = velocity;
		acceleration_delayed = acceleration;
		
		acceleration = (control-damping*velocity)/mass;
		
		if(time > 0){
			velocity = velocity + step / 2 * (acceleration + acceleration_delayed);
			position = position + step / 2 * (velocity + velocity_delayed);
		}
		
		return velocity;
	}
	public double getPosition(){
		return position;
	}
	public double getVelocity(){
		return velocity;
	}
	public double getAcceleration(){
		return acceleration;
	}

	public void setPosition(double position) {
		this.position = position;
		
	}
}

