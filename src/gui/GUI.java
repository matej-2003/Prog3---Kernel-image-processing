package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI {

	public static JFrame frame;
	public static JTabbedPane tabbed_panel;
	public static JPanel single_panel, multiple_panel;

	// Image labels
	public static JLabel input_image_label, output_image_label;
	
	// Summary Table and Model for the main view
	public static JTable summary_table;
	public static DefaultTableModel summary_model;
	
	public static JLabel width_label, height_label, pixel_count_label;
	public static JTextField time_field;
	public static JButton run_button, edit_sequence_button;

	// This is the editor class we built before
	public static OpSequence editor_panel;

	public static void init_components() {
		tabbed_panel = new JTabbedPane();
		editor_panel = new OpSequence(); // Instantiate the editor
		
		// Setup the summary table model (simpler version of the editor table)
		summary_model = new DefaultTableModel(new Object[]{"#", "Operation"}, 0);
		summary_table = new JTable(summary_model);
		summary_table.setEnabled(false); // Only for viewing here

		frame.add(tabbed_panel);
	}

	/**
	 * Helper to scale images to fit the labels
	 */
	private static ImageIcon getScaledIcon(BufferedImage src, int maxWidth, int maxHeight) {
		double ratio = Math.min((double)maxWidth / src.getWidth(), (double)maxHeight / src.getHeight());
		int width = (int) (src.getWidth() * ratio);
		int height = (int) (src.getHeight() * ratio);
		
		Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

	public static void create_single_panel() {
		single_panel = new JPanel(new BorderLayout(10, 10));

		// ===== CENTER: Images =====
		JPanel image_container = new JPanel(new GridLayout(1, 2, 10, 10));
		
		BufferedImage img;
		try {
			img = ImageIO.read(new File("./data/mona lisa.jpg"));
		} catch (Exception e) {
			img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		}

		// Scale images to fit roughly 450x450
		ImageIcon scaledIcon = getScaledIcon(img, 450, 450);
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
		side_panel.setPreferredSize(new Dimension(250, 0));
		side_panel.setBorder(BorderFactory.createTitledBorder("Sequence"));

		edit_sequence_button = new JButton("Edit Sequence...");
		side_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		side_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		// ===== BOTTOM: Info & Run =====
		JPanel bottom_panel = new JPanel(new BorderLayout());
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

		run_button = new JButton("RUN PROCESSOR");
		run_button.setFont(new Font("SansSerif", Font.BOLD, 14));
		run_button.setBackground(new Color(40, 150, 40));
		run_button.setForeground(Color.WHITE);

		bottom_panel.add(stats_panel, BorderLayout.WEST);
		bottom_panel.add(run_button, BorderLayout.EAST);

		// Assembly
		single_panel.add(image_container, BorderLayout.CENTER);
		single_panel.add(side_panel, BorderLayout.EAST);
		single_panel.add(bottom_panel, BorderLayout.SOUTH);

		tabbed_panel.add("Single Image", single_panel);

		// ACTION: Open the editor dialog
		edit_sequence_button.addActionListener(e -> showEditorDialog());
	}

	/**
	 * Opens the OpSequence editor in a popup window
	 */
	private static void showEditorDialog() {
		JDialog dialog = new JDialog(frame, "Operation Sequence Editor", true);
		dialog.setLayout(new BorderLayout());
		dialog.add(editor_panel);
		dialog.setSize(900, 600);
		dialog.setLocationRelativeTo(frame);
		
		// When dialog closes, sync the summary table on the main GUI
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        refreshSummaryTable();
		    }
		});
		
		dialog.setVisible(true);
	}

	/**
	 * Syncs the main view table with the editor's data
	 */
	private static void refreshSummaryTable() {
		summary_model.setRowCount(0);
		int i = 1;
		for (Operation op : editor_panel.getOperations()) {
			summary_model.addRow(new Object[]{i++, op.kernel});
		}
	}

	public static void create_multiple_panel() {
		multiple_panel = new JPanel(new BorderLayout());
		multiple_panel.add(new JLabel("Batch processing area", SwingConstants.CENTER));
		tabbed_panel.add("Multiple Images", multiple_panel);
	}

	public static void main(String[] args) {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

		frame = new JFrame("Image Processor Pro");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100, 700);

		init_components();
		create_single_panel();
		create_multiple_panel();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}