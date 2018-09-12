package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparseTileArrayTest {

    SparseTileArray emptyArray;

    @Before 
    public void setup() {
        this.emptyArray = new SparseTileArray();
    }

    @Test 
    public void testEmptyTileArrayShouldBeNullEverywhere() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertNull(
                    "Initial constructor should be null everywhere.",
                    this.emptyArray.getTile(new Position(i, j))
                );
            }
        }
    }

    @Test
    public void testAddLinkedTilesNormal() throws BlockWorldException {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tiles.add(new Tile());
        }
        tiles.get(0).addExit("south", tiles.get(2));
        tiles.get(2).addExit("west", tiles.get(1));
        tiles.get(1).addExit("south", tiles.get(5));
        tiles.get(5).addExit("east", tiles.get(7));
        tiles.get(7).addExit("north", tiles.get(2));
        tiles.get(2).addExit("south", tiles.get(7));
        tiles.get(7).addExit("east", tiles.get(8));
        tiles.get(8).addExit("north", tiles.get(3));
        tiles.get(2).addExit("east", tiles.get(3));
        tiles.get(3).addExit("east", tiles.get(4));
        tiles.get(4).addExit("south", tiles.get(9));
        tiles.get(9).addExit("west", tiles.get(8));

        this.emptyArray.addLinkedTiles(tiles.get(0), 0, 0);

        HashMap<Position, Tile> expectedMap = new HashMap<>();
        expectedMap.put(new Position(0, 0), tiles.get(0));
        expectedMap.put(new Position(-1, 1), tiles.get(1));
        expectedMap.put(new Position(0, 1), tiles.get(2));
        expectedMap.put(new Position(1, 1), tiles.get(3));
        expectedMap.put(new Position(2, 1), tiles.get(4));
        expectedMap.put(new Position(-1, 2), tiles.get(5));
        expectedMap.put(new Position(0, 2), tiles.get(7));
        expectedMap.put(new Position(1, 2), tiles.get(8));
        expectedMap.put(new Position(2, 2), tiles.get(9));

        for (Map.Entry<Position, Tile> entry : expectedMap.entrySet()) {
            assertEquals("Incorrect tile inserted at position.",
                    entry.getValue(),
                    this.emptyArray.getTile(entry.getKey()));
        }

    }
}
