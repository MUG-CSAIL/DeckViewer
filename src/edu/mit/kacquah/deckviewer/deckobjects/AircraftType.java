package edu.mit.kacquah.deckviewer.deckobjects;

/**
 * Types of aircraft available on deck.
 * @author kojo
 *
 */
public enum AircraftType {
  F18("f18"),
  C2("c2"),
  F35("f35"),
  X47B("pegasus");
  
  public final String name;
  
  AircraftType(String name) {
    this.name = name;
  }
}
