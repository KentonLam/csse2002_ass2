package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparseTileArrayTest {

    /* An empty SparseTileArray. */
    private SparseTileArray sparseArray;
    // Tiles with default blocks and no exits.
    private Tile tile1;
    private Tile tile2;
    private Tile tile3;
    private Tile tile4;
    private Tile tile5;

    // Set up blank tile array and five tiles.
    @Before 
    public void setup() {
        sparseArray = new SparseTileArray();
        tile1 = new Tile();
        tile2 = new Tile();
        tile3 = new Tile();
        tile4 = new Tile();
        tile5 = new Tile();
    }

    /**
     * Returns a tile linked to other tiles.
     * @return Tile.
     */
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

    // Name.
    @Test
    public void testEmptyTileArrayShouldBeNullEverywhere() {
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                assertNull(
                    "Initial constructor should be null everywhere.",
                        sparseArray.getTile(new Position(i, j))
                );
            }
        }
    }

    // An empty map should return empty getTiles.
    @Test
    public void testEmptyMapGetTiles() {
        assertEquals("Empty map has non-empty getTiles().",
                new ArrayList<Tile>(),
                sparseArray.getTiles());
    }

    // Test getTile works normally.
    @Test
    public void testGetTile() throws BlockWorldException {
        Tile tile = new Tile();
        sparseArray.addLinkedTiles(tile, 100, 50);
        assertEquals("Get tile returned incorrect tile.",
                tile,
                sparseArray.getTile(new Position(100, 50))
        );
        
    }

    // Test addLinkedTiles works normally.
    @Test
    public void testAddLinkedTilesNormal() throws BlockWorldException {
        List<Tile> tiles = makeLinkedTile();

        // Execute the code.
        sparseArray.addLinkedTiles(tiles.get(0), 0, 0);

        // Build the map to test against.
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
                    sparseArray.getTile(entry.getKey()));
        }
    }

    // Test getTiles works normally.
    @Test
    public void testGetTilesNormal() throws BlockWorldException {
        List<Tile> tiles = makeLinkedTile();

        sparseArray.addLinkedTiles(tiles.get(0), 0, 0);

        // The following is just to generate the expected tile list.
        int[] order = new int[] {
                0, 2, 3, 7, 1, 4, 8, 5, 9, 6
        };
        List<Tile> expected = new ArrayList<>();
        for (int i : order) {
            expected.add(tiles.get(i));
        }
        assertEquals("Get tiles should return in BFS order.",
                expected,
                sparseArray.getTiles()
        );
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesThrowsWithWrongReverseExit()
            throws BlockWorldException {
        // Layout: tile1 -> tile2.
        // However, tile2's west exit is not tile1 and should throw.
        tile1.addExit("east", tile2);
        tile2.addExit("west", tile3);

        sparseArray.addLinkedTiles(tile1, 0, 0);
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
        sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesLinkingToSelfThrows() throws BlockWorldException {
        tile1.addExit("north", tile1);
        // Tile has itself as a north exit. Clearly wrong.
        sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    @Test(expected = WorldMapInconsistentException.class)
    public void testAddTilesOverlappingTileThrows() throws BlockWorldException {
        tile4 = new Tile();
        tile5 = new Tile();
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
        sparseArray.addLinkedTiles(tile1, 0, 0);
    }

    // Ensure the sparse tile array is fully reset on exceptions.
    @Test
    public void testAddTilesResetsOnException() throws BlockWorldException {
        sparseArray.addLinkedTiles(tile1, 0, 0);

        // Set up a few valid tiles.
        tile1.addExit("north", tile2);
        tile2.addExit("north", tile3);
        // Tile linking to itself should fail.
        tile3.addExit("east", tile3);
        try {
            sparseArray.addLinkedTiles(tile1, 0, 0);
            // We assume addLinkedTiles throws as required, otherwise another
            // test would've failed.
            fail();
        } catch (WorldMapInconsistentException ignored) {}

        assertNull("Sparse map not reset on exception.",
                sparseArray.getTile(new Position(0, 0)));
        assertEquals("Sparse map not reset to size 0.",
                0, sparseArray.getTiles().size());
    }

    // Ensure the sparse tile array is reset before all calls to addLinkedTiles
    @Test
    public void testAddTilesResetsBeforeExecuting() throws BlockWorldException {
        // First tile at (0, 0).
        sparseArray.addLinkedTiles(tile1, 0, 0);
        // Second at (10, 10). Should remove the first.
        sparseArray.addLinkedTiles(tile2, 10, 10);
        assertNull("Position not reset before adding.",
                sparseArray.getTile(new Position(0, 0)));
        //noinspection ArraysAsListWithZeroOrOneArgument
        assertEquals("getTiles not reset.",
                Arrays.asList(tile2),
                sparseArray.getTiles());
    }

    // Modifying the return value of getTiles shouldn't affect the internal
    // data structure.
    @Test
    public void testGetTilesReturnsImmutable() throws BlockWorldException {
        tile1.addExit("north", tile2);
        tile1.addExit("south", tile3);

        List<Tile> originalTiles = Arrays.asList(tile1, tile2, tile3);
        sparseArray.addLinkedTiles(tile1, 0, 0);

        List<Tile> returnedTiles = sparseArray.getTiles();

        try {
            // Try to mutate the returned list by adding elements.
            returnedTiles.add(new Tile());
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            // Modifying returned list was prevented. Squash.
        }

        try {
            // Mutate by replacing an existing element.
            // Catches implementations which support .set() but not .add().
            returnedTiles.set(0, new Tile());
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            // Squash.
        }

        assertEquals("Returned list is mutable.",
                originalTiles, sparseArray.getTiles());
    }
}
