/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture3DData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;

public class CustomTexture3DData
implements Texture3DData {
    private int width;
    private int height;
    private int depth;
    private int mipMapLevel;
    private int glFormat;
    private int glInternalFormat;
    private int glType;
    private ByteBuffer pixels;

    public CustomTexture3DData(int width, int height, int depth, int mipMapLevel, int glFormat, int glInternalFormat, int glType) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.glFormat = glFormat;
        this.glInternalFormat = glInternalFormat;
        this.glType = glType;
        this.mipMapLevel = mipMapLevel;
    }

    @Override
    public boolean isPrepared() {
        return true;
    }

    @Override
    public void prepare() {
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return this.pixels != null;
    }

    @Override
    public int getInternalFormat() {
        return this.glInternalFormat;
    }

    @Override
    public int getGLType() {
        return this.glType;
    }

    public int getGLFormat() {
        return this.glFormat;
    }

    public int getMipMapLevel() {
        return this.mipMapLevel;
    }

    public ByteBuffer getPixels() {
        if (this.pixels == null) {
            int bytesPerChannel;
            int numChannels;
            if (this.glFormat == 6403 || this.glFormat == 36244 || this.glFormat == 6409 || this.glFormat == 6406) {
                numChannels = 1;
            } else if (this.glFormat == 33319 || this.glFormat == 33320 || this.glFormat == 6410) {
                numChannels = 2;
            } else if (this.glFormat == 6407 || this.glFormat == 36248) {
                numChannels = 3;
            } else if (this.glFormat == 6408 || this.glFormat == 36249) {
                numChannels = 4;
            } else {
                throw new GdxRuntimeException("unsupported glFormat: " + this.glFormat);
            }
            if (this.glType == 5121 || this.glType == 5120) {
                bytesPerChannel = 1;
            } else if (this.glType == 5123 || this.glType == 5122 || this.glType == 5131) {
                bytesPerChannel = 2;
            } else if (this.glType == 5125 || this.glType == 5124 || this.glType == 5126) {
                bytesPerChannel = 4;
            } else {
                throw new GdxRuntimeException("unsupported glType: " + this.glType);
            }
            int bytesPerPixel = numChannels * bytesPerChannel;
            this.pixels = BufferUtils.newByteBuffer(this.width * this.height * this.depth * bytesPerPixel);
        }
        return this.pixels;
    }

    @Override
    public void consume3DData() {
        Gdx.gl30.glTexImage3D(32879, this.mipMapLevel, this.glInternalFormat, this.width, this.height, this.depth, 0, this.glFormat, this.glType, this.pixels);
    }
}

