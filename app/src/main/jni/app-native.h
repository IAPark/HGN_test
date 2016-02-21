//
// Created by isaac on 2/18/16.
//

#ifndef HGN_TEST_APP_NATIVE_H
#define HGN_TEST_APP_NATIVE_H

#include <jni.h>

extern "c" {

JNIEXPORT jint JNICALL
        Java_edu_kent_cs_aisp_hgn_1test_findeye_FindEyeCenter_find_1point(JNIEnv *env, jobject instance,
                                                                          jlong image_native_address,
                                                                          jlong point_address);

};


#endif //HGN_TEST_APP_NATIVE_H
