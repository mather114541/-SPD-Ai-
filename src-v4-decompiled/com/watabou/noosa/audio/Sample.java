/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.watabou.noosa.Game;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public enum Sample {
    INSTANCE;

    protected HashMap<Object, Sound> ids = new HashMap();
    private boolean enabled = true;
    private float globalVolume = 1.0f;
    private static final LinkedList<String> loadingQueue;
    private static final HashSet<DelayedSoundEffect> delayedSFX;

    public synchronized void reset() {
        for (Sound sound : this.ids.values()) {
            sound.dispose();
        }
        this.ids.clear();
        delayedSFX.clear();
    }

    public synchronized void pause() {
        for (Sound sound : this.ids.values()) {
            sound.pause();
        }
    }

    public synchronized void resume() {
        for (Sound sound : this.ids.values()) {
            sound.resume();
        }
    }

    public synchronized void load(String asset) {
        if (asset != null) {
            try {
                Sound newSound = Gdx.audio.newSound(Gdx.files.internal(asset));
                this.ids.put(asset, newSound);
            }
            catch (Exception e) {
                Game.reportException(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(String[] assets) {
        LinkedList<String> linkedList = loadingQueue;
        synchronized (linkedList) {
            for (String asset : assets) {
                if (this.ids.containsKey(asset) || loadingQueue.contains(asset)) continue;
                loadingQueue.add(asset);
            }
        }
    }

    public synchronized void unload(Object src) {
        if (this.ids.containsKey(src)) {
            this.ids.get(src).dispose();
            this.ids.remove(src);
        }
    }

    public long play(Object id) {
        return this.play(id, 1.0f);
    }

    public long play(Object id, float volume) {
        return this.play(id, volume, volume, 1.0f);
    }

    public long play(Object id, float volume, float pitch) {
        return this.play(id, volume, volume, pitch);
    }

    public synchronized long play(Object id, float leftVolume, float rightVolume, float pitch) {
        float volume = Math.max(leftVolume, rightVolume);
        float pan = rightVolume - leftVolume;
        if (this.enabled && this.ids.containsKey(id)) {
            return this.ids.get(id).play(this.globalVolume * volume, pitch, pan);
        }
        return -1L;
    }

    public void playDelayed(Object id, float delay) {
        this.playDelayed(id, delay, 1.0f);
    }

    public void playDelayed(Object id, float delay, float volume) {
        this.playDelayed(id, delay, volume, volume, 1.0f);
    }

    public void playDelayed(Object id, float delay, float volume, float pitch) {
        this.playDelayed(id, delay, volume, volume, pitch);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void playDelayed(Object id, float delay, float leftVolume, float rightVolume, float pitch) {
        if (delay <= 0.0f) {
            this.play(id, leftVolume, rightVolume, pitch);
            return;
        }
        DelayedSoundEffect sfx = new DelayedSoundEffect();
        sfx.id = id;
        sfx.delay = delay;
        sfx.leftVol = leftVolume;
        sfx.rightVol = rightVolume;
        sfx.pitch = pitch;
        HashSet<DelayedSoundEffect> hashSet = delayedSFX;
        synchronized (hashSet) {
            delayedSFX.add(sfx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update() {
        AbstractCollection abstractCollection = loadingQueue;
        synchronized (abstractCollection) {
            if (!loadingQueue.isEmpty()) {
                this.load(loadingQueue.poll());
            }
        }
        abstractCollection = delayedSFX;
        synchronized (abstractCollection) {
            if (delayedSFX.isEmpty()) {
                return;
            }
            for (DelayedSoundEffect sfx : delayedSFX.toArray(new DelayedSoundEffect[0])) {
                sfx.delay -= Game.elapsed;
                if (!(sfx.delay <= 0.0f)) continue;
                delayedSFX.remove(sfx);
                this.play(sfx.id, sfx.leftVol, sfx.rightVol, sfx.pitch);
            }
        }
    }

    public void enable(boolean value) {
        this.enabled = value;
    }

    public void volume(float value) {
        this.globalVolume = value;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    static {
        loadingQueue = new LinkedList();
        delayedSFX = new HashSet();
    }

    private class DelayedSoundEffect {
        Object id;
        float delay;
        float leftVol;
        float rightVol;
        float pitch;

        private DelayedSoundEffect() {
        }
    }
}

