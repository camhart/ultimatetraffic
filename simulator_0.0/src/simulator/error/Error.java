package simulator.error;

public class Error extends RuntimeException {
	public Error(String err) {
		super(err);
	}
	public Error() {
		super();
	}
}
