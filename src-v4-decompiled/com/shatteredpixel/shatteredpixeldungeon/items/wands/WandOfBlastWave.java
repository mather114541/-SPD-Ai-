/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfBlastWave
extends DamageWand {
    public WandOfBlastWave() {
        this.image = ItemSpriteSheet.WAND_BLAST_WAVE;
        this.collisionProperties = 7;
    }

    @Override
    public int min(int lvl) {
        return 1 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 3 + 3 * lvl;
    }

    @Override
    public void onZap(Ballistica bolt) {
        Sample.INSTANCE.play("sounds/blast.mp3");
        BlastWave.blast(bolt.collisionPos);
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.traps.get(bolt.collisionPos + i) instanceof TenguDartTrap) continue;
            Dungeon.level.pressCell(bolt.collisionPos + i);
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            Char ch = Actor.findChar(bolt.collisionPos + i);
            if (ch == null) continue;
            this.wandProc(ch, this.chargesPerCast());
            if (ch.alignment != Char.Alignment.ALLY) {
                ch.damage(this.damageRoll(), this);
            }
            if (!ch.isAlive() && !ch.flying && Dungeon.level.pit[ch.pos] || ch.pos != bolt.collisionPos + i) continue;
            Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, 6);
            int strength = Math.round(1.5f + (float)this.buffedLvl() / 2.0f);
            WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
        }
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(this.damageRoll(), this);
            if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos]) && bolt.path.size() > bolt.dist + 1 && ch.pos == bolt.collisionPos) {
                Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), 6);
                int strength = this.buffedLvl() + 3;
                WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
            }
        }
    }

    public static void throwChar(final Char ch, Ballistica trajectory, int power, final boolean closeDoors, boolean collideDmg, final Object cause) {
        int dist;
        boolean collided;
        if (ch.properties().contains((Object)Char.Property.BOSS)) {
            power = (power + 1) / 2;
        }
        boolean bl = collided = (dist = Math.min(trajectory.dist, power)) == trajectory.dist;
        if (dist <= 0 || ch.rooted || ch.properties().contains((Object)Char.Property.IMMOVABLE)) {
            return;
        }
        if (Char.hasProp(ch, Char.Property.LARGE)) {
            for (int i = 1; i <= dist; ++i) {
                if (Dungeon.level.openSpace[trajectory.path.get(i)]) continue;
                dist = i - 1;
                collided = true;
                break;
            }
        }
        if (Actor.findChar(trajectory.path.get(dist)) != null) {
            --dist;
            collided = true;
        }
        if (dist < 0) {
            return;
        }
        final int newPos = trajectory.path.get(dist);
        if (newPos == ch.pos) {
            return;
        }
        final int finalDist = dist;
        final boolean finalCollided = collided && collideDmg;
        final int initialpos = ch.pos;
        Actor.add(new Pushing(ch, ch.pos, newPos, new Callback(){

            @Override
            public void call() {
                if (initialpos != ch.pos || Actor.findChar(newPos) != null) {
                    ch.sprite.place(ch.pos);
                    return;
                }
                int oldPos = ch.pos;
                ch.pos = newPos;
                if (finalCollided && ch.isActive()) {
                    ch.damage(Random.NormalIntRange(finalDist, 2 * finalDist), new Knockback());
                    if (ch.isActive()) {
                        Paralysis.prolong(ch, Paralysis.class, 1.0f + (float)finalDist / 2.0f);
                    } else if (ch == Dungeon.hero) {
                        if (cause instanceof Wand || cause instanceof Weapon.Enchantment) {
                            Badges.validateDeathFromFriendlyMagic();
                        }
                        GLog.n(Messages.get(WandOfBlastWave.class, "knockback_ondeath", new Object[0]), new Object[0]);
                        Dungeon.fail(cause);
                    }
                }
                if (closeDoors && Dungeon.level.map[oldPos] == 6) {
                    Door.leave(oldPos);
                }
                Dungeon.level.occupyCell(ch);
                if (ch == Dungeon.hero) {
                    Dungeon.observe();
                    GameScene.updateFog();
                } else if (Dungeon.level.heroFOV[initialpos] != Dungeon.level.heroFOV[newPos]) {
                    Dungeon.observe();
                }
            }
        }));
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        if (defender.buff(Paralysis.class) != null && defender.buff(BWaveOnHitTracker.class) == null) {
            defender.buff(Paralysis.class).detach();
            int dmg = Hero.heroDamageIntRange(8 + 2 * this.buffedLvl(), 12 + 3 * this.buffedLvl());
            defender.damage(Math.round(WandOfBlastWave.procChanceMultiplier(attacker) * (float)dmg), this);
            BlastWave.blast(defender.pos);
            Sample.INSTANCE.play("sounds/blast.mp3");
            Buff.prolong(defender, BWaveOnHitTracker.class, 3.0f);
        }
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(3 + level);
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(WandOfBlastWave.curUser.sprite.parent, 5, WandOfBlastWave.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x664422);
        particle.am = 0.6f;
        particle.setLifespan(3.0f);
        particle.speed.polar(Random.Float(6.283185f), 0.3f);
        particle.setSize(1.0f, 2.0f);
        particle.radiateXY(2.5f);
    }

    public static class BlastWave
    extends Image {
        private static final float TIME_TO_FADE = 0.2f;
        private float time;
        private float size;

        public BlastWave() {
            super(Effects.get(Effects.Type.RIPPLE));
            this.origin.set(this.width / 2.0f, this.height / 2.0f);
        }

        public void reset(int pos, float size) {
            this.revive();
            this.x = (float)(pos % Dungeon.level.width() * 16) + (16.0f - this.width) / 2.0f;
            this.y = (float)(pos / Dungeon.level.width() * 16) + (16.0f - this.height) / 2.0f;
            this.resetColor();
            this.scale.set(0.0f);
            this.time = 0.2f;
            this.size = size;
        }

        @Override
        public void update() {
            float f;
            super.update();
            this.time -= Game.elapsed;
            if (f <= 0.0f) {
                this.kill();
            } else {
                float p = this.time / 0.2f;
                this.alpha(p);
                this.scale.y = this.scale.x = (1.0f - p) * this.size;
            }
        }

        public static void blast(int pos) {
            BlastWave.blast(pos, 3.0f);
        }

        public static void blast(int pos, float radius) {
            BlastWave.blast(pos, radius, -1);
        }

        public static void blast(int pos, float radius, int hardLight) {
            Group parent = Dungeon.hero.sprite.parent;
            BlastWave b = (BlastWave)parent.recycle(BlastWave.class);
            parent.bringToFront(b);
            b.reset(pos, radius);
            if (hardLight != -1) {
                b.hardlight(hardLight);
            }
        }
    }

    public static class BWaveOnHitTracker
    extends FlavourBuff {
    }

    public static class Knockback {
    }
}

