/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

public class BArray {
    private static boolean[] falseArray;

    public static void setFalse(boolean[] toBeFalse) {
        if (falseArray == null || falseArray.length < toBeFalse.length) {
            falseArray = new boolean[toBeFalse.length];
        }
        System.arraycopy(falseArray, 0, toBeFalse, 0, toBeFalse.length);
    }

    public static boolean[] and(boolean[] a, boolean[] b, boolean[] result) {
        int length = a.length;
        if (result == null) {
            result = new boolean[length];
        }
        for (int i = 0; i < length; ++i) {
            result[i] = a[i] && b[i];
        }
        return result;
    }

    public static boolean[] or(boolean[] a, boolean[] b, boolean[] result) {
        return BArray.or(a, b, 0, a.length, result);
    }

    public static boolean[] or(boolean[] a, boolean[] b, int offset, int length, boolean[] result) {
        if (result == null) {
            result = new boolean[length];
        }
        for (int i = offset; i < offset + length; ++i) {
            result[i] = a[i] || b[i];
        }
        return result;
    }

    public static boolean[] not(boolean[] a, boolean[] result) {
        int length = a.length;
        if (result == null) {
            result = new boolean[length];
        }
        for (int i = 0; i < length; ++i) {
            result[i] = !a[i];
        }
        return result;
    }

    public static boolean[] is(int[] a, boolean[] result, int v1) {
        int length = a.length;
        if (result == null) {
            result = new boolean[length];
        }
        for (int i = 0; i < length; ++i) {
            result[i] = a[i] == v1;
        }
        return result;
    }

    public static boolean[] isOneOf(int[] a, boolean[] result, int ... v) {
        int length = a.length;
        int nv = v.length;
        if (result == null) {
            result = new boolean[length];
        }
        block0: for (int i = 0; i < length; ++i) {
            result[i] = false;
            for (int j = 0; j < nv; ++j) {
                if (a[i] != v[j]) continue;
                result[i] = true;
                continue block0;
            }
        }
        return result;
    }

    public static boolean[] isNot(int[] a, boolean[] result, int v1) {
        int length = a.length;
        if (result == null) {
            result = new boolean[length];
        }
        for (int i = 0; i < length; ++i) {
            result[i] = a[i] != v1;
        }
        return result;
    }

    public static boolean[] isNotOneOf(int[] a, boolean[] result, int ... v) {
        int length = a.length;
        int nv = v.length;
        if (result == null) {
            result = new boolean[length];
        }
        block0: for (int i = 0; i < length; ++i) {
            result[i] = true;
            for (int j = 0; j < nv; ++j) {
                if (a[i] != v[j]) continue;
                result[i] = false;
                continue block0;
            }
        }
        return result;
    }
}

