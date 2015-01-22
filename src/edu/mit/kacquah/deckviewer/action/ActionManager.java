package edu.mit.kacquah.deckviewer.action;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.vecmath.Point2f;

import edu.mit.kacquah.deckviewer.action.ActionCommand.ActionCommandType;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.speech.ActionError;
import processing.core.PApplet;

public class ActionManager {
  // Utils
  private static Logger LOGGER = Logger.getLogger(ActionManager.class
      .getName());
  private PApplet parent;
  
  // Action State
  private SelectionManager selectionManager;
  private FlyingObjectManager flyingObjectManager;
  private ActionCommand previousActionCommand;
  
  // Status state
  private String statusMessage;
  
  public ActionManager(PApplet p, SelectionManager sel, FlyingObjectManager fly) {
    this.parent = p;
    this.selectionManager = sel;
    this.flyingObjectManager = fly;
    resetStatus();
  }
  
  /**
   * Accepts a new action command and process the action based on current state.
   * @param actionCommand
   */
  public void processActionCommand(ActionCommand actionCommand) {
    if (lastActionComplete()) {
      // Start new action command.
      if (actionCommand.commandType == ActionCommandType.MOVE) {
        processMoveCommand(actionCommand);
      } else if (actionCommand.commandType == ActionCommandType.LOCATION) {
        // Cannot process location command without move command first.
        LOGGER.severe(ActionError.NO_SELECTION.description);
        updateStatusWithError(ActionError.NO_SELECTION);
      }
    } else {
      // Start new action command.
      if (actionCommand.commandType == ActionCommandType.MOVE) {
        processMoveCommand(actionCommand);
      } else if (actionCommand.commandType == ActionCommandType.LOCATION) {
        processLocationCommand(actionCommand);
      }
    }
  }
  
  private void processMoveCommand(ActionCommand actionCommand) {
    boolean success;
    // Select aircraft
    success = selectionManager.selectAircraftAtFingerLocation();
    if (success) {
      // Move commands simply become the last action command if selection successuful.
      updateActionCommand(actionCommand);
    } else {
      // Report error
      updateStatusWithError(selectionManager.getError());
    }
  }
  
  private void processLocationCommand(ActionCommand actionCommand) {
    // Merge with previous action
    ActionCommand result = ActionCommand.mergeActionCommands(
        previousActionCommand, actionCommand);
    if (result != null && selectionManager.hasSelection()) {
      processMoveToLocationCommand(result);
    } else {
      // Something went wrong...
      LOGGER.severe(ActionError.ACTION_MANAGER_ERROR.description);
      updateStatusWithError(ActionError.ACTION_MANAGER_ERROR);
    }
  }
  
  private void processMoveToLocationCommand(ActionCommand actionCommand) {
    // Get the current finger point
    Point fingerPointTarget = selectionManager.getCurrentFingerPoint();
    if (fingerPointTarget == null) {
      // Report error
      updateStatusWithError(selectionManager.getError());
      return;
    }
    
    // Attempt to move object and test for intersections.
    LinkedList<FlyingObject> potentialObjects = selectionManager.getSelection();
    FlyingObject selectedObject = potentialObjects.get(0);
    Point2f oldPosition = selectedObject.getPosition();
    selectedObject.setPosition(fingerPointTarget.x, fingerPointTarget.y);
    LinkedList<FlyingObject> possibleIntersections = flyingObjectManager
        .intersectsFlyingObjects(selectedObject);
    if (possibleIntersections.size() > 1) {
      selectedObject.setPosition(oldPosition.x, oldPosition.y);
      LOGGER.severe(ActionError.AIRCRAFT_COLLISIONS.description);
      return;
    }

    // Clear selection after executing action.
    selectionManager.clearSelection();
    updateActionCommand(actionCommand);
  }
  
  /**
   * Denotes to whether the last stored action command is a complete action.
   * @return
   */
  private boolean lastActionComplete() {
    if (previousActionCommand == null
        || previousActionCommand.commandType == ActionCommandType.MOVE_TO_LOCATION) {
      return true;
    }else {
      return false;
    }
  }
  
  /**
   * Updates the previous action command and status message.
   * @param actionCommand
   */
  private void updateActionCommand(ActionCommand actionCommand) {
    previousActionCommand = actionCommand;
    this.statusMessage = actionCommand.text;
  }
  
  private void updateStatusWithError(ActionError e) {
    this.statusMessage = e.description;
  }
  
  private void resetStatus() {
    this.statusMessage = "Ready for command...";
  }
  
  /**
   * Returns string status for the status bar to display.
   * @return
   */
  public String getStatus() {
    return this.statusMessage;
  }

}
