package co.forsaken.projectindigo.gui.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import co.forsaken.projectindigo.data.log.Logger;
import co.forsaken.projectindigo.gui.SettingsPanel;
import co.forsaken.projectindigo.utils.Settings;

@SuppressWarnings("serial") public class DirectorySelector extends JFrame implements ActionListener {

  private SettingsPanel _settings;
  private String        _choosertitle = "Please select an installation location";
  JFileChooser          _chooser      = new JFileChooser();

  public DirectorySelector(SettingsPanel settings) {
    super();
    _settings = settings;

    setPreferredSize(getPreferredSize());
    setSize(getPreferredSize());
    setLocationRelativeTo(_settings);
  }

  public void actionPerformed(ActionEvent e) {
    setPreferredSize(getPreferredSize());
    setSize(getPreferredSize());
    setLocationRelativeTo(_settings);
    _chooser.setCurrentDirectory(new File(Settings.getToken().installPath));
    _chooser.setDialogTitle(_choosertitle);
    _chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    _chooser.setAcceptAllFileFilterUsed(false);
    if (_chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      _settings.setInstallFolderText(getChooserDir());
    } else {
      Logger.logWarn("No Selection.");
    }
  }

  public String getChooserDir() {
    String path = _chooser.getSelectedFile().getAbsolutePath();
    if (!path.endsWith(File.separator)) {
      path += File.separator;
    }
    return path;
  }

  public Dimension getPreferredSize() {
    return new Dimension(200, 200);
  }
}