package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import edu.mit.kacquah.deckviewer.action.ActionManager;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObjectManager;
import edu.mit.kacquah.deckviewer.deckobjects.Sprite.Direction;
import edu.mit.kacquah.deckviewer.environment.ParkingRegion.ParkingRegionType;
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
  public final float scaleRatio;

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
  
  /**
   * Create the singleton instance of the deck.
   * @param parent
   * @return
   */
  public static Deck initInstance(PApplet parent) {
    if (instance != null) {
      LOGGER.severe("Already initialized Deck instance");
    } else {
      instance = new Deck(parent);
      // Create deck environment
      instance.initDeckEnvironment();
      instance.initDeckParking();
    }
    return instance;
  }
  
  /**
   * Get the singleton instance of the deck. Must be called after initInstance.
   * @return
   */
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
  }

  /**
   * Initialize deck objects.
   */
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
  
  /**
   * Initialize deck parking regions.
   */
  private void initDeckParking() {
    // Catapults
//    ParkingRegion cat1 = new ParkingRegion(ParkingRegionType.CATAPULT_1, catapults[0].takeoffDirection());
//    ParkingRegion cat2 = new ParkingRegion(ParkingRegionType.CATAPULT_2, catapults[1].takeoffDirection());
//    ParkingRegion cat3 = new ParkingRegion(ParkingRegionType.CATAPULT_3, catapults[2].takeoffDirection());
//    ParkingRegion cat4 = new ParkingRegion(ParkingRegionType.CATAPULT_4, catapults[3].takeoffDirection());
//    cat1.addParkingSpot(catapults[0].startPoint());
//    cat2.addParkingSpot(catapults[1].startPoint());
//    cat3.addParkingSpot(catapults[2].startPoint());
//    cat4.addParkingSpot(catapults[3].startPoint());
//    catapults[0].setCatapultParking(cat1);
//    catapults[1].setCatapultParking(cat2);
//    catapults[2].setCatapultParking(cat3);
//    catapults[3].setCatapultParking(cat4);
    
    // Catapult Queues
    CatapultQueue cat1Queue = new CatapultQueue(ParkingRegionType.CATAPULT_1, catapults[0]);
    CatapultQueue cat2Queue = new CatapultQueue(ParkingRegionType.CATAPULT_2, catapults[1]);
    CatapultQueue cat3Queue = new CatapultQueue(ParkingRegionType.CATAPULT_3, catapults[2]);
    CatapultQueue cat4Queue = new CatapultQueue(ParkingRegionType.CATAPULT_4, catapults[3]);
    parkingRegions.add(cat1Queue);
    parkingRegions.add(cat2Queue);
    parkingRegions.add(cat3Queue);
    parkingRegions.add(cat4Queue);
    
    // Elevators
    ParkingRegion el1 = new ParkingRegion(ParkingRegionType.ELEVATOR_1, elevators[0].elevatorDirection());
    ParkingRegion el2 = new ParkingRegion(ParkingRegionType.ELEVATOR_2, elevators[1].elevatorDirection());
    ParkingRegion el3 = new ParkingRegion(ParkingRegionType.ELEVATOR_3, elevators[2].elevatorDirection());
    ParkingRegion el4 = new ParkingRegion(ParkingRegionType.ELEVATOR_4, elevators[3].elevatorDirection());
    el1.addParkingSpot(elevators[0].elevatorCenter());
    el2.addParkingSpot(elevators[1].elevatorCenter());
    el3.addParkingSpot(elevators[2].elevatorCenter());
    el4.addParkingSpot(elevators[3].elevatorCenter());
    elevators[0].setElevatorParking(el1);
    elevators[1].setElevatorParking(el2);
    elevators[2].setElevatorParking(el3);
    elevators[3].setElevatorParking(el4);
    
    // Between Elevator 1 and the Catapults
    ParkingRegion el1_and_cat = new ParkingRegion(
        ParkingRegionType.BTWN_EL1_AND_CAT, -150);
    parkingRegions.add(el1_and_cat);
    el1_and_cat.addParkingSpot(scale(1860, 826));
    el1_and_cat.addParkingSpot(scale(1800, 866));
    el1_and_cat.addParkingSpot(scale(1740, 916));
    
    // Over Elevator One and Two
    ParkingRegion el1_and_el2 = new ParkingRegion(
        ParkingRegionType.OVER_EL1_AND_EL2, Direction.UP.degrees);
    parkingRegions.add(el1_and_el2);
    el1_and_el2.addParkingSpot(scale(1650, 916));
    el1_and_el2.addParkingSpot(scale(1570, 916));
    el1_and_el2.addParkingSpot(scale(1490, 916));
    el1_and_el2.addParkingSpot(scale(1410, 916));
    el1_and_el2.addParkingSpot(scale(1330, 916));
    el1_and_el2.addParkingSpot(scale(1250, 916));
    el1_and_el2.addParkingSpot(scale(1170, 916));
    
    // Behind the Tower
    ParkingRegion bhnd_tower = new ParkingRegion(ParkingRegionType.BHND_TOWER,
        Direction.UP.degrees);
    parkingRegions.add(bhnd_tower);
    bhnd_tower.addParkingSpot(scale(730, 916));
    bhnd_tower.addParkingSpot(scale(640, 916));
    bhnd_tower.addParkingSpot(scale(550, 916));
    
    // In front of the Tower
    ParkingRegion frnt_tower =  new ParkingRegion(ParkingRegionType.FRNT_TOWER,
        Direction.UP.degrees);
    parkingRegions.add(frnt_tower);
    frnt_tower.addParkingSpot(scale(925, 826));
    frnt_tower.addParkingSpot(scale(1025, 826));
    
    // Near the Sixpack and the Corral
    ParkingRegion street = new ParkingRegion(ParkingRegionType.STREET,
        Direction.UP.degrees);
    parkingRegions.add(street);
    street.addParkingSpot(scale(1525, 786));
    street.addParkingSpot(scale(1455, 786));
    street.addParkingSpot(scale(1385, 786));
    street.addParkingSpot(scale(1315, 786));
    street.addParkingSpot(scale(1245, 786));
    street.addParkingSpot(scale(1175, 786));

    // Near the Street and the Corral
    ParkingRegion sixpack = new ParkingRegion(ParkingRegionType.SIXPACK,
        Direction.DOWN.degrees);
    parkingRegions.add(sixpack);
    sixpack.addParkingSpot(scale(1490, 696));
    sixpack.addParkingSpot(scale(1420, 696));
    sixpack.addParkingSpot(scale(1350, 696));
    sixpack.addParkingSpot(scale(1280, 696));
    sixpack.addParkingSpot(scale(1210, 696));
    sixpack.addParkingSpot(scale(1140, 696));
    
    // Fantail
    ParkingRegion fantail = new ParkingRegion(ParkingRegionType.FANTAIL,
        Direction.RIGHT.degrees);
    parkingRegions.add(fantail);
    fantail.addParkingSpot(scale(220, 646));
    fantail.addParkingSpot(scale(235, 730));
    fantail.addParkingSpot(scale(250, 816));
    fantail.addParkingSpot(scale(315, 596));
    fantail.addParkingSpot(scale(330, 681));
    fantail.addParkingSpot(scale(345, 766));
    fantail.addParkingSpot(scale(410, 546));
    fantail.addParkingSpot(scale(425, 631));
    fantail.addParkingSpot(scale(440, 716));
    
    // Over Elevator 4
    ParkingRegion over_el4 = new ParkingRegion(ParkingRegionType.OVER_EL4,
        Direction.DOWN.degrees);
    parkingRegions.add(over_el4);
    over_el4.addParkingSpot(scale(515, 526));
    over_el4.addParkingSpot(scale(585, 526));
    over_el4.addParkingSpot(scale(655, 526));
  }
  
  /**
   * Set flying object manager on deck.
   * @param m
   */
  public void setFlyingObjectManager(FlyingObjectManager m) {
    this.flyingObjectManager = m;
  }
  
  /**
   * Catapult by number.
   * @param number
   * @return
   */
  public Catapult getCatapult(int number) {
    return this.catapults[number - 1];
  }
  
  /**
   * Elevator by number.
   * @param number
   * @return
   */
  public Elevator getElevator(int number) {
    return this.elevators[number - 1];
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
   * Returns a parking region of given type or null if not found.
   * @param type
   * @return
   */
  public ParkingRegion getParkingRegion(ParkingRegionType type) {
    for (ParkingRegion p: parkingRegions) {
      if (p.type == type) {
        return p;
      }
    }
    return null;
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
  
  /**
   * Finds the n closest free parking spots to a target spot, excluding block spots.
   * If there are are insufficient free spots, a null list is returned.
   * 
   * blockSpots or blockRegionTypes can be omitted.
   * @param target
   * @param blockSpots
   * @return
   */
  public LinkedList<ParkingSpot> closestFreeParkingSpots(Point target, int number,
      LinkedList<ParkingSpot> blockSpots, 
      LinkedList<ParkingRegionType> blockRegionsTypes) {
    LinkedList<ParkingSpot> result = new LinkedList<ParkingSpot>();
    
    if (blockSpots == null) {
      blockSpots = new LinkedList<ParkingSpot>();
    }
    
    if (blockRegionsTypes == null) {
      blockRegionsTypes = new LinkedList<ParkingRegionType>();
    }
    
    LinkedList<ParkingSpot> sortedParkingSpots = Sorting
        .parkingSpotDistanceSort(this.parkingSpots, target);
    
    int numFound = 0;
    for(ParkingSpot spot: sortedParkingSpots) {
      if (!spot.isOccupied() && !blockSpots.contains(spot)
          && !blockRegionsTypes.contains(spot.parkingRegion.type)) {
        result.add(spot);
        numFound ++;
        if (numFound == number) {
          return result;
        }
      }
    }
    // We did not find enough free spots.
    return null;
//    for (int i = sortedParkingSpots.size(); i < number; ++i) {
//      sortedParkingSpots.add(null);
//    }    
//    return result;
  }
  

  @Override
  public void update(long elapsedTime) {
    // Update parking spots and parking regions
    for (ParkingRegion region : parkingRegions) {
      region.update(elapsedTime);
    }
    for (ParkingSpot spot : parkingSpots) {
      spot.update(elapsedTime);
    }
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

    // Render parking spots and parking regions
    for (ParkingRegion region : parkingRegions) {
      region.render(p);
    }
    for (ParkingSpot spot : parkingSpots) {
      spot.render(p);
    }
  }

}
