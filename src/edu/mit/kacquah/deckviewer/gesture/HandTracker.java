package edu.mit.kacquah.deckviewer.gesture;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.vecmath.Point2f;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.FilteredPoints;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import edu.mit.yingyin.tabletop.models.HandTracker.DiecticEvent;
import edu.mit.yingyin.tabletop.models.HandTracker.ManipulativeEvent;
import edu.mit.yingyin.tabletop.models.HandTracker.ManipulativeEvent.FingerEventType;
import edu.mit.yingyin.tabletop.models.HandTrackingEngine.IHandEventListener;
import edu.mit.yingyin.util.SystemUtil;

/**
 * Tracks the state of user's hands for the app.
 * 
 * @author kojo
 * 
 */
public class HandTracker implements IHandEventListener, PAppletRenderObject {
  PApplet parent;

  private final Dimension tabletopRes;
  private List<ManipulativeEvent> feList;

  /**
   * When true, dots for fingers are rendered on app.
   */
  boolean showFingers;

  /**
   * Filter points over a window for smoother finger tracking.
   */
  FilteredPoints filteredPoints;
  boolean useFilteredPoints;
  
  /**
   * Length of history window to filter points
   */
  private int historyLength;

  // Drawing constants
  private int circleRadius;

  public HandTracker(PApplet p, Dimension screenResolution) {
    this.parent = p;
    this.tabletopRes = screenResolution;
    showFingers = true;
    
    historyLength = GlobalSettings.FILTER_HISTORY_LENGTH;
    circleRadius = GlobalSettings.FINGER_CIRCLE_RADIUS;

    filteredPoints = new FilteredPoints(historyLength);
    filteredPoints.resetHistory();
    // Enabling auto resize allows us to always track a new set of fingers when
    // the number of detected fingers changes.
    filteredPoints.setAutoResize(true);
    useFilteredPoints = true;
  }

  public void toggleShowFingers(boolean newState) {
    this.showFingers = newState;
  }
  
  /**
   * Scales points from tabletop coordinates to current desktop coordinates. If
   * app is running on the tabletop, then this method has no effect.
   * 
   * @param p
   * @return
   */
  private Point2f scale(Point2f p) {
    Dimension d = SystemUtil.getVirtualScreenBounds().getSize();
    return new Point2f(p.x * d.width / tabletopRes.width, p.y * d.height
        / tabletopRes.height);
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(PApplet p) {
    p.pushStyle();
    p.noStroke();
    if (feList != null) {
      if (useFilteredPoints) {
        p.fill(ColorUtil.RED);
        Point2f[] points = filteredPoints.getFilteredPoints();
        for (Point2f point : points) {
          Point pointInImageCoord = new Point((int) point.x, (int) point.y);
          //SwingUtilities.convertPointFromScreen(pointInImageCoord, p);
          p.ellipse(pointInImageCoord.x, pointInImageCoord.y, circleRadius * 2,
              circleRadius * 2);
        }
      } else {
        for (ManipulativeEvent fe : feList) {
          FingerEventType type = fe.type;
          if (type == FingerEventType.PRESSED) {
            p.fill(ColorUtil.RED);
          } else {
            p.fill(ColorUtil.GREEN);
          }
          Point2f point = scale(fe.posDisplay);
          Point pointInImageCoord = new Point((int) point.x, (int) point.y);
          SwingUtilities.convertPointFromScreen(pointInImageCoord, p);
          p.ellipse(pointInImageCoord.x, pointInImageCoord.y, circleRadius * 2,
              circleRadius * 2);
        }
      }
    }
    p.popStyle();
  }

  @Override
  public void fingerPressed(List<ManipulativeEvent> feList) {
    this.feList = feList;
    
    // Extract and scale points for filtering
    Point2f newPoints[] = new Point2f[feList.size()];
    for (int i = 0; i < feList.size(); ++i) {
      Point2f point = feList.get(i).posDisplay;
      Point pointInImageCoord = new Point((int) point.x, (int) point.y);
      SwingUtilities.convertPointFromScreen(pointInImageCoord, parent);
      point.x = pointInImageCoord.x;
      point.y = pointInImageCoord.y;
      newPoints[i] = point;
    }
    filteredPoints.updatePoints(newPoints);
  }

  @Override
  public void fingerPointed(DiecticEvent de) {
    // TODO Auto-generated method stub

  }
  
  /**
   * List of current filtered finger pointed maintained by hand tracker.
   * @return
   */
  public Point2f[]  getFilteredPoints() {
    if (GlobalSettings.useMousePoint) {
      Point2f mouse[] = {new Point2f(parent.mouseX, parent.mouseY) };
      return mouse;
    }
    return filteredPoints.getFilteredPoints();
  }
}
