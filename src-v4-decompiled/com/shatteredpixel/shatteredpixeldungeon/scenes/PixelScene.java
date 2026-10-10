/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tooltip;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Image;
import com.watabou.noosa.Scene;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.noosa.ui.Cursor;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;
import com.watabou.utils.Signal;
import java.util.ArrayList;

public class PixelScene
extends Scene {
    public static final float MIN_WIDTH_P = 135.0f;
    public static final float MIN_HEIGHT_P = 225.0f;
    public static final float MIN_WIDTH_L = 240.0f;
    public static final float MIN_HEIGHT_L = 160.0f;
    public static final float MIN_WIDTH_FULL = 360.0f;
    public static final float MIN_HEIGHT_FULL = 200.0f;
    public static int defaultZoom = 0;
    public static int maxDefaultZoom = 0;
    public static float minZoom;
    public static float maxZoom;
    public static Camera uiCamera;
    public static BitmapText.Font pixelFont;
    protected boolean inGameScene = false;
    private Signal.Listener<KeyEvent> fullscreenListener;
    private Image cursor = null;
    private static ArrayList<Class<? extends Window>> savedWindows;
    private static Class<? extends PixelScene> savedClass;
    public static boolean noFade;

    @Override
    public void create() {
        float scaleFactor;
        float minHeight;
        float minWidth;
        super.create();
        GameScene.scene = null;
        if (!this.inGameScene && InterlevelScene.lastRegion != -1) {
            InterlevelScene.lastRegion = -1;
            TextureCache.clear();
            TitleBackground.reset();
            Holiday.clearCachedHoliday();
        }
        if (SPDSettings.interfaceSize() > 0) {
            minWidth = 360.0f;
            minHeight = 200.0f;
            scaleFactor = 3.75f;
        } else if (PixelScene.landscape()) {
            minWidth = 240.0f;
            minHeight = 160.0f;
            scaleFactor = 2.5f;
        } else {
            minWidth = 135.0f;
            minHeight = 225.0f;
            scaleFactor = 2.5f;
        }
        RectF insets = Game.platform.getSafeInsets(3);
        float w = (float)Game.width - insets.left - insets.right;
        float h = (float)Game.height - insets.top - insets.bottom;
        maxDefaultZoom = (int)Math.min(w / minWidth, h / minHeight);
        maxDefaultZoom = Math.max(2, maxDefaultZoom);
        defaultZoom = SPDSettings.scale();
        if ((double)defaultZoom < Math.ceil(Game.density * 2.0f) || defaultZoom > maxDefaultZoom) {
            defaultZoom = (int)GameMath.gate(2.0f, (int)Math.ceil(Game.density * scaleFactor), maxDefaultZoom);
            if (SPDSettings.interfaceSize() > 0 && defaultZoom < (maxDefaultZoom + 1) / 2) {
                defaultZoom = (maxDefaultZoom + 1) / 2;
            }
        }
        minZoom = 1.0f;
        maxZoom = defaultZoom * 2;
        Camera.reset(new PixelCamera(defaultZoom));
        float uiZoom = defaultZoom;
        uiCamera = Camera.createFullscreen(uiZoom);
        Camera.add(uiCamera);
        pixelFont = BitmapText.Font.colorMarked(TextureCache.get("fonts/pixel_font.png"), 0, " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f");
        PixelScene.pixelFont.baseLine = 6.0f;
        PixelScene.pixelFont.tracking = -1.0f;
        int renderedTextPageSize = defaultZoom <= 3 ? 256 : (defaultZoom <= 8 ? 512 : 1024);
        if (Messages.lang() == Languages.CHI_SMPL || Messages.lang() == Languages.CHI_TRAD || Messages.lang() == Languages.KOREAN || Messages.lang() == Languages.JAPANESE) {
            renderedTextPageSize *= 2;
        }
        Game.platform.setupFontGenerators(renderedTextPageSize, SPDSettings.systemFont());
        Tooltip.resetLastUsedTime();
        Cursor.setCustomCursor(Cursor.Type.DEFAULT, defaultZoom);
    }

    @Override
    public void update() {
        if (DeviceCompat.isDesktop() && this.fullscreenListener == null) {
            this.fullscreenListener = new Signal.Listener<KeyEvent>(){
                private boolean alt;
                private boolean enter;

                @Override
                public boolean onSignal(KeyEvent keyEvent) {
                    if (keyEvent.code == 58) {
                        this.alt = keyEvent.pressed;
                    } else if (keyEvent.code == 66) {
                        this.enter = keyEvent.pressed;
                    }
                    if (this.alt && this.enter) {
                        SPDSettings.fullscreen(!SPDSettings.fullscreen());
                        return true;
                    }
                    return false;
                }
            };
            KeyEvent.addKeyListener(this.fullscreenListener);
        }
        super.update();
        if (!Cursor.isCursorCaptured() && (Math.abs(ControllerHandler.rightStickPosition.x) >= 0.2f || Math.abs(ControllerHandler.rightStickPosition.y) >= 0.2f)) {
            if (!ControllerHandler.controllerPointerActive()) {
                ControllerHandler.setControllerPointer(true);
            }
            int sensitivity = SPDSettings.controllerPointerSensitivity() * 100;
            float xMove = (float)Math.pow(Math.abs(ControllerHandler.rightStickPosition.x), 1.5);
            if (ControllerHandler.rightStickPosition.x < 0.0f) {
                xMove = -xMove;
            }
            float yMove = (float)Math.pow(Math.abs(ControllerHandler.rightStickPosition.y), 1.5);
            if (ControllerHandler.rightStickPosition.y < 0.0f) {
                yMove = -yMove;
            }
            PointF virtualCursorPos = ControllerHandler.getControllerPointerPos();
            virtualCursorPos.x += (float)(defaultZoom * sensitivity) * Game.elapsed * xMove;
            virtualCursorPos.y += (float)(defaultZoom * sensitivity) * Game.elapsed * yMove;
            PointF cameraShift = new PointF();
            if (virtualCursorPos.x < 0.0f) {
                cameraShift.x = virtualCursorPos.x;
                virtualCursorPos.x = 0.0f;
            } else if (virtualCursorPos.x > Camera.main.screenWidth()) {
                cameraShift.x = virtualCursorPos.x - Camera.main.screenWidth();
                virtualCursorPos.x = Camera.main.screenWidth();
            }
            if (virtualCursorPos.y < 0.0f) {
                cameraShift.y = virtualCursorPos.y;
                virtualCursorPos.y = 0.0f;
            } else if (virtualCursorPos.y > Camera.main.screenHeight()) {
                cameraShift.y = virtualCursorPos.y - Camera.main.screenHeight();
                virtualCursorPos.y = Camera.main.screenHeight();
            }
            cameraShift.invScale(Camera.main.zoom);
            cameraShift.x *= Camera.main.edgeScroll.x;
            cameraShift.y *= Camera.main.edgeScroll.y;
            if (cameraShift.length() > 0.0f) {
                Camera.main.shift(cameraShift);
            }
            ControllerHandler.updateControllerPointer(virtualCursorPos, true);
        }
    }

    @Override
    public synchronized void draw() {
        super.draw();
        if (ControllerHandler.controllerPointerActive()) {
            if (this.cursor == null) {
                this.cursor = new Image(Cursor.Type.CONTROLLER.file);
            }
            PointF virtualCursorPos = ControllerHandler.getControllerPointerPos();
            this.cursor.x = virtualCursorPos.x / (float)defaultZoom - this.cursor.width() / 2.0f;
            this.cursor.y = virtualCursorPos.y / (float)defaultZoom - this.cursor.height() / 2.0f;
            this.cursor.camera = uiCamera;
            PixelScene.align(this.cursor);
            this.cursor.draw();
        }
    }

    public synchronized void saveWindows() {
        if (this.members == null) {
            return;
        }
        savedWindows.clear();
        savedClass = this.getClass();
        for (Gizmo g : this.members.toArray(new Gizmo[0])) {
            if (!(g instanceof Window)) continue;
            savedWindows.add((Class<? extends Window>) g.getClass());
        }
    }

    public synchronized void restoreWindows() {
        if (this.getClass().equals(savedClass)) {
            for (Class<? extends Window> w : savedWindows) {
                try {
                    this.add(Reflection.newInstanceUnhandled(w));
                }
                catch (Exception exception) {}
            }
        }
        savedWindows.clear();
    }

    @Override
    public void destroy() {
        super.destroy();
        PointerEvent.clearListeners();
        if (this.fullscreenListener != null) {
            KeyEvent.removeKeyListener(this.fullscreenListener);
        }
        if (this.cursor != null) {
            this.cursor.destroy();
        }
    }

    public static boolean landscape() {
        return SPDSettings.interfaceSize() > 0 || Game.width > Game.height;
    }

    public static RenderedTextBlock renderTextBlock(int size) {
        return PixelScene.renderTextBlock("", size);
    }

    public static RenderedTextBlock renderTextBlock(String text, int size) {
        float scale = DeviceCompat.getRealPixelScaleX();
        RenderedTextBlock result = new RenderedTextBlock(text, size * Math.round((float)defaultZoom * scale));
        result.zoom(1.0f / (float)Math.round((float)defaultZoom * scale));
        return result;
    }

    public static float align(float pos) {
        return (float)Math.round(pos * (float)defaultZoom) / (float)defaultZoom;
    }

    public static float align(Camera camera, float pos) {
        return (float)Math.round(pos * camera.zoom) / camera.zoom;
    }

    public static void align(Visual v) {
        v.x = PixelScene.align(v.x);
        v.y = PixelScene.align(v.y);
    }

    public static void align(Component c) {
        c.setPos(PixelScene.align(c.left()), PixelScene.align(c.top()));
    }

    protected void fadeIn() {
        if (noFade) {
            noFade = false;
        } else {
            this.fadeIn(-16777216, false);
        }
    }

    protected void fadeIn(int color, boolean light) {
        this.add(new Fader(color, light));
    }

    public static void showBadge(final Badges.Badge badge) {
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Scene s = Game.scene();
                if (s != null) {
                    BadgeBanner banner = BadgeBanner.show(badge.image);
                    s.add(banner);
                    float offset = Camera.main.centerOffset.y;
                    int left = PixelScene.uiCamera.width / 2 - 8;
                    left = (int)((float)left - 48.0f * (float)(BadgeBanner.showing.size() - 1) / 2.0f);
                    for (int i = 0; i < BadgeBanner.showing.size(); ++i) {
                        banner = BadgeBanner.showing.get(i);
                        banner.camera = uiCamera;
                        banner.x = PixelScene.align(banner.camera, left);
                        banner.y = PixelScene.align(uiCamera, ((float)PixelScene.uiCamera.height - banner.height) / 2.0f - banner.height / 2.0f - 16.0f - offset);
                        left = (int)((float)left + 48.0f);
                    }
                    WndJournal.last_index = 4;
                    WndJournal.BadgesTab.global = badge.type != Badges.BadgeType.LOCAL;
                }
            }
        });
    }

    public static void shake(float magnitude, float duration) {
        if (rl.RLMode.headless) return;   // headless：无渲染相机，跳过屏幕震动（否则 Camera.main 为 null NPE）
        Camera.main.shake(magnitude *= (float)SPDSettings.screenShake(), duration);
    }

    public RectF getCommonInsets() {
        RectF all = Game.platform.getSafeInsets(3);
        RectF blocking = Game.platform.getSafeInsets(1);
        all.left = blocking.left;
        all.right = blocking.right;
        return all.scale(1.0f / (float)defaultZoom);
    }

    static {
        savedWindows = new ArrayList();
        savedClass = null;
        noFade = false;
    }

    private static class PixelCamera
    extends Camera {
        public PixelCamera(float zoom) {
            super((int)((double)Game.width - Math.ceil((float)Game.width / zoom) * (double)zoom) / 2, (int)((double)Game.height - Math.ceil((float)Game.height / zoom) * (double)zoom) / 2, (int)Math.ceil((float)Game.width / zoom), (int)Math.ceil((float)Game.height / zoom), zoom);
            this.fullScreen = true;
        }

        @Override
        protected void updateMatrix() {
            float sx = PixelScene.align(this, this.scroll.x + this.shakeX);
            float sy = PixelScene.align(this, this.scroll.y + this.shakeY);
            this.matrix[0] = this.zoom * invW2;
            this.matrix[5] = -this.zoom * invH2;
            this.matrix[12] = -1.0f + (float)this.x * invW2 - sx * this.matrix[0];
            this.matrix[13] = 1.0f - (float)this.y * invH2 - sy * this.matrix[5];
        }
    }

    protected static class Fader
    extends ColorBlock {
        private static float FADE_TIME = 1.0f;
        private boolean light;
        private float time;
        private static Fader INSTANCE;

        public Fader(int color, boolean light) {
            super(PixelScene.uiCamera.width, PixelScene.uiCamera.height, color);
            this.light = light;
            this.camera = uiCamera;
            this.alpha(1.0f);
            this.time = FADE_TIME;
            if (INSTANCE != null) {
                INSTANCE.killAndErase();
            }
            INSTANCE = this;
        }

        @Override
        public void update() {
            float f = this.time / FADE_TIME;   // 反编译丢失的初始化：fade 进度
            super.update();
            this.time -= Game.elapsed;
            if (f <= 0.0f) {
                this.alpha(0.0f);
                this.parent.remove(this);
                this.destroy();
                if (INSTANCE == this) {
                    INSTANCE = null;
                }
            } else {
                this.alpha(this.time / FADE_TIME);
            }
        }

        @Override
        public void draw() {
            if (this.light) {
                Blending.setLightMode();
                super.draw();
                Blending.setNormalMode();
            } else {
                super.draw();
            }
        }
    }
}

