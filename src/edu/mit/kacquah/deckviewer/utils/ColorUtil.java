package edu.mit.kacquah.deckviewer.utils;


/**
 * Static color utilities for processing apps. 
 * @author kojo
 *
 */
public class ColorUtil {

  // Color constants
  public static final int RED = color(255, 0, 0);
  public static final int GREEN = color(0, 255,0);
  public static final int BLUE = color(0, 0, 255);
  public static final int BLACK = color(0);
  public static final int WHITE = color(255);
  
  public static final int color(float grey) {
    return color(grey, grey, grey, 255);
  }
  
  public static final int color(float grey, float a) {
    return color(grey, grey, grey, a);
  }
  
  public static final int color(float x, float y, float z) {
    return color(x, y, z, 255);
  }
  
  /**
   * Produces Processing format color stored in int given parameters. Color
   * channels should be between 0 and 255.
   * 
   * @param x
   * @param y
   * @param z
   * @param a
   * @return
   */
  public static final int color(float x, float y, float z, float a) {
    if (a > 255)
      a = 255;
    else if (a < 0)
      a = 0;
    if (x > 255)
      x = 255;
    else if (x < 0)
      x = 0;
    if (y > 255)
      y = 255;
    else if (y < 0)
      y = 0;
    if (z > 255)
      z = 255;
    else if (z < 0)
      z = 0;

    return ((int) a << 24) | ((int) x << 16) | ((int) y << 8) | (int) z;
  }

}
