#ifndef MAIN_H
#define MAIN_H

const char* getLuaUI();
const char* getLuaCredits();
const char* getLuaDevFlyOn();
const char* getLuaDevFlyOff();
const char* getLuaSmoothFpsOn();
const char* getLuaSmoothFpsOff();
const char* getLuaRainbowNameOn();
const char* getLuaRainbowNameOff();


void RunLua(const char* luaCode);
bool Detector();

#endif // MAIN_H