/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.watabou.utils.Random;
import java.util.Locale;

public class DungeonSeed {
    public static long TOTAL_SEEDS = 5429503678976L;

    public static long randomSeed() {
        Long seed;
        String seedText;
        while ((seedText = DungeonSeed.convertToCode(seed = Long.valueOf(Random.Long(TOTAL_SEEDS)))).contains("A") || seedText.contains("E") || seedText.contains("I") || seedText.contains("O") || seedText.contains("U")) {
        }
        return seed;
    }

    public static long convertFromCode(String code) {
        if (code.length() == 11 && code.charAt(3) == '-' && code.charAt(7) == '-') {
            code = code.toUpperCase(Locale.ROOT);
        }
        if ((code = code.replaceAll("[-\\s]", "")).length() != 9) {
            throw new IllegalArgumentException("codes must be 9 A-Z characters.");
        }
        long result = 0L;
        for (int i = 8; i >= 0; --i) {
            char c = code.charAt(i);
            if (c > 'Z' || c < 'A') {
                throw new IllegalArgumentException("codes must be 9 A-Z characters.");
            }
            result = (long)((double)result + (double)(c - 65) * Math.pow(26.0, 8 - i));
        }
        return result;
    }

    public static String convertToCode(long seed) {
        if (seed < 0L || seed >= TOTAL_SEEDS) {
            throw new IllegalArgumentException("seeds must be within the range [0, TOTAL_SEEDS)");
        }
        String interrim = Long.toString(seed, 26);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 9; ++i) {
            if (i < interrim.length()) {
                char c = interrim.charAt(i);
                c = c <= '9' ? (char)(c + 17) : (char)(c - 22);
                result.append(c);
                continue;
            }
            result.insert(0, 'A');
        }
        result.insert(3, '-');
        result.insert(7, '-');
        return result.toString();
    }

    public static long convertFromText(String inputText) {
        if (inputText.isEmpty()) {
            return -1L;
        }
        try {
            return DungeonSeed.convertFromCode(inputText);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            try {
                return Long.parseLong(inputText.replaceAll("\\s", "")) % TOTAL_SEEDS;
            }
            catch (NumberFormatException numberFormatException) {
                long total = 0L;
                for (char c : inputText.toCharArray()) {
                    total = 31L * total + (long)c;
                }
                if (total < 0L) {
                    total += Long.MAX_VALUE;
                }
                return total %= TOTAL_SEEDS;
            }
        }
    }

    public static String formatText(String inputText) {
        try {
            return DungeonSeed.convertToCode(DungeonSeed.convertFromCode(inputText));
        }
        catch (IllegalArgumentException e) {
            return inputText;
        }
    }
}

