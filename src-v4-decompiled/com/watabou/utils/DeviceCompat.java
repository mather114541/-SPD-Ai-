/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.watabou.noosa.Game;

public class DeviceCompat {
    public static int getPlatformVersion() {
        return Gdx.app.getVersion();
    }

    public static boolean isAndroid() {
        return SharedLibraryLoader.os == Os.Android;
    }

    public static boolean isiOS() {
        return SharedLibraryLoader.os == Os.IOS;
    }

    public static boolean isDesktop() {
        return SharedLibraryLoader.os == Os.Windows || SharedLibraryLoader.os == Os.MacOsX || SharedLibraryLoader.os == Os.Linux;
    }

    public static boolean hasHardKeyboard() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);
    }

    public static boolean isDebug() {
        return Game.version.contains("INDEV");
    }

    public static void log(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    public static float getRealPixelScaleX() {
        return (float)Gdx.graphics.getBackBufferWidth() / (float)Game.width;
    }

    public static float getRealPixelScaleY() {
        return (float)Gdx.graphics.getBackBufferHeight() / (float)Game.height;
    }
}

