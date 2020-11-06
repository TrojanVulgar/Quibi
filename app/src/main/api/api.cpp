#include <jni.h>
#include <string>




// Your Server Api Here
extern "C" JNIEXPORT jstring JNICALL
Java_com_easyplex_util_Constants_baseUrlFromJNI(JNIEnv *env, jclass clazz) {
    std::string mUrl = "aHR0cDovLzE5Mi4xNjguMS4xMDcvcHVibGljL2FwaS8";
    return env->NewStringUTF(mUrl.c_str());
}




extern "C" JNIEXPORT jstring JNICALL
Java_com_easyplex_util_Constants_AppfromJNI(JNIEnv *env, jclass clazz) {
std::string mUrl = "aHR0cHM6Ly9hcGkuZW52YXRvLmNvbS92My8";
return env->NewStringUTF(mUrl.c_str());
}