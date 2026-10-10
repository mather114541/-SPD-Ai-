/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Annoying
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.05f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                mob.beckon(attacker.pos);
            }
            attacker.sprite.centerEmitter().start(Speck.factory(5), 0.3f, 3);
            Sample.INSTANCE.play("sounds/mimic.mp3");
            Invisibility.dispel();
            if (Random.Int(33) != 0) {
                GLog.n(Messages.get(this, "msg_" + Random.IntRange(1, 10), new Object[0]), new Object[0]);
            } else {
                GLog.n(Messages.get(this, "msg_" + Random.IntRange(11, 13), new Object[0]), new Object[0]);
            }
        }
        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }
}

