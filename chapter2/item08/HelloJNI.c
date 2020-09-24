#include <stdio.h>
#include <jni.h>
#include "HelloJNI.h"

JNIEXPORT void JNICALL Java_HelloJNI_helloFromC(JNIEnv *env, jclass ojb) {
    printf("%s", "Hello from C!\n");
}

