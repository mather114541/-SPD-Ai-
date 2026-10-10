/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector4
implements Serializable,
Vector<Vector4> {
    private static final long serialVersionUID = -5394070284130414492L;
    public float x;
    public float y;
    public float z;
    public float w;
    public static final Vector4 X = new Vector4(1.0f, 0.0f, 0.0f, 0.0f);
    public static final Vector4 Y = new Vector4(0.0f, 1.0f, 0.0f, 0.0f);
    public static final Vector4 Z = new Vector4(0.0f, 0.0f, 1.0f, 0.0f);
    public static final Vector4 W = new Vector4(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Vector4 Zero = new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Vector4 One = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);

    public Vector4() {
    }

    public Vector4(float x, float y, float z, float w) {
        this.set(x, y, z, w);
    }

    public Vector4(Vector4 vector) {
        this.set(vector.x, vector.y, vector.z, vector.w);
    }

    public Vector4(float[] values) {
        this.set(values[0], values[1], values[2], values[3]);
    }

    public Vector4(Vector2 vector, float z, float w) {
        this.set(vector.x, vector.y, z, w);
    }

    public Vector4(Vector3 vector, float w) {
        this.set(vector.x, vector.y, vector.z, w);
    }

    public Vector4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @Override
    public Vector4 set(Vector4 vector) {
        return this.set(vector.x, vector.y, vector.z, vector.w);
    }

    @Override
    public Vector4 set(float[] values) {
        return this.set(values[0], values[1], values[2], values[3]);
    }

    public Vector4 set(Vector2 vector, float z, float w) {
        return this.set(vector.x, vector.y, z, w);
    }

    public Vector4 set(Vector3 vector, float w) {
        return this.set(vector.x, vector.y, vector.z, w);
    }

    @Override
    public Vector4 setToRandomDirection() {
        float v2;
        float v1;
        float s;
        while ((s = (v1 = (MathUtils.random() - 0.5f) * 2.0f) * v1 + (v2 = (MathUtils.random() - 0.5f) * 2.0f) * v2) >= 1.0f || s == 0.0f) {
        }
        float multiplier = (float)Math.sqrt(-2.0 * Math.log(s) / (double)s);
        this.x = v1 * multiplier;
        this.y = v2 * multiplier;
        while ((s = (v1 = (MathUtils.random() - 0.5f) * 2.0f) * v1 + (v2 = (MathUtils.random() - 0.5f) * 2.0f) * v2) >= 1.0f || s == 0.0f) {
        }
        multiplier = (float)Math.sqrt(-2.0 * Math.log(s) / (double)s);
        this.z = v1 * multiplier;
        this.w = v2 * multiplier;
        return this.nor();
    }

    @Override
    public Vector4 cpy() {
        return new Vector4(this);
    }

    @Override
    public Vector4 add(Vector4 vector) {
        return this.add(vector.x, vector.y, vector.z, vector.w);
    }

    public Vector4 add(float x, float y, float z, float w) {
        return this.set(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    @Override
    public Vector4 add(float values) {
        return this.set(this.x + values, this.y + values, this.z + values, this.w + values);
    }

    @Override
    public Vector4 sub(Vector4 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z, a_vec.w);
    }

    public Vector4 sub(float x, float y, float z, float w) {
        return this.set(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    @Override
    public Vector4 sub(float value) {
        return this.set(this.x - value, this.y - value, this.z - value, this.w - value);
    }

    @Override
    public Vector4 scl(float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }

    @Override
    public Vector4 scl(Vector4 other) {
        return this.set(this.x * other.x, this.y * other.y, this.z * other.z, this.w * other.w);
    }

    public Vector4 scl(float vx, float vy, float vz, float vw) {
        return this.set(this.x * vx, this.y * vy, this.z * vz, this.w * vw);
    }

    @Override
    public Vector4 mulAdd(Vector4 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        this.w += vec.w * scalar;
        return this;
    }

    @Override
    public Vector4 mulAdd(Vector4 vec, Vector4 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        this.w += vec.w * mulVec.w;
        return this;
    }

    public static float len(float x, float y, float z, float w) {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }

    @Override
    public float len() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public static float len2(float x, float y, float z, float w) {
        return x * x + y * y + z * z + w * w;
    }

    @Override
    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public boolean idt(Vector4 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z && this.w == vector.w;
    }

    public static float dst(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        float d = w2 - w1;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    @Override
    public float dst(Vector4 vector) {
        float a = vector.x - this.x;
        float b = vector.y - this.y;
        float c = vector.z - this.z;
        float d = vector.w - this.w;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    public float dst(float x, float y, float z, float w) {
        float a = x - this.x;
        float b = y - this.y;
        float c = z - this.z;
        float d = w - this.w;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    public static float dst2(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        float d = w2 - w1;
        return a * a + b * b + c * c + d * d;
    }

    @Override
    public float dst2(Vector4 point) {
        float a = point.x - this.x;
        float b = point.y - this.y;
        float c = point.z - this.z;
        float d = point.w - this.w;
        return a * a + b * b + c * c + d * d;
    }

    public float dst2(float x, float y, float z, float w) {
        float a = x - this.x;
        float b = y - this.y;
        float c = z - this.z;
        float d = w - this.w;
        return a * a + b * b + c * c + d * d;
    }

    @Override
    public Vector4 nor() {
        float len2 = this.len2();
        if (len2 == 0.0f || len2 == 1.0f) {
            return this;
        }
        return this.scl(1.0f / (float)Math.sqrt(len2));
    }

    public static float dot(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
    }

    @Override
    public float dot(Vector4 vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z + this.w * vector.w;
    }

    public float dot(float x, float y, float z, float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }

    @Override
    public boolean isUnit() {
        return this.isUnit(1.0E-9f);
    }

    @Override
    public boolean isUnit(float margin) {
        return Math.abs(this.len2() - 1.0f) < margin;
    }

    @Override
    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f && this.w == 0.0f;
    }

    @Override
    public boolean isZero(float margin) {
        return this.len2() < margin;
    }

    @Override
    public boolean isOnLine(Vector4 other, float epsilon) {
        int flags = 0;
        float dx = 0.0f;
        float dy = 0.0f;
        float dz = 0.0f;
        float dw = 0.0f;
        if (MathUtils.isZero(this.x, epsilon)) {
            if (!MathUtils.isZero(other.x, epsilon)) {
                return false;
            }
        } else {
            dx = this.x / other.x;
            flags |= 1;
        }
        if (MathUtils.isZero(this.y, epsilon)) {
            if (!MathUtils.isZero(other.y, epsilon)) {
                return false;
            }
        } else {
            dy = this.y / other.y;
            flags |= 2;
        }
        if (MathUtils.isZero(this.z, epsilon)) {
            if (!MathUtils.isZero(other.z, epsilon)) {
                return false;
            }
        } else {
            dz = this.z / other.z;
            flags |= 4;
        }
        if (MathUtils.isZero(this.w, epsilon)) {
            if (!MathUtils.isZero(other.w, epsilon)) {
                return false;
            }
        } else {
            dw = this.w / other.w;
            flags |= 8;
        }
        switch (flags) {
            case 0: 
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                return true;
            }
            case 3: {
                return MathUtils.isEqual(dx, dy, epsilon);
            }
            case 5: {
                return MathUtils.isEqual(dx, dz, epsilon);
            }
            case 9: {
                return MathUtils.isEqual(dx, dw, epsilon);
            }
            case 6: {
                return MathUtils.isEqual(dy, dz, epsilon);
            }
            case 10: {
                return MathUtils.isEqual(dy, dw, epsilon);
            }
            case 12: {
                return MathUtils.isEqual(dz, dw, epsilon);
            }
            case 7: {
                return MathUtils.isEqual(dx, dy, epsilon) && MathUtils.isEqual(dx, dz, epsilon);
            }
            case 11: {
                return MathUtils.isEqual(dx, dy, epsilon) && MathUtils.isEqual(dx, dw, epsilon);
            }
            case 13: {
                return MathUtils.isEqual(dx, dz, epsilon) && MathUtils.isEqual(dx, dw, epsilon);
            }
            case 14: {
                return MathUtils.isEqual(dy, dz, epsilon) && MathUtils.isEqual(dy, dw, epsilon);
            }
        }
        return MathUtils.isEqual(dx, dy, epsilon) && MathUtils.isEqual(dx, dz, epsilon) && MathUtils.isEqual(dx, dw, epsilon);
    }

    @Override
    public boolean isOnLine(Vector4 other) {
        return this.isOnLine(other, 1.0E-6f);
    }

    @Override
    public boolean isCollinear(Vector4 other, float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasSameDirection(other);
    }

    @Override
    public boolean isCollinear(Vector4 other) {
        return this.isOnLine(other) && this.hasSameDirection(other);
    }

    @Override
    public boolean isCollinearOpposite(Vector4 other, float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasOppositeDirection(other);
    }

    @Override
    public boolean isCollinearOpposite(Vector4 other) {
        return this.isOnLine(other) && this.hasOppositeDirection(other);
    }

    @Override
    public boolean isPerpendicular(Vector4 vector) {
        return MathUtils.isZero(this.dot(vector));
    }

    @Override
    public boolean isPerpendicular(Vector4 vector, float epsilon) {
        return MathUtils.isZero(this.dot(vector), epsilon);
    }

    @Override
    public boolean hasSameDirection(Vector4 vector) {
        return this.dot(vector) > 0.0f;
    }

    @Override
    public boolean hasOppositeDirection(Vector4 vector) {
        return this.dot(vector) < 0.0f;
    }

    @Override
    public Vector4 lerp(Vector4 target, float alpha) {
        this.x += alpha * (target.x - this.x);
        this.y += alpha * (target.y - this.y);
        this.z += alpha * (target.z - this.z);
        this.w += alpha * (target.w - this.w);
        return this;
    }

    @Override
    public Vector4 interpolate(Vector4 target, float alpha, Interpolation interpolator) {
        return this.lerp(target, interpolator.apply(alpha));
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + "," + this.w + ")";
    }

    public Vector4 fromString(String v) {
        int s0 = v.indexOf(44, 1);
        int s1 = v.indexOf(44, s0 + 1);
        int s2 = v.indexOf(44, s1 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                float x = Float.parseFloat(v.substring(1, s0));
                float y = Float.parseFloat(v.substring(s0 + 1, s1));
                float z = Float.parseFloat(v.substring(s1 + 1, s2));
                float w = Float.parseFloat(v.substring(s2 + 1, v.length() - 1));
                return this.set(x, y, z, w);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new GdxRuntimeException("Malformed Vector4: " + v);
    }

    @Override
    public Vector4 limit(float limit) {
        return this.limit2(limit * limit);
    }

    @Override
    public Vector4 limit2(float limit2) {
        float len2 = this.len2();
        if (len2 > limit2) {
            this.scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    @Override
    public Vector4 setLength(float len) {
        return this.setLength2(len * len);
    }

    @Override
    public Vector4 setLength2(float len2) {
        float oldLen2 = this.len2();
        return oldLen2 == 0.0f || oldLen2 == len2 ? this : this.scl((float)Math.sqrt(len2 / oldLen2));
    }

    @Override
    public Vector4 clamp(float min, float max) {
        float len2 = this.len2();
        if (len2 == 0.0f) {
            return this;
        }
        float max2 = max * max;
        if (len2 > max2) {
            return this.scl((float)Math.sqrt(max2 / len2));
        }
        float min2 = min * min;
        if (len2 < min2) {
            return this.scl((float)Math.sqrt(min2 / len2));
        }
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + NumberUtils.floatToIntBits(this.x);
        result = 31 * result + NumberUtils.floatToIntBits(this.y);
        result = 31 * result + NumberUtils.floatToIntBits(this.z);
        result = 31 * result + NumberUtils.floatToIntBits(this.w);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Vector4 other = (Vector4)obj;
        if (NumberUtils.floatToIntBits(this.x) != NumberUtils.floatToIntBits(other.x)) {
            return false;
        }
        if (NumberUtils.floatToIntBits(this.y) != NumberUtils.floatToIntBits(other.y)) {
            return false;
        }
        if (NumberUtils.floatToIntBits(this.z) != NumberUtils.floatToIntBits(other.z)) {
            return false;
        }
        return NumberUtils.floatToIntBits(this.w) == NumberUtils.floatToIntBits(other.w);
    }

    @Override
    public boolean epsilonEquals(Vector4 other, float epsilon) {
        if (other == null) {
            return false;
        }
        if (Math.abs(other.x - this.x) > epsilon) {
            return false;
        }
        if (Math.abs(other.y - this.y) > epsilon) {
            return false;
        }
        if (Math.abs(other.z - this.z) > epsilon) {
            return false;
        }
        return !(Math.abs(other.w - this.w) > epsilon);
    }

    public boolean epsilonEquals(float x, float y, float z, float w, float epsilon) {
        if (Math.abs(x - this.x) > epsilon) {
            return false;
        }
        if (Math.abs(y - this.y) > epsilon) {
            return false;
        }
        if (Math.abs(z - this.z) > epsilon) {
            return false;
        }
        return !(Math.abs(w - this.w) > epsilon);
    }

    public boolean epsilonEquals(Vector4 other) {
        return this.epsilonEquals(other, 1.0E-6f);
    }

    public boolean epsilonEquals(float x, float y, float z, float w) {
        return this.epsilonEquals(x, y, z, w, 1.0E-6f);
    }

    @Override
    public Vector4 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
        return this;
    }
}

