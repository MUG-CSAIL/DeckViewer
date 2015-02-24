package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.utils.Geometry;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Represents a catapult launcher on a deck.
 * @author kojo
 *
 */
public class Catapult implements PAppletRenderObject{
  private Point startPoint, endPoint;
  public final int catapultNumber;
  private CatapultQueue catapultParking;

  public Catapult(Point start, Point end, int number) {
    this.startPoint = start;
    this.endPoint = end;
    this.catapultNumber = number;
  }
  
  /**
   * Returns the direction this catapult points in.
   * @return
   */
  public float takeoffDirection() {
    return Geometry.angle(startPoint, endPoint);
  }
  
  /**
   * Starting point of catapult.
   * @return
   */
  public Point startPoint() {
    return this.startPoint;
  }
  
  /**
   * End point of catapult.
   * @return
   */
  public Point endPoint() {
    return this.endPoint;
  }
  
  /**
   * Parking region for this catapult.
   * @return
   */
  public ParkingRegion catapultParking() {
    return this.catapultParking;
  }
  
  /**
   * Parking region for this catapult.
   * @return
   */
  public CatapultQueue catapultParkingQueue() {
    return this.catapultParking;
  }
  
  public void setCatapultParking(ParkingRegion parking) {
    this.catapultParking = (CatapultQueue)parking;
  }
  
  /**
   * Parks an aircraft for takeoff on this catapult. Returns false if parking
   * unsuccessful.
   * 
   * @param f
   * @return
   */
  public boolean parkAircraft(FlyingObject f) {
    ParkingSpot parkingSpot = catapultParking.getNextFreeParkingSpot();
    if (parkingSpot == null) {
      return false;
    }
    return parkingSpot.park(f);
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  /**
   * Renders a line representing a catapult.
   */
  @Override
  public void render(PApplet p) {
    p.line(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
  }
}
