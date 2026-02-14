package kip.gui;

import kip.ImageProcessor;

public class Operation {
	public String kernel;
	public String edge;
	public String custom_kernel;

	public Operation(String kernel, String edge, String custom_kernel) {
		this.kernel = kernel;
		this.edge = edge;
		this.custom_kernel = custom_kernel;
	}

	@Override
	public String toString() {
		return "Operation [kernel=" + kernel + ", edge=" + edge + "]";
	}

	public float[][] getCustomKernel() {
		try {
			String kernel_tokes[] = custom_kernel.split("\n");
			float[][] c_kernel = new float[kernel_tokes.length][];
			for (int i = 0; i < kernel_tokes.length; i++) {
				String line_tokes[] = kernel_tokes[i].split("\s+");
				c_kernel[i] = new float[line_tokes.length];
	
				for (int j = 0; j < line_tokes.length; j++) {
					float e = Float.parseFloat(line_tokes[j]);
					c_kernel[i][j] = e;
				}
			}

			return c_kernel;
		} catch (Exception e) {
			return ImageProcessor.indentiy_kernel;
		}
	}
}