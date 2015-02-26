package simulator.outputter;

public class Outputter {
	
	private static OutputterInterface oi; 
	
	//singleton
	public static OutputterInterface getOutputter() {
		if(oi == null) {
			oi = SQLiteOutputter.getOutputter();
		}
		return oi;
	}
}
