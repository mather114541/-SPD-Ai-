/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM200Sprite;

public class VaultDM200
extends DM200 {
    public VaultDM200() {
        this.activateSteathGameplayBehaviour();
        this.spriteClass = DM200Sprite.Vault.class;
        this.defenseSkill = 15;
        this.maxLvl = 30;
        this.EXP = 0;
        this.loot = DwarfToken.class;
        this.lootChance = 1.0f;
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
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

