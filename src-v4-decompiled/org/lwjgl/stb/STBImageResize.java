/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.stb;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.stb.LibSTB;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class STBImageResize {
    public static final int STBIR_ALPHA_CHANNEL_NONE = -1;
    public static final int STBIR_FLAG_ALPHA_PREMULTIPLIED = 1;
    public static final int STBIR_FLAG_ALPHA_USES_COLORSPACE = 2;
    public static final int STBIR_EDGE_CLAMP = 1;
    public static final int STBIR_EDGE_REFLECT = 2;
    public static final int STBIR_EDGE_WRAP = 3;
    public static final int STBIR_EDGE_ZERO = 4;
    public static final int STBIR_FILTER_DEFAULT = 0;
    public static final int STBIR_FILTER_BOX = 1;
    public static final int STBIR_FILTER_TRIANGLE = 2;
    public static final int STBIR_FILTER_CUBICBSPLINE = 3;
    public static final int STBIR_FILTER_CATMULLROM = 4;
    public static final int STBIR_FILTER_MITCHELL = 5;
    public static final int STBIR_COLORSPACE_LINEAR = 0;
    public static final int STBIR_COLORSPACE_SRGB = 1;
    public static final int STBIR_TYPE_UINT8 = 0;
    public static final int STBIR_TYPE_UINT16 = 1;
    public static final int STBIR_TYPE_UINT32 = 2;
    public static final int STBIR_TYPE_FLOAT = 3;

    protected STBImageResize() {
        throw new UnsupportedOperationException();
    }

    public static native int nstbir_resize_uint8(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10);

    @NativeType(value="int")
    public static boolean stbir_resize_uint8(@NativeType(value="unsigned char const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="unsigned char *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_uint8(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels) != 0;
    }

    public static native int nstbir_resize_float(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10);

    @NativeType(value="int")
    public static boolean stbir_resize_float(@NativeType(value="float const *") FloatBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="float *") FloatBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 2));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 2));
        }
        return STBImageResize.nstbir_resize_float(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels) != 0;
    }

    public static native int nstbir_resize_uint8_srgb(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12);

    @NativeType(value="int")
    public static boolean stbir_resize_uint8_srgb(@NativeType(value="unsigned char const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="unsigned char *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_uint8_srgb(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags) != 0;
    }

    public static native int nstbir_resize_uint8_srgb_edgemode(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13);

    @NativeType(value="int")
    public static boolean stbir_resize_uint8_srgb_edgemode(@NativeType(value="unsigned char const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="unsigned char *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_uint8_srgb_edgemode(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode) != 0;
    }

    public static native int nstbir_resize_uint8_generic(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, long var16);

    @NativeType(value="int")
    public static boolean stbir_resize_uint8_generic(@NativeType(value="unsigned char const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="unsigned char *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode, @NativeType(value="stbir_filter") int filter, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_uint8_generic(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode, filter, space, 0L) != 0;
    }

    public static native int nstbir_resize_uint16_generic(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, long var16);

    @NativeType(value="int")
    public static boolean stbir_resize_uint16_generic(@NativeType(value="stbir_uint16 const *") ShortBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="stbir_uint16 *") ShortBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode, @NativeType(value="stbir_filter") int filter, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 1));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 1));
        }
        return STBImageResize.nstbir_resize_uint16_generic(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode, filter, space, 0L) != 0;
    }

    public static native int nstbir_resize_float_generic(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, long var16);

    @NativeType(value="int")
    public static boolean stbir_resize_float_generic(@NativeType(value="float const *") FloatBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="float *") FloatBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode, @NativeType(value="stbir_filter") int filter, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 2));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 2));
        }
        return STBImageResize.nstbir_resize_float_generic(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode, filter, space, 0L) != 0;
    }

    public static native int nstbir_resize(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, int var18, long var19);

    @NativeType(value="int")
    public static boolean stbir_resize(@NativeType(value="void const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="void *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, @NativeType(value="stbir_datatype") int datatype, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_mode_horizontal, @NativeType(value="stbir_edge") int edge_mode_vertical, @NativeType(value="stbir_filter") int filter_horizontal, @NativeType(value="stbir_filter") int filter_vertical, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels << STBImageResize.getTypeShift(datatype) : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels << STBImageResize.getTypeShift(datatype) : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, datatype, num_channels, alpha_channel, flags, edge_mode_horizontal, edge_mode_vertical, filter_horizontal, filter_vertical, space, 0L) != 0;
    }

    public static native int nstbir_resize_subpixel(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, int var18, long var19, float var21, float var22, float var23, float var24);

    @NativeType(value="int")
    public static boolean stbir_resize_subpixel(@NativeType(value="void const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="void *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, @NativeType(value="stbir_datatype") int datatype, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_mode_horizontal, @NativeType(value="stbir_edge") int edge_mode_vertical, @NativeType(value="stbir_filter") int filter_horizontal, @NativeType(value="stbir_filter") int filter_vertical, @NativeType(value="stbir_colorspace") int space, float x_scale, float y_scale, float x_offset, float y_offset) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels << STBImageResize.getTypeShift(datatype) : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels << STBImageResize.getTypeShift(datatype) : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_subpixel(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, datatype, num_channels, alpha_channel, flags, edge_mode_horizontal, edge_mode_vertical, filter_horizontal, filter_vertical, space, 0L, x_scale, y_scale, x_offset, y_offset) != 0;
    }

    public static native int nstbir_resize_region(long var0, int var2, int var3, int var4, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, int var18, long var19, float var21, float var22, float var23, float var24);

    @NativeType(value="int")
    public static boolean stbir_resize_region(@NativeType(value="void const *") ByteBuffer input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="void *") ByteBuffer output_pixels, int output_w, int output_h, int output_stride_in_bytes, @NativeType(value="stbir_datatype") int datatype, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_mode_horizontal, @NativeType(value="stbir_edge") int edge_mode_vertical, @NativeType(value="stbir_filter") int filter_horizontal, @NativeType(value="stbir_filter") int filter_vertical, @NativeType(value="stbir_colorspace") int space, float s0, float t0, float s1, float t1) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels << STBImageResize.getTypeShift(datatype) : input_stride_in_bytes));
            Checks.check((Buffer)output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels << STBImageResize.getTypeShift(datatype) : output_stride_in_bytes));
        }
        return STBImageResize.nstbir_resize_region(MemoryUtil.memAddress(input_pixels), input_w, input_h, input_stride_in_bytes, MemoryUtil.memAddress(output_pixels), output_w, output_h, output_stride_in_bytes, datatype, num_channels, alpha_channel, flags, edge_mode_horizontal, edge_mode_vertical, filter_horizontal, filter_vertical, space, 0L, s0, t0, s1, t1) != 0;
    }

    public static native int nstbir_resize_float(float[] var0, int var1, int var2, int var3, float[] var4, int var5, int var6, int var7, int var8);

    @NativeType(value="int")
    public static boolean stbir_resize_float(@NativeType(value="float const *") float[] input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="float *") float[] output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels) {
        if (Checks.CHECKS) {
            Checks.check(input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 2));
            Checks.check(output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 2));
        }
        return STBImageResize.nstbir_resize_float(input_pixels, input_w, input_h, input_stride_in_bytes, output_pixels, output_w, output_h, output_stride_in_bytes, num_channels) != 0;
    }

    public static native int nstbir_resize_uint16_generic(short[] var0, int var1, int var2, int var3, short[] var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, long var14);

    @NativeType(value="int")
    public static boolean stbir_resize_uint16_generic(@NativeType(value="stbir_uint16 const *") short[] input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="stbir_uint16 *") short[] output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode, @NativeType(value="stbir_filter") int filter, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check(input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 1));
            Checks.check(output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 1));
        }
        return STBImageResize.nstbir_resize_uint16_generic(input_pixels, input_w, input_h, input_stride_in_bytes, output_pixels, output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode, filter, space, 0L) != 0;
    }

    public static native int nstbir_resize_float_generic(float[] var0, int var1, int var2, int var3, float[] var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, long var14);

    @NativeType(value="int")
    public static boolean stbir_resize_float_generic(@NativeType(value="float const *") float[] input_pixels, int input_w, int input_h, int input_stride_in_bytes, @NativeType(value="float *") float[] output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels, int alpha_channel, int flags, @NativeType(value="stbir_edge") int edge_wrap_mode, @NativeType(value="stbir_filter") int filter, @NativeType(value="stbir_colorspace") int space) {
        if (Checks.CHECKS) {
            Checks.check(input_pixels, input_h * (input_stride_in_bytes == 0 ? input_w * num_channels : input_stride_in_bytes >> 2));
            Checks.check(output_pixels, output_h * (output_stride_in_bytes == 0 ? output_w * num_channels : output_stride_in_bytes >> 2));
        }
        return STBImageResize.nstbir_resize_float_generic(input_pixels, input_w, input_h, input_stride_in_bytes, output_pixels, output_w, output_h, output_stride_in_bytes, num_channels, alpha_channel, flags, edge_wrap_mode, filter, space, 0L) != 0;
    }

    private static int getTypeShift(int type) {
        switch (type) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
        }
        return 2;
    }

    static {
        LibSTB.initialize();
    }
}

