package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PFont;
import edu.mit.kacquah.deckviewer.utils.DeckPolygon;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

public class FlyingObjectManager implements PAppletRenderObject {
  private LinkedList<FlyingObject> flyingObjects;
  private int nextUID;
  public final PFont font;
  public final int fontSize;
  private PApplet parent;

  public FlyingObjectManager(PApplet p) {
    this.flyingObjects = new LinkedList<FlyingObject>();
    this.nextUID = 1;
    this.fontSize = 14;
    this.font = p.createFont("Arial", this.fontSize);
    this.parent = p;
    p.textAlign(p.CENTER, p.CENTER);
  }

  public void addFlyingObject(FlyingObject f) {
    this.flyingObjects.add(f);
    f.addToFlyingObjectManager(this);
  }

  public void removeFlyingObject(FlyingObject f) {
    this.flyingObjects.remove(f);
    f.removeFromFlyingObjectManager(this);
  }
  
  public int getNextUID() {
    return nextUID++;
  }

  /**
   * Returns all flying objects in this manager who's bounds intersect the given
   * point.
   * 
   * @param p
   * @return
   */
  public LinkedList<FlyingObject> intersectsPoint(Point p) {
    LinkedList<FlyingObject> result = new LinkedList<FlyingObject>();
    for (FlyingObject f : this.flyingObjects) {
      if (f.intersectsPoint(p)) {
        result.add(f);
      }
    }
    return result;
  }
  
  /**
   * Returns all flying objects that intersect a given flying object. 
   * @param other
   * @return
   */
  public LinkedList<FlyingObject> intersectsFlyingObjects(FlyingObject other) {
    LinkedList<FlyingObject> result = new LinkedList<FlyingObject>();
    for (FlyingObject f : this.flyingObjects) {
      if (f.intersectsFlyingObject(other)) {
        result.add(f);
      }
    }
    return result; 
  }
  
  /**
   * Returns deck objects that intersect polygon.
   * @return
   */
  public LinkedList<FlyingObject> intersectsPolygon(DeckPolygon p) {
    LinkedList<FlyingObject> result = new LinkedList<FlyingObject>();
    for (FlyingObject f : this.flyingObjects) {
      if (p.contains(f.getPosition())) {
        result.add(f);
      }
    }
    return result;   }
  
  /**
   * Returns aircraft with specific UID or null if can't find one.
   * @param number
   * @return
   */
  public FlyingObject getAircraftWithUID(int number) {
    for (FlyingObject f : this.flyingObjects) {
      if (f.getUID() == number) {
        return f;
      }
    }
    return null;
  }

  @Override
  public void update(long elapsedTime) {
    // Update the manager.

    // Update all flying objects
    for (FlyingObject f : this.flyingObjects) {
      f.update(elapsedTime);
    }

  }

  @Override
  public void render(PApplet p) {
    // Render all flying objects
    for (FlyingObject f : this.flyingObjects) {
      f.render(p);
    }
  }

}
