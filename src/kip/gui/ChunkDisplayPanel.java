package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

public class ChunkDisplayPanel extends JPanel {

	public ChunkDisplayPanel() {
		// Dark background to make the gaps between chunks visible
		setBackground(Color.DARK_GRAY);
		// Default layout
		setLayout(new FlowLayout());
	}

	public void setChunks(List<BufferedImage> chunks, int cols, int rows) {
		this.removeAll();
		
		// Set grid layout based on the thread distribution
		this.setLayout(new GridLayout(rows, cols, 2, 2));

		// Calculate a reasonable size for each chunk label in the UI
		// We assume the middle panel has a preferred area of roughly 400x500
		int viewWidth = 350 / cols;
		int viewHeight = 450 / rows;

		for (BufferedImage chunkImg : chunks) {
			JLabel label = new JLabel();
			
			// Scale the chunk so it fits in the grid cell
			label.setIcon(getScaledIcon(chunkImg, viewWidth, viewHeight));
			
			// Add a border to visualize the thread boundaries
			label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			
			this.add(label);
		}

		this.revalidate();
		this.repaint();
	}

	private ImageIcon getScaledIcon(BufferedImage src, int maxWidth, int maxHeight) {
		if (src == null) return null;
		
		// Ensure dimensions are at least 1px
		maxWidth = Math.max(1, maxWidth);
		maxHeight = Math.max(1, maxHeight);
		
		double ratio = Math.min((double) maxWidth / src.getWidth(), (double) maxHeight / src.getHeight());
		int width = (int) (src.getWidth() * ratio);
		int height = (int) (src.getHeight() * ratio);
		
		Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}