package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.mit.kacquah.deckviewer.deckobjects.FlyingObject;
import edu.mit.kacquah.deckviewer.environment.ParkingSpot;

/**
 * Utils for sorting deck objects.
 * 
 * @author kojo
 * 
 */
public class Sorting {
  
  /**
   * Tuple of an element and a distance.
   * @author kojo
   *
   */
  public static class DistancePair<E> implements Comparable<DistancePair<E>>{
    public final E element;
    public final float distance;
    
    public DistancePair(E element, float distance) {
      this.element = element;
      this.distance = distance;
    }
    
    @Override
    public int compareTo(DistancePair o) {
      return distance < o.distance ? -1 : distance > o.distance ? 1 : 0;
    }
  }
  
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
    List<DistancePair<ParkingSpot>> pairs = new ArrayList<DistancePair<ParkingSpot>>();
    for(ParkingSpot p : parkingSpots) {
      float distance = (float) p.center.distance(target);
      pairs.add(new DistancePair<ParkingSpot>(p, distance));
    }
    
    // Sort
    Collections.sort(pairs);
    
    // Return result
    LinkedList<ParkingSpot> result = new LinkedList<ParkingSpot>();
    for (DistancePair<ParkingSpot> pair: pairs) {
      result.add(pair.element);
    }
    return result;
  }
  
  /**
   * Takes in a list of flying objects and a target point. Returns a sorted list
   * of the flyinb objects based on increasing distance from the target spot.
   * 
   * @param flyingObjects
   * @param target
   * @return
   */
  public static LinkedList<FlyingObject> flyingObjectDistanceSort(
    LinkedList<FlyingObject> flyingObjects, Point target, LinkedList<FlyingObject> result) {
    
    // Create list of distance pairs
    List<DistancePair<FlyingObject>> pairs = new ArrayList<DistancePair<FlyingObject>>();
    for(FlyingObject f : flyingObjects) {
      float distance = (float) f.position().distance(target);
      pairs.add(new DistancePair<FlyingObject>(f, distance));
    }
    
    // Sort
    Collections.sort(pairs);
    
    // Return result
    if (result == null) {
      result = new LinkedList<FlyingObject>();
    }
    for (DistancePair<FlyingObject> pair: pairs) {
      result.add(pair.element);
    }
    return result;
  }

}
