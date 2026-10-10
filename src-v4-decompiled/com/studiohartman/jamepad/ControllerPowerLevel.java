/*
 * Decompiled with CFR 0.152.
 */
package com.studiohartman.jamepad;

public enum ControllerPowerLevel {
    POWER_UNKNOWN,
    POWER_EMPTY,
    POWER_LOW,
    POWER_MEDIUM,
    POWER_FULL,
    POWER_WIRED,
    POWER_MAX;


    public static ControllerPowerLevel valueOf(int n) {
        return ControllerPowerLevel.values()[n + 1];
    }
}

