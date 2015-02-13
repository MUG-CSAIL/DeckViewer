package edu.mit.kacquah.deckviewer.gui.shape;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * An interface for defining objects that take up space and can exhibit
 * contactable actions (such as collisions) on deck.
 * 
 * @author kojo
 * 
 */
public interface Contactable {
  /**
   * Center position of object
   * @return
   */
  public Point position();
  /**
   * Radius of object.
   * @return
   */
  public float radius();
  /**
   * Rectangle outlining edges of object.
   * @return
   */
  public Rectangle bounds();
}
