package edu.mit.kacquah.deckviewer.game;

import edu.mit.kacquah.deckviewer.gui.DeckViewerSwingFrame;
import processing.core.PApplet;

/**
 * Initiate and start the DeckViewer.
 * @author kojo
 *
 */
public class DeckViewerMain {
  
  /**
   * Specifies whether to launch app in swing frame container.
   */
  public static final boolean USE_SWING_FRAME = true;
  
  /**
   * Create swing frame for app
   */
  private static void createAndShowGUI() {
    // Create and set up the window.
    DeckViewerSwingFrame frame = new DeckViewerSwingFrame("DeckViewer");
    frame.showUI();

  }
  
  /**
   * Main kicks off the PApplet for our game.
   * @param args
   */
  public static void main(String[] args) {
    if (USE_SWING_FRAME) {
      // Schedule a job for the event-dispatching thread:
      // creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          createAndShowGUI();
        }
      });
    } else {
      String[] newArgs = new String[] { "edu.mit.kacquah.deckviewer.game.DeckViewerPApplet"};
      PApplet.main(newArgs);
    }
  }


}
