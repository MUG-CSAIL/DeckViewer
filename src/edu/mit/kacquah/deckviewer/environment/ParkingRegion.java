package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;

import edu.mit.kacquah.deckviewer.action.ActionCommand;

public class ParkingRegion {
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
    CATAPULT_4(ActionCommand.CATAPULT_4);
    
    public final String name;
    ParkingRegionType(String name) {
      this.name = name;
    }
  }
  
  private int parkingRegionID;
  private float angle;
  private LinkedList<ParkingSpot> parkingSpots;
  
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
  public ParkingSpot getFirstFreeParkingSpot() {
    for (ParkingSpot p : parkingSpots) {
      if (!p.isOccupied()) {
        return p;
      }
    }
    return null;
  }
  
  

}
