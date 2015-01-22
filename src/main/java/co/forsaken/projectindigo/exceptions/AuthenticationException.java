package co.forsaken.projectindigo.exceptions;

@SuppressWarnings("serial") public class AuthenticationException extends LauncherException {

  public AuthenticationException(String message, String localizedMessage) {
    super(message, localizedMessage);
  }

  public AuthenticationException(Throwable cause, String localizedMessage) {
    super(cause, localizedMessage);
  }
}