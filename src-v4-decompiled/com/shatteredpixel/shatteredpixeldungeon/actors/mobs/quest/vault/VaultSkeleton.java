/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;

public class VaultSkeleton
extends Skeleton {
    public VaultSkeleton() {
        this.activateSteathGameplayBehaviour();
        this.spriteClass = SkeletonSprite.Vault.class;
        this.defenseSkill = 20;
        this.maxLvl = 30;
        this.EXP = 0;
        this.loot = DwarfToken.class;
        this.lootChance = 1.0f;
    }

    @Override
    public int attackSkill(Char target) {
        return 24;
    }

    @Override
    public float lootChance() {
        return 1.0f;
    }

    @Override
    public Item createLoot() {
        return new DwarfToken();
    }

    @Override
    public int drRoll() {
        return super.drRoll() + 4;
    }
}

