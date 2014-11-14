package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.vecmath.Point2f;

import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import processing.core.*;

public class Sprite implements PAppletRenderObject {
  private PImage spriteImages[];
  private PVector pos;
  float rotation;
  int selectedSpriteImage;

  public Sprite(PImage image, PVector position) {
    this.spriteImages = new PImage[1];
    this.spriteImages[0] = image;
    this.selectedSpriteImage = 0;
    this.pos = position;
    this.rotation = 0;
  }

  public Sprite(PImage image, PVector position, float rotation) {
    this.spriteImages = new PImage[1];
    this.spriteImages[0] = image;
    this.selectedSpriteImage = 0;
    this.pos = position;
    this.rotation = rotation; 
  }

  public void setPosition(float x, float y) {
    this.pos.x = x;
    this.pos.y = y;
  }
  
  public Point2f getPosition() {
    return new Point2f(pos.x, pos.y);
  }

  public void setRotation(float newRot) {
    this.rotation = newRot;
  }
  
  public float getRotation() {
    return rotation;
  }
  
  public void setSelectedSpriteImage(int newSelection) {
    if (newSelection < spriteImages.length) {
      this.selectedSpriteImage = newSelection;
    }
  }

  public void move(float xOffset, float yOffset) {
    this.pos.x += xOffset;
    this.pos.y += yOffset;
  }

  public Rectangle getBounds() {
    int width = spriteImages[selectedSpriteImage].width;
    int height = spriteImages[selectedSpriteImage].height;
    // Sprites are drawn from position center, so subtract half of dimension.
    return new Rectangle((int) pos.x - width/2, (int) pos.y - height/2, width, height);
  }

  public Dimension getDimensions() {
    return new Dimension(spriteImages[selectedSpriteImage].width,
        spriteImages[selectedSpriteImage].height);
  }

  public boolean intersectsPoint(Point p) {
    return getBounds().contains(p);
  }
  
  @Override
  public void update(long elapsedTime) {
  }

  @Override
  public void render(PApplet p) {
    p.pushMatrix();
    p.translate(pos.x, pos.y);
    p.rotate(rotation);
    p.image(spriteImages[selectedSpriteImage], 0, 0);
    p.popMatrix();
  }

}
