package simulator.error;

/**
 * Error class used to specify unimplemented methods, etc.
 * @author Cam
 *
 */
public class Error extends RuntimeException {
	
	
	public Error(String err) {
		super(err);
	}
	public Error() {
		super();
	}
}
