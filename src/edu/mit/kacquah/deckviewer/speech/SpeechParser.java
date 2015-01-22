package edu.mit.kacquah.deckviewer.speech;

import java.util.logging.Logger;

import edu.cmu.sphinx.api.SpeechResult;
import edu.mit.kacquah.deckviewer.action.ActionCommand;
import edu.mit.kacquah.deckviewer.action.ActionManager;
import edu.mit.kacquah.deckviewer.action.SelectionManager;
import edu.mit.kacquah.deckviewer.action.ActionCommand.ActionCommandType;
import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.speech.SpeechEngine.ISpeechEventListener;

/**
 * Parser for processing speech commands.
 * 
 * @author kojo
 * 
 */
public class SpeechParser implements ISpeechEventListener {
  private static Logger LOGGER = Logger.getLogger(SpeechParser.class.getName());
  ActionManager actionManager;

  public SpeechParser() {
  }

  // public void setSelectionManager(SelectionManager m) {
  // this.selectionManager = m;
  // }

  public void setActionManager(ActionManager m) {
    this.actionManager = m;
  }

  @Override
  public void handleSpeechResult(SpeechResult result) {
    String command = result.getHypothesis().toLowerCase();

    boolean success;

    // Since commands come in two types (action + selection) or (location), we
    // simply check for text unique to each sequence and handle appropriately.
    if (command.contains(Commands.MOVE) || command.contains(Commands.PLACE)) {
      success = createMoveAction(command);
    } else if (command.contains(Commands.TO) || command.contains(Commands.OVER)) {
      success = createLocationAction(command);
    } else {
      success = false;
    }

    if (!success) {
      // We don't understand this command...
      LOGGER.severe("Unable to parse command:" + command);
    }
  }

  /**
   * Creats a move action for the ActionManager.
   * 
   * @param command
   * @return
   */
  public boolean createMoveAction(String command) {
    ActionCommand actionCommand = new ActionCommand(ActionCommandType.MOVE,
        command);
    actionCommand.aircraftType = AircraftType.F18;
    actionManager.processActionCommand(actionCommand);
    return true;
  }

  /**
   * Creates a location action for the ActionManager.
   * 
   * @param command
   * @return
   */
  public boolean createLocationAction(String command) {
    ActionCommand actionCommand = new ActionCommand(ActionCommandType.LOCATION,
        command);
    // Determine location
    int number = 0;
    if (command.contains(ActionCommand.CATAPULT)) {
      number = getNumberFromCommmand(command);
      actionCommand.locationType = ActionCommand.LocationType.CATAPULT;
      actionCommand.locationNumber = number;
    } else if (command.contains(ActionCommand.ELEVATOR)) {
      number = getNumberFromCommmand(command);
      actionCommand.locationType = ActionCommand.LocationType.ELEVATOR;
      actionCommand.locationNumber = number;
    } else if (command.contains(ActionCommand.THERE)
        || command.contains(ActionCommand.HERE)) {
      actionCommand.locationType = ActionCommand.LocationType.POINTING;
    } else {
      return false;
    }
    if (number == -1) {
      return false;
    }
    actionManager.processActionCommand(actionCommand);
    return true;
  }

  private int getNumberFromCommmand(String command) {
    if (command.contains(ActionCommand.ONE)) {
      return 1;
    } else if (command.contains(ActionCommand.TWO)) {
      return 2;
    } else if (command.contains(ActionCommand.THREE)) {
      return 3;
    } else if (command.contains(ActionCommand.FOUR)) {
      return 4;
    } else {
      return -1;
    }
  }
}
