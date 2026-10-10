/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GuardSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Guard
extends Mob {
    private boolean chainsUsed = false;
    private final String CHAINSUSED = "chainsused";

    public Guard() {
        this.spriteClass = GuardSprite.class;
        this.HT = 40;
        this.HP = 40;
        this.defenseSkill = 10;
        this.EXP = 7;
        this.maxLvl = 14;
        this.loot = Generator.Category.ARMOR;
        this.lootChance = 0.2f;
        this.properties.add(Char.Property.UNDEAD);
        this.HUNTING = new Hunting();
        this.CHAINSUSED = "chainsused";
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(4, 12);
    }

    private boolean chain(int target) {
        if (this.chainsUsed || this.enemy.properties().contains((Object)Char.Property.IMMOVABLE)) {
            return false;
        }
        Ballistica chain = new Ballistica(this.pos, target, 7);
        if (chain.collisionPos != this.enemy.pos || chain.path.size() < 2 || Dungeon.level.pit[chain.path.get(1)]) {
            return false;
        }
        int newPos = -1;
        for (int i : chain.subPath(1, chain.dist)) {
            if (Dungeon.level.solid[i] || Actor.findChar(i) != null || !Dungeon.level.openSpace[i] && Char.hasProp(this.enemy, Char.Property.LARGE)) continue;
            newPos = i;
            break;
        }
        if (newPos == -1) {
            return false;
        }
        final int newPosFinal = newPos;
        this.target = newPos;
        if (this.sprite.visible || this.enemy.sprite.visible) {
            this.yell(Messages.get(this, "scorpion", new Object[0]));
            new Item().throwSound();
            Sample.INSTANCE.play("sounds/chains.mp3");
            this.sprite.parent.add(new Chains(this.sprite.center(), this.enemy.sprite.destinationCenter(), Effects.Type.CHAIN, new Callback(){

                @Override
                public void call() {
                    Actor.add(new Pushing(Guard.this.enemy, Guard.this.enemy.pos, newPosFinal, new Callback(){

                        @Override
                        public void call() {
                            Guard.this.pullEnemy(Guard.this.enemy, newPosFinal);
                        }
                    }));
                    Guard.this.next();
                }
            }));
        } else {
            this.pullEnemy(this.enemy, newPos);
        }
        this.chainsUsed = true;
        return true;
    }

    private void pullEnemy(Char enemy, int pullPos) {
        enemy.pos = pullPos;
        enemy.sprite.place(pullPos);
        Dungeon.level.occupyCell(enemy);
        Cripple.prolong(enemy, Cripple.class, 4.0f);
        if (enemy == Dungeon.hero) {
            Dungeon.hero.interrupt();
            Dungeon.observe();
            GameScene.updateFog();
        } else {
            enemy.sprite.visible = Dungeon.level.heroFOV[pullPos];
        }
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 7);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.3333333432674408, Dungeon.LimitedDrops.GUARD_ARM.count);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.GUARD_ARM.count;
        return super.createLoot();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("chainsused", this.chainsUsed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.chainsUsed = bundle.getBoolean("chainsused");
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
            super(Guard.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Guard.this.enemySeen = enemyInFOV;
            if (!Guard.this.chainsUsed && enemyInFOV && !Guard.this.isCharmedBy(Guard.this.enemy) && !Guard.this.canAttack(Guard.this.enemy) && Dungeon.level.distance(Guard.this.pos, Guard.this.enemy.pos) < 5 && Guard.this.chain(Guard.this.enemy.pos)) {
                return !Guard.this.sprite.visible && !Guard.this.enemy.sprite.visible;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}

