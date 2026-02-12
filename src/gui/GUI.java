package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

public class GUI {
	public static JFrame frame;
	public static JTabbedPane tabbed_panel;
	public static JPanel single_panel, multiple_panel, parallel_panel;

	public static JLabel input_image_label, output_image_label;
	
	public static JTable summary_table;
	public static DefaultTableModel summary_model;
	
	public static JLabel width_label, height_label, pixel_count_label;
	public static JTextField time_field;
	public static JButton run_button, edit_sequence_button, save_button, exec_settings_button;

	public static JTextArea console_area;
	public static OpSequence editor_panel;
	public static RunSettings run_settings_panel;

	public static void init_components() {
		tabbed_panel = new JTabbedPane();
		editor_panel = new OpSequence(); 
		run_settings_panel = new RunSettings(); // Initialize the new settings class
		summary_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge method"}, 0);
		summary_table = new JTable(summary_model);
		summary_table.setEnabled(false); 
		
		TableColumn idColumn = summary_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);

		frame.add(tabbed_panel);
	}
	
	private static ImageIcon getScaledIcon(BufferedImage src, int maxWidth, int maxHeight) {
		if (src == null) return null;
		
		double ratio = Math.min((double)maxWidth / src.getWidth(), (double)maxHeight / src.getHeight());
		int width = (int) (src.getWidth() * ratio);
		int height = (int) (src.getHeight() * ratio);
		
		// Use SCALE_SMOOTH for better quality
		Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

	public static void create_single_panel() {
		single_panel = new JPanel(new BorderLayout(5, 5));
		JPanel center_panel = new JPanel(new BorderLayout(10, 10));

		// ===== IMAGES =====
		JPanel image_container = new JPanel(new GridLayout(1, 2, 10, 10));
		
		BufferedImage img;
		try {
			img = ImageIO.read(new File("./data/mona lisa.jpg"));
			// img = ImageIO.read(new File("./images/rockefeller_center.jpg"));
		} catch (Exception e) {
			img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
		}

		// Calculate fitting size (Panel width is ~1100, half is 550, minus margins)
		ImageIcon scaledIcon = getScaledIcon(img, 400, 450);
		input_image_label = new JLabel(scaledIcon);
		output_image_label = new JLabel(scaledIcon);

		JPanel left_box = new JPanel(new GridBagLayout());
		left_box.setBorder(BorderFactory.createTitledBorder("Input"));
		left_box.add(input_image_label);

		JPanel right_box = new JPanel(new GridBagLayout());
		right_box.setBorder(BorderFactory.createTitledBorder("Output"));
		right_box.add(output_image_label);

		image_container.add(left_box);
		image_container.add(right_box);

		// ===== RIGHT: Sequence Summary =====
		JPanel side_panel = new JPanel(new BorderLayout(5, 5));
		side_panel.setPreferredSize(new Dimension(220, 0));
		side_panel.setBorder(BorderFactory.createTitledBorder("Sequence Summary"));

		edit_sequence_button = new JButton("Edit Sequence...");
		side_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		side_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		// ===== STATS BAR (Bottom of Center Panel) =====
		JPanel stats_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		width_label = new JLabel("W: " + img.getWidth());
		height_label = new JLabel("H: " + img.getHeight());
		pixel_count_label = new JLabel("Pixels: " + (img.getWidth() * img.getHeight()));
		time_field = new JTextField("0", 6);
		time_field.setEditable(false);

		stats_panel.add(width_label);
		stats_panel.add(height_label);
		stats_panel.add(pixel_count_label);
		stats_panel.add(new JLabel("Time(ms):"));
		stats_panel.add(time_field);

		// ===== CONSOLE (Bottom of Split Pane) =====
		console_area = new JTextArea();
		console_area.setEditable(false);
		console_area.setBackground(new Color(245, 245, 245));
		
		JPanel console_button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		run_button = new JButton("Run Process");
		save_button = new JButton("Save Images");
		exec_settings_button = new JButton("Execution settings");
		console_button_panel.add(save_button);
		console_button_panel.add(exec_settings_button);
		console_button_panel.add(run_button);

		JPanel console_panel = new JPanel(new BorderLayout(5, 5));
		console_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		console_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		console_panel.add(console_button_panel, BorderLayout.SOUTH);

		// ===== ASSEMBLY =====
		center_panel.add(image_container, BorderLayout.CENTER);
		center_panel.add(side_panel, BorderLayout.EAST);
		center_panel.add(stats_panel, BorderLayout.SOUTH);
		
		// Split pane divides the Image/Stats area from the Console
		JSplitPane vertical_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center_panel, console_panel);
		vertical_split.setDividerLocation(420);
		vertical_split.setResizeWeight(0.7); // Gives more space to images on resize
		
		single_panel.add(vertical_split, BorderLayout.CENTER);
		tabbed_panel.add("Single Image", single_panel);

		edit_sequence_button.addActionListener(e -> showEditorDialog());
		exec_settings_button.addActionListener(e -> showDialog("Execution Settings", run_settings_panel, 400, 300));
	}

	private static void showEditorDialog() {
		JDialog dialog = new JDialog(frame, "Operation Sequence Editor", true);
		dialog.setLayout(new BorderLayout());
		dialog.add(editor_panel);
		dialog.setSize(1000, 600);
		dialog.setLocationRelativeTo(frame);
		
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				refreshSummaryTable();
			}
		});
		
		dialog.setVisible(true);
	}

	private static void showDialog(String title, JPanel panel, int w, int h) {
		JDialog dialog = new JDialog(frame, title, true);
		dialog.setLayout(new BorderLayout());
		dialog.add(panel, BorderLayout.CENTER);
		dialog.setSize(w, h);
		dialog.setLocationRelativeTo(frame);
		
		// Sync summary table if we closed the sequence editor
		if (panel == editor_panel) {
			dialog.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) { refreshSummaryTable(); }
			});
		}
		dialog.setVisible(true);
	}


	private static void refreshSummaryTable() {
		summary_model.setRowCount(0);
		int i = 1;
		for (Operation op : editor_panel.getOperations()) {
			summary_model.addRow(new Object[]{i++, op.kernel, op.edge});
		}
	}

	public static void create_multiple_panel() {
		multiple_panel = new JPanel(new BorderLayout());
		multiple_panel.add(new JLabel("Batch processing area", SwingConstants.CENTER));
		tabbed_panel.add("Multiple Images", multiple_panel);
	}

	public static void create_parallel_panel() {
		parallel_panel = new JPanel(new BorderLayout());
		parallel_panel.add(new JLabel("Parallel processing area", SwingConstants.CENTER));
		tabbed_panel.add("Parallel execution", parallel_panel);
	}

	public static void main(String[] args) {
		frame = new JFrame("Kernel Image processor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);

		init_components();
		create_single_panel();
		create_parallel_panel();
		create_multiple_panel();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}