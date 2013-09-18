package co.zmc.projectindigo.gui.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.gui.SettingsPanel;
import co.zmc.projectindigo.utils.Settings;

@SuppressWarnings("serial")
public class DirectorySelector extends JFrame implements ActionListener {

    private SettingsPanel _settings;
    private String        _choosertitle = "Please select an installation location";
    JFileChooser          _chooser      = new JFileChooser();

    public DirectorySelector(SettingsPanel settings) {
        super();
        _settings = settings;
    }

    public void actionPerformed(ActionEvent e) {
        setPreferredSize(getPreferredSize());
        setSize(getPreferredSize());
        setLocationRelativeTo(_settings);
        _chooser.setCurrentDirectory(new File(_settings.getSettings().get(Settings.INSTALL_PATH)));
        _chooser.setDialogTitle(_choosertitle);
        _chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        _chooser.setAcceptAllFileFilterUsed(false);
        if (_chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Logger.logInfo("getCurrentDirectory(): " + _chooser.getCurrentDirectory());
            Logger.logInfo("getSelectedFile() : " + _chooser.getSelectedFile());
            _settings.setInstallFolderText(_chooser.getSelectedFile().getPath());
        } else {
            Logger.logWarn("No Selection.");
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }
}