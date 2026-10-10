/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class DivineSense
extends ClericSpell {
    public static final DivineSense INSTANCE = new DivineSense();

    @Override
    public int icon() {
        return 47;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.DIVINE_SENSE);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        Buff.prolong(hero, DivineSenseTracker.class, 50.0f);
        Dungeon.observe();
        Sample.INSTANCE.play("sounds/read.mp3");
        SpellSprite.show(hero, 6);
        hero.sprite.operate(hero.pos);
        hero.next();
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
            Buff.prolong(ally, DivineSenseTracker.class, 50.0f);
            SpellSprite.show(ally, 6);
        }
        this.onSpellCast(tome, hero);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 4 + 4 * Dungeon.hero.pointsInTalent(Talent.DIVINE_SENSE)) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class DivineSenseTracker
    extends FlavourBuff {
        public static final float DURATION = 50.0f;

        public DivineSenseTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 77;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (50.0f - this.visualcooldown()) / 50.0f);
        }

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }
    }
}

