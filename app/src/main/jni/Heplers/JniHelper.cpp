#include "JniHelper.h"
#include "BGFX/Logger.h"
#include <jni.h>
#include <android/log.h>

extern JavaVM* g_JavaVM;

JNIEnv* JniHelper::getEnv() {
    if (!g_JavaVM) {
        BGFX_LOG_ERROR("g_JavaVM is null");
        return nullptr;
    }
    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) return nullptr;
    
    return env;
}

jclass JniHelper::loadClassFromApp(JNIEnv* env, const char* className) {
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentApplication = env->GetStaticMethodID(activityThread, "currentApplication", "()Landroid/app/Application;");
    jobject context = env->CallStaticObjectMethod(activityThread, currentApplication);
    if (!context) return nullptr;

    jclass contextClass = env->GetObjectClass(context);
    jmethodID getClassLoader = env->GetMethodID(contextClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
    jobject classLoader = env->CallObjectMethod(context, getClassLoader);

    jclass classLoaderClass = env->FindClass("java/lang/ClassLoader");
    jmethodID loadClass = env->GetMethodID(classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

    jstring classNameStr = env->NewStringUTF(className);
    jclass clazz = (jclass)env->CallObjectMethod(classLoader, loadClass, classNameStr);
    env->DeleteLocalRef(classNameStr);
    return clazz;
}

jobject getCurrentActivity() {
    if (!g_JavaVM) {
        BGFX_LOG_ERROR("g_JavaVM is null");
        return nullptr;
    }

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) {
        BGFX_LOG_ERROR("Failed to attach current thread");
        return nullptr;
    }

    jclass cls = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!cls) {
        BGFX_LOG_ERROR("Failed to find HelperProXD class");
        return nullptr;
    }

    jmethodID mid = env->GetStaticMethodID(cls, "getActivity", "()Landroid/app/Activity;");
    if (!mid) {
        BGFX_LOG_ERROR("Failed to find getActivity method");
        return nullptr;
    }

    jobject activity = env->CallStaticObjectMethod(cls, mid);
    return activity;
}

void JniHelper::showToast(const char* msg) {
    if (!g_JavaVM) return;

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) return;

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) return;

    jmethodID toastMethod = env->GetStaticMethodID(helperClass, "f", "(Ljava/lang/String;)V");
    if (!toastMethod) return;

    jstring jMsg = env->NewStringUTF(msg);
    env->CallStaticVoidMethod(helperClass, toastMethod, jMsg);
    env->DeleteLocalRef(jMsg);

    g_JavaVM->DetachCurrentThread();
}

void JniHelper::showDialog(const char* message) {
    if (!g_JavaVM) return;

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) return;

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) return;

    jmethodID dialogMethod = env->GetStaticMethodID(helperClass, "xb", "(Ljava/lang/String;)V");
    if (!dialogMethod) return;

    jstring jMsg = env->NewStringUTF(message);
    env->CallStaticVoidMethod(helperClass, dialogMethod, jMsg);
    env->DeleteLocalRef(jMsg);

    g_JavaVM->DetachCurrentThread();
}

void JniHelper::showDialog(const char* title, const char* message) {
    if (!g_JavaVM) return;

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) return;

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) return;

    jmethodID dialogMethod = env->GetStaticMethodID(helperClass, "xb", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (!dialogMethod) return;

    jstring jTitle = env->NewStringUTF(title);
    jstring jMessage = env->NewStringUTF(message);
    env->CallStaticVoidMethod(helperClass, dialogMethod, jTitle, jMessage);
    env->DeleteLocalRef(jTitle);
    env->DeleteLocalRef(jMessage);

    g_JavaVM->DetachCurrentThread();
}

void JniHelper::showDialog(const char* title, const char* message, bool addButton) {
    if (!g_JavaVM) return;

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) return;

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) return;

    jmethodID dialogMethod = env->GetStaticMethodID(helperClass, "xb", "(Ljava/lang/String;Ljava/lang/String;Z)V");
    if (!dialogMethod) return;

    jstring jTitle = env->NewStringUTF(title);
    jstring jMsg = env->NewStringUTF(message);
    env->CallStaticVoidMethod(helperClass, dialogMethod, jTitle, jMsg, JNI_TRUE);
    env->DeleteLocalRef(jTitle);
    env->DeleteLocalRef(jMsg);

    g_JavaVM->DetachCurrentThread();
}

std::string JniHelper::getScreenshot() {
    if (!g_JavaVM) {
        return "g_JavaVM is null";
    }

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) {
        return "Failed to attach thread";
    }

    jobject activity = getCurrentActivity();
    if (!activity) {
        return "Activity is null";
    }

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) {
        return "HelperProXD class not found";
    }

    jmethodID methodID = env->GetStaticMethodID(helperClass, "xx", "()Ljava/lang/String;");
    if (!methodID) {
        return "xx method not found";
    }

    jstring result = (jstring)env->CallStaticObjectMethod(helperClass, methodID);
    if (!result) {
        return "Result string is null";
    }

    const char* str = env->GetStringUTFChars(result, nullptr);
    std::string finalStr = str ? str : "null";
    env->ReleaseStringUTFChars(result, str);

    g_JavaVM->DetachCurrentThread();

    return finalStr;
}

std::string JniHelper::getDeviceId() {
    if (!g_JavaVM) {
        return "g_JavaVM is null";
    }

    JNIEnv* env = nullptr;
    if (g_JavaVM->AttachCurrentThread(&env, nullptr) != JNI_OK || !env) {
        return "Failed to attach thread";
    }

    /*jobject activity = getCurrentActivity();
    if (!activity) {
        return "Activity is null";
    }*/

    jclass helperClass = JniHelper::loadClassFromApp(env, "com/executor/helpers/HelperProXD");
    if (!helperClass) {
        return "HelperProXD class not found";
    }

    jmethodID methodID = env->GetStaticMethodID(helperClass, "ebl", "()Ljava/lang/String;");
    if (!methodID) {
        return "ebl method not found";
    }

    jstring result = (jstring)env->CallStaticObjectMethod(helperClass, methodID);
    if (!result) {
        return "Result string is null";
    }

    const char* str = env->GetStringUTFChars(result, nullptr);
    std::string finalStr = str ? str : "null";
    env->ReleaseStringUTFChars(result, str);

    g_JavaVM->DetachCurrentThread();

    return finalStr;
}
