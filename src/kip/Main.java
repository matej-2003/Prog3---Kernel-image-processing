package kip;

import javax.swing.*;

public class Main extends JFrame {
/*
	JLabel inputLabel = new JLabel();
	JButton applyBtn = new JButton("Apply");
	JLabel outputLabel = new JLabel();
	JLabel infoLabel = new JLabel("No image selected");

	JPanel thumbPanel = new JPanel();
	JScrollPane scrollPane;

	JComboBox<String> kernelBox;

	File imageFolder = new File("./data/");
	JButton kernelEditorButton = new JButton("Custom Kernel");
	int[][] customKernel = ImageProcessor.indentiy_kernel;

	public Main() {
		setTitle("Image Kernel Lab");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// ===== LEFT: thumbnails =====
		thumbPanel.setLayout(new BoxLayout(thumbPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(thumbPanel);
		scrollPane.setPreferredSize(new Dimension(120, 500));
		add(scrollPane, BorderLayout.WEST);

		// ===== CENTER: images =====
		JPanel imagePanel = new JPanel(new GridLayout(1, 2));
		inputLabel.setHorizontalAlignment(JLabel.CENTER);
		outputLabel.setHorizontalAlignment(JLabel.CENTER);
		imagePanel.add(inputLabel);
		imagePanel.add(outputLabel);
		add(imagePanel, BorderLayout.CENTER);

		// ===== TOP: controls =====
		String[] kernels = {
				"Identity", "Blur", "Gaussian", "Sharpen",
				"Emboss", "Outline", "Edge", "Sobel X", "Sobel Y",
				"Custom"
		};


		kernelBox = new JComboBox<>(kernels);

		JPanel controlPanel = new JPanel();
		controlPanel.add(kernelBox);
		controlPanel.add(kernelEditorButton);
		controlPanel.add(applyBtn);
		add(controlPanel, BorderLayout.NORTH);


		// ===== BOTTOM: info =====
		infoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		add(infoLabel, BorderLayout.SOUTH);

		loadThumbnails();
		setupActions();

		setSize(1000, 650);
		setVisible(true);
	}

	void loadThumbnails() {
		File[] files = imageFolder.listFiles();
		if (files == null) return;

		for (File file : files) {
			try {
				BufferedImage img = ImageIO.read(file);
				if (img == null) continue;

				Image thumb = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
				JLabel thumbLabel = new JLabel(new ImageIcon(thumb));
				thumbLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				thumbLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

				thumbLabel.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						loadMainImage(file);
					}
				});

				thumbPanel.add(thumbLabel);

			} catch (IOException e) {
				System.out.println("Failed: " + file.getName());
			}
		}
	}

	public void setCustomKernel(int[][] kernel) {
		customKernel = kernel;
	}

	void loadMainImage(File file) {
		try {
			BufferedImage img = ImageIO.read(file);
			inputLabel.setIcon(new ImageIcon(img));
			outputLabel.setIcon(null);

			ImageProcessor.input_img = img;
			ImageProcessor.width = img.getWidth();
			ImageProcessor.height = img.getHeight();
			ImageProcessor.output_img = new BufferedImage(
					ImageProcessor.width,
					ImageProcessor.height,
					BufferedImage.TYPE_INT_ARGB
			);

			long kb = file.length() / 1024;

			infoLabel.setText(
					"File: " + file.getName() +
							" | Size: " + img.getWidth() + "x" + img.getHeight() +
							" | Disk: " + kb + " KB"
			);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void setupActions() {
		kernelBox.addActionListener(e -> apply_kernel(ImageProcessor::get_edge_extend));
		applyBtn.addActionListener(e -> apply_kernel(ImageProcessor::get_edge_extend));

		kernelEditorButton.addActionListener(e -> {
			new KernelEditorUI(this);
		});
	}

	public void apply_kernel(ImageProcessor.EdgeHandler edge_handler) {
		String selected = (String) kernelBox.getSelectedItem();

		ImageProcessor.output_img = new BufferedImage(
				ImageProcessor.width,
				ImageProcessor.height,
				BufferedImage.TYPE_INT_ARGB
		);

		long startTime = System.nanoTime();
		switch (selected) {
			case "Blur" -> ImageProcessor.kernel_convolution(ImageProcessor.blur_kernel, edge_handler);
			case "Gaussian" -> ImageProcessor.kernel_convolution(ImageProcessor.gaussian_kernel, edge_handler);
			case "Sharpen" -> ImageProcessor.kernel_convolution(ImageProcessor.sharpen_kernel, edge_handler);
			case "Emboss" -> ImageProcessor.kernel_convolution(ImageProcessor.emboss_kernel, edge_handler);
			case "Outline" -> ImageProcessor.kernel_convolution(ImageProcessor.outline_kernel, edge_handler);
			case "Edge" -> ImageProcessor.kernel_convolution(ImageProcessor.edge_kernel, edge_handler);
			case "Sobel X" -> ImageProcessor.kernel_convolution(ImageProcessor.sobel_x, edge_handler);
			case "Sobel Y" -> ImageProcessor.kernel_convolution(ImageProcessor.sobel_y, edge_handler);
			case "Custom" -> ImageProcessor.kernel_convolution(customKernel, edge_handler);
			default -> ImageProcessor.kernel_convolution(ImageProcessor.indentiy_kernel, edge_handler);
		}

		long stopTime = System.nanoTime();
		System.out.println(stopTime - startTime);
		outputLabel.setIcon(new ImageIcon(ImageProcessor.output_img));
	}

	public static void main(String[] args) {
		ImageProcessor.init_kernels();
		new Main();
	}
		*/
}
