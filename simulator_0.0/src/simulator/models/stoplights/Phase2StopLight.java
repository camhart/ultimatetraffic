package simulator.models.stoplights;

import java.util.ArrayList;

import simulator.outputter.Outputter;

import simulator.phases.PhaseHandler;

public class Phase2StopLight extends Phase1StopLight {

	public Phase2StopLight(String configString) {
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
//				this.CallIntermediateAlgorithmOnAllCars(phase); //TODO: Make this actually work! Currently broken because of faulty lane changes between lights
			}
		}
	}

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
	
	public void addGreenTime(int greens){
		if(this.greenTimesEarned + greens < this.MAX_EARNED_TIME){
			greenTimesEarned += greens;
		}
		else{
			greenTimesEarned = MAX_EARNED_TIME;
		}
	}
	
	public ArrayList<Integer> appendTimes(ArrayList<Integer> a, int reds, int greens, boolean lightStatus){
		if(reds > 0){//there are reds to add
			addGreenTime(reds);
			if(lightStatus){//the last light was green, so we can just append the red and green
				a.add(reds);
				//a.add(greens);
			}
			else{//last light was red, so we need to add the new reds to the last value and append green
				a.set(a.size()-1, a.get(a.size()-1)+reds);
				//a.add(greens);
			}
		}
//		else{//just add green light
//			if(lightStatus){//last light was green, so add to last value
//				a.set(a.size()-1, a.get(a.size()-1)+1);
//			}
//			else{//last light was red, so we can just append the new green
//				a.add(greens);
//			}
//		}
		if(greens > 0){
			if(lightStatus){
				a.set(a.size()-1, a.get(a.size()-1)+greens);
			}
			else{
				a.add(greens);
			}
		}
		return a;
	}
	
	public boolean canLightBeGreenAtTime(double time){
		boolean greenLight = false;
		if(this.currentColor == Color.GREEN)
			greenLight = true;
		int size = lightTimes.size();
		time -= this.timeUntilColorChange;
		//ArrayList<Integer> tempArray = this.lightTimes;//this doesn't actually create anything new... oops.
		//tempArray.set(0, tempArray.get(0)-1);
		if(time > 0){
			int i;
			int timesUsedPerSection = 0;
			for(i=0;i<size && time > 0;i++){ //subtract off planned light timing until planned time is up or time is found
				int timesPlannedPerSection = lightTimes.get(i);
				timesUsedPerSection = 0;
				if(i == 0){
					timesPlannedPerSection--;
					timesUsedPerSection++;
				}
				while(time > 0 && timesPlannedPerSection > 0){
					time -= getTimeChunk(greenLight);
					timesUsedPerSection++;
					timesPlannedPerSection--;
				}
				if(timesPlannedPerSection == 0 && i < size-1 && time > 0){
					greenLight = !greenLight;
				}
				if(time < 0){
					break;
				}
			}
			if(time > 0){ //after subtracting, if we still have unplanned future times for the light, let's add them in
				int redsToAdd = 0;
				while(time > 0){
					if(time - this.timeAsGreen < 0){
						if(this.greenTimesEarned > 0){
							lightTimes = appendTimes(lightTimes, redsToAdd, 1, greenLight);
							//time = -1;
							this.greenTimesEarned--;
							return true;
						}
						else{
							lightTimes = appendTimes(lightTimes, redsToAdd+1,0,greenLight);
							return false;
						}
					}
					else{
						redsToAdd++;
						//addGreenTime();
						time -= this.timeAsRed;
						if(time < 0){//our red interval is bigger than the green interval, so we need multiple greens here
							//reverse what just happened
							time +=this.timeAsRed;
							redsToAdd--;
							//get needed green light times
							int greensNeeded = 1;
							while(time > 0){
								time -= this.timeAsGreen;
								greensNeeded++;
							}
							if(this.greenTimesEarned >= greensNeeded){
								this.greenTimesEarned -= greensNeeded;
								lightTimes = appendTimes(lightTimes, redsToAdd, greensNeeded, greenLight);
								return true;
							}
							else{
								lightTimes = appendTimes(lightTimes, redsToAdd,0,greenLight);
								return false;
							}
						}
					}
				}
				
			}
			else{
				if(!greenLight){
					int greensNeeded = 0;
					while(time < 0){
						time += this.timeAsGreen;
						greensNeeded++;
					}
					if(this.greenTimesEarned > greensNeeded){
						this.greenTimesEarned -= greensNeeded;
						int timeToSplit = this.lightTimes.get(i);
						int timeFirst = timeToSplit - (timesUsedPerSection);
						int timeAfter = timeToSplit - (timeFirst + greensNeeded);
						if(timeFirst > 0){
							lightTimes.set(i,timeFirst);
							if(timeAfter > 0){
								lightTimes.add(i+1, timeAfter);
								lightTimes.add(i+1, greensNeeded);
							}
							else{
								lightTimes.set(i+1, lightTimes.get(i+1) + greensNeeded);
							}
						}
						else{
							if(timeAfter > 0){
								lightTimes.set(i, timeAfter);
							}
							if(i > 0)
								lightTimes.set(i-1, lightTimes.get(i-1) + greensNeeded);
							else{
								lightTimes.add(i, greensNeeded);
								System.out.println("WHAAAAAAAT? THIS SHOULD NEVER HAPPEN!");
							}
						}
//						int timeToSplit = tempArray.get(i);
//						int timeFirst = timeToSplit - (timesUsedPerSection);
//						int timeAfter = timeToSplit - timeFirst;
//						tempArray.set(i,timeFirst);
//						tempArray.add(i+1, timeAfter);
//						tempArray.add(i+1, 1);
//						tempArray.set(0, tempArray.get(0)+1);
//						this.lightTimes = tempArray;
						return true;
					}
					return false;
				}
				else{
					return greenLight; //the light is already planning to be green at that time
				}
			}
		}
		return greenLight;
	}
	
	public double getTimeChunk(boolean greenLight){
		if(greenLight){
			return this.timeAsGreen;
		}
		else{
			return this.timeAsRed;
		}
	}
}
