package edu.mit.kacquah.deckviewer.gui.shape;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.Geometry;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Draws a straight line and arrow on screen.
 * @author kojo
 *
 */
public class StraightLineArrow implements PAppletRenderObject {
  
  private Point start, end;
  private int color;
  private float arrowAngle;
  
  private final float STROKE_WEIGHT = 3;
  private final int TRIANGLE_RADIUS = 10;
  
  private Point t1, t2, t3;
  
  public StraightLineArrow(Point start, Point end, int color) {
    this.start = start;
    this.end = end;
    this.color = color;
    this.arrowAngle = Geometry.angle(start, end);
    // Triangle points
    t1 = new Point(0, -TRIANGLE_RADIUS);
    t2 = new Point(0, +TRIANGLE_RADIUS);
    t3 = new Point(2 * TRIANGLE_RADIUS, 0);
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void render(PApplet p) {
    p.pushStyle();
    p.pushMatrix();
    // Draw line
    p.stroke(color);
    p.strokeWeight(STROKE_WEIGHT);
    p.line(start.x, start.y, end.x, end.y);
    // Draw arrow
    p.translate(end.x, end.y);
    p.rotate(arrowAngle);
    p.triangle(t1.x, t1.y, t2.x, t2.y, t3.x, t3.y);
    p.popMatrix();
    p.popStyle(); 
    
  }

}
