package co.forsaken.projectindigo;

import javax.swing.UIManager;
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
    //We have to set the LAF before the first JOptionPane can show up
    setLookAndFeel();
    AutoUpdater.main(args);
  }
  
  private static void setLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e)//I would prefer multicatch, but the compilance level is set to java 1.6
    {
      LogManager.error("Could not change the Look And Feel to the system default, you have to stick with the Metal Look And Feel");
    }
  }
}
