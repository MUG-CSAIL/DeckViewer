package edu.mit.kacquah.deckviewer.action.exec;

import java.awt.Point;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion.ParkingRegionType;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.gui.shape.BlinkingCircle;
import edu.mit.kacquah.deckviewer.gui.shape.StraightLineArrow;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.RenderGroup;

/**
 * Action for finding an alternate destination for aircraft being moved on deck.
 * Note, this currently only supports moving one aircraft.
 * TODO(KoolJBlack) Update this to handle multiple aircraft re-routing.
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
      calculateAlternateParkingSpots();
      
      // Start the render pipeline
      parentGraph.setNextSpeechNode(new RenderBlocked(parentGraph));
      yieldNext();
    }

    @Override
    public void postSpeechProcess() {
      // Not called      
    }
    
    /**
     * Determines alternate spots and regions for parked aircraft by looking up
     * closest free parking spots.
     */
    private void calculateAlternateParkingSpots() {
      // Find the closest spots in the parking region target
      Point centroid = moveToParkingRegion.getCentroid();
      LinkedList<ParkingSpot> blockSpots = new LinkedList<ParkingSpot>();
      for (ParkingSpot spot: moveToParkingSpots) {
        if (spot != null) {
          blockSpots.add(spot);
        }
      }
      alternateParkingSpots = Deck.getInstance().closestFreeParkingSpots(
          centroid, numNullSpots, blockSpots, ParkingRegionType.CATAPULT_TYPES);
      // Get the names of the alternate parking region
      alternateParkingRegions = new LinkedList<ParkingRegion>();
      for (ParkingSpot spot: alternateParkingSpots) {
        if (!alternateParkingRegions.contains(spot.parkingRegion())) {
          alternateParkingRegions.add(spot.parkingRegion());
        }
      }
    }
  }
  
  private class RenderBlocked extends SpeechNode {
    
    public RenderBlocked(SpeechGraph speechGraph) {
      super(speechGraph);
    }

    @Override
    public void preSpeechProcess() {
      // Explain block spots.
      this.speechText = "Sorry, there is not enough room on the " + moveToParkingRegion.name();
      // Highlight all parking spots in the target region
      for (ParkingSpot spot: moveToParkingRegion.parkingSpots()) {
        BlinkingCircle circle = new BlinkingCircle(spot.center, GlobalSettings.AIRCRAFT_RADIUS, ColorUtil.RED, true);
        renderGroup.addRenderObject(circle);
      }
      DeckViewerPApplet.getInstance().renderStack().addRenderGroup(renderGroup);
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      DeckViewerPApplet.getInstance().renderStack().removeRenderGroup(renderGroup);
      renderGroup.clear();
      parentGraph.setNextSpeechNode(new RenderAlternate(parentGraph));
      yieldNext();
    }
  }
  
  private class RenderAlternate extends SpeechNode {
    
    public RenderAlternate(SpeechGraph speechGraph) {
      super(speechGraph);
    }
    
    @Override
    public void preSpeechProcess() {
      // Explain block spots. We assume there is only one parking region.
      
      this.speechText = "The next closest spot is at the "
          + alternateParkingRegions.get(0).name()
          + ". Shall I move the aircraft there instead?";
      // Render the alternate placement
      Point center = alternateParkingSpots.get(0).center;
      BlinkingCircle circle = new BlinkingCircle(center, GlobalSettings.AIRCRAFT_RADIUS, ColorUtil.BLUE, false);
      Point start = new Point((int)(moveAircraft.get(0).positionFloat().x), (int)(moveAircraft.get(0).positionFloat().y));
      StraightLineArrow lineArrow = new StraightLineArrow(start, center, ColorUtil.BLUE);
      renderGroup.addRenderObject(circle);
      renderGroup.addRenderObject(lineArrow);
      DeckViewerPApplet.getInstance().renderStack().addRenderGroup(renderGroup);
      yieldAffirmative();
    }

    @Override
    public void postSpeechProcess() {
      DeckViewerPApplet.getInstance().renderStack().removeRenderGroup(renderGroup);
      renderGroup.clear();
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
        ParkingSpot p = alternateParkingSpots.get(i);
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
  /**
   * Rendering animations.
   */
  private RenderGroup renderGroup;
  
  public FindAlternateTargetAction(ExecActionStack actionStack,
      LinkedList<FlyingObject> moveAircraft, ParkingRegion target,
      LinkedList<ParkingSpot> moveToParkingSpots,
      int numNullSpots) {
    this.actionStack = actionStack;
    this.moveAircraft = moveAircraft;
    this.moveToParkingSpots = moveToParkingSpots;
    this.numNullSpots = numNullSpots;
    this.moveToParkingRegion = target;
    this.renderGroup = new RenderGroup();
  }
  
  @Override
  protected SpeechNode rootNode() {
    return new PreProcessTarget(this);
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
