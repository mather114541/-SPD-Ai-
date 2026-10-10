/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class VaultRat
extends Rat {
    public VaultRat() {
        this.activateSteathGameplayBehaviour();
        this.defenseSkill = 18;
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
    public String description() {
        return Messages.get(Rat.class, "desc", new Object[0]) + "\n\n" + super.description();
    }
}

