package co.zmc.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import co.zmc.gui.BaseFrame;
import co.zmc.gui.MainFrame;
import co.zmc.gui.pages.BasePage;

@SuppressWarnings("serial")
public class SidePanel extends JLayeredPane {
    private static final int PLAY_TIME  = 400;

    private TransparentImage background = new TransparentImage("side_bar_bg.png", 0.7F);
    private Image            selector   = new Image("selector.png");
    private List<Image>      pages      = new ArrayList<Image>();
    private JPanel           list       = new JPanel();
    private int              targetY;
    private long             startTime;
    private int              startY     = 0;

    public SidePanel(BaseFrame main) {
        super();
        setup(main);
    }

    private void setup(final BaseFrame baseFrame) {
        setLayout(null);
        Dimension size = new Dimension(selector.getWidth(), baseFrame.getHeight());
        setSize(size);
        setPreferredSize(size);
        setOpaque(false);

        size = new Dimension(background.getWidth(), (int) ((double) background.getHeight() / 1.5));
        list.setOpaque(false);
        list.setSize(size);
        list.setPreferredSize(size);
        list.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                final int clickedId = (int) Math.floor(e.getY() / (list.getHeight() / list.getComponentCount()));
                if (clickedId != ((MainFrame) baseFrame).getCurrentPageId()) {
                    ((MainFrame) baseFrame).setCurrentPageId(clickedId);
                    ((Image) list.getComponent(clickedId)).setEnabled(!((Image) list.getComponent(clickedId)).isEnabled());
                    for (int i = 0; i < list.getComponentCount(); i++) {
                        if (i != clickedId) {
                            list.getComponent(i).setEnabled(false);
                        }
                    }

                    startY = selector.getY();
                    targetY = getNewY(clickedId);
                    Timer timer = new Timer(40, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int y = ((list.getHeight() / list.getComponentCount()) * clickedId);
                            long duration = System.currentTimeMillis() - startTime;
                            float progress = (float) duration / (float) PLAY_TIME;
                            if (progress > 1f) {
                                progress = 1f;
                                startY = selector.getY();
                                ((Timer) (e.getSource())).stop();
                            }

                            y = startY + (int) Math.round((targetY - startY) * progress);

                            selector.setLocation(0, y);
                        }
                    });
                    startTime = System.currentTimeMillis();
                    timer.start();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
        reload(baseFrame);

        add(background, 1);
        add(selector, 0);
        add(list, 0);

    }

    public void reload(BaseFrame baseFrame) {
        pages.clear();
        if (((MainFrame) baseFrame).getPages() != null) {
            for (BasePage p : ((MainFrame) baseFrame).getPages()) {
                pages.add(p.getIcon());
            }
            list.setLayout(new GridLayout(pages.size(), 1, 0, 0));
        } else {
            list.setLayout(new FlowLayout());
        }
        for (int i = 0; i < pages.size(); i++) {
            Image page = pages.get(i);
            page.setEnabled(i == 0);
            page.setHorizontalAlignment(JLabel.CENTER);
            page.setVerticalAlignment(JLabel.CENTER);
            list.add(page);
        }
        int x = getNewY(((MainFrame) baseFrame).getCurrentPageId());
        selector.setLocation(0, x);
    }

    private int getRowSize() {
        return (list.getHeight() / list.getComponentCount());
    }

    private int getNewY(int selectedPageId) {
        if (list.getComponentCount() > 0) {
            return (((getRowSize() / 2) - (selector.getHeight() / 2 + 1)) + list.getComponent(selectedPageId).getY());
        } else {
            return 0;
        }
    }

    public int getActualWidth() {
        return background.getWidth();
    }
}
