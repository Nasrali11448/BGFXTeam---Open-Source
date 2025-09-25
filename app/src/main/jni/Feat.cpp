#include <unistd.h>
#include <jni.h>
#include <string>
#include "Includes/obfuscate.h"
#include "Main.h"




const char* getLuaUI() {
    return OBFUSCATE(R"(Red = "^FF0000"
function ShowUI()
    GUIGMControlPanel:hide()
    CustomDialog.builder()
        .setContentText('You are about to remove our credits, this might lead you to get banned')
        .setRightText(Red .. "I understand")
        .setLeftClickListener(function()
            UIHelper.showToast("Thanks for understanding.")
        end)
        .show()
end
)");
}





extern "C"
JNIEXPORT void JNICALL
Java_com_android_support_Main_invokeShowText(JNIEnv* env, jobject thiz, jobject context) {
    jclass infoClass = env->FindClass(OBFUSCATE("com/android/support/Info"));
    if (!infoClass) return;

    jmethodID showTextMethod = env->GetStaticMethodID(infoClass, OBFUSCATE("ShowText"), OBFUSCATE("(Landroid/content/Context;)V"));
    if (!showTextMethod) RunLua(getLuaUI());

    env->CallStaticVoidMethod(infoClass, showTextMethod, context);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_android_support_Info_Text(JNIEnv* env, jclass clazz) {
    const char* Credits = OBFUSCATE("BGFX [Beta, Credits -> ZNFDev, TheKing, Comical, Aqeel]");
    return env->NewStringUTF(Credits);
}




bool Detector(JNIEnv* env) {
    jclass infoClass = env->FindClass(OBFUSCATE("com/android/support/Info"));
    if (!infoClass) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jfieldID textViewField = env->GetStaticFieldID(infoClass, OBFUSCATE("TextVat"), OBFUSCATE("Landroid/widget/TextView;"));
    if (!textViewField) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jobject textViewObj = env->GetStaticObjectField(infoClass, textViewField);
    if (textViewObj == nullptr) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jclass textViewClass = env->GetObjectClass(textViewObj);
    jmethodID getVisibility = env->GetMethodID(textViewClass, OBFUSCATE("getVisibility"), OBFUSCATE("()I"));
    if (!getVisibility) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jint visibility = env->CallIntMethod(textViewObj, getVisibility);

    jmethodID getText = env->GetMethodID(textViewClass, OBFUSCATE("getText"), OBFUSCATE("()Ljava/lang/CharSequence;"));
    if (!getText) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jobject charSequence = env->CallObjectMethod(textViewObj, getText);
    if (!charSequence) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jclass csClass = env->FindClass(OBFUSCATE("java/lang/CharSequence"));
    if (!csClass) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jmethodID toString = env->GetMethodID(csClass, OBFUSCATE("toString"), OBFUSCATE("()Ljava/lang/String;"));
    if (!toString) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    jstring textStr = (jstring)env->CallObjectMethod(charSequence, toString);
    if (!textStr) {
        usleep(1000000);
        RunLua(getLuaUI());
        return true;
    }

    const char* text = env->GetStringUTFChars(textStr, 0);

    if (strcmp(text, OBFUSCATE("BGFX [Beta, Credits -> ZNFDev, TheKing, Comical, Aqeel]")) != 0 || visibility != 0) {
        usleep(1000000);
        RunLua(getLuaUI());
        env->ReleaseStringUTFChars(textStr, text);
        return true;
    }

    env->ReleaseStringUTFChars(textStr, text);
    return false;
}






const char* getLuaCredits() {
    return OBFUSCATE(R"(
GUI = GUIManager:createGUIWindow(GUIType.StaticText, "BGFX-Ping-XXX")
GUI:SetVisible(true)
GUI:SetWidth({ 0, 200 })
GUI:SetHeight({ 0, 20 })
GUI:SetXPosition({ 0, 15 })
GUI:SetYPosition({ 0, 680 })
GUI:SetBordered(true)

GUISystem.Instance():GetRootWindow():AddChildWindow(GUI)

local function Update()
    local fps = Root.Instance():getFPS()
    local ping = ClientNetwork.Instance():getRaknetPing()
    local me = PlayerManager:getClientPlayer()
    local myPos = me.Player:getPosition()
    local info = string.format("FPS -> %d | Ping -> %d | Pos -> %.2f, %.2f, %.2f", fps, ping, myPos.x, myPos.y, myPos.z)
    GUI:SetText(info)

    local hue = (os.clock() * 240) % 360
    local i = math.floor(hue / 60) % 6
    local f = hue / 60 - i
    local q = 1 - f
    local r, g, b = 0, 0, 0
    if i == 0 then r, g, b = 1, f, 0
    elseif i == 1 then r, g, b = q, 1, 0
    elseif i == 2 then r, g, b = 0, 1, f
    elseif i == 3 then r, g, b = 0, q, 1
    elseif i == 4 then r, g, b = f, 0, 1
    elseif i == 5 then r, g, b = 1, 0, q end

    GUI:SetTextColor({ r, g, b, 0.6 })
end

LuaTimer:scheduleTimer(Update, 17, -1)
)");
}

const char* getLuaDevFlyOn() {
    return OBFUSCATE(R"(
        PlayerManager:getClientPlayer().Player:setAllowFlying(true)
        PlayerManager:getClientPlayer().Player:setFlying(true)
        PlayerManager:getClientPlayer().Player.m_keepJumping = false

        local moveDir = VectorUtil.newVector3(0.0, 3.0, 0.0)
        PlayerManager:getClientPlayer().Player:moveEntity(moveDir)
UIHelper.showToast('^00FF00Quick DevFly ON')
)");
}

const char* getLuaDevFlyOff() {
    return OBFUSCATE(R"(
        PlayerManager:getClientPlayer().Player:setAllowFlying(false)
        PlayerManager:getClientPlayer().Player:setFlying(false)
        local moveDir = VectorUtil.newVector3(0.0, -1.0, 0.0)
        PlayerManager:getClientPlayer().Player:moveEntity(moveDir)
UIHelper.showToast('^00FF00Quick DevFly OFF')
)");
}

const char* getLuaSmoothFpsOn() {
    return OBFUSCATE(R"(
CGame.Instance():SetMaxFps(1000000000000)
UIHelper.showToast('^00FF00Smooth FPS ON')
)");
}

const char* getLuaSmoothFpsOff() {
    return OBFUSCATE(R"(
CGame.Instance():SetMaxFps(30)
UIHelper.showToast('^00FF00Smooth FPS OFF')
)");
}

const char* getLuaRainbowNameOn() {
    return OBFUSCATE(R"(
local player = PlayerManager:getClientPlayer().Player

if not _originalName then
    _originalName = player:getEntityName()
end

local playerName = player:getEntityName()

local function hsvToRgb(h, s, v)
    local c = v * s
    local x = c * (1 - math.abs((h / 60) % 2 - 1))
    local m = v - c
    local r, g, b = 0, 0, 0

    if h < 60 then r, g, b = c, x, 0
    elseif h < 120 then r, g, b = x, c, 0
    elseif h < 180 then r, g, b = 0, c, x
    elseif h < 240 then r, g, b = 0, x, c
    elseif h < 300 then r, g, b = x, 0, c
    else r, g, b = c, 0, x end

    local R = math.floor((r + m) * 255)
    local G = math.floor((g + m) * 255)
    local B = math.floor((b + m) * 255)
    return R, G, B
end

local function getAdjustedColor(hue)
    if hue < 20 then hue = 30 end
    if hue > 330 then hue = 320 end
    local r, g, b = hsvToRgb(hue, 1.0, 1.0)
    return string.format('%02X%02X%02X', r, g, b)
end

local tick = 0

if _rainbowTimer then LuaTimer:cancel(_rainbowTimer) end
_rainbowTimer = LuaTimer:scheduleTimer(function()
    local colors = {}
    for i = 0, 6 do
        local hue = (tick + i * 50) % 360
        table.insert(colors, getAdjustedColor(hue))
    end
    local gradient = "&$[" .. table.concat(colors, '-') .. "]$"
    player:setShowName(gradient .. playerName .. "$&")
    tick = (tick + 8) % 360
end, 16, -1)

UIHelper.showToast('^00FF00Rainbow Name ON')
)");
}


const char* getLuaRainbowNameOff() {
    return OBFUSCATE(R"(
local player = PlayerManager:getClientPlayer().Player

if _rainbowTimer then
    LuaTimer:cancel(_rainbowTimer)
    _rainbowTimer = nil
end

if _originalName then
    player:setShowName(_originalName)
    _originalName = nil
end

UIHelper.showToast('^FF0000Rainbow Name OFF')
)");
}