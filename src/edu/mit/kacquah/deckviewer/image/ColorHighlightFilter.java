package edu.mit.kacquah.deckviewer.image;

import javax.vecmath.Point4f;
import javax.vecmath.Tuple4f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * A rate controlled, single color oscillating color tint filter.
 * @author kojo
 *
 */
public class ColorHighlightFilter implements DynamicImageFilter {
  /**
   * Rate of filter oscillation.
   */
  private static final double OSCILLATION_RATE = 5.0;

  private double angle;
  private long lastElapsedTime;
  private Point4f peakColorComponents; 
  private int renderColor;

  public ColorHighlightFilter(int c) {
    this.peakColorComponents = ColorUtil.colorComponents(c);
    this.lastElapsedTime = System.currentTimeMillis();
    this.angle = 0;
  }
  
  /**
   * Update the filter oscillation.
   */
  @Override
  public void updateFilter(long elapsedTime) {
    // Update angle
    angle = elapsedTime /1000.0 * OSCILLATION_RATE;
    angle %= Math.PI * 2;
    lastElapsedTime = elapsedTime;
    // Compute color
    float a = (float)((Math.sin(angle) + 1)/ 2.0);
    Point4f renderColorComponents = new Point4f();
    renderColorComponents.interpolate((Tuple4f)peakColorComponents, 
        (Tuple4f)ColorUtil.WHITE_COMPONENTS, a);
    renderColor = ColorUtil.color(renderColorComponents);
  }

  /**
   * Applies color tint to the renderer.
   */
  @Override
  public void applyFilter(PApplet p) {
    p.tint(renderColor);
  }

}
