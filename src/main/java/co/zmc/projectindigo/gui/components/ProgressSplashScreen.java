package co.zmc.projectindigo.gui.components;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class ProgressSplashScreen extends SplashScreen {
    private ProgressBar _progressBar = new ProgressBar(0.7F);

    public ProgressSplashScreen(String reason, int initialValue) {
        super();
        _progressBar.setFont(IndigoLauncher.getMinecraftFont(14));
        _progressBar.setMaximum(100);
        _progressBar.setBounds(_icon.getIconWidth() / 2 - (370 / 2), (_icon.getIconHeight() / 2) - (29 / 2), 370, 29);
        updateProgress(reason, initialValue);
        _progressBar.setVisible(true);
        getContentPane().add(_progressBar);
    }

    public void updateProgress(String reason, int percent) {
        _progressBar.updateProgress(reason, percent);
    }

    public void updateProgress(int percent) {
        _progressBar.updateProgress(percent);
    }
}
