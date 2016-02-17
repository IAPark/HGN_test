package edu.kent.cs.aisp.hgn_test.findeye;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.IOException;

import edu.kent.cs.aisp.hgn_test.R;


public class FindEye implements Finder{

    CascadeClassifier eye_classifier;


    public FindEye(Context context) {
        try {
            eye_classifier = new CascadeClassifier(utility.resource_to_file("face.xml", R.raw.haarcascade_eye, context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public MatOfRect find(Mat image) {
        MatOfRect eyes = new MatOfRect();
        eye_classifier.detectMultiScale(image, eyes,  1.1, 2, 0, new Size(150, 150), new Size());

        Rect largest = null;
        int largest_size = 0;

        Rect second_largest = null;
        int second_largest_size = 0;

        for (Rect rect: eyes.toArray()) {
            int current_size = (rect.height * rect.height) + (rect.width * rect.width);

            if (current_size > second_largest_size) {
                if (current_size > largest_size) {
                    second_largest = largest;
                    second_largest_size = largest_size;

                    largest = rect;
                    largest_size = current_size;
                } else {
                    second_largest = rect;
                    largest_size = current_size;
                }
            }
        }

        if (largest != null && second_largest != null) {
            eyes = new MatOfRect(largest, second_largest);
        }

        return eyes;
    }
}
