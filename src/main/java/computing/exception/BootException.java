package computing.exception;

public class BootException extends RuntimeException {
	private static final long serialVersionUID = 7009433345399753891L;

	public BootException(String message) {
		super(message);
	}

	public BootException(String message, Throwable cause) {
		super(message, cause);
	}
}
