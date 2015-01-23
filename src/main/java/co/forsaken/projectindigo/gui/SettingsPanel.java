package co.forsaken.projectindigo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import co.forsaken.projectindigo.gui.components.Button;
import co.forsaken.projectindigo.gui.components.DirectorySelector;
import co.forsaken.projectindigo.gui.components.Label;
import co.forsaken.projectindigo.gui.components.RoundedBox;
import co.forsaken.projectindigo.gui.components.TextBox;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.Settings;

@SuppressWarnings("serial") public class SettingsPanel extends BasePanel {

  private RoundedBox _bg;
  private Button     _continueBtn;
  private Button     _backBtn;
  private Settings   _settings;

  private Label      _installDirLbl;
  private TextBox    _installDirBox;
  private Button     _installDirBtn;
  private Label      _maxRamLbl;
  private Label      _maxRamAmtLbl;
  private JSlider    _maxRamSlider;

  private Label      _javaParamsLbl;
  private TextBox    _javaParamsBox;

  public SettingsPanel(MainPanel mainPanel) {
    super(mainPanel, 3);
  }

  public void initComponents() {
    loadSettings();

    _bg = new RoundedBox(MainPanel.BORDER_COLOUR);
    _bg.setBounds((getWidth() - (getWidth() - 50)) / 2, (getHeight() - (getHeight() - 50)) / 2, getWidth() - 50, getHeight() - 50);

    _backBtn = new Button(this, "Back");
    _backBtn.setBounds(_bg.getWidth() - (200 - 10), _bg.getHeight() - (25 - 10), 200, 25);
    _backBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        _mainPanel.switchPage(1);
      }
    });
    _continueBtn = new Button(this, "Save");
    _continueBtn.setBounds(_backBtn.getX() - 200 - 50, _bg.getHeight() - (25 - 10), 200, 25);
    _continueBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        save();
        _mainPanel.switchPage(1);
      }
    });

    _installDirLbl = new Label(this, "Install Location: ");
    _installDirBox = new TextBox(this, Settings.getToken().installPath);
    _installDirBox.setEditable(false);
    _installDirBox.setText(Settings.getToken().installPath);
    _installDirBtn = new Button(this, "...");
    _installDirBtn.addActionListener(new DirectorySelector(SettingsPanel.this));
    _installDirBox.setBounds(((getWidth() - (getWidth() - 300)) / 2) - (150 / 2), (50 / 2) + 50, (getWidth() - 300), 25);
    _installDirBtn.setBounds(_installDirBox.getX() + _installDirBox.getWidth() + 50, _installDirBox.getY(), 100, _installDirBox.getHeight());
    _installDirLbl.setBounds(_installDirBox.getX(), _installDirBox.getY() - 30, _installDirBox.getWidth(), _installDirBox.getHeight());

    _maxRamAmtLbl = new Label(this, Settings.getToken().maxRam);
    long ram = 0;
    OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    Method m;
    try {
      m = operatingSystemMXBean.getClass().getDeclaredMethod("getTotalPhysicalMemorySize");
      m.setAccessible(true);
      Object value = m.invoke(operatingSystemMXBean);
      if (value != null) {
        ram = Long.valueOf(value.toString()) / 1024 / 1024;
      } else {
        LogManager.warn("Could not get RAM Value");
        ram = 4096;
      }
    } catch (Exception e) {
      LogManager.error(e.getMessage());
    }

    _maxRamSlider = new JSlider();

    _maxRamSlider.setSnapToTicks(true);
    _maxRamSlider.setMajorTickSpacing(256);
    _maxRamSlider.setMinorTickSpacing(256);
    _maxRamSlider.setMinimum(256);
    String vmType = System.getProperty("sun.arch.data.model");
    if (vmType != null) {
      if (vmType.equals("64")) {
        _maxRamSlider.setMaximum((int) ram);
      } else if (vmType.equals("32")) {
        if (ram < 1024) {
          _maxRamSlider.setMaximum((int) ram);
        } else {
          _maxRamSlider.setMaximum(1024);
        }
      }
    }
    int ramMax = (Integer.parseInt(Settings.getToken().maxRam) > _maxRamSlider.getMaximum()) ? _maxRamSlider.getMaximum() : Integer.parseInt(Settings.getToken().maxRam);
    _maxRamSlider.setValue(ramMax);
    _maxRamAmtLbl.setText(getAmount());
    _maxRamSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        _maxRamAmtLbl.setText(getAmount());
      }
    });
    add(_maxRamSlider, 0);
    _maxRamLbl = new Label(this, "Max RAM Allocation: ");
    _maxRamSlider.setBounds(((getWidth() - (getWidth() - 300)) / 2) - (150 / 2), _installDirBox.getY() + 50 + 30, (getWidth() - 300), 25);
    _maxRamAmtLbl.setBounds(_maxRamSlider.getX() + _maxRamSlider.getWidth() + 50, _maxRamSlider.getY(), 100, _maxRamSlider.getHeight());
    _maxRamLbl.setBounds(_maxRamSlider.getX(), _maxRamSlider.getY() - 30, _maxRamSlider.getWidth(), _maxRamSlider.getHeight());
    ;

    _javaParamsLbl = new Label(this, "Additional Java Parameters: ");
    _javaParamsBox = new TextBox(this, "");
    _javaParamsBox.setText(Settings.getToken().javaParams);

    _javaParamsBox.setBounds(((getWidth() - (getWidth() - 150)) / 2), _maxRamSlider.getY() + 50 + 30, (getWidth() - 150), 25);
    _javaParamsLbl.setBounds(_javaParamsBox.getX(), _javaParamsBox.getY() - 30, _javaParamsBox.getWidth(), _javaParamsBox.getHeight());

    add(_bg);

  }

  private String getAmount() {
    int ramMax = _maxRamSlider.getValue();
    return (ramMax >= 1024) ? Math.round((ramMax / 256) / 4) + "." + (((ramMax / 256) % 4) * 25) + " GB" : ramMax + " MB";
  }

  public void setInstallFolderText(String path) {
    _installDirBox.setText(path);
  }

  public Settings getSettings() {
    return _settings;
  }

  public void loadSettings() {
    _settings = new Settings();
  }

  public void save() {
    ((ServerBasePanel) getMainPanel().getPanel(1)).updateServersDir();
    Settings.getToken().installPath = _installDirBox.getText();
    Settings.getToken().maxRam = _maxRamSlider.getValue() + "";
    Settings.getToken().javaParams = _javaParamsBox.getText();
    Settings.save();
  }
}
