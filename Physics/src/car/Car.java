package car;

public class Car {
	
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
	
	public Car(double initial_velocity, double time_step) {
		position = 0;
		velocity = initial_velocity;
		velocity_delayed = 0;
		acceleration = 0;
		acceleration_delayed = 0;
		Kp = 600;
		mass = 1270;
		damping = 10;
		step = time_step;
		speed_limit = 22;
		energy_used = 0;
		target_velocity = velocity;
		stop_distance = 0;
		map = new VelocityMap("table.txt");
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
		target_velocity = target;
		calculateEnergyUsed(velocity, target_velocity);
		changeSpeed();
	}
	/*
	 * Call this when updating each iteration after a command has been issued
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
		stop_distance = distance;
		target_velocity = speed_limit;
		speed_before_stop = velocity;
		stop();
	}
	/*
	 * Call this when updating each iteration after a command has been issued
	 */
	public void stop(){
		Pair stop_info = map.getAccelerationInfo(new Pair(Math.round(this.velocity-0.5)+0.5, 0));
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
}
