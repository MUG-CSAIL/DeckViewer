package edu.mit.kacquah.deckviewer.speech.synthesis;

import java.util.LinkedList;

import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;

/**
 * Creates TTS events and passes them on to any listeners.
 * @author kojo
 *
 */
public class TextToSpeechEventGenerator implements SpeakableListener {
  public interface TextToSpeechEventListener {
    /**
     * Called when there is a new speech event.
     * @param e
     */
    public void textToSpeechEvent(SpeakableEvent e);
  }
  
  //============================================================================
  
  /**
   * TextToSpepech listeners.
   */
  private LinkedList<TextToSpeechEventListener> listeners;
  
  public TextToSpeechEventGenerator() {
    this.listeners = new LinkedList<TextToSpeechEventListener>();
  }
  
  /**
   * Add Listener.
   * @param l
   */
  public void addListener(TextToSpeechEventListener l) {
    this.listeners.add(l);
  }
  
  /**
   * Remove listener.
   * @param l
   */
  public void removeListener(TextToSpeechEventListener l) {
    this.listeners.remove(l);
  }
  
  /**
   * Pass message to all listeners.
   * @param e
   */
  private void alertListeners(SpeakableEvent e) {
    for (TextToSpeechEventListener l : listeners) {
      l.textToSpeechEvent(e);
    }
  }

  @Override
  public void markerReached(SpeakableEvent arg0) {
    alertListeners(arg0);    
  }

  @Override
  public void speakableCancelled(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void speakableEnded(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void speakablePaused(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void speakableResumed(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void speakableStarted(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void topOfQueue(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

  @Override
  public void wordStarted(SpeakableEvent arg0) {
    alertListeners(arg0);        
  }

}
