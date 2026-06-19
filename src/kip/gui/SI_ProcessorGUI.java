package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import kip.ImageProcessor;

public class SI_ProcessorGUI extends JPanel {
	public JFrame frame;
	public JLabel input_image_label, output_image_label;
	public JTable opseq_table;
	public DefaultTableModel opseq_model;
	public JLabel i_path_label, i_size_label, i_pixel_count_label;
	public JLabel u_size_label, u_pixel_count_label;
	public JTextField time_field;
	public JButton run_button, edit_sequence_button, select_image_button, save_image_button;

	public JTextArea console_area;
	public OpSequence operations_panel;
	private BufferedImage input_image, output_image;

	public SI_ProcessorGUI(JFrame frame_) {
		super(new BorderLayout()); 
		this.frame = frame_;
		init_components();
		create_single_panel();

		display_image(new File("./images/mona lisa-s.png"));
		operations_panel.operations.add(new Operation("Emboss", "Extend", ""));
		operations_panel.update_table();
		refresh_opseq_table();
	}

	public void init_components() {
		operations_panel = new OpSequence(); 
		opseq_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge method"}, 0);
		opseq_table = new JTable(opseq_model);
		opseq_table.setEnabled(false); 
		
		i_path_label = new JLabel("Path: None");
		i_size_label = new JLabel("Size: 0 x 0");
		i_pixel_count_label = new JLabel("Pixels: 0");

		u_size_label = new JLabel("Size: 0 x 0");
		u_pixel_count_label = new JLabel("Pixels: 0");

		save_image_button = new JButton("Save image");
		save_image_button.setEnabled(false);

		select_image_button = new JButton("Select Input Image...");
		
		TableColumn idColumn = opseq_table.getColumnModel().getColumn(0);
		idColumn.setMaxWidth(40);
	}
	
	private ImageIcon get_scaled_icons(BufferedImage src, JLabel targetLabel) {
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

		JPanel image_container = new JPanel(new GridLayout(1, 2, 10, 10));
		
		input_image_label = new JLabel("", SwingConstants.CENTER);
		output_image_label = new JLabel("", SwingConstants.CENTER);

		JScrollPane input_scroll = new JScrollPane(input_image_label);
		input_scroll.setPreferredSize(new Dimension(400, 450));
		
		JScrollPane output_scroll = new JScrollPane(output_image_label);
		output_scroll.setPreferredSize(new Dimension(400, 450));

		JPanel left_box = new JPanel(new BorderLayout(5, 5));
		left_box.setBorder(BorderFactory.createTitledBorder("Input"));
		left_box.add(input_scroll, BorderLayout.CENTER);

		JPanel left_controls = new JPanel(new BorderLayout(5, 5));
		JPanel metadata_panel = new JPanel(new GridLayout(3, 1));
		metadata_panel.add(i_path_label);
		metadata_panel.add(i_size_label);
		metadata_panel.add(i_pixel_count_label);
		
		left_controls.add(metadata_panel, BorderLayout.CENTER);
		left_controls.add(select_image_button, BorderLayout.SOUTH);
		left_box.add(left_controls, BorderLayout.SOUTH);

		JPanel right_box = new JPanel(new BorderLayout(5, 5));
		right_box.setBorder(BorderFactory.createTitledBorder("Input"));
		right_box.add(output_scroll, BorderLayout.CENTER);

		JPanel right_controls = new JPanel(new BorderLayout(5, 5));
		JPanel r_metadata_panel = new JPanel(new GridLayout(3, 1));
		r_metadata_panel.add(u_size_label);
		r_metadata_panel.add(u_pixel_count_label);
		
		right_controls.add(r_metadata_panel, BorderLayout.CENTER);
		right_controls.add(save_image_button, BorderLayout.SOUTH);
		right_box.add(right_controls, BorderLayout.SOUTH);


		image_container.add(left_box);
		image_container.add(right_box);

		JPanel side_panel = new JPanel(new BorderLayout(5, 5));
		side_panel.setPreferredSize(new Dimension(220, 0));
		side_panel.setBorder(BorderFactory.createTitledBorder("Sequence Summary"));
		edit_sequence_button = new JButton("Edit Sequence...");
		side_panel.add(new JScrollPane(opseq_table), BorderLayout.CENTER);
		side_panel.add(edit_sequence_button, BorderLayout.SOUTH);

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

		console_area = new JTextArea();
		console_area.setEditable(false);
		JPanel console_panel = new JPanel(new BorderLayout(5, 5));
		console_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		console_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		console_panel.add(bottom_bar, BorderLayout.SOUTH);

		center_panel.add(image_container, BorderLayout.CENTER);
		center_panel.add(side_panel, BorderLayout.EAST);
		
		JSplitPane vertical_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center_panel, console_panel);
		vertical_split.setDividerLocation(550);
		vertical_split.setResizeWeight(0.8); 
		
		add(vertical_split, BorderLayout.CENTER);

		edit_sequence_button.addActionListener(e -> show_editor_dialog());
		select_image_button.addActionListener(e -> open_image_action());

		input_image_label.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				if (input_image != null) {
					input_image_label.setIcon(get_scaled_icons(input_image, input_image_label));
				}
			}
		});

		run_button.addActionListener((e) -> {
			run_operations();
		});

		save_image_button.addActionListener((e) -> {
			JFileChooser chooser = new JFileChooser("./images");
			chooser.setSelectedFile(new File("./output.png"));
			
			int option = chooser.showSaveDialog(frame);
			if(option == JFileChooser.APPROVE_OPTION){
				File file = chooser.getSelectedFile();
				ImageProcessor.save_image(file.getAbsolutePath());
			}else{
				console_area.append("Save command canceled" + "\n");
			}
		});
	}

	private void open_image_action() {
		JFileChooser chooser = new JFileChooser("./images");
		chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "bmp", "jpeg"));
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			display_image(chooser.getSelectedFile());
		}
	}

	private void display_image(File file) {
		save_image_button.setEnabled(false); 
		input_image_label.setIcon(null);
		input_image_label.setText("Loading: " + file.getName() + "...");
		input_image_label.paintImmediately(input_image_label.getVisibleRect());

		try {
			BufferedImage img = ImageIO.read(file);
			if (img != null) {
				input_image = img;
				input_image_label.setText("");
				input_image_label.setIcon(get_scaled_icons(img, input_image_label));
				output_image_label.setIcon(null);

				i_path_label.setText("Path: " + file.getName());
				i_size_label.setText("Size: " + img.getWidth() + " x " + img.getHeight());
				i_pixel_count_label.setText("Pixels: " + (long)img.getWidth() * img.getHeight());
				console_area.append("Loaded: " + file.getAbsolutePath() + "\n");

				ImageProcessor.set_input_img(input_image);
			}
		} catch (Exception ex) {
			input_image_label.setText("Error loading image.");
			console_area.append("Error: " + ex.getMessage() + "\n");
		}
	}

	private void show_editor_dialog() {
		JDialog dialog = new JDialog(frame, "Operation Sequence Editor", true);
		dialog.add(operations_panel);
		dialog.setSize(1000, 600);
		dialog.setLocationRelativeTo(frame);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) { refresh_opseq_table(); }
		});
		dialog.setVisible(true);
	}

	private void refresh_opseq_table() {
		opseq_model.setRowCount(0);
		int i = 1;
		for (Operation op : operations_panel.getOperations()) {
			opseq_model.addRow(new Object[]{i++, op.kernel, op.edge});
		}
	}

	private void run_operations() {
		save_image_button.setEnabled(false);
		output_image_label.setIcon(null);
		output_image_label.setText("Processing sequence...");
		output_image_label.paintImmediately(output_image_label.getVisibleRect());

		console_area.append("running" + "\n");
		ArrayList<Operation> op_list = operations_panel.getOperations();
		BufferedImage current_image = input_image;

		long totalStartTime = System.currentTimeMillis();

		for (Operation op: op_list) {
			console_area.append("running " + op.kernel + " " + op.edge + "\n");
			
			BufferedImage out;
			
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
			
			long opStartTime = System.currentTimeMillis();

			current_image = ImageProcessor.kernel_convolution(current_image, kernelToUse, em);
			
			long opEndTime = System.currentTimeMillis();
			console_area.append("kernel convolution " + op.kernel + ": " + (opEndTime - opStartTime) + " ms\n");
		}

		long totalEndTime = System.currentTimeMillis();
		long totalTime = totalEndTime - totalStartTime;
		time_field.setText(String.valueOf(totalTime));
		console_area.append("Total time: " + totalTime + " ms\n\n");

		output_image = current_image;
		output_image_label.setIcon(get_scaled_icons(output_image, output_image_label));

		output_image_label.setText(""); 
		output_image_label.setIcon(get_scaled_icons(output_image, output_image_label));

		u_size_label.setText("Size: " + output_image.getWidth() + " x " + output_image.getHeight());
		u_pixel_count_label.setText("Pixels: " + (long) output_image.getWidth() * output_image.getHeight());
		save_image_button.setEnabled(true);
	}
}