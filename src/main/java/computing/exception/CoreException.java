package computing.exception;

public class CoreException extends RuntimeException {
	private static final long serialVersionUID = 6274724364267540314L;

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}
}
