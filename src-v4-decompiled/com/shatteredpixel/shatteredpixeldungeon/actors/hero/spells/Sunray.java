/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Sunray
extends TargetedClericSpell {
    public static final Sunray INSTANCE = new Sunray();

    @Override
    public int icon() {
        return 46;
    }

    @Override
    public String desc() {
        int min = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 6 : 4;
        int max = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 12 : 8;
        int dur = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 6 : 4;
        return Messages.get(this, "desc", min, max, dur) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.SUNRAY);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Ballistica aim = new Ballistica(hero.pos, target, this.targetingFlags());
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
        Sample.INSTANCE.play("sounds/ray.mp3");
        hero.sprite.zap(target);
        hero.sprite.parent.add(new Beam.SunRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(aim.collisionPos)));
        Sample.INSTANCE.play("sounds/ray.mp3");
        Char ch = Actor.findChar(aim.collisionPos);
        if (ch != null) {
            ch.sprite.burst(-188, 5);
            if (Char.hasProp(ch, Char.Property.UNDEAD) || Char.hasProp(ch, Char.Property.DEMONIC)) {
                if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
                    ch.damage(12, this);
                } else {
                    ch.damage(8, this);
                }
            } else if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
                ch.damage(Hero.heroDamageIntRange(6, 12), this);
            } else {
                ch.damage(Hero.heroDamageIntRange(4, 8), this);
            }
            if (ch.isAlive()) {
                if (ch.buff(Blindness.class) != null && ch.buff(SunRayRecentlyBlindedTracker.class) != null) {
                    Buff.prolong(ch, Paralysis.class, 2.0f + 2.0f * (float)hero.pointsInTalent(Talent.SUNRAY));
                    ch.buff(SunRayRecentlyBlindedTracker.class).detach();
                } else if (ch.buff(SunRayUsedTracker.class) == null) {
                    Buff.prolong(ch, Blindness.class, 2.0f + 2.0f * (float)hero.pointsInTalent(Talent.SUNRAY));
                    Buff.prolong(ch, SunRayRecentlyBlindedTracker.class, 2.0f + 2.0f * (float)hero.pointsInTalent(Talent.SUNRAY));
                    Buff.affect(ch, SunRayUsedTracker.class);
                }
                if (hero.subClass == HeroSubClass.PRIEST) {
                    Buff.affect(ch, GuidingLight.Illuminated.class);
                }
            }
        }
        hero.spend(1.0f);
        hero.next();
        this.onSpellCast(tome, hero);
    }

    public static class SunRayRecentlyBlindedTracker
    extends FlavourBuff {
    }

    public static class SunRayUsedTracker
    extends Buff {
    }
}

