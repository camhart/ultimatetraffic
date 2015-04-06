package simulator.phases;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.car.Car;
import simulator.models.car.Car.Command;
import simulator.models.stoplights.StopLight;
import simulator.outputter.Outputter;

public class Phase0Handler extends PhaseHandler {
	
	public static final double YELLOW_LIGHT_TIME_LEFT = 4.0;
	public static final double RUN_YELLOW_LIGHT_DISTANCE = 50.0;
	public static final int PHASE_NUMBER = 0;
	public static final double CAR_SPACING = 3.0;
	
	public static double getTimeUntilCollision(CarManager c1, CarManager c2) {
		double time = 0.0;
		
		if(c2 == null || c1 == null || c2.getTargetVelocity() > c1.getTargetVelocity()) {
			return Double.MAX_VALUE;
		}
		double p1 = c1.getPosition();
		double p2 = c2.getPosition();
		double v1 = c1.getTargetVelocity();
		double v2 = c2.getTargetVelocity();
		
		time = (p1 - p2) / (v2 - v1); //p1 +v1*t = p2 + v2*t -> solve for t
		
		if(!Double.isFinite(time)) {
			return Double.MAX_VALUE;
		}
		
		return time;
	}
	
	public void handleStopCommand(CarManager c, CarManager nextCar, double stopDistance, double timeUntilCollision) {
		if(timeUntilCollision < Phase0Handler.CAR_SPACING) {
			//even though it's handleGoCommand, it should take care of slowing down for the car in front of us...
			double speed = Math.min(Math.max(nextCar.getTargetVelocity()-1.0, 0),Math.max(c.getVelocity()-1.0,  0));
			double stopPosition = nextCar.getPosition() - c.getPosition() - CarManager.CAR_STOP_CUSHION;
			stopPosition = Math.max(stopPosition, 0);
			
//			assert stopPosition < stopDistance : "shouldn't be happening...?";
			c.resetAccelerationDelay();
			if(speed < 1.0) {
//				c.resetAccelerationDelay();
				c.giveDelayedChangeSpeedCommand(0, Command.CHANGE_SPEED);
			} else
				c.giveDelayedChangeSpeedCommand(speed, Command.CHANGE_SPEED);
		} else {
			c.giveStopCommand(stopDistance);
		}
	}
	
	public void handleGoCommand(CarManager c, CarManager nextCar, double timeUntilCollision) {
		if(timeUntilCollision < Phase0Handler.CAR_SPACING) {

			double speed = Math.min(Math.max(nextCar.getTargetVelocity()-1.0, 0),Math.max(c.getVelocity()-1.0,  0));
			double stopPosition = nextCar.getPosition() - c.getPosition() - CarManager.CAR_STOP_CUSHION;
			stopPosition = Math.max(stopPosition, 0);
			c.resetAccelerationDelay();
			if(speed < 1.0) {
				
				c.giveDelayedChangeSpeedCommand(0, Command.CHANGE_SPEED);
			}
			else
				c.giveDelayedChangeSpeedCommand(speed, Command.CHANGE_SPEED);
		} else {
			c.giveGoCommand();
		}
	}
	
	public boolean carMoving(CarManager c) {
		return c.getCommand() == Command.GO || (c.getCommand() == Command.CHANGE_SPEED && c.getVelocity() > 0.0);
	}
	
	@Override
	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverything(
			CarManager car, StopLight light) {

		car.moveCarForward(); // move car
		if (light.getCurrentColor() == StopLight.Color.RED && car.getCommand() != Car.Command.STOP) {
			car.giveStopCommand(car.getStopDistance(light));
		}
		if (light.getCurrentColor() == StopLight.Color.GREEN
				&& light.getTimeUntilChange() <= Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
			// the light is green and about to change red (yellow). Tell car to
			// stop.

			if (car.getCommand() == Car.Command.GO
					&& car.getPosition() < (light.getPosition() - RUN_YELLOW_LIGHT_DISTANCE)
					&& car.canRunLight(light)) {
				// stop
				car.giveStopCommand(car.getStopDistance(light));
			}
			// else run light

		} else if (car.getCommand() == Car.Command.STOP
				&& light.getCurrentColor() == StopLight.Color.GREEN) {

			CarManager nextCar = car.getLaneObject().getNextCar(car);
			if (nextCar != null) {
				if (nextCar.getCommand() == Car.Command.GO) {
					// creates a slight delay between car's being given the go
					// command
					car.giveGoCommand();
				}
				// else wait
			} else {
				car.giveGoCommand();
			}
			// go!
		}

		Outputter.getOutputter().addCarOutput(car);
	}
	
//	
////	@Override
//	public void handleEverythingWithCarsAndStoppingAndGoingAndTargetSpeedAndEverythingbad(
//			CarManager car, StopLight light) {
//
//		car.moveCarForward(); // move car
//		
//		double distanceToNextCar = car.getLaneObject().getDistanceToNextCarFrom(car.getPosition());
//		double distanceToLight = light.getPosition() - car.getPosition();
////		double timeUntilHitCar = distanceToNextCar / car.getVelocity();
//		CarManager nextCar = car.getLaneObject().getNextCar(car);
//		double timeUntilHitCar = getTimeUntilCollision(car, nextCar);
//
//		
//		if(car.getId() == 2 && Simulator.getSimulator().getCurrentIteration() == 417) {
//			System.out.println("A");
//		}
//		
////		red light
//		if(light.getCurrentColor() == StopLight.Color.RED) {
//			if(car.getPosition() < light.getPosition()) {		
//				double stopDistance = car.getStopDistance(light);
//				
//				if(carMoving(car)) {
//					handleStopCommand(car, nextCar, stopDistance, timeUntilHitCar);
////					car.giveStopCommand(stopDistance);
//				} else if(!carMoving(car)) {
//					car.resetAccelerationDelay();
//					if(car.getStopPosition() < stopDistance) {
////						car.giveChangeSpeedCommand(1.0, Command.CHANGE_SPEED);
////						car.giveStopCommand(stopDistance);
//						handleStopCommand(car, nextCar, stopDistance, timeUntilHitCar);
//					}
////					if(timeUntilHitCar < this.CAR_SPACING) {
////						car.giveStopCommand(distanceToNextCar);
////					}
//				}
//			}
//		}
////		yellow light
//		else if(light.getCurrentColor() == StopLight.Color.GREEN && light.getTimeUntilChange() < Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
//			
//			double timeUntilCrossedLight = (light.getPosition() - car.getPosition()) / car.getVelocity();
//			
////			if ((carMoving(car) && car.getPosition() < (light.getPosition() - RUN_YELLOW_LIGHT_DISTANCE))) {
//			if (carMoving(car) && timeUntilCrossedLight < Phase0Handler.YELLOW_LIGHT_TIME_LEFT - 1.0) {
//				// 	stop
////				car.giveStopCommand(car.getStopDistance(light));
//				handleStopCommand(car, nextCar, car.getStopDistance(light), timeUntilHitCar);
//			} else if(car.getPosition() < light.getPosition() && !car.canRunLight(light)) {
////				car.giveStopCommand(car.getStopDistance(light));
//				handleStopCommand(car, nextCar, car.getStopDistance(light), timeUntilHitCar);
//			}
//			else { // if(car.getCommand() == Command.CHANGE_SPEED) {
//				handleGoCommand(car, nextCar, timeUntilHitCar);
//			}
//			// 	else run light
//		}
////		green light
//		else if(light.getCurrentColor() == StopLight.Color.GREEN) {
//			if(!carMoving(car)) {
//				if(timeUntilHitCar > this.CAR_SPACING) {
//					handleGoCommand(car, nextCar, timeUntilHitCar);
//				}
//			}
//		}
//		
//		//other
//		else if(timeUntilHitCar < CAR_SPACING) {
//			this.handleGoCommand(car, nextCar, timeUntilHitCar);
//		} 
//		
//		else if(car.getCommand() == Command.CHANGE_SPEED) {
//			this.handleGoCommand(car, nextCar, timeUntilHitCar);
//		}
//		
//		
//		
////		//red light
////		if (light.getCurrentColor() == StopLight.Color.RED && car.getCommand() != Car.Command.STOP) {
////			car.giveStopCommand(stopDistance);
////		}
////		//yellow light
////		else if (light.getCurrentColor() == StopLight.Color.GREEN
////				&& light.getTimeUntilChange() <= Phase0Handler.YELLOW_LIGHT_TIME_LEFT) {
////			// the light is green and about to change red (yellow). Tell car to
////			// stop.
////
////			if (car.getCommand() == Car.Command.GO
////					&& car.getPosition() < (light.getPosition() - RUN_YELLOW_LIGHT_DISTANCE)
////					&& car.canRunLight(light)) {
////				// stop
////				car.giveStopCommand(car.getStopDistance(light));
////			}
////			// else run light
////
////		//stopped at green light
////		} else if (car.getCommand() == Car.Command.STOP
////				&& light.getCurrentColor() == StopLight.Color.GREEN) {
////
////			CarManager nextCar = car.getLaneObject().getNextCar(car);
////			if (nextCar != null) {
////				if (nextCar.getCommand() == Car.Command.GO) {
////					// creates a slight delay between car's being given the go
////					// command
////					if(timeUntilHitCar > CAR_SPACING)
////						car.giveGoCommand();
////				}
////				// else wait
////			} else {
////				car.giveGoCommand();
////			}
////			// go!
////		} else if(timeUntilHitCar < CAR_SPACING) {
////			car.giveStopCommand(distanceToNextCar);
////		}
//
//		Outputter.getOutputter().addCarOutput(car);
//	}
	
	public void intermediateAlgorithm(CarManager car, StopLight light){
		throw new Error("Phase0 is trying to call the algorithm!");
	}

	@Override
	public StopLight buildStopLight(String configString) {
		return new StopLight(configString);
	}
}
