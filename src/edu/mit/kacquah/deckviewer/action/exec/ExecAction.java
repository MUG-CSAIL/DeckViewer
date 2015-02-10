package edu.mit.kacquah.deckviewer.action.exec;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Interface for objects placed on execution stack.
 * @author kojo
 *
 */
public interface ExecAction{
  public void update(long elapsedTime);
  public void render(PApplet p);
  public boolean isDone();
}
