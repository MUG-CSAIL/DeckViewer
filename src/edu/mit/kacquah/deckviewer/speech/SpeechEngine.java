package edu.mit.kacquah.deckviewer.speech;

import java.io.IOException;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;

/**
 * Engine for running Sphinx4 speech recognition and returning the result.
 * @author kojo
 *
 */
public class SpeechEngine implements Runnable {  
  
  /**
   * Listener interface for receiving speech events.
   * @author kojo
   *
   */
  public static interface ISpeechEventListener {
    public void handleSpeechResult(SpeechResult result);
  }
  
  // App utils
  private static Logger LOGGER = Logger.getLogger(SpeechEngine.class
      .getName());

  // Configuration
  Configuration configuration;
//  boolean useLanguage = false;
  private String grammarPath;
  private String grammarName;
  
  // Recognizer
  LiveSpeechRecognizer recognizer;
  SpeechResult result;

  // Threads
  boolean initialized;
  Thread speechRecognitionThread;
  
  // Listeners for events
  ISpeechEventListener speechListener;
  
  public SpeechEngine() {
    this.initialized = false;
  }
  
  public void setGrammarPath(String path) {
    this.grammarPath = path;
  }
  
  public void setGrammarName(String name) {
    this.grammarName = name;
  }
  
  public void setSpeechListener(ISpeechEventListener listener) {
    this.speechListener = listener;
  }

  public void initRecognition() {
    LOGGER.info("Loading models...");

    configuration = new Configuration();

    // Set path to acoustic model.
    configuration
        .setAcousticModelPath("resource:/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz");
    // Set path to dictionary.
    configuration
        .setDictionaryPath("resource:/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/dict/cmudict.0.6d");
    // Set language model.
    if (grammarName.isEmpty() || grammarPath.isEmpty()) {
      configuration.setLanguageModelPath("models/language/en-us.lm.dmp");
    } else {
      configuration.setGrammarPath(grammarPath);
      configuration.setUseGrammar(true);
      configuration.setGrammarName(grammarName);
    }
    
    try {
      recognizer = new LiveSpeechRecognizer(configuration);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    initialized = true;
  }
  
  public void startRecognition() {
    if (initialized) {
      speechRecognitionThread = new Thread(this);
      speechRecognitionThread.start();
    } else {
      throw new IllegalStateException("Need to initializes speech.");
    }
  }

  @Override
  public void run() {
    // Start recognition process pruning previously cached data.
    recognizer.startRecognition(true);
    SpeechResult result;
    LOGGER.info("Recognizer created...");

    // Process speech results and send to listeners.
    while ((result = recognizer.getResult()) != null) {
      LOGGER.info("New speech result: " + result.getHypothesis());
      if (speechListener != null) {
        speechListener.handleSpeechResult(result);
      }
    }
    
    recognizer.stopRecognition();
    LOGGER.info("Speech thread terminating");
  }
  

}
