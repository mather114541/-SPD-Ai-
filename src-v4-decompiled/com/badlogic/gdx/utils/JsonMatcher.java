/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.JsonSkimmer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.PatternParser;
import java.io.InputStream;
import java.io.Reader;

public class JsonMatcher
extends JsonSkimmer {
    private static final boolean debug = false;
    private static final int none = 0;
    static final int match = 1;
    static final int process = 2;
    static final int capture = 4;
    static final int array = 8;
    static final int keys = 16;
    static final int single = 32;
    Processor processor;
    Pattern[] patterns = new Pattern[0];
    Pattern[] original;
    Pattern[] all;
    int total;
    int endCaptures;
    private boolean rejected;
    boolean stoppable = true;
    int depth;
    int captured;
    char[] chars;
    final IntArray path = new IntArray();
    Pattern processPattern;

    public JsonMatcher() {
    }

    public JsonMatcher(String ... patterns) {
        for (String pattern : patterns) {
            this.addPattern(pattern);
        }
    }

    public void setProcessor(@Null Processor processor) {
        this.processor = processor;
    }

    public int addPattern(String pattern) {
        return this.addPattern(pattern, null);
    }

    public int addPattern(String pattern, @Null Processor processor) {
        Pattern newPattern;
        if (this.chars != null) {
            throw new IllegalStateException();
        }
        Pattern[] newPatterns = new Pattern[this.patterns.length + 1];
        System.arraycopy(this.patterns, 0, newPatterns, 0, this.patterns.length);
        if (pattern.isEmpty()) {
            newPattern = new Pattern(new Node(new Match[0], false, null), processor, Integer.MAX_VALUE, false);
            newPattern.captureRoot = true;
            newPattern.captureAll = true;
            ++this.endCaptures;
        } else {
            newPattern = PatternParser.parse(this, pattern, processor);
        }
        newPatterns[this.patterns.length] = newPattern;
        this.patterns = newPatterns;
        return this.patterns.length - 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(char[] data, int offset, int length) {
        if (this.chars != null) {
            throw new IllegalStateException();
        }
        this.parseStart();
        this.captureRoot();
        this.chars = data;
        try {
            super.parse(data, offset, length);
            for (Pattern pattern : this.patterns) {
                this.process(pattern, false);
            }
            this.parseEnd();
        }
        finally {
            for (Pattern pattern : this.patterns) {
                pattern.reset();
            }
            this.patterns = this.original;
            this.depth = 0;
            this.captured = 0;
            this.chars = null;
            this.path.clear();
        }
    }

    private void captureRoot() {
        this.original = this.patterns;
        if (this.patterns.length == 0) {
            if (this.all == null) {
                this.addPattern("", null);
                this.all = this.patterns;
            } else {
                this.patterns = this.all;
            }
        }
    }

    protected void parseStart() {
    }

    protected void parseEnd() {
    }

    @Override
    protected void push(@Null JsonSkimmer.JsonToken name, boolean object) {
        if (name != null) {
            this.path.add(name.start, name.length);
        } else {
            this.path.add(object ? 0 : 1, 0);
        }
        if (this.depth == 0) {
            for (Pattern pattern : this.patterns) {
                if (!pattern.captureRoot) continue;
                this.captureAllStart(pattern, 32, null, object);
            }
        } else {
            block1: for (Pattern pattern : this.patterns) {
                int flags;
                if (pattern.captureAll) {
                    JsonValue value = new JsonValue(object ? JsonValue.ValueType.object : JsonValue.ValueType.array);
                    this.captureAllValue(pattern, name, value);
                    pattern.stack.add(value);
                    continue;
                }
                Node node = pattern.current;
                if (this.depth > node.dead) continue;
                Node next = node.next;
                int n = flags = next == null ? 0 : next.match(name);
                if (flags != 0) {
                    while (true) {
                        if ((flags & 2) != 0) {
                            this.process(pattern, true);
                            if (this.stop) {
                                return;
                            }
                        }
                        if ((flags & 4) != 0) {
                            if ((flags & 0x10) != 0) {
                                this.captureValue(pattern, flags, null, name.value());
                                this.captured(pattern);
                                continue block1;
                            }
                            this.captureAllStart(pattern, flags, name, object);
                            continue block1;
                        }
                        pattern.current = next;
                        next.pop = this.depth;
                        Node nextNext = next.next;
                        if (!next.starStar || nextNext == null || (flags = nextNext.match(name)) == 0) continue block1;
                        next = nextNext;
                    }
                }
                if (node.starStar) continue;
                if (node.backtrack != null) {
                    pattern.current = node.backtrack;
                    continue;
                }
                node.dead = this.depth;
            }
        }
        ++this.depth;
    }

    @Override
    protected void pop() {
        int nextDepth = this.depth - 1;
        block0: for (Pattern pattern : this.patterns) {
            if (pattern.captureAll) {
                pattern.stack.pop();
                if (pattern.stack.notEmpty()) continue;
                pattern.captureAll = pattern.captureRoot;
                this.captured(pattern);
            }
            Node node = pattern.current;
            while (true) {
                if (node.dead == nextDepth) {
                    node.dead = Integer.MAX_VALUE;
                }
                if (node.pop != nextDepth) {
                    if (!node.processEach) continue block0;
                    this.process(pattern, true);
                    if (!this.stop) continue block0;
                    return;
                }
                if (node.processEach || node.processPop) {
                    this.process(pattern, true);
                    if (this.stop) {
                        return;
                    }
                }
                if (node.prev == null) continue block0;
                pattern.current = node = node.prev;
            }
        }
        this.depth = nextDepth;
        this.path.size -= 2;
    }

    @Override
    protected void value(@Null JsonSkimmer.JsonToken name, JsonSkimmer.JsonToken value) {
        block0: for (Pattern pattern : this.patterns) {
            int flags;
            if (pattern.captureAll) {
                this.captureAllValue(pattern, name, value.value());
                continue;
            }
            if (this.depth > pattern.current.dead) continue;
            Node next = pattern.current.next;
            int n = flags = next == null ? 0 : next.match(name);
            if (flags == 0) continue;
            while (true) {
                if ((flags & 2) != 0) {
                    this.process(pattern, true);
                    if (this.stop) {
                        return;
                    }
                }
                if ((flags & 4) != 0) {
                    if ((flags & 0x10) != 0) {
                        if (name != null) {
                            this.captureValue(pattern, flags, null, name.value());
                            this.captured(pattern);
                        }
                    } else {
                        this.captureValue(pattern, flags, name, value.value());
                        this.captured(pattern);
                    }
                    if ((flags & 2) == 0) continue block0;
                    this.process(pattern, true);
                    if (!this.stop) continue block0;
                    return;
                }
                Node nextNext = next.next;
                if (!next.starStar || nextNext == null || (flags = nextNext.match(name)) == 0) continue block0;
                next = nextNext;
            }
        }
    }

    private void captureValue(Pattern pattern, int flags, @Null JsonSkimmer.JsonToken name, JsonValue value) {
        JsonValue capture = pattern.capture;
        if ((flags & 0x20) != 0) {
            String string = capture.name = name == null ? null : name.toString();
            if ((flags & 8) != 0) {
                if (pattern.captured == 0) {
                    capture.setType(JsonValue.ValueType.array);
                }
                capture.addChild(value);
            } else {
                capture.set(value);
            }
        } else {
            String key;
            String string = key = name == null ? "" : name.toString();
            if ((flags & 8) != 0) {
                JsonValue array = capture.get(key);
                if (array == null) {
                    array = new JsonValue(JsonValue.ValueType.array);
                    capture.addChild(key, array);
                }
                array.addChild(value);
                value.parent = array;
            } else {
                capture.setChild(key, value);
            }
        }
    }

    private void captured(Pattern pattern) {
        if (this.stoppable && ++this.captured >= this.total) {
            this.end();
        }
        ++pattern.captured;
        if (pattern.captured >= pattern.total) {
            pattern.current.pop = -1;
            pattern.current.dead = -1;
        }
    }

    private void captureAllStart(Pattern pattern, int flags, @Null JsonSkimmer.JsonToken name, boolean object) {
        JsonValue capture;
        JsonValue.ValueType type;
        JsonValue.ValueType valueType = type = object ? JsonValue.ValueType.object : JsonValue.ValueType.array;
        if ((flags & 0x28) == 32) {
            capture = pattern.capture;
            capture.setType(type);
            capture.name = name == null ? null : name.toString();
        } else {
            capture = new JsonValue(type);
            this.captureValue(pattern, flags, name, capture);
        }
        pattern.stack.add(capture);
        pattern.captureAll = true;
    }

    private void captureAllValue(Pattern pattern, @Null JsonSkimmer.JsonToken name, JsonValue value) {
        if (pattern.stack.isEmpty()) {
            pattern.capture.set(value);
            this.captured(pattern);
        } else {
            JsonValue parent = pattern.stack.peek();
            if (name == null) {
                parent.addChild(value);
            } else {
                parent.setChild(name.toString(), value);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void process(Pattern pattern, boolean clear) {
        if (pattern.captured == 0) {
            return;
        }
        JsonValue capture = pattern.capture;
        this.rejected = false;
        this.processPattern = pattern;
        try {
            if (pattern.processor != null) {
                pattern.processor.process(capture);
                if (this.rejected) {
                    return;
                }
            }
            if (this.processor != null) {
                this.processor.process(capture);
                if (this.rejected) {
                    return;
                }
            }
            this.process(capture);
        }
        finally {
            if (clear) {
                pattern.clearCapture();
            } else {
                pattern.capture = new JsonValue(JsonValue.ValueType.object);
            }
            this.processPattern = null;
        }
    }

    protected void process(JsonValue value) {
    }

    public int depth() {
        return this.depth;
    }

    public int pattern() {
        int n = this.patterns.length;
        for (int i = 0; i < n; ++i) {
            if (this.patterns[i] != this.processPattern) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void stop() {
        this.rejected = true;
        this.clearAll();
        super.stop();
    }

    public void end() {
        super.stop();
    }

    public void reject() {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        this.processPattern.current.dead = this.depth - 1;
        this.rejected = true;
    }

    public void reject(int patternIndex) {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        this.patterns[patternIndex].current.dead = this.depth - 1;
        this.rejected = true;
    }

    public void rejectAll() {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        int dead = this.depth - 1;
        for (Pattern pattern : this.patterns) {
            pattern.current.dead = dead;
        }
        this.rejected = true;
    }

    public void clear() {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        this.processPattern.clearCapture();
    }

    public void clear(int patternIndex) {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        this.patterns[patternIndex].clearCapture();
    }

    public void clearAll() {
        if (this.processPattern == null) {
            throw new IllegalStateException();
        }
        for (Pattern pattern : this.patterns) {
            pattern.clearCapture();
        }
    }

    public String path() {
        this.buffer.size = 0;
        int n = this.path.size;
        for (int i = 0; i < n; i += 2) {
            if (i > 0) {
                this.buffer.append('/');
            }
            int start = this.path.get(i);
            int length = this.path.get(i + 1);
            if (length == 0) {
                this.buffer.append(start == 0 ? "{}" : "[]");
                continue;
            }
            this.buffer.append(this.chars, start, length);
        }
        return this.buffer.toString();
    }

    public String parent() {
        int n = this.path.size;
        if (n == 0) {
            return "";
        }
        int start = this.path.get(n - 2);
        int length = this.path.get(n - 1);
        if (length == 0) {
            return start == 0 ? "{}" : "[]";
        }
        return new String(this.chars, start, length);
    }

    public String parent(int up) {
        int n = this.path.size;
        int i = n - (up << 1);
        if (i < 2) {
            return "";
        }
        int start = this.path.get(i - 2);
        int length = this.path.get(i - 1);
        if (length == 0) {
            return start == 0 ? "{}" : "[]";
        }
        return new String(this.chars, start, length);
    }

    Match newMatch(String name, boolean brackets, boolean at, boolean processEach, boolean valueCapture, boolean keyCapture, boolean star, boolean starStar) {
        int flags = 1;
        if (at || processEach) {
            flags |= 2;
        }
        if (valueCapture) {
            flags |= 4;
            if (brackets) {
                flags |= 8;
            }
            if (processEach) {
                flags |= 0x20;
            }
        }
        if (keyCapture) {
            flags |= 0x10;
        }
        return new Match(name, flags, star, starStar);
    }

    Node newNode(Match[] matches, boolean processEach, Node backtrack, @Null Node prev) {
        Node node = new Node(matches, processEach, backtrack);
        if (prev == null) {
            if (node.starStar) {
                return node;
            }
            prev = new Node(new Match[]{new Match(".", 0, false, false)}, false, null);
        }
        prev.next = node;
        prev.nextStarStar = node.starStar;
        node.prev = prev;
        return node;
    }

    Pattern newPattern(Node root, Processor processor) {
        Node current = root;
        boolean multi = false;
        boolean at = false;
        boolean stoppable = true;
        int captures = 0;
        Match prevCapture = null;
        do {
            for (Match match : current.matches) {
                int flags = match.flags;
                if ((flags & 8) != 0 || (match.star || match.starStar) && (flags & 2) != 0) {
                    stoppable = false;
                }
                if ((flags & 0x14) != 0) {
                    if (prevCapture != null) {
                        multi = true;
                    } else {
                        prevCapture = match;
                    }
                    if ((flags & 4) != 0) {
                        ++captures;
                    }
                }
                if ((flags & 2) == 0) continue;
                at = true;
                if (!multi && prevCapture != null) {
                    prevCapture.flags |= 0x20;
                }
                multi = false;
                prevCapture = null;
            }
        } while ((current = current.next) != null);
        if (!multi && prevCapture != null) {
            prevCapture.flags |= 0x20;
        }
        if (!stoppable) {
            this.stoppable = false;
        } else {
            this.total += captures;
        }
        if (at || !stoppable) {
            captures = Integer.MAX_VALUE;
        } else {
            ++this.endCaptures;
        }
        return new Pattern(root, processor, captures, at);
    }

    private void debug(Pattern pattern, String text) {
        int i;
        for (i = 0; i < this.depth; ++i) {
            System.out.print("  ");
        }
        int n = this.patterns.length;
        for (i = 0; i < n; ++i) {
            if (this.patterns[i] != pattern) continue;
            System.out.print("[" + i + "] ");
        }
        System.out.println(text);
    }

    private JsonValue valueStart() {
        this.captureRoot();
        if (this.endCaptures == 0) {
            throw new IllegalStateException("Must have at least one pattern without @.");
        }
        if (this.endCaptures == 1) {
            for (Pattern pattern : this.patterns) {
                if (pattern.at) continue;
                return pattern.capture;
            }
        }
        JsonValue value = new JsonValue(JsonValue.ValueType.array);
        for (Pattern pattern : this.patterns) {
            if (pattern.at) continue;
            value.addChild(pattern.capture);
        }
        return value;
    }

    public JsonValue parseValue(char[] data, int offset, int length) {
        JsonValue value = this.valueStart();
        this.parse(data, offset, length);
        return value;
    }

    public JsonValue parseValue(String json) {
        JsonValue value = this.valueStart();
        this.parse(json);
        return value;
    }

    public JsonValue parseValue(Reader reader) {
        JsonValue value = this.valueStart();
        this.parse(reader);
        return value;
    }

    public JsonValue parseValue(InputStream input) {
        JsonValue value = this.valueStart();
        this.parse(input);
        return value;
    }

    public JsonValue parseValue(FileHandle file) {
        JsonValue value = this.valueStart();
        this.parse(file);
        return value;
    }

    static class Pattern {
        final Node root;
        final Processor processor;
        JsonValue capture = new JsonValue(JsonValue.ValueType.object);
        int captured;
        int total;
        boolean captureAll;
        boolean captureRoot;
        boolean at;
        final Array<JsonValue> stack = new Array();
        Node current;

        Pattern(Node root, Processor processor, int total, boolean at) {
            this.root = root;
            this.processor = processor;
            this.total = total;
            this.at = at;
            this.current = root;
        }

        void clearCapture() {
            this.captured = 0;
            this.capture.name = null;
            this.capture.child = null;
            this.capture.last = null;
            this.capture.setType(JsonValue.ValueType.object);
            this.capture.size = 0;
        }

        void reset() {
            Node node = this.root;
            do {
                node.dead = Integer.MAX_VALUE;
            } while ((node = node.next) != null);
            this.clearCapture();
            this.captureAll = this.captureRoot;
            this.stack.clear();
            this.current = this.root;
        }

        public String toString() {
            return super.toString();
        }
    }

    public static interface Processor {
        public void process(JsonValue var1);
    }

    static class Node {
        final Match[] matches;
        final boolean processEach;
        boolean processPop;
        boolean starStar;
        boolean nextStarStar;
        @Null
        Node prev;
        @Null
        Node next;
        @Null
        Node backtrack;
        int pop;
        int dead = Integer.MAX_VALUE;

        Node(Match[] matches, boolean processEach, Node backtrack) {
            this.matches = matches;
            this.processEach = processEach;
            this.backtrack = backtrack;
            for (Match match : matches) {
                if (match.starStar) {
                    this.starStar = true;
                }
                if ((match.flags & 2) == 0) continue;
                this.processPop = true;
            }
        }

        int match(@Null JsonSkimmer.JsonToken name) {
            if (name != null) {
                for (Match match : this.matches) {
                    if (!match.any && !name.equalsString(match.name)) continue;
                    return match.flags;
                }
            } else {
                for (Match match : this.matches) {
                    if (!match.any) continue;
                    return match.flags;
                }
            }
            return this.nextStarStar ? this.next.match(name) : 0;
        }

        private void toString(StringBuilder buffer) {
            int last = this.matches.length - 1;
            for (int i = 0; i <= last; ++i) {
                this.matches[i].toString(buffer);
                if (i >= last) continue;
                buffer.append(',');
            }
            if (this.next != null) {
                buffer.append('/');
                this.next.toString(buffer);
            }
        }

        public String toString() {
            return super.toString();
        }
    }

    static class Match {
        final String name;
        int flags;
        final boolean star;
        final boolean starStar;
        final boolean any;

        Match(String name, int flags, boolean star, boolean starStar) {
            this.name = name;
            this.flags = flags;
            this.star = star;
            this.starStar = starStar;
            this.any = star || starStar;
        }

        void toString(StringBuilder buffer) {
            if ((this.flags & 4) != 0) {
                buffer.append('(');
            }
            buffer.append(this.name);
            if ((this.flags & 8) != 0) {
                buffer.append("[]");
            }
            if ((this.flags & 2) != 0) {
                buffer.append('@');
            }
            if ((this.flags & 4) != 0) {
                buffer.append(')');
            }
            if ((this.flags & 0x20) != 0) {
                buffer.append('1');
            }
        }

        public String toString() {
            return super.toString();
        }
    }
}

