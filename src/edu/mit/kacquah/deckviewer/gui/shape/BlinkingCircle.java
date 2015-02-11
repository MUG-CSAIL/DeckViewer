package edu.mit.kacquah.deckviewer.gui.shape;

import java.awt.Point;

import javax.vecmath.Point4f;
import javax.vecmath.Tuple4f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Renders a blinking circle on screen.
 */
public class BlinkingCircle implements PAppletRenderObject {

  private Point center;
  private float radius;
  private Point4f peakColorComponents; 
  private int color;
  private int renderColor;
  private double angle;
  private float alpha;
  private boolean hollow;
  
  public BlinkingCircle(Point center, float radius, int color, boolean hollow) {
    this.center = center;
    this.radius = radius;
    this.peakColorComponents = ColorUtil.colorComponents(color);
    this.color = color;
    this.angle = 0;
    this.hollow = hollow;
  }

  @Override
  public void update(long elapsedTime) {
    // Update angle
    angle = elapsedTime /1000.0 * GlobalSettings.OSCILLATION_RATE;
    angle %= Math.PI * 2;
    // Compute color
    alpha = (float)((Math.sin(angle) + 1)/ 2.0);
    Point4f renderColorComponents = new Point4f();
    renderColorComponents.interpolate((Tuple4f)peakColorComponents, 
        (Tuple4f)ColorUtil.WHITE_COMPONENTS, alpha);
    renderColor = ColorUtil.color(renderColorComponents);    
  }

  @Override
  public void render(PApplet p) {
    p.pushMatrix();
    p.pushStyle();
    p.translate(center.x, center.y);
    p.fill(color, alpha * 255);
    p.stroke(color, alpha * 255);
    p.strokeWeight(GlobalSettings.STROKE_WEIGHT);
    if (hollow) {
      p.noFill();
    } else {
      p.noStroke();
    }
    p.ellipse(0, 0, radius * 2, radius * 2);
    p.popStyle();
    p.popMatrix();
    
  }
}
