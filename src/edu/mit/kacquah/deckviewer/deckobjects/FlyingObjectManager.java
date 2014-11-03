package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

public class FlyingObjectManager implements PAppletRenderObject {
  private LinkedList<FlyingObject> flyingObjects;

  public FlyingObjectManager() {
    this.flyingObjects = new LinkedList<FlyingObject>();
  }

  public void addFlyingObject(FlyingObject f) {
    this.flyingObjects.add(f);
  }

  public void removeFlyingObject(FlyingObject f) {
    this.flyingObjects.remove(f);
  }

  /**
   * Returns all flying objects in this manager who's bounds intersect the given
   * point.
   * 
   * @param p
   * @return
   */
  public ArrayList<FlyingObject> intersectsPoint(Point p) {
    ArrayList<FlyingObject> result = new ArrayList<FlyingObject>();
    for (FlyingObject f : this.flyingObjects) {
      if (f.intersectsPoint(p)) {
        result.add(f);
      }
    }
    return result;
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
