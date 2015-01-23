package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import edu.mit.kacquah.deckviewer.action.ActionManager;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GameConstants;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.game.GlobalSettings.BackgroundRatio;
import edu.mit.kacquah.deckviewer.utils.*;
import processing.core.PImage;
import processing.core.PApplet;

public class Deck implements PAppletRenderObject {
  // Utils
  private static Logger LOGGER = Logger.getLogger(Deck.class
      .getName());
  
  // Parent PApplet that owns the deck.
  private DeckViewerPApplet parent;
  private final float scaleRatio;

  // Deck background image.
  private PImage deckImage;

  // Deck Objects.
  private DeckPolygon deckEdges;
  private Catapult[] catapults;
  private Elevator[] elevators;

  // TODO define regions of interest on deck

  // Deck objects and managers
  private FlyingObjectManager flyingObjectManager;
  
  // Singleton
  private static Deck instance;
  
  // Parking regions and spots
  private LinkedList <ParkingSpot> parkingSpots;
  private LinkedList <ParkingRegion> parkingRegions;
  private int nextParkingSpotID, nextParkingRegionID;
  
  
  public static Deck initInstance(PApplet parent) {
    if (instance != null) {
      LOGGER.severe("Already initialized Deck instance");
    } else {
      instance = new Deck(parent);
    }
    return instance;
  }
  
  public static Deck getInstance() {
    if (instance == null) {
      LOGGER.severe("Cannot get uninitialized Deck instance");
    }
    return instance;
  }

  private Deck(PApplet p) {
    this.parent = (DeckViewerPApplet) p;
    this.scaleRatio = parent.scaleRatio();
    String fileName;
    if (GlobalSettings.backgroundRatio == BackgroundRatio.NORMAL) {
      fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR,
          "cvn_full_wake.png");
    } else {
      fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR,
          "cvn_full_wake_16-9.png");
    }
    deckImage = parent.loadImage(fileName);

    // Resize the deck image using scaling ratio
    float origWidth = deckImage.width;
    float origHeight = deckImage.height;
    deckImage.resize((int) (origWidth * parent.scaleRatio()),
        (int) (origHeight * parent.scaleRatio()));
    
    // Lists
    this.parkingRegions = new LinkedList<ParkingRegion>();
    this.parkingSpots = new LinkedList<ParkingSpot>();
    this.nextParkingSpotID = 0;
    this.nextParkingRegionID = 0;

    // Deck environment
    initDeckEnvironment();
    initDeckParking();
  }

  private void initDeckEnvironment() {
    // The deck outline.
    deckEdges = new DeckPolygon();
    deckEdges.addPoint(182 * scaleRatio, 616 * scaleRatio);
    deckEdges.addPoint(474 * scaleRatio, 476 * scaleRatio);
    deckEdges.addPoint(690 * scaleRatio, 476 * scaleRatio);
    deckEdges.addPoint(698 * scaleRatio, 488 * scaleRatio);
    deckEdges.addPoint(1350 * scaleRatio, 488 * scaleRatio);
    deckEdges.addPoint(1480 * scaleRatio, 460 * scaleRatio);
    deckEdges.addPoint(1680 * scaleRatio, 462 * scaleRatio);
    deckEdges.addPoint(1866 * scaleRatio, 608 * scaleRatio);
    deckEdges.addPoint(2380 * scaleRatio, 662 * scaleRatio);
    deckEdges.addPoint(2380 * scaleRatio, 812 * scaleRatio);
    deckEdges.addPoint(1886 * scaleRatio, 856 * scaleRatio);
    deckEdges.addPoint(1728 * scaleRatio, 970 * scaleRatio);
    deckEdges.addPoint(510 * scaleRatio, 970 * scaleRatio);
    deckEdges.addPoint(510 * scaleRatio, 926 * scaleRatio);
    deckEdges.addPoint(306 * scaleRatio, 908 * scaleRatio);
    deckEdges.addPoint(270 * scaleRatio, 866 * scaleRatio);
    deckEdges.addPoint(220 * scaleRatio, 870 * scaleRatio);

    // Catapults
    catapults = new Catapult[4];
    catapults[0] = new Catapult(scale(new Point(1656, 819)), 
                                scale(new Point(2362, 768)), 1);
    catapults[1] = new Catapult(scale(new Point(1638, 694)),
                                scale(new Point(2346, 694)), 2);
    catapults[2] = new Catapult(scale(new Point(1014, 604)), 
                                scale(new Point(1718, 548)), 3);
    catapults[3] = new Catapult(scale(new Point(844, 532)), 
                                scale(new Point(1552, 532)), 4);

    // Elevators
    elevators = new Elevator[4];
    Elevator el1 = new Elevator(scale(new Point(615, 914)), 1);
    el1.addPoint(scale(538, 846));
    el1.addPoint(scale(687, 846));
    el1.addPoint(scale(687, 910));
    el1.addPoint(scale(717, 941));
    el1.addPoint(scale(717, 970));
    el1.addPoint(scale(538, 970));
    Elevator el2 = new Elevator(scale(new Point(1173, 914)), 2);
    el2.addPoint(scale(538 + 555, 846));
    el2.addPoint(scale(687 + 555, 846));
    el2.addPoint(scale(687 + 555, 910));
    el2.addPoint(scale(717 + 555, 941));
    el2.addPoint(scale(717 + 555, 970));
    el2.addPoint(scale(538 + 555, 970));
    Elevator el3 = new Elevator(scale(new Point(1536, 914)), 3);
    el3.addPoint(scale(538 + 910, 846));
    el3.addPoint(scale(687 + 910, 846));
    el3.addPoint(scale(687 + 910, 910));
    el3.addPoint(scale(717 + 910, 941));
    el3.addPoint(scale(717 + 910, 970));
    el3.addPoint(scale(538 + 910, 970));
    Elevator el4 = new Elevator(scale(new Point(573, 544)), 4);
    el4.addPoint(scale(499, 477));
    el4.addPoint(scale(678, 477));
    el4.addPoint(scale(678, 506));
    el4.addPoint(scale(648, 536));
    el4.addPoint(scale(648, 599));
    el4.addPoint(scale(499, 599));
    
    elevators[0] = el1;
    elevators[1] = el2;
    elevators[2] = el3;
    elevators[3] = el4;
    
  }
  
  private void initDeckParking() {
    
  }
  
  /**
   * Get the flying object manager on this deck.
   * @return
   */
  public FlyingObjectManager getFlyingObjectManager() {
    return this.flyingObjectManager;
  }
  
  /**
   * Adds a parking region to the deck.
   * @param parkingRegion
   */
  public void addParkingRegion(ParkingRegion parkingRegion) {
    this.parkingRegions.add(parkingRegion);
  }
  
  /**
   * Adds a parking spot to the deck.
   * @param parkingSpot
   */
  public void addParkingSpot(ParkingSpot parkingSpot) {
    this.parkingSpots.add(parkingSpot);
  }
  
  public int nextParkingRegionID() {
    return this.nextParkingRegionID++;
  }
  
  public int nextParkingSpotID() {
    return this.nextParkingSpotID++;
  }

  /**
   * Scales a point based on the scale factor for the deck.
   * 
   * @param p
   * @return
   */
  public Point scale(Point p) {
    return new Point((int) (p.x * scaleRatio), (int) (p.y * scaleRatio));
  }
  
  public Point scale(int x, int y) {
    return new Point((int) (x * scaleRatio), (int) (y * scaleRatio));
  }

  /**
   * Returns true if a point is on the deck.
   * 
   * @param p
   * @return
   */
  public boolean contains(Point p) {
    return deckEdges.contains(p);
  }

  @Override
  public void update(long elapsedTime) {

  }

  @Override
  public void render(PApplet p) {
    // Set image mode to corners
    p.pushStyle();
    p.imageMode(p.CORNERS);
    p.image(deckImage, 0, 0);
    p.popStyle();

    // Draw the deck outline in blue.
    if (GlobalSettings.renderDeckOutline) {
      p.pushStyle();
      p.noFill();
      p.stroke(ColorUtil.BLUE);
      deckEdges.render(p);
      p.popStyle();
    }

    // Draw the catapults in orange.
    if (GlobalSettings.renderDeckCatapults) {
      p.pushStyle();
      p.strokeWeight(1);
      p.noFill();
      p.stroke(ColorUtil.ORANGE);
      for (Catapult c : catapults) {
        c.render(p);
      }
      p.popStyle();
    }
    
    // Draw the elevators in orange.
    if (GlobalSettings.renderDeckElevators) {
      p.pushStyle();
      p.strokeWeight(1);
      p.noFill();
      p.stroke(ColorUtil.ORANGE);
      for (Elevator e : elevators) {
        e.render(p);
      }
      p.popStyle();
    }

  }

}
