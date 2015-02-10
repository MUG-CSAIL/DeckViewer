package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.action.ActionCommand;
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
      return null;
    }
  }
  
  
  
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
   * Returns text name of this parking region.
   * @return
   */
  public String getParkingRegionName() {
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
   * Parking spot by index
   * @param index
   * @return
   */
  public ParkingSpot getParkingSpot(int index) {
    return this.parkingSpots.get(index);
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
