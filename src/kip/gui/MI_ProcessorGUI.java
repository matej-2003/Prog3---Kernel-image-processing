package kip.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class MI_ProcessorGUI extends JPanel {
	public JFrame frame;
	
	// Multiple Image List Components
	public DefaultListModel<String> image_list_model;
	public JList<String> image_list;
	public JButton add_img_btn, remove_img_btn;
	
	// Output Folder Components
	public JTextField output_path_field;
	public JButton browse_output_btn;

	// Sequence and Execution
	public JTable summary_table;
	public DefaultTableModel summary_model;
	public JButton edit_sequence_button, run_button;
	public JSpinner thread_count_spinner;
	public JTextArea console_area;

	public MI_ProcessorGUI(JFrame frame_) {
		super(new BorderLayout(10, 10));
		this.frame = frame_;
		init_components();
		create_layout();

		TableColumn idColumn = summary_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);
	}

	private void init_components() {
		// Image List setup
		image_list_model = new DefaultListModel<>();
		image_list = new JList<>(image_list_model);
		add_img_btn = new JButton("Add Images...");
		remove_img_btn = new JButton("Remove Selected");

		// Output setup
		output_path_field = new JTextField("No folder selected...");
		output_path_field.setEditable(false);
		browse_output_btn = new JButton("Browse...");

		// Sequence Table setup
		summary_model = new DefaultTableModel(new Object[]{"#", "Operation", "Parameters"}, 0);
		summary_table = new JTable(summary_model);
		edit_sequence_button = new JButton("Edit Sequence");

		// Execution setup
		thread_count_spinner = new JSpinner(new SpinnerNumberModel(4, 1, 128, 1));
		run_button = new JButton("Start Batch Process");
		// run_button.setBackground(new Color(46, 139, 87)); // SeaGreen for visibility
		// run_button.setForeground(Color.WHITE);

		// Console
		console_area = new JTextArea(8, 20);
	}

	private void create_layout() {
		// --- TOP/CENTER: The Main Configuration Area ---
		JPanel config_panel = new JPanel(new GridLayout(1, 2, 10, 10));

		// Left Column: Image Selection
		JPanel left_panel = new JPanel(new BorderLayout(5, 5));
		left_panel.setBorder(BorderFactory.createTitledBorder("Input Images"));
		left_panel.add(new JScrollPane(image_list), BorderLayout.CENTER);
		
		JPanel img_btn_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		img_btn_panel.add(add_img_btn);
		img_btn_panel.add(remove_img_btn);
		left_panel.add(img_btn_panel, BorderLayout.SOUTH);

		// Right Column: Sequence Table
		JPanel right_panel = new JPanel(new BorderLayout(5, 5));
		right_panel.setBorder(BorderFactory.createTitledBorder("Processing Sequence"));
		right_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		right_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		config_panel.add(left_panel);
		config_panel.add(right_panel);

		// --- MIDDLE: Output Folder Selection ---
		JPanel output_panel = new JPanel(new BorderLayout(5, 5));
		output_panel.setBorder(BorderFactory.createTitledBorder("Output Destination"));
		output_panel.add(output_path_field, BorderLayout.CENTER);
		output_panel.add(browse_output_btn, BorderLayout.EAST);

		// Combine Top and Middle
		JPanel top_container = new JPanel(new BorderLayout(5, 10));
		top_container.add(config_panel, BorderLayout.CENTER);
		top_container.add(output_panel, BorderLayout.SOUTH);

		// --- BOTTOM: Console and Run Button ---
		JPanel bottom_panel = new JPanel(new BorderLayout(5, 5));
		bottom_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		bottom_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);

		JPanel run_bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		run_bar.add(new JLabel("Max Threads: "));
		run_bar.add(thread_count_spinner);
		run_bar.add(Box.createHorizontalStrut(20));
		run_bar.add(run_button);
		bottom_panel.add(run_bar, BorderLayout.SOUTH);

		// Final Split
		JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top_container, bottom_panel);
		split_pane.setDividerLocation(400);
		split_pane.setResizeWeight(0.6);

		add(split_pane, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
}