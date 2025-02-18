package simulator.models.stoplights;

public class Phase1StopLight extends StopLight {

	public Phase1StopLight(String configString) {
		super(configString);
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
	
//	public void callIntermediateAlgorithmOnAllCars(PhaseHandler phase) {
//		
//		Iterator<CarManager> iter = this.iterator();
//		CarManager car = null; 
//		
//		while(iter.hasNext()) {
//			car = iter.next();
//			((Phase1Handler)phase).intermediateAlgorithm(car, this);
//		}
//		
//		
////		Iterator<CarManager> lane1Iter = lane1.getIterable();
////		Iterator<CarManager> lane2Iter = lane2.getIterable();
////		CarManager lane1Car = null;
////		CarManager lane2Car = null;
////
////		while (lane1Iter.hasNext() || lane2Iter.hasNext()) {
////
////			// if(lane1Iter.hasNext()) {
////			// lane1Car = lane1Iter.next();
////			// phase.intermediateAlgorithm(lane1Car, this);
////			// }
////			//
////			// if(lane2Iter.hasNext()) {
////			// lane2Car = lane2Iter.next();
////			// phase.intermediateAlgorithm(lane2Car, this);
////			// }
////
////			if (lane1Iter.hasNext()) {
////				lane1Car = lane1Iter.next();
////			}
////			if (lane2Iter.hasNext()) {
////				lane2Car = lane2Iter.next();
////			}
////			double car1Position = 0;
////			double car2Position = 0;
////			if (lane1Car != null && lane2Car != null) {// there are cars in both
////														// lanes
////				car1Position = lane1Car.getPosition();
////				car2Position = lane2Car.getPosition();
////				while (car1Position > car2Position) {
////					phase.intermediateAlgorithm(lane1Car, this);
////					if (lane1Iter.hasNext()) {
////						lane1Car = lane1Iter.next();
////						car1Position = lane1Car.getPosition();
////					} else {
////						lane1Car = null;
////						car1Position = -1;
////					}
////				}
////				while (car2Position > car1Position) {
////					phase.intermediateAlgorithm(lane2Car, this);
////					if (lane2Iter.hasNext()) {
////						lane2Car = lane2Iter.next();
////						car2Position = lane2Car.getPosition();
////					} else {
////						lane2Car = null;
////						car2Position = -1;
////					}
////				}
////			} else if (lane1Car != null) {// we only have a car in lane 1
////				phase.intermediateAlgorithm(lane1Car, this);
////				lane1Car = null;
////			} else if (lane2Car != null) {// we only have a car in lane 2
////				phase.intermediateAlgorithm(lane2Car, this);
////				lane2Car = null;
////			}
////
////			while (lane1Car != null || lane2Car != null) { // this means we
////															// picked up a car
////															// in the first 'if'
////															// that hasn't had
////															// an algorithm call
////				if (car1Position > car2Position) {
////					phase.intermediateAlgorithm(lane1Car, this);
////					car1Position = -1;
////					lane1Car = null;
////				} else {
////					phase.intermediateAlgorithm(lane2Car, this);
////					car2Position = -1;
////					lane2Car = null;
////				}
////			}
////		}
//	}

}
