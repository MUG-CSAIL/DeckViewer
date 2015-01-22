package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;
import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.OpenNI.GeneralException;

import edu.mit.kacquah.deckviewer.action.ActionManager;
import edu.mit.kacquah.deckviewer.action.SelectionManager;
import edu.mit.kacquah.deckviewer.deckobjects.*;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.game.GlobalSettings.BackgroundRatio;
import edu.mit.kacquah.deckviewer.gesture.HandTracker;
import edu.mit.kacquah.deckviewer.gui.DeckViewerSwingFrame;
import edu.mit.kacquah.deckviewer.gui.StaticTextView;
import edu.mit.kacquah.deckviewer.gui.StatusBar;
import edu.mit.kacquah.deckviewer.speech.Commands;
import edu.mit.kacquah.deckviewer.speech.SpeechEngine;
import edu.mit.kacquah.deckviewer.speech.SpeechParser;
import edu.mit.kacquah.deckviewer.utils.*;
import edu.mit.yingyin.tabletop.controllers.ProcessPacketController;
import edu.mit.yingyin.tabletop.models.HandTrackingEngine;
import edu.mit.yingyin.tabletop.models.ProcessPacket;
import edu.mit.yingyin.util.SystemUtil;
import processing.core.*;

/**
 * Main PApplet for running the DeckViewerApp. Extended from processing core
 * Papplet for main run loop and drawing functions.
 * 
 * @author kojo
 * 
 */
public class DeckViewerPApplet extends PApplet implements PAppletRenderObject {
  // App utils
  private static Logger LOGGER = Logger.getLogger(DeckViewerPApplet.class
      .getName());

  // Directory constants
  public final String WORKING_DIR = System.getProperty("user.dir");
  public static final String MAIN_DIR = ".";
  public static final String RESOURCE_DIR = FileUtil
      .join(MAIN_DIR, "resources");
  public static final String CALIB_DIR = FileUtil.join("data", "calibration");
  public static final String OPENNI_CONFIG_FILE = FileUtil.join(MAIN_DIR,
      "config", "config.xml");
  public static final String CALIB_FILE = FileUtil.join(MAIN_DIR, CALIB_DIR,
      "calibration.txt");

  /**
   * App dimensions used to size the application window.
   */
  public int appWidth, appHeight;
  private float scaleRatio;

  // Deck Objects and Managers
  private Deck deck;
  private FlyingObjectManager flyingObjectManager;

  // Hand Tracking
  private HandTracker handTracker;

  // Speech
  private SpeechEngine speechEngine;
  private SpeechParser speechParser;

  // Actions
  private SelectionManager selectionManager;
  private ActionManager actionManager;
  
  // Static Views
  private LinkedList<StaticTextView> staticViews;
  
  /**
   * JFrame that contains this app.
   */
  private JFrame parentFrame;
  
  /**
   * Status bar presented under app.
   */
  private StatusBar statusbar;
  private NumberFormat numberFormater = new DecimalFormat("#0.00");     
  


  public void setup() {
    // Init app state
    initScreenSize();
    // Linux can't use opengl.
    // TODO(KoolJBlack): determine linux opengl bug.
    if (System.getProperty("os.name").equals("Linux")) {
      size(appWidth, appHeight);
    } else {
      size(appWidth, appHeight, OPENGL);
    }
    frameRate(30);

    // Rendering modes
    imageMode(CENTER);
    ellipseMode(CENTER);

    // Init app utils
    PImagePool.setParent(this);
    
    // Static views
    staticViews = new LinkedList<StaticTextView>();

    // Setup the deck environment and variables
    initDeckObjects();

    // Setup tracking
    initHandTracking();

    // Debug strings
    LOGGER.info(WORKING_DIR);

    // Setup speech
    initSpeech();

    // Setup Actions
    selectionManager = new SelectionManager(this, flyingObjectManager, deck,
        handTracker);
    actionManager = new ActionManager(this, selectionManager,
        flyingObjectManager);
    speechParser.setActionManager(actionManager);
    
    // Setup status bar.
    initStatusBar();
  }

  /****************************************************************************/
  /* Processing Methods******************************************************** */
  /****************************************************************************/

  public void draw() {
    // Update the app.
    long elapsedTime = System.currentTimeMillis();
    update(elapsedTime);
    // render the app.
    render(this);
  }

  public void update(long elapsedTime) {
    Point point = new Point(mouseX, mouseY);
    SwingUtilities.convertPointToScreen(point, this);
    // LOGGER.info(""+point);

    // Update all deck objects
    deck.update(elapsedTime);
    flyingObjectManager.update(elapsedTime);

    // Update hand tracking
    handTracker.update(elapsedTime);
    
    // Update static views
    for (StaticTextView view : staticViews) {
      view.update(elapsedTime);
    }
    
    // Update status bar
    updateStatusBar();
  }
  
  private void updateStatusBar() {
    // Action status on left
    statusbar.setMessageLeft(actionManager.getStatus());
    // FrameRate on right
    statusbar.setMessageRight("FrameRate: " + numberFormater.format(frameRate));
  }

  public void render(PApplet p) {
    // Render all deck objects
    deck.render(this);
    flyingObjectManager.render(this);

    // Render handtracking
    handTracker.render(p);
    
    // Render static views
    for (StaticTextView view : staticViews) {
      view.render(this);
    }
  }

  public void keyPressed() {
    switch (key) {
    case 'S':
    case 's':
      // Select a flying object.
      speechParser.createMoveAction("Move this aircraft (keyboard)");
      break;
    case 'E':
    case 'e':
      // Execute an action.
      speechParser.createLocationAction("over there (keyboard)");
      break;
    case 'R':
    case 'r':
      // Reset the handtracking background calibration.
      handTracker.recalibrateBackground();
      break;
    }
  }

  /****************************************************************************/
  /* Additional Methods******************************************************** */
  /****************************************************************************/

  public void initScreenSize() {
    if (GlobalSettings.fitToWindowScreen) {
      fitWindowToScreen();
    } else {
      appWidth = GlobalSettings.desiredWidth;
      appHeight = GlobalSettings.desiredHeight;
    }
  }

  /**
   * Maximizes the app window to the screen boundary while maintaining aspect
   * ratio.
   */
  private void fitWindowToScreen() {
    // Dimensions for current virtual screen (all monitors combined).
    Dimension screen = SystemUtil.getVirtualScreenBounds().getSize();
    // Account for menu bar at bottom of screen.
    screen.height -= GameConstants.STATUS_BAR_HEIGHT * 4;
    float screenRatio = (float) screen.height / (float) screen.width;
    
    float desiredRatio;
    float origWidth;
    if (GlobalSettings.backgroundRatio == BackgroundRatio.NORMAL) {
      desiredRatio = (float) GameConstants.BACKGROUND_HEIGHT
          / (float) GameConstants.BACKGROUND_WIDTH;
      origWidth = (float) GameConstants.BACKGROUND_WIDTH;
    } else {
      desiredRatio = (float) GameConstants.BACKGROUND_WIDE_HEIGHT
          / (float) GameConstants.BACKGROUND_WIDE_WIDTH;
      origWidth = (float) GameConstants.BACKGROUND_WIDE_WIDTH;
    }

    if (screenRatio > desiredRatio) {
      // Size based on maximizing width.
      appWidth = screen.width;
      appHeight = (int) ((float) appWidth * desiredRatio);
    } else {
      // Size based on maximizing height.
      appHeight = screen.height;
      appWidth = (int) ((float) appHeight / desiredRatio);
    }
    
    // If we're creating a window thats too big, scale it down.
    if (GlobalSettings.limitMaxRes) {
      int numPixels = appWidth * appHeight;
      float pixelRatio = ((float)numPixels) / GlobalSettings.maxNumPixels;
      if (pixelRatio > 1.0) {
        float pixelRatioRoot = (float) Math.sqrt(pixelRatio);
        appWidth =  (int)((float)appWidth/ pixelRatioRoot);
        appHeight = (int)((float)appHeight / pixelRatioRoot);
      }
    }

    // The scaling ratio is used to resize all image sprites accordingly.
    scaleRatio = appWidth / origWidth;
    
    LOGGER.info("Final screen resolution: " + appWidth + "x" + appHeight);
  }

  /**
   * Initialize the deck parameters and flight objects.
   */
  private void initDeckObjects() {
    deck = new Deck(this);
    flyingObjectManager = new FlyingObjectManager(this);

    // For now, we'll just place some random objects on the deck.
    PVector pos = new PVector(width / 2, height / 2);
    FlyingObject flyingObject = new FlyingObject(AircraftType.F18, pos, Sprite.Direction.UP.degrees);
    flyingObjectManager.addFlyingObject(flyingObject);

    pos = new PVector(width / 3, height / 2);
    flyingObject = new FlyingObject(AircraftType.F18, pos, Sprite.Direction.RIGHT.degrees);
    flyingObjectManager.addFlyingObject(flyingObject);

    pos = new PVector(width / 3 * 2, height / 2);
    flyingObject = new FlyingObject(AircraftType.F18, pos, Sprite.Direction.LEFT.degrees);
    flyingObjectManager.addFlyingObject(flyingObject);
  }

  /**
   * Starts the hand tracker and debug display.
   */
  private void initHandTracking() {
    // HandTracker
    Dimension tabletopRes = new Dimension(GameConstants.TABLETOP_WIDTH,
        GameConstants.TABLETOP_HEIGHT);
    handTracker = new HandTracker(this, tabletopRes);
    handTracker.initHandTracking(OPENNI_CONFIG_FILE, CALIB_FILE);
  }

  /**
   * Setup speech recognition for app.
   */
  private void initSpeech() {
    speechEngine = new SpeechEngine();
    speechEngine.setGrammarPath(GlobalSettings.grammarPath);
    speechEngine.setGrammarName(GlobalSettings.grammarName);
    speechEngine.initRecognition();

    speechParser = new SpeechParser();
    speechEngine.setSpeechListener(speechParser);
    
    if (GlobalSettings.useSpeechRecognition) {
      speechEngine.startRecognition();
    }
  }
  
  private void initStatusBar() {
    this.statusbar = ((DeckViewerSwingFrame)parentFrame).getStatusBar();
    this.statusbar.setMessage("Ready for command...");
  }
  

  /****************************************************************************/
  /* Accessors *************************************************************** */
  /****************************************************************************/
  public float scaleRatio() {
    return this.scaleRatio;
  }
  
  public void addStaticView(StaticTextView view) {
    this.staticViews.add(view);
  }
  
  public boolean removeStaticView(StaticTextView view) {
    return this.staticViews.remove(view);
  }
  
  public void setParentFrameContainer(JFrame parentFrameContainer) {
    this.parentFrame = parentFrameContainer;
    this.frame = parentFrameContainer;
  }
  
  public void setStatusBar(StatusBar sb) {
    this.statusbar = sb;
  }
}
