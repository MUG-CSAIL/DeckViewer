package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.util.LinkedList;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.utils.DeckPolygon;

public class ParkingSpot extends DeckPolygon {
  protected ParkingRegion parkingRegion;
  protected int parkingSpotID;
  protected Deck deck;
  protected Point center;

  protected FlyingObject parkedAircraft;

  // Constants
  protected static final int RADIUS = 30;

  public ParkingSpot(Point center, ParkingRegion parkingRegion) {
    this.center = center;
    addPoint(center.x + RADIUS, center.y - RADIUS);
    addPoint(center.x + RADIUS, center.y + RADIUS);
    addPoint(center.x - RADIUS, center.y + RADIUS);
    addPoint(center.x - RADIUS, center.y - RADIUS);
    this.parkingRegion = parkingRegion;
    addToDeck();
  }

  /**
   * Adds parking spot to deck;
   */
  private void addToDeck() {
    this.deck = Deck.getInstance();
    this.parkingSpotID = this.deck.nextParkingSpotID();
    this.deck.addParkingSpot(this);
  }

  /**
   * returns the parking spot id.
   * 
   * @return
   */
  public int parkingSpotID() {
    return this.parkingSpotID;
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
    this.parkedAircraft.setRotation(this.parkingRegion.getAngle());
    this.parkedAircraft.setPosition(this.center.x, this.center.y);
    return true;
  }
  
  /**
   * Checks to see if there is a parked aircraft here and if its still on the spot.
   * @return
   */
  public boolean hasParkedAircraft() {
    if (parkedAircraft != null && contains(parkedAircraft.getPosition())) {
      return true;
    } else {
      // Clear last parked aircraft.
      parkedAircraft = null;
      return false;
    }
  }
  
  /**
   * Checks to see if any aircraft are on top of this spot.
   * @return
   */
  public boolean isCovered() {
    LinkedList<FlyingObject> intersections = deck.getFlyingObjectManager()
        .intersectsPolygon(this);
    if (intersections.size() != 0) {
      return true;
    }
    return false;
  }

  /**
   * Returns true if another aircraft is on this parking spot.
   * 
   * @return
   */
  public boolean isOccupied() {
    // Check parked aircraft to see if it's still on the spot.
    if (hasParkedAircraft()) {
      return true;
    }
    // Check any flying objects on deck to see if they are on the spot {
    if (isCovered()) {
      return true;
    }
    return false;
  }
  
  /**
   * Aircraft currently parked on this spot. Returns null if there is no aircraft.
   * @return
   */
  public FlyingObject parkedAircraft() {
    return this.parkedAircraft;
  }

}
