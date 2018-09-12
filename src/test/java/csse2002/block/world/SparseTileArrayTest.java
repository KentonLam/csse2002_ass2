package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

public class SparseTileArrayTest {

    SparseTileArray emptyArray;

    @Before 
    public void setup() {
        this.emptyArray = new SparseTileArray();
    }

    @Test 
    public void testEmptyTileArrayShouldBeNullEverywhere() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertNull(
                    "Intial constructor should be null everywhere.",
                    this.emptyArray.getTile(new Position(i, j))
                );
            }
        }
    }

    @Test
    public void testAddLinkedTilesNormal() {

    }
}
