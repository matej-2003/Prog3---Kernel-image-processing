package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

public class MI_ProcessorGUI extends JPanel {
	public JFrame frame;
	
	// UI Components
	public DefaultListModel<ImageEntry> image_list_model;
	public JList<ImageEntry> image_list;
	public JButton add_img_btn, remove_img_btn;
	
	public JTextField output_path_field;
	public JButton browse_output_btn;

	public JTable summary_table;
	public DefaultTableModel summary_model;
	public JButton edit_sequence_button, run_button;
	public JSpinner thread_count_spinner;
	public JTextArea console_area;

	// The Sequence Editor (Same as Single Image GUI)
	public OpSequence editor_panel;

	public MI_ProcessorGUI(JFrame frame_) {
		super(new BorderLayout(10, 10));
		this.frame = frame_;
		init_components();
		create_layout();
		attach_listeners();
	}

	// Helper class to store both the file path and the thumbnail
	private class ImageEntry {
		File file;
		ImageIcon thumbnail;
		public ImageEntry(File f) {
			this.file = f;
			this.thumbnail = createThumbnail(f);
		}
		@Override
		public String toString() { return file.getName(); }
	}

	private void init_components() {
		editor_panel = new OpSequence(); // Initialize the shared editor logic
		
		image_list_model = new DefaultListModel<>();
		image_list = new JList<>(image_list_model);
		image_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		image_list.setCellRenderer(new ImageListRenderer()); // Set custom visual style
		
		add_img_btn = new JButton("Add Images...");
		remove_img_btn = new JButton("Remove Selected");

		output_path_field = new JTextField("./images/processed/");
		browse_output_btn = new JButton("Browse...");

		summary_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge"}, 0);
		summary_table = new JTable(summary_model);
		summary_table.setEnabled(false);
		edit_sequence_button = new JButton("Edit Sequence");

		thread_count_spinner = new JSpinner(new SpinnerNumberModel(4, 1, 128, 1));
		run_button = new JButton("Start Batch Process");
		run_button.setBackground(new Color(46, 139, 87));
		run_button.setForeground(Color.WHITE);

		console_area = new JTextArea(8, 20);

		
		TableColumn idColumn = summary_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);
	}

	private void attach_listeners() {
		add_img_btn.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setMultiSelectionEnabled(true);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				for (File f : chooser.getSelectedFiles()) {
					image_list_model.addElement(new ImageEntry(f));
				}
			}
		});

		remove_img_btn.addActionListener(e -> {
			int[] indices = image_list.getSelectedIndices();
			for (int i = indices.length - 1; i >= 0; i--) image_list_model.remove(indices[i]);
		});

		browse_output_btn.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser("./images/processed/");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				output_path_field.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});

		// Open the Sequence Editor in a separate popup window
		edit_sequence_button.addActionListener(e -> {
			JDialog dialog = new JDialog(frame, "Edit Processing Sequence", true);
			dialog.getContentPane().add(editor_panel);
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			dialog.setVisible(true);
			
			// After editor closes, you would sync editor_panel.model to this.summary_model
			syncSequenceTable();
		});
	}

	private void syncSequenceTable() {
		// Logic to copy rows from editor_panel to our local summary_table
		summary_model.setRowCount(0);
		int i = 1;
		for (Operation op : editor_panel.getOperations()) {
			summary_model.addRow(new Object[]{i++, op.kernel, op.edge});
		}
	}

	private ImageIcon createThumbnail(File f) {
		try {
			BufferedImage img = ImageIO.read(f);
			Image scaled = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);
		} catch (Exception e) {
			return null;
		}
	}

	// Custom Renderer to show Image + Text in the JList
	private class ImageListRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof ImageEntry) {
				ImageEntry entry = (ImageEntry) value;
				label.setIcon(entry.thumbnail);
				label.setText(entry.file.getName());
				label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			}
			return label;
		}
	}

	private void create_layout() {
		// ... (Same as previous layout code provided) ...
		// Ensure the summary_table is added to the right-hand panel
		JPanel config_panel = new JPanel(new GridLayout(1, 2, 10, 10));

		JPanel left_panel = new JPanel(new BorderLayout(5, 5));
		left_panel.setBorder(BorderFactory.createTitledBorder("Input Images (Gallery)"));
		left_panel.add(new JScrollPane(image_list), BorderLayout.CENTER);
		JPanel img_btn_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		img_btn_panel.add(add_img_btn); img_btn_panel.add(remove_img_btn);
		left_panel.add(img_btn_panel, BorderLayout.SOUTH);

		JPanel right_panel = new JPanel(new BorderLayout(5, 5));
		right_panel.setBorder(BorderFactory.createTitledBorder("Sequence"));
		right_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		right_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		config_panel.add(left_panel);
		config_panel.add(right_panel);

		JPanel output_panel = new JPanel(new BorderLayout(5, 5));
		output_panel.setBorder(BorderFactory.createTitledBorder("Output Destination"));
		output_panel.add(output_path_field, BorderLayout.CENTER);
		output_panel.add(browse_output_btn, BorderLayout.EAST);

		JPanel top_container = new JPanel(new BorderLayout(5, 10));
		top_container.add(config_panel, BorderLayout.CENTER);
		top_container.add(output_panel, BorderLayout.SOUTH);

		JPanel bottom_panel = new JPanel(new BorderLayout(5, 5));
		bottom_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		bottom_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		JPanel run_bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		run_bar.add(new JLabel("Threads: ")); run_bar.add(thread_count_spinner);
		run_bar.add(run_button);
		bottom_panel.add(run_bar, BorderLayout.SOUTH);

		JSplitPane main_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top_container, bottom_panel);
		main_split.setDividerLocation(400);
		add(main_split, BorderLayout.CENTER);
	}
}