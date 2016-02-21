#include <jni.h>
#include "/OpenCV/sdk/native/jni/include/opencv2/opencv.hpp"

using cv::Mat;


Mat computeGradientX(Mat mat) {
    mat.convertTo(mat, CV_16S);


    Mat out(mat.rows, mat.cols, CV_64F);
    double *out_buffer;
    short *in_buffer;

    out_buffer = (double*) out.ptr();
    in_buffer = (short*) mat.ptr();

    for (int y = 0; y < mat.rows; ++y) {
        int y_offset = y*mat.cols;
        out_buffer[y_offset] = in_buffer[y_offset] - in_buffer[y_offset + 1];
        for (int x = 1; x < mat.cols - 1; ++x) {
            double value = in_buffer[y_offset + x + 1] - in_buffer[y_offset + x -1];
            out_buffer[y_offset + x] = value/2;
        }
        out_buffer[y_offset + mat.cols-1] = (in_buffer[y_offset + mat.cols-1] - in_buffer[y_offset + mat.cols-2]);
    }

    return out;
}


JNIEXPORT jint JNICALL
Java_edu_kent_cs_aisp_hgn_1test_findeye_FindEyeCenter_find_1point(JNIEnv *env, jobject instance,
                                                                  jlong image_native_address,
                                                                  jlong point_address) {


    return 3;
    // TODO

}