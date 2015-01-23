package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;

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

}
