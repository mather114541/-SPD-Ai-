/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GreatCrab
extends Crab {
    private int moving;

    public GreatCrab() {
        this.spriteClass = GreatCrabSprite.class;
        this.HT = 25;
        this.HP = 25;
        this.defenseSkill = 0;
        this.baseSpeed = 1.0f;
        this.EXP = 6;
        this.state = this.WANDERING = new Wandering();
        this.loot = new MysteryMeat().quantity(2);
        this.lootChance = 1.0f;
        this.properties.add(Char.Property.MINIBOSS);
        this.moving = 0;
    }

    @Override
    protected boolean getCloser(int target) {
        ++this.moving;
        if (this.moving < 3) {
            return super.getCloser(target);
        }
        this.moving = 0;
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (this.enemySeen && this.state != this.SLEEPING && this.paralysed == 0 && (src instanceof Wand || src instanceof ClericSpell) && this.enemy == Dungeon.hero && this.enemy.invisible == 0) {
            GLog.n(Messages.get(this, "noticed", new Object[0]), new Object[0]);
            this.sprite.showStatus(0xFFFF00, Messages.get(this, "def_verb", new Object[0]), new Object[0]);
            Sample.INSTANCE.play("sounds/hit_parry.mp3", 1.0f, Random.Float(0.96f, 1.05f));
            Statistics.questScores[0] = Statistics.questScores[0] - 50;
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.enemySeen && this.state != this.SLEEPING && this.paralysed == 0 && enemy == this.enemy && enemy.invisible == 0) {
            if (this.sprite != null && this.sprite.visible) {
                Sample.INSTANCE.play("sounds/hit_parry.mp3", 1.0f, Random.Float(0.96f, 1.05f));
                GLog.n(Messages.get(this, "noticed", new Object[0]), new Object[0]);
            }
            if (enemy == Dungeon.hero) {
                Statistics.questScores[0] = Statistics.questScores[0] - 50;
            }
            return INFINITE_EVASION;
        }
        return super.defenseSkill(enemy);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Ghost.Quest.process();
    }

    protected class Wandering
    extends Mob.Wandering {
        protected Wandering() {
            super(GreatCrab.this);
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

