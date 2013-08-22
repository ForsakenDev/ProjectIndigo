package co.zmc.gui.pages;

import java.awt.Dimension;

import javax.swing.JLayeredPane;

import co.zmc.gui.BaseFrame;
import co.zmc.gui.components.Image;

@SuppressWarnings("serial")
public abstract class BasePage extends JLayeredPane {

    protected BaseFrame _baseFrame;
    protected Image     backgroundImage;
    protected Image     _icon;

    public BasePage(BaseFrame baseFrame, boolean defaultPage) {
        _baseFrame = baseFrame;
        setLayout(null);
        setOpaque(false);
        setVisible(defaultPage);
        Dimension dim = new Dimension(baseFrame.getWidth(), baseFrame.getHeight() - 20);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, getWidth(), getHeight());
        setIcons();
        setupBackgroundImage();
        addComponents(baseFrame);
    }

    public abstract void setIcons();

    public Image getIcon() {
        return _icon;
    }

    public abstract void addComponents(BaseFrame baseFrame);

    public abstract void setupBackgroundImage();
}
