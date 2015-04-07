# DeckViewer

This code is part of the research project for multimodal interaction with an Aircraft Carrier Deck Ouija Board Display. 

# Environment 
* Currently developing on Ubuntu 12.04 with Sun Java 7. Tested on Windows 7. Built using Eclipse (Version Kepler)

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
  * Add the following jars to the lib folder and then add them to the build path for the java:
    * `sphinx4.jar`
  * Add the following jars to the lib folder and then add them to the classpath for java:
    * `WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar`
  * For more information, check the tutorial instructions here: http://cmusphinx.sourceforge.net/wiki/tutorialsphinx4
  * To test speech recognition, run the `deckviewer/speech/recognizer/PushToTalkPApplet.java`. 
* [FreeTTS] (http://freetts.sourceforge.net/docs/index.php) (Version 1.2.2)
  * Download the `freetts-1.2.2-bin.zip` from here: http://sourceforge.net/projects/freetts/files/FreeTTS/
  * Follow install instructions here: http://freetts.sourceforge.net/docs/index.php#download_and_install
  * Add the following jars to the lib folder and then add them to the build path for the java:
    * `freetts.jar`
    * `freetts-jsapi.jar`
    * `jsapi.jar`
* (Optional) [Mbrola] (http://tcts.fpms.ac.be/synthesis/mbrola.html)
  * This is only required if you wish to use MBrola voices for FreeTTS.
  * Download appropriate Mbrola binary and voices for your machine: http://tcts.fpms.ac.be/synthesis/mbrola/mbrcopybin.html
    * Note: only en voices work with FreeTTS
  * Follow these instructiosn to integrate into FreeTTS: http://freetts.sourceforge.net/mbrola/README.html
  * Copy the `mbrola.jar` into the lib folder
    * Note: For DeckViewer, make sure you include the location of the mbrola base as a Java VM argument
    * Example: `-Dmbrola.base=/home/jim/mbrola`

# How to build and run

* To build all code, open up this project in Eclipse and add the prerequisites above (particulary the jar files).
* The main class is `DeckViewerMain.java`.
