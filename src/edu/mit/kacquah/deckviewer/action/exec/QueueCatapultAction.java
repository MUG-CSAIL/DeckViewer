package edu.mit.kacquah.deckviewer.action.exec;

import java.awt.Point;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.CatapultQueue;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.gui.shape.Path;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;

public class QueueCatapultAction extends SpeechGraph implements ExecAction {
  // ---------------------------Speech Nodes------------------------------------

  private class PreProcessQueue extends SpeechNode {

    public PreProcessQueue(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = null;
    }
    
    @Override
    public void preSpeechProcess() {
      // Can we fit the aircraft?
      if (!canFit()) {
        parentGraph.setNextSpeechNode(new CannotFit(parentGraph));
        yieldNext();
        return;
      }
      
      // Do we need to queue?
      if (mustQueue()) {
        parentGraph.setNextSpeechNode(new AskForQueue(parentGraph));
        yieldNext(); 
        return;
      }
      
      // Do the move
      parentGraph.setNextSpeechNode(new DoMove(parentGraph));
      yieldNext();
    }

    @Override
    public void postSpeechProcess() {
      // Not called
    }
    
    private boolean canFit() {
      int numAircraft = moveAircraft.size();
      int numSpots = 0;
      for (CatapultQueue q: catapultTargets) {
        numSpots += q.getNumFreeParkingSpots();
      }
      if (numSpots < numAircraft) {
        return false;
      } else {
        return true;
      }
    }
    
    private boolean mustQueue() {
      if (moveAircraft.size() > catapultTargets.size()) {
        return true;
      } else {
        for (CatapultQueue q: catapultTargets) {
          if (q.isCatapultOccupied()) {
            return true;
          }
        }
        return false;
      }
    }
  }
  
  private class AskForQueue extends SpeechNode {

    public AskForQueue(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = "Do you want me to queue additional aircraft behind the catapults?";
    }
    
    @Override
    public void preSpeechProcess() {
      yieldAffirmative();
    }

    @Override
    public void postSpeechProcess() {
      // Our next action is based on affirmative response.
      Affirmative affirmative = parentGraph.getLastAffirmative();
      if (affirmative == Affirmative.YES) {
        parentGraph.setNextSpeechNode(new DoMove(parentGraph));        
      } else {
        parentGraph.setNextSpeechNode(new DontDoMove(parentGraph));
      }
      yieldNext();
    }
    
  }
  
  
  private class CannotFit extends SpeechNode{
    public CannotFit(SpeechGraph speechGraph) {
      super(speechGraph);
    }
    
    @Override
    public void preSpeechProcess() {
      if (catapultTargets.size() > 1) {
        this.speechText = "There is not enough room at the catapults. Perhaps try moving fewer aircraft or try a different catapult.";
      } else {
        this.speechText = "There is not enough room at the catapult. Perhaps try moving fewer aircraft or try a different catapult.";
      }
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      yieldDone();
    }
  }
  
  private class DoMove extends SpeechNode {
    public DoMove(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = "Ok Done!";
    }

    @Override
    public void preSpeechProcess() {
      // Check the path
      if (checkPath()) {
        // Load the clear path action and yield done
        ClearPathAction action = new ClearPathAction(actionStack, moveAircraft,
            moveAircraftPath, pathBlockAircraft, moveToParkingSpots);
        actionStack.pushTop(action);
        yieldDone();
        return;
      }
      
      // Move aircraft to their destinations
//      int catIndex = 0;
//      for (int i = 0; i < moveAircraft.size(); ++i) {
//        FlyingObject o = moveAircraft.get(i);
//        ParkingSpot p = catapultTargets.get(catIndex).getNextFreeParkingSpot();
//        p.park(o);
//        catIndex += 1;
//        catIndex %= catapultTargets.size();
//      }
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
    
    /**
     * Checks the path from the move aircraft to the destination. If there are
     * any aircraft in the way, they are added to the pathBlockAircraft list and
     * this method returns true.
     * 
     * This assumes the target is already set. Only works for a single aircraft.
     * 
     * @return
     */
    private boolean checkPath() {
      // Fill move to parking spots
      moveToParkingSpots = new LinkedList<ParkingSpot>();
      int catIndex = 0;
      for (int i = 0; i < moveAircraft.size(); ++i) {
        ParkingSpot spot = catapultTargets.get(catIndex).getNextFreeParkingSpot();
        if (spot == null) {
          catIndex += 1;
          catIndex %= catapultTargets.size();
          spot = catapultTargets.get(catIndex).getNextFreeParkingSpot();
        }
        moveToParkingSpots.add(spot);
        catIndex += 1;
        catIndex %= catapultTargets.size();
      }
      
      // If we've selected multiple aircraft, we won't check path.
      if (moveAircraft.size() != 1) {
        return false;
      }
      
      // Check the path
      Point start = moveAircraft.get(0).position();
      Point end = moveToParkingSpots.get(0).center;
      moveAircraftPath = new Path(GlobalSettings.AIRCRAFT_RADIUS,
          GlobalSettings.aircraftPathColor);
      moveAircraftPath.addPoint(start);
      moveAircraftPath.addPoint(end);
      pathBlockAircraft = DeckViewerPApplet.getInstance()
          .getDeckFlyingObjectManager().intersectsPath(moveAircraftPath);
      
      // Remove any aircraft that are move aircraft.
      for (FlyingObject o : moveAircraft) {
        if (pathBlockAircraft.contains(o)) {
          pathBlockAircraft.remove(o);
        }
      }
      
      return !pathBlockAircraft.isEmpty();
    }
  }
  
  private class DontDoMove extends SpeechNode{
    public DontDoMove(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = "Ok, please give another command.";
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
   * Path for move aircraft to traverse.
   */
  private Path moveAircraftPath;
  /**
   * Aircraft blocking the path.
   */
  private LinkedList<FlyingObject> pathBlockAircraft;
  /**
   * List of parking spot destinations corresponding to each move aircraft.
   */
  private LinkedList<ParkingSpot> moveToParkingSpots;
  /**
   * Target catapults for aircraft
   */
  LinkedList<CatapultQueue> catapultTargets;
  
  public QueueCatapultAction(ExecActionStack actionStack,
      LinkedList<CatapultQueue> catapultTargets, LinkedList<FlyingObject> selectedAircraft) {
    this.actionStack = actionStack;
    this.catapultTargets = catapultTargets;
    this.moveAircraft = selectedAircraft; 
  }
  
  @Override
  protected SpeechNode rootNode() {
    return new PreProcessQueue(this);
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
