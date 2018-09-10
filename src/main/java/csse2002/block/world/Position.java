/**
 * Represents the position of a Tile
 * in the SparseTileArray
 *
 */
public class Position extends Object implements Comparable<Position> {

    /**
     * Construct a position for (x, y)
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Position(int x,
                    int y) {}

    /**
     * Get the x coordinate
     * @return the x coordinate
     */
    public int getX() {}

    /**
     * Get the y coordinate
     * @return the y coordinate
     */
    public int getY() {}

    /**
     * Indicates whether some other object is "equal to" this one.
     * (see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html">
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html</a>) 
     * Two Positions are equal if getX() == other.getX() &amp;&amp;
     * getY() == other.getY()
     * @param obj the object to compare to
     * @return true if obj is an instance of
     *  Position and if obj.x == x and obj.y == y.
     */
    public boolean equals(Object obj) {}

    /**
     * Compute a hashCode that
     * meets the contract of Object.hashCode 
     * (see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html">
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html</a>)
     * @return a suitable hashcode for the Position
     */
    public int hashCode() {}

    /**
     * Compare this position to another position. 
     * return
     * <ul>
     * <li> -1 if getX() &lt; other.getX() </li>
     * <li> -1 if getX() == other.getX() and getY() &lt; other.getY() </li>
     * <li> 0 if getX() == other.getX() and getY() == other.getY() </li>
     * <li> 1 if getX() &gt; other.getX() </li>
     * <li> 1 if getX() == other.getX() and getY() &gt; other.getY() </li>
     * </ul>
     * @param other the other Position to compare to
     * @return -1, 0, or 1 depending on conditions above
     */
    public int compareTo(Position other) {}

    /**
     * Convert this position to a string. 
     * String should be "(&lt;x&gt;, &lt;y&gt;)" where
     * &lt;x&gt; is the value returned by getX() and
     * &lt;y&gt; is the value returned by getY(). 
     * Note the space following the comma.
     * @return a string representation of the position "(<x>, <y>)"
     */
    public String toString() {}

}