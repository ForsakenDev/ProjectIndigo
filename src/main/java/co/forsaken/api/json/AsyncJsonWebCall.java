package co.forsaken.api.json;

import co.forsaken.projectindigo.utils.Callback;

public class AsyncJsonWebCall extends JsonWebCall {

  public AsyncJsonWebCall(String url) {
    super(url);
  }

  public void execute() {
    Thread asyncThread = new Thread(new Runnable() {
      public void run() {
        AsyncJsonWebCall.super.executeRet(null);
      }
    });
    asyncThread.start();
  }

  public void execute(final Object argument) {
    Thread asyncThread = new Thread(new Runnable() {
      public void run() {
        AsyncJsonWebCall.super.execute(argument);
      }
    });
    asyncThread.start();
  }

  public <T> void execute(final Class<T> callbackClass, final Callback<T> callback) {
    Thread asyncThread = new Thread(new Runnable() {
      public void run() {
        AsyncJsonWebCall.super.execute(callbackClass, callback);
      }
    });
    asyncThread.start();
  }

  public <T> void execute(final Class<T> callbackClass, final Callback<T> callback, final Object argument) {
    Thread asyncThread = new Thread(new Runnable() {
      public void run() {
        AsyncJsonWebCall.super.execute(callbackClass, callback, argument);
      }
    });
    asyncThread.start();
  }
}
