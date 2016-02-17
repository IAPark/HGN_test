package edu.kent.cs.aisp.hgn_test.findeye;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Created by isaac on 2/15/16.
 */
public interface Finder {
    MatOfRect find(Mat image);
}
