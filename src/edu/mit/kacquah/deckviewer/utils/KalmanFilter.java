package edu.mit.kacquah.deckviewer.utils;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvRealScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSetIdentity;
import static com.googlecode.javacv.cpp.opencv_video.cvCreateKalman;
import static com.googlecode.javacv.cpp.opencv_video.cvKalmanCorrect;
import static com.googlecode.javacv.cpp.opencv_video.cvKalmanPredict;

import java.nio.FloatBuffer;

import javax.vecmath.Point2f;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_video.CvKalman;

public class KalmanFilter {
  
  /**
   * Transition matrix describing relationship between model parameters at step
   * k and at step k + 1 (the dynamics of the model).
   */
  private static final float[] F = {1, 0, 1, 0, 
                                    0, 1, 0, 1,
                                    0, 0, 1, 0,
                                    0, 0, 0, 1};

  private CvKalman kalmanFilter;
  private CvMat measurement;

  public KalmanFilter() {
    // Initialize Kalman Filter
    kalmanFilter = cvCreateKalman(4, 2, 0);
    // Initializes Kalman filter parameters.
    FloatBuffer fb = kalmanFilter.transition_matrix().getFloatBuffer();
    fb.rewind();
    fb.put(F);
    
    cvSetIdentity(kalmanFilter.measurement_matrix(), cvRealScalar(1));
    cvSetIdentity(kalmanFilter.process_noise_cov(), cvRealScalar(1e-4));
    cvSetIdentity(kalmanFilter.measurement_noise_cov(), cvRealScalar(10));
    cvSetIdentity(kalmanFilter.error_cov_post(), cvRealScalar(0.1));
    
    measurement = cvCreateMat(2,1,CV_32FC1);
  }
  
  public void init(float x, float y) {
    CvMat statePre = kalmanFilter.state_pre();
    statePre.put(0, 0, x);
    statePre.put(1, 0, y);
    statePre.put(2, 0, 0);
    statePre.put(3, 0, 0);
  }
  
  public void predict() {
    // Predicted point position.
    CvMat prediction = cvKalmanPredict(kalmanFilter, null);
    float x = (float)prediction.get(0);
    float y = (float)prediction.get(1);
  }
  
  public Point2f correct(float x, float y) {
    measurement.put(x,y);
    CvMat statePost = cvKalmanCorrect(kalmanFilter, measurement);
    Point2f result = new Point2f((float)statePost.get(0), (float)statePost.get(1));
    return result;
  }
}
