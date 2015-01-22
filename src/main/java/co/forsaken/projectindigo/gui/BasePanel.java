package co.forsaken.projectindigo.gui;

import javax.swing.JLayeredPane;

import co.forsaken.projectindigo.IndigoLauncher;

@SuppressWarnings("serial") public abstract class BasePanel extends JLayeredPane {

  protected int             _index;
  protected final MainPanel _mainPanel;

  public BasePanel(MainPanel mainPanel, int index) {
    _mainPanel = mainPanel;
    _index = index;
    setLayout(null);
    setOpaque(false);
    setFont(IndigoLauncher.getMinecraftFont(14));
    setSize(mainPanel.getSize());
    setPreferredSize(mainPanel.getSize());
    if (index == -1) {
      setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getSize().width, mainPanel.getSize().height);
    } else {
      setBounds(mainPanel.getSize().width, mainPanel.getY(), mainPanel.getSize().width, mainPanel.getSize().height);
    }
    initComponents();
  }

  public abstract void initComponents();

  public MainPanel getMainPanel() {
    return _mainPanel;
  }

  public void switchPage(int id) {
    _mainPanel.switchPage(id);
  }

  public void onSwitch() {

  }
}
