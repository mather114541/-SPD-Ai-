/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Quad {
    public static final short[] VALUES = new short[]{0, 1, 2, 0, 2, 3};
    public static final int SIZE = VALUES.length;
    private static ShortBuffer indices;
    private static int indexSize;
    private static int bufferIndex;

    public static FloatBuffer create() {
        return ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static FloatBuffer createSet(int size) {
        return ByteBuffer.allocateDirect(size * 16 * 32 / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static void setupIndices() {
        ShortBuffer indices = Quad.getIndices(Short.MAX_VALUE);
        if (bufferIndex == -1) {
            bufferIndex = Gdx.gl.glGenBuffer();
        }
        Gdx.gl.glBindBuffer(34963, bufferIndex);
        Gdx.gl.glBufferData(34963, indices.capacity() * 2, indices, 35044);
        Gdx.gl.glBindBuffer(34963, 0);
    }

    public static void bindIndices() {
        Gdx.gl.glBindBuffer(34963, bufferIndex);
    }

    public static void releaseIndices() {
        Gdx.gl.glBindBuffer(34963, 0);
    }

    public static ShortBuffer getIndices(int size) {
        if (size > indexSize) {
            indexSize = size;
            indices = ByteBuffer.allocateDirect(size * SIZE * 16 / 8).order(ByteOrder.nativeOrder()).asShortBuffer();
            short[] values = new short[size * 6];
            int pos = 0;
            int limit = size * 4;
            for (int ofs = 0; ofs < limit; ofs += 4) {
                values[pos++] = (short)(ofs + 0);
                values[pos++] = (short)(ofs + 1);
                values[pos++] = (short)(ofs + 2);
                values[pos++] = (short)(ofs + 0);
                values[pos++] = (short)(ofs + 2);
                values[pos++] = (short)(ofs + 3);
            }
            indices.put(values);
            ((Buffer)indices).position(0);
        }
        return indices;
    }

    public static void fill(float[] v, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        v[0] = x1;
        v[1] = y1;
        v[2] = u1;
        v[3] = v1;
        v[4] = x2;
        v[5] = y1;
        v[6] = u2;
        v[7] = v1;
        v[8] = x2;
        v[9] = y2;
        v[10] = u2;
        v[11] = v2;
        v[12] = x1;
        v[13] = y2;
        v[14] = u1;
        v[15] = v2;
    }

    public static void fillXY(float[] v, float x1, float x2, float y1, float y2) {
        v[0] = x1;
        v[1] = y1;
        v[4] = x2;
        v[5] = y1;
        v[8] = x2;
        v[9] = y2;
        v[12] = x1;
        v[13] = y2;
    }

    public static void fillUV(float[] v, float u1, float u2, float v1, float v2) {
        v[2] = u1;
        v[3] = v1;
        v[6] = u2;
        v[7] = v1;
        v[10] = u2;
        v[11] = v2;
        v[14] = u1;
        v[15] = v2;
    }

    static {
        indexSize = 0;
        bufferIndex = -1;
    }
}

