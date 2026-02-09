import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class ImageProcessorThread implements Callable<ChunkResult> {
    public BufferedImage input_img, output_img;
    public int width, height;
    public int kernel[][];
    public int kernel_width, kernel_height, kernel_sum, KW2, KH2;
    private StringBuilder log = new StringBuilder();
    public int x=-1, y=-1;


    public ImageProcessorThread(BufferedImage input_img_, int kernel_[][], int x_, int y_) {
        input_img = input_img_;
        width = input_img.getWidth();
        height = input_img.getHeight();
        output_img = new BufferedImage(width, height, input_img.getType());
        kernel = kernel_;
        kernel_width  = kernel[0].length;
        kernel_height = kernel.length;
        kernel_sum = sum_kernel();

        KW2 = (kernel_width-1)/2;
        KH2 = (kernel_height-1)/2;

        x = x_;
        y = y_;
    }
    public int[] get_pixel(int x, int y) {
        int p = input_img.getRGB(x, y);

        int a = (p >> 24) & 0xff;
        int r = (p >> 16) & 0xff;
        int g = (p >> 8) & 0xff;
        int b = p & 0xff;

        return new int[] {r, g, b, a};
    }
    public void set_pixel(int x, int y, int pixel[]) {
        int r = pixel[0];
        int g = pixel[1];
        int b = pixel[2];
        int a = pixel[3];

        int p = (a << 24) | (r << 16) | (g << 8) | b;
        output_img.setRGB(x, y, p);
    }
    public int sum_kernel() {
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
    public int[] weighted_sum(int x, int y) {
        int wps[] = {0, 0, 0, 255};

        for (int r = 0; r < kernel_height; r++) {
            for (int c = 0; c < kernel_width; c++) {
                int p[] = get_pixel(x + c - KW2, y + r - KH2);
                int w = kernel[r][c];
                wps[0] += w * p[0];
                wps[1] += w * p[1];
                wps[2] += w * p[2];
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

        return wps;
    }
    // Inside ImageProcessorThread.java
    public void kernel_convolution() {
        // Change <= to <
        for (int x = KW2; x < width - KW2; x++) {
            for (int y = KH2; y < height - KH2; y++) {
                int wps[] = weighted_sum(x, y);
                wps[3] = get_pixel(x, y)[3];
                set_pixel(x, y, wps);
            }
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        kernel_convolution();

        long stopTime = System.currentTimeMillis();
        log.append("Thread run time: ")
                .append(stopTime - startTime).append(" ms\n");

        int opn = width * height * kernel.length * kernel[0].length;
        log.append("Number of operations: ").append(opn).append("\n")
                .append("Image width: ").append(width).append("\n")
                .append("Image height: ").append(height).append("\n")
                .append("Kernel width: ").append(kernel[0].length).append("\n")
                .append("Kernel height: ").append(kernel.length).append("\n")
                .append("KW2: ").append(KW2).append("\n")
                .append("KH2: ").append(KH2).append("\n");
    }

    public String getLog() {
        return log.toString();
    }

    @Override
    public ChunkResult call() {
        run();
        // Calculate the size of the processed area
        int processedWidth = width - (2 * KW2);
        int processedHeight = height - (2 * KH2);

        return new ChunkResult(
                output_img.getSubimage(KW2, KH2, processedWidth, processedHeight),
                x,
                y
        );
    }

}
