package edu.mit.kacquah.deckviewer.speech;

/**
 * Command constants replicated from deckviewer.gram. This is used for parsing
 * actions from speech and passing commands throughout the app.
 * 
 * @author kojo
 * 
 */
public class Commands {
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
   * Deck Objects & Locations
   */
  // Catapults
  public static final String CATAPULT_1 = "catapult one";
  public static final String CATAPULT_2 = "catapult two";
  public static final String CATAPULT_3 = "catapult three";
  public static final String CATAPULT_4 = "catapult four";
  // Generic locations
  public static final String THERE = "there";
  public static final String HERE = "here";
}
