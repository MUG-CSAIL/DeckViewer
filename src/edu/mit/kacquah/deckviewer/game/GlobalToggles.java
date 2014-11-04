package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;

/**
 * Global settings for configuring the entire application.
 * @author kojo
 *
 */
public class GlobalToggles {
  
  public enum BackgroundRatio{ WIDE, NORMAL};
  /**
   * Choose between wide cropped or full display of carrier.
   */
  public static BackgroundRatio backgroundRatio = BackgroundRatio.WIDE;

  /**
   * When true, the app will resize to fit the window screen.
   */
  public static boolean fitToWindowScreen  = true;
  
  /**
   * When not using fitToWindowScreen, these will be the app width/height.
   */
  public static int desiredWidth = 2560;
  public static int desiredHeight = 2048;
  
}
