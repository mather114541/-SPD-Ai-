/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Necromancer
extends Mob {
    public boolean summoning;
    public int summoningPos;
    protected boolean firstSummon;
    private NecroSkeleton mySkeleton;
    private int storedSkeletonID;
    private static final String SUMMONING = "summoning";
    private static final String FIRST_SUMMON = "first_summon";
    private static final String SUMMONING_POS = "summoning_pos";
    private static final String MY_SKELETON = "my_skeleton";

    public Necromancer() {
        this.spriteClass = NecromancerSprite.class;
        this.HT = 40;
        this.HP = 40;
        this.defenseSkill = 14;
        this.EXP = 7;
        this.maxLvl = 14;
        this.loot = PotionOfHealing.class;
        this.lootChance = 0.2f;
        this.properties.add(Char.Property.UNDEAD);
        this.HUNTING = new Hunting();
        this.summoning = false;
        this.summoningPos = -1;
        this.firstSummon = true;
        this.storedSkeletonID = -1;
    }

    @Override
    protected boolean act() {
        if (this.summoning && this.state != this.HUNTING) {
            this.summoning = false;
            if (this.sprite instanceof NecromancerSprite) {
                ((NecromancerSprite)this.sprite).cancelSummoning();
            }
        }
        return super.act();
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        if (this.mySkeleton != null && this.mySkeleton.isAlive() && Dungeon.level.mobs.contains(this.mySkeleton) && this.mySkeleton.alignment == this.alignment) {
            this.mySkeleton.aggro(ch);
        }
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * ((6.0f - (float)Dungeon.LimitedDrops.NECRO_HP.count) / 6.0f);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.NECRO_HP.count;
        return super.createLoot();
    }

    @Override
    public void die(Object cause) {
        if (this.storedSkeletonID != -1) {
            Actor ch = Actor.findById(this.storedSkeletonID);
            this.storedSkeletonID = -1;
            if (ch instanceof NecroSkeleton) {
                this.mySkeleton = (NecroSkeleton)ch;
            }
        }
        if (this.mySkeleton != null && this.mySkeleton.isAlive() && this.mySkeleton.alignment == this.alignment) {
            this.mySkeleton.die(null);
        }
        super.die(cause);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SUMMONING, this.summoning);
        bundle.put(FIRST_SUMMON, this.firstSummon);
        if (this.summoning) {
            bundle.put(SUMMONING_POS, this.summoningPos);
        }
        if (this.mySkeleton != null) {
            bundle.put(MY_SKELETON, this.mySkeleton.id());
        } else if (this.storedSkeletonID != -1) {
            bundle.put(MY_SKELETON, this.storedSkeletonID);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.summoning = bundle.getBoolean(SUMMONING);
        if (bundle.contains(FIRST_SUMMON)) {
            this.firstSummon = bundle.getBoolean(FIRST_SUMMON);
        }
        if (this.summoning) {
            this.summoningPos = bundle.getInt(SUMMONING_POS);
        }
        if (bundle.contains(MY_SKELETON)) {
            this.storedSkeletonID = bundle.getInt(MY_SKELETON);
        }
    }

    public void onZapComplete() {
        if (this.mySkeleton == null || this.mySkeleton.sprite == null || !this.mySkeleton.isAlive()) {
            return;
        }
        if (this.mySkeleton.HP < this.mySkeleton.HT) {
            if (this.sprite.visible || this.mySkeleton.sprite.visible) {
                this.sprite.parent.add(new Beam.HealthRay(this.sprite.center(), this.mySkeleton.sprite.center()));
                Sample.INSTANCE.play("sounds/ray.mp3");
            }
            this.mySkeleton.HP = Math.min(this.mySkeleton.HP + this.mySkeleton.HT / 5, this.mySkeleton.HT);
            if (this.mySkeleton.sprite.visible) {
                this.mySkeleton.sprite.showStatusWithIcon(65280, Integer.toString(this.mySkeleton.HT / 5), FloatingText.HEALING, new Object[0]);
            }
        } else if (this.mySkeleton.buff(Adrenaline.class) == null) {
            if (this.sprite.visible || this.mySkeleton.sprite.visible) {
                this.sprite.parent.add(new Beam.HealthRay(this.sprite.center(), this.mySkeleton.sprite.center()));
                Sample.INSTANCE.play("sounds/ray.mp3");
            }
            Buff.affect(this.mySkeleton, Adrenaline.class, 3.0f);
        }
        this.next();
    }

    public void summonMinion() {
        Char existing = Actor.findChar(this.summoningPos);
        if (existing != null && existing != this.mySkeleton || !Dungeon.level.passable[this.summoningPos]) {
            int pushPos = this.pos;
            for (int c : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(this.summoningPos + c) != null || !Dungeon.level.passable[this.summoningPos + c] || !Dungeon.level.openSpace[this.summoningPos + c] && Necromancer.hasProp(Actor.findChar(this.summoningPos), Char.Property.LARGE) || !(Dungeon.level.trueDistance(this.pos, this.summoningPos + c) > Dungeon.level.trueDistance(this.pos, pushPos))) continue;
                pushPos = this.summoningPos + c;
            }
            if (pushPos != this.pos) {
                Char ch = Actor.findChar(this.summoningPos);
                if (ch == null || Char.hasProp(ch, Char.Property.IMMOVABLE)) {
                    this.summoningPos = pushPos;
                } else {
                    Actor.add(new Pushing(ch, ch.pos, pushPos));
                    ch.pos = pushPos;
                    Dungeon.level.occupyCell(ch);
                }
            } else {
                Char blocker = Actor.findChar(this.summoningPos);
                if (blocker != null && blocker.alignment != this.alignment) {
                    blocker.damage(Random.NormalIntRange(2, 10), new SummoningBlockDamage());
                    if (blocker == Dungeon.hero && !blocker.isAlive()) {
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail(this);
                        GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
                    }
                }
                this.spend(1.0f);
                return;
            }
        }
        this.firstSummon = false;
        this.summoning = false;
        if (this.mySkeleton == null || !this.mySkeleton.isActive()) {
            this.mySkeleton = new NecroSkeleton();
            this.mySkeleton.pos = this.summoningPos;
            GameScene.add(this.mySkeleton);
            Dungeon.level.occupyCell(this.mySkeleton);
            for (Buff b : this.buffs()) {
                if (!b.revivePersists) continue;
                Buff.affect(this.mySkeleton, b.getClass());
            }
        } else {
            ScrollOfTeleportation.appear(this.mySkeleton, this.summoningPos);
        }
        ((NecromancerSprite)this.sprite).finishSummoning();
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Necromancer.this.enemySeen = enemyInFOV;
            if (Necromancer.this.enemySeen) {
                Necromancer.this.target = Necromancer.this.enemy.pos;
            }
            if (Necromancer.this.storedSkeletonID != -1) {
                Actor ch = Actor.findById(Necromancer.this.storedSkeletonID);
                Necromancer.this.storedSkeletonID = -1;
                if (ch instanceof NecroSkeleton) {
                    Necromancer.this.mySkeleton = (NecroSkeleton)ch;
                }
            }
            if (Necromancer.this.summoning) {
                Necromancer.this.summonMinion();
                return true;
            }
            if (!(Necromancer.this.mySkeleton == null || Necromancer.this.mySkeleton.isAlive() && Dungeon.level.mobs.contains(Necromancer.this.mySkeleton) && Necromancer.this.mySkeleton.alignment == Necromancer.this.alignment)) {
                Necromancer.this.mySkeleton = null;
            }
            if (Necromancer.this.enemySeen && Dungeon.level.distance(Necromancer.this.pos, Necromancer.this.enemy.pos) <= 4 && Necromancer.this.mySkeleton == null) {
                Necromancer.this.summoningPos = -1;
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                BArray.or(Dungeon.level.passable, passable, passable);
                PathFinder.buildDistanceMap(Necromancer.this.pos, passable, Dungeon.level.distance(Necromancer.this.pos, Necromancer.this.enemy.pos) + 3);
                for (int c : PathFinder.NEIGHBOURS8) {
                    if (Actor.findChar(Necromancer.this.enemy.pos + c) != null || PathFinder.distance[Necromancer.this.enemy.pos + c] == Integer.MAX_VALUE || !Dungeon.level.passable[Necromancer.this.enemy.pos + c] || Char.hasProp(Necromancer.this, Char.Property.LARGE) && !Dungeon.level.openSpace[Necromancer.this.enemy.pos + c] || !Necromancer.this.fieldOfView[Necromancer.this.enemy.pos + c] || !(Dungeon.level.trueDistance(Necromancer.this.pos, Necromancer.this.enemy.pos + c) < Dungeon.level.trueDistance(Necromancer.this.pos, Necromancer.this.summoningPos))) continue;
                    Necromancer.this.summoningPos = Necromancer.this.enemy.pos + c;
                }
                if (Necromancer.this.summoningPos != -1) {
                    Necromancer.this.summoning = true;
                    Necromancer.this.sprite.zap(Necromancer.this.summoningPos);
                    if (Dungeon.level.heroFOV[Necromancer.this.pos] || Dungeon.level.heroFOV[Necromancer.this.summoningPos]) {
                        Dungeon.hero.interrupt();
                    }
                    Necromancer.this.spend(Necromancer.this.firstSummon ? 1.0f : 2.0f);
                } else {
                    Necromancer.this.spend(1.0f);
                }
                return true;
            }
            if (Necromancer.this.enemySeen && Necromancer.this.mySkeleton != null) {
                PathFinder.Path skelePath;
                Necromancer.this.spend(1.0f);
                boolean teleporting = false;
                if (!Necromancer.this.fieldOfView[Necromancer.this.mySkeleton.pos]) {
                    teleporting = true;
                } else if (!Necromancer.this.mySkeleton.canAttack(Necromancer.this.enemy) && ((skelePath = Dungeon.findPath(Necromancer.this.mySkeleton, Necromancer.this.enemy.pos, Dungeon.level.passable, Necromancer.this.fieldOfView, true)) == null || skelePath.size() > 2 * Dungeon.level.distance(Necromancer.this.pos, Necromancer.this.enemy.pos) && skelePath.size() >= 4)) {
                    teleporting = true;
                }
                if (teleporting) {
                    if (!Dungeon.level.adjacent(Necromancer.this.mySkeleton.pos, Necromancer.this.enemy.pos)) {
                        int telePos = -1;
                        for (int c : PathFinder.NEIGHBOURS8) {
                            if (Actor.findChar(Necromancer.this.enemy.pos + c) != null || !Dungeon.level.passable[Necromancer.this.enemy.pos + c] || !Necromancer.this.fieldOfView[Necromancer.this.enemy.pos + c] || !Dungeon.level.openSpace[Necromancer.this.enemy.pos + c] && Char.hasProp(Necromancer.this.mySkeleton, Char.Property.LARGE) || !(Dungeon.level.trueDistance(Necromancer.this.pos, Necromancer.this.enemy.pos + c) < Dungeon.level.trueDistance(Necromancer.this.pos, telePos))) continue;
                            telePos = Necromancer.this.enemy.pos + c;
                        }
                        if (telePos != -1 && Necromancer.this.sprite != null && Necromancer.this.sprite.visible) {
                            Necromancer.this.summoning = true;
                            Necromancer.this.summoningPos = telePos;
                            Necromancer.this.sprite.zap(telePos);
                            if (Dungeon.level.heroFOV[Necromancer.this.pos] || Dungeon.level.heroFOV[Necromancer.this.summoningPos]) {
                                Dungeon.hero.interrupt();
                            }
                            Necromancer.this.spend(1.0f);
                        }
                    }
                    return true;
                }
                if (Necromancer.this.mySkeleton.HP < Necromancer.this.mySkeleton.HT || Necromancer.this.mySkeleton.buff(Adrenaline.class) == null) {
                    if (Necromancer.this.sprite != null && Necromancer.this.sprite.visible) {
                        Necromancer.this.sprite.zap(Necromancer.this.mySkeleton.pos);
                        return false;
                    }
                    Necromancer.this.onZapComplete();
                }
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    public static class NecroSkeleton
    extends Skeleton {
        public NecroSkeleton() {
            this.state = this.WANDERING;
            this.spriteClass = NecroSkeletonSprite.class;
            this.maxLvl = -5;
            this.HP = 20;
        }

        @Override
        public float spawningWeight() {
            return 0.0f;
        }

        public static class NecroSkeletonSprite
        extends SkeletonSprite {
            public NecroSkeletonSprite() {
                this.brightness(0.75f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                this.brightness(0.75f);
            }
        }
    }

    public static class SummoningBlockDamage {
    }
}

