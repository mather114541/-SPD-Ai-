/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class GuidingLight
extends TargetedClericSpell {
    public static final GuidingLight INSTANCE = new GuidingLight();

    @Override
    public int icon() {
        return 40;
    }

    @Override
    protected void onTargetSelected(final HolyTome tome, final Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        final Ballistica aim = new Ballistica(hero.pos, target, this.targetingFlags());
        if (Actor.findChar(aim.collisionPos) == hero) {
            GLog.i(Messages.get(Wand.class, "self_target", new Object[0]), new Object[0]);
            return;
        }
        if (Actor.findChar(aim.collisionPos) != null) {
            QuickSlotButton.target(Actor.findChar(aim.collisionPos));
        } else {
            QuickSlotButton.target(Actor.findChar(target));
        }
        hero.busy();
        Sample.INSTANCE.play("sounds/zap.mp3");
        hero.sprite.zap(target);
        MagicMissile.boltFromChar(hero.sprite.parent, 17, hero.sprite, aim.collisionPos, new Callback(){

            @Override
            public void call() {
                Char ch = Actor.findChar(aim.collisionPos);
                if (ch != null) {
                    ch.damage(Hero.heroDamageIntRange(2, 8), GuidingLight.this);
                    Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, Random.Float(0.87f, 1.15f));
                    ch.sprite.burst(-188, 3);
                    if (ch.isAlive()) {
                        Buff.affect(ch, Illuminated.class);
                        Buff.affect(ch, WasIlluminatedTracker.class);
                    }
                } else {
                    Dungeon.level.pressCell(aim.collisionPos);
                }
                hero.spend(1.0f);
                hero.next();
                GuidingLight.this.onSpellCast(tome, hero);
                if (hero.subClass == HeroSubClass.PRIEST && hero.buff(GuidingLightPriestCooldown.class) == null) {
                    Buff.prolong(hero, GuidingLightPriestCooldown.class, 50.0f);
                    ActionIndicator.refresh();
                }
            }
        });
    }

    @Override
    public float chargeUse(Hero hero) {
        if (hero.subClass == HeroSubClass.PRIEST && hero.buff(GuidingLightPriestCooldown.class) == null) {
            return 0.0f;
        }
        return 1.0f;
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        if (Dungeon.hero.subClass == HeroSubClass.PRIEST) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_priest", new Object[0]);
        }
        return (String)desc + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class GuidingLightPriestCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 81;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.brightness(0.5f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 50.0f);
        }

        @Override
        public void detach() {
            super.detach();
            ActionIndicator.refresh();
        }
    }

    public static class WasIlluminatedTracker
    extends Buff {
    }

    public static class Illuminated
    extends Buff {
        public Illuminated() {
            this.type = Buff.buffType.NEGATIVE;
        }

        @Override
        public int icon() {
            return 81;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.ILLUMINATED);
            } else {
                this.target.sprite.remove(CharSprite.State.ILLUMINATED);
            }
        }

        @Override
        public String desc() {
            Object desc = super.desc();
            if (Dungeon.hero.subClass == HeroSubClass.PRIEST) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_priest", new Object[0]);
            } else if (Dungeon.hero.heroClass != HeroClass.CLERIC) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_generic", new Object[0]);
            }
            return desc;
        }
    }
}

