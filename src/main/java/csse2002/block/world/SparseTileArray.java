package csse2002.block.world;

import java.util.ArrayList;
import java.util.List;

/**
 * A sparse representation of tiles in an Array. 
 * Contains Tiless stored with an
 * associated Position (x, y) in a map.
 */
public class SparseTileArray {

    /**
     * Constructor for a SparseTileArray.
     * Initializes an empty array, such that
     * getTile(x, y) returns null for any x and y.
     */
    public SparseTileArray() {}

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
        return new Tile();
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
        return new ArrayList<>();
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
    public void addLinkedTiles(Tile startingTile,
                               int startingX,
                               int startingY)
                        throws WorldMapInconsistentException {}

}