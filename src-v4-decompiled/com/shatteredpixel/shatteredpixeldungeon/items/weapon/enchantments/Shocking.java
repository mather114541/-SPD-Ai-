/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Shocking
extends Weapon.Enchantment {
    private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF, 0.5f);
    private ArrayList<Char> affected = new ArrayList();
    private ArrayList<Lightning.Arc> arcs = new ArrayList();

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = 0.33333334f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            this.affected.clear();
            this.arcs.clear();
            Shocking.arc(attacker, defender, 2, this.affected, this.arcs);
            this.affected.remove(defender);
            for (Char ch : this.affected) {
                if (ch.alignment == attacker.alignment) continue;
                ch.damage(Math.round((float)damage * 0.5f * powerMulti), this);
            }
            attacker.sprite.parent.addToFront(new Lightning(this.arcs, null));
            Sample.INSTANCE.play("sounds/lightning.mp3");
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }

    public static void arc(Char attacker, Char defender, int dist, ArrayList<Char> affected, ArrayList<Lightning.Arc> arcs) {
        defender.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
        defender.sprite.flash();
        ArrayList<Char> hitThisArc = new ArrayList<Char>();
        PathFinder.buildDistanceMap(defender.pos, BArray.not(Dungeon.level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            Char n;
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || (n = Actor.findChar(i)) == null || n == attacker || affected.contains(n)) continue;
            hitThisArc.add(n);
        }
        affected.addAll(hitThisArc);
        for (Char hit : hitThisArc) {
            arcs.add(new Lightning.Arc(defender.sprite.center(), hit.sprite.center()));
            Shocking.arc(attacker, hit, Dungeon.level.water[hit.pos] && !hit.flying ? 2 : 1, affected, arcs);
        }
    }
}

