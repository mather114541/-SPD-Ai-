/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;

public class VaultGhoul
extends Ghoul {
    public VaultGhoul() {
        this.activateSteathGameplayBehaviour();
        this.partnerID = -2;
        this.maxLvl = 30;
        this.EXP = 0;
        this.loot = DwarfToken.class;
        this.lootChance = 1.0f;
    }

    @Override
    protected boolean act() {
        if (this.state == this.WANDERING || this.state == this.SLEEPING) {
            for (Mob m : Dungeon.level.mobs) {
                if (!(m instanceof VaultGhoul) || Dungeon.level.distance(this.pos, m.pos) >= 4 || m.state != m.INVESTIGATING && m.state != m.HUNTING) continue;
                this.beckon(m.pos);
                break;
            }
        }
        return super.act();
    }
}

