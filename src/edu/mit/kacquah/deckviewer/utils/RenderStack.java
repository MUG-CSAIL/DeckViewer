package edu.mit.kacquah.deckviewer.utils;

import java.util.LinkedList;

import processing.core.PApplet;

/**
 * Maintains groups of render objects or individual render objects and draws
 * them on screen.
 * 
 * @author kojo
 * 
 */
public class RenderStack implements PAppletRenderObject {
  
  private LinkedList<RenderGroup> renderGroups;
  private RenderGroup renderObjects;

  public RenderStack() {
    this.renderGroups = new LinkedList<RenderGroup>();
    this.renderObjects = new RenderGroup();
  }
  
  /**
   * Adds an object to this render stack.
   * @param o
   */
  public void addRenderObject(PAppletRenderObject o) {
    this.renderObjects.addRenderObject(o);
  }
  
  
  /**
   * Removes an object from this render stack.
   * @param o
   * @return
   */
  public boolean removeRenderObject(PAppletRenderObject o) {
    return this.renderObjects.removeRenderObject(o);
  }
  
  /**
   * Add a reder group.
   * @param g
   */
  public void addRenderGroup(RenderGroup g) {
    this.renderGroups.add(g);
  }
  
  /**
   * Remove a render group.
   * @param g
   * @return
   */
  public boolean removeRenderGroup(RenderGroup g) {
    return this.renderGroups.remove(g);
  }

  @Override
  public void update(long elapsedTime) {
    this.renderObjects.update(elapsedTime);
    for (RenderGroup g: renderGroups) {
      g.update(elapsedTime);
    }
  }

  @Override
  public void render(PApplet p) {
    this.renderObjects.render(p);
    for (RenderGroup g: renderGroups) {
      g.render(p);
    }
  }
  
  

}
