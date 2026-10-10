/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.WelcomeScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

public class ShatteredPixelDungeon
extends Game {
    public static final int v1_2_3 = 628;
    public static final int v3_1_1 = 850;
    public static final int v3_2_1 = 861;
    public static final int v3_2_5 = 877;
    public static final int v3_3_0 = 883;
    public static final int v4_0_0 = 909;

    public ShatteredPixelDungeon(PlatformSupport platform) {
        super(sceneClass == null ? WelcomeScene.class : sceneClass, platform);
        Bundle.addAlias(WornKey.class, "com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey");
    }

    @Override
    public void create() {
        super.create();
        ShatteredPixelDungeon.updateSystemUI();
        SPDAction.loadBindings();
        Music.INSTANCE.enable(SPDSettings.music());
        Music.INSTANCE.volume((float)(SPDSettings.musicVol() * SPDSettings.musicVol()) / 100.0f);
        Sample.INSTANCE.enable(SPDSettings.soundFx());
        Sample.INSTANCE.volume((float)(SPDSettings.SFXVol() * SPDSettings.SFXVol()) / 100.0f);
        Sample.INSTANCE.load(Assets.Sounds.all);
    }

    @Override
    public void finish() {
        if (!DeviceCompat.isiOS()) {
            super.finish();
        } else {
            ShatteredPixelDungeon.switchScene(TitleScene.class);
        }
    }

    public static void switchNoFade(Class<? extends PixelScene> c) {
        ShatteredPixelDungeon.switchNoFade(c, null);
    }

    public static void switchNoFade(Class<? extends PixelScene> c, Game.SceneChangeCallback callback) {
        PixelScene.noFade = true;
        ShatteredPixelDungeon.switchScene(c, callback);
    }

    public static void seamlessResetScene(Game.SceneChangeCallback callback) {
        if (ShatteredPixelDungeon.scene() instanceof PixelScene) {
            ((PixelScene)ShatteredPixelDungeon.scene()).saveWindows();
            ShatteredPixelDungeon.switchNoFade(sceneClass, callback);
        } else {
            ShatteredPixelDungeon.resetScene();
        }
    }

    public static void seamlessResetScene() {
        ShatteredPixelDungeon.seamlessResetScene(null);
    }

    @Override
    protected void switchScene() {
        super.switchScene();
        if (this.scene instanceof PixelScene) {
            ((PixelScene)this.scene).restoreWindows();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        if (this.scene instanceof PixelScene && (height != Game.height || width != Game.width)) {
            PixelScene.noFade = true;
            ((PixelScene)this.scene).saveWindows();
        }
        super.resize(width, height);
        this.updateDisplaySize();
    }

    @Override
    public void destroy() {
        super.destroy();
        GameScene.endActorThread();
    }

    public void updateDisplaySize() {
        platform.updateDisplaySize();
    }

    public static void updateSystemUI() {
        platform.updateSystemUI();
    }
}

