/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Succubus
extends Mob {
    private int blinkCooldown = 0;
    private static final String BLINK_CD = "blink_cd";

    public Succubus() {
        this.spriteClass = SuccubusSprite.class;
        this.HT = 80;
        this.HP = 80;
        this.defenseSkill = 25;
        this.viewDistance = 6;
        this.EXP = 12;
        this.maxLvl = 25;
        this.loot = Generator.Category.SCROLL;
        this.lootChance = 0.33f;
        this.properties.add(Char.Property.DEMONIC);
        this.immunities.add(Charm.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(25, 30);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (enemy.buff(Charm.class) != null) {
            int shield = this.HP - this.HT + (5 + damage);
            if (shield > 0) {
                this.HP = this.HT;
                if (shield < 5) {
                    this.sprite.showStatusWithIcon(65280, Integer.toString(5 - shield), FloatingText.HEALING, new Object[0]);
                }
                Buff.affect(this, Barrier.class).setShield(shield);
                this.sprite.showStatusWithIcon(65280, Integer.toString(shield), FloatingText.SHIELDING, new Object[0]);
            } else {
                this.HP += 5 + damage;
                this.sprite.showStatusWithIcon(65280, "5", FloatingText.HEALING, new Object[0]);
            }
            if (Dungeon.level.heroFOV[this.pos]) {
                Sample.INSTANCE.play("sounds/charms.mp3");
            }
        } else if (Random.Int(3) == 0) {
            Charm c = Buff.affect(enemy, Charm.class, 5.0f);
            c.object = this.id();
            c.ignoreNextHit = true;
            if (Dungeon.level.heroFOV[enemy.pos]) {
                enemy.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
                Sample.INSTANCE.play("sounds/charms.mp3");
            }
        }
        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (this.fieldOfView[target] && Dungeon.level.distance(this.pos, target) > 2 && this.blinkCooldown <= 0 && !this.rooted) {
            if (this.blink(target)) {
                this.spend(-1.0f / this.speed());
                return true;
            }
            return false;
        }
        --this.blinkCooldown;
        return super.getCloser(target);
    }

    private boolean blink(int target) {
        Ballistica route = new Ballistica(this.pos, target, 7);
        int cell = route.collisionPos;
        if (Actor.findChar(cell) != null && cell != this.pos) {
            cell = route.path.get(route.dist - 1);
        }
        if (Dungeon.level.avoid[cell] || this.properties().contains((Object)Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int n : PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos + n;
                if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null || this.properties().contains((Object)Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) continue;
                candidates.add(cell);
            }
            if (candidates.size() > 0) {
                cell = (Integer)Random.element(candidates);
            } else {
                this.blinkCooldown = Random.IntRange(4, 6);
                return false;
            }
        }
        ScrollOfTeleportation.appear(this, cell);
        this.blinkCooldown = Random.IntRange(4, 6);
        return true;
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    public Item createLoot() {
        Class<?> loot;
        while ((loot = Random.oneOf(Generator.Category.SCROLL.classes)) == ScrollOfIdentify.class || loot == ScrollOfUpgrade.class) {
        }
        return (Item)Reflection.newInstance(loot);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLINK_CD, this.blinkCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.blinkCooldown = bundle.getInt(BLINK_CD);
    }
}

