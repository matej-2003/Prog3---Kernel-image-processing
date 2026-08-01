package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import kip.ImageProcessor;

public class MI_ProcessorGUI extends JPanel {
	public JFrame frame;
	public DefaultListModel<ImageEntry> image_list_model;
	public JList<ImageEntry> image_list;
	public JButton add_img_btn, remove_img_btn;
	
	public JTextField output_path_field;
	public JButton browse_output_btn;

	public JTable opseq_table;
	public DefaultTableModel opseq_model;
	public JButton edit_sequence_button, run_button;
	public JTextArea console_area;

	
	public OpSequence operations_panel;

	public MI_ProcessorGUI(JFrame frame_) {
		super(new BorderLayout(10, 10));
		this.frame = frame_;
		init_components();

		image_list_model.addElement(new ImageEntry(new File("./images/landscape-s.jpg")));
		image_list_model.addElement(new ImageEntry(new File("./images/mountain-s.jpg")));
		image_list_model.addElement(new ImageEntry(new File("./images/night-s.jpg")));
		image_list_model.addElement(new ImageEntry(new File("./images/orange-s.jpg")));
		image_list_model.addElement(new ImageEntry(new File("./images/river-s.jpg")));

		create_layout();
		attach_listeners();

		operations_panel.operations.add(new Operation("", "Extend", ""));
		operations_panel.update_table();
		refresh_opseq_table();
	}

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

	private void refresh_opseq_table() {
		opseq_model.setRowCount(0);
		int i = 1;
		for (Operation op : operations_panel.getOperations()) {
			opseq_model.addRow(new Object[]{i++, op.kernel, op.edge});
		}
	}

	private void init_components() {
		operations_panel = new OpSequence(); 
		
		image_list_model = new DefaultListModel<>();
		image_list = new JList<>(image_list_model);
		image_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		image_list.setCellRenderer(new ImageListRenderer()); 
		
		add_img_btn = new JButton("Add Images...");
		remove_img_btn = new JButton("Remove Selected");

		output_path_field = new JTextField("./images/processed/");
		browse_output_btn = new JButton("Browse...");

		opseq_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge"}, 0);
		opseq_table = new JTable(opseq_model);
		opseq_table.setEnabled(false);
		edit_sequence_button = new JButton("Edit Sequence");

		run_button = new JButton("Run Process");
		console_area = new JTextArea(8, 20);

		TableColumn idColumn = opseq_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
		idColumn.setPreferredWidth(30);
	}

	private void attach_listeners() {
		add_img_btn.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser("./images");
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

		
		edit_sequence_button.addActionListener(e -> {
			JDialog dialog = new JDialog(frame, "Edit Processing Sequence", true);
			dialog.getContentPane().add(operations_panel);
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			dialog.setVisible(true);
			
			
			syncSequenceTable();
		});

		run_button.addActionListener((e) -> {
			run_operations();
		});
	}

	private void syncSequenceTable() {
		
		opseq_model.setRowCount(0);
		int i = 1;
		for (Operation op : operations_panel.getOperations()) {
			opseq_model.addRow(new Object[]{i++, op.kernel, op.edge});
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
		JPanel config_panel = new JPanel(new GridLayout(1, 2, 10, 10));

		JPanel left_panel = new JPanel(new BorderLayout(5, 5));
		left_panel.setBorder(BorderFactory.createTitledBorder("Input Images (Gallery)"));
		left_panel.add(new JScrollPane(image_list), BorderLayout.CENTER);
		JPanel img_btn_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		img_btn_panel.add(add_img_btn); img_btn_panel.add(remove_img_btn);
		left_panel.add(img_btn_panel, BorderLayout.SOUTH);

		JPanel right_panel = new JPanel(new BorderLayout(5, 5));
		right_panel.setBorder(BorderFactory.createTitledBorder("Sequence"));
		right_panel.add(new JScrollPane(opseq_table), BorderLayout.CENTER);
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
		run_bar.add(run_button);
		bottom_panel.add(run_bar, BorderLayout.SOUTH);

		JSplitPane main_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top_container, bottom_panel);
		main_split.setDividerLocation(400);
		add(main_split, BorderLayout.CENTER);
	}

	private void run_operations() {
		ArrayList<Operation> op_list = operations_panel.getOperations();
		long totalStartTime = System.currentTimeMillis();
		console_area.append("Starting process...\n");

		for (int i = 0; i < image_list_model.size(); i++) {
			ImageEntry elem = image_list_model.get(i);

			BufferedImage current_image = null;
			try {
				current_image = ImageIO.read(elem.file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if (current_image == null) continue;

			for (Operation op: op_list) {
				System.out.println(elem.file.getAbsolutePath() + " " + op.kernel + " " + op.edge);
				
				ImageProcessor.EdgeMethod em = ImageProcessor.EdgeMethod.EXTEND;
				if (op.edge.equals("Wrap")) em = ImageProcessor.EdgeMethod.WRAP;
				if (op.edge.equals("Mirror")) em = ImageProcessor.EdgeMethod.MIRROR;
	
				float[][] kernelToUse;
				switch (op.kernel) {
					case "Blur" -> kernelToUse = ImageProcessor.blur_kernel;
					case "Gaussian" -> kernelToUse = ImageProcessor.gaussian_kernel;
					case "Sharpen" -> kernelToUse = ImageProcessor.sharpen_kernel;
					case "Emboss" -> kernelToUse = ImageProcessor.emboss_kernel;
					case "Outline" -> kernelToUse = ImageProcessor.outline_kernel;
					case "Edge" -> kernelToUse = ImageProcessor.edge_kernel;
					case "Sobel X" -> kernelToUse = ImageProcessor.sobel_x;
					case "Sobel Y" -> kernelToUse = ImageProcessor.sobel_y;
					case "Custom" -> kernelToUse = op.getCustomKernel();
					default -> kernelToUse = ImageProcessor.indentiy_kernel;
				}
				long imageStartTime = System.currentTimeMillis();
				console_area.append("Processing: " + elem.file.getName() + "...");
				current_image = ImageProcessor.kernel_convolution(current_image, kernelToUse, em);
				console_area.append(" Done (" + (System.currentTimeMillis() - imageStartTime) + " ms)\n");
			}

			String file_name = "processed_" + elem.file.getName();
			if (file_name.contains(".")) {
				file_name = file_name.substring(0, file_name.lastIndexOf('.')) + ".png";
			} else {
				file_name += ".png";
			}
			String output_dir = output_path_field.getText();
			File dir = new File(output_dir);
			File out_file = new File(dir, file_name);
			
			try {
				ImageIO.write(current_image, "png", out_file);
				console_area.append("Saved to: " + out_file.getAbsolutePath() + "\n");
			} catch (IOException e) {
				console_area.append("Failed to save: " + file_name + "\n");
			}
		}

		long totalEndTime = System.currentTimeMillis();
		console_area.append("\nTotal time: " + (totalEndTime - totalStartTime) + " ms\n");
	}
}