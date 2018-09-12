package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparseTileArrayTest {

    SparseTileArray sparseArray;
    Tile tile1;
    Tile tile2;
    Tile tile3;

    @Before 
    public void setup() {
        this.sparseArray = new SparseTileArray();
        this.tile1 = new Tile();
        this.tile2 = new Tile();
        this.tile3 = new Tile();
    }

    private List<Tile> makeLinkedTile() {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tiles.add(new Tile());
        }
        try {
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
            tiles.get(8).addExit("south", tiles.get(6));
        } catch (NoExitException e) {
            throw new AssertionError(e);
        }
        return tiles;
    }

    @Test
    public void testEmptyTileArrayShouldBeNullEverywhere() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertNull(
                    "Initial constructor should be null everywhere.",
                    this.sparseArray.getTile(new Position(i, j))
                );
            }
        }
    }

    @Test
    public void testGetTile() throws BlockWorldException {
        Tile tile = new Tile();
        this.sparseArray.addLinkedTiles(tile, 100, 50);
        assertEquals("Get tile returned incorrect tile.",
                tile,
                this.sparseArray.getTile(new Position(100, 50))
        );
        
    }

    @Test
    public void testAddLinkedTilesNormal() throws BlockWorldException {
        List<Tile> tiles = makeLinkedTile();

        this.sparseArray.addLinkedTiles(tiles.get(0), 0, 0);

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
                    this.sparseArray.getTile(entry.getKey()));
        }
    }

    @Test
    public void testGetTilesNormal() throws BlockWorldException {
        List<Tile> tiles = makeLinkedTile();
        // This executes the code under test.
        this.sparseArray.addLinkedTiles(tiles.get(0), 0, 0);

        // The following is just to generate the expected tile list.
        // This could probably be made more robust.
        int[] order = new int[] {
                0, 2, 3, 7, 1, 4, 8, 5, 9, 6
        };
        List<Tile> expected = new ArrayList<>();
        for (int i : order) {
            expected.add(tiles.get(i));
        }
        assertEquals("Get tiles should return in BFS order.",
                expected,
                this.sparseArray.getTiles()
        );
    }


    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesThrowsWithWrongReverseExit()
            throws BlockWorldException {
        // Layout: tile1 -> tile2.
        // However, tile2's west exit is tile1 and should throw.
        tile1.addExit("east", tile2);
        tile2.addExit("west", tile3);

        this.sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesGeometricallyWrongTransitive()
            throws BlockWorldException {
        tile1.addExit("east", tile2);
        tile2.addExit("east", tile3);

        tile1.addExit("north", tile3);
        tile3.addExit("south", tile1);
        // Layout: tile1 -> tile2 -> tile3.
        // However, tile1 has a north exit to tile3, and tile3 has a south
        // exit to tile1. This wouldn't violate the reverse exit requirement,
        // but is still geometrically wrong as it implies tile3 is in two
        // different positions.
        this.sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesLinkingToSelfThrows() throws BlockWorldException {
        tile1.addExit("north", tile1);
        this.sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesOverlappingTileThrows() throws BlockWorldException {
        Tile tile4 = new Tile();
        Tile tile5 = new Tile();
        tile1.addExit("east", tile2);
        tile2.addExit("north", tile3);
        tile3.addExit("west", tile4);
        tile4.addExit("south", tile5);
        // Layout:
        // 4 <- 3
        //      ^
        // 1 -> 2
        // However, 4 has a south exit to 5 and tile5 is obviously not tile1.
        // Should throw.
        this.sparseArray.addLinkedTiles(tile1, 0, 0);
    }

}
