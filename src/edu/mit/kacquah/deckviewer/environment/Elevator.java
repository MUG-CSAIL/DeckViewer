package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.Sprite;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.utils.DeckPolygon;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Represents an elevator on a flight deck.
 * @author kojo
 *
 */
public class Elevator extends DeckPolygon implements PAppletRenderObject {
  private Point centerPoint;
  private int elevatorNumber;
  private ParkingRegion elevatorParking;
  
  private static final float circleRadius = 5.0f;
  
  public Elevator(Point centerPoint, int number) {
    super();
    this.centerPoint = centerPoint;
    this.elevatorNumber = number;
  }
  
  /**
   * Center of elevator
   * @return
   */
  public Point elevatorCenter() {
    return this.centerPoint;
  }
  
  /**
   * Get direction of aircraft on this elevator;
   * @return
   */
  public float elevatorDirection() {
    switch (elevatorNumber) {
    case 1:
      return Sprite.Direction.UP.degrees;
    case 2:
      return Sprite.Direction.UP.degrees;
    case 3:
      return Sprite.Direction.UP.degrees;
    case 4:
      return Sprite.Direction.DOWN.degrees;
    }
    return -1;
  }
  
  /**
   * Set the parking region for this elevator.
   * @param parking
   */
  public void setElevatorParking(ParkingRegion parking) {
    this.elevatorParking = parking;
  }
  
  /**
   * Parks an aircraft for takeoff on this catapult. Returns false if parking
   * unsuccessful.
   * 
   * @param f
   * @return
   */
  public boolean parkAircraft(FlyingObject f) {
    ParkingSpot parkingSpot = elevatorParking.getFirstFreeParkingSpot();
    if (parkingSpot == null) {
      return false;
    }
    return parkingSpot.park(f);
  }
  
  /**
   * Parking region fo rthis elevator.
   */
  public ParkingRegion elevatorParking() {
    return this.elevatorParking;
  }
   
  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  /**
   * Renders the outline and center of deck elevators;
   */
  public void render(PApplet p) {       
    // Render Edges
    p.pushStyle();
    p.noFill();
    p.stroke(0, 255, 0);
    super.render(p);
    // Render Center
    p.noStroke();
    p.fill(p.color(255, 165, 0));
    p.ellipse(centerPoint.x, centerPoint.y, circleRadius, circleRadius);
    p.popStyle();
    
    
  }

}
