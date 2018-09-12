package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldMapTest {
    private WorldMap emptyMap;
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

    /** Tests the private helper getElementTypes. */
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
    public void testConstructorFromFileNormal() throws Exception {
        assertEquals("Initial position wrong.", new Position(11, 7),
                basicMap.getStartPosition());
        assertEquals("Builder name wrong.", "The Builder's Name",
                basicMap.getBuilder().getName());

        List<Block> inv = basicMap.getBuilder().getInventory();
        assertEquals(Arrays.asList(WoodBlock.class, WoodBlock.class,
                SoilBlock.class, WoodBlock.class),
                getElementTypes(basicMap.getTiles().get(0).getBlocks()));


    }

    @Test
    public void testConstructorFromFileEmpty() throws Exception {
        assertEquals("Builder name not empty.",
                "", emptyMap.getBuilder().getName());
        assertEquals("Initial position wrong.",
                new Position(11, 7), emptyMap.getStartPosition());
        assertEquals("Inventory not empty.",
                0, emptyMap.getBuilder().getInventory().size());
        assertEquals("Tile not empty.", 0,
                emptyMap.getTile(new Position(11, 7)).getBlocks().size());
    }
}
