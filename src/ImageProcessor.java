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
				{-1, -1, -1},
				{-1, 9, -1},
				{-1, -1, -1},
		};

		emboss_kernel = new int[][] {
				{0, 1, 0},
				{0, 0, 0},
				{0, -1, 0},
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
				{0, 0, 0},
				{0, 1, 0},
				{0, 0, 0},
		};

		edge_kernel = new int[][] {
				{0, -1, 0},
				{-1, 8, -1},
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
	public static int[] get_edge_extend(int x, int y) {
		while (x < 0) x += 1;
		while (x >= width) x--;
		while (y < 0) y += 1;
		while (y >= height) y--;

		return new int[] {x, y};
	}
	public static int[] get_edge_wrap(int x, int y) {
		x = ((x % width) + width) % width;
		y = ((y % height) + height) % height;

		return new int[] {x, y};
	}
	public static int[] get_edge_mirror(int x, int y) {
		while (x < 0) x = -x;
		while (x >= width) x = 2 * width - x;
		while (y < 0) y = -y;
		while (y >= height) y = 2 * height - y;

		return new int[] {x, y};
	}
	@FunctionalInterface
	public interface EdgeHandler {
		int[] get(int x, int y);
	}
	public static int[] get_pixel(int x, int y) {
		int p = input_img.getRGB(x, y);

		int a = (p >> 24) & 0xff;
		int r = (p >> 16) & 0xff;
		int g = (p >> 8) & 0xff;
		int b = p & 0xff;

		return new int[] {r, g, b, a};
	}
	public static int[] get_pixel(int x, int y, EdgeHandler edge_handler) {
		// System.out.println("b x=" + x + " y=" + y);
		int pixle_coordinates[] = edge_handler.get(x, y);
		// System.out.println("a x=" + x + " y=" + y);
		x = pixle_coordinates[0];
		y = pixle_coordinates[1];

		return get_pixel(x, y);
	}
	public static void set_pixel(int x, int y, int pixel[]) {
		int r = pixel[0];
		int g = pixel[1];
		int b = pixel[2];
		int a = pixel[3];

		int p = (a << 24) | (r << 16) | (g << 8) | b;
		output_img.setRGB(x, y, p);
	}
	public static int sum_kernel(int kernel[][]) {
		int sum = 0;
		int kernel_width  = kernel[0].length;
		int kernel_height = kernel.length;
		
		for (int r = 0; r < kernel_height; r++) {
			for (int c = 0; c < kernel_width; c++) {
				sum += kernel[r][c];
			}
		}

		return sum;
	}
	public static int[] weighted_sum(int kernel[][], int x, int y, EdgeHandler edge_handler) {
		int kernel_width  = kernel[0].length;
		int kernel_height = kernel.length;
		int kernel_sum = sum_kernel(kernel);
		int KW2 = (kernel_width-1)/2;
		int KH2 = (kernel_height-1)/2;
		int wps[] = {0, 0, 0, 255};

		for (int r = 0; r < kernel_height; r++) {
			for (int c = 0; c < kernel_width; c++) {
				int p[] = get_pixel(x + c - KW2, y + r - KH2, edge_handler);
				int w = kernel[r][c];
				//System.out.println("c " + c  + " r " + r + " w " + w);
				//print_pixel(p);

				wps[0] += w * p[0];
				wps[1] += w * p[1];
				wps[2] += w * p[2];
				// wps[3] += w * p[3];
			}
		}

		if (kernel_sum == 0) {
			kernel_sum = 1;
		}

		wps[0] /= kernel_sum;
		wps[1] /= kernel_sum;
		wps[2] /= kernel_sum;

		wps[0] = Math.min(255, Math.max(0, wps[0]));
		wps[1] = Math.min(255, Math.max(0, wps[1]));
		wps[2] = Math.min(255, Math.max(0, wps[2]));
		wps[3] = Math.min(255, Math.max(0, wps[3]));

		// wps[3] /= kernel_sum;

		return wps;
	}
	public static void kernel_convolution(int kernel[][], EdgeHandler edge_handler) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int wps[] = weighted_sum(kernel, x, y, edge_handler);
				wps[3] = get_pixel(x, y)[3];
				set_pixel(x, y, wps);
			}
		}
	}
	public static void print_pixel(int[] p) {
		System.out.println("(r=" + p[0] + ", g=" + p[1] + ", b=" + p[2]+ ", a=" + p[3] + ")");
	}
	
	public static void main(String[] args) {
		// load_image("./data/dice.png");
		load_image("./data/mona lisa.jpg");
		init_kernels();

		int kernel[][] = {
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
		};

		long startTime = System.currentTimeMillis();
		kernel_convolution(kernel, GetSetPixels::get_edge_extend);

		long stopTime = System.currentTimeMillis();
		System.out.println("Run time: " + (stopTime - startTime) + " ms");
		int opn = input_img.getWidth() * input_img.getHeight() * kernel.length * kernel[0].length;
		System.out.println("Number of operations: " + opn);

		System.out.println("Image width: " + input_img.getWidth());
		System.out.println("Image height: " + input_img.getHeight());

		System.out.println("Kernel width: " + kernel[0].length);
		System.out.println("Kernel height: " + kernel.length);

		save_image("output.png");
	}
}