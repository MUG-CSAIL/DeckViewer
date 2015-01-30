package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import edu.mit.kacquah.deckviewer.deckobjects.AircraftType;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;

public class CatapultQueueSpot extends ParkingSpot {
  // Angle for parked aircraft
  private float angle;
  
  private boolean isTakeoffSpot;
  
  public CatapultQueueSpot(Point center, ParkingRegion parkingRegion,
      float angle, boolean isTakeoffSpot) {
    super(center, parkingRegion);
    this.angle = angle;
    this.isTakeoffSpot = isTakeoffSpot;
  }
  
  /**
   * Parks an aircraft at this parking spot. This moves the aircraft and Returns
   * true if successfully parks aircraft. Will fail if parking space is occupied.
   * 
   * @param o
   * @return
   */
  public boolean park(FlyingObject o) {
    if (isOccupied()) {
      return false;
    }
    this.parkedAircraft = o;
    this.parkedAircraft.setRotation(this.angle);
    this.parkedAircraft.setPosition(this.center.x, this.center.y);
    if (this.isTakeoffSpot) {
      o.wingsOpen();
    }
    return true;
  }
  
  

}
