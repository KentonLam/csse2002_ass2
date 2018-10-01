package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class WorldMapTest {
    /**
     * A minimal map of a builder with no name, an empty inventory and
     * a single tile with no exits and no blocks.
     */
    private WorldMap emptyMap;
    /**
     * A basic map with no abnormal features.
     */
    private WorldMap basicMap;

    /**
     * Setup basic and empty maps.
     */
    @Before
    public void setup() throws BlockWorldException, FileNotFoundException {
        emptyMap = new WorldMap("worldmap_test_empty.txt");
        basicMap = new WorldMap("worldmap_test_basic.txt");
    }

    /**
     * Given an iterable, returns a list corresponding to the class of each
     * element.
     * @param iterable input iterable.
     * @return List of classes.
     */
    private List<Class> getElementTypes(Iterable iterable) {
        List<Class> classes = new ArrayList<>();
        for (Object o : iterable) {
            classes.add(o.getClass());
        }
        return classes;
    }

    /**
     * Tests the private helper getElementTypes.
     * This is important, as we use it in many other tests.
     */
    @Test
    public void testGetElementTypes() {
        List<Block> blocks = new ArrayList<Block>() {{
            add(new GrassBlock());
            add(new WoodBlock());
        }};
        List<Class> classes = new ArrayList<Class>() {{
            add(GrassBlock.class);
            add(WoodBlock.class);
        }};
        assertEquals("getElementTypes wrong.",
                classes,
                getElementTypes(blocks)
        );
    }

    // Test getStartPosition.
    @Test
    public void testBasicMapInitialPosition() {
        assertEquals("Initial position wrong.", new Position(11, 7),
                basicMap.getStartPosition());
    }

    // Test first tile of getTiles.
    @Test
    public void testBasicMapFirstTile() {
        assertEquals("First tile of getTiles() is not the starting tile.",
                basicMap.getTiles().get(0),
                basicMap.getTile(basicMap.getStartPosition()));
    }

    // Test builder name.
    @Test
    public void testBasicMapBuilderName() {
        assertEquals("Builder name wrong.", "The Builder's Name",
                basicMap.getBuilder().getName());
    }

    @Test
    public void testBasicMapBuilderInventory() {
        List<Block> inv = basicMap.getBuilder().getInventory();
        assertEquals("Wrong starting inventory.",
                Arrays.asList(WoodBlock.class, WoodBlock.class,
                        WoodBlock.class, SoilBlock.class, WoodBlock.class),
                getElementTypes(inv));
    }

    @Test
    public void testBasicMapTileBlocks() {
        // Set up the expected block types in an iterable structure.
        Class[] tile1Types = new Class[] {
                StoneBlock.class, StoneBlock.class, StoneBlock.class
        };
        Class[] tile2Types = new Class[] {
                StoneBlock.class, WoodBlock.class, SoilBlock.class
        };
        Class[] tile3Types = new Class[] {
                StoneBlock.class, GrassBlock.class, SoilBlock.class,
                WoodBlock.class, WoodBlock.class
        };
        Class[][] tileTypesArray = new Class[][] {
                tile1Types, tile2Types, tile3Types
        };

        List<Tile> mapTiles = basicMap.getTiles();
        assertEquals("Wrong number of tiles added.", 3, mapTiles.size());

        for (int i = 0; i < 3; i++) {
            assertEquals("Wrong blocks on tile.",
                    Arrays.asList(tileTypesArray[i]),
                    getElementTypes(mapTiles.get(i).getBlocks())
            );
        }
    }

    @Test
    public void testBasicMapExits() {
        Map<String, Tile> expected = new HashMap<>();
        List<Tile> tiles = basicMap.getTiles();
        expected.put("east", tiles.get(2));
        expected.put("north", tiles.get(1));

        assertEquals("Incorrect exits from starting tile.",
                expected,
                tiles.get(0).getExits());
    }

    @Test
    public void testEmptyMapBuilderName() {
        assertEquals("Builder name not empty.",
                "", emptyMap.getBuilder().getName());
    }

    @Test
    public void testEmptyMapBuilderStartingTile() {
        assertEquals("Builder starting tile wrong.",
                emptyMap.getBuilder().getCurrentTile(),
                emptyMap.getTiles().get(0));
    }

    @Test
    public void testEmptyMapInitialPosition() {
        assertEquals("Initial position wrong.",
                new Position(11, 7), emptyMap.getStartPosition());
    }

    @Test
    public void testEmptyMapStartingInventory() {
        assertEquals("Inventory not empty.",
                0, emptyMap.getBuilder().getInventory().size());
    }

    @Test
    public void testEmptyMapGetTiles() {
        assertEquals("Minimal map should have exactly 1 tile.",
                1, emptyMap.getTiles().size());
    }

    @Test
    public void testEmptyMapTileBlocks() {
        assertEquals("Tile not empty.", 0,
                emptyMap.getTile(new Position(11, 7)).getBlocks().size());
    }

    @Test
    public void testEmptyMapTileExits() {
        assertEquals("Tile should not have exits.",
                0, emptyMap.getTile(new Position(11, 7)).getExits().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void testConstructorFileNotFound()
            throws BlockWorldException, FileNotFoundException {
        new WorldMap("non-existent.txt");
    }

    @Test
    public void testInvalidMaps() throws BlockWorldException, IOException {
        File[] files = new File("worldmaps_invalid").listFiles();

        List<String> failedFiles = new ArrayList<>();
        for (File file : files) {
            // If it doesn't throw, add it to the list of failures.
            if (!testThrowsFormatException(file.getAbsolutePath())) {
                failedFiles.add(file.getName());
            }
        }
        if (failedFiles.size() != 0) {
            fail("The following files should've thrown a WMFE: "
                    + String.join(", ", failedFiles));
        }
    }

    private boolean testThrowsFormatException(String filename)
            throws WorldMapInconsistentException, FileNotFoundException {
        //noinspection CatchMayIgnoreException
        try {
            WorldMap map = new WorldMap(filename);
        } catch (WorldMapFormatException e) {
            return true;
        }
        return false;
    }

    @Test
    public void testSaveMapNormal() throws BlockWorldException, IOException {
        basicMap.saveMap("worldmaps_valid/basicmap_saved.txt");
        WorldMap map = new WorldMap("worldmaps_valid/basicmap_saved.txt");
    }

    @Test
    public void testLoadSampleMaps() {
        File[] files = new File("worldmaps_sample").listFiles();

        for (File file : files) {
            String name = file.getName();

            Class<? extends Exception> expected = null;
            if (name.startsWith("invalid")) {
                expected = WorldMapFormatException.class;
            } else if (name.startsWith("validButInconsistent")) {
                expected = WorldMapInconsistentException.class;
            }

            boolean thrown = true;
            try {
                WorldMap map = new WorldMap(file.getAbsolutePath());
                thrown = false;
            } catch (Exception e) {
                assertEquals("Incorrect exception thrown.",
                        expected,
                        e.getClass());
            }

            if (!thrown && expected != null) {
                fail("No exception thrown but "+expected+" was expected.");
            }
        }
    }
}
