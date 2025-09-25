
#include "Includes/obfuscate.h"
#include "Menu/Menu.hpp"

bool iconValid = false;
bool settingsValid = false;
bool featuresValid = false;
bool initValid = false;

//Big letter cause crash
void setText(JNIEnv *env, jobject obj, const char* text){
    //https://stackoverflow.com/a/33627640/3763113
    //A little JNI calls here. You really really need a great knowledge if you want to play with JNI stuff
    //Html.fromHtml("");
    jclass html = (*env).FindClass(OBFUSCATE("android/text/Html"));
    jmethodID fromHtml = (*env).GetStaticMethodID(html, OBFUSCATE("fromHtml"), OBFUSCATE("(Ljava/lang/String;)Landroid/text/Spanned;"));

    //setText("");
    jclass textView = (*env).FindClass(OBFUSCATE("android/widget/TextView"));
    jmethodID setText = (*env).GetMethodID(textView, OBFUSCATE("setText"), OBFUSCATE("(Ljava/lang/CharSequence;)V"));

    //Java string
    jstring jstr = (*env).NewStringUTF(text);
    (*env).CallVoidMethod(obj, setText,  (*env).CallStaticObjectMethod(html, fromHtml, jstr));
}

jstring Icon(JNIEnv *env, jobject thiz) {

    //Use https://www.base64encode.org/ to encode your image to base64
    return env->NewStringUTF(
            OBFUSCATE("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"));
}

jstring IconWebViewData(JNIEnv *env, jobject thiz) {

    //WebView support GIF animation. Upload your image or GIF on imgur.com or other sites

    // From internet (Requires android.permission.INTERNET)
    // return env->NewStringUTF(OBFUSCATE("https://i.imgur.com/SujJ85j.gif"));

    // Base64 html:
    // return env->NewStringUTF("data:image/png;base64, <encoded base64 here>");

    // To disable it, return nullptr. It will use normal image above:
    // return nullptr

    //return env->NewStringUTF(OBFUSCATE_KEY("https://i.imgur.com/SujJ85j.gif", 'u'));
    return nullptr;
}

jobjectArray SettingsList(JNIEnv *env, jobject activityObject) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("Category_Credits"),
            OBFUSCATE("ButtonLink_YouTube_https://youtube.com/@the_king.7800"),
            OBFUSCATE("ButtonLink_Discord_https://discord.com/invite/Kcz3j2mgjK"),
            OBFUSCATE("-6_Button_<font color='red'>Close</font>"),
    };

    int Total_Feature = (sizeof features /
                         sizeof features[0]); //Now you dont have to manually update the number everytime;
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));
    int i;
    for (i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}
