package edu.mit.kacquah.deckviewer.deckobjects;

import java.awt.Point;
import java.awt.Rectangle;

import javax.vecmath.Point2f;

import processing.core.*;
import edu.mit.kacquah.deckviewer.action.SelectionManager.SelectionStatus;
import edu.mit.kacquah.deckviewer.environment.Deck;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.image.ColorHighlightFilter;
import edu.mit.kacquah.deckviewer.image.DynamicImageFilter;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import edu.mit.kacquah.deckviewer.utils.PImagePool;

public class FlyingObject implements PAppletRenderObject {
  private Sprite planeSprite;
  public final AircraftType type;
  
  private FlyingObjectManager parentManager;
  private int UID;
  
  private PFont font;
  private int fontSize;
  
  private SelectionStatus selectionStatus;
  
  private ParkingSpot parkingSpot;
  
  public FlyingObject(AircraftType type, PVector pos, float rotation){
    this.type = type;
    // Load image from image pool
    PImage spriteImages[] = PImagePool.getImages(type.name, Deck.getInstance().scaleRatio);
    this.planeSprite = new Sprite(spriteImages, pos, rotation);
    this.parentManager = null;
    this.UID = -1;
  }
  
  public void addToFlyingObjectManager (FlyingObjectManager m) {
    this.parentManager = m;
    this.UID = m.getNextUID();
    this.font = m.font;
    this.fontSize = m.fontSize;
  }
  
  public void removeFromFlyingObjectManager(FlyingObjectManager m) {
    if (this.parentManager == m) {
      this.parentManager = null;
      this.UID = -1;
    }
  }
  
  public Sprite aircraftSprite() {
    return this.planeSprite;
  }
  
  public int getUID() {
    return this.UID;
  }
  
  public void addImageFilter(DynamicImageFilter filter) {
    planeSprite.addImageFilter(filter);
  }
  
  public void resetImageFilters() {
    planeSprite.resetImageFilters();
  }
  
  public void setParkingSpot(ParkingSpot s) {
    this.parkingSpot = s;
  }
  
  public ParkingSpot getParkingSpot() {
    return this.parkingSpot;
  }
  
  /**
   * Updates the seleciton status. Certain selection statuses will automatically
   * apply image filters to the sprite.
   * @param newStatus
   */
  public void setSelectionStatus(SelectionStatus newStatus) {
    this.resetImageFilters();
    this.selectionStatus = newStatus;
    switch (newStatus) {
    case SELECTED:
      this.addImageFilter(new ColorHighlightFilter(
          GlobalSettings.selectionStatusSelectedColor));
      break;
    case HOVERIRNG:
      this.addImageFilter(new ColorHighlightFilter(
          GlobalSettings.selectionStatusHoveringColor));
      break;
    case ERROR:
      this.addImageFilter(new ColorHighlightFilter(
          GlobalSettings.selectionStatusErrorColor));
      break;
    case NONE:
      // Do nothing
      break;
    }
  }
  
  public void setPosition(float x, float y) {
    planeSprite.setPosition(x, y);
  }
  
  public Point2f getPosition() {
    return planeSprite.getPosition();
  }
  
  public void setRotation(float newRot) {
    planeSprite.setRotation(newRot);
  }
  
  public float getRotation() {
    return planeSprite.getRotation();
  }
  
  public void move(float xOffset, float yOffset) {
    planeSprite.move(xOffset, yOffset);
  }
  
  public Rectangle getBounds() {
    return planeSprite.getBounds();
  }
  
  /**
   * Returns true if the flying object sprite intersects a given point
   * @param p
   * @return
   */
  public boolean intersectsPoint(Point p) {
    return planeSprite.intersectsPoint(p);
  }
  
  public boolean intersectsFlyingObject(FlyingObject other){
    return planeSprite.getBounds().intersects(other.getBounds());
  }
  
  /**
   * Opens the aircraft's wings.
   */
  public void wingsOpen() {
    planeSprite.setSelectedSpriteImage(0);
  }
  
  /**
   * Closes the aircraft's wings.
   */
  public void wingsClosed() {
    planeSprite.setSelectedSpriteImage(1);
  }
  
  
  
  @Override
  public void update(long elapsedTime) {
    planeSprite.update(elapsedTime);
  }

  @Override
  public void render(PApplet p) {
    // Render plane image.
    planeSprite.render(p);
    if (GlobalSettings.renderAircraftUIDs) {
      p.pushStyle();
      p.pushMatrix();
      // Text 
      Rectangle bounds = this.planeSprite.getBounds();
      p.translate(bounds.x + bounds.width*5/10, bounds.y + bounds.height*0/10);
      p.textFont(font);
      String text = this.type.name + "--" + this.UID;
//      String text = this.type.name;
      p.textSize(this.fontSize);
      p.fill(ColorUtil.WHITE);
      p.text(text, 0, 0);
      p.popMatrix();
      p.popStyle();
    }
  }

}
