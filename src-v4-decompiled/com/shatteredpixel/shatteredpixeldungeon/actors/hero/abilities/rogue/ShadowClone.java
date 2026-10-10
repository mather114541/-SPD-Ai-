/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ShadowClone
extends ArmorAbility {
    public ShadowClone() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    public String targetingPrompt() {
        if (ShadowClone.getShadowAlly() == null) {
            return super.targetingPrompt();
        }
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    public float chargeUse(Hero hero) {
        if (ShadowClone.getShadowAlly() == null) {
            return super.chargeUse(hero);
        }
        return 0.0f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        ShadowAlly ally = ShadowClone.getShadowAlly();
        if (ally != null) {
            if (target == null) {
                return;
            }
            ally.directTocell(target);
        } else {
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
                spawnPoints.add(p);
            }
            if (!spawnPoints.isEmpty()) {
                armor.charge -= this.chargeUse(hero);
                armor.updateQuickslot();
                ally = new ShadowAlly(hero.lvl);
                ally.pos = (Integer)Random.element(spawnPoints);
                GameScene.add(ally);
                ShadowAlly.appear(ally, ally.pos);
                Invisibility.dispel();
                hero.spendAndNext(1.0f);
            } else {
                GLog.w(Messages.get(SpiritHawk.class, "no_space", new Object[0]), new Object[0]);
            }
        }
    }

    @Override
    public int icon() {
        return 24;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHADOW_BLADE, Talent.CLONED_ARMOR, Talent.PERFECT_COPY, Talent.HEROIC_ENERGY};
    }

    private static ShadowAlly getShadowAlly() {
        for (Char ch : Actor.chars()) {
            if (!(ch instanceof ShadowAlly)) continue;
            return (ShadowAlly)ch;
        }
        return null;
    }

    public static class ShadowAlly
    extends DirectableAlly {
        private static final String DEF_SKILL = "def_skill";

        public ShadowAlly() {
            this.spriteClass = ShadowSprite.class;
            this.HT = 80;
            this.HP = 80;
            this.immunities.add(AllyBuff.class);
            this.properties.add(Char.Property.INORGANIC);
        }

        public ShadowAlly(int heroLevel) {
            this.spriteClass = ShadowSprite.class;
            this.HT = 80;
            this.HP = 80;
            this.immunities.add(AllyBuff.class);
            this.properties.add(Char.Property.INORGANIC);
            int hpBonus = 15 + 5 * heroLevel;
            hpBonus = Math.round(0.1f * (float)Dungeon.hero.pointsInTalent(Talent.PERFECT_COPY) * (float)hpBonus);
            if (hpBonus > 0) {
                this.HT += hpBonus;
                this.HP += hpBonus;
            }
            this.defenseSkill = heroLevel + 4;
        }

        @Override
        protected boolean act() {
            int oldPos = this.pos;
            boolean result = super.act();
            if ((this.pos == this.target || oldPos == this.pos) && this.sprite.looping()) {
                this.sprite.idle();
            }
            return result;
        }

        @Override
        public void defendPos(int cell) {
            GLog.i(Messages.get(this, "direct_defend", new Object[0]), new Object[0]);
            super.defendPos(cell);
        }

        @Override
        public void followHero() {
            GLog.i(Messages.get(this, "direct_follow", new Object[0]), new Object[0]);
            super.followHero();
        }

        @Override
        public void targetChar(Char ch) {
            GLog.i(Messages.get(this, "direct_attack", new Object[0]), new Object[0]);
            super.targetChar(ch);
        }

        @Override
        public int attackSkill(Char target) {
            return this.defenseSkill + 5;
        }

        @Override
        public int damageRoll() {
            int damage = Random.NormalIntRange(10, 20);
            int heroDamage = Dungeon.hero.damageRoll();
            heroDamage = (int)((float)heroDamage / Dungeon.hero.attackDelay());
            heroDamage = Math.round(0.08f * (float)Dungeon.hero.pointsInTalent(Talent.SHADOW_BLADE) * (float)heroDamage);
            if (heroDamage > 0) {
                damage += heroDamage;
            }
            return damage;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);
            if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.SHADOW_BLADE) && Dungeon.hero.belongings.weapon() != null) {
                return Dungeon.hero.belongings.weapon().proc(this, enemy, damage);
            }
            return damage;
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            int heroRoll = Dungeon.hero.drRoll();
            heroRoll = Math.round(0.12f * (float)Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR) * (float)heroRoll);
            if (heroRoll > 0) {
                dr += heroRoll;
            }
            return dr;
        }

        @Override
        public int glyphLevel(Class<? extends Armor.Glyph> cls) {
            if (Dungeon.hero != null && Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR)) {
                return Math.max(super.glyphLevel(cls), Dungeon.hero.glyphLevel(cls));
            }
            return super.glyphLevel(cls);
        }

        @Override
        public int defenseProc(Char enemy, int damage) {
            damage = super.defenseProc(enemy, damage);
            if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR) && Dungeon.hero.belongings.armor() != null) {
                return Dungeon.hero.belongings.armor().proc(enemy, this, damage);
            }
            return damage;
        }

        @Override
        public float speed() {
            float speed = super.speed();
            if (this.state == this.WANDERING && this.defendingPos == -1 && Dungeon.level.distance(this.pos, Dungeon.hero.pos) > 1) {
                speed *= 2.0f;
            }
            return speed;
        }

        @Override
        public boolean canInteract(Char c) {
            if (super.canInteract(c)) {
                return true;
            }
            return Dungeon.level.distance(this.pos, c.pos) <= Dungeon.hero.pointsInTalent(Talent.PERFECT_COPY);
        }

        @Override
        public boolean interact(Char c) {
            if (!Dungeon.hero.hasTalent(Talent.PERFECT_COPY)) {
                return super.interact(c);
            }
            if (!Dungeon.level.passable[this.pos] && !c.flying) {
                return true;
            }
            if (this.properties().contains((Object)Char.Property.LARGE) && !Dungeon.level.openSpace[c.pos] || c.properties().contains((Object)Char.Property.LARGE) && !Dungeon.level.openSpace[this.pos]) {
                return true;
            }
            int curPos = this.pos;
            PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            if (PathFinder.distance[this.pos] == Integer.MAX_VALUE) {
                return true;
            }
            ShadowAlly.appear(this, Dungeon.hero.pos);
            ShadowAlly.appear(Dungeon.hero, curPos);
            Dungeon.observe();
            GameScene.updateFog();
            return true;
        }

        private static void appear(Char ch, int pos) {
            ch.sprite.interruptMotion();
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[ch.pos]) {
                Sample.INSTANCE.play("sounds/puff.mp3");
            }
            ch.move(pos);
            if (ch.pos == pos) {
                ch.sprite.place(pos);
            }
            if (Dungeon.level.heroFOV[pos] || ch == Dungeon.hero) {
                ch.sprite.emitter().burst(SmokeParticle.FACTORY, 10);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DEF_SKILL, this.defenseSkill);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.defenseSkill = bundle.getInt(DEF_SKILL);
        }
    }

    public static class ShadowSprite
    extends MobSprite {
        private Emitter smoke;

        public ShadowSprite() {
            this.texture(HeroClass.ROGUE.spritesheet());
            TextureFilm film = new TextureFilm(HeroSprite.tiers(), (Object)6, 12, 15);
            this.idle = new MovieClip.Animation(1, true);
            this.idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);
            this.run = new MovieClip.Animation(20, true);
            this.run.frames(film, 2, 3, 4, 5, 6, 7);
            this.die = new MovieClip.Animation(20, false);
            this.die.frames(film, 0);
            this.attack = new MovieClip.Animation(15, false);
            this.attack.frames(film, 13, 14, 15, 0);
            this.idle();
            this.resetColor();
        }

        @Override
        public void onComplete(Tweener tweener) {
            super.onComplete(tweener);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            this.alpha(0.8f);
            this.brightness(0.0f);
        }

        @Override
        public void link(Char ch) {
            super.link(ch);
            this.renderShadow = false;
            if (this.smoke == null) {
                this.smoke = this.emitter();
                this.smoke.pour(CityLevel.Smoke.factory, 0.2f);
            }
        }

        @Override
        public void update() {
            super.update();
            if (this.smoke != null) {
                this.smoke.visible = this.visible;
            }
        }

        @Override
        public void kill() {
            super.kill();
            if (this.smoke != null) {
                this.smoke.on = false;
            }
        }
    }
}

