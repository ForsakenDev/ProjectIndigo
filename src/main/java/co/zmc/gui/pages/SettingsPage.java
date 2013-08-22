package co.zmc.gui.pages;

import co.zmc.gui.BaseFrame;
import co.zmc.gui.components.Image;
import co.zmc.gui.components.TransparentImage;

@SuppressWarnings("serial")
public class SettingsPage extends BasePage {

    public SettingsPage(BaseFrame baseFrame) {
        super(baseFrame, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("/icons/settings_hover.png", "/icons/settings.png");
    }

    @Override
    public void addComponents(BaseFrame frame) {
    }

    @Override
    public void setupBackgroundImage() {
        add(new TransparentImage("main_bg.jpg", 0.75F, getWidth(), getHeight()));
    }

}
