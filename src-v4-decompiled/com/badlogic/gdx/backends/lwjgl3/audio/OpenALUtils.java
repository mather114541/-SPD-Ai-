/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3.audio;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class OpenALUtils {
    static int determineFormat(int channels, int bitDepth) {
        int format;
        block0 : switch (channels) {
            case 1: {
                switch (bitDepth) {
                    case 8: {
                        format = 4352;
                        break block0;
                    }
                    case 16: {
                        format = 4353;
                        break block0;
                    }
                    case 32: {
                        format = 65552;
                        break block0;
                    }
                    case 64: {
                        format = 65554;
                        break block0;
                    }
                }
                throw new GdxRuntimeException("Audio: Bit depth must be 8, 16, 32 or 64.");
            }
            case 2: {
                switch (bitDepth) {
                    case 8: {
                        format = 4354;
                        break block0;
                    }
                    case 16: {
                        format = 4355;
                        break block0;
                    }
                    case 32: {
                        format = 65553;
                        break block0;
                    }
                    case 64: {
                        format = 65555;
                        break block0;
                    }
                }
                throw new GdxRuntimeException("Audio: Bit depth must be 8, 16, 32 or 64.");
            }
            case 4: {
                format = 4613;
                break;
            }
            case 6: {
                format = 4619;
                break;
            }
            case 7: {
                format = 4622;
                break;
            }
            case 8: {
                format = 4625;
                break;
            }
            default: {
                throw new GdxRuntimeException("Audio: Invalid number of channels. Must be mono, stereo, quad, 5.1, 6.1 or 7.1.");
            }
        }
        if (channels >= 4) {
            if (bitDepth == 8) {
                --format;
            } else if (bitDepth == 32) {
                ++format;
            } else if (bitDepth != 16) {
                throw new GdxRuntimeException("Audio: Bit depth must be 8, 16 or 32 when 4+ channels are present.");
            }
        }
        return format;
    }
}

