package co.zmc.gui.pages;

import co.zmc.gui.BaseFrame;
import co.zmc.gui.components.Image;

@SuppressWarnings("serial")
public class MainPage extends BasePage {

    public MainPage(BaseFrame baseFrame) {
        super(baseFrame, true);
    }

    @Override
    public void setIcons() {
        _icon = new Image("/icons/home_hover.png", "/icons/home.png");
    }

    @Override
    public void addComponents(BaseFrame frame) {
    }

    @Override
    public void setupBackgroundImage() {
    }

}
