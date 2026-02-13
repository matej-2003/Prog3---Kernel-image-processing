package kip.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class OpSequence extends JPanel {
	public JTable operation_table;
	public DefaultTableModel table_model;
	public JButton add_button, remove_button, up_button, down_button;

	public JComboBox<String> toolbar_kernel_select, toolbar_edge_select;
	public JComboBox<String> settings_kernel_select, settings_edge_select;
	public JLabel operation_count_label, estimated_ops_label;
	public JTextArea custom_kernel_area, console_area;
	public JButton run_button, save_button;

	public String kernel_list[] = {"Blur", "Identity", "Gaussian", "Sharpen", "Emboss", "Outline", "Edge", "Sobel X", "Sobel Y", "Custom"};
	public String edge_list[] = {"Extend", "Wrap", "Mirror"};


	// Ključni seznam operacij
	public ArrayList<Operation> operations;
	public boolean isUpdating = false; // Flag za preprečevanje neskončnih zank pri posodabljanju GUI

	public OpSequence() {
		operations = new ArrayList<>();
		init_components();
		init_actions();
	}

	// Metoda, ki jo potrebuje drug razred
	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void reindexTable() {
		for (int i = 0; i < table_model.getRowCount(); i++) {
			table_model.setValueAt(i + 1, i, 0);
		}
	}

	public void updateOperationCount() {
		operation_count_label.setText("Operations in sequence: " + operations.size());
	}

	/**
	 * Naloži nastavitve izbrane operacije v desni panel
	 */
	public void loadSettingsToPanel() {
		int row = operation_table.getSelectedRow();
		if (row == -1) return;

		isUpdating = true; // Onemogoči sprožanje listenerjev med nalaganjem
		Operation op = operations.get(row);
		settings_kernel_select.setSelectedItem(op.kernel);
		settings_edge_select.setSelectedItem(op.edge);
		custom_kernel_area.setText(op.custom_kernel);
		isUpdating = false;
	}

	public void init_components() {
		setLayout(new BorderLayout(5, 5));

		table_model = new DefaultTableModel(new Object[]{"#", "Kernel", "Edge handling"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		operation_table = new JTable(table_model);
		operation_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		TableColumn idColumn = operation_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);

		toolbar_kernel_select = new JComboBox<>(kernel_list);
		toolbar_edge_select = new JComboBox<>(edge_list);
		settings_kernel_select = new JComboBox<>(kernel_list);
		settings_edge_select = new JComboBox<>(edge_list);

		custom_kernel_area = new JTextArea(10, 12);
		custom_kernel_area.setEditable(false);
		operation_count_label = new JLabel("Operations in sequence: 0");
		estimated_ops_label = new JLabel("Estimated total operations: 0");

		JPanel center_panel = new JPanel(new GridLayout(1, 2, 5, 5));

		// LEFT PANEL
		JPanel left_panel = new JPanel(new BorderLayout(6, 5));
		left_panel.setBorder(BorderFactory.createTitledBorder("Operation Sequence"));
		left_panel.add(new JScrollPane(operation_table), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
		add_button = new JButton(new ImageIcon("./icons/plus.png"));
		remove_button = new JButton(new ImageIcon("./icons/cross.png"));
		up_button = new JButton(new ImageIcon("./icons/arrow-up.png"));
		down_button = new JButton(new ImageIcon("./icons/arrow-down.png"));

		toolbar.add(toolbar_kernel_select);
		toolbar.add(toolbar_edge_select);
		toolbar.add(add_button);
		toolbar.add(remove_button);
		toolbar.add(up_button);
		toolbar.add(down_button);
		left_panel.add(toolbar, BorderLayout.SOUTH);

		// RIGHT PANEL
		JPanel right_panel = new JPanel(new BorderLayout(5, 5));
		right_panel.setBorder(BorderFactory.createTitledBorder("Settings & Info"));

		JPanel settings_panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
		settings_panel.add(new JLabel("Kernel:"), gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		settings_panel.add(settings_kernel_select, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
		settings_panel.add(new JLabel("Edge handling:"), gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		settings_panel.add(settings_edge_select, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		settings_panel.add(new JLabel("Custom kernel:"), gbc);

		gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		settings_panel.add(new JScrollPane(custom_kernel_area), gbc);

		JPanel info_panel = new JPanel();
		info_panel.setLayout(new BoxLayout(info_panel, BoxLayout.Y_AXIS));
		info_panel.add(operation_count_label);
		info_panel.add(Box.createVerticalStrut(5));
		info_panel.add(estimated_ops_label);

		JPanel right_top = new JPanel(new BorderLayout(5, 5));
		right_top.add(settings_panel, BorderLayout.NORTH);
		right_top.add(info_panel, BorderLayout.CENTER);
		right_panel.add(right_top, BorderLayout.NORTH);

		center_panel.add(left_panel);
		center_panel.add(right_panel);

		// CONSOLE
		/*
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
		*/

		add(center_panel, BorderLayout.CENTER);
	}

	public void init_actions() {
		// Inside add_button listener
		add_button.addActionListener(e -> {
			String k = (String) toolbar_kernel_select.getSelectedItem();
			String ed = (String) toolbar_edge_select.getSelectedItem();
			
			operations.add(new Operation(k, ed, ""));
			table_model.addRow(new Object[]{operations.size(), k, ed});
			
			// ADD THIS: Auto-select the new row
			int lastRow = table_model.getRowCount() - 1;
			operation_table.setRowSelectionInterval(lastRow, lastRow);
			
			updateOperationCount();
		});

		remove_button.addActionListener(e -> {
			int[] selectedRows = operation_table.getSelectedRows();
			
			if (selectedRows.length > 0) {
				for (int i = selectedRows.length - 1; i >= 0; i--) {
					int actualRow = selectedRows[i];
					operations.remove(actualRow);
					table_model.removeRow(actualRow);
				}
				reindexTable();
				updateOperationCount();
			} else {
				JOptionPane.showMessageDialog(this, "Please select at least one operation to remove.");
			}
		});

		// UP
		up_button.addActionListener(e -> {
			int row = operation_table.getSelectedRow();
			if (row > 0) {
				Collections.swap(operations, row, row - 1);
				table_model.moveRow(row, row, row - 1);
				operation_table.setRowSelectionInterval(row - 1, row - 1);
				reindexTable();
			}
		});

		// DOWN
		down_button.addActionListener(e -> {
			int row = operation_table.getSelectedRow();
			if (row != -1 && row < operations.size() - 1) {
				Collections.swap(operations, row, row + 1);
				table_model.moveRow(row, row, row + 1);
				operation_table.setRowSelectionInterval(row + 1, row + 1);
				reindexTable();
			}
		});

		// Klik na vrstico v tabeli -> naloži nastavitve na desno
		operation_table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadSettingsToPanel();
			}
		});

		ActionListener settingsUpdater = e -> {
			// IF WE ARE CURRENTLY LOADING A ROW, DO NOT OVERWRITE DATA
			if (isUpdating) return; 

			int row = operation_table.getSelectedRow();
			if (row != -1) {
				Operation op = operations.get(row);
				op.kernel = (String) settings_kernel_select.getSelectedItem();
				op.edge = (String) settings_edge_select.getSelectedItem();
				
				table_model.setValueAt(op.kernel, row, 1);
				table_model.setValueAt(op.edge, row, 2);

				if (op.kernel.equals("Custom")) {
					custom_kernel_area.setEditable(true);
				} else {
					custom_kernel_area.setEditable(false);
				}
			}
		};

		// Apply to dropdowns
		settings_kernel_select.addActionListener(settingsUpdater);
		settings_edge_select.addActionListener(settingsUpdater);

		custom_kernel_area.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { checkCustom(); }
			public void removeUpdate(DocumentEvent e) { checkCustom(); }
			public void changedUpdate(DocumentEvent e) { checkCustom(); }
			
			public void checkCustom() {
				// 1. If we are currently loading a row from the table, do nothing
				if (isUpdating) return; 

				int row = operation_table.getSelectedRow();
				if (row != -1) {
					// 2. Update the custom_kernel string in our object
					operations.get(row).custom_kernel = custom_kernel_area.getText();

					// 3. If the user typed something, switch the dropdown to "Custom"
					// We check !isEmpty to avoid switching if the field was just cleared
					if (!custom_kernel_area.getText().trim().isEmpty()) {
						if (!settings_kernel_select.getSelectedItem().equals("Custom")) {
							settings_kernel_select.setSelectedItem("Custom");
						}
					}
				}
			}
		});

		
		// Inside init_actions()
		operation_table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				updateSettingsFromSelection();
			}
		});
	}

	public void updateSettingsFromSelection() {
		int row = operation_table.getSelectedRow();
		if (row == -1) return;

		// 1. SET FLAG TO TRUE
		isUpdating = true; 

		Operation op = operations.get(row);
		settings_kernel_select.setSelectedItem(op.kernel);
		settings_edge_select.setSelectedItem(op.edge);
		custom_kernel_area.setText(op.custom_kernel);

		// 2. SET FLAG TO FALSE
		isUpdating = false; 
	}

	public void update_table() {
		for (int i = 0; i < operations.size(); i++) {
			Operation op = operations.get(i);
			table_model.addRow(new Object[] {i, op.kernel, op.edge});
		}
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