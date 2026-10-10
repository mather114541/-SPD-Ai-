/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.watabou.utils.Random;

public abstract class VaultElemental
extends Elemental {
    public static Class<? extends Elemental> random() {
        float roll = Random.Float();
        if (roll < 0.4f) {
            return Fire.class;
        }
        if (roll < 0.8f) {
            return Frost.class;
        }
        return Shock.class;
    }

    public static class Fire
    extends Elemental.FireElemental {
        public Fire() {
            this.activateSteathGameplayBehaviour();
            this.maxLvl = 30;
            this.EXP = 0;
            this.loot = DwarfToken.class;
            this.lootChance = 1.0f;
        }
    }

    public static class Frost
    extends Elemental.FrostElemental {
        public Frost() {
            this.activateSteathGameplayBehaviour();
            this.maxLvl = 30;
            this.EXP = 0;
            this.loot = DwarfToken.class;
            this.lootChance = 1.0f;
        }
    }

    public static class Shock
    extends Elemental.ShockElemental {
        public Shock() {
            this.activateSteathGameplayBehaviour();
            this.maxLvl = 30;
            this.EXP = 0;
            this.loot = DwarfToken.class;
            this.lootChance = 1.0f;
        }
    }
}

