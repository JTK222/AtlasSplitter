package net.dark_roleplay.atlas_splitter.textures;

public class TextureData {
	private final int width;
	private final int height;
	private final int[][] pixels;

	public TextureData(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width][height];
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getPixel(int x, int y) {
		return pixels[x][y];
	}

	public void setPixel(int x, int y, int argb) {
		this.pixels[x][y] = argb;
	}

	public void copyFromSegment(TextureData other, int offX, int offY){
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				this.setPixel(x, y, other.getPixel(x + offX, y + offY));
	}
}
