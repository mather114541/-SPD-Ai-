/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Attribute;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Uniform;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class NoosaScript
extends Script {
    public Uniform uCamera;
    public Uniform uModel;
    public Uniform uTex;
    public Uniform uColorM;
    public Uniform uColorA;
    public Attribute aXY;
    public Attribute aUV;
    private Camera lastCamera;
    private static final String SHADER = "uniform mat4 uCamera;\nuniform mat4 uModel;\nattribute vec4 aXYZW;\nattribute vec2 aUV;\nvarying vec2 vUV;\nvoid main() {\n  gl_Position = uCamera * uModel * aXYZW;\n  vUV = aUV;\n}\n//\n#ifdef GL_ES\n  precision mediump float;\n#endif\nvarying vec2 vUV;\nuniform sampler2D uTex;\nuniform vec4 uColorM;\nuniform vec4 uColorA;\nvoid main() {\n  gl_FragColor = texture2D( uTex, vUV ) * uColorM + uColorA;\n}\n";

    public NoosaScript() {
        this.compile(this.shader());
        this.uCamera = this.uniform("uCamera");
        this.uModel = this.uniform("uModel");
        this.uTex = this.uniform("uTex");
        this.uColorM = this.uniform("uColorM");
        this.uColorA = this.uniform("uColorA");
        this.aXY = this.attribute("aXYZW");
        this.aUV = this.attribute("aUV");
        Quad.setupIndices();
        Quad.bindIndices();
    }

    @Override
    public void use() {
        super.use();
        this.aXY.enable();
        this.aUV.enable();
    }

    public void drawElements(FloatBuffer vertices, ShortBuffer indices, int size) {
        ((Buffer)vertices).position(0);
        this.aXY.vertexPointer(2, 4, vertices);
        ((Buffer)vertices).position(2);
        this.aUV.vertexPointer(2, 4, vertices);
        Quad.releaseIndices();
        Gdx.gl20.glDrawElements(4, size, 5123, indices);
        Quad.bindIndices();
    }

    public void drawQuad(FloatBuffer vertices) {
        ((Buffer)vertices).position(0);
        this.aXY.vertexPointer(2, 4, vertices);
        ((Buffer)vertices).position(2);
        this.aUV.vertexPointer(2, 4, vertices);
        Gdx.gl20.glDrawElements(4, Quad.SIZE, 5123, 0);
    }

    public void drawQuad(Vertexbuffer buffer) {
        buffer.updateGLData();
        buffer.bind();
        this.aXY.vertexBuffer(2, 4, 0);
        this.aUV.vertexBuffer(2, 4, 2);
        buffer.release();
        Gdx.gl20.glDrawElements(4, Quad.SIZE, 5123, 0);
    }

    public void drawQuadSet(FloatBuffer vertices, int size) {
        if (size == 0) {
            return;
        }
        ((Buffer)vertices).position(0);
        this.aXY.vertexPointer(2, 4, vertices);
        ((Buffer)vertices).position(2);
        this.aUV.vertexPointer(2, 4, vertices);
        Gdx.gl20.glDrawElements(4, Quad.SIZE * size, 5123, 0);
    }

    public void drawQuadSet(Vertexbuffer buffer, int length, int offset) {
        if (length == 0) {
            return;
        }
        buffer.updateGLData();
        buffer.bind();
        this.aXY.vertexBuffer(2, 4, 0);
        this.aUV.vertexBuffer(2, 4, 2);
        buffer.release();
        Gdx.gl20.glDrawElements(4, Quad.SIZE * length, 5123, Quad.SIZE * 16 / 8 * offset);
    }

    public void lighting(float rm, float gm, float bm, float am, float ra, float ga, float ba, float aa) {
        this.uColorM.value4f(rm, gm, bm, am);
        this.uColorA.value4f(ra, ga, ba, aa);
    }

    public void resetCamera() {
        this.lastCamera = null;
    }

    public void camera(Camera camera) {
        if (camera == null) {
            camera = Camera.main;
        }
        if (camera != this.lastCamera && camera.matrix != null) {
            this.lastCamera = camera;
            this.uCamera.valueM4(camera.matrix);
            if (!camera.fullScreen) {
                Gdx.gl20.glEnable(3089);
                float xScale = DeviceCompat.getRealPixelScaleX();
                float yScale = DeviceCompat.getRealPixelScaleY();
                Gdx.gl20.glScissor(Math.round((float)camera.x * xScale), Math.round((float)(Game.height - camera.screenHeight - camera.y) * yScale), Math.round((float)camera.screenWidth * xScale), Math.round((float)camera.screenHeight * yScale));
            } else {
                Gdx.gl20.glDisable(3089);
            }
        }
    }

    public static NoosaScript get() {
        return Script.use(NoosaScript.class);
    }

    protected String shader() {
        return SHADER;
    }
}

