package csse2002.block.world;

import java.util.Objects;

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
     * Two objects are equal if and only if both their x and y parts are equal.
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
     * Compute a hashCode that
     * meets the contract of Object.hashCode 
     * (see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html">
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html</a>)
     * @return a suitable hashcode for the Position
     */
    @Override
    public int hashCode() {
        // Computes a valid hash of the two variables.
        return Objects.hash(x, y);
    }

    /**
     * Compare this position to another position. 
     * Return
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
    public int compareTo(Position other) {
        // Compare x directions first.
        if (x != other.x) {
            // This results in the correct values. Proof is trivial.
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