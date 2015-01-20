package edu.mit.kacquah.deckviewer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import edu.mit.kacquah.deckviewer.game.DeckViewerPApplet;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Maintains a pool of singleton images for distributing across classes in the
 * app. Only loads each image once into memory.
 * 
 * @author kojo
 * 
 */
public class PImagePool {
  private static PApplet parent;

  public static ArrayList<String> keyWords = new ArrayList<String>(
      Arrays.asList("fmac", "smac", "fuav", "suav", "predator", "c2", "f18",
          "helicopter", "pegasus", "tanker", "f35", "ugv_service",
          "ugv_weapons", "ugv_fuel", "white", "red", "green", "yellow",
          "purple", "brown", "blue"));

  private static HashMap<String, PImage[]> loadedImages = new HashMap<String, PImage[]>();

  public static PImage[] getImages(String imageName) {
    if (imageName == "tanker" || imageName == "Tanker") {
      imageName = "fmac";
    } else if (imageName == "f18" || imageName == "fmac") {
      imageName = "fmac";
    } else if (imageName == "c2" || imageName == "smac") {
      imageName = "smac";
    } else if (imageName == "pegasus" || imageName == "fuav"
        || imageName == "peg") {
      imageName = "fuav";
    } else if (imageName == "predator" || imageName == "suav"
        || imageName == "pred") {
      imageName = "suav";
    } else if (imageName == "f35" || imageName == "vmac") {
      imageName = "f35";
    }
    if (keyWords.contains(imageName)) {
      // Check to see if we've already loaded image list
      if (loadedImages.containsKey(imageName)) {
        return loadedImages.get(imageName);
      }
      int i = 0;
      File tmp = null;
      do {
        i++;
        String fileName = imageName + "_image_" + i + ".png";
        fileName = FileUtil.join(DeckViewerPApplet.RESOURCE_DIR, fileName);
        tmp = new File(fileName);
      } while (tmp.exists());
      i--;

      PImage[] result = new PImage[i];
      for (int j = 1; j <= i; j++) {
        result[j - 1] = parent.loadImage("./resources/" + imageName + "_image_"
            + j + ".png");
        if (result[j - 1] == null) {
          // One of the images was unsuccessfully loaded; return null.
          return null;
        }
      }
      // Store the result for later.
      loadedImages.put(imageName, result);
      return result;
    } else {
      // Keyword not found, so return null.
      return null;
    }
  }

  public static void setParent(PApplet p) {
    parent = p;
  }
}
