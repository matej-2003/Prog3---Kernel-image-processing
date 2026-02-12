package kip;

import java.awt.image.BufferedImage;

class ChunkResult {
	public BufferedImage image;
	public int x;
	public int y;

	ChunkResult(BufferedImage image, int x, int y) {
		this.image = image;
		this.x = x;
		this.y = y;
	}
}
