package edu.mit.kacquah.deckviewer.action.exec;

import java.awt.Point;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.gui.shape.BlinkingCircle;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.RenderGroup;

/**
 * Action for moving one or multiple aircraft to a defined location on deck.
 * @author kojo
 *
 */
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
      // Find parking spots
      findParkingSpots();
      
      // Check the path
      if (checkPath()) {
        // Load the clear path action and yield done
        ClearPathAction action = new ClearPathAction();
        actionStack.addNewAction(action);
        yieldDone();
        return;
      } else if (checkTartet()) {
        // If we're moving multiple aircraft, we won't suggest alternative
        // arrangement, rather, we'll point out that all the aircraft can't fit.
        if (moveAircraft.size() != 1) {
          parentGraph.setNextSpeechNode(new CantMoveMultiple(parentGraph));
          yieldNext();
          return;
        }
        // Load the find alternate target action and yield done
        FindAlternateTargetAction action = new FindAlternateTargetAction(
            moveAircraft, moveToParkingRegion, moveToParkingSpots, numNullSpots);
        actionStack.addNewAction(action);
        yieldDone();
        return;
      } else {
        // Set next node and execute move.
        parentGraph.setNextSpeechNode(new DoMove(parentGraph));
        yieldNext();
        return;
      }
    }

    @Override
    public void postSpeechProcess() {
      // Not called
    }
    
    /**
     * Finds parking spots for each movable aircraft. If a parking spot is not
     * found in the target area, the aircraft is assigned to null.
     */
    public void findParkingSpots() {
      int numMoveAircraft = moveAircraft.size();
      moveToParkingSpots = moveToParkingRegion.getFreeParkingSpots(numMoveAircraft, null);
    }
    

    /**
     * Checks the path from the move aircraft to the destination. If there are
     * any aircraft in the way, they are added to the pathBlockAircraft list and
     * this method returns true.
     * 
     * @return
     */
    private boolean checkPath() {
      // If we've selected multiple aircraft, we won't check path.
      if (moveAircraft.size() != 1) {
        return false;
      }
      
      return false;
    }

    /**
     * Checks the target for the move aircraft. If the target is blocked or
     * full, this method returns true.
     * 
     * @return
     */
    private boolean checkTartet() {
      // Check to see if any of the parking spaces were null.
      numNullSpots = 0;
      for (ParkingSpot p: moveToParkingSpots) {
        if (p == null) {
          numNullSpots +=1;
        }
      }
      if (numNullSpots != 0) {
        return true;
      }
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
      // Move aircraft to their destinations
      for (int i = 0; i < moveAircraft.size(); ++i) {
        FlyingObject o = moveAircraft.get(i);
        ParkingSpot p = moveToParkingSpots.get(i);
        p.park(o);
      }
      // Yeild to give confirmation
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      yieldDone();
    }
  }
  
  private class CantMoveMultiple extends SpeechNode {
    public CantMoveMultiple(SpeechGraph speechGraph) {
      super(speechGraph);
    }

    @Override
    public void preSpeechProcess() {
      // Explain that multiple aircraft can't be moved.
      String moveToLocationName = moveToParkingRegion.name();
      int numFreeSpots = moveAircraft.size() - numNullSpots;
      this.speechText = "I'm sorry, there are only " + numFreeSpots
          + " spots free at the " + moveToLocationName
          + ". Please choose a subset of aircraft to move.";      
      
      // Highlight the only free spots
      createParkingSpotHighlights();
      DeckViewerPApplet.getInstance().renderStack().addRenderGroup(remainingParkingSpots);
      // Yeild for speech.
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      // Remove parking spot highlights
      DeckViewerPApplet.getInstance().renderStack().removeRenderGroup(remainingParkingSpots);
      // We're done.
      yieldDone();
    }
    
    /**
     * Highlights the available parking spots
     * @return
     */
    private void createParkingSpotHighlights() {
      remainingParkingSpots = new RenderGroup();
      for (ParkingSpot spot : moveToParkingSpots) {
        if (spot == null) {
          continue;
        }
        Point center = spot.center;
        BlinkingCircle circle = new BlinkingCircle(center, 15, ColorUtil.RED,
            false);
        remainingParkingSpots.addRenderObject(circle);
        
      }
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
  private int numNullSpots;
  /**
   * Highlight of remaining free parking spots. Used when number of spots is not sufficient.
   */
  RenderGroup remainingParkingSpots;
  
  /**
   * Parking regions specified as the target.
   */
  private ParkingRegion moveToParkingRegion;
  
  public MoveAircraftAction(ExecActionStack actionStack, ParkingRegion target,
      LinkedList<FlyingObject> selectedAircraft) {
    this.actionStack = actionStack;
    this.moveToParkingRegion = target;
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
