package edu.kent.cs.aisp.hgn_test.findeye;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by isaac on 2/15/16.
 */
public class CompositeFinder implements Finder{
    List<Finder> pipeline;

    public CompositeFinder(List<Finder> pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public MatOfRect find(Mat image) {
        Stack<MatOfRect> working = new Stack<>();
        working.push(new MatOfRect(new Rect(0, 0, image.width(), image.height())));

        /*

        for(Finder finder: pipeline) {
            Stack<MatOfRect> layer = new Stack<>();
            for(MatOfRect )
            for(Rect rec: working) {
            }
        }

        */
        return null;
    }
}
