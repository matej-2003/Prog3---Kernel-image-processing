package kip.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageBrowserGUI extends JFrame {

	JLabel mainImageLabel = new JLabel();
	JLabel infoLabel = new JLabel("No image selected");

	JPanel thumbPanel = new JPanel();
	JScrollPane scrollPane;

	File imageFolder = new File("./src/data"); // change if needed

	public ImageBrowserGUI() {
		setTitle("Image Browser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// main image display
		mainImageLabel.setHorizontalAlignment(JLabel.CENTER);
		add(mainImageLabel, BorderLayout.CENTER);

		// info panel
		infoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		add(infoLabel, BorderLayout.SOUTH);

		// thumbnail panel
		thumbPanel.setLayout(new BoxLayout(thumbPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(thumbPanel);
		scrollPane.setPreferredSize(new Dimension(120, 400));
		add(scrollPane, BorderLayout.WEST);

		loadThumbnails();

		setSize(900, 600);
		setVisible(true);
	}

	void loadThumbnails() {
		File[] files = imageFolder.listFiles();
		if (files == null) return;

		for (File file : files) {
			try {
				BufferedImage img = ImageIO.read(file);
				if (img == null) continue;

				Image thumb = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
				ImageIcon icon = new ImageIcon(thumb);

				JLabel thumbLabel = new JLabel(icon);
				thumbLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				thumbLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

				thumbLabel.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						showImage(file);
					}
				});

				thumbPanel.add(thumbLabel);

			} catch (IOException e) {
				System.out.println("Failed to load: " + file.getName());
			}
		}
	}

	void showImage(File file) {
		try {
			BufferedImage img = ImageIO.read(file);
			mainImageLabel.setIcon(new ImageIcon(img));

			long fileSizeKB = file.length() / 1024;
			int width = img.getWidth();
			int height = img.getHeight();

			int avgColor = averageColor(img);
			Color c = new Color(avgColor);

			infoLabel.setText(
					"File: " + file.getName() +
							" | Size: " + width + "x" + height +
							" | Disk: " + fileSizeKB + " KB" +
							" | Avg RGB: (" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")"
			);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	int averageColor(BufferedImage img) {
		long r=0,g=0,b=0;
		int count = 0;

		for (int y=0; y<img.getHeight(); y+=5) {
			for (int x=0; x<img.getWidth(); x+=5) {
				int p = img.getRGB(x,y);
				Color c = new Color(p);
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
				count++;
			}
		}

		return new Color((int)(r/count),(int)(g/count),(int)(b/count)).getRGB();
	}

	public static void main(String[] args) {
		new ImageBrowserGUI();
	}
}
