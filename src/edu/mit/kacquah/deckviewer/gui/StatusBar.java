package edu.mit.kacquah.deckviewer.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

/**
 * Statsu bar with left justified, center justified, and right justified text.
 * @author kojo
 *
 */
public class StatusBar {
  private int width, height;

  private JFrame parent;

  private JLabel leftJustified, centerJustified, rightJustified;

  private JLayeredPane layeredPane;
  
  public StatusBar(int width, int height) {
    this.width = width;
    this.height = height;
    initLayeredLabels();
  }
  
  private void initLayeredLabels() {
    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(width, height)); 
    
    leftJustified = new JLabel();
    leftJustified.setPreferredSize(new Dimension(width, height));
    leftJustified.setSize(new Dimension(width, height));

    centerJustified = new JLabel();
    centerJustified.setPreferredSize(new Dimension(width, height));
    centerJustified.setSize(new Dimension(width, height));
    centerJustified.setHorizontalAlignment(SwingConstants.CENTER);

    rightJustified = new JLabel();
    rightJustified.setPreferredSize(new Dimension(width, height));
    rightJustified.setSize(new Dimension(width, height));
    rightJustified.setHorizontalAlignment(SwingConstants.RIGHT);
    
    layeredPane.add(leftJustified, 1);
    layeredPane.add(centerJustified, 2);
    layeredPane.add(rightJustified, 3);

    setMessage("Ready");
  }
  
  public void setParentJFrame(JFrame parent) {
    this.parent = parent;
    parent.add(layeredPane, java.awt.BorderLayout.SOUTH);
  }
  
  public void setMessageLeft(String message) {
    leftJustified.setText(" " + message);
  }
 
  public void setMessageCenter(String message) {
    centerJustified.setText(" " + message);
  }
  
  public void setMessageRight(String message) {
    rightJustified.setText(" " + message);
  }
  
  public void setMessage(String message) {
    setMessageLeft(message);
  }
  
  public void clearMessageLeft() {
    setMessageLeft("");
  }
  
  public void clearMessageCenter() {
    setMessageCenter("");
  }
  
  public void clearMessageRight() {
    setMessageRight("");
  }
  
  public void clearMessage() {
    setMessageLeft("");
  }
  
  
}

