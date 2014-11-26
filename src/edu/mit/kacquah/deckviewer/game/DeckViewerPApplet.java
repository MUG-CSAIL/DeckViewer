package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.OpenNI.GeneralException;

import edu.mit.kacquah.deckviewer.action.SelectionManager;
import edu.mit.kacquah.deckviewer.deckobjects.*;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.game.GlobalSettings.BackgroundRatio;
import edu.mit.kacquah.deckviewer.gesture.HandTracker;
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
  private int appWidth;
  private int appHeight;
  private float scaleRatio;

  // Deck Objects and Managers
  private Deck deck;
  private FlyingObjectManager flyingObjectManager;

  // Hand Tracking
  private HandTrackingEngine engine;
  private ProcessPacketController packetController;
  private HandTracker handTracker;

  // Speech
  SpeechEngine speechEngine;
  SpeechParser speechParser;

  // Actions
  SelectionManager selectionManager;

  public void setup() {
    // Init app state
    initScreenSize();
    // size(GameConstants.BACKGROUND_WIDTH, GameConstants.BACKGROUND_HEIGHT);
    size(appWidth, appHeight);
    frameRate(30);

    // Rendering modes
    imageMode(CENTER);
    ellipseMode(CENTER);

    // Init app utils
    PImagePool.setParent(this);

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
    speechParser.setSelectionManager(selectionManager);
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
    updateHandTracking(elapsedTime);
  }

  private void updateHandTracking(long elapsedTime) {
    if (!engine.isDone()) {
      try {
        ProcessPacket packet = engine.step();
        packetController.show(packet);
      } catch (GeneralException e) {
        LOGGER.severe(e.getMessage());
        engine.release();
        System.exit(-1);
      }
    }

    handTracker.update(elapsedTime);
  }

  public void render(PApplet p) {
    // Render all deck objects
    deck.render(this);
    flyingObjectManager.render(this);

    // Render handtracking
    handTracker.render(p);
  }

  public void keyPressed() {
    switch (key) {
    case 'S':
    case 's':
      selectionManager.selectWithAction(Commands.MOVE);
      break;
    case 'E':
    case 'e':
      selectionManager.executeActionWithTarget(Commands.TO);
      break;
    }
  }

  /****************************************************************************/
  /* Additional Methods******************************************************** */
  /****************************************************************************/

  private void initScreenSize() {
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

    // The scaling ratio is used to resize all image sprites accordingly.
    scaleRatio = appWidth / origWidth;
  }

  /**
   * Initialize the deck parameters and flight objects.
   */
  private void initDeckObjects() {
    deck = new Deck(this);
    flyingObjectManager = new FlyingObjectManager();

    // For now, we'll just place some random objects on the deck.
    PVector pos = new PVector(width / 2, height / 2);
    FlyingObject flyingObject = new FlyingObject("fmac", pos, 0);
    flyingObjectManager.addFlyingObject(flyingObject);

    pos = new PVector(width / 3, height / 2);
    flyingObject = new FlyingObject("fmac", pos, 0);
    flyingObjectManager.addFlyingObject(flyingObject);

    pos = new PVector(width / 3 * 2, height / 2);
    flyingObject = new FlyingObject("fmac", pos, 0);
    flyingObjectManager.addFlyingObject(flyingObject);
  }

  /**
   * Starts the hand tracker and debug display.
   */
  private void initHandTracking() {
    try {
      engine = new HandTrackingEngine(OPENNI_CONFIG_FILE, CALIB_FILE);
      packetController = new ProcessPacketController(engine.depthWidth(),
          engine.depthHeight(), null);
    } catch (GeneralException ge) {
      LOGGER.severe(ge.getMessage());
      System.exit(-1);
    }

    // Configure depth debug views
    packetController.showDepthImage(false);
    packetController.show3DView(false);

    // HandTracker
    Dimension tabletopRes = new Dimension(GameConstants.TABLETOP_WIDTH,
        GameConstants.TABLETOP_HEIGHT);
    handTracker = new HandTracker(this, tabletopRes);

    engine.addHandEventListener(handTracker);

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
    
    speechEngine.startRecognition();
  }

  /****************************************************************************/
  /* Accessors *************************************************************** */
  /****************************************************************************/
  public float scaleRatio() {
    return this.scaleRatio;
  }
}
