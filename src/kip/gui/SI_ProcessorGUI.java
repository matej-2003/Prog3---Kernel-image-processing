package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

public class SI_ProcessorGUI extends JPanel {
	public JFrame frame;
	public JLabel input_image_label, output_image_label;
	
	public JTable summary_table;
	public DefaultTableModel summary_model;
	
	// Metadata Labels
	public JLabel path_label, size_label, pixel_count_label;
	public JTextField time_field;
	public JButton run_button, edit_sequence_button, select_image_button;

	public JTextArea console_area;
	public OpSequence editor_panel;
	private BufferedImage inputImage;

	public SI_ProcessorGUI(JFrame frame_) {
		super(new BorderLayout()); 
		this.frame = frame_;
		init_components();
		create_single_panel();
	}

	public void init_components() {
		editor_panel = new OpSequence(); 
		summary_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge method"}, 0);
		summary_table = new JTable(summary_model);
		summary_table.setEnabled(false); 
		
		// Metadata init
		path_label = new JLabel("Path: None");
		size_label = new JLabel("Size: 0 x 0");
		pixel_count_label = new JLabel("Pixels: 0");
		
		select_image_button = new JButton("Select Input Image...");
		
		TableColumn idColumn = summary_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
	}
	
	private ImageIcon getScaledIcon(BufferedImage src, JLabel targetLabel) {
		if (src == null || targetLabel.getWidth() == 0 || targetLabel.getHeight() == 0)
			return null;

		double ratio = Math.min(
			(double) targetLabel.getWidth() / src.getWidth(),
			(double) targetLabel.getHeight() / src.getHeight()
		);

		int width = (int) (src.getWidth() * ratio);
		int height = (int) (src.getHeight() * ratio);

		Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}


	public void create_single_panel() {
		JPanel center_panel = new JPanel(new BorderLayout(10, 10));

		// ===== IMAGES (Input & Output) =====
		JPanel image_container = new JPanel(new GridLayout(1, 2, 10, 10));
		
		input_image_label = new JLabel("", SwingConstants.CENTER);
		output_image_label = new JLabel("", SwingConstants.CENTER);

		// Use JScrollPanes to stop the layout from "jumping" when console is resized
		JScrollPane input_scroll = new JScrollPane(input_image_label);
		input_scroll.setPreferredSize(new Dimension(400, 450));
		
		JScrollPane output_scroll = new JScrollPane(output_image_label);
		output_scroll.setPreferredSize(new Dimension(400, 450));

		// --- LEFT BOX (Input + Selection + Metadata) ---
		JPanel left_box = new JPanel(new BorderLayout(5, 5));
		left_box.setBorder(BorderFactory.createTitledBorder("Input"));
		left_box.add(input_scroll, BorderLayout.CENTER);

		JPanel left_controls = new JPanel(new BorderLayout(5, 5));
		JPanel metadata_panel = new JPanel(new GridLayout(3, 1));
		metadata_panel.add(path_label);
		metadata_panel.add(size_label);
		metadata_panel.add(pixel_count_label);
		
		left_controls.add(metadata_panel, BorderLayout.CENTER);
		left_controls.add(select_image_button, BorderLayout.SOUTH);
		left_box.add(left_controls, BorderLayout.SOUTH);

		// --- RIGHT BOX (Output) ---
		JPanel right_box = new JPanel(new BorderLayout());
		right_box.setBorder(BorderFactory.createTitledBorder("Output Preview"));
		right_box.add(output_scroll, BorderLayout.CENTER);

		image_container.add(left_box);
		image_container.add(right_box);

		// ===== SIDE PANEL (Sequence) =====
		JPanel side_panel = new JPanel(new BorderLayout(5, 5));
		side_panel.setPreferredSize(new Dimension(220, 0));
		side_panel.setBorder(BorderFactory.createTitledBorder("Sequence Summary"));
		edit_sequence_button = new JButton("Edit Sequence...");
		side_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		side_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		// ===== BOTTOM BAR (Time & Execution) =====
		JPanel bottom_bar = new JPanel(new BorderLayout());
		
		JPanel time_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		time_field = new JTextField("0", 6);
		time_field.setEditable(false);
		time_panel.add(new JLabel("Execution Time (ms):"));
		time_panel.add(time_field);

		JPanel run_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		run_button = new JButton("Run Process");
		run_panel.add(run_button);

		bottom_bar.add(time_panel, BorderLayout.WEST);
		bottom_bar.add(run_panel, BorderLayout.EAST);

		// ===== CONSOLE =====
		console_area = new JTextArea();
		console_area.setEditable(false);
		JPanel console_panel = new JPanel(new BorderLayout(5, 5));
		console_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		console_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		console_panel.add(bottom_bar, BorderLayout.SOUTH);

		// ===== ASSEMBLY =====
		center_panel.add(image_container, BorderLayout.CENTER);
		center_panel.add(side_panel, BorderLayout.EAST);
		
		JSplitPane vertical_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center_panel, console_panel);
		vertical_split.setDividerLocation(550);
		vertical_split.setResizeWeight(0.8); 
		
		add(vertical_split, BorderLayout.CENTER);

		// Listeners
		edit_sequence_button.addActionListener(e -> showEditorDialog());
		select_image_button.addActionListener(e -> openImageAction());


		input_image_label.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				if (inputImage != null) {
					input_image_label.setIcon(getScaledIcon(inputImage, input_image_label));
				}
			}
		});

	}

	private void openImageAction() {
		JFileChooser chooser = new JFileChooser("./data");
		chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "bmp", "jpeg"));
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				BufferedImage img = ImageIO.read(file);
				if (img != null) {
					inputImage = img;
					input_image_label.setIcon(getScaledIcon(img, input_image_label));
					output_image_label.setIcon(null);

					// Update Metadata Labels
					path_label.setText("Path: " + file.getName());
					size_label.setText("Size: " + img.getWidth() + " x " + img.getHeight());
					pixel_count_label.setText("Pixels: " + (long)img.getWidth() * img.getHeight());
					
					console_area.append("Loaded: " + file.getAbsolutePath() + "\n");
				}
			} catch (Exception ex) {
				console_area.append("Error: " + ex.getMessage() + "\n");
			}
		}
	}

	private void showEditorDialog() {
		JDialog dialog = new JDialog(frame, "Operation Sequence Editor", true);
		dialog.add(editor_panel);
		dialog.setSize(1000, 600);
		dialog.setLocationRelativeTo(frame);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) { refreshSummaryTable(); }
		});
		dialog.setVisible(true);
	}

	private void refreshSummaryTable() {
		summary_model.setRowCount(0);
		int i = 1;
		for (Operation op : editor_panel.getOperations()) {
			summary_model.addRow(new Object[]{i++, op.kernel, op.edge});
		}
	}
}