package edu.mit.kacquah.deckviewer.speech;

import java.util.logging.Logger;

import edu.cmu.sphinx.api.SpeechResult;
import edu.mit.kacquah.deckviewer.action.SelectionManager;
import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import edu.mit.kacquah.deckviewer.speech.SpeechEngine.ISpeechEventListener;

/**
 * Parser for processing speech commands.
 * 
 * @author kojo
 * 
 */
public class SpeechParser implements ISpeechEventListener {
  private static Logger LOGGER = Logger.getLogger(SpeechParser.class
      .getName());
  private SelectionManager selectionManager;

  public SpeechParser() {

  }

  public void setSelectionManager(SelectionManager m) {
    this.selectionManager = m;
  }

  @Override
  public void handleSpeechResult(SpeechResult result) {
    String command = result.getHypothesis().toLowerCase();

    // Since commands come in two types (action + selection) or (location), we
    // simply check for text unique to each sequence and handle appropriately.
    if (command.contains(Commands.MOVE) || command.contains(Commands.PLACE)) {
      //TODO: Pass specific actions to selectionWithAction.
      selectionManager.selectWithAction(null);
    } else if (command.contains(Commands.TO) || command.contains(Commands.OVER)) {
      //TODO: Pass specific targets to executAction.
      selectionManager.executeActionWithTarget(null);
    } else {
      // We don't understand this command...
      LOGGER.severe("Unable to parse command:" + command);
    }

  }

}
