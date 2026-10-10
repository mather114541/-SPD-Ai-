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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ShieldOfLight
extends TargetedClericSpell {
    public static ShieldOfLight INSTANCE = new ShieldOfLight();

    @Override
    public int icon() {
        return 44;
    }

    @Override
    public int targetingFlags() {
        return 1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.SHIELD_OF_LIGHT);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Char ch = Actor.findChar(target);
        if (ch == null || ch.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        QuickSlotButton.target(ch);
        Sample.INSTANCE.play("sounds/read.mp3");
        hero.sprite.operate(hero.pos);
        Buff.prolong((Char)hero, ShieldOfLightTracker.class, (float)4.0f).object = ch.id();
        if (hero.subClass == HeroSubClass.PRIEST) {
            Buff.affect(ch, GuidingLight.Illuminated.class);
        }
        hero.busy();
        hero.sprite.operate(hero.pos);
        hero.sprite.emitter().start(Speck.factory(2), 0.15f, 6);
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
            Buff.prolong((Char)ally, ShieldOfLightTracker.class, (float)3.0f).object = ch.id();
            ally.sprite.emitter().start(Speck.factory(2), 0.15f, 6);
        }
        this.onSpellCast(tome, hero);
    }

    @Override
    public String desc() {
        int min = 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
        int max = 2 * min;
        return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class ShieldOfLightTracker
    extends FlavourBuff {
        public int object = 0;
        private static final float DURATION = 5.0f;
        private static final String OBJECT = "object";

        public ShieldOfLightTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 76;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(OBJECT, this.object);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.object = bundle.getInt(OBJECT);
        }
    }
}

