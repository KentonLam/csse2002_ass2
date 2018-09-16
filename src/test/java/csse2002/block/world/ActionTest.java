package csse2002.block.world;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private WorldMap testMap;

    // From https://stackoverflow.com/a/1119559
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    private final PrintStream oldOut = System.out;
    private final PrintStream oldErr = System.err;

    private BufferedReader makeReader(String string) {
        return new BufferedReader(new StringReader(string));
    }

    @Before 
    public void setupStreams() {
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
    }

    @Before
    public void setupActions() throws BlockWorldException {
        moveBuilderAction = new Action(Action.MOVE_BUILDER, "south");
        moveBlockAction = new Action(Action.MOVE_BLOCK, "east");
        digAction = new Action(Action.DIG, "");
        dropAction = new Action(Action.DROP, "1");

        testMap = makeTestMap();
    }

    @After
    public void restoreStreams() {
        System.setOut(oldOut);
        System.setErr(oldErr);
    }

    private static WorldMap makeTestMap() throws BlockWorldException {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile());
        }

        tiles.get(0).addExit("north", tiles.get(1));
        tiles.get(1).addExit("south", tiles.get(0));
        tiles.get(0).addExit("west", tiles.get(2));
        tiles.get(2).addExit("south", tiles.get(3));
        tiles.get(3).addExit("north", tiles.get(2));
        tiles.get(3).placeBlock(new WoodBlock());
        tiles.get(3).placeBlock(new WoodBlock());

        List<Block> inventory = new ArrayList<>();
        inventory.add(new WoodBlock());
        inventory.add(new WoodBlock());
        inventory.add(new SoilBlock());
        inventory.add(new WoodBlock());
        inventory.add(new WoodBlock());

        return new WorldMap(tiles.get(0),
                new Position(0, 0), new Builder("Bob", tiles.get(0), inventory));
    }

    private static void assertLinesEqual(String message, String expected,
                                         String actual) {
        expected = expected.replaceAll("\\n",
                System.getProperty("line.separator"));
        assertEquals(message, expected, actual);
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
        // action string exists if required.
        String[] actionStrings = {
                "MOVE_BUILDER to_the_fourth_dimension",
                "MOVE_BLOCK somewhere,idk?!@#$%^&*()_+:(){:|:&};:",
                "DROP -100",
                "DROP thatblock",
                "DROP ", // secondary action would be empty string.
                "MOVE_BUILDER "
        };
        for (String actionString : actionStrings) {
            Action action = Action.loadAction(makeReader(actionString));
        }
    }

    @Test
    public void testProcessActions() throws ActionFormatException {
        try {
            Action.processActions(makeReader(""
                    + "MOVE_BUILDER north\n"
                    + "MOVE_BUILDER south\n"
                    + "MOVE_BUILDER west\n"
                    + "DROP 1\n"
                    + "DROP 3\n"
                    + "DROP text\n"
                    + "DIG\n"
                    + "MOVE_BUILDER south\n"
                    + "MOVE_BLOCK north\n"
                    + "RANDOM_ACTION\n"
            ), testMap);
        } catch (ActionFormatException e) {}
        assertLinesEqual("Output wrong.", ""
                + "Moved builder north\n"
                + "Moved builder south\n"
                + "Moved builder west\n"
                + "Dropped a block from inventory\n"
                + "Dropped a block from inventory\n"
                + "Error: Invalid action\n"
                + "Top block on current tile removed\n"
                + "Moved builder south\n"
                + "Moved block north\n", outStream.toString());
    }
}
