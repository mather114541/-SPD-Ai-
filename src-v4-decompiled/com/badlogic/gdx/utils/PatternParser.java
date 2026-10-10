/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArraySupplier;
import com.badlogic.gdx.utils.JsonMatcher;
import com.badlogic.gdx.utils.Null;
import java.lang.invoke.LambdaMetafactory;

class PatternParser {
    private static final byte[] _parser_actions = PatternParser.init__parser_actions_0();
    private static final byte[] _parser_key_offsets = PatternParser.init__parser_key_offsets_0();
    private static final char[] _parser_trans_keys = PatternParser.init__parser_trans_keys_0();
    private static final byte[] _parser_single_lengths = PatternParser.init__parser_single_lengths_0();
    private static final byte[] _parser_range_lengths = PatternParser.init__parser_range_lengths_0();
    private static final short[] _parser_index_offsets = PatternParser.init__parser_index_offsets_0();
    private static final byte[] _parser_indicies = PatternParser.init__parser_indicies_0();
    private static final byte[] _parser_trans_targs = PatternParser.init__parser_trans_targs_0();
    private static final byte[] _parser_trans_actions = PatternParser.init__parser_trans_actions_0();
    private static final byte[] _parser_eof_actions = PatternParser.init__parser_eof_actions_0();
    static final int parser_start = 1;

    PatternParser() {
    }

    /*
     * Unable to fully structure code
     */
    static JsonMatcher.Pattern parse(JsonMatcher matcher, String text, @Null JsonMatcher.Processor processor) {
        data = text.toCharArray();
        p = 0;
        eof = pe = data.length;
        s = 0;
        e = 0;
        c = -1;
        escaped = false;
        quoted = false;
        brackets = false;
        at = false;
        keyCapture = false;
        star = false;
        starStar = false;
        matches = new Array<JsonMatcher.Match>((ArraySupplier<Object>)LambdaMetafactory.metafactory(null, null, null, (I)Ljava/lang/Object;, lambda$parse$0(int ), (I)Ljava/lang/Object;)());
        root = null;
        prev = null;
        backtrack = null;
        processEach = false;
        hasCapture = false;
        try {
            cs = 1;
            _trans = 0;
            _goto_targ = 0;
            block31: while (true) {
                switch (_goto_targ) {
                    case 0: {
                        if (p == pe) {
                            _goto_targ = 4;
                            continue block31;
                        }
                        if (cs == 0) {
                            _goto_targ = 5;
                            continue block31;
                        }
                    }
                    case 1: {
                        _keys = PatternParser._parser_key_offsets[cs];
                        _trans = PatternParser._parser_index_offsets[cs];
                        _klen = PatternParser._parser_single_lengths[cs];
                        if (_klen <= 0) ** GOTO lbl52
                        _lower = _keys;
                        _upper = _keys + _klen - 1;
                        while (_upper >= _lower) {
                            _mid = _lower + (_upper - _lower >> 1);
                            if (data[p] < PatternParser._parser_trans_keys[_mid]) {
                                _upper = _mid - 1;
                                continue;
                            }
                            if (data[p] > PatternParser._parser_trans_keys[_mid]) {
                                _lower = _mid + 1;
                                continue;
                            }
                            _trans += _mid - _keys;
                            ** GOTO lbl66
                        }
                        _keys += _klen;
                        _trans += _klen;
lbl52:
                        // 2 sources

                        if ((_klen = PatternParser._parser_range_lengths[cs]) > 0) {
                            _lower = _keys;
                            _upper = _keys + (_klen << 1) - 2;
                            while (_upper >= _lower) {
                                _mid = _lower + (_upper - _lower >> 1 & -2);
                                if (data[p] < PatternParser._parser_trans_keys[_mid]) {
                                    _upper = _mid - 2;
                                    continue;
                                }
                                if (data[p] > PatternParser._parser_trans_keys[_mid + 1]) {
                                    _lower = _mid + 2;
                                    continue;
                                }
                                _trans += _mid - _keys >> 1;
                                ** GOTO lbl66
                            }
                            _trans += _klen;
                        }
lbl66:
                        // 5 sources

                        _trans = PatternParser._parser_indicies[_trans];
                        cs = PatternParser._parser_trans_targs[_trans];
                        if (PatternParser._parser_trans_actions[_trans] != 0) {
                            _acts = PatternParser._parser_trans_actions[_trans];
                            _nacts = PatternParser._parser_actions[_acts++];
                            block34: while (_nacts-- > 0) {
                                switch (PatternParser._parser_actions[_acts++]) {
                                    case 0: {
                                        s = p;
                                        break;
                                    }
                                    case 1: {
                                        e = p;
                                        break;
                                    }
                                    case 2: {
                                        escaped = true;
                                        break;
                                    }
                                    case 3: {
                                        quoted = true;
                                        break;
                                    }
                                    case 4: {
                                        name = new String(data, s, e - s);
                                        if (quoted) {
                                            name = name.substring(1, name.length() - 1);
                                        }
                                        if (escaped) {
                                            name = name.replace("''", "'");
                                        }
                                        match = matcher.newMatch(name, brackets, at, processEach, c >= 0, keyCapture, star, starStar);
                                        matches.add(match);
                                        if (starStar && (match.flags & 2) != 0) {
                                            processEach = true;
                                        }
                                        escaped = false;
                                        quoted = false;
                                        brackets = false;
                                        at = false;
                                        keyCapture = false;
                                        star = false;
                                        starStar = false;
                                        break;
                                    }
                                    case 5: {
                                        brackets = true;
                                        if (c >= 0) break;
                                        throw new IllegalArgumentException("[] must be within a capture.");
                                    }
                                    case 6: {
                                        at = true;
                                        break;
                                    }
                                    case 7: {
                                        star = true;
                                        keyCapture = true;
                                        hasCapture = true;
                                        break;
                                    }
                                    case 8: {
                                        star = true;
                                        break;
                                    }
                                    case 9: {
                                        starStar = true;
                                        break;
                                    }
                                    case 10: {
                                        c = matches.size;
                                        break;
                                    }
                                    case 11: {
                                        c = -1;
                                        hasCapture = true;
                                        break;
                                    }
                                    case 12: {
                                        n = matches.size;
                                        for (i = c; i < n; ++i) {
                                            ((JsonMatcher.Match)matches.get((int)i)).flags |= 2;
                                        }
                                        continue block34;
                                    }
                                    case 13: {
                                        node = matcher.newNode((JsonMatcher.Match[])matches.toArray(), processEach, backtrack, prev);
                                        if (node.starStar) {
                                            if (matches.size > 1) {
                                                throw new IllegalArgumentException("** cannot have other matches at the same level.");
                                            }
                                            backtrack = node;
                                        }
                                        matches.clear();
                                        if (root == null) {
                                            root = node.prev != null ? node.prev : node;
                                        }
                                        prev = node;
                                    }
                                }
                            }
                        }
                    }
                    case 2: {
                        if (cs == 0) {
                            _goto_targ = 5;
                            continue block31;
                        }
                        if (++p != pe) {
                            _goto_targ = 1;
                            continue block31;
                        }
                    }
                    case 4: {
                        if (p != eof) break block31;
                        __acts = PatternParser._parser_eof_actions[cs];
                        __nacts = PatternParser._parser_actions[__acts++];
                        while (__nacts-- > 0) {
                            switch (PatternParser._parser_actions[__acts++]) {
                                case 1: {
                                    e = p;
                                    break;
                                }
                                case 3: {
                                    quoted = true;
                                    break;
                                }
                                case 4: {
                                    name = new String(data, s, e - s);
                                    if (quoted) {
                                        name = name.substring(1, name.length() - 1);
                                    }
                                    if (escaped) {
                                        name = name.replace("''", "'");
                                    }
                                    match = matcher.newMatch(name, brackets, at, processEach, c >= 0, keyCapture, star, starStar);
                                    matches.add(match);
                                    if (starStar && (match.flags & 2) != 0) {
                                        processEach = true;
                                    }
                                    escaped = false;
                                    quoted = false;
                                    brackets = false;
                                    at = false;
                                    keyCapture = false;
                                    star = false;
                                    starStar = false;
                                    break;
                                }
                                case 11: {
                                    c = -1;
                                    hasCapture = true;
                                    break;
                                }
                                case 13: {
                                    node = matcher.newNode((JsonMatcher.Match[])matches.toArray(), processEach, backtrack, prev);
                                    if (node.starStar) {
                                        if (matches.size > 1) {
                                            throw new IllegalArgumentException("** cannot have other matches at the same level.");
                                        }
                                        backtrack = node;
                                    }
                                    matches.clear();
                                    if (root == null) {
                                        root = node.prev != null ? node.prev : node;
                                    }
                                    prev = node;
                                }
                            }
                        }
                        break block31;
                    }
                }
                break;
            }
            if (p < pe) {
                start = Math.max(0, p - 32);
                throw new IllegalArgumentException("Error parsing pattern near: " + new String(data, start, p - start) + "ERROR" + new String(data, p, Math.min(64, pe - p)));
            }
            if (!hasCapture) {
                throw new IllegalArgumentException("A capture is required.");
            }
            return matcher.newPattern(root, processor);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Error parsing pattern: " + text, ex);
        }
    }

    private static byte[] init__parser_actions_0() {
        return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 4, 1, 5, 1, 6, 1, 7, 1, 9, 1, 11, 1, 12, 2, 0, 8, 2, 1, 4, 2, 1, 6, 2, 3, 1, 2, 4, 13, 2, 10, 0, 2, 11, 13, 3, 1, 4, 13, 3, 3, 1, 4, 3, 3, 1, 6, 4, 3, 1, 4, 13};
    }

    private static byte[] init__parser_key_offsets_0() {
        return new byte[]{0, 0, 9, 10, 11, 12, 21, 29, 38, 39, 44, 45, 47, 48, 51, 52, 56, 61, 68, 70, 73, 78, 81, 83, 87};
    }

    private static char[] init__parser_trans_keys_0() {
        return new char[]{'\'', '(', ')', '*', ',', '/', '@', '[', ']', ']', '\'', '\'', '\'', '(', ')', '*', ',', '/', '@', '[', ']', ')', ',', '/', '@', '[', ']', '\'', '*', '\'', '(', ')', '*', ',', '/', '@', '[', ']', '\'', '\'', ')', ',', '@', '[', '\'', ')', ',', ']', ')', ',', '@', ')', ')', ',', '@', '[', ')', '*', ',', '@', '[', ',', '/', '@', '[', ']', '\'', '*', ',', '/', ',', '/', '@', '\'', ',', '/', '@', '[', ',', '/', '@', ',', '/', ',', '/', '@', '[', '*', ',', '/', '@', '[', '\u0000'};
    }

    private static byte[] init__parser_single_lengths_0() {
        return new byte[]{0, 9, 1, 1, 1, 9, 6, 9, 1, 5, 1, 2, 1, 3, 1, 4, 5, 5, 2, 3, 5, 3, 2, 4, 5};
    }

    private static byte[] init__parser_range_lengths_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
    }

    private static short[] init__parser_index_offsets_0() {
        return new short[]{0, 0, 10, 12, 14, 16, 26, 34, 44, 46, 52, 54, 57, 59, 63, 65, 70, 76, 83, 86, 90, 96, 100, 103, 108};
    }

    private static byte[] init__parser_indicies_0() {
        return new byte[]{1, 2, 4, 3, 4, 4, 4, 4, 4, 0, 5, 4, 7, 6, 9, 8, 11, 12, 13, 14, 4, 4, 4, 4, 4, 10, 16, 17, 4, 18, 19, 4, 4, 15, 11, 12, 4, 14, 4, 4, 4, 4, 4, 10, 21, 20, 22, 23, 24, 25, 26, 4, 28, 27, 29, 30, 4, 31, 4, 29, 30, 32, 4, 33, 4, 16, 17, 18, 19, 4, 16, 34, 17, 18, 19, 4, 36, 37, 38, 39, 4, 4, 35, 40, 41, 4, 40, 41, 42, 4, 43, 44, 45, 46, 47, 4, 48, 49, 50, 4, 48, 49, 4, 36, 37, 38, 39, 4, 51, 36, 37, 38, 39, 4, 0};
    }

    private static byte[] init__parser_trans_targs_0() {
        return new byte[]{17, 3, 5, 24, 0, 19, 3, 20, 3, 20, 6, 8, 14, 23, 16, 6, 21, 7, 11, 12, 8, 9, 10, 21, 7, 11, 12, 8, 9, 21, 7, 13, 11, 15, 15, 17, 1, 1, 18, 2, 1, 1, 18, 4, 1, 1, 18, 2, 1, 1, 22, 23};
    }

    private static byte[] init__parser_trans_actions_0() {
        return new byte[]{1, 1, 36, 21, 0, 9, 0, 0, 5, 5, 1, 1, 1, 13, 21, 0, 24, 24, 27, 3, 0, 0, 0, 46, 46, 50, 30, 5, 5, 7, 7, 9, 11, 13, 15, 0, 24, 42, 27, 3, 7, 33, 11, 0, 46, 54, 50, 30, 17, 39, 19, 15};
    }

    private static byte[] init__parser_eof_actions_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42, 33, 33, 54, 39, 39, 42, 42};
    }

    private static /* synthetic */ Object lambda$parse$0(int x$0) {
        return new JsonMatcher.Match[x$0];
    }
}

