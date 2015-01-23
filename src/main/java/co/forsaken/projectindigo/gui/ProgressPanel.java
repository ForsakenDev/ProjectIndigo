package co.forsaken.projectindigo.gui;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.gui.components.Box;
import co.forsaken.projectindigo.gui.components.Label;
import co.forsaken.projectindigo.gui.components.ProgressBar;
import co.forsaken.projectindigo.utils.Utils;

@SuppressWarnings("serial") public class ProgressPanel extends BasePanel {

  private ProgressBar _progressBar;
  private Box         _progressBox;
  private Label       _progressLabel;

  public ProgressPanel(MainPanel mainPanel) {
    super(mainPanel, -1);
  }

  public void initComponents() {
    _progressBox = new Box(MainPanel.BORDER_COLOUR);
    _progressBox.setBounds((getWidth() - 600) / 2, (getHeight() - 75) / 2, 600, 75);
    add(_progressBox);
    _progressLabel = new Label();
    _progressLabel.setBounds((getWidth() - Utils.getLabelWidth(_progressLabel)) / 2, _progressBox.getY() - 50 - 26, 600, 26);
    _progressLabel.setFont(IndigoLauncher.getMinecraftFont(26));
    add(_progressLabel, 0);
    _progressBar = new ProgressBar(0.75F);
    _progressBar.setFont(IndigoLauncher.getMinecraftFont(20));
    _progressBar.setBounds((getWidth() - 570) / 2, (getHeight() - 45) / 2, 570, 45);
    add(_progressBar, 0);
  }

  private long   _progressChangedSecond = System.currentTimeMillis();
  private int    numDots                = 1;
  private String dots                   = "";

  public void stateChanged(String status, String amountComplete, float progress) {
    int intProgress = Math.round(progress);
    _progressBar.setValue(intProgress);
    if (!amountComplete.isEmpty()) _progressBar.setString(amountComplete);
    if (!status.isEmpty()) {
      if (System.currentTimeMillis() - _progressChangedSecond > 1000) {
        dots = "";
        numDots++;
        for (int i = 0; i < (numDots % 4); i++) {
          dots += ".";
        }
        _progressChangedSecond = System.currentTimeMillis();
        if (status.length() > 60) status = status.substring(0, 60);
      }
      _progressLabel.setText(status + dots);
      _progressLabel.setBounds((getWidth() - Utils.getLabelWidth(_progressLabel)) / 2, _progressBox.getY() - 50 - 26, 600, 26);
    }
  }
}
