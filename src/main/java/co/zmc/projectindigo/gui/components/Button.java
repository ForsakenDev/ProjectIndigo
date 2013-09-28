package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class Button extends JButton implements MouseListener {
	private boolean isClicked = false;
	private boolean isHovering = false;
	private boolean _isToggle = false;
	private Color hoverColour = Color.LIGHT_GRAY;

	public Button(JComponent frame, String label) {
		this(frame, label, false);
	}

	public Button(JComponent frame, String label, boolean isToggle) {
		this(label);
		_isToggle = isToggle;
		frame.add(this, 0);
	}
	
	public Button(String label) {
		setText(label);
		setBackground(Color.WHITE);
		addMouseListener(this);
		setFont(IndigoLauncher.getMinecraftFont(14));
		setRolloverEnabled(false);
		setBorder(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		Rectangle2D rect = new Rectangle2D.Float(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(!this.isClicked ? getBackground() : Color.BLACK);
		g2d.fill(rect);
		g2d.setColor(hoverColour);
		if (this.isHovering && !this.isClicked)
			g2d.fill(rect);
		g2d.setFont(getFont());
		int width = g2d.getFontMetrics().stringWidth(getText());
		g2d.setColor(this.isClicked ? getBackground() : Color.BLACK);
		g2d.drawString(getText(), (getWidth() - width) / 2, getFont().getSize() + 4);

		g2d.dispose();
	}
	
	public void setHoverColour(Color colour) {
		this.hoverColour = colour;
	}

	public boolean isClicked() {
		return isClicked || !isEnabled();
	}

	public void mouseClicked(MouseEvent e) {
		if (_isToggle) {
			this.isClicked = !this.isClicked;
		}
	}

	public void mousePressed(MouseEvent e) {
		if (!_isToggle) {
			this.isClicked = true;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (!_isToggle) {
			this.isClicked = false;
		}
	}

	public void mouseEntered(MouseEvent e) {
		isHovering = true;
		this.repaint();
	}

	public void mouseExited(MouseEvent e) {
		isHovering = false;
		this.repaint();
	}
}