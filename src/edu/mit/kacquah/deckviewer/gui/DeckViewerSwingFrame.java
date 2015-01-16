package edu.mit.kacquah.deckviewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.game.GameConstants;

/**
 * Frame that contains the DeckViewer app.
 * @author kojo
 *
 */
public class DeckViewerSwingFrame extends JFrame {
  
  private DeckViewerPApplet deckViewer;
  private StatusBar sb;
  
  public DeckViewerSwingFrame(String name) {
    super(name);
    this.setResizable(false); 
    this.setLayout(new BorderLayout());
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    deckViewer = new DeckViewerPApplet();
    deckViewer.setParentFrameContainer(this);
    
    // Calculate and size the deckvewer applet
    deckViewer.initScreenSize();
    deckViewer.resize(deckViewer.appWidth, deckViewer.appHeight);
    deckViewer.setPreferredSize(new Dimension(deckViewer.appWidth, deckViewer.appHeight));
    deckViewer.setMinimumSize(new Dimension(deckViewer.appWidth, deckViewer.appHeight));
    
    // Initialize the applet after the frame is created.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        deckViewer.init();
      }
    });
    
    // Add the deckviewer to the frame
    this.getContentPane().add(deckViewer, BorderLayout.CENTER);

    initializeStatusBar();
  }

  public void initializeStatusBar() {
    sb = new StatusBar(deckViewer.appWidth, GameConstants.STATUS_BAR_HEIGHT);
    sb.setParentJFrame(this);
    deckViewer.setStatusBar(sb);
  }
  
  public final StatusBar getStatusBar() {
    return sb;
  }
  
  /**
   * Show the deckview application.
   */
  public void showUI() {
    pack();
    setVisible(true);
  }


}
