package kip.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.table.*;
import kip.ImageProcessor;

public class SI_ParallelProcessorGUI extends JPanel {
	public JFrame frame;
	public JLabel input_image_label, output_image_label;
	
	// The new specialized component
	public ChunkDisplayPanel chunks_viewer; 
	
	public JTable summary_table;
	public DefaultTableModel summary_model;
	public JButton run_button, edit_sequence_button;
	public JSpinner thread_count_spinner;
	public JTextArea console_area;
	public OpSequence editor_panel;

	public SI_ParallelProcessorGUI(JFrame frame_) {
		super(new BorderLayout()); 
		this.frame = frame_;
		init_components();
		create_parallel_panel();
	}

	public void init_components() {
		editor_panel = new OpSequence(); 
		chunks_viewer = new ChunkDisplayPanel();
		
		summary_model = new DefaultTableModel(new Object[]{"#", "Operation", "Edge"}, 0);
		summary_table = new JTable(summary_model);
		summary_table.setEnabled(false);

		thread_count_spinner = new JSpinner(new SpinnerNumberModel(4, 1, 128, 1));
	}

	public void create_parallel_panel() {
		JPanel center_panel = new JPanel(new BorderLayout(10, 10));

		// ===== IMAGES (3 Columns) =====
		JPanel image_container = new JPanel(new GridLayout(1, 3, 10, 10));
		
		BufferedImage img;
		try {
			// Load the image using your processor's logic for consistency
			ImageProcessor.load_image("./data/mona lisa.jpg");
			img = ImageProcessor.input_img;
		} catch (Exception e) {
			img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		}

		// Column 1: Input
		input_image_label = new JLabel(getScaledIcon(img, 350, 450));
		JPanel left_box = createTitledBox("Input", input_image_label);

		// Column 2: The Chunks (Middle)
		// Add a scroll pane in case there are many threads (e.g., 64 threads)
		JScrollPane middle_scroll = new JScrollPane(chunks_viewer);
		middle_scroll.setBorder(null);
		JPanel middle_box = createTitledBox("Thread Chunks (Work Units)", middle_scroll);
		
		// Populate the chunks (Example with 12 threads)

		// Column 3: Output
		output_image_label = new JLabel(getScaledIcon(img, 350, 450));
		JPanel right_box = createTitledBox("Output Preview", output_image_label);

		image_container.add(left_box);
		image_container.add(middle_box);
		image_container.add(right_box);

		// ===== SEQUENCE SUMMARY (Right Side) =====
		JPanel side_panel = new JPanel(new BorderLayout(5, 5));
		side_panel.setPreferredSize(new Dimension(200, 0));
		side_panel.setBorder(BorderFactory.createTitledBorder("Sequence"));
		side_panel.add(new JScrollPane(summary_table), BorderLayout.CENTER);
		edit_sequence_button = new JButton("Edit Sequence");
		side_panel.add(edit_sequence_button, BorderLayout.SOUTH);

		// ===== CONSOLE (Bottom) =====
		console_area = new JTextArea(8, 20);
		JPanel console_panel = new JPanel(new BorderLayout());
		console_panel.setBorder(BorderFactory.createTitledBorder("Console Log"));
		console_panel.add(new JScrollPane(console_area), BorderLayout.CENTER);
		
		run_button = new JButton("Run Parallel Process");
		JPanel btn_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btn_panel.add(new JLabel("Threads: ")); // Add a simple label
		btn_panel.add(thread_count_spinner);    // Add the spinner
		btn_panel.add(Box.createHorizontalStrut(10)); // Add a little spacing
		btn_panel.add(run_button);
		console_panel.add(btn_panel, BorderLayout.SOUTH);

		// Assembly
		center_panel.add(image_container, BorderLayout.CENTER);
		center_panel.add(side_panel, BorderLayout.EAST);
		
		JSplitPane main_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center_panel, console_panel);
		main_split.setDividerLocation(500);
		main_split.setResizeWeight(0.8);

		add(main_split, BorderLayout.CENTER);
	}

	private JPanel createTitledBox(String title, Component comp) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder(title));
		JPanel wrapper = new JPanel(new GridBagLayout());
		wrapper.add(comp);
		p.add(wrapper, BorderLayout.CENTER);
		return p;
	}

	private ImageIcon getScaledIcon(BufferedImage src, int maxWidth, int maxHeight) {
		double ratio = Math.min((double) maxWidth / src.getWidth(), (double) maxHeight / src.getHeight());
		int width = (int) (src.getWidth() * ratio);
		int height = (int) (src.getHeight() * ratio);
		return new ImageIcon(src.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
}