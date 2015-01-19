package edu.mit.kacquah.deckviewer.utils;

import processing.core.PApplet;
import processing.core.PGraphics;


/**
 * Object that is updated and rendered by a PApplet.
 * @author kojo
 *
 */
public interface PAppletRenderObject {
  /**
   * Update this object on timesstep
   * @param elapsedTime
   */
  public abstract void update(long elapsedTime);
  /**
   * Render this object to the given PApplet.
   * @param p
   */
//  public abstract void render(PApplet p);
  public abstract void render(PGraphics p);

}
