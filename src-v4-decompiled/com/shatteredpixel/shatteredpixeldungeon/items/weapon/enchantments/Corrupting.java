/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Multiplicity;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Corrupting
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x440066);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 5.0f) / ((float)level + 25.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance && attacker.alignment == Char.Alignment.ALLY && !defender.isImmune(Corruption.class) && defender.buff(Corruption.class) == null && defender instanceof Mob && defender.isAlive()) {
            Buff.affect((Char)defender, CorruptingTracker.class).powerMulti = Math.max(1.0f, procChance);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    public static class CorruptingTracker
    extends Buff {
        float powerMulti;

        public CorruptingTracker() {
            this.actPriority = 100;
            this.powerMulti = 1.0f;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }

        @Override
        public void detach() {
            Mob corrupted;
            if (!this.target.isAlive() && (corrupted = Multiplicity.duplicate((Mob)this.target)) != null) {
                this.target.sprite.killAndErase();
                corrupted.timeToNow();
                corrupted.pos = this.target.pos;
                GameScene.add(corrupted);
                Corruption.corruptionHeal(corrupted);
                Buff.affect(corrupted, Corruption.class);
                if (this.powerMulti > 1.1f) {
                    Buff.affect(corrupted, Adrenaline.class, Math.round(5.0f * (this.powerMulti - 1.0f)));
                }
            }
            super.detach();
        }
    }
}

