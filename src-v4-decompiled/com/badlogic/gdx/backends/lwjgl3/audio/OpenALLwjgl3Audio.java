/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3.audio;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.backends.lwjgl3.audio.JavaSoundAudioRecorder;
import com.badlogic.gdx.backends.lwjgl3.audio.Lwjgl3Audio;
import com.badlogic.gdx.backends.lwjgl3.audio.Mp3;
import com.badlogic.gdx.backends.lwjgl3.audio.Ogg;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALAudioDevice;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound;
import com.badlogic.gdx.backends.lwjgl3.audio.Wav;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.SOFTReopenDevice;

public class OpenALLwjgl3Audio
implements Lwjgl3Audio {
    private final int deviceBufferSize;
    private final int deviceBufferCount;
    private IntArray idleSources;
    private IntArray allSources;
    private LongMap<Integer> soundIdToSource;
    private IntMap<Long> sourceToSoundId;
    private long nextSoundId = 0L;
    private ObjectMap<String, BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALSound>> extensionToSoundClass = new ObjectMap();
    private ObjectMap<String, BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALMusic>> extensionToMusicClass = new ObjectMap();
    private OpenALSound[] recentSounds;
    private int mostRecetSound = -1;
    private String preferredOutputDevice = null;
    private Thread observerThread;
    Array<OpenALMusic> music = new Array(false, 1, OpenALMusic[]::new);
    long device;
    long context;
    boolean noDevice = false;

    public OpenALLwjgl3Audio() {
        this(16, 9, 512);
    }

    public OpenALLwjgl3Audio(int simultaneousSources, int deviceBufferCount, int deviceBufferSize) {
        this.deviceBufferSize = deviceBufferSize;
        this.deviceBufferCount = deviceBufferCount;
        this.registerSound("ogg", Ogg.Sound::new);
        this.registerMusic("ogg", Ogg.Music::new);
        this.registerSound("wav", Wav.Sound::new);
        this.registerMusic("wav", Wav.Music::new);
        this.registerSound("mp3", Mp3.Sound::new);
        this.registerMusic("mp3", Mp3.Music::new);
        this.device = ALC10.alcOpenDevice((ByteBuffer)null);
        if (this.device == 0L) {
            this.noDevice = true;
            return;
        }
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(this.device);
        this.context = ALC10.alcCreateContext(this.device, (IntBuffer)null);
        if (this.context == 0L) {
            ALC10.alcCloseDevice(this.device);
            this.noDevice = true;
            return;
        }
        if (!ALC10.alcMakeContextCurrent(this.context)) {
            this.noDevice = true;
            return;
        }
        AL.createCapabilities(deviceCapabilities);
        AL10.alGetError();
        this.allSources = new IntArray(false, simultaneousSources);
        for (int i = 0; i < simultaneousSources; ++i) {
            int sourceID = AL10.alGenSources();
            if (AL10.alGetError() != 0) break;
            this.allSources.add(sourceID);
        }
        this.idleSources = new IntArray(this.allSources);
        this.soundIdToSource = new LongMap();
        this.sourceToSoundId = new IntMap();
        FloatBuffer orientation = BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
        ((Buffer)orientation).flip();
        AL10.alListenerfv(4111, orientation);
        FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer)velocity).flip();
        AL10.alListenerfv(4102, velocity);
        FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer)position).flip();
        AL10.alListenerfv(4100, position);
        AL10.alDisable(6571);
        this.observerThread = new Thread(new Runnable(){
            private String[] lastAvailableDevices = new String[0];

            @Override
            public void run() {
                while (true) {
                    boolean isConnected;
                    boolean bl = isConnected = ALC10.alcGetInteger(OpenALLwjgl3Audio.this.device, 787) != 0;
                    if (!isConnected) {
                        OpenALLwjgl3Audio.this.switchOutputDevice(null, false);
                        continue;
                    }
                    if (OpenALLwjgl3Audio.this.preferredOutputDevice != null) {
                        if (Arrays.asList(OpenALLwjgl3Audio.this.getAvailableOutputDevices()).contains(OpenALLwjgl3Audio.this.preferredOutputDevice)) {
                            if (!OpenALLwjgl3Audio.this.preferredOutputDevice.equals(ALC10.alcGetString(OpenALLwjgl3Audio.this.device, 4115))) {
                                OpenALLwjgl3Audio.this.switchOutputDevice(OpenALLwjgl3Audio.this.preferredOutputDevice);
                            }
                        } else if (OpenALLwjgl3Audio.this.preferredOutputDevice.equals(ALC10.alcGetString(OpenALLwjgl3Audio.this.device, 4115))) {
                            OpenALLwjgl3Audio.this.switchOutputDevice(null, false);
                        }
                    } else {
                        Object[] currentDevices = OpenALLwjgl3Audio.this.getAvailableOutputDevices();
                        if (!Arrays.equals(currentDevices, this.lastAvailableDevices)) {
                            OpenALLwjgl3Audio.this.switchOutputDevice(null);
                        }
                        this.lastAvailableDevices = currentDevices;
                    }
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        });
        this.observerThread.setDaemon(true);
        this.observerThread.start();
        this.recentSounds = new OpenALSound[simultaneousSources];
    }

    public void registerSound(String extension, BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALSound> soundSupplier) {
        if (extension == null) {
            throw new IllegalArgumentException("extension cannot be null.");
        }
        if (soundSupplier == null) {
            throw new IllegalArgumentException("soundClass cannot be null.");
        }
        this.extensionToSoundClass.put(extension, soundSupplier);
    }

    public void registerMusic(String extension, BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALMusic> soundSupplier) {
        if (extension == null) {
            throw new IllegalArgumentException("extension cannot be null.");
        }
        if (soundSupplier == null) {
            throw new IllegalArgumentException("musicClass cannot be null.");
        }
        this.extensionToMusicClass.put(extension, soundSupplier);
    }

    @Override
    public OpenALSound newSound(FileHandle file) {
        String extension = file.extension().toLowerCase();
        return this.newSound(file, extension);
    }

    public OpenALSound newSound(FileHandle file, String extension) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALSound> soundSupplier = this.extensionToSoundClass.get(extension);
        if (soundSupplier == null) {
            throw new GdxRuntimeException("Unknown file extension for sound: " + file);
        }
        OpenALSound sound = soundSupplier.apply(this, file);
        if (sound.getType() != null && !sound.getType().equals(extension)) {
            return this.newSound(file, sound.getType());
        }
        return sound;
    }

    @Override
    public OpenALMusic newMusic(FileHandle file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        BiFunction<OpenALLwjgl3Audio, FileHandle, OpenALMusic> musicSupplier = this.extensionToMusicClass.get(file.extension().toLowerCase());
        if (musicSupplier == null) {
            throw new GdxRuntimeException("Unknown file extension for music: " + file);
        }
        return musicSupplier.apply(this, file);
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return this.switchOutputDevice(deviceIdentifier, true);
    }

    private boolean switchOutputDevice(String deviceIdentifier, boolean setPreferred) {
        if (setPreferred) {
            this.preferredOutputDevice = deviceIdentifier;
        }
        return SOFTReopenDevice.alcReopenDeviceSOFT(this.device, (CharSequence)deviceIdentifier, (IntBuffer)null);
    }

    @Override
    public String[] getAvailableOutputDevices() {
        List<String> devices = ALUtil.getStringList(0L, 4115);
        if (devices == null) {
            return new String[0];
        }
        return devices.toArray(new String[0]);
    }

    int obtainSource(boolean isMusic) {
        if (this.noDevice) {
            return 0;
        }
        int n = this.idleSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceId = this.idleSources.get(i);
            int state = AL10.alGetSourcei(sourceId, 4112);
            if (state == 4114 || state == 4115) continue;
            Long oldSoundId = this.sourceToSoundId.remove(sourceId);
            if (oldSoundId != null) {
                this.soundIdToSource.remove(oldSoundId);
            }
            if (isMusic) {
                this.idleSources.removeIndex(i);
            } else {
                long soundId = this.nextSoundId++;
                this.sourceToSoundId.put(sourceId, soundId);
                this.soundIdToSource.put(soundId, sourceId);
            }
            AL10.alSourceStop(sourceId);
            AL10.alSourcei(sourceId, 4105, 0);
            AL10.alSourcef(sourceId, 4106, 1.0f);
            AL10.alSourcef(sourceId, 4099, 1.0f);
            AL10.alSource3f(sourceId, 4100, 0.0f, 0.0f, 1.0f);
            AL10.alSourcei(sourceId, 4147, 2);
            return sourceId;
        }
        return -1;
    }

    void freeSource(int sourceID) {
        if (this.noDevice) {
            return;
        }
        AL10.alSourceStop(sourceID);
        AL10.alSourcei(sourceID, 4105, 0);
        Long soundId = this.sourceToSoundId.remove(sourceID);
        if (soundId != null) {
            this.soundIdToSource.remove(soundId);
        }
        this.idleSources.add(sourceID);
    }

    void freeBuffer(int bufferID) {
        if (this.noDevice) {
            return;
        }
        int n = this.idleSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceID = this.idleSources.get(i);
            if (AL10.alGetSourcei(sourceID, 4105) != bufferID) continue;
            Long soundId = this.sourceToSoundId.remove(sourceID);
            if (soundId != null) {
                this.soundIdToSource.remove(soundId);
            }
            AL10.alSourceStop(sourceID);
            AL10.alSourcei(sourceID, 4105, 0);
        }
    }

    void stopSourcesWithBuffer(int bufferID) {
        if (this.noDevice) {
            return;
        }
        int n = this.idleSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceID = this.idleSources.get(i);
            if (AL10.alGetSourcei(sourceID, 4105) != bufferID) continue;
            Long soundId = this.sourceToSoundId.remove(sourceID);
            if (soundId != null) {
                this.soundIdToSource.remove(soundId);
            }
            AL10.alSourceStop(sourceID);
        }
    }

    void pauseSourcesWithBuffer(int bufferID) {
        if (this.noDevice) {
            return;
        }
        int n = this.idleSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceID = this.idleSources.get(i);
            if (AL10.alGetSourcei(sourceID, 4105) != bufferID) continue;
            AL10.alSourcePause(sourceID);
        }
    }

    void resumeSourcesWithBuffer(int bufferID) {
        if (this.noDevice) {
            return;
        }
        int n = this.idleSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceID = this.idleSources.get(i);
            if (AL10.alGetSourcei(sourceID, 4105) != bufferID || AL10.alGetSourcei(sourceID, 4112) != 4115) continue;
            AL10.alSourcePlay(sourceID);
        }
    }

    @Override
    public void update() {
        if (this.noDevice) {
            return;
        }
        for (int i = 0; i < this.music.size; ++i) {
            ((OpenALMusic[])this.music.items)[i].update();
        }
    }

    public long getSoundId(int sourceId) {
        Long soundId = this.sourceToSoundId.get(sourceId);
        return soundId != null ? soundId : -1L;
    }

    public int getSourceId(long soundId) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        return sourceId != null ? sourceId : -1;
    }

    public void stopSound(long soundId) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        if (sourceId != null) {
            AL10.alSourceStop(sourceId);
        }
    }

    public void pauseSound(long soundId) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        if (sourceId != null) {
            AL10.alSourcePause(sourceId);
        }
    }

    public void resumeSound(long soundId) {
        int sourceId = this.soundIdToSource.get(soundId, -1);
        if (sourceId != -1 && AL10.alGetSourcei(sourceId, 4112) == 4115) {
            AL10.alSourcePlay(sourceId);
        }
    }

    public void setSoundGain(long soundId, float volume) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        if (sourceId != null) {
            AL10.alSourcef(sourceId, 4106, volume);
        }
    }

    public void setSoundLooping(long soundId, boolean looping) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        if (sourceId != null) {
            AL10.alSourcei(sourceId, 4103, looping ? 1 : 0);
        }
    }

    public void setSoundPitch(long soundId, float pitch) {
        Integer sourceId = this.soundIdToSource.get(soundId);
        if (sourceId != null) {
            AL10.alSourcef(sourceId, 4099, pitch);
        }
    }

    public void setSoundPan(long soundId, float pan, float volume) {
        int sourceId = this.soundIdToSource.get(soundId, -1);
        if (sourceId != -1) {
            AL10.alSource3f(sourceId, 4100, MathUtils.cos((pan - 1.0f) * 1.5707964f), 0.0f, MathUtils.sin((pan + 1.0f) * 1.5707964f));
            AL10.alSourcef(sourceId, 4106, volume);
        }
    }

    @Override
    public void dispose() {
        if (this.noDevice) {
            return;
        }
        this.observerThread.interrupt();
        int n = this.allSources.size;
        for (int i = 0; i < n; ++i) {
            int sourceID = this.allSources.get(i);
            int state = AL10.alGetSourcei(sourceID, 4112);
            if (state != 4116) {
                AL10.alSourceStop(sourceID);
            }
            AL10.alDeleteSources(sourceID);
        }
        this.sourceToSoundId = null;
        this.soundIdToSource = null;
        ALC10.alcDestroyContext(this.context);
        ALC10.alcCloseDevice(this.device);
    }

    @Override
    public AudioDevice newAudioDevice(int sampleRate, final boolean isMono) {
        if (this.noDevice) {
            return new AudioDevice(){

                @Override
                public void writeSamples(float[] samples, int offset, int numSamples) {
                }

                @Override
                public void writeSamples(short[] samples, int offset, int numSamples) {
                }

                @Override
                public void setVolume(float volume) {
                }

                @Override
                public boolean isMono() {
                    return isMono;
                }

                @Override
                public int getLatency() {
                    return 0;
                }

                @Override
                public void dispose() {
                }

                @Override
                public void pause() {
                }

                @Override
                public void resume() {
                }
            };
        }
        return new OpenALAudioDevice(this, sampleRate, isMono, this.deviceBufferSize, this.deviceBufferCount);
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        if (this.noDevice) {
            return new AudioRecorder(){

                @Override
                public void read(short[] samples, int offset, int numSamples) {
                }

                @Override
                public void dispose() {
                }
            };
        }
        return new JavaSoundAudioRecorder(samplingRate, isMono);
    }

    protected void retain(OpenALSound sound, boolean stop) {
        ++this.mostRecetSound;
        this.mostRecetSound %= this.recentSounds.length;
        if (stop && this.recentSounds[this.mostRecetSound] != null) {
            this.recentSounds[this.mostRecetSound].stop();
        }
        this.recentSounds[this.mostRecetSound] = sound;
    }

    public void forget(OpenALSound sound) {
        for (int i = 0; i < this.recentSounds.length; ++i) {
            if (this.recentSounds[i] != sound) continue;
            this.recentSounds[i] = null;
        }
    }
}

