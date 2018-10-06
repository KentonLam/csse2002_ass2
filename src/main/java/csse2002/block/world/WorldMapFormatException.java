package csse2002.block.world;

/**
 * A World Map file contains the wrong format.
 */
public class WorldMapFormatException extends BlockWorldException {

    /**
     * New {@link WorldMapFormatException} with no message.
     */
    public WorldMapFormatException() {}

    /**
     * New {@link WorldMapFormatException} with given message.
     */
    public WorldMapFormatException(String message) {
        super(message);
    }

}