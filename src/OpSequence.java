import java.awt.*;
import javax.swing.*;

public class OpSequence extends JPanel {

    // LEFT SIDE
    public JList<String> operation_list;
    public DefaultListModel<String> operation_model;

    public JButton add_button;
    public JButton remove_button;
    public JButton up_button;
    public JButton down_button;

    public JComboBox<String> kernel_select;
    public JTextArea custom_kernel_area;

    // RIGHT SIDE
    public JLabel operation_count_label;
    public JLabel estimated_ops_label;

    // BOTTOM
    public JTextArea console_area;
    public JButton run_button;
    public JButton save_button;

    public String kernel_list[] = {
        "Identity", "Blur", "Gaussian", "Sharpen",
        "Emboss", "Outline", "Edge", "Sobel X", "Sobel Y", "Custom"
    };

    public OpSequence() {
        setLayout(new BorderLayout(8,8));

        // ===== LEFT PANEL =====
        JPanel left_panel = new JPanel(new BorderLayout(5,5));

        operation_model = new DefaultListModel<>();
        operation_list = new JList<>(operation_model);
        JScrollPane list_scroll = new JScrollPane(operation_list);

        left_panel.add(new JLabel("Operation sequence:"), BorderLayout.NORTH);
        left_panel.add(list_scroll, BorderLayout.CENTER);

        // Toolbar buttons
        JPanel toolbar = new JPanel(new GridLayout(2,2,5,5));
        add_button = new JButton("Add");
        remove_button = new JButton("Remove");
        up_button = new JButton("Move Up");
        down_button = new JButton("Move Down");

        toolbar.add(add_button);
        toolbar.add(remove_button);
        toolbar.add(up_button);
        toolbar.add(down_button);

        // Kernel selector
        kernel_select = new JComboBox<>(kernel_list);

        JPanel kernel_panel = new JPanel(new BorderLayout(5,5));
        kernel_panel.add(new JLabel("Kernel:"), BorderLayout.NORTH);
        kernel_panel.add(kernel_select, BorderLayout.CENTER);

        // Custom kernel input
        custom_kernel_area = new JTextArea(4,10);
        JScrollPane custom_scroll = new JScrollPane(custom_kernel_area);

        JPanel custom_panel = new JPanel(new BorderLayout(5,5));
        custom_panel.add(new JLabel("Custom kernel:"), BorderLayout.NORTH);
        custom_panel.add(custom_scroll, BorderLayout.CENTER);

        JPanel left_bottom = new JPanel(new BorderLayout(5,5));
        left_bottom.add(toolbar, BorderLayout.NORTH);
        left_bottom.add(kernel_panel, BorderLayout.CENTER);
        left_bottom.add(custom_panel, BorderLayout.SOUTH);

        left_panel.add(left_bottom, BorderLayout.SOUTH);

        // ===== RIGHT PANEL =====
        JPanel right_panel = new JPanel(new GridLayout(4,1,5,5));
        right_panel.setBorder(BorderFactory.createTitledBorder("Info & Settings"));

        operation_count_label = new JLabel("Operations in sequence: 0");
        estimated_ops_label = new JLabel("Estimated total operations: 0");

        right_panel.add(operation_count_label);
        right_panel.add(estimated_ops_label);
        right_panel.add(new JLabel("Edge handler: (later)"));
        right_panel.add(new JLabel("Output format: (later)"));

        // ===== BOTTOM PANEL =====
        JPanel bottom_panel = new JPanel(new BorderLayout(5,5));

        console_area = new JTextArea(6,40);
        console_area.setEditable(false);
        JScrollPane console_scroll = new JScrollPane(console_area);

        JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        run_button = new JButton("Run");
        save_button = new JButton("Save Images");

        button_panel.add(run_button);
        button_panel.add(save_button);

        bottom_panel.add(new JLabel("Console output:"), BorderLayout.NORTH);
        bottom_panel.add(console_scroll, BorderLayout.CENTER);
        bottom_panel.add(button_panel, BorderLayout.SOUTH);

        // ===== ASSEMBLY =====
        add(left_panel, BorderLayout.WEST);
        add(right_panel, BorderLayout.CENTER);
        add(bottom_panel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(900,600));
    }

    // Optional test harness
    public static void main(String[] args) {
        JFrame frame = new JFrame("Operation Sequence Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new OpSequence());
        frame.pack();
        frame.setVisible(true);
    }
}
