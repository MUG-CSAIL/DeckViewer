package edu.mit.kacquah.deckviewer.deckobjects;

import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.utils.*;
import processing.core.PImage;
import processing.core.PApplet;


public class Deck implements PAppletRenderObject{
  // Parent PApplet that owns the deck.
  private PApplet parent;
  
  // Deck background image.
  private PImage deckImage;
  
  // Deck regions of interest.
  // TODO define regions of interest on deck
  
  // Deck objects and managers
  private FlyingObjectManager flyingObjectManager;


  public Deck(PApplet p) {
    this.parent = p;
    String fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR, "cvn_full_wake.png");
    deckImage = parent.loadImage(fileName);
  }
  
  @Override 
  public void update(long elapsedTime) {
    
  }
  
  @Override
  public void render(PApplet p) {
    // Set image mode to corners
    p.pushStyle();
    p.imageMode(p.CORNERS);
    p.image(deckImage, 0, 0);
    p.popStyle();
  }
  
  
}
