package edu.mit.kacquah.deckviewer.gui.shape;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.Rectangle;
import java.util.LinkedList;

import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.utils.Geometry;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import processing.core.PApplet;

/**
 * Represents a path on the deck (a series of lines with width).
 * @author kojo
 *
 */
public class Path implements PAppletRenderObject{
  private LinkedList<Point> points;
  private LinkedList<Line2D.Float> pathLines;
  /**
   * Thinkness of line.
   */
  private float width;
  
  private int color;
  
  public Path(float width, int color) {
    this.points = new LinkedList<Point>();
    this.pathLines = new LinkedList<Line2D.Float>();
    this.width = Deck.getInstance().scaleRatio * width;
    this.color = color;
  }
  
  /**
   * Add point to line.
   * @param p
   */
  public void addPoint(Point p) {
    points.add(p);
    if (points.size() > 1) {
      Point start = points.get(points.size() - 2);
      Point end = points.get(points.size() - 1);
      float angle = Geometry.angle(start, end);
      angle = (angle + 90) % 360;
      Line2D.Float left = new Line2D.Float(start, end);
      Line2D.Float right = new Line2D.Float(start, end);
      left = Geometry.translateLine(left, angle, -width/2);
      right = Geometry.translateLine(right, angle, width/2);
      pathLines.add(left);
      pathLines.add(right);
    }
  }
  
  /**
   * Returns true if contactable object intersects this path anywhere.
   */
  public boolean intersects(Contactable contactable) {
    for (Line2D l: pathLines) {
      if (contactable.bounds().intersectsLine(l)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void render(PApplet p) {
    p.pushMatrix();
    p.pushStyle();
    p.stroke(color);
    p.strokeWeight(GlobalSettings.STROKE_WEIGHT);
    for (Line2D.Float l: pathLines) {
      p.line(l.x1, l.y1, l.x2, l.y2);
    }
    p.popStyle();
    p.popMatrix();
  }

}
