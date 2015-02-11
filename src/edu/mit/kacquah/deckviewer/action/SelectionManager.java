package edu.mit.kacquah.deckviewer.action;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.vecmath.Point2f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.gesture.HandTracker;
import edu.mit.kacquah.deckviewer.image.ColorHighlightFilter;
import edu.mit.kacquah.deckviewer.speech.recognizer.SpeechParser;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * State machine for handling aircraft selection and action execution.
 * 
 * @author kojo
 * 
 */
public class SelectionManager implements PAppletRenderObject { 
  public enum SelectionStatus {
    SELECTED,
    HOVERIRNG,
    ERROR,
    NONE;
  }
  
  // Utils
  private static Logger LOGGER = Logger.getLogger(SelectionManager.class
      .getName());

  // References
  private PApplet parent;
  private FlyingObjectManager flyingObjectManager;
  private Deck deck;
  private HandTracker handTracker;

  // Selection state.
  private boolean hasSelection;
  private  LinkedList<FlyingObject> selectedObjects;
  
  // Error status
  ActionError currentError;

  public SelectionManager(PApplet p, FlyingObjectManager f, Deck d,
      HandTracker t) {
    // Init references
    this.parent = p;
    this.flyingObjectManager = f;
    this.deck = d;
    this.handTracker = t;

    // Init state
    this.hasSelection = false;
  }

  /**
   * Set the current flying object manager used for selection.
   * 
   * @param m
   */
  public void setFlyingObjectManager(FlyingObjectManager m) {
    this.flyingObjectManager = m;
  }
  
  /**
   * Selects an aircraft with a particular UID.
   * @param number
   * @return
   */
  public boolean selectAircraftWithNumber(int number) {
    FlyingObject selectedObject = flyingObjectManager.getAircraftWithUID(number);
    if (selectedObject == null) {
      currentError = ActionError.NO_AIRCRAFT_NUMBER;
      LOGGER.severe(currentError.description);
      return false;
    }
    LinkedList<FlyingObject> potentialObjects = new LinkedList<FlyingObject>();
    potentialObjects.add(selectedObject);
    // Clean last selection and update new selection.
    selectObjects(potentialObjects);
    return true;
  }
  
  /**
   * Selects aircraft at a specific location. Multiple selection will select
   * additional surrounding aircraft.
   * 
   * @return
   */
  public boolean selectAircraftAtFingerLocation(boolean multipleSelection, AircraftType typeRestriction) {
    // Get finer points
    Point2f fingerPoints[] = handTracker.getFilteredPoints();
    if (fingerPoints.length == 0) {
      currentError = ActionError.NO_FINGERS_ON_SCREEN;
      LOGGER.severe(currentError.description);
      return false;
    }

    // Take the first finger point for selection.
    Point fingerPoint = new Point((int) fingerPoints[0].x,
        (int) fingerPoints[0].y);
    LinkedList<FlyingObject> potentialObjects = flyingObjectManager.intersectsPoint(fingerPoint);

    if (potentialObjects.size() == 0) {
      currentError = ActionError.SELECTION_FAILED;
      LOGGER.severe(currentError.description);
      return false;
    }
    
    if (multipleSelection) {
      potentialObjects = selectSurroundingObjects(potentialObjects, typeRestriction);
    }
    
    // Clean last selection and update new selection.
    selectObjects(potentialObjects);
    return true;
  }
  
  /**
   * Selects aircraft surrounding this aircraft.
   * @param potentialObjects
   */
  private LinkedList<FlyingObject> selectSurroundingObjects(
      LinkedList<FlyingObject> potentialObjects, AircraftType typeRestriction) {
    // We start of with one selected aircraft.
    FlyingObject centerAircraft = potentialObjects.get(0);
    ParkingSpot parkingSpot = centerAircraft.getParkingSpot();
    
    if (parkingSpot != null) {
      /**
       * If the aircraft is on a parking spot, select all aircraft within the parking region;
       */
      ParkingRegion parkingRegion = parkingSpot.parkingRegion();
      return parkingRegion.getParkedAircraft(typeRestriction);
    } else {
      // Not implemented yet
      throw new UnsupportedOperationException();
    }
  }
  
  /**
   * Returns the current position of the fingers. Checks for on deck.
   * @return
   */
  public Point getCurrentFingerPoint() {
    // Get finer points
    Point2f fingerPoints[] = handTracker.getFilteredPoints();
    if (fingerPoints.length == 0) {
      currentError = ActionError.NO_FINGERS_ON_SCREEN;
      LOGGER.severe(currentError.description);
      return null;
    }

    // Take the first finger point for the target.
    Point fingerPointTarget = new Point((int) fingerPoints[0].x,
        (int) fingerPoints[0].y);
    
    if (!deck.contains(fingerPointTarget)) {
      currentError = ActionError.TARGET_NOT_ON_DECK;
      LOGGER.severe(currentError.description);
      return null;
    } else {
      return fingerPointTarget;
    }
  }

  /**
   * Initiates selection of deck object(s) with a given action. Returns true or
   * false depending on selection success.
   * 
   * @param action
   */
//  public boolean selectWithAction(String action) {
//    // Get finer points
//    Point2f fingerPoints[] = handTracker.getFilteredPoints();
//    if (fingerPoints.length == 0) {
//      LOGGER.severe("Cannot select without finger points.");
//      return false;
//    }
//
//    // Take the first finger point for selection.
//    Point fingerPoint = new Point((int) fingerPoints[0].x,
//        (int) fingerPoints[0].y);
//    LinkedList<FlyingObject> potentialObjects = flyingObjectManager.intersectsPoint(fingerPoint);
//
//    if (potentialObjects.size() == 0) {
//      LOGGER.severe("Selection failed to find any aircraft.");
//      return false;
//    }
//    
//    // Clean last selection and update new selection.
//    clearSelection();
//    selectObjects(potentialObjects, action);
//    return true;
//  }

  /**
   * Initiates execution of an action on selected deck object(s) with a given
   * target. If there are no currently selected objects or actions, execution
   * fails. Returns true/false depending on success of execution.
   * 
   * @param target
   */
//  public boolean executeActionWithTarget(String target) {
//    if (!hasSelection) {
//      LOGGER.severe("Need to select aircraft first.");
//      return false;
//    }
//
//    // Get finer points
//    Point2f fingerPoints[] = handTracker.getFilteredPoints();
//    if (fingerPoints.length == 0) {
//      LOGGER.severe("Cannot select without finger points.");
//      return false;
//    }
//
//    // Take the first finger point for the target.
//    Point fingerPointTarget = new Point((int) fingerPoints[0].x,
//        (int) fingerPoints[0].y);
//
//    if (!deck.contains(fingerPointTarget)) {
//      LOGGER.severe("Target must be located on arcraft deck.");
//      return false;
//    }
//
//    // Attempt to move object and test for intersections.
//    FlyingObject selectedObject = selectedObjects.get(0);
//    Point2f oldPosition = selectedObject.getPosition();
//    selectedObject.setPosition(fingerPointTarget.x, fingerPointTarget.y);
//    LinkedList<FlyingObject> possibleIntersections = flyingObjectManager
//        .intersectsFlyingObjects(selectedObject);
//    if (possibleIntersections.size() > 1) {
//      selectedObject.setPosition(oldPosition.x, oldPosition.y);
//      LOGGER.severe("Cannot place aircraft in position with intersections.");
//      return false;
//    }
//
//    // Clear selection after executing action.
//    clearSelection();
//
//    return true;
//  }
  
  /**
   * Selects flying objects and updates state.
   * @param objects
   */
  private void selectObjects(LinkedList<FlyingObject> objects) {
    clearSelection();
    // Update state.
    selectedObjects = objects;
    hasSelection = true;
    currentError = ActionError.SUCCESS;
    // Highlight the objects.
    for (FlyingObject o : selectedObjects) {
      o.setSelectionStatus(SelectionStatus.SELECTED);
    }
  }

  /**
   * Reset the current selection.
   */
  public void clearSelection() {
    // Remove highlights.
    if (selectedObjects != null) {
      for (FlyingObject o : selectedObjects) {
        o.setSelectionStatus(SelectionStatus.NONE);
      }
    }
    // Update state.
    hasSelection = false;
    selectedObjects = null;
    currentError = ActionError.NO_ERROR;
  }
  
  public boolean hasSelection() {
    return this.hasSelection;
  }
  
  public ActionError getError() {
    return this.currentError;
  }
  
  LinkedList<FlyingObject> getSelection() {
    return this.selectedObjects;
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(PApplet p) {
    // TODO Auto-generated method stub

  }

}
