package edu.mit.kacquah.deckviewer.game;

import processing.core.PApplet;

/**
 * Initiate and start the DeckViewer.
 * @author kojo
 *
 */
public class DeckViewerMain {
  /**
   * Main kicks off the PApplet for our game.
   * @param args
   */
  public static void main(String[] args) {
    String[] newArgs = new String[] { "edu.mit.kacquah.deckviewer.game.DeckViewerPApplet"};
    PApplet.main(newArgs);
  }

}
