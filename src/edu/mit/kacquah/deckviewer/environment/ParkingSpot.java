package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.gui.shape.Contactable;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.DeckPolygon;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

public class ParkingSpot extends DeckPolygon implements PAppletRenderObject, Contactable {
  // Utils
  private static Logger LOGGER = Logger.getLogger(ParkingSpot.class
      .getName());
  
  protected ParkingRegion parkingRegion;
  protected int parkingSpotID;
  protected Deck deck;
  public final Point center;

  protected FlyingObject parkedAircraft;
  
  protected boolean renderOutline;

  // Constants
  protected static final int RADIUS = (int) (Deck.getInstance().scaleRatio * 30);

  public ParkingSpot(Point center, ParkingRegion parkingRegion) {
    this.center = center;
    addPoint(center.x + RADIUS, center.y - RADIUS);
    addPoint(center.x + RADIUS, center.y + RADIUS);
    addPoint(center.x - RADIUS, center.y + RADIUS);
    addPoint(center.x - RADIUS, center.y - RADIUS);
    this.parkingRegion = parkingRegion;
    this.renderOutline = false;
    addToDeck();
  }
  
  /**
   * Parking region for this parking spot.
   * @return
   */
  public ParkingRegion parkingRegion() {
    return this.parkingRegion;
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
    this.parkedAircraft.setParkingSpot(this);
    return true;
  }
  
  /**
   * Checks to see if there is a parked aircraft here and if its still on the spot.
   * @return
   */
  public boolean hasParkedAircraft() {
    if (parkedAircraft != null && contains(parkedAircraft.positionFloat())) {
      return true;
    } else {
      // Clear last parked aircraft.
      if (parkedAircraft != null) {
        parkedAircraft.setParkingSpot(null);
      }
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
    if (hasParkedAircraft()) {
      return this.parkedAircraft;
    }
    return null;
  }
  
  @Override
  public Point position() {
    return new Point(center);
  }

  @Override
  public float radius() {
    return RADIUS;
  }

  @Override
  public Rectangle bounds() {
    return new Rectangle((int) center.x - RADIUS, (int) center.y - RADIUS,
        RADIUS * 2, RADIUS * 2);
  }
  
  @Override
  public void update(long elapsedTime) {
    super.update(elapsedTime);
  }

  @Override
  public void render(PApplet p) {
    if (GlobalSettings.renderParkingSpots) {
      p.pushStyle();
      p.noFill();
      p.stroke(ColorUtil.RED);
      p.strokeWeight(GlobalSettings.STROKE_WEIGHT);
      super.render(p);
      p.popStyle();
    }
  }
}
