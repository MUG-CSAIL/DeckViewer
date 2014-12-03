package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;

import edu.mit.kacquah.deckviewer.utils.ColorUtil;

/**
 * Global settings for configuring the entire application.
 * @author kojo
 *
 */
public class GlobalSettings {
  
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
 
  /**
   * HandTracking constants.
   */
  // Length of history for finger position smoothing. 
  public static final int FILTER_HISTORY_LENGTH = 5;
  public static final int FINGER_CIRCLE_RADIUS = 10;
  // Use mouse pointer instead of finger points (for simpler debugging)
  public static final boolean useMousePoint = false;

  
  /**
   * Grammar configuration for speech recognition.
   */
  public static final boolean useSpeechRecognition = true;
  public static final String grammarPath = "resource:/edu/mit/kacquah/deckviewer/speech/";
  public static final String grammarName = "deckviewer";
  
  /**
   * Deck rendering configuration.
   */
  public static boolean renderDeckOutline = true;
  public static boolean renderDeckCatapults = true;
  public static boolean renderDeckElevators = true;
  
  /**
   * Selection engine constants
   */
  public static final int selectionHighlightColor = ColorUtil.ORANGE;
}
