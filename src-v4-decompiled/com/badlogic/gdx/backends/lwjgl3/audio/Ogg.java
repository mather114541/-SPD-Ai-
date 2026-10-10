/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3.audio;

import com.badlogic.gdx.backends.lwjgl3.audio.OggInputStream;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

public class Ogg {

    public static class Sound
    extends OpenALSound {
        public Sound(OpenALLwjgl3Audio audio, FileHandle file) {
            super(audio);
            if (audio.noDevice) {
                return;
            }
            byte[] streamData = file.readBytes();
            ByteBuffer encodedData = BufferUtils.newByteBuffer(streamData.length);
            encodedData.put(streamData);
            encodedData.flip();
            try (MemoryStack stack = MemoryStack.stackPush();){
                IntBuffer channelsBuffer = stack.mallocInt(1);
                IntBuffer sampleRateBuffer = stack.mallocInt(1);
                ShortBuffer decodedData = STBVorbis.stb_vorbis_decode_memory(encodedData, channelsBuffer, sampleRateBuffer);
                int channels = channelsBuffer.get(0);
                int sampleRate = sampleRateBuffer.get(0);
                if (decodedData == null) {
                    throw new GdxRuntimeException("Error decoding OGG file: " + file);
                }
                this.setup(decodedData, channels, 16, sampleRate);
            }
        }
    }

    public static class Music
    extends OpenALMusic {
        private OggInputStream input;
        private OggInputStream previousInput;

        public Music(OpenALLwjgl3Audio audio, FileHandle file) {
            super(audio, file);
            if (audio.noDevice) {
                return;
            }
            this.input = new OggInputStream(file.read());
            this.setup(this.input.getChannels(), 16, this.input.getSampleRate());
        }

        @Override
        public int read(byte[] buffer) {
            if (this.input == null) {
                this.input = new OggInputStream(this.file.read(), this.previousInput);
                this.setup(this.input.getChannels(), 16, this.input.getSampleRate());
                this.previousInput = null;
            }
            return this.input.read(buffer);
        }

        @Override
        public void reset() {
            StreamUtils.closeQuietly(this.input);
            this.previousInput = null;
            this.input = null;
        }

        @Override
        protected void loop() {
            StreamUtils.closeQuietly(this.input);
            this.previousInput = this.input;
            this.input = null;
        }
    }
}

