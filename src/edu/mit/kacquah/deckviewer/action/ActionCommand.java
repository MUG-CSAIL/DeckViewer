package edu.mit.kacquah.deckviewer.action;

import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;

public class ActionCommand {
  /**
   * Parts of speech.
   */
  public static final String ONE = "one";
  public static final String TWO = "two";
  public static final String THREE = "three";
  public static final String FOUR = "four";
  public static final String FIVE = "five";
  public static final String SIX = "six";
  public static final String SEVEN = "seven";
  public static final String EIGHT = "eight";
  public static final String NINE = "nine";
  public static final String TEN = "ten";
  /**
   * Prepositions
   */
  public static final String TO = "to";
  public static final String OVER = "over";
  /**
   * Actions
   */
  public static final String MOVE = "move";
  public static final String PLACE = "place";
  /**
   * Deck Objects
   */
  public static final String AIRCRAFT = "aircraft";
  /**
   * Deck Locations
   */
  // Catapults
  public static final String CATAPULT = "catapult";
  public static final String CATAPULT_1 = "catapult one";
  public static final String CATAPULT_2 = "catapult two";
  public static final String CATAPULT_3 = "catapult three";
  public static final String CATAPULT_4 = "catapult four";
  // Elevators
  public static final String ELEVATOR = "elevator";
  public static final String ELEVATOR_1 = "elevator one";
  public static final String ELEVATOR_2 = "elevator two";
  public static final String ELEVATOR_3 = "elevator three";
  public static final String ELEVATOR_4 = "elevator four";  
  // Generic locations
  public static final String POINTING_LOCATION = "pointing_location";
  public static final String THERE = "there";
  public static final String HERE = "here";
  
  /**
   * Denotes to the type of action command.
   * @author kojo
   *
   */
  public enum ActionCommandType {
    MOVE, LOCATION, MOVE_TO_LOCATION;
  }
  
  public enum LocationType {
    POINTING, ELEVATOR, CATAPULT;
  }
  
  /****************************************************************************/
  /* Members **************************************************************** */
  /****************************************************************************/  
  public final ActionCommandType commandType;
  public AircraftType aircraftType;
  public LocationType locationType;
  public int locationNumber;
  public final String text;
  
  public ActionCommand(ActionCommandType type, String text) {
    this.commandType = type;
    this.text = text;
    this.locationNumber = -1;
  }
  
  /**
   * Merges two action commands. Note, only specific action commands can be merged.
   * @param first
   * @param second
   * @return
   */
  public static ActionCommand mergeActionCommands(ActionCommand first, ActionCommand second) {
    if (first.commandType == ActionCommandType.MOVE
        && second.commandType == ActionCommandType.LOCATION) {
      ActionCommand result = new ActionCommand(ActionCommandType.MOVE_TO_LOCATION, first.text + "||" +second.text);
      result.aircraftType = first.aircraftType;
      result.locationType = second.locationType;
      result.locationNumber = second.locationNumber;
      return result;
    } else {
      return null;
    }
    
  }
  
  
  

}
