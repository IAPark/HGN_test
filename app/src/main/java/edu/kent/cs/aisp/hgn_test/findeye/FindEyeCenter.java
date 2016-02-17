package edu.kent.cs.aisp.hgn_test.findeye;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by isaac on 2/15/16.
 */
public class FindEyeCenter implements Finder{


    public FindEyeCenter() {
        System.loadLibrary("app-native");
    }

    Mat computeGradientX(Mat mat) {
        mat.convertTo(mat, CvType.CV_16S);


        Mat out = new Mat(mat.rows(), mat.cols(), CvType.CV_64F);
        double[] out_buffer = new double[(int) (out.total() * out.channels())];
        short[] in_buffer = new short[(int) (mat.total() * mat.channels())];

        out.get(0, 0, out_buffer);
        mat.get(0, 0, in_buffer);

        for (int y = 0; y < mat.rows(); ++y) {
            int y_offset = y*mat.cols();
            out_buffer[y_offset] = in_buffer[y_offset] - in_buffer[y_offset + 1];
            for (int x = 1; x < mat.cols() - 1; ++x) {
                double value = in_buffer[y_offset + x + 1] - in_buffer[y_offset + x -1];
                out_buffer[y_offset + x] = value/2;
            }
            out_buffer[y_offset + mat.cols()-1] = (in_buffer[y_offset + mat.cols()-1] - in_buffer[y_offset + mat.cols()-2]);
        }

        out.put(0, 0, out_buffer);

        return out;
    }

    Mat computeGradientY(Mat mat) {
        return computeGradientX(mat.t()).t();
    }

    private native Point find_point(Mat image);


    @Override
    public MatOfRect find(Mat image) {

        Mat x_gradient = computeGradientX(image);
        Mat y_gradient = computeGradientY(image);


        Mat x_squared = new Mat(x_gradient.rows(), x_gradient.cols(), x_gradient.type());
        Mat y_squared = new Mat(x_gradient.rows(), x_gradient.cols(), x_gradient.type());

        Core.multiply(x_gradient, x_gradient, x_squared);
        Core.multiply(y_gradient, y_gradient, y_squared);

        Mat magnitude = x_squared;

        Core.add(x_squared, y_squared, magnitude);

        Core.sqrt(magnitude, magnitude);

        double threshold = computeDynamicThreshold(magnitude, 50);


        //normalize

        double[] x_gradient_buffer = new double[(int) x_gradient.total()];
        double[] y_gradient_buffer = new double[(int) y_gradient.total()];
        double[] magnitude_buffer = new double[(int) y_gradient.total()];


        x_gradient.get(0, 0, x_gradient_buffer);
        y_gradient.get(0, 0, y_gradient_buffer);
        magnitude.get(0, 0, magnitude_buffer);

        for (int i = 0; i < magnitude_buffer.length; i++) {
            if (magnitude_buffer[i] > threshold) {
                x_gradient_buffer[i] /= magnitude_buffer[i];
                y_gradient_buffer[i] /= magnitude_buffer[i];
            } else {
                x_gradient_buffer[i] = 0;
                y_gradient_buffer[i] = 0;
            }
        }


        Mat weight = new Mat(image.rows(), image.cols(), CvType.CV_64F);
        Imgproc.GaussianBlur(image, weight, new Size(5, 5), 0, 0);
        Core.subtract(new Mat(weight.rows(), weight.cols(), weight.type(), new Scalar(255)), weight, weight);

        int max_x = -1;
        int max_y = -1;
        double max_score = -1;

        for (int x=0; x < image.cols(); ++x) {
            for (int y=0; y < image.rows(); ++y) {
                double score = calc_score(x, y, weight, x_gradient, y_gradient);

                if (score > max_score) {
                    max_x = x;
                    max_y = y;
                    max_score = score;
                }

            }
        }

        Log.i("test", "Done");
        return new MatOfRect(new Rect(max_x,max_y, 0, 0));
    }

    double calc_score(int cx, int cy, Mat weight, Mat x_gradient, Mat y_gradient) {
        Mat delta_xs = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);
        for(int x=0; x<delta_xs.cols(); ++x) {
            delta_xs.col(x).setTo(new Scalar(x - cx));
        }

        Mat delta_ys =  new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);
        for(int y=0; y<delta_ys.rows(); ++y) {
            delta_ys.row(y).setTo(new Scalar(y - cy));
        }

        Mat delta_xs_squared = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);
        Core.multiply(delta_xs, delta_xs, delta_xs_squared);

        Mat delta_ys_squared = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);
        Core.multiply(delta_ys, delta_ys, delta_ys_squared);

        Mat magnitude = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);

        Core.add(delta_xs_squared, delta_ys_squared, magnitude);

        Core.sqrt(magnitude, magnitude);

        Core.divide(delta_xs, magnitude, delta_xs);
        Core.divide(delta_ys, magnitude, delta_ys);

        Mat dot = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);
        Mat temp = new Mat(x_gradient.rows(), x_gradient.cols(), CvType.CV_64F);

        Core.multiply(delta_xs, x_gradient, dot);
        Core.multiply(delta_ys, y_gradient, temp);

        Core.add(temp, dot, dot);
        Core.multiply(dot, dot, dot);

        Core.multiply(dot, weight, dot, 1, CvType.CV_64F);
        return Core.sumElems(dot).val[0];
    }

    double computeDynamicThreshold(Mat mat, double stdDevFactor) {
        MatOfDouble stdMagnGrad = new MatOfDouble(), meanMagnGrad = new MatOfDouble();
        Core.meanStdDev(mat, meanMagnGrad, stdMagnGrad);
        double stdDev = stdMagnGrad.get(0,0)[0] / Math.sqrt(mat.rows() * mat.cols());
        return stdDevFactor * stdDev + meanMagnGrad.get(0, 0)[0];
    }
}
