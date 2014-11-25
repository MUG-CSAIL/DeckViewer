package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.LinkedList;

import processing.core.PApplet;

/**
 * Wrapper class for Java.awt.Polygon. Adds convenience methods for rendering
 * and dealing with floats.
 * 
 * @author kojo
 * 
 */
public class DeckPolygon implements PAppletRenderObject {
  Polygon polygon;
  private LinkedList<Point> points;
  Point pos;

  public DeckPolygon() {
    polygon = new Polygon();
    points = new LinkedList<Point>();
    pos = new Point();
  }

  public void addPoint(Point p) {
    addPoint(p.x, p.y);
  }

  public void addPoint(float x, float y) {
    addPoint((int)x, (int)y);
  }

  public void addPoint(int x, int y) {
    polygon.addPoint(x, y);
    points.add(new Point(x, y));
  }
  
  public boolean contains(Point p) {
    return polygon.contains(p);
  }
  
  public Rectangle getBounds() {
    return polygon.getBounds();
  }
  
  public void translate(int deltaX, int deltaY) {
    pos.x += deltaX;
    pos.y += deltaY;
    polygon.translate(deltaX, deltaY);
  }

  @Override
  public void update(long elapsedTime) {
    // Nothing to update
  }

  /**
   * Renders the outline of the deck.
   */
  @Override
  public void render(PApplet p) {
    p.pushMatrix();
    p.translate(pos.x, pos.y);
    p.beginShape();
    for (Point point : points) {
      p.vertex(point.x, point.y);
    }
    Point first = points.getFirst();
    p.vertex(first.x, first.y);
    p.endShape();
    p.popMatrix();
  }

}
