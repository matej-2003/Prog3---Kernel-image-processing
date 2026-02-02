import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame {

    JLabel inputLabel = new JLabel();
    JLabel outputLabel = new JLabel();
    JLabel infoLabel = new JLabel("No image selected");

    JPanel thumbPanel = new JPanel();
    JScrollPane scrollPane;

    JComboBox<String> kernelBox;

    File imageFolder = new File("./data");

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
                "Emboss", "Outline", "Edge", "Sobel X", "Sobel Y"
        };

        kernelBox = new JComboBox<>(kernels);

        JPanel controlPanel = new JPanel();
        controlPanel.add(kernelBox);
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

    void loadMainImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            inputLabel.setIcon(new ImageIcon(img));
            outputLabel.setIcon(null);

            GetSetPixels.input_img = img;
            GetSetPixels.width = img.getWidth();
            GetSetPixels.height = img.getHeight();
            GetSetPixels.output_img = new BufferedImage(
                    GetSetPixels.width,
                    GetSetPixels.height,
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
        kernelBox.addActionListener(e -> {
            String selected = (String) kernelBox.getSelectedItem();

            GetSetPixels.output_img = new BufferedImage(
                    GetSetPixels.width,
                    GetSetPixels.height,
                    BufferedImage.TYPE_INT_ARGB
            );

            switch (selected) {
                case "Blur" -> GetSetPixels.kernel_convolution(GetSetPixels.blur_kernel);
                case "Gaussian" -> GetSetPixels.kernel_convolution(GetSetPixels.gaussian_kernel);
                case "Sharpen" -> GetSetPixels.kernel_convolution(GetSetPixels.sharpen_kernel);
                case "Emboss" -> GetSetPixels.kernel_convolution(GetSetPixels.emboss_kernel);
                case "Outline" -> GetSetPixels.kernel_convolution(GetSetPixels.outline_kernel);
                case "Edge" -> GetSetPixels.kernel_convolution(GetSetPixels.edge_kernel);
                case "Sobel X" -> GetSetPixels.kernel_convolution(GetSetPixels.sobel_x);
                case "Sobel Y" -> GetSetPixels.kernel_convolution(GetSetPixels.sobel_y);
                default -> GetSetPixels.kernel_convolution(GetSetPixels.indentiy_kernel);
            }

            outputLabel.setIcon(new ImageIcon(GetSetPixels.output_img));
        });
    }

    public static void main(String[] args) {
        GetSetPixels.init_kernels();
        new Main();
    }
}
