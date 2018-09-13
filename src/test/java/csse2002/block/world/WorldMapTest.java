package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Before
    public void setup() throws BlockWorldException, FileNotFoundException {
        emptyMap = new WorldMap("worldmap_test_empty.txt");
        basicMap = new WorldMap("worldmap_test_basic.txt");
    }

    private List<Class> getElementTypes(Iterable array) {
        List<Class> classes = new ArrayList<>();
        for (Object o : array) {
            classes.add(o.getClass());
        }
        return classes;
    }

    /**
     * Tests the private helper getElementTypes.
     * This is important, as we use it in many other tests.
     * */
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

    @Test
    public void testBasicMapInitialPosition() {
        assertEquals("Initial position wrong.", new Position(11, 7),
                basicMap.getStartPosition());
    }

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
    public void testEmptyMapBuilderName() {
        assertEquals("Builder name not empty.",
                "", emptyMap.getBuilder().getName());
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
}
