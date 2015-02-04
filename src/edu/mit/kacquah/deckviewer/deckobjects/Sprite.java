package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.vecmath.Point2f;

import edu.mit.kacquah.deckviewer.image.DynamicImageFilter;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import processing.core.*;

/**
 * An image object along with position and rotation that is rendered on screen.
 * Also handles image filteres.
 * 
 * @author kojo
 * 
 */
public class Sprite implements PAppletRenderObject {
  public enum Direction {
    UP(270),
    DOWN(90),
    LEFT(180),
    RIGHT(0);
    
    public final int degrees;
    
    Direction (int degrees) {
      this.degrees = degrees;
    }
  }
  
  private PImage spriteImages[];
  private PVector pos;
  private float rotation;
  private int selectedSpriteImage;
  private LinkedList<DynamicImageFilter> imageFilters;
  
  private static final int DEFAULT_SPRITE_IMAGE = 1;
  
  private float scaleRatio;

  public Sprite(PImage image[], PVector position, float rotation) {
    this.spriteImages = image;
    this.selectedSpriteImage = DEFAULT_SPRITE_IMAGE;
    this.pos = position;
    this.rotation = rotation;
    resetImageFilters();
  }
  
  public void addImageFilter(DynamicImageFilter filter) {
    imageFilters.add(filter);
  }
  
  public void resetImageFilters() {
    imageFilters = new LinkedList<DynamicImageFilter>(); 
  }

  public void setPosition(float x, float y) {
    this.pos.x = x;
    this.pos.y = y;
  }

  public Point2f getPosition() {
    return new Point2f(pos.x, pos.y);
  }

  public void setRotation(float newRot) {
    this.rotation = newRot % 360;
  }

  public float getRotation() {
    return rotation;
  }

  public void setSelectedSpriteImage(int newSelection) {
    if (newSelection < spriteImages.length && newSelection >= 0) {
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
    return new Rectangle((int) pos.x - width / 2, (int) pos.y - height / 2,
        width, height);
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
    // Update the image filters.
    for(DynamicImageFilter f: imageFilters) {
      f.updateFilter(elapsedTime);
    }
  }

  @Override
  public void render(PApplet p) {
    p.pushStyle();
    p.pushMatrix();
    p.translate(pos.x, pos.y);
    p.rotate((float)Math.toRadians(rotation));
    // Apply filters
    for (DynamicImageFilter f : imageFilters) {
      f.applyFilter(p);
    }
    // Draw image 
    p.image(spriteImages[selectedSpriteImage], 0, 0);
    p.popMatrix();
    p.popStyle();
  }

}
