package edu.mit.kacquah.deckviewer.speech.synthesis;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;

import processing.core.PApplet;
import edu.mit.kacquah.deckviewer.utils.PAppletRenderObject;

/**
 * Graph for implementing spoken conversations using speech nodes.
 * @author kojo
 *
 */
public abstract class SpeechGraph implements PAppletRenderObject, SpeakableListener {
  /**
   * Yeild states for what to do when synthesizing or waiting for speech responses.
   * @author kojo
   *
   */
  public enum YieldStatus{
    NO_STATUS,
    YIELD_NEXT, // Load next node in graph
    YIELD_NEXT_UPDATE, // Load next node in graph on next update
    YIELD_WAIT, // Wait until current synthesis ends
    YIELD_AFFIRMATIVE, // Wait until current synthesis ends and receive yes/no
    YIELD_DONE; // This speech node is done processing.
  }
  
  protected enum Affirmative {
    YES, NO, NO_STATUS;
  }
  
  protected enum SpeechWaitStatus {
    DO_PRE_SPEECH,
    WAITING_SPEECH,
    WAITING_SPEECH_THEN_AFFIRMATIVE,
    WAITING_AFFIRMATIVE,
    DO_POST_SPEECH;
  }
  
  // App utils
  private static Logger LOGGER = Logger.getLogger(SpeechGraph.class.getName());
  
  // Internal state
  protected boolean isDone;
  protected YieldStatus yeildStatus;
  protected Affirmative lastAffirmative;
  protected SpeechWaitStatus speechWaitStatus; 
  
  protected SpeechNode currentNode, nextNode;
  
  // Synthesizer
  private SpeechSynthesizer speechSynthesizer;
    
  public SpeechGraph() {
    // Init state
    this.isDone = false;
    this.yeildStatus = YieldStatus.NO_STATUS;
    this.lastAffirmative = Affirmative.NO_STATUS;
    this.speechWaitStatus = SpeechWaitStatus.DO_PRE_SPEECH;
    // Set the root node
    this.currentNode = rootNode();
    // Synthesizer
    this.speechSynthesizer = SpeechSynthesizer.getInstance();
  }
  
  /**
   * Set the next speech node.
   * @param speechNode
   */
  protected void setNextSpeechNode(SpeechNode speechNode) {
    this.nextNode = speechNode;
  }
  
  /**
   * Moves on to the next speech node.
   */
  protected void advanceNode() {
    if (nextNode == null) {
      LOGGER.severe("Cannot advance node without next node.");
    }
    currentNode = nextNode;
    nextNode = null;
  }
  
  /**
   * Returns true if this speech graph has finished processing.
   * @return
   */
  public boolean isDone() {
    return this.isDone;
  }
  
  /**
   * Set last affirmative status. Will not process if currently not waiting for
   * affirmative.
   * 
   * @param yes
   */
  public boolean setLastAffirmative(boolean yes) {
    if (speechWaitStatus != SpeechWaitStatus.WAITING_AFFIRMATIVE) {
      LOGGER.severe("Cannot process affirmative in current state: "
          + speechWaitStatus);
      return false;
    }
    if (yes) {
      this.lastAffirmative = Affirmative.YES;
    } else {
      this.lastAffirmative = Affirmative.NO;
    }
    // Do post speech now.
    speechWaitStatus = SpeechWaitStatus.DO_POST_SPEECH;
    return true;
  }  
  
  /**
   * Returns and resets the lastAffirmative status.
   * @return
   */
  public Affirmative getLastAffirmative() {
    Affirmative ans = this.lastAffirmative;
    this.lastAffirmative = Affirmative.NO_STATUS;
    return ans;
  }
  
  /**
   * Starts speech through the synthesizer.
   * @param text
   */
  protected void startSpeech(String text) {
    this.speechSynthesizer.speakText(text, this);
  }
  
  /**
   * Called when last queued synthesis ends. Updates speech wait status.
   * @param e
   */
  protected void notifySeechEnded() {
    if (speechWaitStatus == SpeechWaitStatus.WAITING_SPEECH) {
      speechWaitStatus = SpeechWaitStatus.DO_POST_SPEECH;
    }
    if (speechWaitStatus == SpeechWaitStatus.WAITING_SPEECH_THEN_AFFIRMATIVE) {
      speechWaitStatus = SpeechWaitStatus.WAITING_AFFIRMATIVE;
    }
  }
  
  /**
   * Set the next yield status and updates appropriate speech wait status.
   * @param s
   */
  public void setYieldStatus(YieldStatus s) {
    this.yeildStatus = s;
    // Are we done?
    if (s == YieldStatus.YIELD_DONE) {
      this.isDone = true;
    } else if (s == YieldStatus.YIELD_WAIT) {
      this.speechWaitStatus = SpeechWaitStatus.WAITING_SPEECH;
    } else if (s == YieldStatus.YIELD_AFFIRMATIVE) {
      this.speechWaitStatus = SpeechWaitStatus.WAITING_SPEECH_THEN_AFFIRMATIVE;
    } else if (s == YieldStatus.YIELD_NEXT || s == YieldStatus.YIELD_NEXT_UPDATE) {
      this.speechWaitStatus = SpeechWaitStatus.DO_PRE_SPEECH;
    }
  }
  
  /**
   * Returns and resets yield status.
   */
  protected YieldStatus checkYieldStatus() {
    YieldStatus ans = this.yeildStatus;
    this.yeildStatus = YieldStatus.NO_STATUS;
    return ans;
  }
  
  @Override
  public void update(long elapsedTime) {
    // Don't process if we're done
    if (isDone()) {
      LOGGER.severe("Cannot process a complete speech graph");
      return;
    }
    
    // Process current node preSpeech
    loop: while (true) {
      
      if (speechWaitStatus == SpeechWaitStatus.DO_PRE_SPEECH) {
        // Process before speech
        currentNode.preSpeechProcess();
      } else if (speechWaitStatus == SpeechWaitStatus.DO_POST_SPEECH) {
        // Process after speech
        currentNode.postSpeechProcess();
      } else {
        // We're still waiting for current speech to finish.
        return;
      }
      
      // Check yield status
      switch (checkYieldStatus()) {
      case YIELD_NEXT:
        // Continue while loop.
        advanceNode();
        continue loop;
      case YIELD_NEXT_UPDATE:
        // Loop again on next call to update.
        advanceNode();
        return;
      case YIELD_WAIT:
        // Waiting for speech to finish.
        return;
      case YIELD_AFFIRMATIVE:
        // Waiting for speech to finish.
        return;
      case YIELD_DONE:
        // Return now that speech processing is complete
        return;
      case NO_STATUS:
        // Something went wrong...
        LOGGER.severe("Yield status not set after preSpeechProcees for node"
            + currentNode.toString());
        return;
      }// end switch checkYieldStatus
      
    }// end while true
  }

  @Override
  public void render(PApplet p) {
    // TODO Auto-generated method stub
    
  }
  
  // ----------------------SpeakableListener Methods----------------------------
  @Override
  public void markerReached(SpeakableEvent arg0) {
  }

  @Override
  public void speakableCancelled(SpeakableEvent arg0) {
  }

  @Override
  public void speakableEnded(SpeakableEvent arg0) {
    notifySeechEnded();
  }

  @Override
  public void speakablePaused(SpeakableEvent arg0) {
  }

  @Override
  public void speakableResumed(SpeakableEvent arg0) {
  }

  @Override
  public void speakableStarted(SpeakableEvent arg0) {
  }

  @Override
  public void topOfQueue(SpeakableEvent arg0) {
  }

  @Override
  public void wordStarted(SpeakableEvent arg0) {
  }
  
  // ----------------------Abstract Methods------------------------------------
  abstract protected SpeechNode rootNode();
}
