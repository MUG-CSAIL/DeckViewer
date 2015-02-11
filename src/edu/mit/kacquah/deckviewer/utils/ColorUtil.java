package edu.mit.kacquah.deckviewer.utils;

import javax.vecmath.Point4f;


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
  public static final int ORANGE = color(255, 165, 0);
  public static final int YELLOW = color(255, 255, 0);
  public static final int BLACK = color(0);
  public static final int WHITE = color(255);
  
  // Color component constants
  public static final Point4f WHITE_COMPONENTS = colorComponents(WHITE);
  public static final Point4f RED_COMPONENTS = colorComponents(RED);
  public static final Point4f GREEN_COMPONENTS = colorComponents(GREEN);
  public static final Point4f BLUE_COMPONENTS = colorComponents(BLUE);
  
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
  
  public static final int color(Point4f components){
    return color((int) components.x, 
                 (int) components.y, 
                 (int) components.z,
                 (int) components.w);
  }
  
  public static final Point4f colorComponents(int color) {
    int BLUE_MASK = Integer.parseInt("000000FF", 16);
    int GREEN_MASK = BLUE_MASK << 8;
    int RED_MASK = BLUE_MASK << 16;
    int ALPHA_MASK = BLUE_MASK << 24;    
    Point4f components = new Point4f();
    components.x = (RED_MASK & color) >> 16;
    components.y = (GREEN_MASK & color) >> 8;
    components.z = (BLUE_MASK & color) >> 0;
    components.w = (ALPHA_MASK & color) >>> 24;    
    return components;
  }

}
