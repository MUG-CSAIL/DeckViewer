package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Represents a catapult launcher on a deck.
 * @author kojo
 *
 */
public class Catapult implements PAppletRenderObject{
  private Point startPoint, endPoint;
  private int catapultNumber;

  public Catapult(Point start, Point end, int number) {
    this.startPoint = start;
    this.endPoint = end;
    this.catapultNumber = number;
  }
  
  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  /**
   * Renders a line representing a catapult.
   */
  @Override
  public void render(PApplet p) {
    p.line(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
  }
}
