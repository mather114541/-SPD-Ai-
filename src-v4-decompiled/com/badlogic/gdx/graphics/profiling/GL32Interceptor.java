/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.profiling.GL31Interceptor;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL32Interceptor
extends GL31Interceptor
implements GL32 {
    final GL32 gl32;

    public GL32Interceptor(GLProfiler glProfiler, GL32 gl32) {
        super(glProfiler, gl32);
        this.gl32 = gl32;
    }

    @Override
    public void glBlendBarrier() {
        ++this.calls;
        this.gl32.glBlendBarrier();
        this.check();
    }

    @Override
    public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        ++this.calls;
        this.gl32.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth);
        this.check();
    }

    @Override
    public void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled) {
        ++this.calls;
        this.gl32.glDebugMessageControl(source, type, severity, ids, enabled);
        this.check();
    }

    @Override
    public void glDebugMessageInsert(int source, int type, int id, int severity, String buf) {
        ++this.calls;
        this.gl32.glDebugMessageInsert(source, type, id, severity, buf);
        this.check();
    }

    @Override
    public void glDebugMessageCallback(GL32.DebugProc callsback) {
        ++this.calls;
        this.gl32.glDebugMessageCallback(callsback);
        this.check();
        this.check();
    }

    @Override
    public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        ++this.calls;
        int v = this.gl32.glGetDebugMessageLog(count, sources, types, ids, severities, lengths, messageLog);
        this.check();
        return v;
    }

    @Override
    public void glPushDebugGroup(int source, int id, String message) {
        ++this.calls;
        this.gl32.glPushDebugGroup(source, id, message);
        this.check();
    }

    @Override
    public void glPopDebugGroup() {
        ++this.calls;
        this.gl32.glPopDebugGroup();
        this.check();
    }

    @Override
    public void glObjectLabel(int identifier, int name, String label) {
        ++this.calls;
        this.gl32.glObjectLabel(identifier, name, label);
        this.check();
    }

    @Override
    public String glGetObjectLabel(int identifier, int name) {
        ++this.calls;
        String v = this.gl32.glGetObjectLabel(identifier, name);
        this.check();
        return v;
    }

    @Override
    public long glGetPointerv(int pname) {
        ++this.calls;
        long v = this.gl32.glGetPointerv(pname);
        this.check();
        return v;
    }

    @Override
    public void glEnablei(int target, int index) {
        ++this.calls;
        this.gl32.glEnablei(target, index);
        this.check();
    }

    @Override
    public void glDisablei(int target, int index) {
        ++this.calls;
        this.gl32.glDisablei(target, index);
        this.check();
    }

    @Override
    public void glBlendEquationi(int buf, int mode) {
        ++this.calls;
        this.gl32.glBlendEquationi(buf, mode);
        this.check();
    }

    @Override
    public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        ++this.calls;
        this.gl32.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
        this.check();
    }

    @Override
    public void glBlendFunci(int buf, int src, int dst) {
        ++this.calls;
        this.gl32.glBlendFunci(buf, src, dst);
        this.check();
    }

    @Override
    public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        ++this.calls;
        this.gl32.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
        this.check();
    }

    @Override
    public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        ++this.calls;
        this.gl32.glColorMaski(index, r, g, b, a);
        this.check();
    }

    @Override
    public boolean glIsEnabledi(int target, int index) {
        ++this.calls;
        boolean v = this.gl32.glIsEnabledi(target, index);
        this.check();
        return v;
    }

    @Override
    public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        this.vertexCount.put(count);
        ++this.drawCalls;
        ++this.calls;
        this.gl32.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
        this.check();
    }

    @Override
    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        this.vertexCount.put(count);
        ++this.drawCalls;
        ++this.calls;
        this.gl32.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
        this.check();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        this.vertexCount.put(count);
        ++this.drawCalls;
        ++this.calls;
        this.gl32.glDrawElementsInstancedBaseVertex(mode, count, type, indices, instanceCount, basevertex);
        this.check();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex) {
        this.vertexCount.put(count);
        ++this.drawCalls;
        ++this.calls;
        this.gl32.glDrawElementsInstancedBaseVertex(mode, count, type, indicesOffset, instanceCount, basevertex);
        this.check();
    }

    @Override
    public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        ++this.calls;
        this.gl32.glFramebufferTexture(target, attachment, texture, level);
        this.check();
    }

    @Override
    public int glGetGraphicsResetStatus() {
        ++this.calls;
        int v = this.gl32.glGetGraphicsResetStatus();
        this.check();
        return v;
    }

    @Override
    public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        ++this.calls;
        this.gl32.glReadnPixels(x, y, width, height, format, type, bufSize, data);
        this.check();
    }

    @Override
    public void glGetnUniformfv(int program, int location, FloatBuffer params) {
        ++this.calls;
        this.gl32.glGetnUniformfv(program, location, params);
        this.check();
    }

    @Override
    public void glGetnUniformiv(int program, int location, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetnUniformiv(program, location, params);
        this.check();
    }

    @Override
    public void glGetnUniformuiv(int program, int location, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetnUniformuiv(program, location, params);
        this.check();
    }

    @Override
    public void glMinSampleShading(float value) {
        ++this.calls;
        this.gl32.glMinSampleShading(value);
        this.check();
    }

    @Override
    public void glPatchParameteri(int pname, int value) {
        ++this.calls;
        this.gl32.glPatchParameteri(pname, value);
        this.check();
    }

    @Override
    public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glTexParameterIiv(target, pname, params);
        this.check();
    }

    @Override
    public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glTexParameterIuiv(target, pname, params);
        this.check();
    }

    @Override
    public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetTexParameterIiv(target, pname, params);
        this.check();
    }

    @Override
    public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetTexParameterIuiv(target, pname, params);
        this.check();
    }

    @Override
    public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        ++this.calls;
        this.gl32.glSamplerParameterIiv(sampler, pname, param);
        this.check();
    }

    @Override
    public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        ++this.calls;
        this.gl32.glSamplerParameterIuiv(sampler, pname, param);
        this.check();
    }

    @Override
    public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetSamplerParameterIiv(sampler, pname, params);
        this.check();
    }

    @Override
    public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        ++this.calls;
        this.gl32.glGetSamplerParameterIuiv(sampler, pname, params);
        this.check();
    }

    @Override
    public void glTexBuffer(int target, int internalformat, int buffer) {
        ++this.calls;
        this.gl32.glTexBuffer(target, internalformat, buffer);
        this.check();
    }

    @Override
    public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        ++this.calls;
        this.gl32.glTexBufferRange(target, internalformat, buffer, offset, size);
        this.check();
    }

    @Override
    public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
        ++this.calls;
        this.gl32.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
        this.check();
    }
}

