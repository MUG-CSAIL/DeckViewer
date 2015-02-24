package edu.mit.kacquah.deckviewer.action.exec;

import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.CatapultQueue;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
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
        this.speechText = "There is not enough room at the catapults. Please choose a subset of aircraft to move.";
      } else {
        this.speechText = "There is not enough room at the catapult. Please choose a subset of aircraft to move.";
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
      // Move aircraft to their destinations
      int catIndex = 0;
      for (int i = 0; i < moveAircraft.size(); ++i) {
        FlyingObject o = moveAircraft.get(i);
        ParkingSpot p = catapultTargets.get(catIndex).getNextFreeParkingSpot();
        p.park(o);
        catIndex += 1;
        catIndex %= catapultTargets.size();
      }
      // Yeild to give confirmation
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      yieldDone();
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
