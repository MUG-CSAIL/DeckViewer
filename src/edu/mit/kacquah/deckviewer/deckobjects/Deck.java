package edu.mit.kacquah.deckviewer.deckobjects;

import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GlobalSettings;
import edu.mit.kacquah.deckviewer.game.GlobalSettings.BackgroundRatio;
import edu.mit.kacquah.deckviewer.utils.*;
import processing.core.PImage;
import processing.core.PApplet;

public class Deck implements PAppletRenderObject {
  // Parent PApplet that owns the deck.
  private DeckViewerPApplet parent;

  // Deck background image.
  private PImage deckImage;

  // Deck regions of interest.
  // TODO define regions of interest on deck

  // Deck objects and managers
  private FlyingObjectManager flyingObjectManager;

  public Deck(PApplet p) {
    this.parent = (DeckViewerPApplet)p;
    String fileName;
    if (GlobalSettings.backgroundRatio == BackgroundRatio.NORMAL) {
      fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR,
          "cvn_full_wake.png");
    } else {
      fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR,
          "cvn_full_wake_16-9.png");
    }
    deckImage = parent.loadImage(fileName);

    // Resize the deck image using scaling ratio
    float origWidth = deckImage.width;
    float origHeight = deckImage.height;
    deckImage.resize((int) (origWidth * parent.scaleRatio()), (int) (origHeight
        * parent.scaleRatio()));
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
