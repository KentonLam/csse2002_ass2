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
        dropAction = new Action(Action.DROP, "1");
    }

    @Test
    public void testConstructorDoesntThrow() {
        Map<Integer, String> actionInputs = new HashMap<>();
        actionInputs.put(Action.DIG, "northnortheast");
        actionInputs.put(Action.DROP, "-100");
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
    public void testLoadActionDigWithSpace() throws ActionFormatException {
        Action.loadAction(makeReader("DIG "));
    }

    @Test(expected = ActionFormatException.class)
    public void testLoadActionMoveBuilderTrailingSpace()
            throws ActionFormatException {
        Action.loadAction(makeReader("MOVE_BUILDER north "));
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

    @Test
    public void testLoadActionNormal() throws ActionFormatException {
        // Overwrite instance fields with actions loaded from strings.
        digAction = Action.loadAction(makeReader("DIG"));
        moveBuilderAction = Action.loadAction(makeReader("MOVE_BUILDER east"));
        moveBlockAction = Action.loadAction(makeReader("MOVE_BLOCK south"));
        dropAction = Action.loadAction(makeReader("DROP 3"));

        // None of these should throw.
        assertEquals("Dig action primary wrong.",
                Action.DIG, digAction.getPrimaryAction());
        assertEquals("Dig action secondary wrong.",
                "", digAction.getSecondaryAction());

        assertEquals("Move builder primary wrong.",
                Action.MOVE_BUILDER, moveBuilderAction.getPrimaryAction());
        assertEquals("Move builder secondary wrong.",
                "east", moveBuilderAction.getSecondaryAction());

        assertEquals("Move block primary wrong.",
                Action.MOVE_BLOCK, moveBlockAction.getPrimaryAction());
        assertEquals("Move block secondary wrong.",
                "south", moveBlockAction.getSecondaryAction());

        assertEquals("Drop primary wrong.",
                Action.DROP, dropAction.getPrimaryAction());
        assertEquals("Drop secondary wrong.",
                "3", dropAction.getSecondaryAction());
    }

    @Test
    public void testLoadActionDoesNoValidation() throws ActionFormatException {
        // loadAction should do no validation beyond ensuring a secondary
        // action string exists.
        String[] actionStrings = {
                "MOVE_BUILDER to_the_fourth_dimension",
                "MOVE_BLOCK somewhere,idk?!@#$%^&*()_+:(){:|:&};:",
                "DROP -100",
                "DROP thatblock",
                "DROP ", // secondary action would be empty string.
                "MOVE_BUILDER "
        };
        for (int i = 0; i < actionStrings.length; i++) {
            Action action = Action.loadAction(makeReader(actionStrings[i]));
        }
    }
}
