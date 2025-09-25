/*

void (*orig_nativeInitGame)(
    jobject assetManager, jfloat arg1, jstring arg2, jlong arg3, jstring arg4, jstring arg5,
    jint arg6, jlong arg7, jstring arg8, jstring arg9, jstring arg10, jstring arg11,
    jstring arg12, jstring arg13, jstring arg14, jint arg15, jint arg16, jstring arg17,
    jboolean arg18, jboolean arg19, jstring arg20, jstring arg21, jstring arg22,
    jstring arg23, jstring arg24, jstring arg25, jstring arg26
);

void hook_nativeInitGame(
    jobject assetManager, jfloat arg1, jstring arg2, jlong arg3, jstring arg4, jstring arg5,
    jint arg6, jlong arg7, jstring arg8, jstring arg9, jstring arg10, jstring arg11,
    jstring arg12, jstring arg13, jstring arg14, jint arg15, jint arg16, jstring arg17,
    jboolean arg18, jboolean arg19, jstring arg20, jstring arg21, jstring arg22,
    jstring arg23, jstring arg24, jstring arg25, jstring arg26
) {
    BGFX_LOG_DEBUG("Hook nativeInitGame called!");

    JNIEnv* env = JniHelper::getEnv();
    const char* nick = env->GetStringUTFChars(arg2, 0);
    BGFX_LOG_DEBUG("Nick -> %s", nick);

    orig_nativeInitGame(
        assetManager, arg1, arg2, arg3, arg4, arg5,
        arg6, arg7, arg8, arg9, arg10, arg11,
        arg12, arg13, arg14, arg15, arg16, arg17,
        arg18, arg19, arg20, arg21, arg22,
        arg23, arg24, arg25, arg26
    );
}

void ConfigHooks() {
    

    void* methodAddr = (void*)(getAbsoluteAddress("libBlockMan.so", 0x673CC4) | 1);
    BGFX_LOG_DEBUG("e -> %p", methodAddr);
    MSHookFunction((void*)methodAddr, (void*)&hook_nativeInitGame, (void**)&orig_nativeInitGame);
}

*/

void InjectHooks() {
//    ConfigHooks();
}

// Unused code (Not working)