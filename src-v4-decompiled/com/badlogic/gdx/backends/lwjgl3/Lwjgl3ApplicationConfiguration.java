/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.backends.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import java.io.PrintStream;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Configuration;

public class Lwjgl3ApplicationConfiguration
extends Lwjgl3WindowConfiguration {
    public static PrintStream errorStream = System.err;
    boolean disableAudio = false;
    int maxNetThreads = Integer.MAX_VALUE;
    int audioDeviceSimultaneousSources = 16;
    int audioDeviceBufferSize = 512;
    int audioDeviceBufferCount = 9;
    GLEmulation glEmulation = GLEmulation.GL20;
    int gles30ContextMajorVersion = 3;
    int gles30ContextMinorVersion = 2;
    int r = 8;
    int g = 8;
    int b = 8;
    int a = 8;
    int depth = 16;
    int stencil = 0;
    int samples = 0;
    boolean transparentFramebuffer;
    int idleFPS = 60;
    int foregroundFPS = 0;
    boolean pauseWhenMinimized = true;
    boolean pauseWhenLostFocus = false;
    String preferencesDirectory = ".prefs/";
    Files.FileType preferencesFileType = Files.FileType.External;
    HdpiMode hdpiMode = HdpiMode.Logical;
    boolean debug = false;
    PrintStream debugStream = System.err;

    static Lwjgl3ApplicationConfiguration copy(Lwjgl3ApplicationConfiguration config) {
        Lwjgl3ApplicationConfiguration copy = new Lwjgl3ApplicationConfiguration();
        copy.set(config);
        return copy;
    }

    void set(Lwjgl3ApplicationConfiguration config) {
        super.setWindowConfiguration(config);
        this.disableAudio = config.disableAudio;
        this.audioDeviceSimultaneousSources = config.audioDeviceSimultaneousSources;
        this.audioDeviceBufferSize = config.audioDeviceBufferSize;
        this.audioDeviceBufferCount = config.audioDeviceBufferCount;
        this.glEmulation = config.glEmulation;
        this.gles30ContextMajorVersion = config.gles30ContextMajorVersion;
        this.gles30ContextMinorVersion = config.gles30ContextMinorVersion;
        this.r = config.r;
        this.g = config.g;
        this.b = config.b;
        this.a = config.a;
        this.depth = config.depth;
        this.stencil = config.stencil;
        this.samples = config.samples;
        this.transparentFramebuffer = config.transparentFramebuffer;
        this.idleFPS = config.idleFPS;
        this.foregroundFPS = config.foregroundFPS;
        this.pauseWhenMinimized = config.pauseWhenMinimized;
        this.pauseWhenLostFocus = config.pauseWhenLostFocus;
        this.preferencesDirectory = config.preferencesDirectory;
        this.preferencesFileType = config.preferencesFileType;
        this.hdpiMode = config.hdpiMode;
        this.debug = config.debug;
        this.debugStream = config.debugStream;
    }

    @Override
    public void setInitialVisible(boolean visibility) {
        this.initialVisible = visibility;
    }

    public void disableAudio(boolean disableAudio) {
        this.disableAudio = disableAudio;
    }

    public void setMaxNetThreads(int maxNetThreads) {
        this.maxNetThreads = maxNetThreads;
    }

    public void setAudioConfig(int simultaneousSources, int bufferSize, int bufferCount) {
        this.audioDeviceSimultaneousSources = simultaneousSources;
        this.audioDeviceBufferSize = bufferSize;
        this.audioDeviceBufferCount = bufferCount;
    }

    public void setOpenGLEmulation(GLEmulation glVersion, int gles3MajorVersion, int gles3MinorVersion) {
        this.glEmulation = glVersion;
        this.gles30ContextMajorVersion = gles3MajorVersion;
        this.gles30ContextMinorVersion = gles3MinorVersion;
    }

    public void setBackBufferConfig(int r, int g, int b, int a, int depth, int stencil, int samples) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.depth = depth;
        this.stencil = stencil;
        this.samples = samples;
    }

    public void setTransparentFramebuffer(boolean transparentFramebuffer) {
        this.transparentFramebuffer = transparentFramebuffer;
    }

    public void setIdleFPS(int fps) {
        this.idleFPS = fps;
    }

    public void setForegroundFPS(int fps) {
        this.foregroundFPS = fps;
    }

    public void setPauseWhenMinimized(boolean pauseWhenMinimized) {
        this.pauseWhenMinimized = pauseWhenMinimized;
    }

    public void setPauseWhenLostFocus(boolean pauseWhenLostFocus) {
        this.pauseWhenLostFocus = pauseWhenLostFocus;
    }

    public void setPreferencesConfig(String preferencesDirectory, Files.FileType preferencesFileType) {
        this.preferencesDirectory = preferencesDirectory;
        this.preferencesFileType = preferencesFileType;
    }

    public void setHdpiMode(HdpiMode mode) {
        this.hdpiMode = mode;
    }

    public void enableGLDebugOutput(boolean enable, PrintStream debugOutputStream) {
        this.debug = enable;
        this.debugStream = debugOutputStream;
    }

    public static void useGlfwAsync() {
        if (SharedLibraryLoader.os == Os.MacOsX) {
            Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
        }
    }

    public static Graphics.DisplayMode getDisplayMode() {
        Lwjgl3Application.initializeGlfw();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        return new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(), videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
    }

    public static Graphics.DisplayMode getDisplayMode(Graphics.Monitor monitor) {
        Lwjgl3Application.initializeGlfw();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(((Lwjgl3Graphics.Lwjgl3Monitor)monitor).monitorHandle);
        return new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Graphics.Lwjgl3Monitor)monitor).monitorHandle, videoMode.width(), videoMode.height(), videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
    }

    public static Graphics.DisplayMode[] getDisplayModes() {
        Lwjgl3Application.initializeGlfw();
        GLFWVidMode.Buffer videoModes = GLFW.glfwGetVideoModes(GLFW.glfwGetPrimaryMonitor());
        Graphics.DisplayMode[] result = new Graphics.DisplayMode[videoModes.limit()];
        for (int i = 0; i < result.length; ++i) {
            GLFWVidMode videoMode = (GLFWVidMode)videoModes.get(i);
            result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(), videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
        }
        return result;
    }

    public static Graphics.DisplayMode[] getDisplayModes(Graphics.Monitor monitor) {
        Lwjgl3Application.initializeGlfw();
        GLFWVidMode.Buffer videoModes = GLFW.glfwGetVideoModes(((Lwjgl3Graphics.Lwjgl3Monitor)monitor).monitorHandle);
        Graphics.DisplayMode[] result = new Graphics.DisplayMode[videoModes.limit()];
        for (int i = 0; i < result.length; ++i) {
            GLFWVidMode videoMode = (GLFWVidMode)videoModes.get(i);
            result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Graphics.Lwjgl3Monitor)monitor).monitorHandle, videoMode.width(), videoMode.height(), videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
        }
        return result;
    }

    public static Graphics.Monitor getPrimaryMonitor() {
        Lwjgl3Application.initializeGlfw();
        return Lwjgl3ApplicationConfiguration.toLwjgl3Monitor(GLFW.glfwGetPrimaryMonitor());
    }

    public static Graphics.Monitor[] getMonitors() {
        Lwjgl3Application.initializeGlfw();
        PointerBuffer glfwMonitors = GLFW.glfwGetMonitors();
        Graphics.Monitor[] monitors = new Graphics.Monitor[glfwMonitors.limit()];
        for (int i = 0; i < glfwMonitors.limit(); ++i) {
            monitors[i] = Lwjgl3ApplicationConfiguration.toLwjgl3Monitor(glfwMonitors.get(i));
        }
        return monitors;
    }

    static Lwjgl3Graphics.Lwjgl3Monitor toLwjgl3Monitor(long glfwMonitor) {
        IntBuffer tmp = BufferUtils.createIntBuffer(1);
        IntBuffer tmp2 = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetMonitorPos(glfwMonitor, tmp, tmp2);
        int virtualX = tmp.get(0);
        int virtualY = tmp2.get(0);
        String name = GLFW.glfwGetMonitorName(glfwMonitor);
        return new Lwjgl3Graphics.Lwjgl3Monitor(glfwMonitor, virtualX, virtualY, name);
    }

    static GridPoint2 calculateCenteredWindowPosition(Lwjgl3Graphics.Lwjgl3Monitor monitor, int newWidth, int newHeight) {
        int maxY;
        int minY;
        int maxX;
        int minX;
        IntBuffer tmp = BufferUtils.createIntBuffer(1);
        IntBuffer tmp2 = BufferUtils.createIntBuffer(1);
        IntBuffer tmp3 = BufferUtils.createIntBuffer(1);
        IntBuffer tmp4 = BufferUtils.createIntBuffer(1);
        Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitor);
        GLFW.glfwGetMonitorWorkarea(monitor.monitorHandle, tmp, tmp2, tmp3, tmp4);
        int workareaWidth = tmp3.get(0);
        int workareaHeight = tmp4.get(0);
        if (newWidth > workareaWidth) {
            minX = monitor.virtualX;
            maxX = displayMode.width;
        } else {
            minX = tmp.get(0);
            maxX = workareaWidth;
        }
        if (newHeight > workareaHeight) {
            minY = monitor.virtualY;
            maxY = displayMode.height;
        } else {
            minY = tmp2.get(0);
            maxY = workareaHeight;
        }
        return new GridPoint2(Math.max(minX, minX + (maxX - newWidth) / 2), Math.max(minY, minY + (maxY - newHeight) / 2));
    }

    public static enum GLEmulation {
        ANGLE_GLES20,
        GL20,
        GL30,
        GL31,
        GL32;

    }
}

