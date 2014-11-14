package edu.mit.kacquah.deckviewer.action;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.vecmath.Point2f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.Deck;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.gesture.HandTracker;
import edu.mit.kacquah.deckviewer.speech.SpeechParser;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * State machine for handling aircraft selection and action execution.
 * 
 * @author kojo
 * 
 */
public class SelectionManager implements PAppletRenderObject {
  // Utils
  private static Logger LOGGER = Logger.getLogger(SelectionManager.class
      .getName());
  
  // References
  PApplet parent;
  FlyingObjectManager flyingObjectManager;
  Deck deck;
  HandTracker handTracker;
  
  // Selection state.
  boolean hasSelection;
  String currentAction;
  LinkedList<FlyingObject> selectedObjects;

  public SelectionManager(PApplet p, FlyingObjectManager f, Deck d, HandTracker t) {
    // Init references
    this.parent = p;
    this.flyingObjectManager = f;
    this.deck = d;
    this.handTracker = t;
    
    // Init state
    this.hasSelection = false;
  }

  public void setFlyingObjectManager(FlyingObjectManager m) {
    this.flyingObjectManager = m;
  }

  /**
   * Initiates selection of deck object(s) with a given action. Returns true or
   * false depending on selection success.
   * 
   * @param action
   */
  public boolean selectWithAction(String action) {
    // Get finer points
    Point2f fingerPoints[] = handTracker.getFilteredPoints();
    if (fingerPoints.length == 0) {
      LOGGER.severe("Cannot select without finger points.");
      return false;
    }
    
    // Take the first finger point for selection.
    Point fingerPoint = new Point((int)fingerPoints[0].x, (int)fingerPoints[0].y);
    selectedObjects = flyingObjectManager.intersectsPoint(fingerPoint);
    
    if (selectedObjects.size() == 0) {
      LOGGER.severe("Selection failed to find any aircraft.");
      return false;
    }
    
    // Update state
    currentAction = action;
    hasSelection = true;
    
    return true;
  }

  /**
   * Initiates execution of an action on selected deck object(s) with a given
   * target. If there are no currently selected objects or actions, execution
   * fails. Returns true/false depending on success of execution.
   * 
   * @param target
   */
  public boolean executeActionWithTarget(String target) {
    if (!hasSelection) {
      LOGGER.severe("Need to select aircraft first.");
      return false;
    }
    
    // Get finer points
    Point2f fingerPoints[] = handTracker.getFilteredPoints();
    if (fingerPoints.length == 0) {
      LOGGER.severe("Cannot select without finger points.");
      return false;
    }
    
    // Take the first finger point for the target.
    Point fingerPointTarget = new Point((int)fingerPoints[0].x, (int)fingerPoints[0].y);
    
    // Attempt to move object and test for intersections.
    FlyingObject selectedObject = selectedObjects.get(0);
    Point2f oldPosition = selectedObject.getPosition();
    selectedObject.setPosition(fingerPointTarget.x, fingerPointTarget.y);
    LinkedList<FlyingObject> possibleIntersections = flyingObjectManager.intersectsFlyingObjects(selectedObject);
    if (possibleIntersections.size() > 1) {
      selectedObject.setPosition(oldPosition.x, oldPosition.y);
      LOGGER.severe("Cannot place aircraft in position with intersections.");
      return false;
    }
    
    // Clear selection after executing action
    clearSelection();
    
    return true;
  }
  
  public void clearSelection() {
    hasSelection = false;
    selectedObjects = null;
    currentAction = null;
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
