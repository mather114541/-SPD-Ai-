/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3.audio;

import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;

public class Wav {

    public static class WavInputStream
    extends FilterInputStream {
        public int channels;
        public int bitDepth;
        public int sampleRate;
        public int dataRemaining;
        public int type;

        public WavInputStream(FileHandle file) {
            super(file.read());
            try {
                if (this.read() != 82 || this.read() != 73 || this.read() != 70 || this.read() != 70) {
                    throw new GdxRuntimeException("RIFF header not found: " + file);
                }
                this.skipFully(4);
                if (this.read() != 87 || this.read() != 65 || this.read() != 86 || this.read() != 69) {
                    throw new GdxRuntimeException("Invalid wave file header: " + file);
                }
                int fmtChunkLength = this.seekToChunk('f', 'm', 't', ' ');
                this.type = this.read() & 0xFF | (this.read() & 0xFF) << 8;
                if (this.type == 85) {
                    return;
                }
                if (this.type != 1 && this.type != 3) {
                    throw new GdxRuntimeException("WAV files must be PCM, unsupported format: " + this.getCodecName(this.type) + " (" + this.type + ")");
                }
                this.channels = this.read() & 0xFF | (this.read() & 0xFF) << 8;
                this.sampleRate = this.read() & 0xFF | (this.read() & 0xFF) << 8 | (this.read() & 0xFF) << 16 | (this.read() & 0xFF) << 24;
                this.skipFully(6);
                this.bitDepth = this.read() & 0xFF | (this.read() & 0xFF) << 8;
                if (this.type == 1) {
                    if (this.bitDepth != 8 && this.bitDepth != 16) {
                        throw new GdxRuntimeException("PCM WAV files must be 8 or 16-bit: " + this.bitDepth);
                    }
                } else if (this.type == 3 && this.bitDepth != 32 && this.bitDepth != 64) {
                    throw new GdxRuntimeException("Floating-point WAV files must be 32 or 64-bit: " + this.bitDepth);
                }
                this.skipFully(fmtChunkLength - 16);
                this.dataRemaining = this.seekToChunk('d', 'a', 't', 'a');
            }
            catch (Throwable ex) {
                StreamUtils.closeQuietly(this);
                throw new GdxRuntimeException("Error reading WAV file: " + file, ex);
            }
        }

        private int seekToChunk(char c1, char c2, char c3, char c4) throws IOException {
            while (true) {
                boolean found = this.read() == c1;
                found &= this.read() == c2;
                found &= this.read() == c3;
                found &= this.read() == c4;
                int chunkLength = this.read() & 0xFF | (this.read() & 0xFF) << 8 | (this.read() & 0xFF) << 16 | (this.read() & 0xFF) << 24;
                if (chunkLength == -1) {
                    throw new IOException("Chunk not found: " + c1 + c2 + c3 + c4);
                }
                if (found) {
                    return chunkLength;
                }
                this.skipFully(chunkLength);
            }
        }

        private void skipFully(int count) throws IOException {
            while (count > 0) {
                long skipped = this.in.skip(count);
                if (skipped <= 0L) {
                    throw new EOFException("Unable to skip.");
                }
                count = (int)((long)count - skipped);
            }
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            int length;
            if (this.dataRemaining == 0) {
                return -1;
            }
            int offset = 0;
            do {
                if ((length = Math.min(super.read(buffer, offset, buffer.length - offset), this.dataRemaining)) == -1) {
                    if (offset > 0) {
                        return offset;
                    }
                    return -1;
                }
                this.dataRemaining -= length;
            } while ((offset += length) < buffer.length);
            return offset;
        }

        private String getCodecName(int type) {
            switch (type) {
                case 2: {
                    return "Microsoft ADPCM";
                }
                case 6: {
                    return "ITU-T G.711 A-law";
                }
                case 7: {
                    return "ITU-T G.711 u-law";
                }
                case 17: {
                    return "IMA ADPCM";
                }
                case 34: {
                    return "DSP Group TrueSpeech";
                }
                case 49: {
                    return "Microsoft GSM 6.10";
                }
                case 64: {
                    return "Antex G.721 ADPCM";
                }
                case 112: {
                    return "Lernout & Hauspie CELP 4.8kbps";
                }
                case 114: {
                    return "Lernout & Hauspie CBS 12kbps";
                }
                case 65534: {
                    return "Extensible";
                }
            }
            return "Unknown";
        }
    }

    public static class Sound
    extends OpenALSound {
        public Sound(OpenALLwjgl3Audio audio, FileHandle file) {
            WavInputStream input;
            block6: {
                super(audio);
                if (audio.noDevice) {
                    return;
                }
                input = null;
                input = new WavInputStream(file);
                if (input.type != 85) break block6;
                this.setType("mp3");
                StreamUtils.closeQuietly(input);
                return;
            }
            try {
                this.setup(StreamUtils.copyStreamToByteArray(input, input.dataRemaining), input.channels, input.bitDepth, input.sampleRate);
            }
            catch (IOException ex) {
                try {
                    throw new GdxRuntimeException("Error reading WAV file: " + file, ex);
                }
                catch (Throwable throwable) {
                    StreamUtils.closeQuietly(input);
                    throw throwable;
                }
            }
            StreamUtils.closeQuietly(input);
        }
    }

    public static class Music
    extends OpenALMusic {
        private WavInputStream input;

        public Music(OpenALLwjgl3Audio audio, FileHandle file) {
            super(audio, file);
            this.input = new WavInputStream(file);
            if (audio.noDevice) {
                return;
            }
            this.setup(this.input.channels, this.input.bitDepth, this.input.sampleRate);
        }

        @Override
        public int read(byte[] buffer) {
            if (this.input == null) {
                this.input = new WavInputStream(this.file);
                this.setup(this.input.channels, this.input.bitDepth, this.input.sampleRate);
            }
            try {
                return this.input.read(buffer);
            }
            catch (IOException ex) {
                throw new GdxRuntimeException("Error reading WAV file: " + this.file, ex);
            }
        }

        @Override
        public void reset() {
            StreamUtils.closeQuietly(this.input);
            this.input = null;
        }
    }
}

