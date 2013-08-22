package co.zmc.gui;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import co.zmc.gui.components.Image;
import co.zmc.gui.components.SidePanel;
import co.zmc.gui.pages.AccountPage;
import co.zmc.gui.pages.BasePage;
import co.zmc.gui.pages.MainPage;
import co.zmc.gui.pages.ServersPage;
import co.zmc.gui.pages.SettingsPage;

@SuppressWarnings("serial")
public class MainFrame extends BaseFrame {
    protected static List<BasePage> pages          = new ArrayList<BasePage>();
    protected int                   _currentPageId = 0;
    protected SidePanel             sidePanel;

    public MainFrame() {
        super(900, 580);
    }

    @Override
    public void setupLook() {
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        sidePanel = new SidePanel(this);
        sidePanel.setBounds(0, 0, sidePanel.getWidth(), sidePanel.getHeight());
        sidePanel.setVisible(true);

        if (pages == null) {
            pages = new ArrayList<BasePage>();
        }

        pages.add(new MainPage(this));
        pages.add(new ServersPage(this));
        pages.add(new SettingsPage(this));
        pages.add(new AccountPage(this));

        sidePanel.reload(this);
        contentPane.add(sidePanel);

        for (BasePage page : pages) {
            contentPane.add(page);
        }

        contentPane.add(new Image("bg.jpg", getWidth(), getHeight()));
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public List<BasePage> getPages() {
        return pages;
    }

    public int getCurrentPageId() {
        return _currentPageId;
    }

    public void setCurrentPageId(int currentPageId) {
        pages.get(_currentPageId).setVisible(false);
        pages.get(currentPageId).setVisible(true);
        _currentPageId = currentPageId;
    }

}
