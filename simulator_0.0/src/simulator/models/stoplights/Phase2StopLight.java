package simulator.models.stoplights;

public class Phase2StopLight extends Phase1StopLight {

	public Phase2StopLight(String configString) {
		super(configString);
	}
	
	@Override
	protected void setTimeUntilColorChange() {
		lightTimes.set(0, lightTimes.get(0)-1);
		if(lightTimes.get(0) <= 0){//change color
			this.lightTimes.remove(0);
			if(this.lightTimes.size() < 1){ //This means no cars have requested Green in the future, so we leave the light red
				this.lightTimes.add(1);
				this.currentColor = Color.RED;
				this.timeUntilColorChange = this.timeAsRed;
				addGreenTime(1);
			}
			else{
				if(this.currentColor == Color.GREEN){
					this.currentColor = Color.RED;
					this.timeUntilColorChange = this.timeAsRed;
				}
				else{
					this.currentColor = Color.GREEN;
					this.timeUntilColorChange = this.timeAsGreen;
				}
			}
		}
		else{//don't change
			if(this.currentColor == Color.GREEN){
				this.timeUntilColorChange = this.timeAsGreen;
			}
			else{
				this.timeUntilColorChange = this.timeAsRed;
			}
		}
	}
}
