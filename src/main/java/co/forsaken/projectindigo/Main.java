package co.forsaken.projectindigo;

import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.AutoUpdater;

public class Main {

  public Main() {
    main(new String[0]);
  }

  public Main(String defaultUser) {
    main(new String[] { defaultUser });
  }

  public static void main(String[] args) {
    IndigoLauncher.cleanup();
    LogManager.start();
    AutoUpdater.main(args);
  }
}
