package edu.mit.kacquah.deckviewer.utils;

import javax.vecmath.Point2f;
import java.util.LinkedList;

/**
 * Tool for filtering an incoming stream of javax.vecmath.Point2f over a
 * specified sliding window. Can choose filtering method.
 * 
 * @author kojo
 * 
 */
public class FilteredPoints {
  /**
   * Methods for filtering points.
   * 
   * @author kojo
   * 
   */
  public enum FilteredPointMethod {
    MEAN, MEDIAN
  };

  LinkedList<Point2f[]> pointsHistory;
  int maxHistory, numTrackedPoints;
  FilteredPointMethod method;

  /**
   * When true, the filter will ignore the numTrackedPoints set
   */
  boolean autoResizeNumTrackedPoints;

  public FilteredPoints(int historyLength) {
    this.maxHistory = historyLength;
    this.numTrackedPoints = numTrackedPoints;
    method = FilteredPointMethod.MEAN;
    resetHistory();
  }

  public void setMethod(FilteredPointMethod newMethod) {
    this.method = newMethod;
  }

  /**
   * Configure auto resize for number of points tracked.
   * 
   * @param autoResize
   */
  public void setAutoResize(boolean autoResize) {
    this.autoResizeNumTrackedPoints = autoResize;
  }

  /**
   * Update the points history. Note that the number of new points must match
   * the number of points tracked.
   * 
   * @param newPoints
   */
  public void updatePoints(Point2f[] newPoints) {
    if (autoResizeNumTrackedPoints) {
      // Reset to new points width if input list is different size than tracked.
      if (newPoints.length != numTrackedPoints) {
        this.resetHistory(newPoints.length);
      }
    } else {
      // Without auto resize, input list must match number of points being
      // tracked.
      assert (newPoints.length == numTrackedPoints);
    }

    // Add to end of list
    pointsHistory.addLast(newPoints);

    if (pointsHistory.size() > maxHistory) {
      // Remove Point2fs from front
      pointsHistory.removeFirst();
    }
  }

  /**
   * Removes all points from history, but keeps number of points tracked the
   * same.
   */
  public void resetHistory() {
    pointsHistory = new LinkedList<Point2f[]>();
  }

  /**
   * Removes all points from history and changes number of points tracked..
   * 
   * @param numTrackedPoints
   */
  public void resetHistory(int numTrackedPoints) {
    this.numTrackedPoints = numTrackedPoints;
    resetHistory();
  }

  /**
   * Current number of points this filter is tracking.
   * 
   * @return
   */
  public int numPointsTracked() {
    return this.numTrackedPoints;
  }

  public Point2f[] getFilteredPoints() {
    switch (method) {
    case MEAN:
      return getFilteredPointsMean();
    case MEDIAN:
      // TODO: implement this.
      break;
    }
    return null;
  }

  public Point2f[] getFilteredPointsMean() {
    Point2f result[] = new Point2f[numTrackedPoints];
    for (int i = 0; i < result.length; ++i) {
      result[i] = new Point2f(0,0);
    }
    float denom = pointsHistory.size();
    for (Point2f[] historyStep : pointsHistory) {
      for (int i = 0; i < historyStep.length; ++i) {
        Point2f point = new Point2f(historyStep[i]);
        point.scale(1.0f / denom);
        result[i].add(point);
      }
    }
    return result;
  }

}
