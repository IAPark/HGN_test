package edu.kent.cs.aisp.hgn_test.findeye;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.IOException;

import edu.kent.cs.aisp.hgn_test.R;

/**
 * Created by isaac on 2/15/16.
 */
public class FindFace implements Finder {

    CascadeClassifier face_classifier;

    public FindFace(Context context) {
        try {
            face_classifier = new CascadeClassifier(utility.resource_to_file("face.xml", R.raw.haarcascade_frontalface_default, context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public MatOfRect find(Mat image) {
        MatOfRect faces = new MatOfRect();
        face_classifier.detectMultiScale(image, faces,  1.1, 2, 0, new Size(150, 150), new Size());

        Rect largest = null;
        int largest_size = 0;

        for (Rect rect: faces.toArray()) {
            int current_size = (rect.height * rect.height) + (rect.width * rect.width);

            if (current_size > largest_size) {
                largest = rect;
                largest_size = current_size;
            }
        }

        if (largest != null) {
            faces = new MatOfRect(largest);
        }

        return faces;
    }
}
