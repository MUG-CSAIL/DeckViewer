package edu.mit.kacquah.deckviewer.gui;

import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * A static view that renders with a dimmed background and text. Use to
 * indicating loading or setup to the user.
 * 
 * @author kojo
 * 
 */
public class StaticTextView implements PAppletRenderObject {
  // App Utils
  private PApplet parent;
  
  // Status
  private PFont font;
  private boolean isActive;
  private String text;

  public StaticTextView(PApplet p) {
    this.parent = p;
    this.isActive = false;
    this.text = "";
    this.font = p.createFont("Arial", 128);
    p.textAlign(p.CENTER, p.CENTER);
  }

  public void setText(String newText) {
    this.text = newText;
  }
  
  public boolean isActive() {
    return this.isActive;
  }
  
  public void setIsActive(boolean state) {
    this.isActive = state;
  }

  @Override
  public void update(long elapsedTime) {
    // TODO Auto-generated method stub
  }

  @Override
  public void render(PGraphics p) {
    // Only render when active
    if (!isActive) {
      return;
    }   
    p.textAlign(p.CENTER, p.CENTER);
    p.pushStyle();
    p.rectMode(p.CORNER);
    p.fill(0, 128);
    p.rect(0,0,p.width, p.height);
    p.popStyle();
    p.textFont(font);
    p.text(this.text, p.width/2, p.height/2);
  }

}
