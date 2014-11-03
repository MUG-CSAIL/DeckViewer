package edu.mit.kacquah.deckviewer.utils;

import java.io.File;

public class FileUtil {
  /**
   * Returns a new string formed by joining the strings using File.separator.
   * @param strings
   * @return an empty string if strings is null.
   */
  public static String join(String... strings) {
    if (strings == null)
      return "";
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < strings.length - 1; i++) {
      sb.append(strings[i]);
      sb.append(File.separatorChar);
    }
    sb.append(strings[strings.length - 1]);
    return sb.toString();
  }
}
