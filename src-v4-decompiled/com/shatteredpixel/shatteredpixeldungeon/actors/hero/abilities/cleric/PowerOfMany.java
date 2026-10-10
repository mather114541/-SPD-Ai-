/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PrismaticGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BeamingRay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class PowerOfMany
extends ArmorAbility {
    @Override
    public float chargeUse(Hero hero) {
        if (PowerOfMany.getPoweredAlly() instanceof LightAlly) {
            return 0.0f;
        }
        return super.chargeUse(hero);
    }

    @Override
    public String targetingPrompt() {
        boolean allyExists;
        Char ally = PowerOfMany.getPoweredAlly();
        boolean bl = allyExists = ally != null;
        if (Dungeon.hero.buff(PrismaticGuard.class) != null && Dungeon.hero.buff(PrismaticGuard.class).isEmpowered()) {
            allyExists = true;
        }
        if (Dungeon.hero.buff(WandOfLivingEarth.RockArmor.class) != null && Dungeon.hero.buff(WandOfLivingEarth.RockArmor.class).isEmpowered()) {
            allyExists = true;
        }
        if (Stasis.getStasisAlly() != null) {
            allyExists = true;
        }
        if (ally instanceof LightAlly) {
            return Messages.get(this, "prompt_ally", new Object[0]);
        }
        if (!allyExists) {
            return Messages.get(this, "prompt_default", new Object[0]);
        }
        return null;
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        boolean allyExists;
        Char ally = PowerOfMany.getPoweredAlly();
        boolean bl = allyExists = ally != null;
        if (hero.buff(PrismaticGuard.class) != null && hero.buff(PrismaticGuard.class).isEmpowered()) {
            allyExists = true;
        }
        if (hero.buff(WandOfLivingEarth.RockArmor.class) != null && hero.buff(WandOfLivingEarth.RockArmor.class).isEmpowered()) {
            allyExists = true;
        }
        if (Stasis.getStasisAlly() != null) {
            allyExists = true;
        }
        if (ally instanceof LightAlly) {
            if (target == null) {
                return;
            }
            ((LightAlly)ally).directTocell(target);
        } else if (allyExists) {
            GLog.w(Messages.get(this, "ally_exists", new Object[0]), new Object[0]);
        } else {
            if (target == null) {
                return;
            }
            if (!Dungeon.level.heroFOV[target]) {
                GLog.w(Messages.get(this, "no_vision", new Object[0]), new Object[0]);
                return;
            }
            float chargeUse = this.chargeUse(hero);
            Char ch = Actor.findChar(target);
            if (ch != null) {
                if (ch.alignment != Char.Alignment.ALLY || ch == Dungeon.hero) {
                    GLog.w(Messages.get(this, "only_allies", new Object[0]), new Object[0]);
                    return;
                }
            } else {
                if (!Dungeon.level.passable[target] || Dungeon.level.avoid[target]) {
                    GLog.w(Messages.get(ClericSpell.class, "invalid_target", new Object[0]), new Object[0]);
                    return;
                }
                ch = new LightAlly(hero.lvl);
                ch.pos = target;
                GameScene.add((Mob)ch);
                ScrollOfTeleportation.appear(ch, ch.pos);
            }
            Buff.affect(ch, PowerBuff.class, 100.0f);
            Buff.affect(ch, Barrier.class).setShield(25);
            armor.charge -= chargeUse;
            armor.updateQuickslot();
            hero.sprite.zap(target);
            Sample.INSTANCE.play("sounds/chargeup.mp3");
            Invisibility.dispel();
            hero.spendAndNext(1.0f);
        }
    }

    @Override
    public int icon() {
        return 33;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.BEAMING_RAY, Talent.LIFE_LINK, Talent.STASIS, Talent.HEROIC_ENERGY};
    }

    public static Char getPoweredAlly() {
        for (Char ch : Actor.chars()) {
            if (ch.buff(PowerBuff.class) == null) continue;
            return ch;
        }
        return null;
    }

    public static class LightAlly
    extends DirectableAlly {
        HeroClass cls;
        private static final String HERO_CLS = "hero_cls";
        private static final String DEF_SKILL = "def_skill";

        public LightAlly() {
            this.spriteClass = LightAllySprite.class;
            this.HT = 80;
            this.HP = 80;
            this.immunities.add(AllyBuff.class);
            this.properties.add(Char.Property.INORGANIC);
            this.cls = HeroClass.values()[Random.Int(5)];
        }

        public LightAlly(int heroLevel) {
            this();
            this.defenseSkill = heroLevel + 4;
        }

        @Override
        protected boolean act() {
            if (this.buff(PowerBuff.class) == null) {
                this.die(null);
                return true;
            }
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
            return Random.NormalIntRange(5, 30);
        }

        @Override
        public int drRoll() {
            return super.drRoll() + Random.NormalIntRange(1, 5);
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
        public CharSprite sprite() {
            CharSprite sprite = super.sprite();
            ((LightAllySprite)sprite).setup(this.cls);
            return sprite;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(HERO_CLS, this.cls);
            bundle.put(DEF_SKILL, this.defenseSkill);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.cls = bundle.getEnum(HERO_CLS, HeroClass.class);
            this.defenseSkill = bundle.getInt(DEF_SKILL);
        }
    }

    public static class PowerBuff
    extends FlavourBuff {
        public static float DURATION = 100.0f;

        public PowerBuff() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
        }

        @Override
        public int icon() {
            return 83;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.GLOWING);
            } else {
                this.target.sprite.remove(CharSprite.State.GLOWING);
            }
        }

        @Override
        public boolean act() {
            if (this.target.buff(BeamingRay.BeamingRayBoost.class) != null || this.target.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
                this.spend(1.0f);
                return true;
            }
            return super.act();
        }

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }
    }

    public static class LightAllySprite
    extends MobSprite {
        public LightAllySprite() {
            this.setup(HeroClass.values()[Random.Int(5)]);
        }

        public void setup(HeroClass cls) {
            this.texture(cls.spritesheet());
            TextureFilm film = new TextureFilm(HeroSprite.tiers(), (Object)6, 12, 15);
            this.idle = new MovieClip.Animation(1, true);
            this.idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);
            this.run = new MovieClip.Animation(20, true);
            this.run.frames(film, 2, 3, 4, 5, 6, 7);
            this.die = new MovieClip.Animation(20, false);
            this.die.frames(film, 0);
            this.attack = new MovieClip.Animation(15, false);
            this.attack.frames(film, 13, 14, 15, 0);
            this.play(this.idle, true);
            this.resetColor();
        }

        @Override
        public void link(Char ch) {
            super.link(ch);
            if (ch instanceof LightAlly) {
                this.setup(((LightAlly)ch).cls);
            }
        }

        @Override
        public void resetColor() {
            super.resetColor();
            this.alpha(0.8f);
            this.tint(1.33f, 1.33f, 0.8f, 0.6f);
            this.bm = 0.0f;
            this.gm = 0.0f;
            this.rm = 0.0f;
        }

        @Override
        public void die() {
            super.die();
            this.emitter().start(ShaftParticle.FACTORY, 0.3f, 4);
            this.emitter().start(Speck.factory(2), 0.2f, 3);
        }

        @Override
        public void draw() {
            if (this.alpha() >= 0.8f) {
                this.alpha(0.8f);
            }
            this.bm = 0.0f;
            this.gm = 0.0f;
            this.rm = 0.0f;
            super.draw();
        }
    }
}

