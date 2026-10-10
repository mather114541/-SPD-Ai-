/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Dazzling
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.1f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            for (Char ch : Actor.chars()) {
                if (ch.fieldOfView == null || !ch.fieldOfView[defender.pos]) continue;
                Buff.prolong(ch, Blindness.class, ch == attacker ? 10.0f : 5.0f);
                if (ch != Dungeon.hero) continue;
                GameScene.flash(-2130706433);
            }
            if (Dungeon.level.heroFOV[attacker.pos] || Dungeon.level.heroFOV[defender.pos]) {
                Sample.INSTANCE.play("sounds/blast.mp3");
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

