package edu.mit.kacquah.deckviewer.speech.synthesis;

import java.io.File;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Logger;

import javax.speech.Central;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineStateError;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import edu.mit.kacquah.deckviewer.speech.synthesis.TextToSpeechEventGenerator.TextToSpeechEventListener;



public class SpeechSynthesizer implements TextToSpeechEventListener {
  // Singleton
  private static SpeechSynthesizer instance;
  
  // App utils
  private static Logger LOGGER = Logger.getLogger(SpeechSynthesizer.class.getName());
  
  // Voice synthesis 
  private String voiceName;
  Synthesizer synthesizer;
  
  // Listeners
  TextToSpeechEventGenerator eventGenerator;
  
  /**
   * Returns singleton instance of speech synthesizer.
   * @return
   */
  public static SpeechSynthesizer getInstance() {
    return getInstance(null);
  }
  
  /**
   * Returns singleton instance of speech synthesizer with voice.
   * @return
   */
  public static SpeechSynthesizer getInstance(String voiceName) {
    if (instance == null) {
      instance = new SpeechSynthesizer(voiceName);
      return instance;
    } else {
      return instance;
    }
  }
  
  private SpeechSynthesizer(String voiceName) {
    this.voiceName = voiceName;
    this.eventGenerator = new TextToSpeechEventGenerator();
    this.eventGenerator.addListener(this);
  }
  
  private SpeechSynthesizer() {
    this("kevin16");
  }
  
  /**
   * Returns a "no synthesizer" message, and asks the user to check if the
   * "speech.properties" file is at <code>user.home</code> or
   * <code>java.home/lib</code>.
   * 
   * @return a no synthesizer message
   */
   private String noSynthesizerMessage() {
    String message = "No synthesizer created.  This may be the result of any\n"
        + "number of problems.  It's typically due to a missing\n"
        + "\"speech.properties\" file that should be at either of\n"
        + "these locations: \n\n";
    message += "user.home    : " + System.getProperty("user.home") + "\n";
    message += "java.home/lib: " + System.getProperty("java.home")
        + File.separator + "lib\n\n"
        + "Another cause of this problem might be corrupt or missing\n"
        + "voice jar files in the freetts lib directory.  This problem\n"
        + "also sometimes arises when the freetts.jar file is corrupt\n"
        + "or missing.  Sorry about that.  Please check for these\n"
        + "various conditions and then try again.\n";
    return message;
  }
  
  /**
   * List all voices seen by the api.
   */
  public static void listAllVoices(String modeName) {
    System.out.println();
    System.out.println("All " + modeName
        + " Mode JSAPI Synthesizers and Voices:");

    /*
     * Create a template that tells JSAPI what kind of speech synthesizer we are
     * interested in. In this case, we're just looking for a general domain
     * synthesizer for US English.
     */
    SynthesizerModeDesc required = new SynthesizerModeDesc(null, // engine name
        modeName, // mode name
        Locale.US, // locale
        null, // running
        null); // voices

    /*
     * Contact the primary entry point for JSAPI, which is the Central class, to
     * discover what synthesizers are available that match the template we
     * defined above.
     */
    EngineList engineList = Central.availableSynthesizers(required);
    for (int i = 0; i < engineList.size(); i++) {

      SynthesizerModeDesc desc = (SynthesizerModeDesc) engineList.get(i);
      System.out.println("    " + desc.getEngineName() + " (mode="
          + desc.getModeName() + ", locale=" + desc.getLocale() + "):");
      Voice[] voices = desc.getVoices();
      for (int j = 0; j < voices.length; j++) {
        System.out.println("        " + voices[j].getName());
      }
    }
  }
    
  public void initSpeech() {
    /*
     * List all the "general" domain voices, which are voices that are capable
     * of attempting to speak almost any text you throw at them.
     */
    listAllVoices("general");
    
    LOGGER.info("Starting speech synthesis...");
    LOGGER.info("Using voice: " + voiceName);

    try {
      /*
       * Find a synthesizer that has the general domain voice we are looking
       * for. NOTE: this uses the Central class of JSAPI to find a Synthesizer.
       * The Central class expects to find a speech.properties file in user.home
       * or java.home/lib.
       * 
       * If your situation doesn't allow you to set up a speech.properties file,
       * you can circumvent the Central class and do a very non-JSAPI thing by
       * talking to FreeTTSEngineCentral directly. See the WebStartClock demo
       * for an example of how to do this.
       */
      SynthesizerModeDesc desc = new SynthesizerModeDesc(null, // engine name
          "general", // mode name
          Locale.US, // locale
          null, // running
          null); // voice
      synthesizer = Central.createSynthesizer(desc);

      /*
       * Just an informational message to guide users that didn't set up their
       * speech.properties file.
       */
      if (synthesizer == null) {
        LOGGER.severe(noSynthesizerMessage());
        System.exit(1);
      }
      /*
       * Get the synthesizer ready to speak
       */
      synthesizer.allocate();
      synthesizer.resume();
      
      // Set listener
      synthesizer.addSpeakableListener(eventGenerator);

      /*
       * Choose the voice.
       */
      desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
      Voice[] voices = desc.getVoices();
      Voice voice = null;
      for (int i = 0; i < voices.length; i++) {
        if (voices[i].getName().equals(voiceName)) {
          voice = voices[i];
          break;
        }
      }
      if (voice == null) {
        LOGGER.severe("Synthesizer does not have a voice named "
            + voiceName + ".");
        System.exit(1);
      }
      synthesizer.getSynthesizerProperties().setVoice(voice);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /**
   * Closes the speech synthesizer and cleans.
   */
  public void close() {
    try {
      synthesizer.deallocate();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /**
   * Starts plain text synthesis then returns.
   * @param text
   */
  public void speakText(String text) {
    speakText(text, null);
  }
  
  /**
   * Starts plain text synthesis then returns.
   * @param text
   */
  public void speakText(String text, SpeakableListener l) {
    /*
     * The the synthesizer to speak and wait for it to complete.
     */
    synthesizer.speakPlainText(text, l);
  }
  
  /**
   * Speaks plain text and blocks until synthesis is complete.
   * @param text
   */
  public void speakTextAndWait(String text) {
    speakTextAndWait(text, null);
  }
  
  /**
   * Speaks plain text and blocks until synthesis is complete.
   * @param text
   */
  public void speakTextAndWait(String text, SpeakableListener l) {
    /*
     * The the synthesizer to speak and wait for it to complete.
     */
    synthesizer.speakPlainText(text, l);
    try {
      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void textToSpeechEvent(SpeakableEvent e) {
    LOGGER.info(e.toString());
  }
   
}
