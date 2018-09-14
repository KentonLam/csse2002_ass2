package csse2002.block.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to store a world map
 */
public class WorldMap {

    /**
     * Enum mapping block type strings (used in the world map file format) to
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
         * Instantiates a new class of the block type.
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

        /**
         * Accepts a list of block types as strings and returns a list of
         * block instances given by the input strings.
         * @param blockTypes Iterable of block type strings.
         * @return List of block instances.
         * @throws WorldMapFormatException If there is no matching block type
         * for some input string.
         */
        public static List<Block> makeBlockList(Iterable<String> blockTypes)
                throws WorldMapFormatException {
            List<Block> blocks = new ArrayList<>();
            for (String blockType : blockTypes) {
                try {
                    blocks.add(BlockTypes.valueOf(blockType).newInstance());
                } catch (IllegalArgumentException e) {
                    throw new WorldMapFormatException();
                }
            }
            return blocks;
        }

        /**
         * Accepts a list of block types in an array and returns a list of
         * instances of those blocks.
         * @param blockTypesArray Block types as array list.
         * @return List of block instances.
         * @throws WorldMapFormatException If there is no matching block type
         * for an input string.
         */
        public static List<Block> makeBlockList(String[] blockTypesArray)
                throws WorldMapFormatException {
            return makeBlockList(Arrays.asList(blockTypesArray));
        }
    }

    /**
     * Class representing a pair of related objects.
     * @param <L> Left type.
     * @param <R> Right type.
     */
    private class Pair<L, R> {
        public final L left;
        public final R right;

        /**
         * Constructs a new Pair with the given left and right values.
         * @param left Left parameter.
         * @param right Right parameter.
         */
        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
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
     * @throws FileNotFoundException if the file does not exist
     * @require filename != null
     * @ensure the loaded map is geometrically consistent
     */
    public WorldMap(String filename) throws WorldMapFormatException,
                                            WorldMapInconsistentException,
                                            FileNotFoundException {
        try (FileReader file = new FileReader(filename)) {
            BufferedReader reader = new BufferedReader(file);

            parseBuilderSection(reader);


        } catch (FileNotFoundException e) {
            // Because FileNotFoundExc is a subclass of IOExc, we need to
            // manually propagate it here.
            throw e;
        } catch (IOException e) {
            throw new WorldMapFormatException();
        }
    }

    private void parseBuilderSection(BufferedReader reader)
            throws IOException, WorldMapFormatException {
        // Read the starting position, first 2 lines.
        int startX;
        int startY;
        try {
            startX = Integer.parseInt(reader.readLine());
            startY = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            // Invalid integer format, throw.
            throw new WorldMapFormatException();
        }
        startPosition = new Position(startX, startY);

        // The next 2 lines are the builder's name and inventory.
        builderName = reader.readLine();
        String[] inventoryStrings = reader.readLine().split(",");

        // Convert the block strings to class instances.
        builderInventory = BlockTypes.makeBlockList(inventoryStrings);
    }

    /**
     * Parses a string of the format "label1:N,label2:M,label3:P" into a
     * map.
     *
     * <p>Format (if the string does not match, an exception will be thrown):
     * <ul>
     *     <li>Comma-delimited fields (no spaces)</li>
     *     <li>Lowercase field names</li>
     *     <li>No repeated field names</li>
     *     <li>Number is a non-negative integer</li>
     * </ul>
     * </p>
     *
     * @param string String to parse.
     * @param allowMultiple If false, only one "label:N" will be permitted.
     * @return Map of label names to their number.
     * @throws WorldMapFormatException If multiple==false and there are
     * multiple fields, or the format is invalid (see above).
     */
    private Map<String, Integer> parseColonStrings(
            String string, boolean allowMultiple)
            throws WorldMapFormatException {
        Pattern fieldRegex = Pattern.compile("([a-z]+):(\\d+)");

        String[] fields = string.split(",");
        if (!allowMultiple && fields.length > 1) {
            throw new WorldMapFormatException();
        }

        Map<String, Integer> outputMap = new HashMap<>();
        for (String field : fields) {
            Matcher matcher = fieldRegex.matcher(field);
            // Throw on repeated names too.
            if (matcher.matches() && !outputMap.containsKey(matcher.group(1))) {
                // If it matches, we know the integer will be valid.
                outputMap.put(matcher.group(1),
                        Integer.parseInt(matcher.group(2)));
            } else {
                throw new WorldMapFormatException();
            }
        }

        return outputMap;
    }

    private void parseTilesSection(BufferedReader reader)
            throws IOException, WorldMapFormatException {
        String totalLine = reader.readLine();
        Map<String, Integer> totalLineMap = parseColonStrings(totalLine, false);
        if (!totalLineMap.containsKey("total")) {
            // Either it has no or the wrong string label, throw.
            throw new WorldMapFormatException();
        }

        int numLines = totalLineMap.get("total");

        int currentIndex = 0;
        String line;
        while (!(line = reader.readLine()).equals("")) {
            String[] splitLine = line.split(" ");
            if (splitLine.length != 2) {
                throw new WorldMapFormatException();
            }

            int tileNum;
            try {
                tileNum = Integer.parseInt(splitLine[0]);
            } catch (NumberFormatException e) {
                throw new WorldMapFormatException();
            }



            currentIndex++;
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