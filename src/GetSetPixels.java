import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class GetSetPixels {
    public static BufferedImage input_img, output_img;
    public static File f = null;
    public static int width, height;
    public static int[][] blur_kernel, gaussian_kernel, sharpen_kernel, emboss_kernel, outline_kernel, sobel_x, sobel_y, indentiy_kernel, edge_kernel;
    public static void init_kernels() {
        blur_kernel = new int[][] {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1},
        };

        gaussian_kernel = new int[][] {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1},
        };

        sharpen_kernel = new int[][] {
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0},
        };

        emboss_kernel = new int[][] {
                {-2, -1, 0},
                {-1, 1, 1},
                {0, 1, 2},
        };

        outline_kernel = new int[][] {
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1},
        };

        sobel_x = new int[][] {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1},
        };

        sobel_y = new int[][] {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1},
        };


        indentiy_kernel = new int[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
        };

        edge_kernel = new int[][] {
                {0, -1, 0},
                {-1, 4, -1},
                {0, -1, 0},
        };
    }
    public static void load_image(String filename) {
        File f = null;

        try {
            f = new File(filename);
            input_img = ImageIO.read(f);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        width = input_img.getWidth();
        height = input_img.getHeight();

        output_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    public static void save_image(String filename) {
        try {
            f = new File(filename);
            ImageIO.write(output_img, "png", f);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    public static int[] get_pixel(int x, int y) {
        int p = input_img.getRGB(x, y);

        int a = (p >> 24) & 0xff;
        int r = (p >> 16) & 0xff;
        int g = (p >> 8) & 0xff;
        int b = p & 0xff;

        // p = (a << 24) | (r << 16) | (g << 8) | b;
        // .setRGB(0, 0, p);

        return new int[] {r, g, b, a};
    }
    public static void set_pixel(int x, int y, int pixel[]) {
        int r = pixel[0];
        int g = pixel[1];
        int b = pixel[2];
        int a = pixel[3];

        int p = (a << 24) | (r << 16) | (g << 8) | b;
        output_img.setRGB(x, y, p);
    }

    public static void displayUI() {
        JFrame frame = new JFrame("Kernel Playground");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Image panel
        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        JLabel inputLabel = new JLabel(new ImageIcon(input_img));
        JLabel outputLabel = new JLabel(new ImageIcon(output_img));
        imagePanel.add(inputLabel);
        imagePanel.add(outputLabel);

        // Kernel selector
        String[] kernels = {
                "Identity",
                "Blur",
                "Gaussian",
                "Sharpen",
                "Emboss",
                "Outline",
                "Edge",
                "Sobel X",
                "Sobel Y"
        };

        JComboBox<String> kernelBox = new JComboBox<>(kernels);
        JButton applyButton = new JButton("Apply");

        JPanel controlPanel = new JPanel();
        controlPanel.add(kernelBox);
        controlPanel.add(applyButton);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(imagePanel, BorderLayout.CENTER);

        kernelBox.addActionListener(e -> {
            String selected = (String) kernelBox.getSelectedItem();

            // clear output
            output_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            switch (selected) {
                case "Blur" -> kernel_convolution(blur_kernel);
                case "Gaussian" -> kernel_convolution(gaussian_kernel);
                case "Sharpen" -> kernel_convolution(sharpen_kernel);
                case "Emboss" -> kernel_convolution(emboss_kernel);
                case "Outline" -> kernel_convolution(outline_kernel);
                case "Edge" -> kernel_convolution(edge_kernel);
                case "Sobel X" -> kernel_convolution(sobel_x);
                case "Sobel Y" -> kernel_convolution(sobel_y);
                default -> kernel_convolution(indentiy_kernel);
            }

            outputLabel.setIcon(new ImageIcon(output_img));
            frame.repaint();
        });

        frame.pack();
        frame.setVisible(true);
    }

    public static void kernel_convolution(int kernel[][]) {
        int kernel_width  = kernel[0].length;
        int kernel_height = kernel.length;

        for (int x = (kernel_width-1)/2; x < width - (kernel_width-1)/2; x++) {
            for (int y = (kernel_height-1)/2; y < height - (kernel_height-1)/2; y++) {
                int wps[] = {0, 0, 0, 0};

                for (int i = -(kernel_width-1)/2; i <= (kernel_width-1)/2; i++) {
                    for (int j = -(kernel_height-1)/2; j <= (kernel_height-1)/2; j++) {

                        int p[] = get_pixel(x + i, y + j);
                        int w = kernel[j+(kernel_height-1)/2][i+(kernel_width-1)/2];

                        wps[0] += w * p[0];
                        wps[1] += w * p[1];
                        wps[2] += w * p[2];
                        wps[3] += w * p[3];

                        if (x == 100 && y == 100) {
                            System.out.println(" w:" + w);
                            System.out.println("i=" + i + ", j=" + j + " x=" + (x + i) + ", y="+ (y + j) + "; p=" + p[0] + ", "+ p[1] + ", "+  p[2] + "; " + " wps=" + wps[0] + ", "+ wps[1] + ", "+  wps[2] + ", ");
                        }
                    }
                }

                if (x == 100 && y == 100) {
                    int p[] = get_pixel(x, y);
                    System.out.println("p " + p[0] + ", "+ p[1] + ", "+  p[2] + ", ");
                    System.out.println("wps " + wps[0] + ", "+ wps[1] + ", "+  wps[2] + ", ");
                }

                set_pixel(x, y, wps);
            }
        }
    }

    public static void main(String args[]) throws IOException {
        init_kernels();
        // load_image("./src/image2.png");
        load_image("./src/image1.jpg");
        displayUI();
    }
}