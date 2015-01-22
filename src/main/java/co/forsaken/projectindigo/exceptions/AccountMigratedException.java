package co.forsaken.projectindigo.exceptions;

public class AccountMigratedException extends BadLoginException {
    private static final long serialVersionUID = -2114049508501320797L;
    private final Throwable   cause;
    private final String      message;

    public AccountMigratedException(String message) {
        this(null, message);
    }

    public AccountMigratedException(Throwable throwable, String message) {
        this.cause = null;
        this.message = message;
    }

    public AccountMigratedException() {
        this(null, "Account migrated, please use your email address instead.");
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String getMessage() {
        return this.message;
    }
}
