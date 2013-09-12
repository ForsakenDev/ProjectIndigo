package co.zmc.projectindigo.gui.page;

import java.awt.Dimension;

import javax.swing.JLayeredPane;

import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.components.Image;

@SuppressWarnings("serial")
public abstract class BasePage extends JLayeredPane {

    protected MainPanel _mainPanel;
    protected Image     backgroundImage;
    protected Image     _icon;

    public BasePage(MainPanel mainPanel, boolean defaultPage) {
        _mainPanel = mainPanel;
        setLayout(null);
        setOpaque(false);
        setVisible(defaultPage);
        Dimension dim = new Dimension(mainPanel.getWidth(), mainPanel.getHeight() - 20);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, getWidth(), getHeight());
        setIcons();
        setupBackgroundImage();
        addComponents(mainPanel);
    }

    public abstract void setIcons();

    public Image getIcon() {
        return _icon;
    }

    public abstract void addComponents(MainPanel mainPanel);

    public abstract void setupBackgroundImage();
}