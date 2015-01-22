package co.forsaken.projectindigo.exceptions;

@SuppressWarnings("serial") public class LauncherException extends Exception {

  private final String localizedMessage;

  public LauncherException(String message, String localizedMessage) {
    super(message);
    this.localizedMessage = localizedMessage;
  }

  public LauncherException(Throwable cause, String localizedMessage) {
    super(cause.getMessage(), cause);
    this.localizedMessage = localizedMessage;
  }

  @Override public String getLocalizedMessage() {
    return localizedMessage;
  }
}