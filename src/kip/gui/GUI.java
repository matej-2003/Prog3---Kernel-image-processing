package kip.gui;

import java.awt.*;
import javax.swing.*;

public class GUI {
	public static JFrame frame;
	public static JTabbedPane tabbed_panel;
	
	// These are now handled inside SingleImageProcessorGUI
	public static SI_ProcessorGUI SIPG_panel;
	public static SI_ParallelProcessorGUI SIPPG_panel;
	public static MI_ProcessorGUI MIPG_panel;

	public static void init_components() {
		tabbed_panel = new JTabbedPane();
		frame.add(tabbed_panel);
	}

	public static void create_single_panel() {
		// Instantiate the specialized class we defined earlier
		SIPG_panel = new SI_ProcessorGUI(frame);
		
		// Add the instance directly to the tabbed pane (since it is a JPanel)
		tabbed_panel.addTab("Single Image", SIPG_panel);
	}
	public static void create_single_parallel_panel() {
		SIPPG_panel = new SI_ParallelProcessorGUI(frame);

		tabbed_panel.addTab("Parallel execution", SIPPG_panel);
	}

	public static void create_multiple_panel() {
		MI_ProcessorGUI MIPG_panel = new MI_ProcessorGUI(frame);
		tabbed_panel.addTab("Multiple Images", MIPG_panel);
	}

	public static void create_multiple_parallel() {
		JPanel multiple_panel = new JPanel(new BorderLayout());
		multiple_panel.add(new JLabel("Batch processing area", SwingConstants.CENTER));
		tabbed_panel.addTab("Multiple images parrallel execution", multiple_panel);
	}


	public static void main(String[] args) {
		// Look and Feel (Optional: Makes it look like the OS native UI)
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

		frame = new JFrame("Kernel Image Processor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);

		init_components();
		
		// Setup tabs
		create_single_panel();
		// create_single_parallel_panel();

		create_multiple_panel();
		// create_multiple_parallel();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}