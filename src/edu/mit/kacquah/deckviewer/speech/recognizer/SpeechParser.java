package edu.mit.kacquah.deckviewer.speech.recognizer;

import java.util.logging.Logger;

import edu.cmu.sphinx.api.SpeechResult;
import edu.mit.kacquah.deckviewer.action.ActionCommand;
import edu.mit.kacquah.deckviewer.action.ActionManager;
import edu.mit.kacquah.deckviewer.action.SelectionManager;
import edu.mit.kacquah.deckviewer.action.ActionCommand.ActionCommandType;
import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion.ParkingRegionType;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.speech.recognizer.SpeechRecognizer.ISpeechEventListener;

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
    if (command.contains(ActionCommand.MOVE)) {
      success = createMoveAction(command);
    } else if (command.contains(ActionCommand.TO) || command.contains(ActionCommand.OVER)) {
      success = createLocationAction(command);
    } else if (command.contains(ActionCommand.YES)
        || command.contains(ActionCommand.OK)
        || command.contains(ActionCommand.NO)) {
      success = createAffirmativeCommand(command);
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
    // Determine aircraft type
    int number = getNumberFromCommmand(command);
    if (command.contains("number")) {
      actionCommand.aircraftNumber = number;
    } else if (command.contains(ActionCommand.C2)) {
      actionCommand.aircraftType = AircraftType.C2;
    } else if (command.contains(ActionCommand.F18)) {
      actionCommand.aircraftType = AircraftType.F18;
    } else if (command.contains(ActionCommand.AIRCRAFT)) {
      actionCommand.aircraftType = AircraftType.AIRCRAFT;
    }
    // Check for multiple selection
    if (command.contains(ActionCommand.THESE)) {
      actionCommand.multipleSelection = true;
    } else {
      actionCommand.multipleSelection = false;
    }
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
    
    if (command.contains(ActionCommand.CATAPULTS)) {
      // Location is group of catapults
      actionCommand.locationType = ActionCommand.LocationType.CATAPULT;
      String[] splits = command.split("and");
      actionCommand.locationNumber = getNumberFromCommmand(splits[0]);
      actionCommand.locationNumber2 = getNumberFromCommmand(splits[1]);
      
    } else if (command.contains(ActionCommand.CATAPULT)) {
      // Location is a single catapult
      number = getNumberFromCommmand(command);
      actionCommand.locationType = ActionCommand.LocationType.CATAPULT;
      actionCommand.locationNumber = number;
      
    } else if (command.contains(ActionCommand.ELEVATOR)) {
      // Location is an elevator
      number = getNumberFromCommmand(command);
      actionCommand.locationType = ActionCommand.LocationType.ELEVATOR;
      actionCommand.locationNumber = number;
      
    } else if (command.contains(ActionCommand.THERE)) {
      // Location is where finger is pointing
      actionCommand.locationType = ActionCommand.LocationType.POINTING;
      
    } else {
      // Location is a parking region
      actionCommand.locationType = ActionCommand.LocationType.PARKING_REGION;
      ParkingRegionType region =  parseParkingRegionType(command);
      if (region == null) {
        LOGGER.severe("ParkingRegion did not parse");
        return false;
      }
      actionCommand.parkingRegionType = region;
    }
    
    if (number == -1) {
      return false;
    }
    
    actionManager.processActionCommand(actionCommand);
    return true;
  }
  
  /**
   * Determins the parking region destination in this command.
   * @param command
   * @return
   */
  public ParkingRegionType parseParkingRegionType(String command) {
    int index = command.indexOf(ActionCommand.THE);
    if (index == -1) {
      return null;
    }
    command = command.substring(index + ActionCommand.THE.length() + 1);
    return ParkingRegionType.stringToParkingRegion(command);
  }
  
  /**
   * Creates an affirmative command.
   * @param command
   * @return
   */
  public boolean createAffirmativeCommand(String command) {
    ActionCommand actionCommand = new ActionCommand(ActionCommandType.AFFIRMATIVE,
        command);
    // Determine Affirmative.
    switch(command.toLowerCase()) {
      case ActionCommand.YES:
        actionCommand.affirmative = true;
        break;
      case ActionCommand.OK:
        actionCommand.affirmative = true;
        break;
      case ActionCommand.NO:
        actionCommand.affirmative = false;
        break;
      default:
        return false;
    } 
    actionManager.processActionCommand(actionCommand);
    return true;
  }

  /**
   * Parses a number from a speech command.
   * @param command
   * @return
   */
  private int getNumberFromCommmand(String command) {
    if (command.contains(ActionCommand.ONE)) {
      return 1;
    } else if (command.contains(ActionCommand.TWO)) {
      return 2;
    } else if (command.contains(ActionCommand.THREE)) {
      return 3;
    } else if (command.contains(ActionCommand.FOUR)) {
      return 4;
    } else if (command.contains(ActionCommand.FIVE)) {
      return 5;
    } else if (command.contains(ActionCommand.SIX)) {
      return 6;
    } else if (command.contains(ActionCommand.SEVEN)) {
      return 7;
    } else if (command.contains(ActionCommand.EIGHT)) {
      return 8;
    } else if (command.contains(ActionCommand.NINE)) {
      return 9;
    } else if (command.contains(ActionCommand.TEN)) {
      return 10;
    } else {
      return -1;
    }
  }
}
