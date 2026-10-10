/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALUtils;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.openal.AL10;

public class OpenALSound
implements Sound {
    private int bufferID = -1;
    private final OpenALLwjgl3Audio audio;
    private float duration;
    private int sampleRate;
    private int channels;
    private String type;

    public OpenALSound(OpenALLwjgl3Audio audio) {
        this.audio = audio;
    }

    void setup(byte[] pcm, int channels, int bitDepth, int sampleRate) {
        int validBytes = pcm.length - pcm.length % (channels * (bitDepth >> 3));
        ByteBuffer buffer = BufferUtils.newByteBuffer(validBytes);
        buffer.put(pcm, 0, validBytes);
        ((Buffer)buffer).flip();
        this.setup(buffer.asShortBuffer(), channels, bitDepth, sampleRate);
    }

    void setup(ShortBuffer pcm, int channels, int bitDepth, int sampleRate) {
        this.channels = channels;
        this.sampleRate = sampleRate;
        int sampleFrames = (pcm.limit() << 1) / (bitDepth >> 3) / channels;
        this.duration = (float)sampleFrames / (float)sampleRate;
        if (this.bufferID == -1) {
            this.bufferID = AL10.alGenBuffers();
            int format = OpenALUtils.determineFormat(channels, bitDepth);
            AL10.alBufferData(this.bufferID, format, pcm, sampleRate);
        }
    }

    @Override
    public long play() {
        return this.play(1.0f);
    }

    @Override
    public long play(float volume) {
        if (this.audio.noDevice) {
            return 0L;
        }
        int sourceID = this.audio.obtainSource(false);
        if (sourceID == -1) {
            this.audio.retain(this, true);
            sourceID = this.audio.obtainSource(false);
        } else {
            this.audio.retain(this, false);
        }
        if (sourceID == -1) {
            return -1L;
        }
        long soundId = this.audio.getSoundId(sourceID);
        AL10.alSourcei(sourceID, 4105, this.bufferID);
        AL10.alSourcei(sourceID, 4103, 0);
        AL10.alSourcef(sourceID, 4106, volume);
        AL10.alSourcePlay(sourceID);
        return soundId;
    }

    @Override
    public long loop() {
        return this.loop(1.0f);
    }

    @Override
    public long loop(float volume) {
        if (this.audio.noDevice) {
            return 0L;
        }
        int sourceID = this.audio.obtainSource(false);
        if (sourceID == -1) {
            return -1L;
        }
        long soundId = this.audio.getSoundId(sourceID);
        AL10.alSourcei(sourceID, 4105, this.bufferID);
        AL10.alSourcei(sourceID, 4103, 1);
        AL10.alSourcef(sourceID, 4106, volume);
        AL10.alSourcePlay(sourceID);
        return soundId;
    }

    @Override
    public void stop() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.stopSourcesWithBuffer(this.bufferID);
    }

    @Override
    public void dispose() {
        if (this.audio.noDevice) {
            return;
        }
        if (this.bufferID == -1) {
            return;
        }
        this.audio.freeBuffer(this.bufferID);
        AL10.alDeleteBuffers(this.bufferID);
        this.bufferID = -1;
        this.audio.forget(this);
    }

    @Override
    public void stop(long soundId) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.stopSound(soundId);
    }

    @Override
    public void pause() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.pauseSourcesWithBuffer(this.bufferID);
    }

    @Override
    public void pause(long soundId) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.pauseSound(soundId);
    }

    @Override
    public void resume() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.resumeSourcesWithBuffer(this.bufferID);
    }

    @Override
    public void resume(long soundId) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.resumeSound(soundId);
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundPitch(soundId, pitch);
    }

    @Override
    public void setVolume(long soundId, float volume) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundGain(soundId, volume);
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundLooping(soundId, looping);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundPan(soundId, pan, volume);
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        long id = this.play();
        this.setPitch(id, pitch);
        this.setPan(id, pan, volume);
        return id;
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        long id = this.loop();
        this.setPitch(id, pitch);
        this.setPan(id, pan, volume);
        return id;
    }

    public float duration() {
        return this.duration;
    }

    public int getRate() {
        return this.sampleRate;
    }

    public int getChannels() {
        return this.channels;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

