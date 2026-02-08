import javax.swing.*;
import java.awt.*;

public class KernelEditorUI extends JFrame {

	JTextField[][] fields = new JTextField[3][3];
	Main mainUI; // reference to main UI

	public KernelEditorUI(Main mainUI) {
		this.mainUI = mainUI;

		setTitle("Kernel Editor");
		setLayout(new BorderLayout());
		setSize(300, 300);

		JPanel gridPanel = new JPanel(new GridLayout(3,3));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				fields[y][x] = new JTextField("0", 3);
				fields[y][x].setHorizontalAlignment(JTextField.CENTER);
				gridPanel.add(fields[y][x]);
			}
		}

		JButton applyButton = new JButton("Apply Kernel");

		applyButton.addActionListener(e -> applyKernel());

		add(new JLabel("Enter 3x3 Kernel Values", SwingConstants.CENTER), BorderLayout.NORTH);
		add(gridPanel, BorderLayout.CENTER);
		add(applyButton, BorderLayout.SOUTH);

		setVisible(true);
	}

	void applyKernel() {
		int[][] customKernel = new int[3][3];

		try {
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					customKernel[y][x] = Integer.parseInt(fields[y][x].getText());
				}
			}

			mainUI.setCustomKernel(customKernel);

			JOptionPane.showMessageDialog(this, "Kernel updated!");

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Invalid number in kernel!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
