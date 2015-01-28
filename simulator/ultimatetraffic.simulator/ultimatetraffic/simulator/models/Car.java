package ultimatetraffic.simulator.models;

import ultimatetraffic.simulator.Simulator;

public class Car {
	
	public enum Direction {
		NORTH, SOUTH, EAST, WEST
	}
	
	double position;
	double destination;
	Direction direction;
	double acceleration;
	double velocity;
	double totalAccelerationForce;
	double totalDecelerationForce;
	double totalTime;
	StopLight currentStopLight;
	double arrivalTime;
	int carId;
	
	public void updateCarPosition(double timePassed) {
		//needs implemented
	}

	public boolean reachedDestination() {

		if(direction == Direction.EAST) {
			return position >= destination;
		}
		else if(direction == Direction.WEST) {
			return position <= destination;
		}
		
		assert false : "This code should never run.  Make sure all"
				+ " directions are set properly within cars"
				+ " reachedDestination method";
		
		return false;
	}
	
	public String getCarIntervalString(double timeStamp) {
		return "CAR " + timeStamp + " " + carId + " " + 
				position + " " + direction.name() + "\n";
	}

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getTotalAccelerationForce() {
		return totalAccelerationForce;
	}

	public void setTotalAccelerationForce(double totalAccelerationForce) {
		this.totalAccelerationForce = totalAccelerationForce;
	}

	public double getTotalDecelerationForce() {
		return totalDecelerationForce;
	}

	public void setTotalDecelerationForce(double totalDecelerationForce) {
		this.totalDecelerationForce = totalDecelerationForce;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public StopLight getCurrentStopLight() {
		return currentStopLight;
	}

	public void setCurrentStopLight(StopLight currentStopLight) {
		this.currentStopLight = currentStopLight;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	
}
