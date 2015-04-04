package simulator.models.stoplights;

import java.util.Iterator;

import simulator.models.CarManager;
import simulator.models.stoplights.StopLight.Color;
import simulator.outputter.Outputter;
import simulator.phases.PhaseHandler;

public class Phase1StopLight extends StopLight {

	public Phase1StopLight(String configString) {
		super(configString);
	}
	
	@Override
	public void handleLightColors(double timePassed, PhaseHandler phase) {
		timeUntilColorChange-=timePassed;
		if(timeUntilColorChange < 0) {
			if(this.currentColor == Color.GREEN) {
				//this.currentColor = Color.RED;
				//this.timeUntilColorChange = this.timeAsRed;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
			} else {
				//this.currentColor = Color.GREEN;
				//this.timeUntilColorChange = this.timeAsGreen;
				setTimeUntilColorChange();
				Outputter.getOutputter().addLightOutput(this);
				
				//Call the algorithm on current cars to catch rounding errors on cars approaching the newly green light				
				this.CallIntermediateAlgorithmOnAllCars(phase); //TODO: Make this actually work! Currently broken because of faulty lane changes between lights

			}
		}
	}
	
	public boolean isLightGreenAtTime(double time) {
		boolean willBeGreen = false;
		if(this.currentColor == Color.GREEN){
			willBeGreen = true;
		}
		double newTimeUntilChange = timeUntilColorChange;
		while(time > newTimeUntilChange){
			time -= newTimeUntilChange;
			if(willBeGreen) {	//switch to red count
				newTimeUntilChange = timeAsRed;
			}
			else{	//switch to green count
				newTimeUntilChange = timeAsGreen;
			}
			willBeGreen = !willBeGreen;	//change future light state
		}
		return willBeGreen;
	}
	
	public void CallIntermediateAlgorithmOnAllCars(PhaseHandler phase) {
		Iterator<CarManager> lane1Iter = lane1.getIterable();
		Iterator<CarManager> lane2Iter = lane2.getIterable();
		CarManager lane1Car = null;
		CarManager lane2Car = null;

		while (lane1Iter.hasNext() || lane2Iter.hasNext()) {

			// if(lane1Iter.hasNext()) {
			// lane1Car = lane1Iter.next();
			// phase.intermediateAlgorithm(lane1Car, this);
			// }
			//
			// if(lane2Iter.hasNext()) {
			// lane2Car = lane2Iter.next();
			// phase.intermediateAlgorithm(lane2Car, this);
			// }

			if (lane1Iter.hasNext()) {
				lane1Car = lane1Iter.next();
			}
			if (lane2Iter.hasNext()) {
				lane2Car = lane2Iter.next();
			}
			double car1Position = 0;
			double car2Position = 0;
			if (lane1Car != null && lane2Car != null) {// there are cars in both
														// lanes
				car1Position = lane1Car.getPosition();
				car2Position = lane2Car.getPosition();
				while (car1Position > car2Position) {
					phase.intermediateAlgorithm(lane1Car, this);
					if (lane1Iter.hasNext()) {
						lane1Car = lane1Iter.next();
						car1Position = lane1Car.getPosition();
					} else {
						lane1Car = null;
						car1Position = -1;
					}
				}
				while (car2Position > car1Position) {
					phase.intermediateAlgorithm(lane2Car, this);
					if (lane2Iter.hasNext()) {
						lane2Car = lane2Iter.next();
						car2Position = lane2Car.getPosition();
					} else {
						lane2Car = null;
						car2Position = -1;
					}
				}
			} else if (lane1Car != null) {// we only have a car in lane 1
				phase.intermediateAlgorithm(lane1Car, this);
				lane1Car = null;
			} else if (lane2Car != null) {// we only have a car in lane 2
				phase.intermediateAlgorithm(lane2Car, this);
				lane2Car = null;
			}

			while (lane1Car != null || lane2Car != null) { // this means we
															// picked up a car
															// in the first 'if'
															// that hasn't had
															// an algorithm call
				if (car1Position > car2Position) {
					phase.intermediateAlgorithm(lane1Car, this);
					car1Position = -1;
					lane1Car = null;
				} else {
					phase.intermediateAlgorithm(lane2Car, this);
					car2Position = -1;
					lane2Car = null;
				}
			}
		}
	}

}
