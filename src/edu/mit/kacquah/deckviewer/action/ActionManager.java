package edu.mit.kacquah.deckviewer.action;

import java.util.logging.Logger;

import edu.mit.kacquah.deckviewer.action.ActionCommand.ActionCommandType;
import processing.core.PApplet;

public class ActionManager {
  // Utils
  private static Logger LOGGER = Logger.getLogger(ActionManager.class
      .getName());
  private PApplet parent;
  
  // Action State
  private SelectionManager selectionManager;
  private ActionCommand lastActionCommand;
  
  public ActionManager(PApplet p, SelectionManager sel) {
    this.parent = p;
    this.selectionManager = sel;
  }
  
  public void processActionCommand(ActionCommand actionCommand) {
    if (lastActionComplete()) {
      // Start new action command.
    } else {
      // Try to complete with new actionCommand
    }
  }
  
  /**
   * Denotes to whether the last stored action command is a complete action.
   * @return
   */
  private boolean lastActionComplete() {
    if (lastActionCommand == null
        || lastActionCommand.commandType == ActionCommandType.MOVE_TO_LOCATION) {
      return true;
    }else {
      return false;
    }
  }
  
  /**
   * Returns string status for the status bar to display.
   * @return
   */
  public String getStatus() {
    return "Ready for command";
  }

}
