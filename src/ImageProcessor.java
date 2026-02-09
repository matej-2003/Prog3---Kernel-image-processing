import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;


public class ImageProcessor {
	public static BufferedImage input_img, output_img;
	public static File f = null;
	public static int width, height;
	public static int[][] blur_kernel, gaussian_kernel, sharpen_kernel, emboss_kernel, outline_kernel, sobel_x, sobel_y, indentiy_kernel, edge_kernel;
	public static int[][] kernel;

	@FunctionalInterface
	public interface EdgeHandler {
		int[] get(int x, int y);
	}
	
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
	}
	public static void save_image(BufferedImage out, String filename) {
		try {
			f = new File(filename);
			ImageIO.write(out, "png", f);
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	public static void save_image(String filename) {
		save_image(output_img, filename);
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
	
	
	public static void linarExecution() {
		long startTime = System.currentTimeMillis();
		System.out.println("Linear execution: ");

		kernel_convolution(kernel, ImageProcessor::get_edge_extend);
		
		long stopTime = System.currentTimeMillis();
		System.out.println("Run time: " + (stopTime - startTime) + " ms");
		save_image("line_output.png");
	}
	

	// parallel function

	public static BufferedImage with_padding(BufferedImage img, int n) {
		BufferedImage out = new BufferedImage(img.getWidth() + n, img.getHeight() + n, img.getType());

		Graphics2D g = out.createGraphics();
		g.drawImage(img, n/2, n/2, null);
		g.dispose();

		return out;
	}
	public static int[][] make_chunks(int size, int n) {
		int chunks[][] = new int[n][2];

		int r = size % n;
		int chunks_size = Math.floorDiv(size - r, n);

		for (int i = 0; i < n; i++) {
			chunks[i][0] = chunks_size * i;
			chunks[i][1] = chunks_size * (i + 1) - 1;
		}
		if (r > 0) chunks[n-1][1] += r;

		// for (int[] f: chunks) {
		// 	System.out.println(Arrays.toString(f));
		// }

		return chunks;
	}
	public static int[] find_chunk_sizes(int thread_n) {
		ArrayList<Integer> factors = new ArrayList<>();

		for (int i = 1; i < Math.ceil(thread_n/2); i++) {
			if (thread_n % i == 0) {
				factors.add(i);
			}
		}

		int bc = factors.get(0);
		int min_df = thread_n;

		for (int i=1; i < factors.size(); i++) {
			int f = factors.get(i);
			int df = Math.abs(i - thread_n/f);

			if (df < min_df) {
				min_df = df;
				bc = f;
			}
		}

		int a = bc;
		int b = thread_n / bc;

		if ((width % a) + (height % b) < (width % b) + (height % a)) {
			return new int[] {a, b};
		}
		return new int[] {b, a};
	}
	
	
	public static void parallelExecution(int thread_number) {
		System.out.println("Parallel execution: " + thread_number + " threads");

		int chunk_sizes[] = find_chunk_sizes(thread_number);
		int x_chunk_number = chunk_sizes[0];
		int y_chunk_number = chunk_sizes[1];

		int width_chunks[][] = make_chunks(width, x_chunk_number);
		int height_chunks[][] = make_chunks(height, y_chunk_number);

		int kernel_width = kernel[0].length;
		int kernel_height = kernel.length;
		int KW2 = (kernel_width - 1) / 2;
		int KH2 = (kernel_height - 1) / 2;

		// STEP 1: Create a padded image and FILL the edges using Edge Extension
		// This removes the "black border" effect on the final image edges
		BufferedImage padded_input_image = new BufferedImage(width + 2 * KW2, height + 2 * KH2, input_img.getType());
		for (int py = 0; py < padded_input_image.getHeight(); py++) {
			for (int px = 0; px < padded_input_image.getWidth(); px++) {
				int[] coords = get_edge_extend(px - KW2, py - KH2);
				padded_input_image.setRGB(px, py, input_img.getRGB(coords[0], coords[1]));
			}
		}


		// this is expensive
		long startTime = System.currentTimeMillis();

		ExecutorService executor = Executors.newFixedThreadPool(thread_number);
		List<Future<ChunkResult>> futures = new ArrayList<>();

		for (int x = 0; x < x_chunk_number; x++) {
			int startX = width_chunks[x][0];
			int actualW = width_chunks[x][1] - width_chunks[x][0] + 1; // +1 is crucial!
			int cw = actualW + (kernel_width - 1); 

			for (int y = 0; y < y_chunk_number; y++) {
				int startY = height_chunks[y][0];
				int actualH = height_chunks[y][1] - height_chunks[y][0] + 1; // +1 is crucial!
				int ch = actualH + (kernel_height - 1);

				// Subimage from the fully-padded buffer
				// We start at startX because index 0 in original is index KW2 in padded
				BufferedImage sub = padded_input_image.getSubimage(startX, startY, cw, ch);
				
				// Clone the subimage to avoid threading issues with shared memory
				BufferedImage image_chunk = new BufferedImage(sub.getWidth(), sub.getHeight(), sub.getType());
				Graphics2D g2 = image_chunk.createGraphics();
				g2.drawImage(sub, 0, 0, null);
				g2.dispose();

				// Pass the REAL startX/startY so we know where to stitch it back
				futures.add(executor.submit(new ImageProcessorThread(image_chunk, kernel, startX, startY)));
			}
		}
		executor.shutdown();

		// STEP 2: Stitch back
		Graphics2D g = output_img.createGraphics();
		for (Future<ChunkResult> f : futures) {
			try {
				ChunkResult processed = f.get();
				// Draw the processed sub-chunk at its original coordinates
				g.drawImage(processed.image, processed.x, processed.y, null);
			} catch (Exception e) { e.printStackTrace(); }
		}
		g.dispose();

		long stopTime = System.currentTimeMillis();
		System.out.println("Run time: " + (stopTime - startTime) + " ms");
		
		save_image("para_output.png");
	}

	public static void main(String[] args) {
		// load_image("./data/mona lisa.jpg");
		load_image("./images/rockefeller_center.jpg");
		init_kernels();

//		kernel = new int[][] {
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//				{1, 1, 1, 1, 1, 1, 1},
//		};
		kernel = emboss_kernel;

		System.out.println("Image width: " + input_img.getWidth());
		System.out.println("Image height: " + input_img.getHeight());
		System.out.println("Kernel width: " + kernel[0].length);
		System.out.println("Kernel height: " + kernel.length);
		int opn = input_img.getWidth() * input_img.getHeight() * kernel.length * kernel[0].length;
		System.out.println("Number of operations: " + opn);
		

		// linear execution
		output_img = new BufferedImage(width, height, input_img.getType());
		linarExecution();

		// parallel execution
		output_img = new BufferedImage(width, height, input_img.getType());
		// int thread_numner = Runtime.getRuntime().availableProcessors();
		// System.out.println(thread_numner);
		parallelExecution(60);
	}
}