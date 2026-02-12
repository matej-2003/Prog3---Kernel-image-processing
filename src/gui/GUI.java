package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI {

	public static JFrame frame;

	public static JMenuBar menu_bar;
	public static JMenu menu, submenu;
	public static JMenuItem i1, i2, i3, i4, i5;

	public static JTabbedPane tabbed_panel;
	public static JPanel single_panel, multiple_panel;
	
	// Labels for image stats
	public static JLabel width_label, height_label, pixel_count_label;
	public static JLabel input_image_label, output_image_label;
	public static JTextField time_field;

	// Our new component
	public static OpSequence opSequence;

	public static void create_menu() {
		menu_bar = new JMenuBar();
		menu = new JMenu("Menu");
		submenu = new JMenu("Sub Menu");

		i1 = new JMenuItem("Item 1");
		i2 = new JMenuItem("Item 2");
		i3 = new JMenuItem("Item 3");
		i4 = new JMenuItem("Item 4");
		i5 = new JMenuItem("Item 5");

		menu.add(i1);
		menu.add(i2);
		menu.add(i3);
		submenu.add(i4);
		submenu.add(i5);
		menu.add(submenu);
		menu_bar.add(menu);

		frame.setJMenuBar(menu_bar);
	}

	public static void init_components() {
		tabbed_panel = new JTabbedPane();
		// Initialize the OpSequence component
		opSequence = new OpSequence();
		frame.add(tabbed_panel);
	}

	public static void create_single_panel() {
		single_panel = new JPanel(new BorderLayout(8, 8));

		// ===== TOP: Image Display Area =====
		JPanel image_panel = new JPanel(new GridLayout(1, 2, 5, 5));

		BufferedImage img;
		try {
			// Using a placeholder logic or loading your image
            img = ImageIO.read(new File("./data/mona lisa.jpg"));
//            img = ImageIO.read(new File("./images/rockefeller_center.jpg"));
		} catch (IOException e) {
			img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		}

		input_image_label = new JLabel(new ImageIcon(img));
		output_image_label = new JLabel(new ImageIcon(img));

		JPanel left_img_box = new JPanel(new BorderLayout());
		left_img_box.setBorder(BorderFactory.createTitledBorder("Input Image"));
		left_img_box.add(new JScrollPane(input_image_label));

		JPanel right_img_box = new JPanel(new BorderLayout());
		right_img_box.setBorder(BorderFactory.createTitledBorder("Output Image"));
		right_img_box.add(new JScrollPane(output_image_label));

		image_panel.add(left_img_box);
		image_panel.add(right_img_box);

		// ===== BOTTOM: Status Info =====
		JPanel status_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		status_panel.setBorder(BorderFactory.createEtchedBorder());
		
		width_label = new JLabel("Width: " + img.getWidth());
		height_label = new JLabel("Height: " + img.getHeight());
		pixel_count_label = new JLabel("Pixels: " + (img.getWidth() * img.getHeight()));
		
		status_panel.add(width_label);
		status_panel.add(height_label);
		status_panel.add(pixel_count_label);
		status_panel.add(new JLabel("Last Execution Time:"));
		time_field = new JTextField(8);
		time_field.setEditable(false);
		status_panel.add(time_field);

		// ===== ASSEMBLY USING SPLITPANE =====
		// We put the images on top and the OpSequence editor on the bottom
		JSplitPane main_split = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				image_panel,
				opSequence
		);
		main_split.setDividerLocation(300);
		main_split.setResizeWeight(0.5);

		single_panel.add(main_split, BorderLayout.CENTER);
		single_panel.add(status_panel, BorderLayout.SOUTH);

		tabbed_panel.add("Single image", single_panel);
	}

	public static void create_multiple_panel() {
		multiple_panel = new JPanel(new BorderLayout());
		multiple_panel.add(new JLabel("Batch processing controls go here..."), BorderLayout.CENTER);
		tabbed_panel.add("Multiple images", multiple_panel);
	}

	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {}

		frame = new JFrame("Image Processor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 850);

		init_components();
		create_menu();
		create_single_panel();
		create_multiple_panel();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}