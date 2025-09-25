#ifndef JNI_HELPER_H
#define JNI_HELPER_H

#include <jni.h>
#include <string>

class JniHelper {
public:
    static JNIEnv* getEnv();
    static jclass loadClassFromApp(JNIEnv* env, const char* className);
    static void showToast(const char* msg);
    static void showDialog(const char* message);
    static void showDialog(const char* title, const char* message);
    static void showDialog(const char* title, const char* message, bool addButton);
    static std::string getScreenshot();
    static std::string getDeviceId();
};

#endif // JNI_HELPER_H
