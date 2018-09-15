package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for Action.
 */
public class ActionTest
{
    private Action moveBuilderAction;
    private Action moveBlockAction;
    private Action digAction;
    private Action dropAction;

    private BufferedReader makeReader(String string) {
        return new BufferedReader(new StringReader(string));
    }

    @Before 
    public void setupActions() {
        moveBuilderAction = new Action(Action.MOVE_BUILDER, "south");
        moveBlockAction = new Action(Action.MOVE_BLOCK, "east");
        digAction = new Action(Action.DIG, "");
        dropAction = new Action(Action.DROP, "");
    }

    @Test
    public void testConstructorDoesntThrow() {
        Map<Integer, String> actionInputs = new HashMap<>();
        actionInputs.put(Action.DIG, "northnortheast");
        actionInputs.put(-100, "                  ");
        actionInputs.put(Integer.MAX_VALUE, null);
        actionInputs.put(Integer.MIN_VALUE, "west");

        for (Map.Entry<Integer, String> entry : actionInputs.entrySet()) {
            Action action = new Action(entry.getKey(), entry.getValue());
            assertEquals("Action primary wrong.",
                    entry.getKey().intValue(), action.getPrimaryAction());
            assertEquals("Action secondary wrong.",
                    entry.getValue(), action.getSecondaryAction());
        }
    }

    @Test
    public void testGetPrimaryAction() {
        assertEquals("Get primary action wrong for move builder.",
            Action.MOVE_BUILDER,
            moveBuilderAction.getPrimaryAction());
        assertEquals("Get primary action wrong with dig.", 
            Action.DIG,
            digAction.getPrimaryAction());
    }

    @Test
    public void testGetSecondaryAction() {
        assertEquals("Wrong secondary for move builder.", "south",
                moveBuilderAction.getSecondaryAction());
        assertEquals("Wrong secondary action for dig", "",
                digAction.getSecondaryAction());
    }

    @Test(expected = ActionFormatException.class)
    public void testLoadActionDigSecondary() throws ActionFormatException {
        Action.loadAction(makeReader("DIG south"));
    }

    @Test(expected = ActionFormatException.class)
    public void testLoadActionMoveNoSecondary() throws ActionFormatException {
        Action.loadAction(makeReader("MOVE_BUILDER"));
    }

    @Test(expected = ActionFormatException.class)
    public void testLoadActionInvalidAction() throws ActionFormatException {
        Action.loadAction(makeReader("WEW"));
    }

    @Test(expected = ActionFormatException.class)
    public void testLoadActionEmptyString() throws ActionFormatException {
        Action.loadAction(makeReader("\n"));
    }

    @Test
    public void testLoadActionNull() throws ActionFormatException {
        BufferedReader reader = makeReader("");
        try {
            reader.readLine();
        } catch (IOException e) {
            throw new AssertionError("IOException in StringReader.");
        }
        assertNull("Loading from EOF should be null.",
                Action.loadAction(reader));
    }
}
