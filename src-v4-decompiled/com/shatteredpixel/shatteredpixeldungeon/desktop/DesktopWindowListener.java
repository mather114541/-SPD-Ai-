/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopPlatformSupport;
import com.watabou.noosa.audio.Music;

public class DesktopWindowListener
implements Lwjgl3WindowListener {
    @Override
    public void created(Lwjgl3Window lwjgl3Window) {
    }

    @Override
    public void maximized(boolean b) {
        SPDSettings.windowMaximized(b);
        if (b) {
            SPDSettings.windowResolution(DesktopPlatformSupport.previousSizes[1]);
        }
    }

    @Override
    public void iconified(boolean b) {
    }

    @Override
    public void focusLost() {
        if (!SPDSettings.playMusicInBackground()) {
            Music.INSTANCE.pause();
        }
    }

    @Override
    public void focusGained() {
        if (!SPDSettings.playMusicInBackground()) {
            Music.INSTANCE.resume();
        }
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public void filesDropped(String[] strings) {
    }

    @Override
    public void refreshRequested() {
    }
}

