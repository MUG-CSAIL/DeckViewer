package edu.mit.kacquah.deckviewer.action;

import processing.core.PApplet;

public class ActionManager {
  
  private PApplet parent;
  private SelectionManager selectionManager;
  
  public ActionManager(PApplet p, SelectionManager sel) {
    this.parent = p;
    this.selectionManager = sel;
  }
  
  public void processActionCommand(ActionCommand actionCommand) {
    
  }
  
  /**
   * Returns string status for the status bar to display.
   * @return
   */
  public String getStatus() {
    return "Ready for command";
  }

}
