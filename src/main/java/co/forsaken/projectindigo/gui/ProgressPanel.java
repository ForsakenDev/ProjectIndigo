package co.forsaken.projectindigo.gui;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.gui.components.Box;
import co.forsaken.projectindigo.gui.components.ProgressBar;

@SuppressWarnings("serial") public class ProgressPanel extends BasePanel {

  private ProgressBar _progressBar;
  private Box         _progressBox;

  public ProgressPanel(MainPanel mainPanel) {
    super(mainPanel, -1);
  }

  public void initComponents() {
    _progressBox = new Box(MainPanel.BORDER_COLOUR);
    _progressBox.setBounds((getWidth() - 600) / 2, (getHeight() - 75) / 2, 600, 75);
    add(_progressBox);
    _progressBar = new ProgressBar(0.75F);
    _progressBar.setFont(IndigoLauncher.getMinecraftFont(20));
    _progressBar.setBounds((getWidth() - 570) / 2, (getHeight() - 45) / 2, 570, 45);
    add(_progressBar, 0);
  }

  private long   _progressChangedSecond = System.currentTimeMillis();
  private int    numDots                = 1;
  private String dots                   = "";

  public void stateChanged(final String status, final float progress) {
    int intProgress = Math.round(progress);
    _progressBar.setValue(intProgress);
    String text = status;
    if (System.currentTimeMillis() - _progressChangedSecond > 1000) {
      dots = "";
      numDots++;
      for (int i = 0; i < (numDots % 4); i++) {
        dots += ".";
      }
      _progressChangedSecond = System.currentTimeMillis();
      if (text.length() > 60) {
        text = text.substring(0, 60);
      }
      _progressBar.setString(text + dots);
    }

  }
}
