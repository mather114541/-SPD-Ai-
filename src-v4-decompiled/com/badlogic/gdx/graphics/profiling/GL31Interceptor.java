/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.profiling.GL30Interceptor;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL31Interceptor
extends GL30Interceptor
implements GL31 {
    final GL31 gl31;

    public GL31Interceptor(GLProfiler glProfiler, GL31 gl31) {
        super(glProfiler, gl31);
        this.gl31 = gl31;
    }

    protected void check() {
        int error = this.gl30.glGetError();
        while (error != 0) {
            this.glProfiler.getListener().onError(error);
            error = this.gl30.glGetError();
        }
    }

    @Override
    public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        ++this.calls;
        this.gl31.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
        this.check();
    }

    @Override
    public void glDispatchComputeIndirect(long indirect) {
        ++this.calls;
        this.gl31.glDispatchComputeIndirect(indirect);
        this.check();
    }

    @Override
    public void glDrawArraysIndirect(int mode, long indirect) {
        ++this.drawCalls;
        ++this.calls;
        this.gl31.glDrawArraysIndirect(mode, indirect);
        this.check();
    }

    @Override
    public void glDrawElementsIndirect(int mode, int type, long indirect) {
        ++this.drawCalls;
        ++this.calls;
        this.gl31.glDrawElementsIndirect(mode, type, indirect);
        this.check();
    }

    @Override
    public void glFramebufferParameteri(int target, int pname, int param) {
        ++this.calls;
        this.gl31.glFramebufferParameteri(target, pname, param);
        this.check();
    }

    @Override
    public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        ++this.calls;
        this.gl31.glGetFramebufferParameteriv(target, pname, params);
        this.check();
    }

    @Override
    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        ++this.calls;
        this.gl31.glGetProgramInterfaceiv(program, programInterface, pname, params);
        this.check();
    }

    @Override
    public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        ++this.calls;
        int v = this.gl31.glGetProgramResourceIndex(program, programInterface, name);
        this.check();
        return v;
    }

    @Override
    public String glGetProgramResourceName(int program, int programInterface, int index) {
        ++this.calls;
        String s = this.gl31.glGetProgramResourceName(program, programInterface, index);
        this.check();
        return s;
    }

    @Override
    public void glGetProgramResourceiv(int program, int programInterface, int index, IntBuffer props, IntBuffer length, IntBuffer params) {
        ++this.calls;
        this.gl31.glGetProgramResourceiv(program, programInterface, index, props, length, params);
        this.check();
    }

    @Override
    public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        ++this.calls;
        int v = this.gl31.glGetProgramResourceLocation(program, programInterface, name);
        this.check();
        return v;
    }

    @Override
    public void glUseProgramStages(int pipeline, int stages, int program) {
        ++this.calls;
        this.gl31.glUseProgramStages(pipeline, stages, program);
        this.check();
    }

    @Override
    public void glActiveShaderProgram(int pipeline, int program) {
        ++this.calls;
        this.gl31.glActiveShaderProgram(pipeline, program);
        this.check();
    }

    @Override
    public int glCreateShaderProgramv(int type, String[] strings) {
        ++this.calls;
        int v = this.gl31.glCreateShaderProgramv(type, strings);
        this.check();
        return v;
    }

    @Override
    public void glBindProgramPipeline(int pipeline) {
        ++this.calls;
        this.gl31.glBindProgramPipeline(pipeline);
        this.check();
    }

    @Override
    public void glDeleteProgramPipelines(int count, IntBuffer pipelines) {
        ++this.calls;
        this.gl31.glDeleteProgramPipelines(count, pipelines);
        this.check();
    }

    @Override
    public void glGenProgramPipelines(int count, IntBuffer pipelines) {
        ++this.calls;
        this.gl31.glGenProgramPipelines(count, pipelines);
        this.check();
    }

    @Override
    public boolean glIsProgramPipeline(int pipeline) {
        ++this.calls;
        boolean v = this.gl31.glIsProgramPipeline(pipeline);
        this.check();
        return v;
    }

    @Override
    public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        ++this.calls;
        this.gl31.glGetProgramPipelineiv(pipeline, pname, params);
        this.check();
    }

    @Override
    public void glProgramUniform1i(int program, int location, int v0) {
        ++this.calls;
        this.gl31.glProgramUniform1i(program, location, v0);
        this.check();
    }

    @Override
    public void glProgramUniform2i(int program, int location, int v0, int v1) {
        ++this.calls;
        this.gl31.glProgramUniform2i(program, location, v0, v1);
        this.check();
    }

    @Override
    public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        ++this.calls;
        this.gl31.glProgramUniform3i(program, location, v0, v1, v2);
        this.check();
    }

    @Override
    public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        ++this.calls;
        this.gl31.glProgramUniform4i(program, location, v0, v1, v2, v3);
        this.check();
    }

    @Override
    public void glProgramUniform1ui(int program, int location, int v0) {
        ++this.calls;
        this.gl31.glProgramUniform1ui(program, location, v0);
        this.check();
    }

    @Override
    public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        ++this.calls;
        this.gl31.glProgramUniform2ui(program, location, v0, v1);
        this.check();
    }

    @Override
    public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        ++this.calls;
        this.gl31.glProgramUniform3ui(program, location, v0, v1, v2);
        this.check();
    }

    @Override
    public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        ++this.calls;
        this.gl31.glProgramUniform4ui(program, location, v0, v1, v2, v3);
        this.check();
    }

    @Override
    public void glProgramUniform1f(int program, int location, float v0) {
        ++this.calls;
        this.gl31.glProgramUniform1f(program, location, v0);
        this.check();
    }

    @Override
    public void glProgramUniform2f(int program, int location, float v0, float v1) {
        ++this.calls;
        this.gl31.glProgramUniform2f(program, location, v0, v1);
        this.check();
    }

    @Override
    public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        ++this.calls;
        this.gl31.glProgramUniform3f(program, location, v0, v1, v2);
        this.check();
    }

    @Override
    public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        ++this.calls;
        this.gl31.glProgramUniform4f(program, location, v0, v1, v2, v3);
        this.check();
    }

    @Override
    public void glProgramUniform1iv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform1iv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform2iv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform2iv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform3iv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform3iv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform4iv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform4iv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform1uiv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform1uiv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform2uiv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform2uiv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform3uiv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform3uiv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform4uiv(int program, int location, IntBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform4uiv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform1fv(int program, int location, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform1fv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform2fv(int program, int location, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform2fv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform3fv(int program, int location, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform3fv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniform4fv(int program, int location, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniform4fv(program, location, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix2fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix2fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix3fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix3fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix4fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix4fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix2x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix2x3fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix3x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix3x2fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix2x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix2x4fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix4x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix4x2fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix3x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix3x4fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glProgramUniformMatrix4x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        ++this.calls;
        this.gl31.glProgramUniformMatrix4x3fv(program, location, transpose, value);
        this.check();
    }

    @Override
    public void glValidateProgramPipeline(int pipeline) {
        ++this.calls;
        this.gl31.glValidateProgramPipeline(pipeline);
        this.check();
    }

    @Override
    public String glGetProgramPipelineInfoLog(int program) {
        ++this.calls;
        String s = this.gl31.glGetProgramPipelineInfoLog(program);
        this.check();
        return s;
    }

    @Override
    public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        ++this.calls;
        this.gl31.glBindImageTexture(unit, texture, level, layered, layer, access, format);
        this.check();
    }

    @Override
    public void glGetBooleani_v(int target, int index, IntBuffer data) {
        ++this.calls;
        this.gl31.glGetBooleani_v(target, index, data);
        this.check();
    }

    @Override
    public void glMemoryBarrier(int barriers) {
        ++this.calls;
        this.gl31.glMemoryBarrier(barriers);
        this.check();
    }

    @Override
    public void glMemoryBarrierByRegion(int barriers) {
        ++this.calls;
        this.gl31.glMemoryBarrierByRegion(barriers);
        this.check();
    }

    @Override
    public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        ++this.calls;
        this.gl31.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
        this.check();
    }

    @Override
    public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        ++this.calls;
        this.gl31.glGetMultisamplefv(pname, index, val);
        this.check();
    }

    @Override
    public void glSampleMaski(int maskNumber, int mask) {
        ++this.calls;
        this.gl31.glSampleMaski(maskNumber, mask);
        this.check();
    }

    @Override
    public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        ++this.calls;
        this.gl31.glGetTexLevelParameteriv(target, level, pname, params);
        this.check();
    }

    @Override
    public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        ++this.calls;
        this.gl31.glGetTexLevelParameterfv(target, level, pname, params);
        this.check();
    }

    @Override
    public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        ++this.calls;
        this.gl31.glBindVertexBuffer(bindingindex, buffer, offset, stride);
        this.check();
    }

    @Override
    public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        ++this.calls;
        this.gl31.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
        this.check();
    }

    @Override
    public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        ++this.calls;
        this.gl31.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
        this.check();
    }

    @Override
    public void glVertexAttribBinding(int attribindex, int bindingindex) {
        ++this.calls;
        this.gl31.glVertexAttribBinding(attribindex, bindingindex);
        this.check();
    }

    @Override
    public void glVertexBindingDivisor(int bindingindex, int divisor) {
        ++this.calls;
        this.gl31.glVertexBindingDivisor(bindingindex, divisor);
        this.check();
    }
}

