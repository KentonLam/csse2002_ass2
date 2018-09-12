package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;

public class WorldMapTest {
    private WorldMap map;
    @Test
    public void testConstructorFromFileNormal() throws Exception {
        map = new WorldMap("worldmap_test_basic.txt");
        assertEquals("Initial position wrong.", new Position(11, 7),
                map.getStartPosition());

    }

    @Test
    public void testConstructorFromFileEmpty() throws Exception {
        map = new WorldMap("worldmap_test_empty.txt");
        assertEquals("Builder name not empty.", "", map.getBuilder().getName());
        assertEquals("Initial position wrong.", new Position(11, 7),
                map.getStartPosition());
        assertEquals("Inventory not empty.", 0,
                map.getBuilder().getInventory().size());
        assertEquals("Tile not empty.", 0,
                map.getTile(new Position(11, 7)).getBlocks().size());
    }
}
