/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.utils;

import java.util.GregorianCalendar;

public enum Holiday {
    NONE,
    LUNAR_NEW_YEAR,
    APRIL_FOOLS,
    EASTER,
    PRIDE,
    SHATTEREDPD_BIRTHDAY,
    HALLOWEEN,
    PD_BIRTHDAY,
    WINTER_HOLIDAYS,
    NEW_YEARS;

    private static Holiday cached;

    public static void clearCachedHoliday() {
        cached = null;
    }

    public static Holiday getCurrentHoliday() {
        if (cached == null) {
            cached = Holiday.getHolidayForDate((GregorianCalendar)GregorianCalendar.getInstance());
        }
        return cached;
    }

    public static Holiday getHolidayForDate(GregorianCalendar cal) {
        if (Holiday.isLunarNewYear(cal.get(1), cal.get(6))) {
            return LUNAR_NEW_YEAR;
        }
        if (cal.get(2) == 3 && cal.get(5) == 1) {
            return APRIL_FOOLS;
        }
        if (Holiday.isEaster(cal.get(1), cal.get(6), cal.getActualMaximum(6) == 366)) {
            return EASTER;
        }
        if (cal.get(2) == 5 && cal.get(5) >= 24) {
            return PRIDE;
        }
        if (cal.get(2) == 7 && cal.get(5) <= 7) {
            return SHATTEREDPD_BIRTHDAY;
        }
        if (cal.get(2) == 9 && cal.get(5) >= 24) {
            return HALLOWEEN;
        }
        if (cal.get(2) == 11 && cal.get(5) <= 7) {
            return PD_BIRTHDAY;
        }
        if (cal.get(2) == 11 && cal.get(5) >= 15 && cal.get(5) <= 26) {
            return WINTER_HOLIDAYS;
        }
        if (cal.get(2) == 11 && cal.get(5) >= 27 || cal.get(2) == 0 && cal.get(5) <= 2) {
            return NEW_YEARS;
        }
        return NONE;
    }

    public static boolean isLunarNewYear(int year, int dayOfYear) {
        int lunarNewYearDayOfYear;
        switch (year) {
            default: {
                lunarNewYearDayOfYear = 36;
                break;
            }
            case 2020: {
                lunarNewYearDayOfYear = 25;
                break;
            }
            case 2021: {
                lunarNewYearDayOfYear = 43;
                break;
            }
            case 2022: {
                lunarNewYearDayOfYear = 32;
                break;
            }
            case 2023: {
                lunarNewYearDayOfYear = 22;
                break;
            }
            case 2024: {
                lunarNewYearDayOfYear = 41;
                break;
            }
            case 2025: {
                lunarNewYearDayOfYear = 29;
                break;
            }
            case 2026: {
                lunarNewYearDayOfYear = 48;
                break;
            }
            case 2027: {
                lunarNewYearDayOfYear = 37;
                break;
            }
            case 2028: {
                lunarNewYearDayOfYear = 26;
                break;
            }
            case 2029: {
                lunarNewYearDayOfYear = 44;
                break;
            }
            case 2030: {
                lunarNewYearDayOfYear = 34;
                break;
            }
            case 2031: {
                lunarNewYearDayOfYear = 23;
                break;
            }
            case 2032: {
                lunarNewYearDayOfYear = 42;
                break;
            }
            case 2033: {
                lunarNewYearDayOfYear = 31;
                break;
            }
            case 2034: {
                lunarNewYearDayOfYear = 50;
                break;
            }
            case 2035: {
                lunarNewYearDayOfYear = 39;
                break;
            }
            case 2036: {
                lunarNewYearDayOfYear = 28;
                break;
            }
            case 2037: {
                lunarNewYearDayOfYear = 46;
                break;
            }
            case 2038: {
                lunarNewYearDayOfYear = 35;
                break;
            }
            case 2039: {
                lunarNewYearDayOfYear = 24;
                break;
            }
            case 2040: {
                lunarNewYearDayOfYear = 43;
                break;
            }
            case 2041: {
                lunarNewYearDayOfYear = 32;
                break;
            }
            case 2042: {
                lunarNewYearDayOfYear = 22;
                break;
            }
            case 2043: {
                lunarNewYearDayOfYear = 41;
                break;
            }
            case 2044: {
                lunarNewYearDayOfYear = 30;
                break;
            }
            case 2045: {
                lunarNewYearDayOfYear = 48;
                break;
            }
            case 2046: {
                lunarNewYearDayOfYear = 37;
                break;
            }
            case 2047: {
                lunarNewYearDayOfYear = 26;
                break;
            }
            case 2048: {
                lunarNewYearDayOfYear = 45;
                break;
            }
            case 2049: {
                lunarNewYearDayOfYear = 33;
                break;
            }
            case 2050: {
                lunarNewYearDayOfYear = 23;
                break;
            }
            case 2051: {
                lunarNewYearDayOfYear = 42;
                break;
            }
            case 2052: {
                lunarNewYearDayOfYear = 32;
                break;
            }
            case 2053: {
                lunarNewYearDayOfYear = 50;
                break;
            }
            case 2054: {
                lunarNewYearDayOfYear = 39;
                break;
            }
            case 2055: {
                lunarNewYearDayOfYear = 28;
                break;
            }
            case 2056: {
                lunarNewYearDayOfYear = 46;
                break;
            }
            case 2057: {
                lunarNewYearDayOfYear = 35;
                break;
            }
            case 2058: {
                lunarNewYearDayOfYear = 24;
                break;
            }
            case 2059: {
                lunarNewYearDayOfYear = 43;
                break;
            }
            case 2060: {
                lunarNewYearDayOfYear = 33;
                break;
            }
            case 2061: {
                lunarNewYearDayOfYear = 21;
                break;
            }
            case 2062: {
                lunarNewYearDayOfYear = 40;
                break;
            }
            case 2063: {
                lunarNewYearDayOfYear = 29;
                break;
            }
            case 2064: {
                lunarNewYearDayOfYear = 48;
                break;
            }
            case 2065: {
                lunarNewYearDayOfYear = 36;
                break;
            }
            case 2066: {
                lunarNewYearDayOfYear = 26;
                break;
            }
            case 2067: {
                lunarNewYearDayOfYear = 45;
                break;
            }
            case 2068: {
                lunarNewYearDayOfYear = 34;
                break;
            }
            case 2069: {
                lunarNewYearDayOfYear = 23;
                break;
            }
            case 2070: {
                lunarNewYearDayOfYear = 42;
                break;
            }
            case 2071: {
                lunarNewYearDayOfYear = 31;
                break;
            }
            case 2072: {
                lunarNewYearDayOfYear = 50;
                break;
            }
            case 2073: {
                lunarNewYearDayOfYear = 38;
                break;
            }
            case 2074: {
                lunarNewYearDayOfYear = 27;
                break;
            }
            case 2075: {
                lunarNewYearDayOfYear = 46;
                break;
            }
            case 2076: {
                lunarNewYearDayOfYear = 36;
                break;
            }
            case 2077: {
                lunarNewYearDayOfYear = 24;
                break;
            }
            case 2078: {
                lunarNewYearDayOfYear = 43;
                break;
            }
            case 2079: {
                lunarNewYearDayOfYear = 33;
                break;
            }
            case 2080: {
                lunarNewYearDayOfYear = 22;
                break;
            }
            case 2081: {
                lunarNewYearDayOfYear = 40;
                break;
            }
            case 2082: {
                lunarNewYearDayOfYear = 29;
                break;
            }
            case 2083: {
                lunarNewYearDayOfYear = 48;
                break;
            }
            case 2084: {
                lunarNewYearDayOfYear = 37;
                break;
            }
            case 2085: {
                lunarNewYearDayOfYear = 26;
                break;
            }
            case 2086: {
                lunarNewYearDayOfYear = 45;
                break;
            }
            case 2087: {
                lunarNewYearDayOfYear = 34;
                break;
            }
            case 2088: {
                lunarNewYearDayOfYear = 24;
                break;
            }
            case 2089: {
                lunarNewYearDayOfYear = 41;
                break;
            }
            case 2090: {
                lunarNewYearDayOfYear = 30;
                break;
            }
            case 2091: {
                lunarNewYearDayOfYear = 49;
                break;
            }
            case 2092: {
                lunarNewYearDayOfYear = 38;
                break;
            }
            case 2093: {
                lunarNewYearDayOfYear = 27;
                break;
            }
            case 2094: {
                lunarNewYearDayOfYear = 46;
                break;
            }
            case 2095: {
                lunarNewYearDayOfYear = 36;
                break;
            }
            case 2096: {
                lunarNewYearDayOfYear = 25;
                break;
            }
            case 2097: {
                lunarNewYearDayOfYear = 43;
                break;
            }
            case 2098: {
                lunarNewYearDayOfYear = 32;
                break;
            }
            case 2099: {
                lunarNewYearDayOfYear = 21;
                break;
            }
            case 2100: {
                lunarNewYearDayOfYear = 40;
            }
        }
        return dayOfYear >= lunarNewYearDayOfYear - 4 && dayOfYear <= lunarNewYearDayOfYear + 2;
    }

    public static boolean isEaster(int year, int dayOfYear, boolean isLeapYear) {
        if (dayOfYear < 59 || dayOfYear > 121) {
            return false;
        }
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (a * 19 + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + h * 11 + l * 22) / 451;
        int n = (h + l - m * 7 + 114) / 31;
        int o = (h + l - m * 7 + 114) % 31;
        int easterDayOfYear = 0;
        easterDayOfYear = n == 3 ? (easterDayOfYear += 59) : (easterDayOfYear += 90);
        if (isLeapYear) {
            ++easterDayOfYear;
        }
        return dayOfYear >= (easterDayOfYear += o + 1) - 4 && dayOfYear <= easterDayOfYear + 2;
    }
}

