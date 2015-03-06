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
  public static final BackgroundRatio backgroundRatio = BackgroundRatio.WIDE;

  /**
   * When true, the app will resize to fit the window screen.
   */
  public static final boolean fitToWindowScreen  = true;
  
  /**
   * When not using fitToWindowScreen, these will be the app width/height.
   */
  public static final int desiredWidth = 2560;
  public static final int desiredHeight = 2048;
 
  /**
   * Limit the maximum resolution of the app for runtime optimization.
   */
  public static final int maxNumPixels = 3000000;
  public static final boolean limitMaxRes = false;
  
  /**
   * HandTracking constants.
   */
  // Length of history for finger position smoothing. 
  public static final int FILTER_HISTORY_LENGTH = 5;
  public static final int FINGER_CIRCLE_RADIUS = 10;
  // Use mouse pointer instead of finger points (for simpler debugging)
  public static final boolean useMousePoint = false;
  public static final boolean usePointingEstimate = true;
  public static final boolean showFingerPoints = false;
  public static final boolean showPointingPoint = true;

  /**
   * Grammar configuration for speech recognition.
   */
  public static final boolean useSpeechRecognition = true;
  public static final boolean useSpeechSynthesis = true;
  public static final String speechSynthesisVoice = "mbrola_us2";
  public static final String grammarPath = "resource:/edu/mit/kacquah/deckviewer/speech/recognizer";
  public static final String grammarName = "deckviewer";
  
  /**
   * Deck rendering configuration.
   */
  public static final boolean renderDeckOutline = true;
  public static final boolean renderDeckCatapults = true;
  public static final boolean renderDeckElevators = false;
  public static final boolean renderParkingSpots = false;
  
  /**
   * Selection engine constants
   */
  public static final int selectionStatusSelectedColor = ColorUtil.ORANGE;
  public static final int selectionStatusHoveringColor = ColorUtil.GREEN;
  public static final int selectionStatusErrorColor = ColorUtil.RED;
  // Radius for selecting multiple aircraft
  public static final float MULTI_SELECTION_RADIUS = 60;
  public static final float STICKY_SELECTION_RADIUS = 100;
  public static final boolean USE_STICKY_SELECTION = true;
  
  /**
   * FlyingObject constants.
   */
  public static final boolean renderAircraftUIDs = true;
  public static final float AIRCRAFT_RADIUS = 40;
  
  /**
   * ExecAction constants.
   */
  public static final int aircraftPathColor = ColorUtil.ORANGE;
  public static final boolean renderPathEdgeLines = false;
  public static final float renderPathPointRadius = 5;
  
  /**
   * Rendering/image/gui constants.
   */
  //Rate of filter oscillation
  public static final double OSCILLATION_RATE = 5.0;
  public static final float STROKE_WEIGHT = 3;
  
  /**
   * Scene configuration
   */
  public static final int SCHENE_NUMBER = 1;
}
