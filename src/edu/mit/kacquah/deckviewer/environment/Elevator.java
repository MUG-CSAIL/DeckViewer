package edu.mit.kacquah.deckviewer.environment;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Represents an elevator on a flight deck.
 * @author kojo
 *
 */
public class Elevator implements PAppletRenderObject {
  private Point centerPoint;
  private int elevatorNumber;
  
  pulbic Elevator(Point centerPoint, int number) {
    this.centerPoint = centerPoint;
    this.elevatorNumber = number;
  }
  
  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(PApplet p) {
    
  }

}
