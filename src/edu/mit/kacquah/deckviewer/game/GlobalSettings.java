package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;

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

  
  /**
   * Grammar configuration for speech recognition.
   */
  public static final boolean useSpeechRecognition = false;
  public static final String grammarPath = "resource:/edu/mit/kacquah/deckviewer/speech/";
  public static final String grammarName = "deckviewer";
}