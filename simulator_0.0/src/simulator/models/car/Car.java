package simulator.models.car;

public class Car {
	
	public static double KP = 600;
	public static double MASS = 1270;
	public static double DAMPING = 10;
	public enum Command {STOP, GO, CHANGE_SPEED};
	
	private double position;
	private double velocity;
	private double velocity_delayed;
	private double acceleration;
	private double acceleration_delayed;
	private double Kp;
	private double mass;
	private double damping;
	private double step;
	private double speed_limit;
	private double energy_used;
	private double target_velocity;
	private double speed_before_stop;
	private double stop_distance;
	private VelocityMap map;
	private Command command; 
	
	public Car(double initialVelocity, double timeStep, double initialPosition) {
		position = initialPosition;
		velocity = initialVelocity;
		velocity_delayed = 0;
		acceleration = 0;
		acceleration_delayed = 0;
		Kp = Car.KP;
		mass = Car.MASS;
		damping = Car.DAMPING;
		step = timeStep;
		command = Command.GO;
		speed_limit = 22;
		energy_used = 0;
		target_velocity = velocity;
		stop_distance = 0;
		map = new VelocityMap("config/carTable.txt");
	}
	
	/*
	 * I imagine the simulator doing something like this:
	 * If a car does not have a command, give it a command,
	 * else update all the cars.
	 * If a car has a command go then call car.go();
	 * If a car has the change speed command then call car.changeSpeed();
	 * If a car has the stop command then call car.stop();
	 */
	
	/*
	 * Call this to give a change speed command
	 */
	public void giveChangeSpeedCommand(double target){
		command  = Command.CHANGE_SPEED;
		target_velocity = target;
		calculateEnergyUsed(velocity, target_velocity);
		//changeSpeed();
	}
	/**
	 * Call this when updating each iteration after a command has been issued.
	 * Position, velocity, and acceleration get updated.
	 */
	public void changeSpeed(){
		double error = target_velocity - velocity;
		double control = error * Kp;
		//Update delayed states
		velocity_delayed = velocity;
		acceleration_delayed = acceleration;
		//Calculate the new acceleration based on the control
		acceleration = (control-damping*velocity)/mass;
		//Integrate acceleration to get velocity and position
		velocity = velocity + step / 2 * (acceleration + acceleration_delayed);
		position = position + step / 2 * (velocity + velocity_delayed);
	}
	/*
	 * Give the command to go the speed limit
	 */
	public void giveGoCommand(){
		command = Command.GO;
		target_velocity = speed_limit;
		calculateEnergyUsed(velocity, target_velocity);
		changeSpeed();
	}
	/*
	 * Call this to update after the go command has been issued
	 */
	public void go(){
		changeSpeed();
	}
	/*
	 * Call this to give a stop command
	 */
	public void giveStopCommand(double distance){
		command = Command.STOP;
		stop_distance = distance;
		target_velocity = speed_limit;
		speed_before_stop = velocity;
		stop();
	}
	/*
	 * Call this when updating each iteration after a command has been issued
	 */
	public void stop(){
		Pair stop_info = map.getAccelerationInfo(new Pair(roundUp(velocity), 0));
		if(Math.round(stop_info.getSecond()) >= Math.round(stop_distance) - Math.round(position)){
			calculateEnergyUsed(speed_before_stop, velocity);
			giveChangeSpeedCommand(0);
		}
		else
			changeSpeed();
	}
	private void calculateEnergyUsed(double initial_velocity, double final_velocity){
		energy_used += Math.abs(0.5*mass*Math.pow(initial_velocity, 2) - 0.5*mass*Math.pow(final_velocity, 2));
	}
	public double getPosition(){
		return this.position;
	}
	public double getVelocity(){
		return this.velocity;
	}
	public double getAcceleration(){
		return this.acceleration;
	}
	public double getEnergyUsed(){
		return this.energy_used;
	}
	public double roundUp(double number){
		double result = Math.floor(number);
		double decimal = number - result;
		if(decimal > 0.5)
			result += 1.0;
		else
			result += 0.5;
		return result;
	}
	public double roundDown(double number){
		double result = Math.floor(number);
		double decimal = number - result;
		if(decimal > 0.5)
			result += 0.5;
		return result;
	}
	public double getTimeTo(double newSpeed, double distanceToLight) {
		Pair info = map.getAccelerationInfo(new Pair(roundDown(velocity), newSpeed));
		double time = info.getSecond();//Time to accelerate to newSpeed
		//(distanceToLight - distance to accelerate) / the current speed 
		// = time to go remaining distance to light
		time += (distanceToLight - info.getFirst())/newSpeed;
		return time;
	}

	public void setPosition(double position) {
		this.position = position;
		
	}
	public Command getCommand(){
		return command;
	}
	public double moveCarForward(){
		switch(command){
		case GO:
			go();
			break;
		case STOP:
			stop();
			break;
		case CHANGE_SPEED:
			changeSpeed();
			break;
		default:
			throw new RuntimeException("INVALID CAR COMMAND GIVEN");
			//break;
		}
		return this.position;
	}
}

