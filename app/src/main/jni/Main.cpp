#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <dirent.h>
#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <sys/stat.h>
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.hpp"
#include "Menu/Menu.hpp"
#include "Menu/Jni.hpp"
#include "Includes/Macros.h"
#include "AdPanelTeam.h"
#include "Main.h"
#include "BGFX/Logger.h"
#include "Network/ClientNetwork.h"
#include "Heplers/JniHelper.h"
#include "hooks.h"

#include "Dobby/dobby.h"

#define LibNam OBFUSCATE("libBlockMan.so")

#define Protected OBFUSCATE("libBGFX.so")


uintptr_t UseHex(const char* hexStr) {
    return strtoul(hexStr, nullptr, 16);
}

#define OBF_HEX(name, hex_str) const uintptr_t name = UseHex(OBFUSCATE(hex_str))

struct MemPatches {
    MemoryPatch;
} hexPatches;

JNIEnv* Zenv;
JNIEnv* Aenv;
jclass Aclazz;
jobject Zctx;
JavaVM* g_JavaVM;
const char* defaultScript = nullptr;
bool defaultScriptLoaded = false;




static int (*org_getglobal)(uintptr_t*, const char*) = nullptr;
static void (*org_pushstring)(uintptr_t*, const char*) = nullptr;
static int (*org_pcall)(uintptr_t*, int, int, int) = nullptr;

int (*lua_getglobal)(uintptr_t*, const char*) = nullptr;
void (*lua_pushstring)(uintptr_t*, const char*) = nullptr;
int (*lua_pcall)(uintptr_t*, int, int, int) = nullptr;

uintptr_t* g_luaState = nullptr;

void InjectFunc32bit();
void InjectFunc64bit();
void RunFile(const char* scriptPath);
void RunLua(const char* luaCode);




int hook_getglobal(uintptr_t* L, const char* name) {
    if (L != nullptr) {
        g_luaState = L;
    }
    return org_getglobal ? org_getglobal(L, name) : 0;
}

void hook_pushstring(uintptr_t* L, const char* str) {
    if (L != nullptr) {
        g_luaState = L;
    }
    if (org_pushstring) org_pushstring(L, str);
}

int hook_pcall(uintptr_t* L, int nargs, int nresults, int errfunc) {
    if (L != nullptr) {
        g_luaState = L;
    }
    if (!defaultScriptLoaded && defaultScript != nullptr && nargs == 3) {
        RunFile(defaultScript);
        RunLua(getLuaCredits());
        defaultScriptLoaded = true;
    }
    return org_pcall ? org_pcall(L, nargs, nresults, errfunc) : 0;
}



void RunLua(const char* luaCode) {
    if (!luaCode || strlen(luaCode) == 0) {
        return;
    }
    

    if (!g_luaState) {
        return;
    }

    org_getglobal(g_luaState, "loadstring");
    org_pushstring(g_luaState, luaCode);

    if (org_pcall(g_luaState, 1, 1, 0) != 0) {
        return;
    }

    if (org_pcall(g_luaState, 0, 0, 0) != 0) {
        return;
    }
    BGFX_LOG_INFO("Lua Code Injected!");
}

bool doesExist(const char* path) {
    struct stat buffer;
    return stat(path, &buffer) == 0;
}

void checkFile(const char* path) {
    const char* filename = strrchr(path, '/');
    if (!filename) filename = path;
    else filename += 1;

    if (strcmp(filename, "AdPanelTeam.lua") == 0 && !doesExist(path)) {
        BGFX_LOG_FATAL("`AdPanelTeam.lua` Not found");
        BGFX_LOG_INFO("Crearing `AdPanelTeam.lua` File...");
        const char* scriptsDir = "/sdcard/BGFX/Scripts";

    DIR* dir = opendir(scriptsDir);
    if (!dir) {
        BGFX_LOG_FATAL("Scripts folder not found");
        BGFX_LOG_INFO("Creating Autoexec folder...");
        mkdir(scriptsDir,0);
        BGFX_LOG_INFO("Autoexec folder created!");
    }
        FILE* file = fopen(path, "w");
        if (file) {
            fwrite(getLuaPanel(), sizeof(char), strlen(getLuaPanel()), file);
            fclose(file);
            BGFX_LOG_INFO("`AdPanelTeam.lua` Created!");
        }
    }
}

void RunFile(const char* scriptPath) {
    checkFile(scriptPath);

    auto readLuaScript = [](const char* path) -> char* {
        FILE* file = fopen(path, "r");
        if (!file) return nullptr;

        fseek(file, 0, SEEK_END);
        size_t size = ftell(file);
        rewind(file);

        char* buffer = (char*)malloc(size + 1);
        if (!buffer) {
            fclose(file);
            return nullptr;
        }

        fread(buffer, 1, size, file);
        buffer[size] = '\0';
        fclose(file);
        return buffer;
    };

    char* luaCode = readLuaScript(scriptPath);
    if (!luaCode || strlen(luaCode) == 0) {
        free(luaCode);
        return;
    }
    

    if (!g_luaState) {
        free(luaCode);
        return;
    }

    org_getglobal(g_luaState, "loadstring");
    org_pushstring(g_luaState, luaCode);
    free(luaCode);

    if (org_pcall(g_luaState, 1, 1, 0) != 0) {
        return;
    }

    if (org_pcall(g_luaState, 0, 0, 0) != 0) {
        return;
    }
    
    BGFX_LOG_INFO("Lua File Injected!");
}




bool IsAutoExecEnabled() {
    const char* path = "/sdcard/BGFX/settings.txt";
    FILE* file = fopen(path, "r");
    if (!file) return false;

    char buffer[8];
    fgets(buffer, sizeof(buffer), file);
    fclose(file);

    return strncmp(buffer, "true", 4) == 0;
}


void RunAutoExec() {
    BGFX_LOG_INFO("---------- AutoExecution Start ----------");
    const char* AutoExecDir = "/sdcard/BGFX/Autoexec";

    DIR* dir = opendir(AutoExecDir);
    if (!dir) {
        BGFX_LOG_ERROR("Autoexec folder not found");
        BGFX_LOG_INFO("Creating Autoexec folder...");
        mkdir(AutoExecDir, 0);

        dir = opendir(AutoExecDir);
        if (!dir) {
            BGFX_LOG_ERROR("Failed to open Autoexec folder after creating it");
            return;
        }

        BGFX_LOG_INFO("Autoexec folder created and opened!");
    }

    struct dirent* entry;
    bool foundFile = false;
    while ((entry = readdir(dir)) != nullptr) {
        if (entry->d_type != DT_REG) continue;

        const char* name = entry->d_name;
        const char* ext = strrchr(name, '.');
        if (!ext) continue;

        if (strcmp(ext, ".lua") != 0 && strcmp(ext, ".txt") != 0)
            continue;

        char fullPath[512];
        snprintf(fullPath, sizeof(fullPath), "%s/%s", AutoExecDir, name);

        FILE* file = fopen(fullPath, "r");
        if (!file) continue;

        fseek(file, 0, SEEK_END);
        long size = ftell(file);
        fclose(file);

        if (size > 0) {
            foundFile = true;
            BGFX_LOG_INFO("Running file -> %s", fullPath);
            usleep(500000);
            RunFile(fullPath);
        }
    }

    closedir(dir);

    if (!foundFile) {
        BGFX_LOG_INFO("No executable files (.lua/.txt) found in Autoexec.");
    }

    BGFX_LOG_INFO("---------- AutoExecution End ----------");
}





void InjectFunc32bit() {
sleep(3);

    const char* libName = LibNam;

    auto getLibraryBaseAddress = [](const char* libName) -> uintptr_t {
        FILE* fp = fopen("/proc/self/maps", "r");
        if (!fp) return 0;

        uintptr_t baseAddr = 0;
        char line[512];

        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, libName)) {
                char* end = strchr(line, '-');
                if (end) {
                    *end = '\0';
                    baseAddr = strtoul(line, nullptr, 16);
                    break;
                }
            }
        }

        fclose(fp);
        return baseAddr;
    };

    uintptr_t base = getLibraryBaseAddress(libName);
    if (!base) return;

    OBF_HEX(luar_getglobalOffset,  "10CAF3F"); // search string: package
    OBF_HEX(luar_pushstringOffset, "10CADCD"); // search string: error loading module '%s' from file
    OBF_HEX(luar_pcallOffset,      "10CB685"); // luaL_loadbuffer error
    
    // Note: these strings are helpers to find the new offsets but it's not the offset string itself you'll to scroll until you find the correct offset, it's close to the string.
    
    lua_getglobal = (int (*)(uintptr_t*, const char*))(base + luar_getglobalOffset | 1);
    lua_pushstring = (void (*)(uintptr_t*, const char*))(base + luar_pushstringOffset | 1);
    lua_pcall = (int (*)(uintptr_t*, int, int, int))(base + luar_pcallOffset | 1);


    if (!org_getglobal)
        DobbyHook((void*)lua_getglobal, (void*)hook_getglobal, (void**)&org_getglobal);

    if (!org_pushstring)
        DobbyHook((void*)lua_pushstring, (void*)hook_pushstring, (void**)&org_pushstring);

    if (!org_pcall)
        DobbyHook((void*)lua_pcall, (void*)hook_pcall, (void**)&org_pcall);

    if (!g_luaState) return;
    
    InjectHooks();
    
    BGFX_LOG_INFO("Hooks Installed!");
       
    ClientNetwork::Instance()->connectToServer("172.93.100.26", 6666);
    
if (IsAutoExecEnabled()) {
        BGFX_LOG_INFO("Running AutoExecutions...");
        RunAutoExec();
    } else {
        BGFX_LOG_INFO("AutoExec is disabled");
    }
}




void InjectFunc64bit() {
    sleep(3);

    const char* libName = LibNam;

    auto getLibraryBaseAddress = [](const char* libName) -> uintptr_t {
        FILE* fp = fopen("/proc/self/maps", "r");
        if (!fp) return 0;

        uintptr_t baseAddr = 0;
        char line[512];

        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, libName)) {
                char* end = strchr(line, '-');
                if (end) {
                    *end = '\0';
                    baseAddr = strtoull(line, nullptr, 16);
                    break;
                }
            }
        }

        fclose(fp);
        return baseAddr;
    };

    uintptr_t base = getLibraryBaseAddress(libName);
    if (!base) return;

    OBF_HEX(luar_getglobalOffset,  "19630E0"); // string search: package 
    OBF_HEX(luar_pushstringOffset, "1962DF8"); // search string: error loading module '%s' from file
    OBF_HEX(luar_pcallOffset,      "1963C5C"); // search string: luaL_loadbuffer error
    
    // Note: these strings are helpers to find the new offsets but it's not the offset string itself you'll to scroll until you find the correct offset, it's close to the string.

    lua_getglobal = (int (*)(uintptr_t*, const char*))(base + luar_getglobalOffset);
    lua_pushstring = (void (*)(uintptr_t*, const char*))(base + luar_pushstringOffset);
    lua_pcall = (int (*)(uintptr_t*, int, int, int))(base + luar_pcallOffset);

    if (!org_getglobal)
        DobbyHook((void*)lua_getglobal, (void*)hook_getglobal, (void**)&org_getglobal);

    if (!org_pushstring)
        DobbyHook((void*)lua_pushstring, (void*)hook_pushstring, (void**)&org_pushstring);

    if (!org_pcall)
        DobbyHook((void*)lua_pcall, (void*)hook_pcall, (void**)&org_pcall);

    if (!g_luaState) return;

    InjectHooks();

    BGFX_LOG_INFO("Hooks Installed!");

    ClientNetwork::Instance()->connectToServer("172.93.100.26", 6666);

    if (IsAutoExecEnabled()) {
        BGFX_LOG_INFO("Running AutoExecutions...");
        RunAutoExec();
    } else {
        BGFX_LOG_INFO("AutoExec is disabled");
    }
}




jobjectArray GetFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
    };

    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}


void Changes(JNIEnv* env, jclass clazz, jobject obj, jint featNum, jstring featName,
             jint value, jlong Lvalue, jboolean boolean, jstring str) {
    LOGD(OBFUSCATE("Feature name: %d - %s | Value: = %d | LValue: %lld | Bool: = %d | Text: = %s"),
         featNum, env->GetStringUTFChars(featName, 0), value, Lvalue, boolean,
         str != nullptr ? env->GetStringUTFChars(str, 0) : "");
         
    jclass local = env->FindClass("com/sandboxol/blockmango/EchoesRenderer");
    if (local) {
        Aclazz = (jclass)env->NewGlobalRef(local);
        env->DeleteLocalRef(local);
    }

    switch (featNum) {
        case 20:
            if (boolean) RunLua(getLuaSmoothFpsOn());
            else RunLua(getLuaSmoothFpsOff());
            break;
        case 21:
            if (boolean) RunLua(getLuaRainbowNameOn());
            else RunLua(getLuaRainbowNameOff());
            break;
        case 35:
            if (boolean) RunLua(getLuaDevFlyOn());
            else RunLua(getLuaDevFlyOff());
            break;
        case 27:
            RunLua(env->GetStringUTFChars(str, 0));
            break;
        case 32:
            RunLua(env->GetStringUTFChars(str, 0));
            RunLua("UIHelper.showToast('^2196F3Clipboard Executed')");
            break;
        case 28:
            RunFile(env->GetStringUTFChars(str, 0));
            break;
        case 29:
            defaultScript = env->GetStringUTFChars(str, 0);
            break;
        case 90: {
            const char* path = "/sdcard/BGFX/settings.txt";
            mkdir("/sdcard/BGFX", 0);

            FILE* file = fopen(path, "w");
            if (file) {
                fputs(boolean ? "true" : "false", file);
                fclose(file);
                BGFX_LOG_INFO("settings.txt updated to: %s", boolean ? "true" : "false");
            } else {
                BGFX_LOG_ERROR("Failed to open settings.txt.");
            }
            break;
        }
    }
}



ElfScanner g_il2cppELF;

void hack_thread() {
    while (!isLibraryLoaded(LibNam)) {
        sleep(1);
    }
    
        BGFX_LOG_INFO("%s has been loaded", (const char *) LibNam);
    BGFX_LOG_INFO("Running Injection...");

#if defined(__aarch64__)
    std::thread([] {
        usleep(500000);
        InjectFunc64bit();
    }).detach();
    
#elif defined(__arm__)
    std::thread([] {
        usleep(500000);
        InjectFunc64bit();
    }).detach();

#endif

    do {
        sleep(1);
        g_il2cppELF = ElfScanner::createWithPath(LibNam);
    } while (!g_il2cppELF.isValid());
    
   if (!isLibraryLoaded(Protected)) {
    exit(1);
  }
}


__attribute__((constructor))
void lib_main() {
    std::thread(hack_thread).detach();
}
