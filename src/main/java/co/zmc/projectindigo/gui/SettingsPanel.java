package co.zmc.projectindigo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import co.zmc.projectindigo.gui.components.Button;
import co.zmc.projectindigo.gui.components.DirectorySelector;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.RoundedBox;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.Settings;

@SuppressWarnings("serial")
public class SettingsPanel extends BasePanel {

    private RoundedBox _bg;
    private Button     _continueBtn;
    private Settings   _settings;

    public SettingsPanel(MainPanel mainPanel) {
        super(mainPanel, 2);
    }

    private Label   _installDirLbl;
    private TextBox _installDirBox;
    private Button  _installDirBtn;

    public void initComponents() {
        loadSettings();

        _bg = new RoundedBox(MainPanel.BORDER_COLOUR);
        _bg.setBounds((getWidth() - (getWidth() - 50)) / 2, (getHeight() - (getHeight() - 50)) / 2, getWidth() - 50, getHeight() - 50);

        _continueBtn = new Button(this, "Launch");
        _continueBtn.setBounds(_bg.getWidth() - (150 - 10), _bg.getHeight() - (25 - 10), 150, 25);
        _continueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });

        _installDirBox = new TextBox(this, _settings.get(Settings.INSTALL_PATH));
        _installDirBox.setEnabled(false);
        _installDirBox.setBounds(((getWidth() - (getWidth() - 300)) / 2) - (150 / 2), (50 / 2) + 40, (getWidth() - 300), 35);

        _installDirBtn = new Button(this, "...");
        _installDirBtn.addActionListener(new DirectorySelector(SettingsPanel.this));
        _installDirBtn.setBounds(_installDirBox.getX() + _installDirBox.getWidth() + 50, _installDirBox.getY(), 100, _installDirBox.getHeight());

        _installDirLbl = new Label(this, "Install Location: ");
        _installDirLbl.setBounds(_installDirBox.getX(), _installDirBox.getY() - 40, _installDirBox.getWidth(), _installDirBox.getHeight());

        add(_bg);

    }

    public void setInstallFolderText(String path) {
        _installDirBox.setText(path);
        _settings.set(Settings.INSTALL_PATH, path);
        _settings.save();
        DirectoryLocations.updateServerDir();
        ((ServerPanel) _mainPanel.getPanel(1)).getServerManager().getServer(((ServerPanel) _mainPanel.getPanel(1))._selectedServer).updateDir();
    }

    public Settings getSettings() {
        return _settings;
    }

    public void loadSettings() {
        _settings = new Settings();
    }

    public void launch() {
        _settings.save();
        ((ServerPanel) getMainPanel().getPanel(1)).launchServer(_settings);
    }
}
