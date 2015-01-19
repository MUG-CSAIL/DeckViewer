package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PGraphics;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.utils.DeckPolygon;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Represents an elevator on a flight deck.
 * @author kojo
 *
 */
public class Elevator extends DeckPolygon implements PAppletRenderObject {
  private Point centerPoint;
  private int elevatorNumber;
  
  private static final float circleRadius = 5.0f;
  
  public Elevator(Point centerPoint, int number) {
    super();
    this.centerPoint = centerPoint;
    this.elevatorNumber = number;
  }
  
  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  /**
   * Renders the outline and center of deck elevators;
   */
  public void render(PGraphics p) {       
    // Render Edges
    p.pushStyle();
    p.noFill();
    p.stroke(0, 255, 0);
    super.render(p);
    // Render Center
    p.noStroke();
    p.fill(p.color(255, 165, 0));
    p.ellipse(centerPoint.x, centerPoint.y, circleRadius, circleRadius);
    p.popStyle();
    
    
  }

}
