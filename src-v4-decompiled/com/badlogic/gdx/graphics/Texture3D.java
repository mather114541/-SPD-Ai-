/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture3DData;
import com.badlogic.gdx.graphics.glutils.CustomTexture3DData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import java.util.Map;

public class Texture3D
extends GLTexture {
    static final Map<Application, Array<Texture3D>> managedTexture3Ds = new HashMap<Application, Array<Texture3D>>();
    private Texture3DData data;
    protected Texture.TextureWrap rWrap = Texture.TextureWrap.ClampToEdge;

    public Texture3D(int width, int height, int depth, int glFormat, int glInternalFormat, int glType) {
        this(new CustomTexture3DData(width, height, depth, 0, glFormat, glInternalFormat, glType));
    }

    public Texture3D(Texture3DData data) {
        super(32879, Gdx.gl.glGenTexture());
        if (Gdx.gl30 == null) {
            throw new GdxRuntimeException("Texture3D requires a device running with GLES 3.0 compatibilty");
        }
        this.load(data);
        if (data.isManaged()) {
            Texture3D.addManagedTexture(Gdx.app, this);
        }
    }

    private void load(Texture3DData data) {
        if (this.data != null && data.isManaged() != this.data.isManaged()) {
            throw new GdxRuntimeException("New data must have the same managed status as the old data");
        }
        this.data = data;
        this.bind();
        if (!data.isPrepared()) {
            data.prepare();
        }
        data.consume3DData();
        this.setFilter(this.minFilter, this.magFilter);
        this.setWrap(this.uWrap, this.vWrap, this.rWrap);
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }

    public Texture3DData getData() {
        return this.data;
    }

    public void upload() {
        this.bind();
        this.data.consume3DData();
    }

    @Override
    public int getWidth() {
        return this.data.getWidth();
    }

    @Override
    public int getHeight() {
        return this.data.getHeight();
    }

    @Override
    public int getDepth() {
        return this.data.getDepth();
    }

    @Override
    public boolean isManaged() {
        return this.data.isManaged();
    }

    @Override
    protected void reload() {
        if (!this.isManaged()) {
            throw new GdxRuntimeException("Tried to reload an unmanaged TextureArray");
        }
        this.glHandle = Gdx.gl.glGenTexture();
        this.load(this.data);
    }

    private static void addManagedTexture(Application app, Texture3D texture) {
        Array<Texture3D> managedTextureArray = managedTexture3Ds.get(app);
        if (managedTextureArray == null) {
            managedTextureArray = new Array();
        }
        managedTextureArray.add(texture);
        managedTexture3Ds.put(app, managedTextureArray);
    }

    public static void clearAllTextureArrays(Application app) {
        managedTexture3Ds.remove(app);
    }

    public static void invalidateAllTextureArrays(Application app) {
        Array<Texture3D> managedTextureArray = managedTexture3Ds.get(app);
        if (managedTextureArray == null) {
            return;
        }
        for (int i = 0; i < managedTextureArray.size; ++i) {
            Texture3D textureArray = managedTextureArray.get(i);
            textureArray.reload();
        }
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed TextureArrays/app: { ");
        for (Application app : managedTexture3Ds.keySet()) {
            builder.append(Texture3D.managedTexture3Ds.get((Object)app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedTextures3D() {
        return Texture3D.managedTexture3Ds.get((Object)Gdx.app).size;
    }

    public void setWrap(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureWrap r) {
        this.rWrap = r;
        super.setWrap(u, v);
        Gdx.gl.glTexParameteri(this.glTarget, 32882, r.getGLEnum());
    }

    public void unsafeSetWrap(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureWrap r, boolean force) {
        this.unsafeSetWrap(u, v, force);
        if (r != null && (force || this.rWrap != r)) {
            Gdx.gl.glTexParameteri(this.glTarget, 32882, u.getGLEnum());
            this.rWrap = r;
        }
    }

    public void unsafeSetWrap(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureWrap r) {
        this.unsafeSetWrap(u, v, r, false);
    }
}

