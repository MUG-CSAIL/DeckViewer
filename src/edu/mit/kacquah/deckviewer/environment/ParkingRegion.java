package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;

public class ParkingRegion {
  private int parkingRegionID;
  private float angle;
  private LinkedList<ParkingSpot> parkingSpots;
  
  private Deck deck;
  
  public ParkingRegion(float angle) {
    this.angle = angle;
    this.parkingSpots = new LinkedList<ParkingSpot>();
    addToDeck();
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
    ParkingSpot spot = new ParkingSpot(center, this, parkingSpots.size());
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
