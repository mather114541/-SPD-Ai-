/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SmokeBomb
extends ArmorAbility {
    public SmokeBomb() {
        this.baseChargeUse = 50.0f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    public float chargeUse(Hero hero) {
        if (!hero.hasTalent(Talent.SHADOW_STEP) || hero.invisible <= 0) {
            return super.chargeUse(hero);
        }
        return (float)((double)super.chargeUse(hero) * Math.pow(0.84, hero.pointsInTalent(Talent.SHADOW_STEP)));
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            boolean shadowStepping;
            if (target != hero.pos && hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
                return;
            }
            PathFinder.buildDistanceMap(hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), 6);
            if (PathFinder.distance[target] == Integer.MAX_VALUE || !Dungeon.level.heroFOV[target] || target != hero.pos && Actor.findChar(target) != null) {
                GLog.w(Messages.get(this, "fov", new Object[0]), new Object[0]);
                return;
            }
            armor.charge -= this.chargeUse(hero);
            Item.updateQuickslot();
            boolean bl = shadowStepping = hero.invisible > 0 && hero.hasTalent(Talent.SHADOW_STEP);
            if (!shadowStepping) {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (!Dungeon.level.adjacent(mob.pos, hero.pos) || mob.alignment == Char.Alignment.ALLY) continue;
                    Buff.prolong(mob, Blindness.class, 5.0f);
                    if (mob.state == mob.HUNTING) {
                        mob.state = mob.WANDERING;
                    }
                    mob.sprite.emitter().burst(Speck.factory(2), 4);
                }
                if (hero.hasTalent(Talent.BODY_REPLACEMENT)) {
                    for (Char ch : Actor.chars()) {
                        if (!(ch instanceof NinjaLog)) continue;
                        ch.die(null);
                    }
                    NinjaLog n = new NinjaLog();
                    n.pos = hero.pos;
                    GameScene.add(n);
                    Dungeon.level.occupyCell(n);
                }
                if (hero.hasTalent(Talent.HASTY_RETREAT)) {
                    float duration = 0.67f + (float)hero.pointsInTalent(Talent.HASTY_RETREAT);
                    Buff.affect(hero, Haste.class, duration);
                    Buff.affect(hero, Invisibility.class, duration);
                }
            }
            CellEmitter.get(hero.pos).burst(Speck.factory(7), 10);
            ScrollOfTeleportation.appear(hero, target);
            Sample.INSTANCE.play("sounds/puff.mp3");
            Dungeon.level.occupyCell(hero);
            Dungeon.observe();
            GameScene.updateFog();
            if (!shadowStepping) {
                hero.spendAndNext(1.0f);
            } else {
                hero.next();
            }
        }
    }

    @Override
    public int icon() {
        return 22;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HASTY_RETREAT, Talent.BODY_REPLACEMENT, Talent.SHADOW_STEP, Talent.HEROIC_ENERGY};
    }

    public static class NinjaLog
    extends NPC {
        public NinjaLog() {
            this.spriteClass = NinjaLogSprite.class;
            this.defenseSkill = 0;
            this.properties.add(Char.Property.INORGANIC);
            this.alignment = Char.Alignment.ALLY;
            this.HT = 20;
            if (Dungeon.hero != null) {
                this.HT *= Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT);
            }
            this.HP = this.HT;
            this.immunities.add(Dread.class);
            this.immunities.add(Terror.class);
            this.immunities.add(Amok.class);
            this.immunities.add(Charm.class);
            this.immunities.add(Sleep.class);
            this.immunities.add(AllyBuff.class);
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            return dr += Random.NormalIntRange(Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT), 3 * Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT));
        }
    }

    public static class NinjaLogSprite
    extends MobSprite {
        public NinjaLogSprite() {
            this.texture("sprites/ninja_log.png");
            TextureFilm frames = new TextureFilm(this.texture, 11, 12);
            this.idle = new MovieClip.Animation(0, true);
            this.idle.frames(frames, 0);
            this.run = this.idle.clone();
            this.attack = this.idle.clone();
            this.zap = this.attack.clone();
            this.die = new MovieClip.Animation(12, false);
            this.die.frames(frames, 1, 2, 3, 4);
            this.play(this.idle);
        }

        @Override
        public void showAlert() {
        }

        @Override
        public int blood() {
            return -6921216;
        }
    }
}

