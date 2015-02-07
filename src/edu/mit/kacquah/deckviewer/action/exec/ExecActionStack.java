package edu.mit.kacquah.deckviewer.action.exec;

import java.util.LinkedList;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Action stack processes stackable actions for objects on the deck.
 * @author kojo
 *
 */
public class ExecActionStack implements PAppletRenderObject {
  
  /**
   * Stack of actions being executed.
   */
  LinkedList<ExecAction> execStack;
  
  public ExecActionStack() {
    execStack = new LinkedList<ExecAction>();
  }
  
  /**
   * Adds a new action to the bottom of the stack. It gets process when the
   * current actions complete.
   * 
   * @param action
   */
  public void addNewAction(ExecAction action) {
   pushBottom(action); 
  }
  
  /**
   * Push to the bottom of the stack.
   * @param action
   */
  public void pushBottom(ExecAction action){
    execStack.addLast(action);
  }
  
  /**
   * Push to the top of the stack
   * @param action
   */
  public void pushTop(ExecAction action) {
    execStack.addFirst(action);
  }
  
  /**
   * Peek.
   * @return
   */
  private ExecAction peekStackTop() {
    return execStack.get(0);
  }
  
  /**
   * Pop
   * @return
   */
  private ExecAction popStackTop() {
    return execStack.remove(0);
  }
  
  /**
   * Removes all complete actions from top of the stack
   */
  private void cleanStack() {
    while (!execStack.isEmpty() && peekStackTop().isDone()) {
      popStackTop();
    }
  }

  @Override
  public void update(long elapsedTime) {
    // Clean the top of the stack for complete actions
    cleanStack();
    
    // Nothing to process with empty stack.
    if (execStack.isEmpty()) {
      return;
    }
    
    // Process top stack action
    peekStackTop().update(elapsedTime);
  }

  @Override
  public void render(PApplet p) {
    // Nothing to process with empty stack.
    if (execStack.isEmpty()) {
      return;
    }
    
    // Process top stack action
    peekStackTop().render(p);    
  }

}
