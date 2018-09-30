package csse2002.block.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an Action which can be performed on the world map.
 *
 * An action is something that a builder can do on a tile in the block world.
 * The actions include moving the builder in a direction, moving a block in
 * a direction, digging on the current tile and placing a block from the
 * builder's inventory.
 */

public class Action {
    // These should really be represented as an enum class...
    // Actually, we should have an ActionType superclass with subclasses of
    // specific actions.

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

    /** Valid compass direction names. */
    private static final Set<String> directionNames = new HashSet<>();
    static {
        directionNames.add("north");
        directionNames.add("east");
        directionNames.add("south");
        directionNames.add("west");
    }

    /**
     * Create an Action that represents a manipulation of the block world.
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
     * This constructor does not to check primaryAction or secondaryAction.
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
     * Gets the integer representing the Action type.
     * @return the primary action int.
     */
    public int getPrimaryAction() {
        return primaryAction;
    }

    /**
     * Gets the supplementary information associated with the Action
     * @return the secondary action or an empty string if no secondary
     *  action is required.
     */
    public String getSecondaryAction() {
        return secondaryAction;
    }

    /**
     * Reads the next line of the given reader and creates a single Action.
     *
     * Return null if the reader is at the end of the file.
     * 
     * For details of the action format see
     * {@link #processActions(BufferedReader, WorldMap)}.
     * 
     * If the action cannot be created (including that caused by an IOException),
     * an ActionFormatException is be thrown.
     * @param reader the reader to read the action contents from.
     * @return the created action, or null if the reader is at the end of
     *      the file.
     * @throws ActionFormatException if the line has invalid contents and
     *                                the action cannot be created.
     * @require reader != null
     */
    public static Action loadAction(BufferedReader reader)
                             throws ActionFormatException {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new ActionFormatException("IOException occurred.");
        }
        // Ensure input is valid for loadActionFromString.
        if (line == null) {
            return null;
        } else {
            return loadActionFromString(line);
        }
    }

    /**
     * Loads a single action given by actionString.
     *
     * @param actionString action string, non-null.
     * @return loaded action.
     * @throws ActionFormatException if primary action is not valid, secondary
     * exists for DIG or secondary doesn't exist for MOVE_BUILDER, MOVE_BLOCK
     * or DROP.
     */
    private static Action loadActionFromString(String actionString)
            throws ActionFormatException {
        String[] split = actionString.split(" ", -1);
        if (split.length > 2) {
            // Throw if 2 or more spaces are present.
            throw new ActionFormatException("Incorrect number of spaces.");
        }

        // Detect what type of primary action it is.
        String primary = split[0];
        int primaryID;
        switch (primary) {
            case "MOVE_BUILDER":
                primaryID = MOVE_BUILDER;
                break;
            case "MOVE_BLOCK":
                primaryID = MOVE_BLOCK;
                break;
            case "DIG":
                primaryID = DIG;
                break;
            case "DROP":
                primaryID = DROP;
                break;
            default:
                throw new ActionFormatException(
                        "Invalid primary action: "+primary);
        }

        // Validate secondary action based on primary type.
        // Null indicates an invalid secondary.
        String secondary = null;
        switch (primaryID) {
            // These require some secondary action.
            case MOVE_BUILDER:
            case MOVE_BLOCK:
            case DROP:
                // Using ternary so we don't need _another_ switch/case.
                // If !secondaryValid, we will throw, avoiding NPEs.
                secondary = split.length == 2 ? split[1] : null;
                break;
            case DIG: // DIG mandates no secondary action.
                secondary = split.length == 1 ? "" : null;
                break;
            default: // Invalid. Leave null.
        }

        if (secondary == null) {
            throw new ActionFormatException(
                    "Secondary action exists for DIG or doesn't exist for"
                    +" MOVE_BUILDER, MOVE_BLOCK or DROP.");
        }

        // If we reach here, we have a valid primary and secondary.
        // Return a new action.
        return new Action(primaryID, secondary);
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
    public static void processActions(BufferedReader reader,
                                      WorldMap startingMap)
                               throws ActionFormatException {
        Action action;
        // Inline variable setting. loadAction throws AFE for us.
        while ((action = loadAction(reader)) != null) {
            processAction(action, startingMap);
        }
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
     * <li>For DIG action: call Builder.digOnCurrentTile(),
     *     then print to console "Top block on current tile removed".</li>
     * <li>For DROP action: call Builder.dropFromInventory(), then print to
     *     console "Dropped a block from inventory". The dropped item is given
     *     by action.getSecondaryAction().</li>
     * <li>For MOVE_BLOCK action: call Tile.moveBlock() on the builder's
     *     current tile, then print to console "Moved block {direction}".
     *     The direction is given by action.getSecondaryAction()</li>
     * <li>For MOVE_BUILDER action: call Builder.moveTo(), then print to
     *     console "Moved builder {direction}". The direction is given by
     *     action.getSecondaryAction()</li>
     * <li>If action.getPrimaryAction() &lt; 0 or action.getPrimaryAction()
     *      &gt; 3, or action.getSecondary() is not a direction
     *      (for MOVE_BLOCK or MOVE_BUILDER), or is not a valid integer
     *      (for DROP) then print to console "Error: Invalid action" </li>
     * </ul>
     *
     * "{direction}" is one of "north", "east", "south" or "west". 
     * 
     * For handling exceptions do the following:
     * <ul>
     * <li> If a NoExitException is thrown, print to the console
     *      "No exit this way"</li>
     * <li> If a TooHighException is thrown, print to the console "Too high"</li>
     * <li> If a TooLowException is thrown, print to the console "Too low"</li>
     * <li> If an InvalidBlockException is thrown, print to the console "Cannot
     *      use that block"</li>
     * </ul>
     * @param action the action to be done on the map
     * @param map the map to perform the action on
     * @require action != null, map != null
     */
    public static void processAction(Action action, WorldMap map) {
        boolean success = false;
        try {
            // Offload to helper to avoid excessive nesting.
            // The current function only handles printing to the console.
            unsafeProcessAction(action, map);
            success = true;
        } catch (ActionFormatException e) {
            System.out.println("Error: Invalid action");
        } catch (NoExitException e) {
            System.out.println("No exit this way");
        } catch (TooHighException e) {
            System.out.println("Too high");
        } catch (TooLowException e) {
            System.out.println("Too low");
        } catch (InvalidBlockException e) {
            System.out.println("Cannot use that block");
        }

        if (!success) {
            // Error message already printed, don't print success message.
            return;
        }

        // Print appropriate success message.
        switch (action.getPrimaryAction()) {
            case DIG:
                System.out.println("Top block on current tile removed");
                break;
            case DROP:
                System.out.println("Dropped a block from inventory");
                break;
            case MOVE_BUILDER:
                System.out.println("Moved builder " + action.secondaryAction);
                break;
            case MOVE_BLOCK:
                System.out.println("Moved block " + action.secondaryAction);
                break;
        }
    }

    /**
     * Executes the given action on the given map.
     *
     * Called "unsafe" because it throws exceptions all over the place.
     *
     * @param action Action to perform.
     * @param map Map to perform action on.
     * @throws NoExitException if the action resulted in this exception.
     * @throws TooHighException if the action resulted in this exception.
     * @throws TooLowException if the action resulted in this exception.
     * @throws InvalidBlockException if the action resulted in this exception.
     * @throws ActionFormatException action is invalid (e.g. invalid secondary).
     */
    private static void unsafeProcessAction(Action action, WorldMap map)
            throws NoExitException, TooHighException, TooLowException,
                   InvalidBlockException, ActionFormatException {
        int primary = action.primaryAction;
        String secondary = action.secondaryAction;

        // Validate the actual value of the secondary field.
        boolean secondaryValueValid = false;
        switch (primary) {
            case DIG:
                secondaryValueValid = secondary.equals("");
                break;
            case MOVE_BUILDER:
            case MOVE_BLOCK:
                secondaryValueValid = directionNames.contains(secondary);
                break;
            case DROP:
                // Although exceptions are fairly slow, we expect most input
                // cases would be valid integers and not throw.
                try {
                    //noinspection unused
                    Integer unused = Integer.parseInt(secondary);
                    secondaryValueValid = true;
                } catch (NumberFormatException e) {
                    secondaryValueValid = false;
                }
                break;
            default:
                // Will reach here if primary is invalid. Throws the same
                // exception as invalid secondary values.
        }

        if (!secondaryValueValid) {
            throw new ActionFormatException("Secondary action value invalid.");
        }

        // Perform the actions.
        Builder builder = map.getBuilder();
        Tile currentTile = builder.getCurrentTile();
        switch (primary) {
            case DIG:
                builder.digOnCurrentTile();
                break;
            case MOVE_BUILDER:
                // If no such exit exists, .get() will return null and .moveTo()
                // will throw NoExit as required.
                builder.moveTo(currentTile.getExits().get(secondary));
                break;
            case MOVE_BLOCK:
                currentTile.moveBlock(secondary);
                break;
            case DROP:
                builder.dropFromInventory(Integer.parseInt(secondary));
                break;
        }
    }

}
