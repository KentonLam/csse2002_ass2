package csse2002.block.world;

/**
 * Represents a two-dimensional position on the world map.
 */
public class Position implements Comparable<Position> {

    /** X position. */
    private final int x;
    /** Y position. */
    private final int y;

    /**
     * Construct a position for (x, y)
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate.
     * @return the x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     * @return the y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * (see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html">
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html</a>)
     *
     * Two positions are equal if and only if both their x and y parts are equal.
     *
     * @param obj the object to compare to
     * @return true if obj is an instance of
     *  Position and if obj.x == x and obj.y == y.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }
        Position other = (Position)obj;
        return x == other.getX() && y == other.getY();
    }

    /**
     * Returns an integer such that this.equals(other) implies
     * this.hashCode() == other.hashCode().
     * @return a suitable hashcode for the Position.
     */
    @Override
    public int hashCode() {
        // Adapted from https://stackoverflow.com/a/263416
        int hash = 17;
        hash = 31*hash + x;
        hash = 31*hash + y;
        return hash;
    }

    /**
     * Compare this position to another position.
     *
     * <ul>
     *     <li>If this.x != other.x, returns -1 if this.x < other.x and 1
     *     otherwise.</li>
     *     <li>Otherwise, if this.y != other.y, returns -1 if this.y < other.y
     *     and 1 otherwise.</li>
     *     <li>Otherwise, returns 0.</li>
     * </ul>
     *
     * @param other the other Position to compare to
     * @return -1 if this < other, 0 if this == other, 1 if this > other.
     */
    public int compareTo(Position other) {
        // Compare x directions first.
        if (x != other.x) {
            // This function results in the correct values. Proof is trivial.
            return Integer.signum(x - other.x);
        }
        // At this point, we know the x components are equal, so the result
        // depends only on the y components.
        return Integer.signum(y - other.y);
    }

    /**
     * Convert this position to a string. 
     * String is of the format "(&lt;x&gt;, &lt;y&gt;)" where
     * &lt;x&gt; is the value returned by getX() and
     * &lt;y&gt; is the value returned by getY().
     *
     * @return a string representation of the position, "(<x>, <y>)".
     */
    @Override
    public String toString() {
        return "("+ x +", "+ y +")";
    }

}