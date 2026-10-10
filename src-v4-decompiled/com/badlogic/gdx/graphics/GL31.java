/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.GL30;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL31
extends GL30 {
    public static final int GL_VERTEX_SHADER_BIT = 1;
    public static final int GL_FRAGMENT_SHADER_BIT = 2;
    public static final int GL_COMPUTE_SHADER_BIT = 32;
    public static final int GL_ALL_SHADER_BITS = -1;
    public static final int GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT = 1;
    public static final int GL_ELEMENT_ARRAY_BARRIER_BIT = 2;
    public static final int GL_UNIFORM_BARRIER_BIT = 4;
    public static final int GL_TEXTURE_FETCH_BARRIER_BIT = 8;
    public static final int GL_SHADER_IMAGE_ACCESS_BARRIER_BIT = 32;
    public static final int GL_COMMAND_BARRIER_BIT = 64;
    public static final int GL_PIXEL_BUFFER_BARRIER_BIT = 128;
    public static final int GL_TEXTURE_UPDATE_BARRIER_BIT = 256;
    public static final int GL_BUFFER_UPDATE_BARRIER_BIT = 512;
    public static final int GL_FRAMEBUFFER_BARRIER_BIT = 1024;
    public static final int GL_TRANSFORM_FEEDBACK_BARRIER_BIT = 2048;
    public static final int GL_ATOMIC_COUNTER_BARRIER_BIT = 4096;
    public static final int GL_SHADER_STORAGE_BARRIER_BIT = 8192;
    public static final int GL_ALL_BARRIER_BITS = -1;
    public static final int GL_TEXTURE_WIDTH = 4096;
    public static final int GL_TEXTURE_HEIGHT = 4097;
    public static final int GL_TEXTURE_INTERNAL_FORMAT = 4099;
    public static final int GL_STENCIL_INDEX = 6401;
    public static final int GL_TEXTURE_RED_SIZE = 32860;
    public static final int GL_TEXTURE_GREEN_SIZE = 32861;
    public static final int GL_TEXTURE_BLUE_SIZE = 32862;
    public static final int GL_TEXTURE_ALPHA_SIZE = 32863;
    public static final int GL_TEXTURE_DEPTH = 32881;
    public static final int GL_PROGRAM_SEPARABLE = 33368;
    public static final int GL_ACTIVE_PROGRAM = 33369;
    public static final int GL_PROGRAM_PIPELINE_BINDING = 33370;
    public static final int GL_MAX_COMPUTE_SHARED_MEMORY_SIZE = 33378;
    public static final int GL_MAX_COMPUTE_UNIFORM_COMPONENTS = 33379;
    public static final int GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS = 33380;
    public static final int GL_MAX_COMPUTE_ATOMIC_COUNTERS = 33381;
    public static final int GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS = 33382;
    public static final int GL_COMPUTE_WORK_GROUP_SIZE = 33383;
    public static final int GL_MAX_UNIFORM_LOCATIONS = 33390;
    public static final int GL_VERTEX_ATTRIB_BINDING = 33492;
    public static final int GL_VERTEX_ATTRIB_RELATIVE_OFFSET = 33493;
    public static final int GL_VERTEX_BINDING_DIVISOR = 33494;
    public static final int GL_VERTEX_BINDING_OFFSET = 33495;
    public static final int GL_VERTEX_BINDING_STRIDE = 33496;
    public static final int GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET = 33497;
    public static final int GL_MAX_VERTEX_ATTRIB_BINDINGS = 33498;
    public static final int GL_MAX_VERTEX_ATTRIB_STRIDE = 33509;
    public static final int GL_TEXTURE_COMPRESSED = 34465;
    public static final int GL_TEXTURE_DEPTH_SIZE = 34890;
    public static final int GL_READ_ONLY = 35000;
    public static final int GL_WRITE_ONLY = 35001;
    public static final int GL_READ_WRITE = 35002;
    public static final int GL_TEXTURE_STENCIL_SIZE = 35057;
    public static final int GL_TEXTURE_RED_TYPE = 35856;
    public static final int GL_TEXTURE_GREEN_TYPE = 35857;
    public static final int GL_TEXTURE_BLUE_TYPE = 35858;
    public static final int GL_TEXTURE_ALPHA_TYPE = 35859;
    public static final int GL_TEXTURE_DEPTH_TYPE = 35862;
    public static final int GL_TEXTURE_SHARED_SIZE = 35903;
    public static final int GL_SAMPLE_POSITION = 36432;
    public static final int GL_SAMPLE_MASK = 36433;
    public static final int GL_SAMPLE_MASK_VALUE = 36434;
    public static final int GL_MAX_SAMPLE_MASK_WORDS = 36441;
    public static final int GL_MIN_PROGRAM_TEXTURE_GATHER_OFFSET = 36446;
    public static final int GL_MAX_PROGRAM_TEXTURE_GATHER_OFFSET = 36447;
    public static final int GL_MAX_IMAGE_UNITS = 36664;
    public static final int GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES = 36665;
    public static final int GL_IMAGE_BINDING_NAME = 36666;
    public static final int GL_IMAGE_BINDING_LEVEL = 36667;
    public static final int GL_IMAGE_BINDING_LAYERED = 36668;
    public static final int GL_IMAGE_BINDING_LAYER = 36669;
    public static final int GL_IMAGE_BINDING_ACCESS = 36670;
    public static final int GL_DRAW_INDIRECT_BUFFER = 36671;
    public static final int GL_DRAW_INDIRECT_BUFFER_BINDING = 36675;
    public static final int GL_VERTEX_BINDING_BUFFER = 36687;
    public static final int GL_IMAGE_2D = 36941;
    public static final int GL_IMAGE_3D = 36942;
    public static final int GL_IMAGE_CUBE = 36944;
    public static final int GL_IMAGE_2D_ARRAY = 36947;
    public static final int GL_INT_IMAGE_2D = 36952;
    public static final int GL_INT_IMAGE_3D = 36953;
    public static final int GL_INT_IMAGE_CUBE = 36955;
    public static final int GL_INT_IMAGE_2D_ARRAY = 36958;
    public static final int GL_UNSIGNED_INT_IMAGE_2D = 36963;
    public static final int GL_UNSIGNED_INT_IMAGE_3D = 36964;
    public static final int GL_UNSIGNED_INT_IMAGE_CUBE = 36966;
    public static final int GL_UNSIGNED_INT_IMAGE_2D_ARRAY = 36969;
    public static final int GL_IMAGE_BINDING_FORMAT = 36974;
    public static final int GL_IMAGE_FORMAT_COMPATIBILITY_TYPE = 37063;
    public static final int GL_IMAGE_FORMAT_COMPATIBILITY_BY_SIZE = 37064;
    public static final int GL_IMAGE_FORMAT_COMPATIBILITY_BY_CLASS = 37065;
    public static final int GL_MAX_VERTEX_IMAGE_UNIFORMS = 37066;
    public static final int GL_MAX_FRAGMENT_IMAGE_UNIFORMS = 37070;
    public static final int GL_MAX_COMBINED_IMAGE_UNIFORMS = 37071;
    public static final int GL_SHADER_STORAGE_BUFFER = 37074;
    public static final int GL_SHADER_STORAGE_BUFFER_BINDING = 37075;
    public static final int GL_SHADER_STORAGE_BUFFER_START = 37076;
    public static final int GL_SHADER_STORAGE_BUFFER_SIZE = 37077;
    public static final int GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS = 37078;
    public static final int GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS = 37082;
    public static final int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS = 37083;
    public static final int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS = 37084;
    public static final int GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS = 37085;
    public static final int GL_MAX_SHADER_STORAGE_BLOCK_SIZE = 37086;
    public static final int GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT = 37087;
    public static final int GL_DEPTH_STENCIL_TEXTURE_MODE = 37098;
    public static final int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 37099;
    public static final int GL_DISPATCH_INDIRECT_BUFFER = 37102;
    public static final int GL_DISPATCH_INDIRECT_BUFFER_BINDING = 37103;
    public static final int GL_TEXTURE_2D_MULTISAMPLE = 37120;
    public static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE = 37124;
    public static final int GL_TEXTURE_SAMPLES = 37126;
    public static final int GL_TEXTURE_FIXED_SAMPLE_LOCATIONS = 37127;
    public static final int GL_SAMPLER_2D_MULTISAMPLE = 37128;
    public static final int GL_INT_SAMPLER_2D_MULTISAMPLE = 37129;
    public static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE = 37130;
    public static final int GL_MAX_COLOR_TEXTURE_SAMPLES = 37134;
    public static final int GL_MAX_DEPTH_TEXTURE_SAMPLES = 37135;
    public static final int GL_MAX_INTEGER_SAMPLES = 37136;
    public static final int GL_COMPUTE_SHADER = 37305;
    public static final int GL_MAX_COMPUTE_UNIFORM_BLOCKS = 37307;
    public static final int GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS = 37308;
    public static final int GL_MAX_COMPUTE_IMAGE_UNIFORMS = 37309;
    public static final int GL_MAX_COMPUTE_WORK_GROUP_COUNT = 37310;
    public static final int GL_MAX_COMPUTE_WORK_GROUP_SIZE = 37311;
    public static final int GL_ATOMIC_COUNTER_BUFFER = 37568;
    public static final int GL_ATOMIC_COUNTER_BUFFER_BINDING = 37569;
    public static final int GL_ATOMIC_COUNTER_BUFFER_START = 37570;
    public static final int GL_ATOMIC_COUNTER_BUFFER_SIZE = 37571;
    public static final int GL_MAX_VERTEX_ATOMIC_COUNTER_BUFFERS = 37580;
    public static final int GL_MAX_FRAGMENT_ATOMIC_COUNTER_BUFFERS = 37584;
    public static final int GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS = 37585;
    public static final int GL_MAX_VERTEX_ATOMIC_COUNTERS = 37586;
    public static final int GL_MAX_FRAGMENT_ATOMIC_COUNTERS = 37590;
    public static final int GL_MAX_COMBINED_ATOMIC_COUNTERS = 37591;
    public static final int GL_MAX_ATOMIC_COUNTER_BUFFER_SIZE = 37592;
    public static final int GL_ACTIVE_ATOMIC_COUNTER_BUFFERS = 37593;
    public static final int GL_UNSIGNED_INT_ATOMIC_COUNTER = 37595;
    public static final int GL_MAX_ATOMIC_COUNTER_BUFFER_BINDINGS = 37596;
    public static final int GL_UNIFORM = 37601;
    public static final int GL_UNIFORM_BLOCK = 37602;
    public static final int GL_PROGRAM_INPUT = 37603;
    public static final int GL_PROGRAM_OUTPUT = 37604;
    public static final int GL_BUFFER_VARIABLE = 37605;
    public static final int GL_SHADER_STORAGE_BLOCK = 37606;
    public static final int GL_TRANSFORM_FEEDBACK_VARYING = 37620;
    public static final int GL_ACTIVE_RESOURCES = 37621;
    public static final int GL_MAX_NAME_LENGTH = 37622;
    public static final int GL_MAX_NUM_ACTIVE_VARIABLES = 37623;
    public static final int GL_NAME_LENGTH = 37625;
    public static final int GL_TYPE = 37626;
    public static final int GL_ARRAY_SIZE = 37627;
    public static final int GL_OFFSET = 37628;
    public static final int GL_BLOCK_INDEX = 37629;
    public static final int GL_ARRAY_STRIDE = 37630;
    public static final int GL_MATRIX_STRIDE = 37631;
    public static final int GL_IS_ROW_MAJOR = 37632;
    public static final int GL_ATOMIC_COUNTER_BUFFER_INDEX = 37633;
    public static final int GL_BUFFER_BINDING = 37634;
    public static final int GL_BUFFER_DATA_SIZE = 37635;
    public static final int GL_NUM_ACTIVE_VARIABLES = 37636;
    public static final int GL_ACTIVE_VARIABLES = 37637;
    public static final int GL_REFERENCED_BY_VERTEX_SHADER = 37638;
    public static final int GL_REFERENCED_BY_FRAGMENT_SHADER = 37642;
    public static final int GL_REFERENCED_BY_COMPUTE_SHADER = 37643;
    public static final int GL_TOP_LEVEL_ARRAY_SIZE = 37644;
    public static final int GL_TOP_LEVEL_ARRAY_STRIDE = 37645;
    public static final int GL_LOCATION = 37646;
    public static final int GL_FRAMEBUFFER_DEFAULT_WIDTH = 37648;
    public static final int GL_FRAMEBUFFER_DEFAULT_HEIGHT = 37649;
    public static final int GL_FRAMEBUFFER_DEFAULT_SAMPLES = 37651;
    public static final int GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS = 37652;
    public static final int GL_MAX_FRAMEBUFFER_WIDTH = 37653;
    public static final int GL_MAX_FRAMEBUFFER_HEIGHT = 37654;
    public static final int GL_MAX_FRAMEBUFFER_SAMPLES = 37656;

    public void glDispatchCompute(int var1, int var2, int var3);

    public void glDispatchComputeIndirect(long var1);

    public void glDrawArraysIndirect(int var1, long var2);

    public void glDrawElementsIndirect(int var1, int var2, long var3);

    public void glFramebufferParameteri(int var1, int var2, int var3);

    public void glGetFramebufferParameteriv(int var1, int var2, IntBuffer var3);

    public void glGetProgramInterfaceiv(int var1, int var2, int var3, IntBuffer var4);

    public int glGetProgramResourceIndex(int var1, int var2, String var3);

    public String glGetProgramResourceName(int var1, int var2, int var3);

    public void glGetProgramResourceiv(int var1, int var2, int var3, IntBuffer var4, IntBuffer var5, IntBuffer var6);

    public int glGetProgramResourceLocation(int var1, int var2, String var3);

    public void glUseProgramStages(int var1, int var2, int var3);

    public void glActiveShaderProgram(int var1, int var2);

    public int glCreateShaderProgramv(int var1, String[] var2);

    public void glBindProgramPipeline(int var1);

    public void glDeleteProgramPipelines(int var1, IntBuffer var2);

    public void glGenProgramPipelines(int var1, IntBuffer var2);

    public boolean glIsProgramPipeline(int var1);

    public void glGetProgramPipelineiv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform1i(int var1, int var2, int var3);

    public void glProgramUniform2i(int var1, int var2, int var3, int var4);

    public void glProgramUniform3i(int var1, int var2, int var3, int var4, int var5);

    public void glProgramUniform4i(int var1, int var2, int var3, int var4, int var5, int var6);

    public void glProgramUniform1ui(int var1, int var2, int var3);

    public void glProgramUniform2ui(int var1, int var2, int var3, int var4);

    public void glProgramUniform3ui(int var1, int var2, int var3, int var4, int var5);

    public void glProgramUniform4ui(int var1, int var2, int var3, int var4, int var5, int var6);

    public void glProgramUniform1f(int var1, int var2, float var3);

    public void glProgramUniform2f(int var1, int var2, float var3, float var4);

    public void glProgramUniform3f(int var1, int var2, float var3, float var4, float var5);

    public void glProgramUniform4f(int var1, int var2, float var3, float var4, float var5, float var6);

    public void glProgramUniform1iv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform2iv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform3iv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform4iv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform1uiv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform2uiv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform3uiv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform4uiv(int var1, int var2, IntBuffer var3);

    public void glProgramUniform1fv(int var1, int var2, FloatBuffer var3);

    public void glProgramUniform2fv(int var1, int var2, FloatBuffer var3);

    public void glProgramUniform3fv(int var1, int var2, FloatBuffer var3);

    public void glProgramUniform4fv(int var1, int var2, FloatBuffer var3);

    public void glProgramUniformMatrix2fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix3fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix4fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix2x3fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix3x2fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix2x4fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix4x2fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix3x4fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glProgramUniformMatrix4x3fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public void glValidateProgramPipeline(int var1);

    public String glGetProgramPipelineInfoLog(int var1);

    public void glBindImageTexture(int var1, int var2, int var3, boolean var4, int var5, int var6, int var7);

    public void glGetBooleani_v(int var1, int var2, IntBuffer var3);

    public void glMemoryBarrier(int var1);

    public void glMemoryBarrierByRegion(int var1);

    public void glTexStorage2DMultisample(int var1, int var2, int var3, int var4, int var5, boolean var6);

    public void glGetMultisamplefv(int var1, int var2, FloatBuffer var3);

    public void glSampleMaski(int var1, int var2);

    public void glGetTexLevelParameteriv(int var1, int var2, int var3, IntBuffer var4);

    public void glGetTexLevelParameterfv(int var1, int var2, int var3, FloatBuffer var4);

    public void glBindVertexBuffer(int var1, int var2, long var3, int var5);

    public void glVertexAttribFormat(int var1, int var2, int var3, boolean var4, int var5);

    public void glVertexAttribIFormat(int var1, int var2, int var3, int var4);

    public void glVertexAttribBinding(int var1, int var2);

    public void glVertexBindingDivisor(int var1, int var2);
}

