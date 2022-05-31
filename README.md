# AtlasSplitter - Usage
Before you can start, you have to create a json file, defining all the textures in your atlas.
```json
{
	"0:0": "texture_0",
	"1:0:4:4": "large_texture_1"
}
```
The key can have 2 or 4 integers seperated by `:`, the former 2 specify the x and y coordinate, based on grid cells. (E.g. a 512x512 texture with a grid size of 16px will have a grid size of 32). The latter 2 values specify how many grid cells the texture uses, vertically and horizontally.  

Tldr; `pos_x:pos_y:width:height`  

The value will be your file name, `.png` will be attached automatically.

The json file has to have the same name as your atlas and be in the same folder. E.g. for an atlas.png you'd have a atlas.json file.  

Once you're done with the preperation, you can run the application using this command (may vary depending on your OS):
```
java -jar <jar-name> <atlas-path> <grid_cell_width> [grid_cell_height] [output-path]
```
an example might be:
```
java -jar ./AtlasSplitter.jar ./atlas.png 16
```

