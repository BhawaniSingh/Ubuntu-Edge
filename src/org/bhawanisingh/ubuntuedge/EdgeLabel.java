package org.bhawanisingh.ubuntuedge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class EdgeLabel extends JLabel {
	private static final Color firstColor = new Color(119, 33, 111, 255);
	private static final Color lastColor = new Color(44, 0, 30, 255);
	private static final Color fontColor = new Color(221, 72, 20, 255);
	private static final Font textFont = new Font("Ubuntu", Font.BOLD, 30);

	public EdgeLabel(String text) {
		super(text);
		setHorizontalAlignment(SwingConstants.CENTER);
		setForeground(fontColor);
		setFont(textFont);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	}

	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		GradientPaint paint = new GradientPaint(0, 0, firstColor, 0, height, lastColor, true);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setPaint(paint);
		g2d.fillRoundRect(0, 0, width, height, 2, 2);

		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(lastColor);
		g2d.draw(new RoundRectangle2D.Double(0, 0, width, height, 2, 2));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2d);
	}
}
