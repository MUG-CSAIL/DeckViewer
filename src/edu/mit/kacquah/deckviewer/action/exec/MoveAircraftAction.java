package edu.mit.kacquah.deckviewer.action.exec;

import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;

public class MoveAircraftAction extends SpeechGraph implements ExecAction {

  // ---------------------------Speech Nodes------------------------------------

  /**
   * Root node. Checks to see if the path and target are clear. If there are
   * blocks in the path or the target, adds the appropriate action to the action
   * stack and yields next update.
   * 
   */
  private class PreProcessMove extends SpeechNode {
    public PreProcessMove(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = null;
    }

    @Override
    public void preSpeechProcess() {
      // Check the path
      if (checkPath()) {

      } else if (checkTartet()) {

      } else {
        // Set next node and execute move.
        parentGraph.setNextSpeechNode(new DoMove(parentGraph));
        yieldNext();
      }
    }

    @Override
    public void postSpeechProcess() {
      // Not called
    }

    /**
     * Checks the path from the move aircraft to the destination. If there are
     * any aircraft in the way, they are added to the pathBlockAircraft list and
     * this method returns true.
     * 
     * @return
     */
    private boolean checkPath() {
      return false;
    }

    /**
     * Checks the target for the move aircraft. If the target is blocked or
     * full, this method returns true.
     * 
     * @return
     */
    private boolean checkTartet() {
      return false;
    }
  }

  private class DoMove extends SpeechNode {
    public DoMove(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = "Ok Done!";
    }

    @Override
    public void preSpeechProcess() {
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      yieldDone();
    }
  }

  // ---------------------------Speech Graph------------------------------------

  // Parent action stack.
  private ExecActionStack actionStack;

  // Move items
  /**
   * List of aircraft to move.
   */
  private LinkedList<FlyingObject> moveAircraft;
  /**
   * Aircraft blocking the path.
   */
  private LinkedList<FlyingObject> pathBlockAircraft;
  /**
   * List of parking spot destinations corresponding to each move aircraft.
   */
  private LinkedList<ParkingSpot> moveToParkingSpots;

  private ParkingRegion targetParkingRegion;

  public MoveAircraftAction(ExecActionStack actionStack, ParkingRegion target,
      LinkedList<FlyingObject> selectedAircraft) {
    this.actionStack = actionStack;
    this.targetParkingRegion = target;
    this.moveAircraft = selectedAircraft;
  }

  @Override
  protected SpeechNode rootNode() {
    return new PreProcessMove(this);
  }

  @Override
  public void update(long elapsedTime) {
    super.update(elapsedTime);
  }

  @Override
  public void render(PApplet p) {
    super.render(p);

  }
}
