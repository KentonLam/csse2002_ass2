package csse2002.block.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to store a world map
 */
public class WorldMap {

    /**
     * Enum mapping block blockClass strings (used in the world map file format) to
     * their Java classes.
     */
    private enum BlockTypes {
        wood(WoodBlock.class),
        grass(GrassBlock.class),
        soil(SoilBlock.class),
        stone(StoneBlock.class);

        private Class<? extends Block> blockClass;

        BlockTypes(Class<? extends Block> blockClass) {
            this.blockClass = blockClass;
        }

        /**
         * Instantiates a new class of the block blockClass.
         * @return New block object.
         */
        public Block newInstance() {
            try {
                return blockClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                // The enum values we define should never result in these
                // exceptions.
                throw new AssertionError(
                        "Exception while instantiating block class.", e);
            }
        }
    }

    private Builder builder;

    private Position startPosition;

    private final SparseTileArray sparseArray = new SparseTileArray();

    // Used to temporarily store this information until we construct a
    // tile for the builder.
    private String builderName;
    private List<Block> builderInventory;

    /**
     * Constructs a new block world map from a startingTile, position and
     * builder, such that getBuilder() == builder,
     * getStartPosition() == startPosition, and getTiles() returns a list
     * of tiles that are linked to startingTile. 
     * 
     * Hint: create a SparseTileArray as a member, and use the
     * addLinkedTiles to populate it.
     * @param startingTile the tile which the builder starts on
     * @param startPosition the position of the starting tile
     * @param builder the builder who will traverse the block world
     * @throws WorldMapInconsistentException if there are inconsistencies
     *          in the positions of tiles (such as two tiles at a single
     *          position)
     * @require startingTile != null, startPosition != null, builder != null
     */
    public WorldMap(Tile startingTile,
                    Position startPosition,
                    Builder builder)
             throws WorldMapInconsistentException {
        sparseArray.addLinkedTiles(
                startingTile, startPosition.getX(), startPosition.getY());
        this.startPosition = startPosition;
        this.builder = builder;
    }

    /**
     * Construct a block world map from the given filename.
     * 
     * The block world map format is as follows:
     * <pre>
     * &lt;startingX&gt;
     * &lt;startingY&gt;
     * &lt;builder's name&gt;
     * &lt;inventory1&gt;,&lt;inventory2&gt;, ... ,&lt;inventoryN&gt;
     * 
     * total:&lt;number of tiles&gt;
     * &lt;tile0 id&gt; &lt;block1&gt;,&lt;block2&gt;, ... ,&lt;blockN&gt;
     * &lt;tile1 id&gt; &lt;block1&gt;,&lt;block2&gt;, ... ,&lt;blockN&gt;
     *    ...
     * &lt;tileN-1 id&gt; &lt;block1&gt;,&lt;block2&gt;, ... ,&lt;blockN&gt;
     * 
     * exits
     * &lt;tile0 id&gt; &lt;name1&gt;:&lt;id1&gt;,&lt;name2&gt;:&lt;id2&gt;, ... ,&lt;nameN&gt;:&lt;idN&gt;
     * &lt;tile1 id&gt; &lt;name1&gt;:&lt;id1&gt;,&lt;name2&gt;:&lt;id2&gt;, ... ,&lt;nameN&gt;:&lt;idN&gt;
     *    ...
     * &lt;tileN-1 id&gt; &lt;name1&gt;:&lt;id1&gt;,&lt;name2&gt;:&lt;id2&gt;, ... ,&lt;nameN&gt;:&lt;idN&gt;
     * </pre>
     * 
     * 
     * For example: 
     * <pre>
     * 1
     * 2
     * Bob
     * wood,wood,wood,soil
     * 
     * total:4
     * 0 soil,soil,grass,wood
     * 1 grass,grass,soil
     * 2 soil,soil,soil,wood
     * 3 grass,grass,grass,stone
     * 
     * exits
     * 0 east:2,north:1,west:3
     * 1 south:0
     * 2 west:0
     * 3 east:0
     * </pre>
     * Tile IDs are the ordering of tiles returned by getTiles()
     * i.e. tile 0 is getTiles().get(0). 
     * The ordering does not need to be checked when loading a map (but
     * the saveMap function below does when saving). 
     * 
     * The function should do the following:
     * <ol>
     * <li> Open the filename and read a map in the format
     *     given above. </li>
     * <li> Construct a new Builder with the name and inventory from the
     *     file (to be returned by getBuilder()) </li>
     * <li> Construct a new Position for the starting position from the
     *     file to be returned as getStartPosition() </li>
     * <li> Construct a Tile for each tile entry in the file (to be
     *     returned by getTiles() and getTile()) </li>
     * <li> Link each tile by the exits that are given. </li>
     * <li> Throw a WorldMapFormatException if the format of the
     *     file is incorrect, if loaded tiles contain too many blocks, or
     *     GroundBlocks that have an index that is too
     * high (i.e., if the Tile constructor
     *     would throw an exception).</li>
     * <li> Throw a WorldMapInconsistentException if the format is
     *     correct, but tiles would end up in geometrically impossible
     *     locations (see SparseTileArray.addLinkedTiles()). </li>
     * </ol>
     * Hint: create a SparseTileArray as a member and call SparseTileArray.addLinkedTiles() to populate it.
     * @param filename the name to load the file from
     * @throws WorldMapFormatException if the file is incorrectly formatted
     * @throws WorldMapInconsistentException if the file is correctly
     *          formatted, but has inconsistencies (such as overlapping tiles)
     * @throws java.io.FileNotFoundException if the file does not exist
     * @require filename != null
     * @ensure the loaded map is geometrically consistent
     */
    public WorldMap(String filename)
             throws WorldMapFormatException, WorldMapInconsistentException,
                    java.io.FileNotFoundException {
        try (FileReader file = new FileReader(filename)) {
            BufferedReader reader = new BufferedReader(file);

            int lineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {


            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void parseBuilderSection(BufferedReader reader)
            throws IOException {
        // Read the starting position, first 2 lines.
        int startX = Integer.parseInt(reader.readLine());
        int startY = Integer.parseInt(reader.readLine());
        startPosition = new Position(startX, startY);

        // The next 2 lines are the builder's name and inventory.
        builderName = reader.readLine();
        String[] inventoryStrings = reader.readLine().split(",");

        // Convert the block strings to class instances.
        builderInventory = new ArrayList<>();
        for (String blockType : inventoryStrings) {
            builderInventory.add(BlockTypes.valueOf(blockType).newInstance());
        }
    }

    /**
     * Gets the builder associated with this block world.
     * @return the builder object
     */
    public Builder getBuilder() {
        return new Builder("DUMMY", new Tile());
    }

    /**
     * Gets the starting position.
     * @return the starting position.
     */
    public Position getStartPosition() {
        return new Position(0, 0);
    }

    /**
     * Get a tile by position. 
     * Hint: call SparseTileArray.getTile()
     * @param position get the Tile at this position
     * @return the tile at that position
     * @require position != null
     */
    public Tile getTile(Position position) {
        return new Tile();
    }

    /**
     * Get a list of tiles in a breadth-first-search
     * order (see <a href="../../../csse2002/block/world/SparseTileArray.html" title="class in csse2002.block.world"><code>SparseTileArray.getTiles()</code></a>
     * for details). 
     * Hint: call SparseTileArray.getTiles().
     * @return a list of ordered tiles
     */
    public List<Tile> getTiles() {
        return new ArrayList<Tile>();
    }

    /**
     * Saves the given WorldMap to a file specified by the filename. 
     * See the WorldMap(filename) constructor for the format of the map. 
     * The Tile IDs need to relate to the ordering of tiles returned by getTiles()
     * i.e. tile 0 is getTiles().get(0) 
     * The function should do the following:
     * <ol>
     * <li> Open the filename and write a map in the format
     *     given in the WorldMap constructor. </li>
     * <li> Write the current builder's (given by getBuilder()) name
     *     and inventory.</li>
     * <li> Write the starting position (given by getStartPosition())
     *     </li>
     * <li> Write the number of tiles </li>
     * <li> Write the index, and then each tile as given by
     *     getTiles() (in the same order). </li>
     * <li> Write each tiles exits, as given by
     *     getTiles().get(id).getExits() </li>
     * <li> Throw an IOException if the file cannot be opened for
     *     writing, or if writing fails. </li>
     * </ol>
     * 
     * Hint: call getTiles()
     * @param filename the filename to be written to
     * @throws java.io.IOException if the file cannot be opened or written to.
     * @require filename != null
     */
    public void saveMap(String filename)
                 throws java.io.IOException {}

}