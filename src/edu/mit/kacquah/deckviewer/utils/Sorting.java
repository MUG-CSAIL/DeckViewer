package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.mit.kacquah.deckviewer.environment.ParkingSpot;

/**
 * Utils for sorting deck objects.
 * 
 * @author kojo
 * 
 */
public class Sorting {
  /**
   * Takes in a list of parking spots and a target point. Returns a sorted list
   * of the parking spots based on increasing distance from the target spot.
   * 
   * @param parkingSpots
   * @param target
   * @return
   */
  public static LinkedList<ParkingSpot> parkingSpotDistanceSort(
    LinkedList<ParkingSpot> parkingSpots, Point target) {
    
    // Create list of distance pairs
    List<ParkingSpotDistancePair> pairs = new ArrayList<ParkingSpotDistancePair>();
    for(ParkingSpot p : parkingSpots) {
      float distance = (float) p.center.distance(target);
      pairs.add(new ParkingSpotDistancePair(p, distance));
    }
    
    // Sort
    Collections.sort(pairs);
    
    // Return result
    LinkedList<ParkingSpot> result = new LinkedList<ParkingSpot>();
    for (ParkingSpotDistancePair pair: pairs) {
      result.add(pair.parkingSpot);
    }
    return result;
  }

}
