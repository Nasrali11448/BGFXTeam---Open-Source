// thanks to shmoo and joeyjurjens for the useful stuff under this comment.
#ifndef ANDROID_MOD_MENU_MACROS_H
#define ANDROID_MOD_MENU_MACROS_H

#include "KittyMemory/MemoryPatch.hpp"
#include "KittyMemory/KittyInclude.hpp"

#if defined(__aarch64__) //Compile for arm64 lib only
#include <And64InlineHook/And64InlineHook.hpp>
#else //Compile for armv7 lib only.
#include <Substrate/SubstrateHook.h>
#include <Substrate/CydiaSubstrate.h>
#endif

void hook(void *offset, void* ptr, void **orig)
{
#if defined(__aarch64__)
    A64HookFunction(offset, ptr, orig);
#else
    MSHookFunction(offset, ptr, orig);
#endif
}

#define HOOK(lib, offset, ptr, orig) hook((void *)getAbsoluteAddress(lib, offset), (void *)ptr, (void **)&orig)
#define HOOK_NO_ORIG(lib, offset, ptr) hook((void *)getAbsoluteAddress(lib, offset), (void *)ptr, NULL)
#define HOOKSYM(lib, sym, ptr, org) hook(dlsym(dlopen(lib, 4), sym), (void *)ptr, (void **)&org)
#define HOOKSYM_NO_ORIG(lib, sym, ptr) hook(dlsym(dlopen(lib, 4), sym), (void *)ptr, NULL)

// Patching a offset without switch.

std::map<uint64_t, MemoryPatch> g_patches;

void patchOffset(const char *libName, uint64_t offset, std::string value, bool isOn)
{
    ElfScanner g_il2cppELF = ElfScanner::createWithPath(libName);
    uintptr_t il2cppBase = g_il2cppELF.base();

    KittyUtils::String::Trim(value);

    auto iequals = [](const std::string& a, const std::string& b){
        if (a.size()!=b.size()) return false;
        for (size_t i=0;i<a.size();++i) if (std::tolower((unsigned char)a[i])!=std::tolower((unsigned char)b[i])) return false;
        return true;
    };

#if defined(__aarch64__)
    static const char* RET_TRUE  = "20 00 80 52 C0 03 5F D6";
    static const char* RET_FALSE = "00 00 80 52 C0 03 5F D6";
    static const char* JUST_RET  = "C0 03 5F D6";
#else
    static const char* RET_TRUE  = "01 00 A0 E3 1E FF 2F E1";
    static const char* RET_FALSE = "00 00 A0 E3 1E FF 2F E1";
    static const char* JUST_RET  = "1E FF 2F E1";
#endif

    if (iequals(value, "true") || iequals(value, "rettrue") || iequals(value, "return_true")) {
        value = RET_TRUE;
    } else if (iequals(value, "false") || iequals(value, "retfalse") || iequals(value, "return_false")) {
        value = RET_FALSE;
    } else if (iequals(value, "ret") || iequals(value, "return")) {
        value = JUST_RET;
    } else if (!KittyUtils::String::ValidateHex(value)) {
        int32_t iv = std::stoi(value);
        value = KittyUtils::data2Hex(&iv, sizeof(iv));
    }

    if (g_patches.find(offset) == g_patches.end()) {
    g_patches[offset] = MemoryPatch::createWithHex(il2cppBase + offset, value);
}

    MemoryPatch &patch = g_patches[offset];
    if (!patch.isValid()) {
        LOGE(OBFUSCATE("Failing offset: 0x%llx, please re-check the value/hex"), offset);
        return;
    }

    if (isOn) {
        if (!patch.Modify())
            LOGE(OBFUSCATE("Something went wrong while patching offset: 0x%llx"), offset);
    } else {
        if (!patch.Restore())
            LOGE(OBFUSCATE("Something went wrong while restoring offset: 0x%llx"), offset);
    }
}

void patchOffsetSym(uintptr_t offset, std::string hexBytes)
{
    MemoryPatch patch = MemoryPatch::createWithHex(offset, hexBytes);
    if (!patch.isValid())
    {
        LOGE(OBFUSCATE("Failing offset: 0x%llu, please re-check the hex you entered."), offset);
        return;
    }
    if (!patch.Modify())
    {
        LOGE(OBFUSCATE("Something went wrong while patching this offset: 0x%llu"), offset);
        return;
    }
}

#define PATCH(lib, offset, hex) patchOffset(lib, offset, hex)
#define PATCH_SYM(lib, sym, hex) patchOffset(dlsym(dlopen(lib, 4), sym)), hex), true)

#endif //ANDROID_MOD_MENU_MACROS_H