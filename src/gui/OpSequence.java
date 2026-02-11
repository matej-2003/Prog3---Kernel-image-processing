package gui;

import java.awt.*;
import java.util.ArrayList;
import javax.management.openmbean.OpenDataException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class OpSequence extends JPanel {

	public JTable operation_table;
	public DefaultTableModel table_model;
	public JButton add_button, remove_button, up_button, down_button;

	public JComboBox<String> toolbar_kernel_select, toolbar_edge_select;
	public JComboBox<String> settings_kernel_select, settings_edge_select;
	public JLabel operation_count_label, estimated_ops_label;
	public JTextArea custom_kernel_area, console_area;
	public JButton run_button, save_button;
	public String kernel_list[] = {"Identity", "Blur", "Gaussian", "Sharpen", "Emboss", "Outline", "Edge", "Sobel X", "Sobel Y", "Custom"};
	public String edge_list[] = {"Zero padding", "Clamp", "Wrap", "Mirror"};
	public ArrayList<Operation> operations;
	

	public OpSequence() {
		operations = new ArrayList<Operation>();
		init_components();
		init_actions();
	}

	private void reindexTable() {
		for (int i = 0; i < table_model.getRowCount(); i++) {
			table_model.setValueAt(i + 1, i, 0);
		}
	}

	private void updateOperationCount() {
		operation_count_label.setText("Operations in sequence: " + table_model.getRowCount());
	}

	public void load_settings() {
		
	}

	public void init_components() {
		setLayout(new BorderLayout(5, 5));

		// Initialize Table
		table_model = new DefaultTableModel(
				new Object[]{"#", "Kernel", "Edge handling"}, 0
		);
		operation_table = new JTable(table_model);

		// Column width for "#"
		TableColumn idColumn = operation_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);

		// Initialize UI Components
		toolbar_kernel_select = new JComboBox<>(kernel_list);
		toolbar_edge_select = new JComboBox<>(edge_list);

		settings_kernel_select = new JComboBox<>(kernel_list);
		settings_edge_select = new JComboBox<>(edge_list);

		custom_kernel_area = new JTextArea(10, 12);
		operation_count_label = new JLabel("Operations in sequence: 0");
		estimated_ops_label = new JLabel("Estimated total operations: 0");

		JPanel center_panel = new JPanel(new GridLayout(1, 2, 5, 5));

		// ================= LEFT PANEL =================
		JPanel left_panel = new JPanel(new BorderLayout(6, 5));
		left_panel.setBorder(BorderFactory.createTitledBorder("Operation Sequence"));
		left_panel.add(new JScrollPane(operation_table), BorderLayout.CENTER);

		// TOOLBAR
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
		add_button = new JButton(new ImageIcon("./icons/plus.png"));
		remove_button = new JButton(new ImageIcon("./icons/cross.png"));
		up_button = new JButton(new ImageIcon("./icons/arrow-up.png"));
		down_button = new JButton(new ImageIcon("./icons/arrow-down.png"));

		// Set buttons to be compact since they have icons
		add_button.setMargin(new Insets(2,2,2,2));
		remove_button.setMargin(new Insets(2,2,2,2));
		up_button.setMargin(new Insets(2,2,2,2));
		down_button.setMargin(new Insets(2,2,2,2));

		toolbar.add(toolbar_kernel_select);
		toolbar.add(toolbar_edge_select);
		toolbar.add(add_button);
		toolbar.add(remove_button);
		toolbar.add(up_button);
		toolbar.add(down_button);

		left_panel.add(toolbar, BorderLayout.SOUTH);

		// ================= RIGHT PANEL =================
		JPanel right_panel = new JPanel(new BorderLayout(5, 5));
		right_panel.setBorder(BorderFactory.createTitledBorder("Settings & Info"));

		JPanel settings_panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Row 0: Kernel Select
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
		settings_panel.add(new JLabel("Kernel:"), gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		settings_panel.add(settings_kernel_select, gbc);

		// Row 1: Edge Select
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
		settings_panel.add(new JLabel("Edge handling:"), gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		settings_panel.add(settings_edge_select, gbc);

		// Row 2: Custom Kernel Area
		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		settings_panel.add(new JLabel("Custom kernel:"), gbc);

		gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		settings_panel.add(new JScrollPane(custom_kernel_area), gbc);

		JPanel info_panel = new JPanel();
		info_panel.setLayout(new BoxLayout(info_panel, BoxLayout.Y_AXIS));
		info_panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		info_panel.add(operation_count_label);
		info_panel.add(Box.createVerticalStrut(5));
		info_panel.add(estimated_ops_label);

		JPanel right_top = new JPanel(new BorderLayout(5, 5));
		right_top.add(settings_panel, BorderLayout.NORTH);
		right_top.add(info_panel, BorderLayout.CENTER);
		right_panel.add(right_top, BorderLayout.NORTH);

		center_panel.add(left_panel);
		center_panel.add(right_panel);

		// ================= CONSOLE =================
		console_area = new JTextArea();
		console_area.setEditable(false);
		JPanel console_button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		run_button = new JButton("Run");
		save_button = new JButton("Save Images");
		console_button_panel.add(run_button);
		console_button_panel.add(save_button);

		JPanel console_panel = new JPanel(new BorderLayout(5, 5));
		console_panel.setBorder(BorderFactory.createTitledBorder("Console"));
		console_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		console_panel.add(console_button_panel, BorderLayout.SOUTH);

		JSplitPane vertical_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center_panel, console_panel);
		vertical_split.setDividerLocation(400);
		add(vertical_split, BorderLayout.CENTER);
	}
	
	public void init_actions() {
		// ADD
		add_button.addActionListener(e -> {
			table_model.addRow(new Object[]{
					table_model.getRowCount() + 1,
					toolbar_kernel_select.getSelectedItem(),
					toolbar_edge_select.getSelectedItem()
			});
			updateOperationCount();
		});

		// REMOVE
		remove_button.addActionListener(e -> {
			int row = operation_table.getSelectedRow();
			if (row != -1) {
				table_model.removeRow(row);
				reindexTable();
				updateOperationCount();
			}
		});

		// UP
		up_button.addActionListener(e -> {
			int row = operation_table.getSelectedRow();
			if (row > 0) {
				table_model.moveRow(row, row, row - 1);
				operation_table.setRowSelectionInterval(row - 1, row - 1);
				reindexTable();
			}
		});

		// DOWN
		down_button.addActionListener(e -> {
			int row = operation_table.getSelectedRow();
			if (row != -1 && row < table_model.getRowCount() - 1) {
				table_model.moveRow(row, row, row + 1);
				operation_table.setRowSelectionInterval(row + 1, row + 1);
				reindexTable();
			}
		});

		table_model.addActionListener(e -> {
			
		});
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Operation Sequence Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new OpSequence());
		frame.setSize(1100, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}