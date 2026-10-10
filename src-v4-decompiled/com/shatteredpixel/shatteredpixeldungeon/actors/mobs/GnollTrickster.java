/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GnollTrickster
extends Gnoll {
    private int combo;
    private static final String COMBO = "combo";

    public GnollTrickster() {
        this.spriteClass = GnollTricksterSprite.class;
        this.HT = 20;
        this.HP = 20;
        this.defenseSkill = 5;
        this.EXP = 5;
        this.state = this.WANDERING = new Wandering();
        this.loot = Generator.Category.MISSILE;
        this.lootChance = 1.0f;
        this.properties.add(Char.Property.MINIBOSS);
        this.combo = 0;
    }

    @Override
    public int attackSkill(Char target) {
        return 16;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return !Dungeon.level.adjacent(this.pos, enemy.pos) && (super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)7).collisionPos == enemy.pos);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (this.combo >= 1) {
            Statistics.questScores[0] = Statistics.questScores[0] - 50;
        }
        ++this.combo;
        int effect = Random.Int(4) + this.combo;
        if (effect > 2) {
            if (effect >= 6 && enemy.buff(Burning.class) == null) {
                if (Dungeon.level.flamable[enemy.pos]) {
                    GameScene.add(Blob.seed(enemy.pos, 4, Fire.class));
                }
                Buff.affect(enemy, Burning.class).reignite(enemy);
            } else {
                Buff.affect(enemy, Poison.class).set(effect - 2);
            }
        }
        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        this.combo = 0;
        if (this.state == this.HUNTING) {
            return this.enemySeen && this.getFurther(target);
        }
        return super.getCloser(target);
    }

    @Override
    public void aggro(Char ch) {
        if (ch == null || this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length() || this.fieldOfView[ch.pos]) {
            super.aggro(ch);
        }
    }

    @Override
    public Item createLoot() {
        MissileWeapon drop = (MissileWeapon)super.createLoot();
        drop.level(0);
        if (drop.hasCurseEnchant()) {
            drop.enchant(null);
        }
        drop.cursed = false;
        drop.identify(false);
        drop.quantity((drop.quantity() + 1) / 2);
        return drop;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Ghost.Quest.process();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COMBO, this.combo);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.combo = bundle.getInt(COMBO);
    }

    protected class Wandering
    extends Mob.Wandering {
        protected Wandering() {
            super(GnollTrickster.this);
        }

        @Override
        protected int randomDestination() {
            int pos1 = super.randomDestination();
            int pos2 = super.randomDestination();
            PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
            if (PathFinder.distance[pos2] < PathFinder.distance[pos1]) {
                return pos2;
            }
            return pos1;
        }
    }
}

