package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * Unit test for Action.
 */
public class ActionTest
{
    private Action moveBuilderAction;
    private Action moveBlockAction;
    private Action digAction;
    private Action dropAction;

    @Before 
    public void setupActions() {
        moveBuilderAction = new Action(Action.MOVE_BUILDER, "south");
        moveBlockAction = new Action(Action.MOVE_BLOCK, "");
        digAction = new Action(Action.DIG, "");
        dropAction = new Action(Action.DROP, "");
    }

    @Test
    public void testGetPrimaryAction()
    {
        assertEquals("Get primary action wrong for move builder.",
            Action.MOVE_BUILDER,
            moveBuilderAction.getPrimaryAction());
        assertEquals("Get primary action wrong with dig.", 
            Action.DIG,
            digAction.getPrimaryAction());
    }
}
