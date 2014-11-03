package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Point;

import processing.core.*;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import edu.mit.kacquah.deckviewer.utils.PImagePool;

public class FlyingObject implements PAppletRenderObject {
  private Sprite planeSprite;
  private String name;
  
  public FlyingObject(String name, PVector pos, float rotation){
    this.name = name;
    // Load image from image pool
    PImage spriteImage = PImagePool.getImages(name)[0];
    this.planeSprite = new Sprite(spriteImage, pos, rotation);
  }
  
  /**
   * Returns true if the flying object sprite intersects a given point
   * @param p
   * @return
   */
  public boolean intersectsPoint(Point p) {
    return planeSprite.intersectsPoint(p);
  }
  
  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(PApplet p) {
    // Render plane image.
    planeSprite.render(p);
  }

}
