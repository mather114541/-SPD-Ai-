/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

public enum Os {
    Windows,
    Linux,
    MacOsX,
    Android,
    IOS;


    public String getJniPlatform() {
        if (this == Windows) {
            return "win32";
        }
        if (this == Linux) {
            return "linux";
        }
        if (this == MacOsX) {
            return "mac";
        }
        return "";
    }

    public String getLibPrefix() {
        if (this == Linux || this == Android || this == MacOsX) {
            return "lib";
        }
        return "";
    }

    public String getLibExtension() {
        if (this == Windows) {
            return "dll";
        }
        if (this == Linux) {
            return "so";
        }
        if (this == MacOsX) {
            return "dylib";
        }
        if (this == Android) {
            return "so";
        }
        return "";
    }
}

