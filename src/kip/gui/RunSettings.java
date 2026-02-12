package kip.gui;

import java.awt.*;
import javax.swing.*;

public class RunSettings extends JPanel {

	public JComboBox<String> mode_select;
	public JSpinner thread_spinner;
	public JComboBox<String> scheduling_select;
	public JCheckBox use_tiling_checkbox;
	public JTextField tile_size_field;
	
	private JLabel cpu_info_label;

	public String modes[] = { "Single-threaded", "Multi-threaded (Fixed Pool)", "Multi-threaded (Work Stealing)" };
	public String scheduling_types[] = { "Static", "Dynamic", "Guided" };

	public RunSettings() {
		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createTitledBorder("Execution Settings"));

		JPanel form_panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 8, 5, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// --- Row 0: Execution Mode ---
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
		form_panel.add(new JLabel("Mode:"), gbc);
		
		mode_select = new JComboBox<>(modes);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form_panel.add(mode_select, gbc);

		// --- Row 1: Thread Count ---
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
		form_panel.add(new JLabel("Threads:"), gbc);

		int cores = Runtime.getRuntime().availableProcessors();
		// Spinner: value, min, max, step
		SpinnerModel model = new SpinnerNumberModel(cores, 1, cores * 2, 1);
		thread_spinner = new JSpinner(model);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form_panel.add(thread_spinner, gbc);

		// --- Row 2: Scheduling ---
		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
		form_panel.add(new JLabel("Scheduling:"), gbc);

		scheduling_select = new JComboBox<>(scheduling_types);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form_panel.add(scheduling_select, gbc);

		// --- Row 3: Tiling / Granularity ---
		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
		use_tiling_checkbox = new JCheckBox("Use Image Tiling");
		form_panel.add(use_tiling_checkbox, gbc);

		tile_size_field = new JTextField("128");
		tile_size_field.setEnabled(false); // Enable only if checkbox is ticked
		gbc.gridx = 1; gbc.weightx = 1.0;
		form_panel.add(tile_size_field, gbc);

		// --- Info Area ---
		JPanel info_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cpu_info_label = new JLabel("Detected Logical Cores: " + cores);
		cpu_info_label.setFont(new Font("SansSerif", Font.ITALIC, 11));
		info_panel.add(cpu_info_label);

		add(form_panel, BorderLayout.CENTER);
		add(info_panel, BorderLayout.SOUTH);

		// Simple interaction logic
		init_actions();
	}

	private void init_actions() {
		// Toggle thread count based on mode
		mode_select.addActionListener(e -> {
			boolean isParallel = !mode_select.getSelectedItem().equals("Single-threaded");
			thread_spinner.setEnabled(isParallel);
			scheduling_select.setEnabled(isParallel);
		});

		// Toggle tile field
		use_tiling_checkbox.addActionListener(e -> {
			tile_size_field.setEnabled(use_tiling_checkbox.isSelected());
		});
	}
}