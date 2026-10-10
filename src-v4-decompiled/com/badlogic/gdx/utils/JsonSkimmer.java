/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

public class JsonSkimmer {
    final JsonToken nameString;
    final JsonToken value;
    int[] stack = new int[8];
    protected final CharArray buffer = new CharArray();
    private static final byte[] _json_actions = JsonSkimmer.init__json_actions_0();
    private static final short[] _json_key_offsets = JsonSkimmer.init__json_key_offsets_0();
    private static final char[] _json_trans_keys = JsonSkimmer.init__json_trans_keys_0();
    private static final byte[] _json_single_lengths = JsonSkimmer.init__json_single_lengths_0();
    private static final byte[] _json_range_lengths = JsonSkimmer.init__json_range_lengths_0();
    private static final short[] _json_index_offsets = JsonSkimmer.init__json_index_offsets_0();
    private static final byte[] _json_indicies = JsonSkimmer.init__json_indicies_0();
    private static final byte[] _json_trans_targs = JsonSkimmer.init__json_trans_targs_0();
    private static final byte[] _json_trans_actions = JsonSkimmer.init__json_trans_actions_0();
    private static final byte[] _json_eof_actions = JsonSkimmer.init__json_eof_actions_0();
    static final int json_start = 1;
    static final int json_first_final = 31;
    static final int json_error = 0;
    static final int json_en_object = 4;
    static final int json_en_array = 20;
    static final int json_en_main = 1;
    protected boolean stop;

    public JsonSkimmer() {
        this.nameString = new JsonToken(this.buffer);
        this.value = new JsonToken(this.buffer);
    }

    public void parse(String json) {
        char[] data = json.toCharArray();
        this.parse(data, 0, data.length);
    }

    public void parse(Reader reader) {
        char[] data = new char[1024];
        int offset = 0;
        try {
            int length;
            while ((length = reader.read(data, offset, data.length - offset)) != -1) {
                if (length == 0) {
                    char[] newData = new char[data.length * 2];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                    continue;
                }
                offset += length;
            }
        }
        catch (IOException ex) {
            throw new SerializationException("Error reading input.", ex);
        }
        finally {
            StreamUtils.closeQuietly(reader);
        }
        this.parse(data, 0, offset);
    }

    public void parse(InputStream input) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(input, "UTF-8");
        }
        catch (Exception ex) {
            throw new SerializationException("Error reading stream.", ex);
        }
        this.parse(reader);
    }

    public void parse(FileHandle file) {
        Reader reader;
        try {
            reader = file.reader("UTF-8");
        }
        catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
        try {
            this.parse(reader);
        }
        catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    /*
     * Unable to fully structure code
     */
    public void parse(char[] data, int offset, int length) {
        this.stop = false;
        p = offset;
        eof = pe = length;
        top = 0;
        stack = this.stack;
        nameString = this.nameString;
        string = value = this.value;
        name = null;
        nameString.chars = data;
        value.chars = data;
        parseRuntimeEx = null;
        debug = false;
        if (debug) {
            System.out.println();
        }
        try {
            cs = 1;
            top = 0;
            _trans = 0;
            _goto_targ = 0;
            block46: while (true) {
                switch (_goto_targ) {
                    case 0: {
                        if (p == pe) {
                            _goto_targ = 4;
                            continue block46;
                        }
                        if (cs == 0) {
                            _goto_targ = 5;
                            continue block46;
                        }
                    }
                    case 1: {
                        _keys = JsonSkimmer._json_key_offsets[cs];
                        _trans = JsonSkimmer._json_index_offsets[cs];
                        _klen = JsonSkimmer._json_single_lengths[cs];
                        if (_klen <= 0) ** GOTO lbl48
                        _lower = _keys;
                        _upper = _keys + _klen - 1;
                        while (_upper >= _lower) {
                            _mid = _lower + (_upper - _lower >> 1);
                            if (data[p] < JsonSkimmer._json_trans_keys[_mid]) {
                                _upper = _mid - 1;
                                continue;
                            }
                            if (data[p] > JsonSkimmer._json_trans_keys[_mid]) {
                                _lower = _mid + 1;
                                continue;
                            }
                            _trans += _mid - _keys;
                            ** GOTO lbl62
                        }
                        _keys += _klen;
                        _trans += _klen;
lbl48:
                        // 2 sources

                        if ((_klen = JsonSkimmer._json_range_lengths[cs]) > 0) {
                            _lower = _keys;
                            _upper = _keys + (_klen << 1) - 2;
                            while (_upper >= _lower) {
                                _mid = _lower + (_upper - _lower >> 1 & -2);
                                if (data[p] < JsonSkimmer._json_trans_keys[_mid]) {
                                    _upper = _mid - 2;
                                    continue;
                                }
                                if (data[p] > JsonSkimmer._json_trans_keys[_mid + 1]) {
                                    _lower = _mid + 2;
                                    continue;
                                }
                                _trans += _mid - _keys >> 1;
                                ** GOTO lbl62
                            }
                            _trans += _klen;
                        }
lbl62:
                        // 5 sources

                        _trans = JsonSkimmer._json_indicies[_trans];
                        cs = JsonSkimmer._json_trans_targs[_trans];
                        if (JsonSkimmer._json_trans_actions[_trans] == 0) ** GOTO lbl246
                        _acts = JsonSkimmer._json_trans_actions[_trans];
                        _nacts = JsonSkimmer._json_actions[_acts++];
                        block49: while (_nacts-- > 0) {
                            switch (JsonSkimmer._json_actions[_acts++]) {
                                case 0: {
                                    if (debug) {
                                        System.out.println("startObject: " + name);
                                    }
                                    this.push(name, true);
                                    if (this.stop) {
                                        return;
                                    }
                                    name = null;
                                    if (top == stack.length) {
                                        stack = this.stack = Arrays.copyOf(stack, stack.length << 1);
                                    }
                                    stack[top++] = cs;
                                    cs = 4;
                                    _goto_targ = 2;
                                    continue block46;
                                }
                                case 1: {
                                    if (debug) {
                                        System.out.println("endObject");
                                    }
                                    this.pop();
                                    if (this.stop) {
                                        return;
                                    }
                                    cs = stack[--top];
                                    _goto_targ = 2;
                                    continue block46;
                                }
                                case 2: {
                                    if (debug) {
                                        System.out.println("startArray: " + name);
                                    }
                                    this.push(name, false);
                                    if (this.stop) {
                                        return;
                                    }
                                    name = null;
                                    if (top == stack.length) {
                                        stack = this.stack = Arrays.copyOf(stack, stack.length << 1);
                                    }
                                    stack[top++] = cs;
                                    cs = 20;
                                    _goto_targ = 2;
                                    continue block46;
                                }
                                case 3: {
                                    if (debug) {
                                        System.out.println("endArray");
                                    }
                                    this.pop();
                                    if (this.stop) {
                                        return;
                                    }
                                    cs = stack[--top];
                                    _goto_targ = 2;
                                    continue block46;
                                }
                                case 4: {
                                    start = p;
                                    if (data[p++] == '/') {
                                        while (p != eof && data[p] != '\n') {
                                            ++p;
                                        }
                                        --p;
                                    } else {
                                        while (p + 1 < eof && (data[p] != '*' || data[p + 1] != '/')) {
                                            ++p;
                                        }
                                        ++p;
                                    }
                                    if (!debug) continue block49;
                                    System.out.println("comment " + new String(data, start - 1, p - start + 2));
                                    break;
                                }
                                case 5: {
                                    if (debug) {
                                        System.out.println("unquotedName");
                                    }
                                    string.start = start = p;
                                    ws = false;
                                    block52: while (true) {
                                        switch (data[p]) {
                                            case '\\': {
                                                string.unescape = true;
                                                ** GOTO lbl147
                                            }
                                            case '/': {
                                                if (p + 1 != eof && ((c = data[p + 1]) == '/' || c == '*')) {
                                                    break block52;
                                                }
                                                ** GOTO lbl147
                                            }
                                            case '\t': 
                                            case ' ': {
                                                ws = true;
                                                ** GOTO lbl147
                                            }
                                            case '\n': 
                                            case '\r': 
                                            case ':': {
                                                break block52;
                                            }
lbl147:
                                            // 4 sources

                                            default: {
                                                if (!debug) continue block52;
                                                System.out.println("name char: '" + data[p] + "'");
                                                if (++p != eof) continue block52;
                                                break block52;
                                            }
                                        }
                                        break;
                                    }
                                    --p;
                                    if (ws) {
                                        block53: while (true) {
                                            switch (data[p]) {
                                                case '\t': 
                                                case ' ': {
                                                    --p;
                                                    continue block53;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    string.length = p - start + 1;
                                    break;
                                }
                                case 6: {
                                    if (debug) {
                                        System.out.println("unquotedValue");
                                    }
                                    string.start = start = p;
                                    ws = false;
                                    block54: while (true) {
                                        switch (data[p]) {
                                            case '\\': {
                                                string.unescape = true;
                                                ** GOTO lbl180
                                            }
                                            case '/': {
                                                if (p + 1 != eof && ((c = data[p + 1]) == '/' || c == '*')) {
                                                    break block54;
                                                }
                                                ** GOTO lbl180
                                            }
                                            case '\t': 
                                            case ' ': {
                                                ws = true;
                                                ** GOTO lbl180
                                            }
                                            case '\n': 
                                            case '\r': 
                                            case ',': 
                                            case ']': 
                                            case '}': {
                                                break block54;
                                            }
lbl180:
                                            // 4 sources

                                            default: {
                                                if (!debug) continue block54;
                                                System.out.println("value char: '" + data[p] + "'");
                                                if (++p != eof) continue block54;
                                                break block54;
                                            }
                                        }
                                        break;
                                    }
                                    --p;
                                    if (ws) {
                                        block55: while (true) {
                                            switch (data[p]) {
                                                case '\t': 
                                                case ' ': {
                                                    --p;
                                                    continue block55;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    string.length = p - start + 1;
                                    string.type = JsonToken.TokenType.other;
                                    if (string.length == 4) {
                                        if (data[start] == 't' && data[start + 1] == 'r' && data[start + 2] == 'u' && data[start + 3] == 'e') {
                                            string.type = JsonToken.TokenType.trueValue;
                                            break;
                                        }
                                        if (data[start] != 'n' || data[start + 1] != 'u' || data[start + 2] != 'l' || data[start + 3] != 'l') continue block49;
                                        string.type = JsonToken.TokenType.nullValue;
                                        break;
                                    }
                                    if (string.length != 5 || data[start] != 'f' || data[start + 1] != 'a' || data[start + 2] != 'l' || data[start + 3] != 's' || data[start + 4] != 'e') continue block49;
                                    string.type = JsonToken.TokenType.falseValue;
                                    break;
                                }
                                case 7: {
                                    if (debug) {
                                        System.out.println("quotedString");
                                    }
                                    string.start = ++p;
                                    block56: while (true) {
                                        switch (data[p]) {
                                            case '\\': {
                                                string.unescape = true;
                                                ++p;
                                                ** GOTO lbl216
                                            }
                                            case '\"': {
                                                break block56;
                                            }
lbl216:
                                            // 2 sources

                                            default: {
                                                if (!debug) continue block56;
                                                System.out.println("quoted char: '" + data[p] + "'");
                                                if (++p != eof) continue block56;
                                                break block56;
                                            }
                                        }
                                        break;
                                    }
                                    string.length = p - string.start;
                                    break;
                                }
                                case 8: {
                                    name = nameString;
                                    string = nameString;
                                    if (!debug) break;
                                    System.out.println("name start " + p);
                                    break;
                                }
                                case 9: {
                                    if (debug) {
                                        System.out.println("name: " + p + ", " + name);
                                    }
                                    nameString.unescape = false;
                                    string = value;
                                    break;
                                }
                                case 10: {
                                    if (debug) {
                                        System.out.println("value: " + name + "=" + value);
                                    }
                                    this.value(name, value);
                                    if (this.stop) {
                                        return;
                                    }
                                    value.unescape = false;
                                    value.type = JsonToken.TokenType.other;
                                    name = null;
                                    string = value;
                                }
                            }
                        }
                    }
lbl246:
                    // 3 sources

                    case 2: {
                        if (cs == 0) {
                            _goto_targ = 5;
                            continue block46;
                        }
                        if (++p != pe) {
                            _goto_targ = 1;
                            continue block46;
                        }
                    }
                    case 4: {
                        if (p != eof) break block46;
                        __acts = JsonSkimmer._json_eof_actions[cs];
                        __nacts = JsonSkimmer._json_actions[__acts++];
                        while (__nacts-- > 0) {
                            switch (JsonSkimmer._json_actions[__acts++]) {
                                case 10: {
                                    if (debug) {
                                        System.out.println("value: " + name + "=" + value);
                                    }
                                    this.value(name, value);
                                    if (this.stop) {
                                        return;
                                    }
                                    value.unescape = false;
                                    value.type = JsonToken.TokenType.other;
                                    name = null;
                                    string = value;
                                }
                            }
                        }
                        break block46;
                    }
                }
                break;
            }
        }
        catch (RuntimeException ex) {
            parseRuntimeEx = ex;
        }
        if (p < pe) {
            lineNumber = 1;
            for (i = 0; i < p; ++i) {
                if (data[i] != '\n') continue;
                ++lineNumber;
            }
            start = Math.max(0, p - 32);
            throw new SerializationException("Error parsing JSON on line " + lineNumber + " near: " + new String(data, start, p - start) + "*ERROR*" + new String(data, p, Math.min(64, pe - p)), parseRuntimeEx);
        }
        if (parseRuntimeEx != null) {
            throw new SerializationException("Error parsing JSON: " + new String(data), parseRuntimeEx);
        }
    }

    private static byte[] init__json_actions_0() {
        return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 6, 1, 7, 1, 9, 1, 10, 2, 8, 5, 2, 8, 7, 2, 10, 1, 2, 10, 3};
    }

    private static short[] init__json_key_offsets_0() {
        return new short[]{0, 0, 11, 13, 15, 24, 30, 36, 38, 49, 56, 63, 72, 81, 83, 85, 94, 96, 98, 100, 102, 113, 120, 127, 138, 149, 151, 153, 164, 166, 168, 170, 175, 180, 180};
    }

    private static char[] init__json_trans_keys_0() {
        return new char[]{'\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '*', '/', '*', '/', '\r', ' ', '\"', ',', '/', ':', '}', '\t', '\n', '\r', ' ', '/', ':', '\t', '\n', '\r', ' ', '/', ':', '\t', '\n', '*', '/', '\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '\t', '\n', '\r', ' ', ',', '/', '}', '\t', '\n', '\r', ' ', ',', '/', '}', '\r', ' ', '\"', ',', '/', ':', '}', '\t', '\n', '\r', ' ', '\"', ',', '/', ':', '}', '\t', '\n', '*', '/', '*', '/', '\r', ' ', '\"', ',', '/', ':', '}', '\t', '\n', '*', '/', '*', '/', '*', '/', '*', '/', '\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '\t', '\n', '\r', ' ', ',', '/', ']', '\t', '\n', '\r', ' ', ',', '/', ']', '\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '*', '/', '*', '/', '\r', ' ', '\"', ',', '/', ':', '[', ']', '{', '\t', '\n', '*', '/', '*', '/', '*', '/', '\r', ' ', '/', '\t', '\n', '\r', ' ', '/', '\t', '\n', '\u0000'};
    }

    private static byte[] init__json_single_lengths_0() {
        return new byte[]{0, 9, 2, 2, 7, 4, 4, 2, 9, 7, 7, 7, 7, 2, 2, 7, 2, 2, 2, 2, 9, 7, 7, 9, 9, 2, 2, 9, 2, 2, 2, 3, 3, 0, 0};
    }

    private static byte[] init__json_range_lengths_0() {
        return new byte[]{0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0};
    }

    private static short[] init__json_index_offsets_0() {
        return new short[]{0, 0, 11, 14, 17, 26, 32, 38, 41, 52, 60, 68, 77, 86, 89, 92, 101, 104, 107, 110, 113, 124, 132, 140, 151, 162, 165, 168, 179, 182, 185, 188, 193, 198, 199};
    }

    private static byte[] init__json_indicies_0() {
        return new byte[]{1, 1, 2, 3, 4, 3, 5, 3, 6, 1, 0, 7, 7, 3, 8, 8, 3, 10, 10, 11, 12, 13, 3, 14, 10, 9, 15, 15, 16, 17, 15, 3, 18, 18, 19, 20, 18, 3, 21, 21, 3, 20, 20, 23, 3, 24, 3, 25, 3, 26, 20, 22, 27, 28, 28, 27, 29, 30, 31, 3, 32, 33, 33, 32, 12, 34, 14, 3, 33, 33, 11, 35, 36, 3, 14, 33, 9, 35, 35, 11, 3, 37, 3, 3, 35, 9, 38, 38, 3, 39, 39, 3, 12, 12, 11, 3, 40, 3, 14, 12, 9, 41, 41, 3, 42, 42, 3, 43, 43, 3, 44, 44, 3, 46, 46, 47, 48, 49, 3, 50, 51, 52, 46, 45, 53, 54, 54, 53, 55, 56, 57, 3, 58, 59, 59, 58, 48, 60, 51, 3, 59, 59, 47, 61, 62, 3, 50, 51, 52, 59, 45, 61, 61, 47, 3, 63, 3, 50, 3, 52, 61, 45, 64, 64, 3, 65, 65, 3, 48, 48, 47, 3, 66, 3, 50, 51, 52, 48, 45, 67, 67, 3, 68, 68, 3, 69, 69, 3, 70, 70, 71, 70, 3, 72, 72, 73, 72, 3, 3, 3, 0};
    }

    private static byte[] init__json_trans_targs_0() {
        return new byte[]{31, 1, 31, 0, 3, 32, 32, 32, 1, 5, 4, 5, 15, 19, 33, 6, 7, 8, 6, 7, 8, 6, 9, 9, 18, 10, 10, 10, 11, 15, 17, 33, 10, 11, 17, 12, 14, 13, 12, 11, 16, 15, 10, 8, 4, 21, 20, 21, 27, 30, 22, 34, 22, 22, 23, 27, 29, 34, 22, 23, 29, 24, 26, 25, 24, 23, 28, 27, 22, 20, 32, 2, 32, 2};
    }

    private static byte[] init__json_trans_actions_0() {
        return new byte[]{11, 0, 13, 0, 0, 5, 1, 9, 9, 19, 0, 22, 0, 0, 3, 15, 15, 15, 0, 0, 0, 9, 11, 13, 0, 5, 1, 17, 17, 17, 17, 25, 0, 0, 0, 0, 0, 0, 9, 9, 0, 9, 9, 9, 9, 11, 0, 13, 0, 0, 5, 7, 1, 17, 17, 17, 17, 28, 0, 0, 0, 0, 0, 0, 9, 9, 0, 9, 9, 9, 17, 17, 0, 0};
    }

    private static byte[] init__json_eof_actions_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 0, 0};
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isStopped() {
        return this.stop;
    }

    protected void push(@Null JsonToken name, boolean object) {
    }

    protected void pop() {
    }

    protected void value(@Null JsonToken name, JsonToken value) {
    }

    public static class JsonToken {
        final CharArray buffer;
        public char[] chars;
        public int start;
        public int length;
        public boolean unescape;
        public TokenType type = TokenType.other;

        JsonToken(CharArray buffer) {
            this.buffer = buffer;
        }

        public boolean equalsString(String string) {
            if (string == null) {
                return false;
            }
            if (this.unescape) {
                return this.toString().equals(string);
            }
            int n = this.length;
            if (string.length() != n) {
                return false;
            }
            char[] chars = this.chars;
            int c = this.start;
            for (int s = 0; s < n; ++s) {
                if (chars[c] != string.charAt(s)) {
                    return false;
                }
                ++c;
            }
            return true;
        }

        public String toString() {
            if (this.type == TokenType.nullValue) {
                return "null";
            }
            return this.unescape ? this.unescape() : new String(this.chars, this.start, this.length);
        }

        public JsonValue value() {
            switch (this.type) {
                case nullValue: {
                    return new JsonValue(JsonValue.ValueType.nullValue);
                }
                case trueValue: {
                    return new JsonValue(true);
                }
                case falseValue: {
                    return new JsonValue(false);
                }
            }
            return new JsonValue(this.toString());
        }

        private boolean equals(String string) {
            int n = this.length;
            char[] chars = this.chars;
            int c = this.start;
            for (int s = 0; s < n; ++s) {
                if (chars[c] != string.charAt(s)) {
                    return false;
                }
                ++c;
            }
            return true;
        }

        private String unescape() {
            char[] chars = this.chars;
            this.buffer.size = 0;
            this.buffer.ensureCapacity(this.length + 16);
            int i = this.start;
            int n = i + this.length;
            block9: while (i < n) {
                char c;
                if ((c = chars[i++]) != '\\') {
                    this.buffer.append(c);
                    continue;
                }
                if (i == n) {
                    throw new SerializationException("Illegal escape sequence: \\");
                }
                c = chars[i++];
                switch (c) {
                    case 'u': {
                        if (i + 4 > n) {
                            throw new SerializationException("Illegal escape sequence: \\u");
                        }
                        this.buffer.size += Character.toChars(Character.digit(chars[i++], 16) << 12 | Character.digit(chars[i++], 16) << 8 | Character.digit(chars[i++], 16) << 4 | Character.digit(chars[i++], 16), this.buffer.items, this.buffer.size);
                        continue block9;
                    }
                    case '\"': 
                    case '/': 
                    case '\\': {
                        break;
                    }
                    case 'b': {
                        c = '\b';
                        break;
                    }
                    case 'f': {
                        c = '\f';
                        break;
                    }
                    case 'n': {
                        c = '\n';
                        break;
                    }
                    case 'r': {
                        c = '\r';
                        break;
                    }
                    case 't': {
                        c = '\t';
                        break;
                    }
                    default: {
                        throw new SerializationException("Illegal escaped character: \\" + c);
                    }
                }
                this.buffer.append(c);
            }
            return this.buffer.toString();
        }

        public static enum TokenType {
            nullValue,
            trueValue,
            falseValue,
            other;

        }
    }
}

