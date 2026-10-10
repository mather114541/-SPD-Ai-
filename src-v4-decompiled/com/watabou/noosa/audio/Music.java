/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public enum Music {
    INSTANCE;

    private com.badlogic.gdx.audio.Music player;
    private String lastPlayed;
    private boolean looping;
    private boolean enabled = true;
    private float volume = 1.0f;
    private float fadeTime = -1.0f;
    private float fadeTotal = -1.0f;
    private Callback onFadeOut = null;
    String[] trackList;
    float[] trackChances;
    private final ArrayList<String> trackQueue = new ArrayList();
    private HashMap<String, com.badlogic.gdx.audio.Music> trackPlayers = new HashMap();
    boolean shuffle = false;
    private Music.OnCompletionListener trackLooper = new Music.OnCompletionListener(){

        @Override
        public void onCompletion(com.badlogic.gdx.audio.Music music) {
            if (Music.this.fadeTotal == -1.0f) {
                Music.this.playNextTrack(music);
            }
        }
    };
    private boolean paused = false;

    public synchronized void play(String assetName, boolean looping) {
        if (assetName != null && DeviceCompat.isiOS()) {
            assetName = assetName.replace(".ogg", ".mp3");
        }
        if (this.player != null && this.lastPlayed != null && this.lastPlayed.equals(assetName)) {
            this.player.setVolume(this.volumeWithFade());
            if (!this.isPlaying()) {
                this.player.play();
            }
            return;
        }
        this.reset();
        this.lastPlayed = assetName;
        this.trackList = null;
        this.looping = looping;
        this.shuffle = false;
        if (!this.enabled || assetName == null) {
            return;
        }
        this.play(assetName, null);
    }

    public synchronized void playTracks(String[] tracks, float[] chances, boolean shuffle) {
        int i;
        if (tracks == null || tracks.length == 0 || tracks.length != chances.length) {
            this.stop();
            return;
        }
        if (tracks != null && DeviceCompat.isiOS()) {
            for (i = 0; i < tracks.length; ++i) {
                tracks[i] = tracks[i].replace(".ogg", ".mp3");
            }
        }
        if (this.player != null && this.trackList != null && tracks.length == this.trackList.length) {
            boolean sameList = false;
            for (int ofs = 0; ofs < tracks.length; ++ofs) {
                sameList = true;
                for (int j = 0; j < tracks.length; ++j) {
                    int i2 = (j + ofs) % tracks.length;
                    if (tracks[i2].equals(this.trackList[j]) && chances[i2] == this.trackChances[j]) continue;
                    sameList = false;
                    break;
                }
                if (sameList) break;
            }
            if (sameList) {
                this.player.setVolume(this.volumeWithFade());
                if (!this.isPlaying()) {
                    this.player.play();
                }
                return;
            }
        }
        this.reset();
        this.lastPlayed = null;
        this.trackList = tracks;
        this.trackChances = chances;
        this.trackQueue.clear();
        for (i = 0; i < this.trackList.length; ++i) {
            this.createPlayer(this.trackList[i]);
            if (!(Random.Float() < this.trackChances[i])) continue;
            this.trackQueue.add(this.trackList[i]);
        }
        this.looping = false;
        this.shuffle = shuffle;
        if (!this.enabled || this.trackQueue.isEmpty()) {
            return;
        }
        this.play(this.trackQueue.remove(0), this.trackLooper);
    }

    public synchronized void fadeOut(float duration, Callback onComplete) {
        if (this.fadeTotal == -1.0f) {
            this.fadeTotal = duration;
            this.fadeTime = 0.0f;
        } else {
            this.fadeTime = this.fadeTime / this.fadeTotal * duration;
            this.fadeTotal = duration;
        }
        this.onFadeOut = onComplete;
    }

    public synchronized void update() {
        if (this.fadeTotal > 0.0f && !this.paused) {
            this.fadeTime += Game.elapsed;
            if (this.player != null) {
                this.player.setVolume(this.volumeWithFade());
            }
            if (this.fadeTime >= this.fadeTotal) {
                this.fadeTotal = -1.0f;
                this.fadeTime = -1.0f;
                if (this.onFadeOut != null) {
                    this.onFadeOut.call();
                }
            }
        }
    }

    private synchronized void playNextTrack(com.badlogic.gdx.audio.Music music) {
        if (this.trackList == null || this.trackList.length == 0 || music != this.player || this.player.isLooping()) {
            return;
        }
        this.stop();
        if (this.trackQueue.isEmpty()) {
            for (int i = 0; i < this.trackList.length; ++i) {
                if (!(Random.Float() < this.trackChances[i])) continue;
                this.trackQueue.add(this.trackList[i]);
            }
            if (this.shuffle) {
                Collections.shuffle(this.trackQueue);
            }
        }
        if (!this.enabled || this.trackQueue.isEmpty()) {
            return;
        }
        this.play(this.trackQueue.remove(0), this.trackLooper);
    }

    private synchronized void play(String track, Music.OnCompletionListener listener) {
        try {
            this.fadeTotal = -1.0f;
            this.fadeTime = -1.0f;
            this.player = this.createPlayer(track);
            this.player.setLooping(this.looping);
            this.player.setVolume(this.volumeWithFade());
            if (!this.paused) {
                this.player.play();
            }
            if (listener != null) {
                this.player.setOnCompletionListener(listener);
            } else {
                this.player.setOnCompletionListener(null);
            }
        }
        catch (Exception e) {
            Game.reportException(e);
            this.player = null;
        }
    }

    public synchronized void end() {
        this.lastPlayed = null;
        this.trackList = null;
        this.stop();
    }

    public synchronized boolean paused() {
        return this.paused;
    }

    public synchronized void pause() {
        this.paused = true;
        if (this.player != null) {
            this.player.pause();
        }
    }

    public synchronized void resume() {
        this.paused = false;
        if (this.player != null) {
            this.player.play();
            this.player.setLooping(this.looping);
        }
    }

    public synchronized void stop() {
        if (this.player != null) {
            this.player.stop();
            this.player = null;
        }
    }

    public synchronized void reset() {
        if (this.player != null) {
            this.player.stop();
            this.player = null;
        }
        for (com.badlogic.gdx.audio.Music cached : this.trackPlayers.values()) {
            cached.dispose();
        }
        this.trackPlayers.clear();
    }

    private com.badlogic.gdx.audio.Music createPlayer(String asset) {
        if (this.trackPlayers.containsKey(asset)) {
            return this.trackPlayers.get(asset);
        }
        com.badlogic.gdx.audio.Music player = Gdx.audio.newMusic(Gdx.files.internal(asset));
        this.trackPlayers.put(asset, player);
        return player;
    }

    public synchronized void volume(float value) {
        this.volume = value;
        if (this.player != null) {
            this.player.setVolume(this.volumeWithFade());
        }
    }

    private synchronized float volumeWithFade() {
        if (this.fadeTotal > 0.0f) {
            return Math.max(0.0f, this.volume * ((this.fadeTotal - this.fadeTime) / this.fadeTotal));
        }
        return this.volume;
    }

    public synchronized boolean isPlaying() {
        return this.player != null && this.player.isPlaying();
    }

    public synchronized void enable(boolean value) {
        this.enabled = value;
        if (this.isPlaying() && !value) {
            this.stop();
        } else if (!this.isPlaying() && value) {
            if (this.trackList != null) {
                this.playTracks(this.trackList, this.trackChances, this.shuffle);
            } else if (this.lastPlayed != null) {
                this.play(this.lastPlayed, this.looping);
            }
        }
    }

    public synchronized boolean isEnabled() {
        return this.enabled;
    }
}

