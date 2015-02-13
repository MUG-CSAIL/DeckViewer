package edu.mit.kacquah.deckviewer.action;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.vecmath.Point2f;

import edu.mit.kacquah.deckviewer.action.ActionCommand.ActionCommandType;
import edu.mit.kacquah.deckviewer.action.ActionCommand.LocationType;
import edu.mit.kacquah.deckviewer.action.exec.ExecActionStack;
import edu.mit.kacquah.deckviewer.action.exec.MoveAircraftAction;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.environment.Catapult;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.Elevator;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import processing.core.PApplet;

public class ActionManager implements PAppletRenderObject {
  // Utils
  private static Logger LOGGER = Logger.getLogger(ActionManager.class
      .getName());
  private PApplet parent;
  
  // ActionCommand State
  private SelectionManager selectionManager;
  private FlyingObjectManager flyingObjectManager;
  private ActionCommand previousActionCommand;
  
  // ActionStack
  private ExecActionStack actionStack;
  
  // Status state
  private String statusMessage;
  
  // Deck
  private Deck deck;
  
  public ActionManager(PApplet p, SelectionManager sel, FlyingObjectManager fly) {
    this.parent = p;
    this.selectionManager = sel;
    this.flyingObjectManager = fly;
    this.deck = Deck.getInstance();
    this.actionStack = new ExecActionStack();
    resetStatus();
  }
  
  /**
   * Accepts a new action command and process the action based on current state.
   * @param actionCommand
   */
  public void processActionCommand(ActionCommand actionCommand) {
    // Start new action command.
    if (actionCommand.commandType == ActionCommandType.MOVE) {
      processMoveCommand(actionCommand);
    } else if (actionCommand.commandType == ActionCommandType.LOCATION) {
      processLocationCommand(actionCommand);
    } else if(actionCommand.commandType == ActionCommandType.AFFIRMATIVE) {
      processAffirmativeCommand(actionCommand);
    } else {
      LOGGER.severe("Unknown ActionCommand:" + actionCommand.toString());
    }
  }
  
  /**
   * Process command for selecting an aircraft to move.
   * @param actionCommand
   */
  private void processMoveCommand(ActionCommand actionCommand) {
    boolean success;
    // Select aircraft
    if (actionCommand.aircraftNumber != -1) {
      success = selectionManager.selectAircraftWithNumber(actionCommand.aircraftNumber);
    } else {
      success = selectionManager.selectAircraftAtFingerLocation(
          actionCommand.multipleSelection, actionCommand.aircraftType);
    }
    if (success) {
      // Move commands simply become the last action command if selection successful.
      updateActionCommand(actionCommand);
    } else {
      // Report error
      updateStatusWithError(selectionManager.getError());
    }
  }
  
  /**
   * Process command for moving selected aircraft to a specific location on deck.
   * @param actionCommand
   */
  private void processLocationCommand(ActionCommand actionCommand) {
    if (lastActionComplete()
        || previousActionCommand.commandType != ActionCommandType.MOVE) {
      // Cannot process location command without move command first.
      updateStatusWithError(ActionError.NO_SELECTION);
      return;
    }
    // Merge with previous action
    ActionCommand result = ActionCommand.mergeActionCommands(
        previousActionCommand, actionCommand);
    if (result != null && selectionManager.hasSelection()) {
      processMoveToLocationCommand(result);
    } else {
      // Something went wrong...
      updateStatusWithError(ActionError.ACTION_MANAGER_ERROR);
    }
  }
  
  /**
   * Process command for moving selected aircraft to a specific location on deck.
   * @param actionCommand
   */
  private void processMoveToLocationCommand(ActionCommand actionCommand) {
    if (actionCommand.locationType == LocationType.POINTING) {
      // Move to a spot being pointed at.
      moveToPointing(actionCommand);
    } else if (actionCommand.locationType == LocationType.CATAPULT) {
      // Move to catapult.
      moveToCatapult(actionCommand);
    } else if (actionCommand.locationType == LocationType.ELEVATOR) {
      // Move to elevator
      moveToElevator(actionCommand);
    } else if (actionCommand.locationType == LocationType.PARKING_REGION) {
      moveToParkingRegion(actionCommand);
    }
  }
  
  /**
   * Passes incoming affirmative information to the action stack.
   * @param actionCommand
   */
  private void processAffirmativeCommand(ActionCommand actionCommand) {
    actionStack.notifyAffirmative(actionCommand.affirmative);
  }
  
  /**
   * Attempts to move the selected aircraft to a region pointed to on deck.
   */
  private void moveToPointing(ActionCommand actionCommand) {
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
    Point2f oldPosition = selectedObject.positionFloat();
    selectedObject.setPosition(fingerPointTarget.x, fingerPointTarget.y);
    LinkedList<FlyingObject> possibleIntersections = flyingObjectManager
        .intersectsFlyingObjects(selectedObject);
    if (possibleIntersections.size() > 1) {
      selectedObject.setPosition(oldPosition.x, oldPosition.y);
      updateStatusWithError(ActionError.AIRCRAFT_COLLISIONS);
      return;
    }
    // Clear selection after executing action.
    selectionManager.clearSelection();
    updateActionCommand(actionCommand);
  }
  
  /**
   * Attempts to move the selected aircraft to a catapult.
   */
  private void moveToCatapult(ActionCommand actionCommand) {
    Catapult catapult = deck.getCatapult(actionCommand.locationNumber);
    // Attempt to move object and test for success.
    LinkedList<FlyingObject> potentialObjects = selectionManager.getSelection();
    FlyingObject selectedObject = potentialObjects.get(0);
    boolean success = catapult.parkAircraft(selectedObject);
    if (success) {
      // Clear selection after executing action.
      selectionManager.clearSelection();
      updateActionCommand(actionCommand);
    } else {
      // Report error
      updateStatusWithError(ActionError.AIRCRAFT_COLLISIONS);
    }
  }
  
  /**
   * Attempts to move the selected aircraft to an elevator.
   */
  private void moveToElevator(ActionCommand actionCommand) {
    Elevator elevator = deck.getElevator(actionCommand.locationNumber);
    // Attempt to move object and test for success.
    LinkedList<FlyingObject> potentialObjects = selectionManager.getSelection();
    FlyingObject selectedObject = potentialObjects.get(0);
    boolean success = elevator.parkAircraft(selectedObject);
    if (success) {
      // Clear selection after executing action.
      selectionManager.clearSelection();
      updateActionCommand(actionCommand);
    } else {
      // Report error
      updateStatusWithError(ActionError.AIRCRAFT_COLLISIONS);
    }
  }
  
  /**
   * Attempts to move the selected aircraft(s) to a parking region.
   * Creates a MoveAircraftAction and adds it to the stack.
   * @param actionCommand
   */
  public void moveToParkingRegion(ActionCommand actionCommand) {
    ParkingRegion parkingRegion = Deck.getInstance().getParkingRegion(
        actionCommand.parkingRegionType);
    // Get and clear selection.
    LinkedList<FlyingObject> selectedObjects = selectionManager.getSelection();
    selectionManager.clearSelection();
    // Start execution
    MoveAircraftAction action = new MoveAircraftAction(actionStack,
        parkingRegion, selectedObjects);
    actionStack.addNewAction(action);
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
    LOGGER.severe(e.description);
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

  @Override
  public void update(long elapsedTime) {
    actionStack.update(elapsedTime);
  }

  @Override
  public void render(PApplet p) {
    actionStack.render(p);
  }

}
