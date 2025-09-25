# BGFXTeam

BGFX - The #1 Blockman Go Executor

BGFX is a Blockman Go Executor and it's the first executor ever to be made for Blockman Go. It's fully open source, supports 64bit and 32bit, and works for version 2.117.1 (com.sandboxol.blockymods.official).

The source won't be getting any updates because it's getting discontinued.


---

# Inject

Clone or download BGFX source, compile it with Visual Studio, Android Studio, or AIDE (Android).

Take the compiled APK and open it, then go to lib/ and extract both arm64 and arm32 libs.

Go to the Blockman Go APK, open assets/ and create two folders:

BGFXArm       (for 32bit)
BGFXArm64     (for 64bit)

Paste libBGFX.so of the respective ABI into these folders.

Next, extract classes.dex from the BGFX Menu APK, rename it as the next dex in Blockman Go APK.
For example, if the last dex in BG APK is classes20.dex, rename BGFX Menu dex to:

classes21.dex

Open all BG's dex files and navigate to:

com/sandboxol/blockmango/BlockManEchoesActivity

(or equivalently com.sandboxol.blockmango.BlockManEchoesActivity)

Go inside onCreate() and add the following:

Update:

invoke-static {p0}, Lcom/update/checker/UpdLoader;->start(Landroid/app/Activity;)V

Main:

invoke-static {p0}, Lcom/android/support/Main;->StartWithoutPermission(Landroid/content/Context;)V

Save and close the dex files.

Open AndroidManifest.xml and add storage and overlay permissions:

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

You are now done with the injection! Simply install the modded APK and enjoy.


---

# Update

Blockman Go updated and you need to update your executor/injector?

1. Open the project source code.


2. Go to Main.cpp.


3. Search for all the offset strings.


4. Open the Blockman Go APK in IDA Pro or Ghidra to find the new addresses/offsets.


5. Update the offsets in your source code accordingly.




---

# Bypass

For Blockman Go bypass, we won’t leak any of our methods.
You will need to handle the bypass yourself.

