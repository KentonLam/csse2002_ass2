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
import java.util.Arrays;
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

    /**
     * Contains a single tile with no exits and only the default 3 blocks.
     * The builder has a ground block (grass) in slot 0 and normal block (wood)
     * in slot 1.
     */
    private WorldMap blankMap;

    private Tile blankMapStartingTile;

    // From https://stackoverflow.com/a/1119559
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    private final PrintStream oldOut = System.out;
    private final PrintStream oldErr = System.err;

    private BufferedReader makeReader(String string) {
        return new BufferedReader(new StringReader(string));
    }

    /**
     * Redirects stdout and stderr to streams we can check.
     */
    @Before 
    public void setupStreams() {
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
    }

    /**
     * Initialise some useful fields.
     * @throws BlockWorldException
     */
    @Before
    public void setup() throws BlockWorldException {
        moveBuilderAction = new Action(Action.MOVE_BUILDER, "south");
        moveBlockAction = new Action(Action.MOVE_BLOCK, "east");
        digAction = new Action(Action.DIG, "");
        dropAction = new Action(Action.DROP, "1");

        testMap = makeTestMap();

        blankMapStartingTile = new Tile();
        blankMap = new WorldMap(blankMapStartingTile, new Position(0, 0),
                new Builder("Bob2", blankMapStartingTile,
                        Arrays.asList(new SoilBlock(), new WoodBlock())));
    }

    /**
     * Restore stdout and stderr to the previous streams.
     */
    @After
    public void restoreStreams() {
        System.setOut(oldOut);
        System.setErr(oldErr);
    }

    /**
     * Builds a {@link WorldMap} with some linked tiles.
     * @return a WorldMap.
     */
    private static WorldMap makeTestMap() {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile());
        }

        try {
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
        } catch (BlockWorldException e) {
            // We don't expect these to throw.
            throw new AssertionError(e);
        }
    }

    /**
     * Asserts the given strings are equal, converting \n in expected to
     * a platform-dependent line ending.
     *
     * @param message Message on failure.
     * @param expected Expected lines, with \n line endings.
     * @param actual Actual.
     */
    private static void assertLinesEqual(String message, String expected,
                                         String actual) {
        expected = expected.replaceAll("\\n",
                System.getProperty("line.separator"));
        assertEquals(message, expected, actual);
    }

    /**
     * Asserts System.out is exactly the given string, converting \n in
     * expected to a platform-dependent line ending.
     *
     * @param message Message on failure.
     * @param expected Expected System.out output, with \n line endings.
     */
    private void assertSystemOut(String message, String expected) {
        assertLinesEqual(message, expected, outStream.toString());
    }

    /**
     * Asserts System.out is exactly the given string, converting \n in
     * expected to a platform-dependent line ending.
     *
     * @param expected Expected System.out output, \n line endings.
     */
    private void assertSystemOut(String expected) {
        assertSystemOut("Incorrect System.out output.", expected);
    }

    /**
     * Asserts System.err is exactly the given string, converting \n in
     * expected to a platform-dependent line ending.
     *
     * @param message Message on failure.
     * @param expected Expected System.err output, with \n line endings.
     */
    private void assertSystemErr(String message, String expected) {
        assertLinesEqual(message, expected, errStream.toString());
    }

    /**
     * Asserts System.err is exactly the given string, converting \n in
     * expected to a platform-dependent line ending.
     *
     * @param expected Expected System.err output, \n line endings.
     */
    private void assertSystemErr(String expected) {
        assertSystemErr("Incorrect System.err output.", expected);
    }


    /**
     * Ensure the {@link Action#Action(int, String)} constructor doesn't throw,
     * regardless of inputs.
     */
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

    /**
     * Ensure {@link Action#getPrimaryAction()} works normally.
     */
    @Test
    public void testGetPrimaryAction() {
        assertEquals("Get primary action wrong for move builder.",
            Action.MOVE_BUILDER,
            moveBuilderAction.getPrimaryAction());
        assertEquals("Get primary action wrong with dig.", 
            Action.DIG,
            digAction.getPrimaryAction());
    }

    /**
     * Ensure {@link Action#getSecondaryAction()} works normally.
     */
    @Test
    public void testGetSecondaryAction() {
        assertEquals("Wrong secondary for move builder.", "south",
                moveBuilderAction.getSecondaryAction());
        assertEquals("Wrong secondary action for dig", "",
                digAction.getSecondaryAction());
    }

    /**
     * Ensure {@link Action#loadAction(BufferedReader)} throws when DIG
     * has a secondary.
     * @throws ActionFormatException expected.
     */
    @Test(expected = ActionFormatException.class)
    public void testLoadActionDigSecondary() throws ActionFormatException {
        Action.loadAction(makeReader("DIG south"));
    }

    /**
     * Ensure {@link Action#loadAction(BufferedReader)} throws when DIG
     * has a trailing space.
     * @throws ActionFormatException expected.
     */
    @Test(expected = ActionFormatException.class)
    public void testLoadActionDigWithSpace() throws ActionFormatException {
        Action.loadAction(makeReader("DIG "));
    }

    /**
     * Ensure {@link Action#loadAction(BufferedReader)} throws when
     * MOVE_BUILDER north has a trailing space.
     * @throws ActionFormatException expected.
     */
    @Test(expected = ActionFormatException.class)
    public void testLoadActionMoveBuilderTrailingSpace()
            throws ActionFormatException {
        Action.loadAction(makeReader("MOVE_BUILDER north "));
    }

    /**
     * Ensure {@link Action#loadAction(BufferedReader)} throws when MOVE_BUILDER
     * has no secondary.
     * @throws ActionFormatException expected.
     */
    @Test(expected = ActionFormatException.class)
    public void testLoadActionMoveNoSecondary() throws ActionFormatException {
        Action.loadAction(makeReader("MOVE_BUILDER"));
    }

    // Got lazy...

    // Invalid primary (WEW) should throw.
    @Test(expected = ActionFormatException.class)
    public void testLoadActionInvalidAction() throws ActionFormatException {
        Action.loadAction(makeReader("WEW"));
    }

    // Empty line should throw.
    @Test(expected = ActionFormatException.class)
    public void testLoadActionEmptyString() throws ActionFormatException {
        Action.loadAction(makeReader("\n"));
    }

    // Loading from EOF should be null.
    @Test
    public void testLoadActionNull() throws ActionFormatException {
        BufferedReader reader = makeReader("");
        assertNull("Loading from EOF should be null.",
                Action.loadAction(reader));
    }

    // loadAction works normally.
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

    // loadAction should do no validation.
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

    // Example processActions from Javadoc.
    @Test
    public void testProcessActionsFromJavaDoc() throws ActionFormatException {
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
            fail("ActionFormatException not thrown.");
        } catch (ActionFormatException ignored) {}
        assertSystemOut(""
                + "Moved builder north\n"
                + "Moved builder south\n"
                + "Moved builder west\n"
                + "Dropped a block from inventory\n"
                + "Dropped a block from inventory\n"
                + "Error: Invalid action\n"
                + "Top block on current tile removed\n"
                + "Moved builder south\n"
                + "Moved block north\n");
        assertSystemErr("");
    }

    // processAction handling TooHigh.
    @Test
    public void testProcessActionTooHigh() {
        Action.processAction(new Action(Action.DROP, "0"), blankMap);
        assertSystemOut("Too high\n");
        assertSystemErr("");
    }

    // processAction handling NoExit.
    @Test
    public void testProcessActionNoExit() {
        Action.processAction(new Action(Action.MOVE_BUILDER, "east"), blankMap);
        assertSystemOut("No exit this way\n");
        assertSystemErr("");
    }

    // processAction handling TooLow.
    @Test
    public void testProcessActionsTooLow() {
        // Get rid of default starting blocks.
        try {
            blankMapStartingTile.removeTopBlock();
            blankMapStartingTile.removeTopBlock();
            blankMapStartingTile.removeTopBlock();
        } catch (TooLowException e) {
            fail("TooLow while removing default blocks.");
        }

        Action.processAction(new Action(Action.DIG, ""), blankMap);
        assertSystemOut("Too low\n");
        assertSystemErr("");
    }

    // processAction handling InvalidBlock.
    @Test
    public void testProcessActionInvalidBlock()
            throws TooHighException, InvalidBlockException {
        // Add undiggable block.
        blankMapStartingTile.placeBlock(new StoneBlock());

        Action.processAction(new Action(Action.DIG, ""), blankMap);
        assertSystemOut("Cannot use that block\n");
        assertSystemErr("");
    }

    // processAction handling DIG normally.
    @Test
    public void testProcessActionDig() {
        Action.processAction(new Action(Action.DIG, ""), blankMap);
        assertEquals("Block not dug.",
                2, blankMapStartingTile.getBlocks().size());
        assertSystemOut("Top block on current tile removed\n");
        assertSystemErr("");
    }

    // MOVE_BUILDER normally.
    @Test
    public void testProcessActionMoveBuilder() {
        Action.processAction(new Action(Action.MOVE_BUILDER, "north"), testMap);
        assertEquals("Moved to wrong block.",
                testMap.getTiles().get(1),
                testMap.getBuilder().getCurrentTile());
        assertSystemOut("Moved builder north\n");
        assertSystemErr("");
    }

    // DROP normal.
    @Test
    public void testProcessActionDrop() throws TooLowException {
        Block theBlock = blankMap.getBuilder().getInventory().get(1);
        Action.processAction(new Action(Action.DROP, "1"), blankMap);
        assertEquals("Placed wrong block.",
                theBlock,
                blankMapStartingTile.getTopBlock());
        assertFalse("Block still in inventory.",
                blankMap.getBuilder().getInventory().contains(theBlock));
        assertSystemOut("Dropped a block from inventory\n");
        assertSystemErr("");
    }

    // MOVE_BLOCK normal.
    @Test
    public void testProcessActionMoveBlock() throws BlockWorldException {
        Block topBlock = new WoodBlock();
        Tile northTile = new Tile();
        blankMapStartingTile.placeBlock(topBlock);
        blankMapStartingTile.addExit("north", northTile);

        Action.processAction(new Action(Action.MOVE_BLOCK, "north"), blankMap);

        assertNotEquals("Block still on old tile.",
                topBlock, blankMapStartingTile.getTopBlock());
        assertEquals("Block moved to other tile.",
                topBlock, northTile.getTopBlock());
        assertSystemOut("Moved block north\n");
        assertSystemErr("");
    }

    // MOVE_BUILDER via northeast should fial.
    @Test
    public void testProcessActionMoveBuilderExitExistsButNotCompass()
            throws NoExitException {
        blankMapStartingTile.addExit("northeast", new Tile());

        Action.processAction(
                new Action(Action.MOVE_BUILDER, "northeast"), blankMap);
        assertEquals("Builder shouldn't move.",
                blankMapStartingTile,
                blankMap.getBuilder().getCurrentTile());
        assertSystemOut("Error: Invalid action\n");
    }

    // Tests integer values of constants.
    @Test
    public void testActionIntegerValues() {
        assertEquals(0, Action.MOVE_BUILDER);
        assertEquals(1, Action.MOVE_BLOCK);
        assertEquals(2, Action.DIG);
        assertEquals(3, Action.DROP);
    }
}
