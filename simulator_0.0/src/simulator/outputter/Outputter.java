package simulator.outputter;

public class Outputter {
	
	private static OutputterInterface oi; 
	
	//singleton
	/**
	 * Retrieve a singleton object used to write
	 * output from the simulator.
	 * @return
	 */
	public static OutputterInterface getOutputter() {
		if(oi == null) {
			oi = SQLiteOutputter.getOutputter();
		}
		return oi;
	}
}
