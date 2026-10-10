/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Blazing
extends Weapon.Enchantment {
    private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing(0xFF4400);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 3.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            if (defender.buff(Burning.class) == null) {
                Buff.affect(defender, Burning.class).reignite(defender, 8.0f);
                powerMulti -= 1.0f;
            }
            if (powerMulti > 0.0f) {
                int burnDamage = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth() / 4);
                if ((burnDamage = Math.round((float)burnDamage * 0.67f * powerMulti)) > 0) {
                    defender.damage(burnDamage, this);
                }
            }
            defender.sprite.emitter().burst(FlameParticle.FACTORY, level + 1);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return ORANGE;
    }
}

