package csse2002.block.world;

import java.util.*;


/**
 * A sparse representation of tiles in an Array. 
 * Contains Tiless stored with an
 * associated Position (x, y) in a map.
 */
public class SparseTileArray {

    /**
     * Helper class for a 2-tuple of tile and a position.
     * Used during the BFS as a queue item.
     */
    private class TileAtPos
            extends AbstractMap.SimpleImmutableEntry<Tile, Position> {
        private Tile tile;
        private final Position position;

        private TileAtPos(Tile tile, Position pos) {
            super(tile, pos);
            this.tile = tile;
            this.position = pos;
        }
    }

    /**
     * Helper class containing arithmetic operations for Position objects.
     */
    private static class PosFunc {
        private static Position add(Position p1, Position p2) {
            return new Position(p1.getX()+p2.getX(), p1.getY()+p2.getY());
        }
        private static Position neg(Position pos) {
            return new Position(-pos.getX(), -pos.getY());
        }
    }

    /**
     * Represents cardinal directions, as well as relations between directions
     * and their representation as a Position shift.
     */
    private enum Direction {
        north(0, -1, "south"),
        east(1, 0, "west"),
        south(0, 1, "north"),
        west(-1, 0, "east");

        private final String oppositeName;
        private final Position position;

        /**
         * Creates a Direction object.
         * @param x Shift in the x direction.
         * @param y Shift in the y direction.
         * @param oppositeName Name of the direction opposite this.
         */
        Direction(int x, int y, String oppositeName) {
            this.oppositeName = oppositeName;
            position = new Position(x, y);
        }

        /**
         * Returns the direction value of the compass direction opposite this.
         * */
        Direction opposite() {
            return Direction.valueOf(oppositeName);
        }

        /**
         * Returns this direction as a change in position.
         */
        Position position() {
            return position;
        }
    }

    /** Set of inserted tiles, in BFS order relative to a starting tile. */
    private List<Tile> insertedTiles = new ArrayList<>();

    /** Mapping of position to tiles. */
    private Map<Position, Tile> positionToTile = new HashMap<>();

    /**
     * Constructor for a SparseTileArray.
     * Initializes an empty array, such that
     * getTile(x, y) returns null for any x and y.
     */
    public SparseTileArray() {
    }

    private void resetInternalState() {
        insertedTiles.clear();
        positionToTile.clear();
    }

    /**
     * Get the tile at position (x, y). Return
     * null if there is no tile at (x, y). 
     * Hint: Construct a Map&lt;Position, Tile&gt;
     * in addLinkedTiles to allow looking up tiles
     * by position.
     * @param position the tile position
     * @return the tile at (x, y) or null if
     *          no such tile exists.
     * @require position != null
     */
    public Tile getTile(Position position) {
        return positionToTile.get(position);
    }

    /**
     * Get a set of ordered tiles from SparseTileArray in
     * breadth-first-search order. 
     * The startingTile (passed to addLinkTiles) should
     * be the first tile in the list. The following tiles
     * should be the tiles at the "north", "east", "south" and
     * "west" exits from the starting tile, if they exist. 
     * Then for each of those tiles, the next tiles will be their "north",
     * "east", "south" and "west" exits, if they exist.
     * The order should continue in the same way through all the tiles
     * that are linked to startingTile.
     * @return a list of tiles in breadth-first-search
     *          order.
     */
    public List<Tile> getTiles() {
        return insertedTiles;
    }

    /**
     * Add a set of tiles to the sparse tilemap. 
     * This function does the following:
     * <ol>
     * <li> Remove any tiles that are already existing in the sparse map. </li>
     * <li> Add startingTile at position (startingX, startingY), such
     * that getTile(startingX, startingY) == startingTile. </li>
     * <li> For each pair of linked tiles (tile1 at (x1, y1) and tile2 at (x2, y2)
     * that are accessible from startingTile (i.e. there is a
     * path through a series of exits startingTile.getExits().get("north").getExits().get("east") ...
     * between the two tiles), tile2 will get a new position based on tile1's position,
     * and tile1's exit name.
     * <ul>
     * <li> tile2 at "north"  exit should get a new position of (x1, y1 - 1),
     * i.e. getTile(x1, y1 - 1) == tile1.getExits().get("north")</li>
     * <li> tile2 at "East" exit should get a position of (x1 + 1, y1),
     * i.e. getTile(x1 + 1, y1)  == tile1.getExits().get("east")</li>
     * <li> tile2 at "South" exit should get a position of (x1, y1 + 1),
     * i.e. getTile(x1, y1 + 1) == tile1.getExits().get("south")</li>
     * <li> tile2 at "West" exit should get a position of (x1 - 1, y1),
     * i.e. getTile(x1 - 1, y1)  == tile1.getExits().get("west")</li>
     * </ul>
     * </li>
     * <li> If there are tiles that are not geometrically consistent, i.e.
     * tile1.getExits().get("north").getExits().get("south) is non null and not
     * equal to tile1, throw a WorldMapInconsistentException. Note: one way
     * exits are allowed, so
     * tile1.getExits().get("north").getExits().get("south) == null
     * would be acceptable, but
     * tile1.getExits().get("north").getExits().get("south) == tile2 for
     * some other non-null tile2 is not.</li>
     * <li> getTiles() should return a list of each accessible tile in a
     * breadth-first search order (see getTiles()) </li>
     * <li> If an exception is thrown, reset the state of the SparseTileArray
     * such that getTile(x, y) returns null for any x and y. </li>
     * </ol>
     * @param startingTile the starting point in adding the linked tiles. All
     *                      added tiles must have a path (via multiple exits) to this
     *                      tile.
     * @param startingX the x coordinate of startingTile in the array
     * @param startingY the y coordinate of startingTile in the array
     * @throws WorldMapInconsistentException if the tiles in the set are not
     *                                        Geometrically consistent
     * @require startingTile != null
     * @ensure tiles accessed through getTile() are geometrically consistent
     */
    public void addLinkedTiles(Tile startingTile, int startingX, int startingY)
            throws WorldMapInconsistentException {
        // We offload the actual computations to a helper function so we can
        // cleanup then throw here in one place.
        boolean success = breadthFirstAddLinkedTiles(
                startingTile, new Position(startingX, startingY));
        if (!success) {
            resetInternalState();
            throw new WorldMapInconsistentException();
        }
    }

    /**
     * Helper function to execute the breadth first recursion through
     * startingTile's adjacent tiles.
     * @param startingTile Tile to start from, cannot be null.
     * @param startingPos Position to start at.
     * @return true added successfully, false if there are inconsistencies
     * with the exits' geometry.
     */
    private boolean breadthFirstAddLinkedTiles(Tile startingTile,
                                               Position startingPos) {
        // Initialise queue with starting tile.
        Queue<TileAtPos> tilesToCheck = new LinkedList<>();
        tilesToCheck.add(new TileAtPos(startingTile, startingPos));

        while (!tilesToCheck.isEmpty()) {
            // Extract the next tile in the queue.
            TileAtPos tileAtPos = tilesToCheck.remove();
            Tile currentTile = tileAtPos.tile;
            Position currentPos = tileAtPos.position;

            // The following 'if' logic makes sure each tile exists in only
            // one position.
            if (insertedTiles.contains(currentTile)) {
                // The tile has already been encountered.
                if (positionToTile.containsKey(currentPos)
                        && positionToTile.get(currentPos).equals(currentTile)) {
                    // All good, the tile has been seen at the same position.
                    continue;
                } else {
                    // Inconsistent map. This tile has already been placed
                    // elsewhere.
                    return false;
                }
            }

            // The following makes sure each position only contains one tile.
            if (positionToTile.containsKey(currentPos)) {
                // If we reach here, then this tile hasn't been placed before.
                // However, there already exists a different tile in its
                // position. This is geometrically inconsistent; return false
                // so addLinkedTiles throws.
                return false;
            }

            // Add
            positionToTile.put(currentPos, currentTile);
            insertedTiles.add(currentTile);

            Map<String, Tile> exits = currentTile.getExits();
            // Iterate over the directions in order of N, E, S, W.
            for (Direction dir : Direction.values()) {
                if (exits.containsKey(dir.name())) {
                    Tile adjTile = exits.get(dir.name());
                    Map<String, Tile> adjExits = adjTile.getExits();
                    String opposite = dir.opposite().name();

                    // If the adjacent tile has an exit in our direction
                    // but it is not this tile, the map is inconsistent.
                    if (adjExits.containsKey(opposite)
                            && !adjExits.get(opposite).equals(currentTile)) {
                        return false;
                    }

                    // All good, queue the adjacent tile to be processed.
                    tilesToCheck.add(new TileAtPos(
                            adjTile,
                            PosFunc.add(currentPos, dir.position())
                    ));
                }
            }
        }
        return true;
    }
}