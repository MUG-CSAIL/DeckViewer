package edu.mit.kacquah.deckviewer.game;

import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.OpenNI.GeneralException;

import edu.mit.kacquah.deckviewer.deckobjects.*;
import edu.mit.kacquah.deckviewer.gesture.HandTracker;
import edu.mit.kacquah.deckviewer.utils.*;
import edu.mit.yingyin.tabletop.controllers.ProcessPacketController;
import edu.mit.yingyin.tabletop.models.HandTrackingEngine;
import edu.mit.yingyin.tabletop.models.ProcessPacket;
import edu.mit.yingyin.util.SystemUtil;
import processing.core.*;

/**
 * Main PApplet for running the DeckViewerApp. Extended from processing core 
 * Papplet for main run loop and drawing functions. 
 * @author kojo
 *
 */
public class DeckViewerPApplet extends PApplet{
  // App utils
  private static Logger LOGGER = Logger.getLogger(DeckViewerPApplet.class.getName());
  
  // Directory constants
  public final String WORKING_DIR = System.getProperty("user.dir");
  public static final String MAIN_DIR = ".";
  public static final String RESOURCE_DIR = FileUtil.join(MAIN_DIR, "resources");
  public static final String CALIB_DIR = FileUtil.join("data", "calibration");
  public static final String OPENNI_CONFIG_FILE = FileUtil.join(
      MAIN_DIR, "config", "config.xml");
  public static final String CALIB_FILE = FileUtil.join(MAIN_DIR, 
      CALIB_DIR, "calibration.txt");
  
  /**
   * App dimensions used to size the application window.
   */
  private int appWidth;
  private int appHeight;
  private float scaling;

  // Deck Objects and Managers
  private Deck deck;
  private FlyingObjectManager flyingObjectManager;
  
  // Hand Tracking
  private HandTrackingEngine engine;
  private ProcessPacketController packetController;
  private HandTracker handTracker;

  public void setup() {
    // Init app state
    fitWindowToScreen();
//    size(GameConstants.BACKGROUND_WIDTH, GameConstants.BACKGROUND_HEIGHT);
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
  }

  /****************************************************************************/
  /*Processing Methods*********************************************************/
  /****************************************************************************/
  
  public void draw() {
    // Update the app.
    long elapsedTime = System.currentTimeMillis() % 1000;
    update(elapsedTime);
    // render the app.
    render(this);
  }
  
  public void update(long elapsedTime) {
    Point point = new Point(mouseX, mouseY);
    SwingUtilities.convertPointToScreen(point, this);
    //LOGGER.info(""+point);
    
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
  
  /****************************************************************************/
  /*Additional Methods*********************************************************/
  /****************************************************************************/
  
  /**
   * Pulls the width and height from the args list and sets them as appWidth and
   * appHeight.
   */
  private void fitWindowToScreen() {
    // Dimensions for curren virtual screen (all monitors combined).
    Dimension screen = SystemUtil.getVirtualScreenBounds().getSize();
    float screenRatio = (float)screen.height / (float)screen.width;
    
    float desiredRatio = (float)GameConstants.BACKGROUND_HEIGHT / (float)GameConstants.BACKGROUND_WIDTH;
    if (screenRatio > desiredRatio) { 
      // Size based on maximizing width.
      appWidth = screen.width;
      appHeight = (int) ((float)appWidth * desiredRatio);  
    } else {
      // Size based on maximizing height.
      appHeight = screen.height;
      appWidth = (int) ((float)appHeight / desiredRatio);
    }
  }
  
  /**
   * Initialize the deck parameters and flight objects. 
   */
  private void initDeckObjects() {
    deck = new Deck(this); 
    
    flyingObjectManager = new FlyingObjectManager();

    PVector pos = new PVector(width/2, height/2);
    FlyingObject flyingObject = new FlyingObject("fmac", pos, 0);
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
}
