package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.vecmath.Point2f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.action.ActionCommand;
import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

public class ParkingRegion implements PAppletRenderObject {
  /**
   * Names of defined parking regions on deck.
   * @author kojo
   *
   */
  public enum ParkingRegionType {
    ELEVATOR_1(ActionCommand.ELEVATOR_1),
    ELEVATOR_2(ActionCommand.ELEVATOR_2),
    ELEVATOR_3(ActionCommand.ELEVATOR_3),
    ELEVATOR_4(ActionCommand.ELEVATOR_4),
    CATAPULT_1(ActionCommand.CATAPULT_1),
    CATAPULT_2(ActionCommand.CATAPULT_2),
    CATAPULT_3(ActionCommand.CATAPULT_3),
    CATAPULT_4(ActionCommand.CATAPULT_4),
    
    FANTAIL(ActionCommand.FANTAIL),
    OVER_EL1_AND_EL2(ActionCommand.OVER_EL1_AND_EL2),
    BTWN_EL1_AND_CAT(ActionCommand.BTWN_EL1_AND_CAT),
    BHND_TOWER(ActionCommand.BHND_TOWER),
    OVER_EL4(ActionCommand.OVER_EL4),
    FRNT_TOWER(ActionCommand.FRNT_TOWER),

    STREET(ActionCommand.STREET),
    SIXPACK(ActionCommand.SIXPACK),
    POINT(ActionCommand.POINT),
    PATIO(ActionCommand.PATIO),
    CROTCH(ActionCommand.CROTCH),
    CORRAL(ActionCommand.CORRAL),
    JUNK_YARD(ActionCommand.JUNK_YARD);
    
    
    public final String name;
    ParkingRegionType(String name) {
      this.name = name;
    }
    
    /**
     * Returns corresponding parking region given name or null.
     * @param name
     * @return
     */
    public static ParkingRegionType stringToParkingRegion(String name) {
      for (ParkingRegionType t: ParkingRegionType.values()) {
        if (t.name.equals(name)) {
          return t;
        }
      }
      LOGGER.severe("Could not find parking region type with name: " + name);
      return null;
    }
  }
  
  
  // App utils
  private static Logger LOGGER = Logger.getLogger(ParkingRegion.class
      .getName());
  
  private int parkingRegionID;
  private float angle;
  protected LinkedList<ParkingSpot> parkingSpots;
  
  private Deck deck;
  
  public final ParkingRegionType type;
  
  public ParkingRegion(ParkingRegionType type, float angle) {
    this.type = type;
    this.angle = angle;
    this.parkingSpots = new LinkedList<ParkingSpot>();
    addToDeck();
  }
  
  /**
   * Returns the centroid of all parking spots in this parking region
   * @return
   */
  public Point getCentroid() {
    Point2f centroid = new Point2f();
    for (ParkingSpot spot: parkingSpots) {
      centroid.x += spot.center.x;
      centroid.y += spot.center.y;
    }
    centroid.scale(1.0f/parkingSpots.size());
    return new Point((int)centroid.x, (int)centroid.y);
  }
  
  /**
   * Returns text name of this parking region.
   * @return
   */
  public String name() {
    return this.type.name;
  }
  
  /**
   * Adds parking region to deck.
   */
  private void addToDeck() {
    this.deck = Deck.getInstance();
    this.parkingRegionID = this.deck.nextParkingRegionID();
    this.deck.addParkingRegion(this);
  }
  
  /**
   * returns the parking region id.
   * 
   * @return
   */
  public int parkingRegionID() {
    return this.parkingRegionID;
  }
  
  /**
   * Adds parking spot to parking region.
   * @param center
   */
  public void addParkingSpot(Point center) {
    ParkingSpot spot = new ParkingSpot(center, this);
    this.parkingSpots.add(spot);
  }
  
  /**
   * Returns angle for all parking spots in this parking region.
   * @return
   */
  public float getAngle() {
    return this.angle;
  }
  
  /**
   * Number of parking spots in this parking region.
   * @return
   */
  public int numberParkingSpots() {
    return parkingSpots.size();
  }
    
  /**
   * Returns the first free parking spot in this parking space, or null
   * if there are no free parking spots in this parking space.
   * @return
   */
  public ParkingSpot getNextFreeParkingSpot() {
    for (ParkingSpot p : parkingSpots) {
      if (!p.isOccupied()) {
        return p;
      }
    }
    return null;
  }
  
  /**
   * Returns the specified amount of free parking spots in theis parking region,
   * ignoring any spot that is a "block spot". Will return null for every non
   * free parking spot requested.
   * 
   * @return
   */
  public LinkedList<ParkingSpot> getFreeParkingSpots(int number, LinkedList<ParkingSpot> blockSpots) {
    LinkedList <ParkingSpot> spots = new LinkedList<ParkingSpot>();
    int spotCount = 0;
    if (blockSpots == null) {
      blockSpots = new LinkedList<ParkingSpot>();
    }
    for (ParkingSpot p : parkingSpots) {
      if (!p.isOccupied() && !blockSpots.contains(p)) {
        spots.add(p);
        spotCount += 1;
      }
      if (spotCount == number) {
        return spots;
      }
    }
    for (int i = spotCount; i < number; i++) {
      spots.add(null);
    }
    return spots;
  }
  
  /**
   * Returns all parked aircraft parked in this region;
   * @return
   */
  public LinkedList<FlyingObject> getParkedAircraft(AircraftType typeRestriction) {
    LinkedList<FlyingObject> result = new LinkedList<FlyingObject>();
    for (ParkingSpot p: parkingSpots){
      if (p.hasParkedAircraft()) {
        FlyingObject f = p.parkedAircraft();
        if (typeRestriction != null && f.type != typeRestriction) {
          continue;
        }
        result.add(f);
      }
    }
    return result;
  }
  
  /**
   * Parking spot by index
   * @param index
   * @return
   */
  public ParkingSpot getParkingSpot(int index) {
    return this.parkingSpots.get(index);
  }
  
  /**
   * Parking spots in this parking region.
   * @return
   */
  public LinkedList<ParkingSpot> parkingSpots() {
    return this.parkingSpots;
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void render(PApplet p) {
    // TODO Auto-generated method stub
    
  }
}
