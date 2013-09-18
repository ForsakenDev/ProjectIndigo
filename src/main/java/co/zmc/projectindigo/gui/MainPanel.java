package co.zmc.projectindigo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.Timer;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.utils.Utils;
import co.zmc.projectindigo.utils.Utils.OS;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

    public static final Color         BORDER_COLOUR    = new Color(45, 45, 45, 130);
    public static final Color         HIGHLIGHT_COLOUR = new Color(13, 86, 166, 200);

    public static final int           PADDING          = 10;
    public static final Dimension     BTN_SIZE         = new Dimension(110, 24);
    private static final int          PLAY_TIME        = 300;

    protected final IndigoLauncher    _launcher;
    protected final Logger            logger           = Logger.getLogger("launcher");

    protected Map<Integer, BasePanel> _panels          = new HashMap<Integer, BasePanel>();

    private int                       _startX;
    private int                       _targetX;
    private int                       _currentPage     = -1;
    private long                      _startTime;
    private int                       _width;
    private int                       _height;

    public MainPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        setLayout(null);
        setOpaque(false);
        if (Utils.getCurrentOS() == OS.MACOSX) {
            height -= 20;
        } else if (Utils.getCurrentOS() == OS.WINDOWS) {
            height -= 25;
        }
        _width = width;
        _height = height;
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(_width, _height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, _width, _height);
        setupLook();
    }

    public BasePanel getPanel(int id) {
        return _panels.get(id);
    }

    public void setupLook() {
        setLayout(null);
        _panels.put(-1, new ProgressPanel(this));
        _panels.put(0, new LoginPanel(this));
        _panels.put(1, new ServerPanel(this));
        _panels.put(2, new SettingsPanel(this));

        for (BasePanel p : _panels.values()) {
            add(p);
        }
        ((LoginPanel) _panels.get(0)).loadUserManager();

        add(new Image("bg", getWidth(), getHeight()));
    }

    public Logger getLogger() {
        return logger;
    }

    private Timer currentTimer = null;

    public void switchPage(final int pageTo) {
        _startX = _panels.get(pageTo).getX();
        _targetX = 0;
        int dist = _targetX - _startX;
        final double accel = 2 * dist * Math.pow(PLAY_TIME, -2);
        if (currentTimer != null) {
            currentTimer.stop();
            currentTimer = null;
        }
        currentTimer = new Timer(15, new ActionListener() {
            double location = _startX;
            double velocity = 0;
            long   lastTime = 0;

            public void actionPerformed(ActionEvent e) {
                if (lastTime == 0) {
                    lastTime = System.currentTimeMillis();
                }
                long deltaTime = lastTime - System.currentTimeMillis();
                velocity += (accel * (double) deltaTime);
                location += (velocity * (double) deltaTime);

                lastTime = System.currentTimeMillis();
                if (!_panels.get(pageTo).isVisible()) {
                    _panels.get(pageTo).setVisible(true);
                }
                _panels.get(pageTo).setLocation((int) location, 0);
                if (pageTo == -1 || (pageTo > _currentPage && _currentPage != -1)) {
                    _panels.get(_currentPage).setLocation((int) (Math.round(location) - _width), 0);
                } else {
                    _panels.get(_currentPage).setLocation(getWidth() + (int) Math.round(location), 0);
                }
                if (System.currentTimeMillis() - _startTime > PLAY_TIME) {
                    _panels.get(pageTo).setLocation(_targetX, 0);

                    if (pageTo == -1 || (pageTo > _currentPage && _currentPage != -1)) {
                        _panels.get(_currentPage).setLocation(0 - _width, 0);
                    } else {
                        _panels.get(_currentPage).setLocation(_width, 0);
                    }
                    _panels.get(_currentPage).setVisible(false);

                    _currentPage = pageTo;
                    currentTimer.stop();
                    currentTimer = null;
                }
            }
        });
        _startTime = System.currentTimeMillis();
        currentTimer.start();
    }
}
