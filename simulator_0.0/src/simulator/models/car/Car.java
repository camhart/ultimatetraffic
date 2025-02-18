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
	private double stop_position;
	private VelocityMap map;
	private Command command;
	private boolean stopGiven; 
	
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
		stop_position = 0;
		map = VelocityMap.getInstance();
	}
	
	/*
	 * I imagine the simulator doing something like this:
	 * If a car does not have a command, give it a command,
	 * else update all the cars.
	 * If a car has a command go then call car.go();
	 * If a car has the change speed command then call car.changeSpeed();
	 * If a car has the stop command then call car.stop();
	 */
	
	/**
	 * Call this to give a change speed command
	 * @param command 
	 */
	public void giveChangeSpeedCommand(double target, Command command){
		this.command  = command;
		target_velocity = target;
		//calculateEnergyUsed(velocity, target_velocity);
//		changeSpeed(); //should this be commented out?
	}
	/**
	 * Call this when updating each iteration after a command has been issued.
	 * Position, velocity, and acceleration get updated.
	 */
	public void changeSpeed(){
		double error = target_velocity - velocity;
		if(Math.abs(error) < 0.5){
			error = 0;
			velocity = target_velocity;
		}
		double control = error * Kp;
		//Update delayed states
		velocity_delayed = velocity;
		acceleration_delayed = acceleration;
		//Calculate the new acceleration based on the control
		if(control == 0) {
			acceleration = 0;
		}
		else {
			acceleration = (control-damping*velocity)/mass;
		}
		//Integrate acceleration to get velocity and position
		velocity = velocity + step / 2 * (acceleration + acceleration_delayed);
		if(velocity < 0) {
			velocity = 0;
		}
		position = position + step / 2 * (velocity + velocity_delayed);
		this.energy_used += Math.abs(step / 2 * (velocity + velocity_delayed) * control);
		assert velocity >= 0 : "we don't go in reverse... target_velocity = " + this.target_velocity + " acceleration=" + acceleration ;
	}
	
	/**
	 * Give the command to go the speed limit
	 */
	public void giveGoCommand(){
		command = Command.GO;
		target_velocity = speed_limit;
		//calculateEnergyUsed(velocity, target_velocity);
//		changeSpeed();
	}
	
	/**
	 * Call this to update after the go command has been issued
	 */
	private void go(){
		changeSpeed();
	}
	
	/**
	 * Call this to give a stop command
	 */
	public void giveStopCommand(double distance){
		this.command = Command.STOP;
		stop_position = this.position + distance;
		//target_velocity = speed_limit;
		speed_before_stop = velocity;
		this.stopGiven = false;
		//stop();
	}
	
	/**
	 * Call this when updating each iteration after a command has been issued
	 */
	private void stop(){
		Pair stop_info = map.getAccelerationInfo(new Pair(roundUp(velocity), 0));
		if(roundDown(stop_info.getSecond()) >= roundDown(stop_position) - roundUp(position) && !this.stopGiven){
			//calculateEnergyUsed(speed_before_stop, velocity);
			giveChangeSpeedCommand(0, Command.STOP);
			this.stopGiven = true;
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
		Pair inputPair = new Pair(roundDown(velocity), roundUp(newSpeed));
		Pair info = map.getAccelerationInfo(inputPair);
		double time = info.getFirst();//Time to accelerate to newSpeed
		//(distanceToLight - distance to accelerate) / the current speed 
		// = time to go remaining distance to light
		time += (distanceToLight - info.getSecond())/newSpeed;
		return time;
	}

	public void setPosition(double position) {
		this.position = position;
		
	}
	
	public Command getCommand(){
		return command;
	}
	
	/**
	 * Use this to move cars forward
	 * @return
	 */
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
			throw new Error("INVALID CAR COMMAND GIVEN");
			//break;
		}
		return this.position;
	}

	@Override
	public String toString() {
		return "Car [position=" + position + ", velocity=" + velocity
				+ ", acceleration=" + acceleration + ", speed_limit="
				+ speed_limit + ", energy_used=" + energy_used
				+ ", target_velocity=" + target_velocity + ", stop_position="
				+ stop_position + ", command=" + command + ", stopGiven="
				+ stopGiven + "]";
	}

	public double getStopPosition() {
		return stop_position;
	}

	public double getTargetVelocity() {
		return this.target_velocity;
	}
	
	
}

