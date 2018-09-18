package csse2002.block.world;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Representation of tiles on a two-dimensional grid of arbitrary size.
 */
public class SparseTileArray {

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
     * Represents cardinal directions as well as opposites to each direction
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
    private final List<Tile> insertedTiles = new ArrayList<>();

    /** Mapping of position to tiles. */
    private final Map<Position, Tile> positionToTile = new HashMap<>();

    /**
     * Constructor which initialises an empty SparseTileArray.
     * More precisely, getTile(new Position(x, y)) returns null for any x and y.
     */
    public SparseTileArray() {}

    /**
     * Resets the map data structures to be empty. Only mutates the fields;
     * does not reassign them.
     */
    private void resetInternalState() {
        insertedTiles.clear();
        positionToTile.clear();
    }

    /**
     * Gets the tile at position (x, y) or null if there is no tile at that
     * position.
     * @param position position.
     * @return tile at (x, y) or null if no such tile exists.
     * @require position != null
     */
    public Tile getTile(Position position) {
        return positionToTile.get(position);
    }

    /**
     * Returns a set of ordered tiles from the sparse tile array in
     * breadth-first-search order.
     *
     * More precisely, tiles are given from
     * the startingTile to other tiles, iterating over exits in the direction
     * north, east, south, west.
     * @return a list of tiles in breadth-first-search order.
     */
    public List<Tile> getTiles() {
        // Because addLinkedTiles is implemented as BFS, we simply cache
        // the result of that and return it here.
        return Collections.unmodifiableList(insertedTiles);
    }

    /**
     * Add a set of tiles to the sparse grid.
     *
     * <p>Removes all existing tiles before inserting any new tiles, then adds
     * startingTile at the given x and y coordinates. Iterates over any tiles
     * connected to startingTile (directly or transitively) via north, east,
     * south or west exits and adds them to positions relative to the starting
     * tile. If an exception is thrown (see below), the sparse tile array is
     * reset to an empty state.</p>
     *
     * <p>Enforces geometric consistency. Specifically, one tile can only
     * appear  one position and one position can only contain one tile.
     * More formally, there exists a bijection from positions to tiles.
     * If these constraints are violated, a {@link WorldMapInconsistentException}
     * will be thrown.</p>
     *
     * @param startingTile the starting point in adding the linked tiles. All
     *                      added tiles must have a path (via multiple exits) to this
     *                      tile.
     * @param startingX the x coordinate for startingTile.
     * @param startingY the y coordinate for startingTile.
     * @throws WorldMapInconsistentException if the tiles in the set are not
     *                                        geometrically consistent.
     * @require startingTile != null
     * @ensure tiles accessed through getTile() are geometrically consistent.
     */
    public void addLinkedTiles(Tile startingTile, int startingX, int startingY)
            throws WorldMapInconsistentException {
        // We offload the actual computations to a helper function and
        // clean before/after it.
        resetInternalState();
        try {
            unsafeBreadthFirstAddTiles(
                    startingTile, new Position(startingX, startingY));
        } catch (WorldMapInconsistentException e) {
            resetInternalState();
            throw e;
        }
    }

    /**
     * Shortcut for
     * <code>new AbstractMap.SimpleImmutableEntry(left, right)</code>.
     * @param left Left object.
     * @param right Right object.
     * @param <L> Left type.
     * @param <R> Right type.
     * @return The entry pair.
     */
    private static <L, R> Map.Entry<L, R> makeEntry(L left, R right) {
        return new AbstractMap.SimpleImmutableEntry<>(left, right);
    }

    /**
     * Helper function to execute the breadth first recursion through
     * startingTile's adjacent tiles. Return value indicates if adding all
     * linked tiles succeeded.
     *
     * <b>Important:</b> This method is unsafe; it does not clear the internal
     * state before executing or on exceptions.
     *
     * @param startingTile Tile to start from, cannot be null.
     * @param startingPos Position to start at.
     * @return true if all tiles were added successfully, false if there are
     * inconsistencies with the exits' geometry.
     * @throws WorldMapInconsistentException Map is geometrically inconsistent.
     */
    private void unsafeBreadthFirstAddTiles(Tile startingTile,
                                            Position startingPos)
            throws WorldMapInconsistentException {
        // Initialise queue with starting tile.
        Queue<Map.Entry<Tile, Position>> tilesToCheck = new LinkedList<>();
        tilesToCheck.add(makeEntry(startingTile, startingPos));

        while (!tilesToCheck.isEmpty()) {
            // Extract the next tile in the queue.
            Map.Entry<Tile, Position> tileAtPos = tilesToCheck.remove();
            Tile currentTile = tileAtPos.getKey();
            Position currentPos = tileAtPos.getValue();

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
                    throw new WorldMapInconsistentException(
                            "Exits lead to one tile in multiple positions.");
                }
            }

            // The following makes sure each position only contains one tile.
            if (positionToTile.containsKey(currentPos)) {
                // If we reach here, then this tile hasn't been placed before.
                // However, there already exists a different tile in its
                // position. This is geometrically inconsistent; return false
                // so addLinkedTiles throws.

                // This also handles the case where a reverse exit maps to a
                // different tile. The earlier tile would already have been
                // placed in the position, leading to this case.
                throw new WorldMapInconsistentException(
                        "Exits lead to multiple tiles in one position.");
            }

            // Add the tile to the grid.
            positionToTile.put(currentPos, currentTile);
            insertedTiles.add(currentTile);

            // Exits of the current tile.
            Map<String, Tile> exits = currentTile.getExits();
            // Iterate over the directions in order of N, E, S, W.
            for (Direction dir : Direction.values()) {
                if (exits.containsKey(dir.name())) {
                    Tile adjTile = exits.get(dir.name());

                    // All good, queue the adjacent tile to be processed.
                    tilesToCheck.add(makeEntry(
                            adjTile,
                            PosFunc.add(currentPos, dir.position())
                    ));
                }
            }
        }
    }
}