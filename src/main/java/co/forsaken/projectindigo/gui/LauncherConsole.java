package co.forsaken.projectindigo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.gui.components.Console;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.ResourceUtils;

public class LauncherConsole extends JFrame {

  private static final long serialVersionUID = -3538990021922025818L;
  public Console            console;
  private JScrollPane       scrollPane;
  private JPopupMenu        contextMenu;
  private JMenuItem         copy;

  public LauncherConsole() {
    this.setTitle(IndigoLauncher.TITLE + " Console ");
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setIconImage(ResourceUtils.getImage("icon.png"));
    this.setMinimumSize(new Dimension(600, 400));
    this.setLayout(new BorderLayout());

    console = new Console();
    console.setFont(Font.getFont("SansSerif"));
    console.setForeground(new Color(255, 255, 255));
    console.setSelectionColor(new Color(200, 200, 255));
    console.setBackground(new Color(50, 55, 60));

    setupContextMenu();

    scrollPane = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);
    setVisible(true);
    IndigoLauncher._launcher.requestFocus();
  }

  private void setupContextMenu() {
    contextMenu = new JPopupMenu();

    copy = new JMenuItem("Copy");
    copy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        StringSelection text = new StringSelection(console.getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(text, null);
      }
    });
    contextMenu.add(copy);

    console.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (console.getSelectedText() != null) {
          if (e.getButton() == MouseEvent.BUTTON3) {
            contextMenu.show(console, e.getX(), e.getY());
          }
        }
      }
    });
  }

  public String getLog() {
    return console.getText();
  }

  public void setupLanguage() {
    LogManager.info("Setting up language for console");
    copy.setText("Copy");
    LogManager.info("Finished setting up language for console");
  }

  public void clearConsole() {
    console.setText(null);
  }
}