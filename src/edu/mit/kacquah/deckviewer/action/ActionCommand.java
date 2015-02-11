package edu.mit.kacquah.deckviewer.action;

import java.util.logging.Logger;

import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion.ParkingRegionType;
import edu.mit.kacquah.deckviewer.speech.recognizer.PushToTalkPApplet;

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
   * Pronoun
   */
  public static final String THESE = "these";
  public static final String THIS = "this";
  /**
   * Prepositions
   */
  public static final String TO = "to";
  public static final String OVER = "over";
  /**
   * Adjectives
   */
  public static final String THE = "the";
  /**
   * Afffirmative
   */
  public static final String YES = "yes";
  public static final String OK = "ok";
  public static final String NO = "no";
  
  /**
   * Actions
   */
  public static final String MOVE = "move";
  /**
   * Deck Objects
   */
  public static final String AIRCRAFT = "aircraft";
  public static final String F18 = "f eighteen";
  public static final String C2 = "c two";
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
  // Parking Regions
  public static final String FANTAIL = "fantail";
  public static final String OVER_EL1_AND_EL2 = "over elevator one and two";
  public static final String BTWN_EL1_AND_CAT = "between elevator one and the catapults";
  public static final String BHND_TOWER = "behind the tower";
  public static final String OVER_EL4 = "over elevator four";
  public static final String FRNT_TOWER = "in front of the tower";
  
  public static final String STREET = "street";
  public static final String SIXPACK = "six yeipack";
  public static final String POINT = "point";
  public static final String PATIO = "patio";
  public static final String CROTCH = "crotch";
  public static final String CORRAL = "corral";
  public static final String JUNK_YARD = "junk yard";
  // Generic locations
  public static final String POINTING_LOCATION = "pointing_location";
  public static final String THERE = "there";

  
  /**
   * Denotes to the type of action command.
   * @author kojo
   *
   */
  public enum ActionCommandType {
    MOVE, LOCATION, MOVE_TO_LOCATION, AFFIRMATIVE;
  }
  
  /**
   * Denotes to the location target.
   * @author kojo
   *
   */
  public enum LocationType {
    POINTING, CATAPULT, ELEVATOR, PARKING_REGION;
  }
  
  /****************************************************************************/
  /* Members **************************************************************** */
  /****************************************************************************/
  // App utils
  private static Logger LOGGER = Logger.getLogger(ActionCommand.class
      .getName());
  
  public final ActionCommandType commandType;
  public AircraftType aircraftType;
  public boolean multipleSelection;
  public int aircraftNumber;
  public LocationType locationType;
  public ParkingRegionType parkingRegionType;
  public int locationNumber;
  public final String text;
  public boolean affirmative;
  
  public ActionCommand(ActionCommandType type, String text) {
    this.commandType = type;
    this.text = text;
    this.locationNumber = -1;
    this.aircraftNumber = -1;
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
      result.multipleSelection = first.multipleSelection;
      result.locationType = second.locationType;
      result.locationNumber = second.locationNumber;
      result.parkingRegionType = second.parkingRegionType;
      return result;
    } else {
      LOGGER.severe("Could not merge ActionCommands " + first.toString() + " and " + second.toString());
      return null;
    }
    
  }
  
  
  

}
