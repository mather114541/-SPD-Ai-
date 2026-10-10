/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;

public class VaultGolem
extends Golem {
    public VaultGolem() {
        this.activateSteathGameplayBehaviour();
        this.maxLvl = 30;
        this.EXP = 0;
        this.loot = DwarfToken.class;
        this.lootChance = 1.0f;
    }

    @Override
    public float lootChance() {
        return 1.0f;
    }

    @Override
    public Item createLoot() {
        return new DwarfToken();
    }
}

