import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class OpSequence extends JPanel {

    public JTable operation_table;
    public DefaultTableModel table_model;

    public JButton add_button;
    public JButton remove_button;
    public JButton up_button;
    public JButton down_button;

    public JComboBox<String> kernel_select;
    public JComboBox<String> edge_select;
    public JTextArea custom_kernel_area;

    public JLabel operation_count_label;
    public JLabel estimated_ops_label;

    public JTextArea console_area;
    public JButton run_button;
    public JButton save_button;

    public String kernel_list[] = {
        "Identity", "Blur", "Gaussian", "Sharpen",
        "Emboss", "Outline", "Edge", "Sobel X", "Sobel Y", "Custom"
    };

    public String edge_list[] = {
        "Zero padding", "Clamp", "Wrap", "Mirror"
    };

    public OpSequence() {
        setLayout(new BorderLayout(5, 5));

        JPanel center_panel = new JPanel(new GridLayout(1, 2, 5, 5));

        // ================= LEFT: Operation Sequence =================
        JPanel left_panel = new JPanel(new BorderLayout(5, 5));
        left_panel.setBorder(BorderFactory.createTitledBorder("Operation Sequence"));

        table_model = new DefaultTableModel(
                new Object[]{"#", "Kernel", "Edge handling"}, 0
        );
        
        // --- POPULATE INITIAL VALUES ---
        table_model.addRow(new Object[]{"1", "Blur", "Wrap"});
        table_model.addRow(new Object[]{"2", "Sobel X", "Clamp"});
        table_model.addRow(new Object[]{"3", "Sharpen", "Mirror"});

        operation_table = new JTable(table_model);
        
        // --- SET COLUMN WIDTH FOR "#" ---
        TableColumn idColumn = operation_table.getColumnModel().getColumn(0);
        idColumn.setPreferredWidth(30);
        idColumn.setMaxWidth(40);
        idColumn.setMinWidth(25);

        left_panel.add(new JScrollPane(operation_table), BorderLayout.CENTER);

        // --- BUTTONS WITH BETTER ICONS ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        
        // Using common UIManager keys that look like Add/Remove/Up/Down
        add_button = new JButton("Add");
        remove_button = new JButton("Remove");
        up_button = new JButton("Up");
        down_button = new JButton("Down");

        // Set tooltips so users know what they do
        add_button.setToolTipText("Add Operation");
        remove_button.setToolTipText("Remove Operation");
        up_button.setToolTipText("Move Up");
        down_button.setToolTipText("Move Down");

        toolbar.add(add_button);
        toolbar.add(remove_button);
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(up_button);
        toolbar.add(down_button);

        left_panel.add(toolbar, BorderLayout.SOUTH);

        // ================= RIGHT: Settings & Info =================
        JPanel right_panel = new JPanel(new BorderLayout(5, 5));
        right_panel.setBorder(BorderFactory.createTitledBorder("Settings & Info"));

        // SETTINGS PANEL (GridBagLayout for Form Alignment)
        JPanel settings_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        kernel_select = new JComboBox<>(kernel_list);
        edge_select = new JComboBox<>(edge_list);
        custom_kernel_area = new JTextArea(13, 12);

        // Row 0: Kernel
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        settings_panel.add(new JLabel("Kernel:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        settings_panel.add(kernel_select, gbc);

        // Row 1: Edge Handling
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        settings_panel.add(new JLabel("Edge handling:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        settings_panel.add(edge_select, gbc);

        // Row 2: Custom Kernel
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        settings_panel.add(new JLabel("Custom kernel:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        settings_panel.add(new JScrollPane(custom_kernel_area), gbc);

        // INFO PANEL
        JPanel info_panel = new JPanel();
        info_panel.setLayout(new BoxLayout(info_panel, BoxLayout.Y_AXIS));
        operation_count_label = new JLabel("Operations in sequence: 3");
        estimated_ops_label = new JLabel("Estimated total operations: 15.2M");
        info_panel.add(Box.createVerticalStrut(10));
        info_panel.add(operation_count_label);
        info_panel.add(estimated_ops_label);

        JPanel right_top = new JPanel(new BorderLayout(5, 5));
        right_top.add(settings_panel, BorderLayout.NORTH);
        right_top.add(info_panel, BorderLayout.CENTER);

        right_panel.add(right_top, BorderLayout.NORTH);

        center_panel.add(left_panel);
        center_panel.add(right_panel);

        // ================= CONSOLE =================
        console_area = new JTextArea();
        console_area.setEditable(false);
        JScrollPane console_scroll = new JScrollPane(console_area);

        JPanel console_button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        run_button = new JButton("Run");
        save_button = new JButton("Save Images");
        console_button_panel.add(run_button);
        console_button_panel.add(save_button);

        JPanel console_panel = new JPanel(new BorderLayout(5, 5));
        console_panel.setBorder(BorderFactory.createTitledBorder("Console"));
        console_panel.add(console_scroll, BorderLayout.CENTER);
        console_panel.add(console_button_panel, BorderLayout.SOUTH);

        JSplitPane vertical_split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                center_panel,
                console_panel
        );
        vertical_split.setResizeWeight(0.6);
        vertical_split.setDividerLocation(400);

        add(vertical_split, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Set System Look and Feel for better looking icons
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {}

        JFrame frame = new JFrame("Operation Sequence Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new OpSequence());
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}