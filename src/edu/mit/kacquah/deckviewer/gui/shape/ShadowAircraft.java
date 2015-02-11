package edu.mit.kacquah.deckviewer.gui.shape;

import java.awt.Point;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.deckobjects.Sprite;
import edu.mit.kacquah.deckviewer.image.ColorHighlightFilter;
import edu.mit.kacquah.deckviewer.utils.ColorUtil;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Renders shadow of an aircraft sprite on screen.
 * @author kojo
 *
 */
public class ShadowAircraft implements PAppletRenderObject {
  
  private Sprite planeSprite;
  private Point center;
  private float rotation;
  
  private ColorHighlightFilter imageFilter;
  
  public ShadowAircraft(FlyingObject aircraft, Point center, float rotation) {
    this.planeSprite = aircraft.aircraftSprite();
    this.center = center;
    this.rotation = rotation;
    this.imageFilter = new ColorHighlightFilter(ColorUtil.BLACK);
  }

  @Override
  public void update(long elapsedTime) {
    imageFilter.updateFilter(elapsedTime);    
  }

  @Override
  public void render(PApplet p) {
    p.pushStyle();
    p.pushMatrix();
    p.translate(center.x, center.y);
    p.rotate((float)Math.toRadians(rotation));
    // Apply filters
    imageFilter.applyFilter(p);
    // Draw image 
    p.image(planeSprite.getSelectedImage(), 0, 0);
    p.popMatrix();
    p.popStyle();    
  }

}
