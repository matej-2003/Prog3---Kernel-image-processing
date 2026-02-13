package kip.gui;

import java.awt.*;
import javax.swing.*;

public class GUI {
	public JFrame frame;
	public JTabbedPane tabbed_panel;
	
	// These are now handled inside SingleImageProcessorGUI
	public SI_ProcessorGUI SIPG_panel;
	public MI_ProcessorGUI MIPG_panel;

	public void init_components() {
		tabbed_panel = new JTabbedPane();
		frame.add(tabbed_panel);
	}

	public void create_single_panel() {
		// Instantiate the specialized class we defined earlier
		SIPG_panel = new SI_ProcessorGUI(frame);
		
		// Add the instance directly to the tabbed pane (since it is a JPanel)
		tabbed_panel.addTab("Single Image", SIPG_panel);
	}
	public void create_single_parallel_panel() {
	}

	public void create_multiple_panel() {
		MI_ProcessorGUI MIPG_panel = new MI_ProcessorGUI(frame);
		tabbed_panel.addTab("Multiple Images", MIPG_panel);
	}

	public void create_multiple_parallel() {
		JPanel multiple_panel = new JPanel(new BorderLayout());
		multiple_panel.add(new JLabel("Batch processing area", SwingConstants.CENTER));
		tabbed_panel.addTab("Multiple images parrallel execution", multiple_panel);
	}


	public GUI() {
		frame = new JFrame("Kernel Image Processor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);

		init_components();
		create_single_panel();
		create_multiple_panel();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}