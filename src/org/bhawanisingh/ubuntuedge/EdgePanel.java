package org.bhawanisingh.ubuntuedge;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class EdgePanel extends JPanel {

	private static final Color firstColor = new Color(119, 33, 111, 0);
	private static final Color lastColor = new Color(44, 0, 30, 0);

	public EdgePanel() {
		setOpaque(false);
	}

	public EdgePanel(LayoutManager gridLayout) {
		super(gridLayout);
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		GradientPaint paint = new GradientPaint(0, 0, firstColor, 0, getHeight(), lastColor, true);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(paint);
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		// g2d.dispose();

	}

}