# DeckViewer

This code is part of the research project for multimodal interaction with an Aircraft Carreir Deck Ouija Board Display. 

# Environment 
* Currently developing on Ubuntu 12.04 with Sun Java 7. Tested on Windows 7.

# Prerequisites

## Install the Tabletop Kinect project
* [TabletopKinect] (https://github.com/MUG-CSAIL/tabletop_kinect)
 * Follow the instructions to get Tabletop Kinect running. Tabletop Kinect is used by DeckVeiwer for hand tracking.

## Install Java speech synthesis and recognition libraries. 
* [Processing] (processing.org) (Version 2.0.3)
  * Download build for your specific platform.
  * Pull the Core.jar from the processing lib folder and place in the project lib folder. Add the jar to the Java build path. 
  * More information on using processing in Eclipse here: https://processing.org/tutorials/eclipse/
* [CMUSphinx] (http://cmusphinx.sourceforge.net/) (Version 5prealpha)
  * Download from here: http://sourceforge.net/projects/cmusphinx/files/sphinx4/
  * Follow tutorial instructions here: http://cmusphinx.sourceforge.net/wiki/tutorialsphinx4
  * Add the following jars to the lib folder and add to the build path for the java:
   * sphinx4.jar
  * Add the following jars to the lib folder and then add them to the class path for java:
   * WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar
  * To test speech recognition, run the `deckviewer/speech/recognizer/PushToTalkPApplet.java`. 
* [FreeTTS] (http://freetts.sourceforge.net/docs/index.php) (Version1.2.2)
  * Download the `freetts-1.2.2-bin.zip` from here: http://sourceforge.net/projects/freetts/files/FreeTTS/
  * Follow install instructions here: http://freetts.sourceforge.net/docs/index.php#download_and_install
  * Place ??? jars in the lib folder and add to the build path for the java.
  


  
  
  
  
