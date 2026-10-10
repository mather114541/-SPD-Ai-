/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

public enum Architecture {
    x86,
    ARM,
    RISCV,
    LOONGARCH;


    public String toSuffix() {
        if (this == x86) {
            return "";
        }
        return this.name().toLowerCase();
    }

    public static enum Bitness {
        _32,
        _64,
        _128;


        public String toSuffix() {
            if (this == _32) {
                return "";
            }
            return this.name().substring(1);
        }
    }
}

