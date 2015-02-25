package edu.mit.kacquah.deckviewer.action;

import java.awt.Point;
import java.net.Socket;
import java.util.Iterator;
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
import edu.mit.kacquah.deckviewer.utils.Sorting;

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
  
  /**
   * Updated on every update cycle. Aircraft currently under the finger point.
   */
  LinkedList<FlyingObject> hoverObjects;
  
  
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
    this.hoverObjects = new LinkedList<FlyingObject>();
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
    // Get finer point
    Point fingerPoint = getCurrentFingerPoint();
    if (fingerPoint == null) {
      return false;
    }
    
    LinkedList<FlyingObject> potentialObjects = this.getHoverObjects();
    
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
    Point fingerPoint = handTracker.getFingerPoint();
    if (fingerPoint == null) {
      currentError = ActionError.NO_FINGERS_ON_SCREEN;
      LOGGER.severe(currentError.description);
      return null;
    }
    return fingerPoint;

//    if (!deck.contains(fingerPoint)) {
//      currentError = ActionError.TARGET_NOT_ON_DECK;
//      LOGGER.severe(currentError.description);
//      return null;
//    } else {
//      return fingerPoint;
//    }
  }
  
  public boolean isOnDeck(Point p) {
    if (!deck.contains(p)) {
      currentError = ActionError.TARGET_NOT_ON_DECK;
      LOGGER.severe(currentError.description);
      return false;
    } else {
      return true;
    }
  }
  
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
      if (hoverObjects.contains(o)) {
        hoverObjects.remove(o);
      }
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
  
  public LinkedList<FlyingObject> getSelection() {
    return this.selectedObjects;
  }
  
  /**
   * Synchronized access for hoverObjects list.
   * @return
   */
  public LinkedList<FlyingObject> getHoverObjects() {
    synchronized(this) {
      return new LinkedList<FlyingObject>(this.hoverObjects);
    }
  }

  @Override
  public void update(long elapsedTime) {
    
    // Get finer point
    Point fingerPoint = getCurrentFingerPoint();
    if (fingerPoint == null) {
      return;
    }

    synchronized(this) {
      LinkedList<FlyingObject> newHoverObjects = flyingObjectManager
          .intersectsPoint(fingerPoint);
      
      // Did we select anything?
      if (newHoverObjects.isEmpty()) {
        // If we're using sticky selection. use the closest flying object if
        // its in the selection radius.
        if (GlobalSettings.USE_STICKY_SELECTION) {
          newHoverObjects = flyingObjectManager.sortFlyingObjectsToTarget(
              fingerPoint, newHoverObjects);
          FlyingObject closestFlyingObject = newHoverObjects.get(0);
          newHoverObjects.clear();
          float scaleRatio = Deck.getInstance().scaleRatio;
          if (closestFlyingObject.position().distance(fingerPoint) < scaleRatio
              * GlobalSettings.STICKY_SELECTION_RADIUS) {
            newHoverObjects.add(closestFlyingObject);
          }
        }
      }
         
      // Highlight new hover objects, remove old ones. 
      Iterator<FlyingObject> i = hoverObjects.iterator();
      while (i.hasNext()) {
        FlyingObject o = i.next();
        if (!newHoverObjects.contains(o) && o.selectionStatus() == SelectionStatus.HOVERIRNG) {
          o.setSelectionStatus(SelectionStatus.NONE);
          i.remove();
        }
      }
      
      for (FlyingObject o : newHoverObjects) {
        if (!hoverObjects.contains(o) && o.selectionStatus() == SelectionStatus.NONE) {
          o.setSelectionStatus(SelectionStatus.HOVERIRNG);
          hoverObjects.add(o);
        }
      }
    } // end synchronized this.
  }

  @Override
  public void render(PApplet p) {
    // TODO Auto-generated method stub

  }

}
