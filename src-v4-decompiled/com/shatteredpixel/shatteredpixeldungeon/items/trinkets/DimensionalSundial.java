/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DimensionalSundial
extends Trinket {
    public static boolean sundialWarned = false;

    public DimensionalSundial() {
        this.image = ItemSpriteSheet.SUNDIAL;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", (int)(100.0f * (1.0f - DimensionalSundial.enemySpawnMultiplierDaytime(this.buffedLvl()))), (int)(100.0f * (DimensionalSundial.enemySpawnMultiplierNighttime(this.buffedLvl()) - 1.0f)));
        }
        return Messages.get(this, "typical_stats_desc", (int)(100.0f * (1.0f - DimensionalSundial.enemySpawnMultiplierDaytime(0))), (int)(100.0f * (DimensionalSundial.enemySpawnMultiplierNighttime(0) - 1.0f)));
    }

    public static float spawnMultiplierAtCurrentTime() {
        if (DimensionalSundial.trinketLevel(DimensionalSundial.class) != -1) {
            Calendar cal = GregorianCalendar.getInstance();
            if (cal.get(11) >= 20 || cal.get(11) <= 7) {
                if (!sundialWarned) {
                    GLog.w(Messages.get(DimensionalSundial.class, "warning", new Object[0]), new Object[0]);
                    sundialWarned = true;
                }
                return DimensionalSundial.enemySpawnMultiplierNighttime();
            }
            return DimensionalSundial.enemySpawnMultiplierDaytime();
        }
        return 1.0f;
    }

    public static float enemySpawnMultiplierDaytime() {
        return DimensionalSundial.enemySpawnMultiplierDaytime(DimensionalSundial.trinketLevel(DimensionalSundial.class));
    }

    public static float enemySpawnMultiplierDaytime(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 0.95f - 0.05f * (float)level;
    }

    public static float enemySpawnMultiplierNighttime() {
        return DimensionalSundial.enemySpawnMultiplierNighttime(DimensionalSundial.trinketLevel(DimensionalSundial.class));
    }

    public static float enemySpawnMultiplierNighttime(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 1.25f + 0.25f * (float)level;
    }
}

