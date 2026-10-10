/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.NinePatch;

public class Chrome {
    public static NinePatch get(Type type) {
        String Asset = "interfaces/chrome.png";
        switch (type.ordinal()) {
            case 4: {
                return new NinePatch(Asset, 0, 0, 20, 20, 6);
            }
            case 5: {
                return new NinePatch(Asset, 86, 0, 22, 22, 7);
            }
            case 0: {
                return new NinePatch(Asset, 20, 0, 9, 9, 4);
            }
            case 1: 
            case 8: {
                return new NinePatch(Asset, 20, 9, 9, 9, 4);
            }
            case 2: {
                return new NinePatch(Asset, 29, 9, 9, 9, 4);
            }
            case 3: {
                return new NinePatch(Asset, 29, 0, 9, 9, 4);
            }
            case 6: {
                return new NinePatch(Asset, 38, 0, 6, 6, 2);
            }
            case 7: {
                return new NinePatch(Asset, 38, 6, 6, 6, 2);
            }
            case 9: {
                return new NinePatch(Asset, 22, 18, 16, 14, 3);
            }
            case 10: {
                return new NinePatch(Asset, 0, 32, 32, 32, 13);
            }
            case 11: {
                return new NinePatch(Asset, 32, 32, 32, 32, 5, 11, 5, 11);
            }
            case 12: {
                return new NinePatch(Asset, 64, 0, 20, 20, 6);
            }
            case 13: {
                return new NinePatch(Asset, 65, 22, 8, 13, 3, 7, 3, 5);
            }
            case 14: {
                return new NinePatch(Asset, 75, 22, 8, 13, 3, 7, 3, 5);
            }
            case 15: {
                return new NinePatch(Asset, 45, 0, 1, 1, 0, 0, 0, 0);
            }
        }
        return null;
    }

    public static enum Type {
        TOAST,
        TOAST_TR,
        TOAST_TR_HEAVY,
        TOAST_WHITE,
        WINDOW,
        WINDOW_SILVER,
        RED_BUTTON,
        GREY_BUTTON,
        GREY_BUTTON_TR,
        TAG,
        GEM,
        SCROLL,
        TAB_SET,
        TAB_SELECTED,
        TAB_UNSELECTED,
        BLANK;

    }
}

