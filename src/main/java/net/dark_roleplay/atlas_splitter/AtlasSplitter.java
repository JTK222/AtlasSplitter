package net.dark_roleplay.atlas_splitter;

import net.dark_roleplay.atlas_splitter.textures.TextureData;
import net.dark_roleplay.atlas_splitter.textures.TextureInfo;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AtlasSplitter {

	public static void main(String[] args){
		try {
			long start = System.currentTimeMillis();
			int argCount = args.length;
			if(argCount < 2){
				System.out.println("Usage: AtlasSplitter.jar <path-to-png> <cell-width> [cell-height] [output-path]");
			}

			int resX = Integer.parseInt(args[1]);
			int resY = Integer.parseInt(argCount >= 3 ? args[2] : args[1]);

			Path inputFile = Path.of(args[0]).normalize();
			String inputFileName = inputFile.getFileName().toString().split("\\.")[0];

			Path inputFolder = inputFile.normalize().toAbsolutePath().getParent();
			Path outputFolder = argCount >= 4 ? Path.of(args[3]).normalize() : inputFolder.resolve("output/");
			Path jsonFile = inputFolder.resolve(inputFileName + ".json");
			Path pngFile = inputFolder.resolve(inputFileName + ".png");

			System.out.println("Loading input JSON File at: " + jsonFile);
			JSONObject json = new JSONObject(new JSONTokener(new String(Files.readAllBytes(Path.of("./" + inputFileName + ".json")), StandardCharsets.UTF_8)));
			int entries = json.keySet().size();
			System.out.println("Loaded json, found " + entries + " entries");

			System.out.println("Loading texture atlas: " + pngFile);
			TextureData atlas = loadAtlas(Path.of("./", inputFileName + ".png"));
			System.out.println("Loaded texture atlas, size: " + atlas.getWidth() + " x " + atlas.getHeight());

			System.out.println("Processing textures");
			int entry = 1;
			for (String key : json.keySet()) {
				updateProgress(entry, entries);
				TextureInfo texInfo = new TextureInfo(key);
				String fileName = json.getString(key);

				TextureData texture = new TextureData(resX * texInfo.getW(), resY * texInfo.getH());
				texture.copyFromSegment(atlas, resX * texInfo.getX(), resY * texInfo.getY());
				writeTexture(outputFolder.resolve(fileName + ".png"), texture);
				entry++;
			}
			System.out.println("\nFinished processing, took:" + (System.currentTimeMillis() - start) + "ms");
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	private static void updateProgress(int done, int total){
		int count = 50;
		int progress = (int) Math.ceil((done / (total * 1f)) * count);

		String output = "[";
		for(int i = 0; i < count; i++)
			output += i < progress ? "|" : " ";
		output += "] " + done + " / " + total + "\r";
		System.out.print(output);
	}

	private static TextureData loadAtlas(Path location){
		ByteBuffer inputBuf = null;
		try (InputStream input = new BufferedInputStream(Files.newInputStream(location)); MemoryStack stack = MemoryStack.stackPush()) {
			byte[] imageData = input.readAllBytes();

			inputBuf = MemoryUtil.memAlloc(imageData.length);
			inputBuf.put(imageData);
			inputBuf.flip();

			IntBuffer widthBuf = stack.mallocInt(1);
			IntBuffer heightBuf = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer decodedImage = STBImage.stbi_load_from_memory(inputBuf, widthBuf, heightBuf, channels, 4);

			int width = widthBuf.get();
			int height = heightBuf.get();

			TextureData atlas = new TextureData(width, height);

			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					atlas.setPixel(x, y, decodedImage.getInt());

			return atlas;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputBuf != null)
				MemoryUtil.memFree(inputBuf);
		}
		return null;
	}

	public static void writeTexture(Path destination, TextureData texture) {
		try {
			if (!Files.exists(destination)) {
				Files.createDirectories(destination.getParent());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteBuffer outputBuf = null;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			outputBuf = MemoryUtil.memAlloc((texture.getWidth() * texture.getHeight()) * 4);

			for (int y = 0; y < texture.getHeight(); y++)
				for (int x = 0; x < texture.getWidth(); x++)
					outputBuf.putInt(texture.getPixel(x, y));

			outputBuf.rewind();

			STBImageWrite.stbi_write_png(destination.toString(), texture.getWidth(), texture.getHeight(), 4, outputBuf, texture.getWidth() * 4);
		} finally {
			if (outputBuf != null)
				MemoryUtil.memFree(outputBuf);
		}
	}
}
