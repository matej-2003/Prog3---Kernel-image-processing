package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI {

    public static JFrame frame;

    public static JMenuBar menu_bar;
    public static JMenu menu, submenu;
    public static JMenuItem i1, i2, i3, i4, i5;

    public static String kernel_list[] = {
        "Identity", "Blur", "Gaussian", "Sharpen", "Emboss",
        "Outline", "Edge", "Sobel X", "Sobel Y", "Custom"
    };

    public static String edge_handler_list[] = {
        "Zero padding", "Clamp", "Wrap", "Mirror"
    };

    public static DefaultComboBoxModel<String> kernel_model, edge_model;
    public static JTabbedPane tabbed_panel;
    public static JPanel single_panel, multiple_panel;
    public static JComboBox<String> kernel_select, edge_select;
    public static JTextArea custom_kernel_area, sequence_area;
    public static JLabel width_label, height_label, pixel_count_label, operation_count_label;
    public static JLabel input_image_label, output_image_label;
    public static JTextField time_field;
    public static JButton run_button;

    public static void create_menu() {
        menu_bar = new JMenuBar();
        menu = new JMenu("Menu");
        submenu = new JMenu("Sub Menu");

        i1 = new JMenuItem("Item 1");
        i2 = new JMenuItem("Item 2");
        i3 = new JMenuItem("Item 3");
        i4 = new JMenuItem("Item 4");
        i5 = new JMenuItem("Item 5");

        menu.add(i1);
        menu.add(i2);
        menu.add(i3);
        submenu.add(i4);
        submenu.add(i5);
        menu.add(submenu);
        menu_bar.add(menu);

        frame.setJMenuBar(menu_bar);
    }

    public static void init_components() {
        kernel_model = new DefaultComboBoxModel<>(kernel_list);
        edge_model = new DefaultComboBoxModel<>(edge_handler_list);

        tabbed_panel = new JTabbedPane();
        frame.add(tabbed_panel);
    }

	public static JPanel create_control_panel() {
        // ===== TOP (controls) =====
        JPanel top_panel = new JPanel(new GridLayout(2,2,5,5));
        kernel_select = new JComboBox<>(kernel_model);
        edge_select = new JComboBox<>(edge_model);

        top_panel.add(new JLabel("Kernel:"));
        top_panel.add(kernel_select);
        top_panel.add(new JLabel("Edge handler:"));
        top_panel.add(edge_select);

		return top_panel;
	}

    public static void create_single_panel() {
        single_panel = new JPanel(new BorderLayout(8,8));

        // ===== TOP (controls) =====
        JPanel top_panel = create_control_panel();

        // ===== CENTER (images) =====
        JPanel image_panel = new JPanel(new GridLayout(1,2,5,5));

        BufferedImage img;
        try {
            img = ImageIO.read(new File("./data/mona lisa.jpg"));
        } catch (IOException e) {
            img = new BufferedImage(300,300,BufferedImage.TYPE_INT_RGB);
        }

        input_image_label = new JLabel(new ImageIcon(img));
        output_image_label = new JLabel(new ImageIcon(img));

        image_panel.add(new JScrollPane(input_image_label));
        image_panel.add(new JScrollPane(output_image_label));

        // ===== RIGHT (custom + sequence) =====
        JPanel right_panel = new JPanel(new BorderLayout(5,5));

        custom_kernel_area = new JTextArea(6,12);
        sequence_area = new JTextArea(6,12);

        JPanel custom_panel = new JPanel(new BorderLayout());
        custom_panel.add(new JLabel("Custom kernel:"), BorderLayout.NORTH);
        custom_panel.add(new JScrollPane(custom_kernel_area), BorderLayout.CENTER);

        JPanel sequence_panel = new JPanel(new BorderLayout());
        sequence_panel.add(new JLabel("Operation sequence:"), BorderLayout.NORTH);
        sequence_panel.add(new JScrollPane(sequence_area), BorderLayout.CENTER);

        right_panel.add(custom_panel, BorderLayout.CENTER);
        right_panel.add(sequence_panel, BorderLayout.SOUTH);

        // ===== BOTTOM (info + run) =====
        JPanel bottom_panel = new JPanel(new GridLayout(2,3,5,5));

        width_label = new JLabel("Width:");
        height_label = new JLabel("Height:");
        pixel_count_label = new JLabel("Pixels:");
        operation_count_label = new JLabel("Operations:");
        time_field = new JTextField();
        time_field.setEditable(false);

        bottom_panel.add(width_label);
        bottom_panel.add(height_label);
        bottom_panel.add(pixel_count_label);
        bottom_panel.add(operation_count_label);
        bottom_panel.add(new JLabel("Time (ms):"));
        bottom_panel.add(time_field);

        run_button = new JButton("Run");

        JPanel south_wrapper = new JPanel(new BorderLayout());
        south_wrapper.add(bottom_panel, BorderLayout.CENTER);
        south_wrapper.add(run_button, BorderLayout.EAST);

        // ===== ASSEMBLY =====
        single_panel.add(top_panel, BorderLayout.NORTH);
        single_panel.add(image_panel, BorderLayout.CENTER);
        single_panel.add(right_panel, BorderLayout.EAST);
        single_panel.add(south_wrapper, BorderLayout.SOUTH);

        tabbed_panel.add("Single image", single_panel);
    }

    public static void create_multiple_panel() {
        multiple_panel = new JPanel(new BorderLayout());
        JComboBox<String> kernel_select = new JComboBox<>(kernel_model);
        JButton run_button = new JButton("Run batch");

        multiple_panel.add(kernel_select, BorderLayout.NORTH);
        multiple_panel.add(run_button, BorderLayout.SOUTH);

        tabbed_panel.add("Multiple images", multiple_panel);
    }

    public static void main(String[] args) {
        frame = new JFrame("Image Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);

        init_components();
        create_menu();
        create_single_panel();
        create_multiple_panel();

        frame.setVisible(true);
    }
}
