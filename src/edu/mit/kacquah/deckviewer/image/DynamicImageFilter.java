package edu.mit.kacquah.deckviewer.image;

import processing.core.PApplet;

public interface DynamicImageFilter {
  /**
   * Update this filter on times step.
   * @param elapsedTime
   */
  public abstract void updateFilter(long elapsedTime);
  /**
   * Render this filter to the given PApplet.
   * @param p
   */
  public abstract void applyFilter(PApplet p);
}
