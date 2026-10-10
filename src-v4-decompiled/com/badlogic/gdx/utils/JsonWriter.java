/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Null;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class JsonWriter
extends Writer {
    private static final int none = 0;
    private static final int needsComma = 1;
    private static final int object = 250;
    private static final int array = 186;
    private static final int isObject = 64;
    Writer writer;
    private final IntArray stack = new IntArray();
    private int current;
    private boolean named;
    private OutputType outputType = OutputType.json;
    private boolean quoteLongValues;

    public JsonWriter(Writer writer) {
        this.writer = writer;
    }

    protected JsonWriter() {
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public void setQuoteLongValues(boolean quoteLongValues) {
        this.quoteLongValues = quoteLongValues;
    }

    public JsonWriter object() throws IOException {
        this.requireCommaOrName();
        this.writer.write(123);
        this.stack.add(this.current);
        this.current = 250;
        return this;
    }

    public JsonWriter array() throws IOException {
        this.requireCommaOrName();
        this.writer.write(91);
        this.stack.add(this.current);
        this.current = 186;
        return this;
    }

    public JsonWriter value(@Null Object value) throws IOException {
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
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        this.requireCommaOrName();
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        this.requireCommaOrName();
        this.writer.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(int value) throws IOException {
        this.requireCommaOrName();
        this.writer.write(Integer.toString(value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        if (this.quoteLongValues) {
            this.value(Long.toString(value));
        } else {
            this.requireCommaOrName();
            this.writer.write(Long.toString(value));
        }
        return this;
    }

    public JsonWriter value(float value) throws IOException {
        this.requireCommaOrName();
        this.writer.write(Float.toString(value));
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (this.quoteLongValues) {
            this.value(Double.toString(value));
        } else {
            this.requireCommaOrName();
            this.writer.write(Double.toString(value));
        }
        return this;
    }

    public JsonWriter json(String json) throws IOException {
        this.requireCommaOrName();
        this.writer.write(json);
        return this;
    }

    private void requireCommaOrName() throws IOException {
        if ((this.current & 0x40) != 0) {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        } else if ((this.current & 1) != 0) {
            this.writer.write(44);
        } else if (this.current != 0) {
            this.current |= 1;
        }
    }

    public JsonWriter name(String name) throws IOException {
        this.nameValue(name);
        this.named = true;
        return this;
    }

    private void nameValue(String name) throws IOException {
        if ((this.current & 0x40) == 0) {
            throw new IllegalStateException("Current item must be an object.");
        }
        if ((this.current & 1) != 0) {
            this.writer.write(44);
        } else {
            this.current |= 1;
        }
        this.writer.write(this.outputType.quoteName(name));
        this.writer.write(58);
    }

    public JsonWriter object(String name) throws IOException {
        this.nameValue(name);
        this.writer.write(123);
        this.stack.add(this.current);
        this.current = 250;
        return this;
    }

    public JsonWriter array(String name) throws IOException {
        this.nameValue(name);
        this.writer.write(91);
        this.stack.add(this.current);
        this.current = 186;
        return this;
    }

    public JsonWriter set(String name, Object value) throws IOException {
        this.name(name);
        this.value(value);
        return this;
    }

    public JsonWriter set(String name, String value) throws IOException {
        this.nameValue(name);
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }

    public JsonWriter set(String name, boolean value) throws IOException {
        this.nameValue(name);
        this.writer.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter set(String name, int value) throws IOException {
        this.nameValue(name);
        this.writer.write(Integer.toString(value));
        return this;
    }

    public JsonWriter set(String name, long value) throws IOException {
        if (this.quoteLongValues) {
            this.set(name, Long.toString(value));
        } else {
            this.nameValue(name);
            this.writer.write(Long.toString(value));
        }
        return this;
    }

    public JsonWriter set(String name, float value) throws IOException {
        this.nameValue(name);
        this.writer.write(Float.toString(value));
        return this;
    }

    public JsonWriter set(String name, double value) throws IOException {
        if (this.quoteLongValues) {
            this.set(name, Double.toString(value));
        } else {
            this.nameValue(name);
            this.writer.write(Double.toString(value));
        }
        return this;
    }

    public JsonWriter json(String name, String json) throws IOException {
        this.nameValue(name);
        this.writer.write(json);
        return this;
    }

    public JsonWriter pop() throws IOException {
        if (this.named) {
            throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        }
        this.writer.write((char)(this.current >> 1));
        this.current = this.stack.size == 0 ? 0 : this.stack.items[--this.stack.size];
        return this;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        while (this.stack.size > 0) {
            this.pop();
        }
        this.writer.close();
    }

    public static enum OutputType {
        json,
        javascript,
        minimal;

        private static Pattern javascriptPattern;
        private static Pattern minimalNamePattern;
        private static Pattern minimalValuePattern;

        public String quoteValue(@Null Object value) {
            int length;
            if (value == null) {
                return "null";
            }
            String string = value.toString();
            if (value instanceof Number || value instanceof Boolean) {
                return string;
            }
            boolean quote = false;
            block4: for (int i = 0; i < string.length(); ++i) {
                switch (string.charAt(i)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case '\\': {
                        string = OutputType.escape(string, i);
                        quote = true;
                        break block4;
                    }
                    case '\"': {
                        quote = true;
                    }
                    default: {
                        continue block4;
                    }
                }
            }
            if (!(this != minimal || string.equals("true") || string.equals("false") || string.equals("null") || string.contains("//") || string.contains("/*") || (length = string.length()) <= 0 || string.charAt(length - 1) == ' ' || !minimalValuePattern.matcher(string).matches())) {
                return string;
            }
            return quote ? OutputType.escapeQuote(string) : '\"' + string + '\"';
        }

        public String quoteName(String value) {
            boolean quote = false;
            block8: for (int i = 0; i < value.length(); ++i) {
                switch (value.charAt(i)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case '\\': {
                        value = OutputType.escape(value, i);
                        quote = true;
                        break block8;
                    }
                    case '\"': {
                        quote = true;
                    }
                    default: {
                        continue block8;
                    }
                }
            }
            switch (this) {
                case minimal: {
                    if (!value.contains("//") && !value.contains("/*") && minimalNamePattern.matcher(value).matches()) {
                        return value;
                    }
                }
                case javascript: {
                    if (!javascriptPattern.matcher(value).matches()) break;
                    return value;
                }
            }
            return quote ? OutputType.escapeQuote(value) : '\"' + value + '\"';
        }

        private static String escape(String value, int i) {
            StringBuilder buffer = new StringBuilder(value.length() + 6);
            buffer.append(value, 0, i);
            while (i < value.length()) {
                char c = value.charAt(i);
                switch (c) {
                    case '\\': {
                        buffer.append("\\\\");
                        break;
                    }
                    case '\r': {
                        buffer.append("\\r");
                        break;
                    }
                    case '\n': {
                        buffer.append("\\n");
                        break;
                    }
                    case '\t': {
                        buffer.append("\\t");
                        break;
                    }
                    default: {
                        buffer.append(c);
                    }
                }
                ++i;
            }
            return buffer.toString();
        }

        private static String escapeQuote(String value) {
            StringBuilder buffer = new StringBuilder(value.length() + 6);
            buffer.append('\"');
            for (int i = 0; i < value.length(); ++i) {
                char c = value.charAt(i);
                if (c == '\"') {
                    buffer.append("\\\"");
                    continue;
                }
                buffer.append(c);
            }
            buffer.append('\"');
            return buffer.toString();
        }

        static {
            javascriptPattern = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
            minimalNamePattern = Pattern.compile("^[^\":,}/ ][^:]*$");
            minimalValuePattern = Pattern.compile("^[^\":,{\\[\\]/ ][^}\\],]*$");
        }
    }
}

