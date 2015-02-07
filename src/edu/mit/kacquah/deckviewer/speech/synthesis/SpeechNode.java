package edu.mit.kacquah.deckviewer.speech.synthesis;

import java.util.logging.Logger;

import edu.mit.kacquah.deckviewer.speech.synthesis.SpeechGraph.YieldStatus;

/**
 * Node for carrying out speech conversations.
 * @author kojo
 *
 */
public abstract class SpeechNode {
  // Parent
  private SpeechGraph parentGraph;
  
  protected static Logger LOGGER = Logger.getLogger(SpeechNode.class.getName());
  
  /**
   * Text for speech synthesis. Must be set befor yielding for speech.
   */
  protected String speechText;
  
  public SpeechNode(SpeechGraph graph) {
    this.parentGraph = graph;
  }
  
  protected void yieldNext() {
    this.parentGraph.setYieldStatus(YieldStatus.YIELD_NEXT);
  }
  
  protected void yieldNextUpdate() {
    this.parentGraph.setYieldStatus(YieldStatus.YIELD_NEXT_UPDATE);
  }
  
  protected void yieldWait() {
    this.parentGraph.setYieldStatus(YieldStatus.YIELD_WAIT);
    startSpeech();
  }
  
  protected void yieldAffirmative() {
    this.parentGraph.setYieldStatus(YieldStatus.YIELD_AFFIRMATIVE);
    startSpeech();
  }
  
  protected void yieldDone() {
    this.parentGraph.setYieldStatus(YieldStatus.YIELD_DONE);
  }
  
  /**
   * Starts speech synthesis for this node through the parent graph.
   */
  protected void startSpeech() {
    // If no text, don't speak
    if (this.speechText == null) {
      LOGGER.severe("Cannot speak without text");
      return;
    }
    this.parentGraph.startSpeech(this.speechText);
  }
  
  // ----------------------Abstract Methods------------------------------------

  /**
   * Called before speech is spoken. Must make yield call before returning.
   */
  abstract public void preSpeechProcess() ;
  /**
   * Called after speech is spoken.
   */
  abstract public void postSpeechProcess() ;
  
  
  
  
}
