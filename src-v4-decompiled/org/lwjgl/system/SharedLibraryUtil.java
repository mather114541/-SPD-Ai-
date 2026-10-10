/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.lwjgl.system;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;

public final class SharedLibraryUtil {
    private static native int getLibraryPath(long var0, long var2, int var4);

    @Nullable
    public static String getLibraryPath(long pLib) {
        int maxLen = 256;
        ByteBuffer buffer = MemoryUtil.memAlloc(maxLen);
        try {
            while (true) {
                int len;
                if ((len = SharedLibraryUtil.getLibraryPath(pLib, MemoryUtil.memAddress(buffer), maxLen)) == 0) {
                    String string = null;
                    return string;
                }
                if (len < maxLen) {
                    String string = MemoryUtil.memUTF8(buffer, len - 1);
                    return string;
                }
                maxLen = maxLen * 3 / 2;
                buffer = MemoryUtil.memRealloc(buffer, maxLen);
            }
        }
        finally {
            MemoryUtil.memFree(buffer);
        }
    }
}

