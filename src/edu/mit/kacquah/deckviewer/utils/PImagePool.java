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
          "purple", "brown", "blue", "vmac"));

  private static HashMap<String, PImage[]> loadedImages = new HashMap<String, PImage[]>();

  public static PImage[] getImages(String imageName, float scaleRatio) {
    String loadedImagesName = imageName + "-" + scaleRatio;
    if (imageName == "tanker" || imageName == "Tanker") {
      imageName = "fmac";
    } else if (imageName == "F18" || imageName == "fmac") {
      imageName = "fmac";
    } else if (imageName == "C2" || imageName == "smac") {
      imageName = "smac";
    } else if (imageName == "Pegasus" || imageName == "fuav"
        || imageName == "peg") {
      imageName = "fuav";
    } else if (imageName == "predator" || imageName == "suav"
        || imageName == "pred") {
      imageName = "suav";
    } else if (imageName == "F35" || imageName == "vmac") {
      imageName = "vmac";
    }
    if (keyWords.contains(imageName)) {
      // Check to see if we've already loaded image list
      if (loadedImages.containsKey(loadedImagesName)) {
        return loadedImages.get(loadedImagesName);
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

      PImage[] results = new PImage[i];
      for (int j = 1; j <= i; j++) {
        PImage result;
         result = parent.loadImage("./resources/" + imageName + "_image_"
            + j + ".png");
        //  Scale image to correct size
         float width = result.width;
         float height = result.height;
         result.resize((int)(width * scaleRatio), (int)(height * scaleRatio));
         // Set output
         results[j - 1] = result;
        if (results[j - 1] == null) {
          // One of the images was unsuccessfully loaded; return null.
          return null;
        }
      }
      // Store the result for later.
      loadedImages.put(loadedImagesName, results);
      return results;
    } else {
      // Keyword not found, so return null.
      return null;
    }
  }

  public static void setParent(PApplet p) {
    parent = p;
  }
}
