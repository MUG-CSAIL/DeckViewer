package edu.mit.kacquah.deckviewer.action.exec;

import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;

/**
 * Action for finding an alternate destination for aircraft being moved on deck.
 * @author kojo
 *
 */
public class FindAlternateTargetAction extends SpeechGraph implements ExecAction {
  // ---------------------------Speech Nodes------------------------------------
  private class PreProcessTarget extends SpeechNode {

    public PreProcessTarget(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = null;
    }
    
    @Override
    public void preSpeechProcess() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void postSpeechProcess() {
      // Not called      
    }
    
    /**
     * Determines alternate spots for parked aircraft by looking up closest free
     * parking spots.
     */
    private void calculateAlternateParkingSpots() {
      
    }
  }
  
  private class RenderBlocked extends SpeechNode {
    
    public RenderBlocked(SpeechGraph speechGraph) {
      super(speechGraph);
    }

    @Override
    public void preSpeechProcess() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void postSpeechProcess() {
      // TODO Auto-generated method stub
      
    }
  }
  
  private class RenderAlternate extends SpeechNode {
    
    public RenderAlternate(SpeechGraph speechGraph) {
      super(speechGraph);
    }
    
    @Override
    public void preSpeechProcess() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void postSpeechProcess() {
      // TODO Auto-generated method stub
      
    }
  }
  
  private class DoMove extends SpeechNode {
    
    public DoMove(SpeechGraph speechGraph) {
      super(speechGraph);
    }

    @Override
    public void preSpeechProcess() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void postSpeechProcess() {
      // TODO Auto-generated method stub
      
    }
  }
  
  private class DontDoMove extends SpeechNode{
    
    public DontDoMove(SpeechGraph speechGraph) {
      super(speechGraph);
    }
    
    @Override
    public void preSpeechProcess() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void postSpeechProcess() {
      // TODO Auto-generated method stub
      
    }
  }
  
  // ---------------------------Speech Graph------------------------------------

  /**
   * List of aircraft to move.
   */
  private LinkedList<FlyingObject> moveAircraft;
  /**
   * List of parking spot destinations corresponding to each move aircraft.
   */
  private LinkedList<ParkingSpot> moveToParkingSpots;
  private int numNullSpots;
  
  /**
   * Parking regions specified as the target.
   */
  private ParkingRegion moveToParkingRegion;
  
  /**
   * Alternate spots for parking.
   */
  private LinkedList<ParkingSpot> alternateParkingSpots;
  private LinkedList<ParkingRegion> alternateParkingRegions;
  
  public FindAlternateTargetAction(LinkedList<FlyingObject> moveAircraft,
      ParkingRegion target, LinkedList<ParkingSpot> moveToParkingSpots,
      int numNullSpots) {
    this.moveAircraft = moveAircraft;
    this.moveToParkingSpots = moveToParkingSpots;
    this.numNullSpots = numNullSpots;
    this.moveToParkingRegion = target;
  }
  
  @Override
  protected SpeechNode rootNode() {
    // TODO Auto-generated method stub
    return null;
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
