/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.TimeUtils;
import com.watabou.glscripts.Script;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.input.ControllerHandler;
import com.watabou.input.InputHandler;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Reflection;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Game
implements ApplicationListener {
    public static Game instance;
    public static int width;
    public static int height;
    public static float density;
    public static String version;
    public static int versionCode;
    protected Scene scene;
    protected Scene requestedScene;
    protected boolean requestedReset = true;
    protected SceneChangeCallback onChange;
    protected static Class<? extends Scene> sceneClass;
    public static float timeScale;
    public static float elapsed;
    public static float timeTotal;
    public static long realTime;
    public static InputHandler inputHandler;
    public static PlatformSupport platform;
    private GLVersion versionContextRef;
    private boolean justResumed = true;

    public Game(Class<? extends Scene> c, PlatformSupport platform) {
        sceneClass = c;
        instance = this;
        Game.platform = platform;
    }

    @Override
    public void create() {
        float reportedHeight;
        float reportedWidth;
        int dispHeight;
        int dispWidth;
        density = Gdx.graphics.getDensity();
        if (density == Float.POSITIVE_INFINITY) {
            density = 0.625f;
        } else if (DeviceCompat.isDesktop() && (dispWidth = Gdx.graphics.getDisplayMode().width) > (dispHeight = Gdx.graphics.getDisplayMode().height) != (reportedWidth = (float)dispWidth / Gdx.graphics.getPpiX()) > (reportedHeight = (float)dispHeight / Gdx.graphics.getPpiY())) {
            float realPpiX = (float)dispWidth / reportedHeight;
            density = realPpiX / 160.0f;
        }
        inputHandler = new InputHandler(Gdx.input);
        if (ControllerHandler.controllersSupported()) {
            Controllers.addListener(new ControllerHandler());
        }
        this.versionContextRef = Gdx.graphics.getGLVersion();
        Blending.useDefault();
        TextureCache.reload();
        Vertexbuffer.reload();
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        if (this.versionContextRef != Gdx.graphics.getGLVersion()) {
            this.versionContextRef = Gdx.graphics.getGLVersion();
            Blending.useDefault();
            TextureCache.reload();
            Vertexbuffer.reload();
        }
        if (height != Game.height || width != Game.width) {
            Game.width = width;
            Game.height = height;
            Game.resetScene();
        }
    }

    @Override
    public void render() {
        if (instance != this) {
            this.finish();
            return;
        }
        if (this.justResumed) {
            this.justResumed = false;
            if (DeviceCompat.isAndroid()) {
                return;
            }
        }
        NoosaScript.get().resetCamera();
        NoosaScriptNoLighting.get().resetCamera();
        Gdx.gl.glDisable(3089);
        Gdx.gl.glClear(16384);
        this.draw();
        Gdx.gl.glDisable(3089);
        this.step();
    }

    @Override
    public void pause() {
        if (this.scene != null) {
            this.scene.onPause();
        }
        Script.reset();
    }

    @Override
    public void resume() {
        this.justResumed = true;
    }

    public void finish() {
        Gdx.app.exit();
    }

    public void destroy() {
        if (this.scene != null) {
            this.scene.destroy();
            this.scene = null;
        }
        sceneClass = null;
        Music.INSTANCE.reset();
        Sample.INSTANCE.reset();
    }

    @Override
    public void dispose() {
        this.destroy();
    }

    public static void resetScene() {
        Game.switchScene(sceneClass);
    }

    public static void switchScene(Class<? extends Scene> c) {
        Game.switchScene(c, null);
    }

    public static void switchScene(Class<? extends Scene> c, SceneChangeCallback callback) {
        sceneClass = c;
        Game.instance.requestedReset = true;
        Game.instance.onChange = callback;
    }

    public static Scene scene() {
        if (Game.instance == null) return null;
        return Game.instance.scene;
    }

    public static boolean switchingScene() {
        return Game.instance.requestedReset;
    }

    protected void step() {
        if (this.requestedReset) {
            this.requestedReset = false;
            this.requestedScene = Reflection.newInstance(sceneClass);
            if (this.requestedScene != null) {
                this.switchScene();
            }
        }
        this.update();
    }

    protected void draw() {
        if (this.scene != null) {
            this.scene.draw();
        }
    }

    protected void switchScene() {
        Camera.reset();
        if (this.scene != null) {
            this.scene.destroy();
        }
        Vertexbuffer.clear();
        this.scene = this.requestedScene;
        if (this.onChange != null) {
            this.onChange.beforeCreate();
        }
        this.scene.create();
        if (this.onChange != null) {
            this.onChange.afterCreate();
        }
        this.onChange = null;
        elapsed = 0.0f;
        timeScale = 1.0f;
        timeTotal = 0.0f;
    }

    protected void update() {
        float frameDelta = Math.min(0.2f, Gdx.graphics.getDeltaTime());
        elapsed = timeScale * frameDelta;
        timeTotal += elapsed;
        realTime = TimeUtils.millis();
        inputHandler.processAllEvents();
        Music.INSTANCE.update();
        Sample.INSTANCE.update();
        this.scene.update();
        Camera.updateAll();
    }

    public static void reportException(Throwable tr) {
        if (instance != null && Gdx.app != null) {
            instance.logException(tr);
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            pw.flush();
            System.err.println(sw.toString());
        }
    }

    protected void logException(Throwable tr) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        Gdx.app.error("GAME", sw.toString());
    }

    public static void runOnRenderThread(final Callback c) {
        Gdx.app.postRunnable(new Runnable(){

            @Override
            public void run() {
                c.call();
            }
        });
    }

    public static void vibrate(int milliseconds) {
        if (platform.supportsVibration()) {
            platform.vibrate(milliseconds);
        }
    }

    static {
        density = 1.0f;
        timeScale = 1.0f;
        elapsed = 0.0f;
        timeTotal = 0.0f;
        realTime = 0L;
    }

    public static interface SceneChangeCallback {
        public void beforeCreate();

        public void afterCreate();
    }
}

