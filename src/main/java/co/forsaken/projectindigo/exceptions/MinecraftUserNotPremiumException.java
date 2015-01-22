package co.forsaken.projectindigo.exceptions;

public class MinecraftUserNotPremiumException extends Exception {
	private static final long serialVersionUID = 23047620275026750L;
	private final Throwable cause;
	private final String message;

	public MinecraftUserNotPremiumException(String message) {
		this(null, message);
	}

	public MinecraftUserNotPremiumException(Throwable throwable, String message) {
		this.cause = null;
		this.message = message;
	}

	public MinecraftUserNotPremiumException() {
		this(null, "User not premium");
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return this.message;
	}
}
