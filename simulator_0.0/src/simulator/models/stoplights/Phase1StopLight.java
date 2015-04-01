package simulator.models.stoplights;

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
//				if(phase.getPhase() > 0){  //this will always be true
//				this.CallIntermediateAlgorithmOnAllCars(phase); //TODO: Make this actually work! Currently broken because of faulty lane changes between lights
//				}
			}
		}
	}

}
