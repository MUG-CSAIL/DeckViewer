package edu.mit.kacquah.deckviewer.action.exec;

import java.awt.Point;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion.ParkingRegionType;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.gui.shape.BlinkingCircle;
import edu.mit.kacquah.deckviewer.gui.shape.Path;
import edu.mit.kacquah.deckviewer.gui.shape.ShadowAircraft;
import edu.mit.kacquah.deckviewer.gui.shape.StraightLineArrow;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph;
import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechNode;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.RenderGroup;

/**
 * Actionfor moving aircraft blocking a path to alternate parking spots.
 * Note, this currenty assumes only one aircraft is being moved.
 * TODO (KoolJBlack) Update this to handle multiplke aircraft.
 * @author kojo
 *
 */
public class ClearPathAction  extends SpeechGraph implements ExecAction {
  // ---------------------------Speech Nodes------------------------------------
  
  private class PreProcessPath extends SpeechNode {

    public PreProcessPath(SpeechGraph speechGraph) {
      super(speechGraph);
      this.speechText = null;
    }
    
    @Override
    public void preSpeechProcess() {
      calculateAlternateParkingSpots();
      
      // Inform the user of the move issue.
      this.speechText = "Unable to move the " + moveAircraft.get(0).name()
          + " to the " + moveToParkingSpots.get(0).parkingRegion().name() + ".";
      yieldWait();
    }

    @Override
    public void postSpeechProcess() {
      // Start the render pipeline
      parentGraph.setNextSpeechNode(new RenderBlocked(parentGraph));
      yieldNext();   
    }
    
    /**
     * Determines alternate spots and regions for parked aircraft by looking up
     * closest free parking spots.
     */
    private void calculateAlternateParkingSpots() {
      // Block spots start out as moveToParkingSpots 
      LinkedList<ParkingSpot> blockSpots = new LinkedList<ParkingSpot>();
      for (ParkingSpot spot: moveToParkingSpots) {
        if (spot != null) {
          blockSpots.add(spot);
        }
      }
      
      // Block spots also include spots along the path
      LinkedList<ParkingSpot> pathIntersectionSpots = Deck.getInstance().intersectsPath(moveAircraftPath) ;
      blockSpots.addAll(pathIntersectionSpots);
      
      // The result that we'll return.
      LinkedList<ParkingSpot> result = new LinkedList<ParkingSpot>();
      
      for (FlyingObject blockAircraft: pathBlockAircraft) {
        // Find the closest alternate spots to one of the blocking aircraft
        Point centroid = blockAircraft.position();
        alternateParkingSpots = Deck.getInstance().closestFreeParkingSpots(
            centroid, 1, blockSpots, ParkingRegionType.CATAPULT_TYPES);
        // Add alternate spot to result and block spots
        result.add(alternateParkingSpots.get(0));
        blockSpots.add(alternateParkingSpots.get(0));
      }
      
      // Set the result
      alternateParkingSpots = result;

      // Get the names of the alternate parking region
      alternateParkingRegions = new LinkedList<ParkingRegion>();
      for (ParkingSpot spot: alternateParkingSpots) {
        if (!alternateParkingRegions.contains(spot.parkingRegion())) {
          alternateParkingRegions.add(spot.parkingRegion());
        }
      }
    } // end void calculate
  }
  
  private class RenderBlocked extends SpeechNode {
    
    public RenderBlocked(SpeechGraph speechGraph) {
      super(speechGraph);
    }

    @Override
    public void preSpeechProcess() {
      // Explain blocking aircraft.
      LinkedList<ParkingRegion> regions = new LinkedList<ParkingRegion>();
      this.speechText = "These aircraft ";
      for (FlyingObject o: pathBlockAircraft) {
        // Some aircraft won't be parked
        if (o.getParkingSpot() == null) {
          continue;
        }
        ParkingRegion r = o.getParkingSpot().parkingRegion();
        if (!regions.contains(r)) {
          regions.add(r);
        }
      }
      if (regions.size() >0 ) {
        this.speechText += "near the " + regions.get(0).name();
        for (int i = 1; i < regions.size(); ++i) {
          this.speechText += " and the " + regions.get(i).name();
        }
      }
      this.speechText += "are blocking the path.";
      // Highlight all parking spots with blocked aircraft
      for (FlyingObject o: pathBlockAircraft) {
        BlinkingCircle circle = new BlinkingCircle(o.position(), GlobalSettings.AIRCRAFT_RADIUS, ColorUtil.RED, true);
        renderGroup.addRenderObject(circle);
      }
      // Show the blocked path
      renderGroup.addRenderObject(moveAircraftPath);
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
      // Explain alternate spots.
      this.speechText = "I can move the blocking aicraft to the "
          + alternateParkingRegions.get(0).name();
      for (int i = 1; i < alternateParkingRegions.size(); ++i) {
        this.speechText += " and the " + alternateParkingRegions.get(i).name();
      }
      this.speechText += ", then move the aircraft to the "
          + moveToParkingSpots.get(0).parkingRegion().name();
      
      // Render the alternate placement
      for (int i = 0; i < pathBlockAircraft.size(); ++i) {
        Point center = alternateParkingSpots.get(i).center;
        float rotation = alternateParkingSpots.get(i).parkingRegion().getAngle();
        FlyingObject blockAircraft = pathBlockAircraft.get(i);
        // Create ghost aircraft
        ShadowAircraft shadow = new ShadowAircraft(blockAircraft, center, rotation);
        renderGroup.addRenderObject(shadow);
        // Hide the original aircraft
        blockAircraft.setHide(true);
      }
      
      DeckViewerPApplet.getInstance().renderStack().addRenderGroup(renderGroup);
      yieldAffirmative();
    }

    @Override
    public void postSpeechProcess() {
      DeckViewerPApplet.getInstance().renderStack().removeRenderGroup(renderGroup);
      renderGroup.clear();
      // Un hide aircraft
      for (int i = 0; i < pathBlockAircraft.size(); ++i) {
        pathBlockAircraft.get(i).setHide(false);
      }
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
      // Move block aircraft to alternate spots
      for (int i = 0; i < pathBlockAircraft.size(); ++i) {
        FlyingObject o = pathBlockAircraft.get(i);
        ParkingSpot p = alternateParkingSpots.get(i);
        p.park(o);
      }
      // Move aircraft to their destinations
      for (int i = 0; i < pathBlockAircraft.size(); ++i) {
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

  /**
   * Used to inform other actions if the path was cleared by the ClearPathAction
   * @author kojo
   *
   */
  public interface ClearPathActionCallback {
    public void didClearPath(boolean clearPath);
  }
  
  private ClearPathActionCallback listener;
  
  // Parent action stack.
  private ExecActionStack actionStack;
  
  /**
   * List of aircraft to move.
   */
  private LinkedList<FlyingObject> moveAircraft;    
  /**
   * Aircraft blocking the path.
   */
  private LinkedList<FlyingObject> pathBlockAircraft;
  /**
   * Path for move aircraft to traverse.
   */
  private Path moveAircraftPath;
  /**
   * List of parking spot destinations corresponding to each move aircraft.
   */
  private LinkedList<ParkingSpot> moveToParkingSpots;
  /**
   * Alternate spots for parking.
   */
  private LinkedList<ParkingSpot> alternateParkingSpots;
  private LinkedList<ParkingRegion> alternateParkingRegions;
  /**
   * Rendering animations.
   */
  private RenderGroup renderGroup;
  
  public ClearPathAction(ExecActionStack actionStack,
      LinkedList<FlyingObject> moveAircraft,
      Path moveAircraftPath,
      LinkedList<FlyingObject> pathBlockAircraft,
      LinkedList<ParkingSpot> moveToParkingSpots,
      ClearPathActionCallback listener) {
    this.actionStack = actionStack;
    this.moveAircraft = moveAircraft;
    this.moveToParkingSpots = moveToParkingSpots;
    this.pathBlockAircraft = pathBlockAircraft;
    this.renderGroup = new RenderGroup();
    this.listener = listener;
    this.moveAircraftPath = moveAircraftPath;
  }
  
  @Override
  protected SpeechNode rootNode() {
    return new PreProcessPath(this);
  }
  
  /**
   * Inform possible listener of action result.
   * @param didClearPath
   */
  protected boolean notifyListener(boolean didClearPath) {
    if (listener != null) {
      listener.didClearPath(didClearPath);
      return true;
    } else {
      return false;
    }
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
