package csse2002.block.world;

/**
 * A World Map file is geometrically inconsistent.
 */
public class WorldMapInconsistentException extends BlockWorldException {

    /**
     * New {@link WorldMapInconsistentException} with no message.
     */
    public WorldMapInconsistentException() {}

    /**
     * New {@link WorldMapInconsistentException} with the given message.
     */
    public WorldMapInconsistentException(String message) {
        super(message);
    }
}