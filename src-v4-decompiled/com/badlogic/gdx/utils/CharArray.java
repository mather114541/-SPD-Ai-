/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Null;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class CharArray
implements CharSequence,
Appendable {
    private static final int CAPACITY = 16;
    private static final int EOS = -1;
    private static final int FALSE_STRING_SIZE = 5;
    private static final int TRUE_STRING_SIZE = 4;
    private static final String NULL = "null";
    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final int MAX_BUFFER_SIZE = 0x7FFFFFF7;
    public char[] items;
    public int size;
    public boolean ordered;

    public CharArray() {
        this(true, 16);
    }

    public CharArray(int capacity) {
        this(true, capacity);
    }

    public CharArray(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = new char[capacity];
    }

    public CharArray(CharArray array) {
        this.ordered = array.ordered;
        this.size = array.size;
        this.items = new char[this.size];
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public CharArray(char[] array) {
        this(true, array, 0, array.length);
    }

    public CharArray(boolean ordered, char[] array, int start, int count) {
        this(ordered, count);
        this.size = count;
        System.arraycopy(array, start, this.items, 0, count);
    }

    public CharArray(CharSequence seq) {
        this(seq.length() + 16);
        this.append(seq);
    }

    public CharArray(String str) {
        this(str.length() + 16);
        this.append(str);
    }

    public CharArray(StringBuilder str) {
        this(str.length() + 16);
        this.append(str);
    }

    private CharArray(char[] initialBuffer, int length) {
        Objects.requireNonNull(initialBuffer, "initialBuffer");
        this.items = initialBuffer;
        this.size = length;
    }

    public void add(char value) {
        if (this.size == this.items.length) {
            this.resizeBuffer(this.size + 1);
        }
        this.items[this.size++] = value;
    }

    public void add(char value1, char value2) {
        if (this.size + 1 >= this.items.length) {
            this.resizeBuffer(this.size + 2);
        }
        this.items[this.size] = value1;
        this.items[this.size + 1] = value2;
        this.size += 2;
    }

    public void add(char value1, char value2, char value3) {
        if (this.size + 2 >= this.items.length) {
            this.resizeBuffer(this.size + 3);
        }
        char[] items = this.items;
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        this.size += 3;
    }

    public void add(char value1, char value2, char value3, char value4) {
        if (this.size + 3 >= this.items.length) {
            this.resizeBuffer(this.size + 4);
        }
        char[] items = this.items;
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        items[this.size + 3] = value4;
        this.size += 4;
    }

    public void addAll(CharArray array) {
        this.addAll(array.items, 0, array.size);
    }

    public void addAll(CharArray array, int offset, int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        this.addAll(array.items, offset, length);
    }

    public void addAll(char ... array) {
        this.addAll(array, 0, array.length);
    }

    public void addAll(char[] array, int offset, int length) {
        int sizeNeeded = this.size + length;
        if (sizeNeeded > this.items.length) {
            this.resizeBuffer(sizeNeeded);
        }
        System.arraycopy(array, offset, this.items, this.size, length);
        this.size += length;
    }

    public char get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        return this.items[index];
    }

    public void set(int index, char value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        this.items[index] = value;
    }

    public void incr(int index, char value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        int n = index;
        this.items[n] = (char)(this.items[n] + value);
    }

    public void incr(char value) {
        char[] items = this.items;
        int i = 0;
        int n = this.size;
        while (i < n) {
            int n2 = i++;
            items[n2] = (char)(items[n2] + value);
        }
    }

    public void mul(int index, char value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        int n = index;
        this.items[n] = (char)(this.items[n] * value);
    }

    public void mul(char value) {
        char[] items = this.items;
        int i = 0;
        int n = this.size;
        while (i < n) {
            int n2 = i++;
            items[n2] = (char)(items[n2] * value);
        }
    }

    public void swap(int first, int second) {
        if (first >= this.size) {
            throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + this.size);
        }
        if (second >= this.size) {
            throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + this.size);
        }
        char[] items = this.items;
        char firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }

    public boolean replaceFirst(char value, char replacement) {
        if (value != replacement) {
            char[] items = this.items;
            int n = this.size;
            for (int i = 0; i < n; ++i) {
                if (items[i] != value) continue;
                items[i] = replacement;
                return true;
            }
        }
        return false;
    }

    public int replaceAll(char value, char replacement) {
        int replacements = 0;
        if (value != replacement) {
            char[] items = this.items;
            int n = this.size;
            for (int i = 0; i < n; ++i) {
                if (items[i] != value) continue;
                items[i] = replacement;
                ++replacements;
            }
        }
        return replacements;
    }

    public boolean contains(char value) {
        int i = this.size - 1;
        char[] items = this.items;
        while (i >= 0) {
            if (items[i--] != value) continue;
            return true;
        }
        return false;
    }

    public int indexOf(char value) {
        char[] items = this.items;
        int n = this.size;
        for (int i = 0; i < n; ++i) {
            if (items[i] != value) continue;
            return i;
        }
        return -1;
    }

    public int lastIndexOf(char value) {
        char[] items = this.items;
        for (int i = this.size - 1; i >= 0; --i) {
            if (items[i] != value) continue;
            return i;
        }
        return -1;
    }

    public boolean removeValue(char value) {
        char[] items = this.items;
        int n = this.size;
        for (int i = 0; i < n; ++i) {
            if (items[i] != value) continue;
            this.removeIndex(i);
            return true;
        }
        return false;
    }

    public char removeIndex(int index) {
        this.validateIndex(index);
        char[] items = this.items;
        char value = items[index];
        --this.size;
        if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
        } else {
            items[index] = items[this.size];
        }
        return value;
    }

    public void removeRange(int start, int end) {
        int n = this.size;
        this.validateRange(start, end);
        int count = end - start + 1;
        int lastIndex = n - count;
        if (this.ordered) {
            System.arraycopy(this.items, start + count, this.items, start, n - (start + count));
        } else {
            int i = Math.max(lastIndex, end + 1);
            System.arraycopy(this.items, i, this.items, start, n - i);
        }
        this.size = n - count;
    }

    public boolean removeAll(CharArray array) {
        int size;
        int startSize = size = this.size;
        char[] items = this.items;
        int n = array.size;
        block0: for (int i = 0; i < n; ++i) {
            char item = array.get(i);
            for (int ii = 0; ii < size; ++ii) {
                if (item != items[ii]) continue;
                this.removeIndex(ii);
                --size;
                continue block0;
            }
        }
        return size != startSize;
    }

    public char pop() {
        return this.items[--this.size];
    }

    public char peek() {
        return this.items[this.size - 1];
    }

    public char first() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        return this.items[0];
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        this.size = 0;
    }

    public char[] setSize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        }
        if (newSize > this.items.length) {
            this.resize(Math.max(8, newSize));
        }
        this.size = newSize;
        return this.items;
    }

    public char[] shrink() {
        if (this.items.length > this.size) {
            this.resize(this.size);
        }
        return this.items;
    }

    public void trimToSize() {
        this.shrink();
    }

    public char[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded - this.items.length > 0) {
            this.resizeBuffer(sizeNeeded);
        }
        return this.items;
    }

    private void require(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded - this.items.length > 0) {
            this.resizeBuffer(sizeNeeded);
        }
    }

    private void resizeBuffer(int minCapacity) {
        int oldCapacity = this.items.length;
        int newCapacity = (oldCapacity >> 1) + oldCapacity + 2;
        if ((newCapacity ^ Integer.MIN_VALUE) < (minCapacity ^ Integer.MIN_VALUE)) {
            newCapacity = minCapacity;
        }
        if ((newCapacity ^ Integer.MIN_VALUE) > -9) {
            if (minCapacity < 0) {
                throw new RuntimeException("Unable to allocate array size: " + ((long)minCapacity & 0xFFFFFFFFL));
            }
            newCapacity = Math.max(minCapacity, 0x7FFFFFF7);
        }
        this.resize(newCapacity);
    }

    protected char[] resize(int newSize) {
        this.items = Arrays.copyOf(this.items, newSize);
        return this.items;
    }

    public void sort() {
        Arrays.sort(this.items, 0, this.size);
    }

    public void shuffle() {
        char[] items = this.items;
        for (int i = this.size - 1; i >= 0; --i) {
            int ii = MathUtils.random(i);
            char temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public void truncate(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        }
        if (this.size > newSize) {
            this.size = newSize;
        }
    }

    public char random() {
        if (this.size == 0) {
            return '\u0000';
        }
        return this.items[MathUtils.random(0, this.size - 1)];
    }

    public char[] toArray() {
        char[] array = new char[this.size];
        System.arraycopy(this.items, 0, array, 0, this.size);
        return array;
    }

    public CharArray append(boolean value) {
        if (value) {
            this.require(4);
            this.appendTrue(this.size);
        } else {
            this.require(5);
            this.appendFalse(this.size);
        }
        return this;
    }

    @Override
    public CharArray append(char value) {
        this.require(1);
        this.items[this.size++] = value;
        return this;
    }

    public CharArray append(@Null char[] ch) {
        if (ch == null) {
            return this.appendNull();
        }
        int strLength = ch.length;
        if (strLength > 0) {
            this.require(strLength);
            System.arraycopy(ch, 0, this.items, this.size, strLength);
            this.size += strLength;
        }
        return this;
    }

    public CharArray append(@Null char[] ch, int start, int length) {
        if (ch == null) {
            return this.appendNull();
        }
        if (start < 0 || start > ch.length) {
            throw new IndexOutOfBoundsException("Invalid start: " + start);
        }
        if (length < 0 || start + length > ch.length) {
            throw new IndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            this.require(length);
            System.arraycopy(ch, start, this.items, this.size, length);
            this.size += length;
        }
        return this;
    }

    public CharArray append(@Null CharBuffer str) {
        if (str == null) {
            this.appendNull();
        } else {
            this.append(str, 0, str.length());
        }
        return this;
    }

    public CharArray append(@Null CharBuffer buf, int start, int end) {
        if (buf == null) {
            return this.appendNull();
        }
        if (buf.hasArray()) {
            int totalLength = buf.remaining();
            if (start < 0 || end < 0 || start > end || end > totalLength) {
                throw new IndexOutOfBoundsException();
            }
            int length = end - start;
            this.require(length);
            System.arraycopy(buf.array(), buf.arrayOffset() + buf.position() + start, this.items, this.size, length);
            this.size += length;
        } else {
            this.append(buf.toString(), start, end);
        }
        return this;
    }

    @Override
    public CharArray append(@Null CharSequence seq) {
        if (seq == null) {
            return this.appendNull();
        }
        if (seq instanceof CharArray) {
            return this.append((CharArray)seq);
        }
        if (seq instanceof StringBuilder) {
            return this.append((StringBuilder)seq);
        }
        if (seq instanceof StringBuffer) {
            return this.append((StringBuffer)seq);
        }
        if (seq instanceof CharBuffer) {
            return this.append((CharBuffer)seq);
        }
        return this.append(seq.toString());
    }

    @Override
    public CharArray append(@Null CharSequence seq, int start, int end) {
        if (seq == null) {
            return this.appendNull();
        }
        if (start < 0 || end < 0 || start > end || end > seq.length()) {
            throw new IndexOutOfBoundsException();
        }
        return this.append(seq.toString(), start, end);
    }

    public CharArray append(double value) {
        return this.append(String.valueOf(value));
    }

    public CharArray append(float value) {
        return this.append(String.valueOf(value));
    }

    public CharArray append(int value) {
        return this.append(value, 0, '0');
    }

    public CharArray append(int value, int minLength) {
        return this.append(value, minLength, '0');
    }

    public CharArray append(int value, int minLength, char prefix) {
        if (value == Integer.MIN_VALUE) {
            this.append("-2147483648");
            return this;
        }
        if (value < 0) {
            this.append('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - CharArray.numChars(value, 10); j > 0; --j) {
                this.append(prefix);
            }
        }
        if (value >= 10000) {
            if (value >= 1000000000) {
                this.append(DIGITS[(int)((long)value % 10000000000L / 1000000000L)]);
            }
            if (value >= 100000000) {
                this.append(DIGITS[value % 1000000000 / 100000000]);
            }
            if (value >= 10000000) {
                this.append(DIGITS[value % 100000000 / 10000000]);
            }
            if (value >= 1000000) {
                this.append(DIGITS[value % 10000000 / 1000000]);
            }
            if (value >= 100000) {
                this.append(DIGITS[value % 1000000 / 100000]);
            }
            this.append(DIGITS[value % 100000 / 10000]);
        }
        if (value >= 1000) {
            this.append(DIGITS[value % 10000 / 1000]);
        }
        if (value >= 100) {
            this.append(DIGITS[value % 1000 / 100]);
        }
        if (value >= 10) {
            this.append(DIGITS[value % 100 / 10]);
        }
        this.append(DIGITS[value % 10]);
        return this;
    }

    public CharArray append(long value) {
        return this.append(value, 0, '0');
    }

    public CharArray append(long value, int minLength) {
        return this.append(value, minLength, '0');
    }

    public CharArray append(long value, int minLength, char prefix) {
        if (value == Long.MIN_VALUE) {
            this.append("-9223372036854775808");
            return this;
        }
        if (value < 0L) {
            this.append('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - CharArray.numChars(value, 10); j > 0; --j) {
                this.append(prefix);
            }
        }
        if (value >= 10000L) {
            if (value >= 1000000000000000000L) {
                this.append(DIGITS[(int)((double)value % 1.0E19 / 1.0E18)]);
            }
            if (value >= 100000000000000000L) {
                this.append(DIGITS[(int)(value % 1000000000000000000L / 100000000000000000L)]);
            }
            if (value >= 10000000000000000L) {
                this.append(DIGITS[(int)(value % 100000000000000000L / 10000000000000000L)]);
            }
            if (value >= 1000000000000000L) {
                this.append(DIGITS[(int)(value % 10000000000000000L / 1000000000000000L)]);
            }
            if (value >= 100000000000000L) {
                this.append(DIGITS[(int)(value % 1000000000000000L / 100000000000000L)]);
            }
            if (value >= 10000000000000L) {
                this.append(DIGITS[(int)(value % 100000000000000L / 10000000000000L)]);
            }
            if (value >= 1000000000000L) {
                this.append(DIGITS[(int)(value % 10000000000000L / 1000000000000L)]);
            }
            if (value >= 100000000000L) {
                this.append(DIGITS[(int)(value % 1000000000000L / 100000000000L)]);
            }
            if (value >= 10000000000L) {
                this.append(DIGITS[(int)(value % 100000000000L / 10000000000L)]);
            }
            if (value >= 1000000000L) {
                this.append(DIGITS[(int)(value % 10000000000L / 1000000000L)]);
            }
            if (value >= 100000000L) {
                this.append(DIGITS[(int)(value % 1000000000L / 100000000L)]);
            }
            if (value >= 10000000L) {
                this.append(DIGITS[(int)(value % 100000000L / 10000000L)]);
            }
            if (value >= 1000000L) {
                this.append(DIGITS[(int)(value % 10000000L / 1000000L)]);
            }
            if (value >= 100000L) {
                this.append(DIGITS[(int)(value % 1000000L / 100000L)]);
            }
            this.append(DIGITS[(int)(value % 100000L / 10000L)]);
        }
        if (value >= 1000L) {
            this.append(DIGITS[(int)(value % 10000L / 1000L)]);
        }
        if (value >= 100L) {
            this.append(DIGITS[(int)(value % 1000L / 100L)]);
        }
        if (value >= 10L) {
            this.append(DIGITS[(int)(value % 100L / 10L)]);
        }
        this.append(DIGITS[(int)(value % 10L)]);
        return this;
    }

    public CharArray append(@Null Object obj) {
        if (obj == null) {
            return this.appendNull();
        }
        if (obj instanceof CharSequence) {
            return this.append((CharSequence)obj);
        }
        return this.append(obj.toString());
    }

    public CharArray append(@Null String str) {
        if (str == null) {
            this.appendNull();
        } else {
            int length = str.length();
            this.require(length);
            str.getChars(0, length, this.items, this.size);
            this.size += length;
        }
        return this;
    }

    public CharArray append(String str, String separator) {
        if (this.size > 0) {
            this.append(separator);
        }
        this.append(str);
        return this;
    }

    public CharArray append(@Null String str, int start, int end) {
        if (str == null) {
            return this.appendNull();
        }
        if (start < 0 || end < 0 || start > end || end > str.length()) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        if (length > 0) {
            this.require(length);
            str.getChars(start, end, this.items, this.size);
            this.size += length;
        }
        return this;
    }

    public CharArray append(@Null StringBuffer str) {
        if (str == null) {
            this.appendNull();
        } else {
            this.append(str, 0, str.length());
        }
        return this;
    }

    public CharArray append(@Null StringBuffer str, int start, int end) {
        if (str == null) {
            return this.appendNull();
        }
        if (start < 0 || end < 0 || start > end || end > str.length()) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        if (length > 0) {
            this.require(length);
            str.getChars(start, end, this.items, this.size);
            this.size += length;
        }
        return this;
    }

    public CharArray append(@Null StringBuilder str) {
        if (str == null) {
            this.appendNull();
        } else {
            this.append(str, 0, str.length());
        }
        return this;
    }

    public CharArray append(@Null StringBuilder str, int start, int end) {
        if (str == null) {
            return this.appendNull();
        }
        if (start < 0 || end < 0 || start > end || end > str.length()) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        if (length > 0) {
            this.require(length);
            str.getChars(start, end, this.items, this.size);
            this.size += length;
        }
        return this;
    }

    public CharArray append(@Null CharArray str) {
        if (str == null) {
            this.appendNull();
        } else {
            this.append(str, 0, str.size);
        }
        return this;
    }

    public CharArray append(@Null CharArray str, int start, int end) {
        if (str == null) {
            return this.appendNull();
        }
        if (start < 0 || end < 0 || start > end || end > str.size) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        if (length > 0) {
            this.require(length);
            str.getChars(start, end, this.items, this.size);
            this.size += length;
        }
        return this;
    }

    public CharArray appendAll(Iterable<?> iterable) {
        Iterator<?> iter = iterable.iterator();
        while (iter.hasNext()) {
            this.append(iter.next());
        }
        return this;
    }

    public CharArray appendAll(Iterator<?> iter) {
        while (iter.hasNext()) {
            this.append(iter.next());
        }
        return this;
    }

    public final <T> CharArray appendAll(T ... array) {
        if (array != null) {
            for (T element : array) {
                this.append(element);
            }
        }
        return this;
    }

    private void appendFalse(int index) {
        this.items[index++] = 102;
        this.items[index++] = 97;
        this.items[index++] = 108;
        this.items[index++] = 115;
        this.items[index] = 101;
        this.size += 5;
    }

    public CharArray appendFixedWidthPadLeft(int value, int width, char padChar) {
        return this.appendFixedWidthPadLeft(String.valueOf(value), width, padChar);
    }

    public CharArray appendFixedWidthPadLeft(@Null Object obj, int width, char padChar) {
        if (width > 0) {
            this.require(width);
            String str = Objects.toString(obj, NULL);
            int strLength = str.length();
            if (strLength >= width) {
                str.getChars(strLength - width, strLength, this.items, this.size);
            } else {
                int padLen = width - strLength;
                int toIndex = this.size + padLen;
                Arrays.fill(this.items, this.size, toIndex, padChar);
                str.getChars(0, strLength, this.items, toIndex);
            }
            this.size += width;
        }
        return this;
    }

    public CharArray appendFixedWidthPadRight(int value, int width, char padChar) {
        return this.appendFixedWidthPadRight(String.valueOf(value), width, padChar);
    }

    public CharArray appendFixedWidthPadRight(@Null Object obj, int width, char padChar) {
        if (width > 0) {
            this.require(width);
            String str = Objects.toString(obj, NULL);
            int strLength = str.length();
            if (strLength >= width) {
                str.getChars(0, width, this.items, this.size);
            } else {
                str.getChars(0, strLength, this.items, this.size);
                int fromIndex = this.size + strLength;
                Arrays.fill(this.items, fromIndex, fromIndex + width - strLength, padChar);
            }
            this.size += width;
        }
        return this;
    }

    public CharArray appendln(boolean value) {
        return this.append(value).appendLine();
    }

    public CharArray appendln(char ch) {
        return this.append(ch).appendLine();
    }

    public CharArray appendln(@Null char[] ch) {
        return this.append(ch).appendLine();
    }

    public CharArray appendln(@Null char[] ch, int start, int length) {
        return this.append(ch, start, length).appendLine();
    }

    public CharArray appendln(double value) {
        return this.append(value).appendLine();
    }

    public CharArray appendln(float value) {
        return this.append(value).appendLine();
    }

    public CharArray appendln(int value) {
        return this.append(value).appendLine();
    }

    public CharArray appendln(long value) {
        return this.append(value).appendLine();
    }

    public CharArray appendln(@Null Object obj) {
        return this.append(obj).appendLine();
    }

    public CharArray appendln(@Null String str) {
        this.append(str);
        return this.append('\n');
    }

    public CharArray appendLine(@Null String str) {
        this.append(str);
        return this.append('\n');
    }

    public CharArray appendln(@Null String str, int start, int end) {
        return this.append(str, start, end).appendLine();
    }

    public CharArray appendln(@Null StringBuffer str) {
        return this.append(str).appendLine();
    }

    public CharArray appendln(@Null StringBuffer str, int start, int end) {
        return this.append(str, start, end).appendLine();
    }

    public CharArray appendln(@Null StringBuilder str) {
        return this.append(str).appendLine();
    }

    public CharArray appendln(@Null StringBuilder str, int start, int end) {
        return this.append(str, start, end).appendLine();
    }

    public CharArray appendln(@Null CharArray str) {
        return this.append(str).appendLine();
    }

    public CharArray appendln(@Null CharArray str, int start, int end) {
        return this.append(str, start, end).appendLine();
    }

    public CharArray appendln() {
        return this.append('\n');
    }

    public CharArray appendLine() {
        return this.append('\n');
    }

    public CharArray appendNull() {
        this.require(4);
        int length = this.size;
        this.items[length] = 110;
        this.items[length + 1] = 117;
        this.items[length + 2] = 108;
        this.items[length + 3] = 108;
        this.size = length + 4;
        return this;
    }

    public CharArray appendPadding(int padCount, char padChar) {
        if (padCount > 0) {
            this.require(padCount);
            Arrays.fill(this.items, this.size, this.size + padCount, padChar);
            this.size += padCount;
        }
        return this;
    }

    public CharArray appendSeparator(char separator) {
        if (this.notEmpty()) {
            this.append(separator);
        }
        return this;
    }

    public CharArray appendSeparator(char standard, char defaultIfEmpty) {
        if (this.isEmpty()) {
            this.append(defaultIfEmpty);
        } else {
            this.append(standard);
        }
        return this;
    }

    public CharArray appendSeparator(char separator, int loopIndex) {
        if (loopIndex > 0) {
            this.append(separator);
        }
        return this;
    }

    public CharArray appendSeparator(@Null String separator) {
        return this.appendSeparator(separator, null);
    }

    public CharArray appendSeparator(@Null String separator, int loopIndex) {
        if (separator != null && loopIndex > 0) {
            this.append(separator);
        }
        return this;
    }

    public CharArray appendSeparator(@Null String standard, @Null String defaultIfEmpty) {
        String str;
        String string = str = this.isEmpty() ? defaultIfEmpty : standard;
        if (str != null) {
            this.append(str);
        }
        return this;
    }

    public void appendTo(Appendable appendable) throws IOException {
        if (appendable instanceof Writer) {
            ((Writer)appendable).write(this.items, 0, this.size);
        } else if (appendable instanceof StringBuilder) {
            ((StringBuilder)appendable).append(this.items, 0, this.size);
        } else if (appendable instanceof StringBuffer) {
            ((StringBuffer)appendable).append(this.items, 0, this.size);
        } else if (appendable instanceof CharBuffer) {
            ((CharBuffer)appendable).put(this.items, 0, this.size);
        } else {
            appendable.append(this);
        }
    }

    private void appendTrue(int index) {
        this.items[index++] = 116;
        this.items[index++] = 114;
        this.items[index++] = 117;
        this.items[index] = 101;
        this.size += 4;
    }

    public CharArray appendWithSeparators(Iterable<?> iterable, @Null String separator) {
        this.appendWithSeparators(iterable.iterator(), separator);
        return this;
    }

    public CharArray appendWithSeparators(Iterator<?> it, @Null String separator) {
        String sep = Objects.toString(separator, "");
        while (it.hasNext()) {
            this.append(it.next());
            if (!it.hasNext()) continue;
            this.append(sep);
        }
        return this;
    }

    public CharArray appendWithSeparators(Object[] array, @Null String separator) {
        if (array.length > 0) {
            String sep = Objects.toString(separator, "");
            this.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                this.append(sep);
                this.append(array[i]);
            }
        }
        return this;
    }

    public CharArray appendCodePoint(int codePoint) {
        this.append(Character.toChars(codePoint));
        return this;
    }

    public Reader reader() {
        return new CharArrayReader();
    }

    public Writer writer() {
        return new CharArrayWriter();
    }

    @Override
    public char charAt(int index) {
        return this.items[index];
    }

    public int codePointAt(int index) {
        this.validateIndex(index);
        return Character.codePointAt(this.items, index, this.size);
    }

    public int codePointBefore(int index) {
        if (index < 1 || index > this.size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size);
        }
        return Character.codePointBefore(this.items, index);
    }

    public int codePointCount(int begin, int end) {
        if (begin < 0 || end > this.size || begin > end) {
            throw new IndexOutOfBoundsException();
        }
        return Character.codePointCount(this.items, begin, end - begin);
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return Character.offsetByCodePoints(this.items, 0, this.size, index, codePointOffset);
    }

    public boolean contains(String str) {
        return this.indexOf(str, 0) >= 0;
    }

    public boolean containsIgnoreCase(String str) {
        return this.indexOfIgnoreCase(str, 0) != -1;
    }

    public CharArray delete(int start, int end) {
        int actualEnd = this.validateRange(start, end);
        int length = actualEnd - start;
        if (length > 0) {
            this.delete(start, actualEnd, length);
        }
        return this;
    }

    public CharArray deleteAll(char ch) {
        for (int i = 0; i < this.size; ++i) {
            if (this.items[i] != ch) continue;
            int start = i;
            while (++i < this.size && this.items[i] == ch) {
            }
            int length = i - start;
            this.delete(start, i, length);
            i -= length;
        }
        return this;
    }

    public CharArray deleteAll(String str) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        int length = str.length();
        if (length > 0) {
            int index = this.indexOf(str, 0);
            while (index >= 0) {
                this.delete(index, index + length, length);
                index = this.indexOf(str, index);
            }
        }
        return this;
    }

    public CharArray deleteCharAt(int index) {
        this.validateIndex(index);
        this.delete(index, index + 1, 1);
        return this;
    }

    public CharArray deleteFirst(char ch) {
        for (int i = 0; i < this.size; ++i) {
            if (this.items[i] != ch) continue;
            this.delete(i, i + 1, 1);
            break;
        }
        return this;
    }

    public CharArray deleteFirst(String str) {
        int index;
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        int length = str.length();
        if (length > 0 && (index = this.indexOf(str, 0)) >= 0) {
            this.delete(index, index + length, length);
        }
        return this;
    }

    private void delete(int start, int end, int length) {
        System.arraycopy(this.items, end, this.items, start, this.size - end);
        this.size -= length;
    }

    public char drainChar(int index) {
        this.validateIndex(index);
        char c = this.items[index];
        this.deleteCharAt(index);
        return c;
    }

    public int drainChars(int start, int end, char[] target, int targetIndex) {
        int length = end - start;
        if (this.isEmpty() || length == 0 || target.length == 0) {
            return 0;
        }
        int actualLength = Math.min(Math.min(this.size, length), target.length - targetIndex);
        this.getChars(start, start + actualLength, target, targetIndex);
        this.delete(start, start + actualLength);
        return actualLength;
    }

    public boolean endsWith(String str) {
        int length = str.length();
        if (length == 0) {
            return true;
        }
        if (length > this.size) {
            return false;
        }
        int pos = this.size - length;
        int i = 0;
        while (i < length) {
            if (this.items[pos] != str.charAt(i)) {
                return false;
            }
            ++i;
            ++pos;
        }
        return true;
    }

    public char[] getChars(@Null char[] target) {
        int length = this.size;
        if (target == null || target.length < length) {
            target = new char[length];
        }
        System.arraycopy(this.items, 0, target, 0, length);
        return target;
    }

    public void getChars(int start, int end, char[] target, int targetIndex) {
        if (start < 0) {
            throw new IndexOutOfBoundsException("start: " + start);
        }
        if (end < 0 || end > this.size) {
            throw new IndexOutOfBoundsException("end: " + end + ", size: " + this.size);
        }
        if (start > end) {
            throw new IndexOutOfBoundsException("end < start");
        }
        System.arraycopy(this.items, start, target, targetIndex, end - start);
    }

    public int indexOf(char ch, int start) {
        if ((start = Math.max(0, start)) >= this.size) {
            return -1;
        }
        char[] thisBuf = this.items;
        int n = this.size;
        for (int i = start; i < n; ++i) {
            if (thisBuf[i] != ch) continue;
            return i;
        }
        return -1;
    }

    public int indexOf(String str) {
        return this.indexOf(str, 0);
    }

    public int indexOf(String str, int start) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        if ((start = Math.max(0, start)) >= this.size) {
            return -1;
        }
        int strLen = str.length();
        if (strLen == 1) {
            return this.indexOf(str.charAt(0), start);
        }
        if (strLen == 0) {
            return start;
        }
        if (strLen > this.size) {
            return -1;
        }
        char[] thisBuf = this.items;
        int searchLen = this.size - strLen + 1;
        for (int i = start; i < searchLen; ++i) {
            boolean found = true;
            for (int j = 0; j < strLen && found; ++j) {
                found = str.charAt(j) == thisBuf[i + j];
            }
            if (!found) continue;
            return i;
        }
        return -1;
    }

    public int indexOfIgnoreCase(String str, int start) {
        int length;
        if (start < 0) {
            start = 0;
        }
        if ((length = str.length()) == 0) {
            return start < this.size || start == 0 ? start : this.size;
        }
        int maxIndex = this.size - length;
        if (start > maxIndex) {
            return -1;
        }
        char firstUpper = Character.toUpperCase(str.charAt(0));
        char firstLower = Character.toLowerCase(firstUpper);
        while (true) {
            char upper;
            char c;
            int i;
            boolean found = false;
            for (i = start; i <= maxIndex; ++i) {
                char c2 = this.items[i];
                if (c2 != firstUpper && c2 != firstLower) continue;
                found = true;
                break;
            }
            if (!found) {
                return -1;
            }
            int o1 = i;
            int o2 = 0;
            while (++o2 < length && ((c = this.items[++o1]) == (upper = Character.toUpperCase(str.charAt(o2))) || c == Character.toLowerCase(upper))) {
            }
            if (o2 == length) {
                return i;
            }
            start = i + 1;
        }
    }

    public CharArray insert(int index, boolean value) {
        this.validateIndex(index);
        if (value) {
            this.require(4);
            System.arraycopy(this.items, index, this.items, index + 4, this.size - index);
            this.appendTrue(index);
        } else {
            this.require(5);
            System.arraycopy(this.items, index, this.items, index + 5, this.size - index);
            this.appendFalse(index);
        }
        return this;
    }

    public void insert(int index, char value) {
        this.validateIndex(index);
        this.require(1);
        char[] items = this.items;
        if (this.ordered) {
            System.arraycopy(items, index, items, index + 1, this.size - index);
        } else {
            items[this.size] = items[index];
        }
        ++this.size;
        items[index] = value;
    }

    public void insertRange(int index, int count) {
        this.validateIndex(index);
        int sizeNeeded = this.size + count;
        if (sizeNeeded > this.items.length) {
            this.items = this.resize(Math.max(Math.max(8, sizeNeeded), (int)((float)this.size * 1.75f)));
        }
        System.arraycopy(this.items, index, this.items, index + count, this.size - index);
        this.size = sizeNeeded;
    }

    public CharArray insert(int index, @Null char[] ch) {
        this.validateIndex(index);
        if (ch == null) {
            return this.insert(index, NULL);
        }
        int length = ch.length;
        if (length > 0) {
            this.require(length);
            System.arraycopy(this.items, index, this.items, index + length, this.size - index);
            System.arraycopy(ch, 0, this.items, index, length);
            this.size += length;
        }
        return this;
    }

    public CharArray insert(int index, @Null char[] ch, int offset, int length) {
        this.validateIndex(index);
        if (ch == null) {
            return this.insert(index, NULL);
        }
        if (offset < 0 || offset > ch.length) {
            throw new IndexOutOfBoundsException("Invalid offset: " + offset);
        }
        if (length < 0 || offset + length > ch.length) {
            throw new IndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            this.require(length);
            System.arraycopy(this.items, index, this.items, index + length, this.size - index);
            System.arraycopy(ch, offset, this.items, index, length);
            this.size += length;
        }
        return this;
    }

    public CharArray insert(int index, double value) {
        return this.insert(index, String.valueOf(value));
    }

    public CharArray insert(int index, float value) {
        return this.insert(index, String.valueOf(value));
    }

    public CharArray insert(int index, int value) {
        return this.insert(index, String.valueOf(value));
    }

    public CharArray insert(int index, long value) {
        return this.insert(index, String.valueOf(value));
    }

    public CharArray insert(int index, @Null Object obj) {
        if (obj == null) {
            return this.insert(index, NULL);
        }
        return this.insert(index, obj.toString());
    }

    public CharArray insert(int index, @Null String str) {
        int strLength;
        this.validateIndex(index);
        if (str == null) {
            str = NULL;
        }
        if ((strLength = str.length()) > 0) {
            this.require(strLength);
            System.arraycopy(this.items, index, this.items, index + strLength, this.size - index);
            this.size += strLength;
            str.getChars(0, strLength, this.items, index);
        }
        return this;
    }

    public int lastIndexOf(char ch, int start) {
        int n = start = start >= this.size ? this.size - 1 : start;
        if (start < 0) {
            return -1;
        }
        for (int i = start; i >= 0; --i) {
            if (this.items[i] != ch) continue;
            return i;
        }
        return -1;
    }

    public int lastIndexOf(String str) {
        return this.lastIndexOf(str, this.size - 1);
    }

    public int lastIndexOf(String str, int start) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        int n = start = start >= this.size ? this.size - 1 : start;
        if (start < 0) {
            return -1;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return start;
        }
        if (strLen > this.size) {
            return -1;
        }
        if (strLen == 1) {
            return this.lastIndexOf(str.charAt(0), start);
        }
        for (int i = start - strLen + 1; i >= 0; --i) {
            boolean found = true;
            for (int j = 0; j < strLen && found; ++j) {
                found = str.charAt(j) == this.items[i + j];
            }
            if (!found) continue;
            return i;
        }
        return -1;
    }

    public String leftString(int length) {
        if (length <= 0) {
            return "";
        }
        if (length >= this.size) {
            return new String(this.items, 0, this.size);
        }
        return new String(this.items, 0, length);
    }

    @Override
    public int length() {
        return this.size;
    }

    public int capacity() {
        return this.items.length;
    }

    public String midString(int index, int length) {
        if (index < 0) {
            index = 0;
        }
        if (length <= 0 || index >= this.size) {
            return "";
        }
        if (this.size <= index + length) {
            return new String(this.items, index, this.size - index);
        }
        return new String(this.items, index, length);
    }

    public int readFrom(CharBuffer charBuffer) {
        int oldSize = this.size;
        int remaining = charBuffer.remaining();
        this.require(remaining);
        charBuffer.get(this.items, this.size, remaining);
        this.size += remaining;
        return this.size - oldSize;
    }

    public int readFrom(Readable readable) throws IOException {
        if (readable instanceof Reader) {
            return this.readFrom((Reader)readable);
        }
        if (readable instanceof CharBuffer) {
            return this.readFrom((CharBuffer)readable);
        }
        int oldSize = this.size;
        while (true) {
            this.require(1);
            CharBuffer buf = CharBuffer.wrap(this.items, this.size, this.items.length - this.size);
            int read = readable.read(buf);
            if (read == -1) break;
            this.size += read;
        }
        return this.size - oldSize;
    }

    public int readFrom(Reader reader) throws IOException {
        int oldSize = this.size;
        this.require(1);
        int readCount = reader.read(this.items, this.size, this.items.length - this.size);
        if (readCount == -1) {
            return -1;
        }
        do {
            this.size += readCount;
            this.require(1);
        } while ((readCount = reader.read(this.items, this.size, this.items.length - this.size)) != -1);
        return this.size - oldSize;
    }

    public int readFrom(Reader reader, int count) throws IOException {
        if (count <= 0) {
            return 0;
        }
        int oldSize = this.size;
        this.require(count);
        int target = count;
        int readCount = reader.read(this.items, this.size, target);
        if (readCount == -1) {
            return -1;
        }
        do {
            this.size += readCount;
            readCount = reader.read(this.items, this.size, target -= readCount);
        } while (target > 0 && readCount != -1);
        return this.size - oldSize;
    }

    public CharArray replace(int start, int end, String replaceStr) {
        end = this.validateRange(start, end);
        this.replace(start, end, end - start, replaceStr, replaceStr.length());
        return this;
    }

    public CharArray replaceAll(String searchStr, String replaceStr) {
        int searchLength = searchStr.length();
        if (searchLength > 0) {
            int replaceLength = replaceStr.length();
            int index = this.indexOf(searchStr, 0);
            while (index >= 0) {
                this.replace(index, index + searchLength, searchLength, replaceStr, replaceLength);
                index = this.indexOf(searchStr, index + replaceLength);
            }
        }
        return this;
    }

    public CharArray replace(char find, String replace) {
        int replaceLength = replace.length();
        int index = 0;
        while (index != this.size) {
            if (this.items[index] != find) {
                ++index;
                continue;
            }
            this.replace(index, index + 1, 1, replace, replaceLength);
            index += replaceLength;
        }
        return this;
    }

    public CharArray replaceFirst(String searchStr, String replaceStr) {
        int index;
        int searchLength = searchStr.length();
        if (searchLength > 0 && (index = this.indexOf(searchStr, 0)) >= 0) {
            int replaceLength = replaceStr.length();
            this.replace(index, index + searchLength, searchLength, replaceStr, replaceLength);
        }
        return this;
    }

    private void replace(int start, int end, int removeLength, @Null String insertStr, int insertLength) {
        int newSize = this.size - removeLength + insertLength;
        if (insertLength != removeLength) {
            this.require(newSize);
            System.arraycopy(this.items, end, this.items, start + insertLength, this.size - end);
            this.size = newSize;
        }
        if (insertLength > 0) {
            insertStr.getChars(0, insertLength, this.items, start);
        }
    }

    public void reverse() {
        char[] items = this.items;
        int lastIndex = this.size - 1;
        int n = this.size / 2;
        for (int i = 0; i < n; ++i) {
            int ii = lastIndex - i;
            char temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public CharArray reverseCodePoints() {
        if (this.size < 2) {
            return this;
        }
        int end = this.size - 1;
        char frontHigh = this.items[0];
        char endLow = this.items[end];
        boolean allowFrontSur = true;
        boolean allowEndSur = true;
        int i = 0;
        int mid = this.size / 2;
        while (i < mid) {
            boolean surAtFront;
            char frontLow = this.items[i + 1];
            char endHigh = this.items[end - 1];
            boolean bl = surAtFront = allowFrontSur && frontLow >= '\udc00' && frontLow <= '\udfff' && frontHigh >= '\ud800' && frontHigh <= '\udbff';
            if (surAtFront && this.size < 3) {
                return this;
            }
            boolean surAtEnd = allowEndSur && endHigh >= '\ud800' && endHigh <= '\udbff' && endLow >= '\udc00' && endLow <= '\udfff';
            allowEndSur = true;
            allowFrontSur = true;
            if (surAtFront == surAtEnd) {
                if (surAtFront) {
                    this.items[end] = frontLow;
                    this.items[end - 1] = frontHigh;
                    this.items[i] = endHigh;
                    this.items[i + 1] = endLow;
                    frontHigh = this.items[i + 2];
                    endLow = this.items[end - 2];
                    ++i;
                    --end;
                } else {
                    this.items[end] = frontHigh;
                    this.items[i] = endLow;
                    frontHigh = frontLow;
                    endLow = endHigh;
                }
            } else if (surAtFront) {
                this.items[end] = frontLow;
                this.items[i] = endLow;
                endLow = endHigh;
                allowFrontSur = false;
            } else {
                this.items[end] = frontHigh;
                this.items[i] = endHigh;
                frontHigh = frontLow;
                allowEndSur = false;
            }
            ++i;
            --end;
        }
        if (!((this.size & 1) != 1 || allowFrontSur && allowEndSur)) {
            this.items[end] = allowFrontSur ? endLow : frontHigh;
        }
        return this;
    }

    public String rightString(int length) {
        if (length <= 0) {
            return "";
        }
        if (length >= this.size) {
            return new String(this.items, 0, this.size);
        }
        return new String(this.items, this.size - length, length);
    }

    public CharArray set(CharSequence str) {
        this.clear();
        this.append(str);
        return this;
    }

    public CharArray setCharAt(int index, char ch) {
        this.validateIndex(index);
        this.items[index] = ch;
        return this;
    }

    public CharArray setLength(int length) {
        if (length < 0) {
            throw new IndexOutOfBoundsException("length: " + length);
        }
        if (length < this.size) {
            this.size = length;
        } else if (length > this.size) {
            this.require(length - this.size);
            int oldEnd = this.size;
            this.size = length;
            Arrays.fill(this.items, oldEnd, length, '\u0000');
        }
        return this;
    }

    public boolean startsWith(String str) {
        int length = str.length();
        if (length == 0) {
            return true;
        }
        if (length > this.size) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (this.items[i] == str.charAt(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        this.validateRange(start, end);
        return this.substring(start, end);
    }

    public String substring(int start) {
        return this.substring(start, this.size);
    }

    public String substring(int start, int end) {
        end = this.validateRange(start, end);
        return new String(this.items, start, end - start);
    }

    public char[] toCharArray() {
        return Arrays.copyOf(this.items, this.size);
    }

    public char[] toCharArray(int start, int end) {
        end = this.validateRange(start, end);
        return Arrays.copyOfRange(this.items, start, end);
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "";
        }
        return new String(this.items, 0, this.size);
    }

    public String toString(String separator) {
        if (this.size == 0) {
            return "";
        }
        char[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append(items[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append(items[i]);
        }
        return buffer.toString();
    }

    public String toStringAndClear() {
        String string = this.toString();
        this.clear();
        return string;
    }

    public CharArray trim() {
        int pos;
        if (this.size == 0) {
            return this;
        }
        int length = this.size;
        char[] buf = this.items;
        for (pos = 0; pos < length && buf[pos] <= ' '; ++pos) {
        }
        while (pos < length && buf[length - 1] <= ' ') {
            --length;
        }
        if (length < this.size) {
            this.delete(length, this.size);
        }
        if (pos > 0) {
            this.delete(0, pos);
        }
        return this;
    }

    protected void validateIndex(int index) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size);
        }
    }

    protected int validateRange(int start, int end) {
        if (start < 0) {
            throw new IndexOutOfBoundsException("start: " + start);
        }
        if (end > this.size) {
            throw new IndexOutOfBoundsException("end: " + end + ", size: " + this.size);
        }
        if (start > end) {
            throw new IndexOutOfBoundsException("start: " + start + ", end: " + end);
        }
        return end;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!this.ordered) {
            return false;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof CharArray)) {
            return false;
        }
        CharArray other = (CharArray)object;
        if (!other.ordered) {
            return false;
        }
        int length = this.size;
        if (length != other.size) {
            return false;
        }
        char[] chars = this.items;
        char[] chars2 = other.items;
        for (int i = 0; i < length; ++i) {
            if (chars[i] == chars2[i]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(CharArray other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        int length = this.size;
        if (length != other.size) {
            return false;
        }
        char[] chars = this.items;
        char[] chars2 = other.items;
        for (int i = 0; i < length; ++i) {
            if (chars[i] == chars2[i]) continue;
            return false;
        }
        return true;
    }

    public boolean equalsIgnoreCase(CharArray other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        int length = this.size;
        if (length != other.size) {
            return false;
        }
        char[] chars = this.items;
        char[] chars2 = other.items;
        for (int i = 0; i < length; ++i) {
            char c = chars[i];
            char upper = Character.toUpperCase(chars2[i]);
            if (c == upper || c == Character.toLowerCase(upper)) continue;
            return false;
        }
        return true;
    }

    public boolean equalsString(@Null String other) {
        if (other == null) {
            return false;
        }
        int length = this.size;
        if (length != other.length()) {
            return false;
        }
        char[] chars = this.items;
        for (int i = 0; i < length; ++i) {
            if (chars[i] == other.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public boolean equalsIgnoreCase(@Null String other) {
        if (other == null) {
            return false;
        }
        int length = this.size;
        if (length != other.length()) {
            return false;
        }
        char[] chars = this.items;
        for (int i = 0; i < length; ++i) {
            char c = chars[i];
            char upper = Character.toUpperCase(other.charAt(i));
            if (c == upper || c == Character.toLowerCase(upper)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        }
        char[] chars = this.items;
        int result = 31 + this.size;
        for (int index = 0; index < this.size; ++index) {
            result = 31 * result + chars[index];
        }
        return result;
    }

    public static CharArray wrap(char[] initialBuffer) {
        return new CharArray(initialBuffer, initialBuffer.length);
    }

    public static CharArray wrap(char[] initialBuffer, int length) {
        return new CharArray(initialBuffer, length);
    }

    public static CharArray with(char ... array) {
        return new CharArray(array);
    }

    public static int numChars(int value, int radix) {
        int result;
        int n = result = value < 0 ? 2 : 1;
        while ((value /= radix) != 0) {
            ++result;
        }
        return result;
    }

    public static int numChars(long value, int radix) {
        int result;
        int n = result = value < 0L ? 2 : 1;
        while ((value /= (long)radix) != 0L) {
            ++result;
        }
        return result;
    }

    class CharArrayReader
    extends Reader {
        private int mark;
        private int pos;

        CharArrayReader() {
        }

        @Override
        public void close() {
        }

        @Override
        public void mark(int readAheadLimit) {
            this.mark = this.pos;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public int read() {
            if (!this.ready()) {
                return -1;
            }
            return CharArray.this.charAt(this.pos++);
        }

        @Override
        public int read(char[] b, int off, int length) {
            if (off < 0 || length < 0 || off > b.length || off + length > b.length || off + length < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (length == 0) {
                return 0;
            }
            if (this.pos >= CharArray.this.size) {
                return -1;
            }
            if (this.pos + length > CharArray.this.size) {
                length = CharArray.this.size - this.pos;
            }
            CharArray.this.getChars(this.pos, this.pos + length, b, off);
            this.pos += length;
            return length;
        }

        @Override
        public boolean ready() {
            return this.pos < CharArray.this.size;
        }

        @Override
        public void reset() {
            this.pos = this.mark;
        }

        @Override
        public long skip(long n) {
            if ((long)this.pos + n > (long)CharArray.this.size) {
                n = CharArray.this.size - this.pos;
            }
            if (n < 0L) {
                return 0L;
            }
            this.pos += (int)n;
            return n;
        }
    }

    class CharArrayWriter
    extends Writer {
        CharArrayWriter() {
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void write(char[] cbuf) {
            CharArray.this.append(cbuf);
        }

        @Override
        public void write(char[] cbuf, int off, int length) {
            CharArray.this.append(cbuf, off, length);
        }

        @Override
        public void write(int c) {
            CharArray.this.append((char)c);
        }

        @Override
        public void write(String str) {
            CharArray.this.append(str);
        }

        @Override
        public void write(String str, int off, int length) {
            CharArray.this.append(str, off, length);
        }
    }
}

