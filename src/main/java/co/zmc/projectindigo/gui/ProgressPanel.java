package co.zmc.projectindigo.gui;

import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.components.Box;
import co.zmc.projectindigo.gui.components.ProgressBar;

@SuppressWarnings("serial")
public class ProgressPanel extends BasePanel {

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

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int intProgress = Math.round(progress);
                _progressBar.setValue(intProgress);
                String text = status;
                if (text.length() > 60) {
                    text = text.substring(0, 60) + "...";
                }
                _progressBar.setString(intProgress + "% " + text);
            }
        });
    }
}
