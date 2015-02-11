package edu.mit.kacquah.deckviewer.utils;

import java.util.LinkedList;

import processing.core.PApplet;

/**
 * A group of PAppletRender objects to be renderd on screen.
 * @author kojo
 *
 */
public class RenderGroup implements PAppletRenderObject {
  
  private LinkedList<PAppletRenderObject> renderObjects;
  
  public RenderGroup() {
    this.renderObjects = new LinkedList<PAppletRenderObject>();
  }
  
  /**
   * Adds an object to this render group
   * @param o
   */
  public void addRenderObject(PAppletRenderObject o) {
    this.renderObjects.add(o);
  }
  
  
  /**
   * Removes an object from this render group.
   * @param o
   * @return
   */
  public boolean removeRenderObject(PAppletRenderObject o) {
    return this.renderObjects.remove(o);
  }

  @Override
  public void update(long elapsedTime) {
    for (PAppletRenderObject o : renderObjects) {
      o.update(elapsedTime);
    }  
  }

  @Override
  public void render(PApplet p) {
    for (PAppletRenderObject o : renderObjects) {
      o.render(p);
    }    
  }
}
