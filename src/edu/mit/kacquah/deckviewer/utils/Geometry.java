package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;
import java.awt.geom.Line2D;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

/**
 * Math helpers for points and polygons.
 * @author kojo
 *
 */
public class Geometry {
  /**
   * Returns the angle (in degrees) of a line formed by two points compared to the x axis
   * @param start
   * @param end
   * @return
   */
  public static float angle(Point start, Point end) {
    Vector2f v1 = new Vector2f(1, 0);
    Vector2f v2 = new Vector2f(end.x - start.x, end.y - start.y);
    v2.normalize();
    float angle = v1.angle(v2);
    angle = (float)Math.toDegrees(angle);
    if (v2.y < 0) {
      angle = 180 + (180 - angle);
    }
    return angle;
  }
  
  /**
   * Converts a point2f to a point.
   * @param p
   * @return
   */
  public static Point pointFloatToInt(Point2f p) {
    return new Point((int)p.x, (int)p.y);
  }
    
  /**
   * Translates a line in a given direction by a set distance.
   * @param line
   * @param direction
   * @param distance
   * @return
   */
  public static Line2D.Float translateLine(Line2D.Float line, float direction,
      float distance) {
    line = new Line2D.Float(line.x1, line.y1, line.x2, line.y2);
    
    // Get components
    float xcomp = (float) (Math.cos(direction) * distance);
    float ycomp = (float) (Math.sin(direction) * distance);
    
    line.x1 += xcomp;
    line.x2 += xcomp;
    line.y1 += ycomp;
    line.y2 += ycomp;
    
    return line;
  }
}
