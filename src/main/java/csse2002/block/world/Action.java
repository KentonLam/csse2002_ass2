package csse2002.block.world;

/**
 * Represents an Action which can be performed on the block world (also
 * called world map). 
 * 
 * An action is something that a builder can do on a tile in the block world.
 * The actions include, moving the builder in a direction, moving a block in
 * a direction, digging on the current tile the builder is standing on and
 * dropping an item from a builder's inventory.
 */

public class Action {

    // TODO: These should really be represented as an enum class.
    /**
     * MOVE_BUILDER action which is represented by integer 0.
     */
    public static final int MOVE_BUILDER = 0;

    /**
     * MOVE_BLOCK action which is represented by integer 1.
     */
    public static final int MOVE_BLOCK = 1;

    /**
     * DIG action which is represented by integer 2.
     */
    public static final int DIG = 2;

    /**
     * DROP action which is represented by integer 3.
     */
    public static final int DROP = 3;

    /** Integer representing the action. */
    private final int primaryAction;

    /** String representing parameters of the action */
    private final String secondaryAction;

    /**
     * Create an Action that represents a manipulation of the blockworld.
     * An action is represented by a primary action (one of MOVE_BUILDER,
     * MOVE_BLOCK, DIG or DROP), and a secondary action 
     * 
     * Whether a secondary action is required depends on the primary action:
     * <ol>
     * <li> MOVE_BUILDER and MOVE_BLOCK require a direction as the
     *    secondaryAction (one of "north", "east", "south" or "west"). </li>
     * <li> DROP requires the index of at which a Block from the inventory
     *    should be dropped (stored as a string in this class, e.g., "1"). </li>
     * <li> DIG does not require a secondary action, so an empty string
     *    can be passed to secondaryAction. </li>
     * </ol>
     * 
     * This constructor does not need to check primaryAction or secondaryAction,
     * it just needs to construct an action such that
     * getPrimaryAction() == primaryAction, and
     * getSecondaryAction().equals(secondaryAction).
     * @param primaryAction the action to be created
     * @param secondaryAction the supplementary information associated with the
     *                         primary action
     * @require secondaryAction != null
     */
    public Action(int primaryAction, String secondaryAction) {
        this.primaryAction = primaryAction;
        this.secondaryAction = secondaryAction;
    }

    /**
     * Get the integer representing the Action (e.g., return 0 if Action is MOVE_BUILDER)
     * @return the primary action
     */
    public int getPrimaryAction() {
        return this.primaryAction;
    }

    /**
     * Gets the supplementary information associated with the Action
     * @return the secondary action, or "" (empty string) if no secondary
     *  action exists
     */
    public String getSecondaryAction() {
        return this.secondaryAction;
    }

    /**
     * Create a single Action if possible from the given reader. 
     * 
     * Read a line from the given reader and load the Action on that line.
     * Only load one Action (<b>hint:</b> reader.readLine()) and return
     * the created action. 
     * 
     * Return null if the reader is at the end of the file. 
     * 
     * For details of the action format see Action.loadActions(). 
     * 
     * If the action cannot be created (including that caused by an IOException)
     * an ActionFormatException should be thrown.
     * @param reader the reader to read the action contents form
     * @return the created action, or null if the reader is at the end of
     *      the file.
     * @throws ActionFormatException if the line has invalid contents and
     *                                the action cannot be created
     * @require reader != null
     */
    public static Action loadAction(java.io.BufferedReader reader)
                             throws ActionFormatException {
        // TODO: Implement Action.loadAction().
        return new Action(0, "DUMMY");
    }

    /**
     * Read all the actions from the given reader and perform them on the
     * given block world. 
     * 
     * All actions that can be performed should print an appropriate message
     * (as outlined in processAction()), any invalid actions that cannot
     * be created or performed on the world map, should also print an error message
     * (also described in processAction()). 
     * 
     * Each message should be printed on a new line (Use System.out.println()). 
     * 
     * Each action is listed on a single line, and one file can contain
     * multiple actions. 
     * 
     * Each action must be processed after it is read (i.e. do not read the whole
     * file first, read and process each action one at a time).
     * 
     * The file format is as follows:
     * 
     * <pre>
     * primaryAction1 secondaryAction1
     * primaryAction2 secondaryAction2
     * ...
     * primaryActionN secondaryActionN
     * </pre>
     * 
     * There is a single space " " between each primaryAction and secondaryAction. 
     * The primaryAction should be one of the following values:
     * <ul>
     * <li> MOVE_BUILDER </li>
     * <li> MOVE_BLOCK </li>
     * <li> DIG </li>
     * <li> DROP </li>
     * </ul>
     * 
     * 
     * If the secondaryAction is present, it should be one of the following values:
     * <ul>
     * <li> north </li>
     * <li> east </li>
     * <li> south </li>
     * <li> west </li>
     * <li> (a number) for DROP action </li>
     * </ul>
     * 
     * An example file may look like this:
     * <pre>
     * MOVE_BUILDER north
     * MOVE_BUILDER south
     * MOVE_BUILDER west
     * DROP 1
     * DROP 3
     * DROP text
     * DIG
     * MOVE_BUILDER south
     * MOVE_BLOCK north
     * RANDOM_ACTION
     * </pre>
     * 
     * If all actions can be performed on the map, the output from the above
     * file is:
     * <pre>
     * Moved builder north
     * Moved builder south
     * Moved builder west
     * Dropped a block from inventory
     * Dropped a block from inventory
     * Error: Invalid Action
     * Top block on current tile removed
     * Moved builder south
     * Moved block north
     * 
     * (The line "RANDOM_ACTION" should then cause an ActionFormatException to be thrown)
     * </pre>
     * 
     * 
     * Hint: Repeatedly call Action.loadAction() to get the next Action, and
     * then Action.processAction() to process the action.
     * @param reader the reader to read actions from
     * @param startingMap the starting map that actions will be applied to
     * @throws ActionFormatException if loadAction throws an
     *   ActionFormatException
     * @require reader != null, startingMap != null
     */
    public static void processActions(java.io.BufferedReader reader,
                                      WorldMap startingMap)
                               throws ActionFormatException {
        // TODO: Implement processActions(), but do processAction() first.
    }

    /**
     * Perform the given action on a WorldMap, and print output to System.out.
     * After this method
     * finishes, map should be updated. (e.g., If the action is DIG, the
     * Tile on which the builder is currently on should be updated to contain
     * 1 less block (Builder.digOnCurrentTile()). The builder to use for actions
     * is that given by map.getBuilder().
     * 
     * Do the following for these actions:
     * <ul>
     * <li>For DIG action: call Builder.digOnCurrentTile(), then print to console "Top block on
     *      current tile removed".</li>
     * <li>For DROP action: call Builder.dropFromInventory(), then print to console "Dropped a
     *      block from inventory". The dropped item is given by action.getSecondaryAction(), but it
     *      will need to be converted to an integer. </li>
     * <li>For the MOVE_BLOCK action: call Tile.moveBlock() on the builder's current tile
     *      (Builder.getCurrentTile()), then print to console "Moved block
     *      {direction}". The direction is given by action.getSecondaryAction()</li>
     * <li>For MOVE_BUILDER action: call Builder.moveTo(), then print to console "Moved
     *      builder {direction}". The direction is given by action.getSecondaryAction()</li>
     * <li>If action.getPrimaryAction() &lt; 0 or action.getPrimaryAction()
     *      &gt; 3, or action.getSecondary() is not a direction (for MOVE_BLOCK or MOVE_BUILDER),
     *      or a valid integer (for DROP) then print to console "Error: Invalid action" </li>
     * </ul>
     * "{direction}" is one of "north", "east", "south" or "west". 
     * 
     * For handling exceptions do the following:
     * <ul>
     * <li> If a NoExitException is thrown, print to the console "No exit this way" </li>
     * <li> If a TooHighException is thrown, print to the console "Too high" </li>
     * <li> If a TooLowException is thrown, print to the console "Too low" </li>
     * <li> If an InvalidBlockException is thrown, print to the console "Cannot
     *      use that block" </li>
     * </ul>
     * 
     * Each line printed to the console should have a trailing newline
     * (i.e., use System.out.println()).
     * @param action the action to be done on the map
     * @param map the map to perform the action on
     * @require action != null, map != null
     */
    public static void processAction(Action action, WorldMap map) {
        // TODO: Implement processAction()
    }

}