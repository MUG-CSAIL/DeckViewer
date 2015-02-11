package edu.mit.kacquah.deckviewer.utils;

import edu.mit.kacquah.deckviewer.environment.ParkingSpot;

/**
 * Tuple of parking spot and a distance.
 * @author kojo
 *
 */
public class ParkingSpotDistancePair implements Comparable<ParkingSpotDistancePair>{
  public final ParkingSpot parkingSpot;
  public final float distance;
  
  public ParkingSpotDistancePair(ParkingSpot parkingSpot, float distance) {
    this.parkingSpot = parkingSpot;
    this.distance = distance;
  }
  
  @Override
  public int compareTo(ParkingSpotDistancePair o) {
    return distance < o.distance ? -1 : distance > o.distance ? 1 : 0;
  }
}
