package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import javax.vecmath.Point2f;

import edu.mit.kacquah.deckviewer.deckobjects.Sprite.Direction;

public class CatapultQueue extends ParkingRegion {
  Catapult catapult;
  
  public CatapultQueue(ParkingRegionType type, Catapult catapult) {
    super(type, -1);
    this.catapult = catapult;
    initParkingSpots();
  }
  
  /**
   * Add a queue spot to catapult queue.
   * @param center
   * @param angle
   */
  public void addQueueSpot(Point center, float angle, boolean isTakeoffSpot) {
    CatapultQueueSpot spot = new CatapultQueueSpot(center, this, angle, isTakeoffSpot);
    this.parkingSpots.add(spot);
  }
  
  /**
   * Scales a point based on the scale factor for the deck.
   * 
   * @param p
   * @return
   */
  public Point scale(int x, int y) {
    float scaleRatio = Deck.getInstance().scaleRatio;
    return new Point((int) (x * scaleRatio), (int) (y * scaleRatio));
  }  
  
  public int scale(int diff) {
    float scaleRatio = Deck.getInstance().scaleRatio;
    return (int) (diff * scaleRatio);
  }
  
  /**
   * Sets up parking spots for this catapult queue based on catapult number.
   */
  public void initParkingSpots() {
    Deck deck = Deck.getInstance();
    Point catTakeoffSpot;
    Point queueSpot1;
    Point queueSpot2;
    Point queueSpot3;
    switch (this.catapult.catapultNumber) {
    case 1:
      // Takeoff spot
      catTakeoffSpot = catapultTakeoffSpot(this.catapult);
      this.addQueueSpot(catTakeoffSpot, this.catapult.takeoffDirection(), true);
      // Queue spot 1
      queueSpot1 = new Point(this.catapult.startPoint());
      queueSpot1.x -= scale(120);
      queueSpot1.y -= scale(10);
      this.addQueueSpot(queueSpot1, Direction.RIGHT.degrees + 10, false);
      // Queue spot 2
      queueSpot2 = new Point(queueSpot1);
      queueSpot2.x -= scale(140);
      this.addQueueSpot(queueSpot2, Direction.RIGHT.degrees, false);
      // Queue spot 3
      queueSpot3 = new Point(queueSpot2);
      queueSpot3.x -= scale(140);
      this.addQueueSpot(queueSpot3, Direction.RIGHT.degrees, false);
      break;
    case 2:
      // Takeoff spot
      catTakeoffSpot = catapultTakeoffSpot(this.catapult);
      this.addQueueSpot(catTakeoffSpot, this.catapult.takeoffDirection(), true);
      // Queue spot 1
      queueSpot1 = new Point(this.catapult.startPoint());
      queueSpot1.x -= scale(120);
      this.addQueueSpot(queueSpot1, Direction.RIGHT.degrees, false);
      // Queue spot 2
      queueSpot2 = new Point(queueSpot1);
      queueSpot2.x -= scale(140);
      this.addQueueSpot(queueSpot2, Direction.RIGHT.degrees, false);
      // Queue spot 3
      queueSpot3 = new Point(queueSpot2);
      queueSpot3.x -= scale(140);
      this.addQueueSpot(queueSpot3, Direction.RIGHT.degrees, false);
      break;
    case 3:
      // Takeoff spot
      catTakeoffSpot = catapultTakeoffSpot(this.catapult);
      this.addQueueSpot(catTakeoffSpot, this.catapult.takeoffDirection(), true);
      // Queue spot 1
      queueSpot1 = new Point(this.catapult.startPoint());
      queueSpot1.x -= scale(120);
      queueSpot1.y += scale(30);
      this.addQueueSpot(queueSpot1, this.catapult.takeoffDirection(), false);
      // Queue spot 2
      queueSpot2 = new Point(queueSpot1);
      queueSpot2.x -= scale(140);
      this.addQueueSpot(queueSpot2, Direction.RIGHT.degrees, false);
      // Queue spot 3
      queueSpot3 = new Point(queueSpot2);
      queueSpot3.x -= scale(140);
      this.addQueueSpot(queueSpot3, Direction.RIGHT.degrees, false);
      break;
    case 4:
      // Takeoff spot
      catTakeoffSpot = catapultTakeoffSpot(this.catapult);
      this.addQueueSpot(catTakeoffSpot, this.catapult.takeoffDirection(), true);
      // Queue spot 1
      queueSpot1 = new Point(this.catapult.startPoint());
      queueSpot1.x -= scale(120);
      this.addQueueSpot(queueSpot1, Direction.RIGHT.degrees, false);
      // Queue spot 2
      queueSpot2 = new Point(queueSpot1);
      queueSpot2.x -= scale(140);
      this.addQueueSpot(queueSpot2, Direction.RIGHT.degrees, false);
      // Queue spot 3
      queueSpot3 = new Point(queueSpot2);
      queueSpot3.x -= scale(140);
      this.addQueueSpot(queueSpot3, Direction.RIGHT.degrees, false);
      break;
    }
  }
  
  /**
   * Finds the correct takeoff point along a given catapult.
   */
  private Point catapultTakeoffSpot(Catapult cat) {
    float lerpAmount = 0.1f;
    Point2f startPoint = new Point2f(cat.startPoint().x, cat.startPoint().y);
    Point2f endPoint = new Point2f(cat.endPoint().x, cat.endPoint().y);
    Point2f result = new Point2f();
    result.interpolate(startPoint, endPoint, lerpAmount);
    return new Point((int) result.x, (int) result.y);
  }
  
 
  /**
   * Returns the next free spot in the queue. Will return null if the next free
   * spot is blocked or if there are no more parking spots available.
   */
  public ParkingSpot getNextFreeParkingSpot() {
    return super.getNextFreeParkingSpot();
  }
  
  /**
   * Returns true if the launch spot for the catapult is occupied
   * @return
   */
  public boolean isCatapultOccupied() {
    return this.parkingSpots.get(0).isOccupied();
  }
  
  

}
