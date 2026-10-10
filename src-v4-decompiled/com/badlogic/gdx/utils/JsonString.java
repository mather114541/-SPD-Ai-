/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Null;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonString {
    private static final int none = 0;
    private static final int needsComma = 1;
    private static final int object = 250;
    private static final int array = 186;
    private static final int isObject = 64;
    final StringBuilder buffer;
    private final IntArray stack = new IntArray();
    private int current;
    private boolean named;
    private JsonWriter.OutputType outputType = JsonWriter.OutputType.json;
    private boolean quoteLongValues;

    public JsonString() {
        this(64);
    }

    public JsonString(int initialBufferSize) {
        this.buffer = new StringBuilder(initialBufferSize);
    }

    public StringBuilder getBuffer() {
        return this.buffer;
    }

    public void setOutputType(JsonWriter.OutputType outputType) {
        this.outputType = outputType;
    }

    public void setQuoteLongValues(boolean quoteLongValues) {
        this.quoteLongValues = quoteLongValues;
    }

    public JsonString object() {
        this.requireCommaOrName();
        this.buffer.append('{');
        this.stack.add(this.current);
        this.current = 250;
        return this;
    }

    public JsonString array() {
        this.requireCommaOrName();
        this.buffer.append('[');
        this.stack.add(this.current);
        this.current = 186;
        return this;
    }

    public JsonString value(@Null Object value) {
        if (this.quoteLongValues && (value instanceof Long || value instanceof Double || value instanceof BigDecimal || value instanceof BigInteger)) {
            value = value.toString();
        } else if (value instanceof Number) {
            Number number = (Number)value;
            long longValue = number.longValue();
            if (number.doubleValue() == (double)longValue) {
                value = longValue;
            }
        }
        this.requireCommaOrName();
        this.buffer.append(this.outputType.quoteValue(value));
        return this;
    }

    public JsonString value(String value) {
        this.requireCommaOrName();
        this.buffer.append(this.outputType.quoteValue(value));
        return this;
    }

    public JsonString value(boolean value) {
        this.requireCommaOrName();
        this.buffer.append(value);
        return this;
    }

    public JsonString value(int value) {
        this.requireCommaOrName();
        this.buffer.append(value);
        return this;
    }

    public JsonString value(long value) {
        if (this.quoteLongValues) {
            this.value(Long.toString(value));
        } else {
            this.requireCommaOrName();
            this.buffer.append(value);
        }
        return this;
    }

    public JsonString value(float value) {
        this.requireCommaOrName();
        this.buffer.append(value);
        return this;
    }

    public JsonString value(double value) {
        if (this.quoteLongValues) {
            this.value(Double.toString(value));
        } else {
            this.requireCommaOrName();
            this.buffer.append(value);
        }
        return this;
    }

    public JsonString json(String json) {
        this.requireCommaOrName();
        this.buffer.append(json);
        return this;
    }

    private void requireCommaOrName() {
        if ((this.current & 0x40) != 0) {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        } else if ((this.current & 1) != 0) {
            this.buffer.append(',');
        } else if (this.current != 0) {
            this.current |= 1;
        }
    }

    public JsonString name(String name) {
        this.nameValue(name);
        this.named = true;
        return this;
    }

    private void nameValue(String name) {
        if ((this.current & 0x40) == 0) {
            throw new IllegalStateException("Current item must be an object.");
        }
        if ((this.current & 1) != 0) {
            this.buffer.append(',');
        } else {
            this.current |= 1;
        }
        this.buffer.append(this.outputType.quoteName(name));
        this.buffer.append(':');
    }

    public JsonString object(String name) {
        this.nameValue(name);
        this.buffer.append('{');
        this.stack.add(this.current);
        this.current = 250;
        return this;
    }

    public JsonString array(String name) {
        this.nameValue(name);
        this.buffer.append('[');
        this.stack.add(this.current);
        this.current = 186;
        return this;
    }

    public JsonString set(String name, Object value) {
        this.name(name);
        this.value(value);
        return this;
    }

    public JsonString set(String name, String value) {
        this.nameValue(name);
        this.buffer.append(this.outputType.quoteValue(value));
        return this;
    }

    public JsonString set(String name, boolean value) {
        this.nameValue(name);
        this.buffer.append(value);
        return this;
    }

    public JsonString set(String name, int value) {
        this.nameValue(name);
        this.buffer.append(value);
        return this;
    }

    public JsonString set(String name, long value) {
        if (this.quoteLongValues) {
            this.set(name, Long.toString(value));
        } else {
            this.nameValue(name);
            this.buffer.append(value);
        }
        return this;
    }

    public JsonString set(String name, float value) {
        this.nameValue(name);
        this.buffer.append(value);
        return this;
    }

    public JsonString set(String name, double value) {
        if (this.quoteLongValues) {
            this.set(name, Double.toString(value));
        } else {
            this.nameValue(name);
            this.buffer.append(value);
        }
        return this;
    }

    public JsonString json(String name, String json) {
        this.nameValue(name);
        this.buffer.append(json);
        return this;
    }

    public JsonString pop() {
        if (this.named) {
            throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        }
        this.buffer.append((char)(this.current >> 1));
        this.current = this.stack.size == 0 ? 0 : this.stack.items[--this.stack.size];
        return this;
    }

    public JsonString close() {
        while (this.stack.size > 0) {
            this.pop();
        }
        return this;
    }

    public void reset() {
        this.buffer.setLength(0);
        this.stack.size = 0;
        this.current = 0;
        this.named = false;
    }

    public String toString() {
        return this.buffer.toString();
    }
}

