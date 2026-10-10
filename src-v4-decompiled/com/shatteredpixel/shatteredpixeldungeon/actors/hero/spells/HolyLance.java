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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class HolyLance
extends TargetedClericSpell {
    public static final HolyLance INSTANCE = new HolyLance();

    @Override
    public int icon() {
        return 51;
    }

    @Override
    public String desc() {
        int min = 15 + 15 * Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
        int max = Math.round(27.5f + 27.5f * (float)Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
        return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_LANCE) && hero.buff(LanceCooldown.class) == null;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 4.0f;
    }

    @Override
    public int targetingFlags() {
        return 7;
    }

    @Override
    protected void onTargetSelected(final HolyTome tome, final Hero hero, final Integer target) {
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
        hero.sprite.zap(target);
        hero.busy();
        Sample.INSTANCE.play("sounds/zap.mp3");
        final Char enemy = Actor.findChar(aim.collisionPos);
        if (enemy != null) {
            ((MissileSprite)hero.sprite.parent.recycle(MissileSprite.class)).reset((Visual)hero.sprite, (Visual)enemy.sprite, (Item)new HolyLanceVFX(), new Callback(){

                @Override
                public void call() {
                    int min = 15 + 15 * Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
                    int max = Math.round(27.5f + 27.5f * (float)Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
                    if (Char.hasProp(enemy, Char.Property.UNDEAD) || Char.hasProp(enemy, Char.Property.DEMONIC)) {
                        min = max;
                    }
                    enemy.damage(Hero.heroDamageIntRange(min, max), HolyLance.this);
                    Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, Random.Float(0.8f, 1.0f));
                    Sample.INSTANCE.play("sounds/hit_stab.mp3", 1.0f, Random.Float(0.8f, 1.0f));
                    if (enemy.isActive()) {
                        Buff.affect(enemy, GuidingLight.Illuminated.class);
                    }
                    enemy.sprite.burst(-1, 10);
                    hero.spendAndNext(1.0f);
                    HolyLance.this.onSpellCast(tome, hero);
                    FlavourBuff.affect(hero, LanceCooldown.class, 30.0f);
                }
            });
        } else {
            ((MissileSprite)hero.sprite.parent.recycle(MissileSprite.class)).reset((Visual)hero.sprite, (int)target, (Item)new HolyLanceVFX(), new Callback(){

                @Override
                public void call() {
                    Splash.at(target, -1, 10);
                    Dungeon.level.pressCell(aim.collisionPos);
                    hero.spendAndNext(1.0f);
                    HolyLance.this.onSpellCast(tome, hero);
                    FlavourBuff.affect(hero, LanceCooldown.class, 30.0f);
                }
            });
        }
    }

    public static class LanceCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.67f, 0.67f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 30.0f);
        }
    }

    public static class HolyLanceVFX
    extends Item {
        public HolyLanceVFX() {
            this.image = ItemSpriteSheet.THROWING_SPIKE;
        }

        @Override
        public ItemSprite.Glowing glowing() {
            return new ItemSprite.Glowing(0xFFFFFF, 0.1f);
        }

        @Override
        public Emitter emitter() {
            Emitter emitter = new Emitter();
            emitter.pos(5.0f, 5.0f, 0.0f, 0.0f);
            emitter.fillTarget = false;
            emitter.pour(SparkParticle.FACTORY, 0.025f);
            return emitter;
        }
    }
}

