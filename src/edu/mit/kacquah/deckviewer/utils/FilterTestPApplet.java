package edu.mit.kacquah.deckviewer.utils;

import java.awt.Point;

import javax.vecmath.Point2f;

import processing.core.PApplet;
import processing.core.PVector;

public class FilterTestPApplet extends PApplet {
  
  private static final float RAND_RADIUS = 10;
  private static final float CIRCLE_RADIUS = 5;
  
  private final int NOISE_COLOR = color(255, 105, 0);
  private final int MOUSE_COLOR = color(0, 255, 0);
  private final int FILTER_COLOR = color(0, 0, 255);
  
  private PVector noisePoint;
  private Point2f estimatedPoint;
  
  private KalmanFilter kalmanFilter;
//  private Mat measurement, prediction, estimated;
  
  public void setup() {
    size(800, 800);
    noisePoint = new PVector(mouseX, mouseY);
    ellipseMode(CENTER);
    kalmanFilter = new KalmanFilter();
    kalmanFilter.init(mouseX, mouseY);
  }

  public void draw() {
    // Update the app.
    long elapsedTime = System.currentTimeMillis() % 1000;
    update(elapsedTime);
    // render the app.
    render();
  }

  public void update(long elapsedTime) {
    noisePoint.x = mouseX + random(-RAND_RADIUS, RAND_RADIUS);
    noisePoint.y = mouseY + random(-RAND_RADIUS, RAND_RADIUS);
    
    kalmanFilter.predict();
    estimatedPoint = kalmanFilter.correct(noisePoint.x, noisePoint.y);
    
  }

  public void render() {
    background(0);
    
    stroke(MOUSE_COLOR);
    fill(MOUSE_COLOR);
    ellipse(mouseX , mouseY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
    
    stroke(NOISE_COLOR);
    fill(NOISE_COLOR);
    ellipse(noisePoint.x , noisePoint.y, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
    
    stroke(FILTER_COLOR);
    fill(FILTER_COLOR);
    ellipse(estimatedPoint.x , estimatedPoint.y, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
  }

  public static void main(String[] args) {
    String[] newArgs = new String[] { "edu.mit.kacquah.deckviewer.utils.FilterTestPApplet" };
    PApplet.main(newArgs);
  }
}
