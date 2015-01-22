package edu.mit.kacquah.deckviewer.speech;

public enum ActionError {
  NO_ERROR("No Error."),
  SUCCESS("Success."),
  NO_FINGERS_ON_SCREEN("Cannot select without finger points."),
  SELECTION_FAILED("Selection failed to find any aircraft."),
  NO_SELECTION("Must select aircraft."),
  ACTION_MANAGER_ERROR("Problem with actionManager"),
  TARGET_NOT_ON_DECK("Target must be located on arcraft deck."),
  AIRCRAFT_COLLISIONS("Cannot place aircraft in position with intersections.");
  
  public final String description;
  ActionError(String description) {
    this.description = description;
  }
  
  public final String description() {
    return this.description;
  }
  
}
