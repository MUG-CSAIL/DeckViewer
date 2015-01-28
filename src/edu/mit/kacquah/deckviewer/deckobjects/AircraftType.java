package edu.mit.kacquah.deckviewer.deckobjects;

/**
 * Types of aircraft available on deck.
 * @author kojo
 *
 */
public enum AircraftType {
  F18("F18"),
  C2("C2"),
  F35("F35"),
  X47B("Pegasus");
  
  public final String name;
  
  AircraftType(String name) {
    this.name = name;
  }
}
