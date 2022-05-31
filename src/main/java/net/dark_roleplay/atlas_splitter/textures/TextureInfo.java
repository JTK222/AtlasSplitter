package net.dark_roleplay.atlas_splitter.textures;

public class TextureInfo {
	private int x, y, w = 1, h = 1;

	public TextureInfo(String pos){
		String[] coordsUnp = pos.split(":");
		int[] coords = new int[coordsUnp.length];
		for(int i = 0; i < coords.length; i++)
			coords[i] = Integer.parseInt(coordsUnp[i]);
		if(coords.length >= 2) {
			x = coords[0];
			y = coords[1];
		}if(coords.length >= 4){
			w = coords[2];
			h = coords[3];
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}
}
