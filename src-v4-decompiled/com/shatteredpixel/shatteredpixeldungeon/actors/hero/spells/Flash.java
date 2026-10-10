/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Flash
extends TargetedClericSpell {
    public static Flash INSTANCE = new Flash();

    @Override
    public int icon() {
        return 60;
    }

    @Override
    public float chargeUse(Hero hero) {
        if (hero.buff(AscendedForm.AscendBuff.class) != null) {
            return 2 + hero.buff(AscendedForm.AscendBuff.class).flashCasts;
        }
        return 2.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.FLASH) && hero.buff(AscendedForm.AscendBuff.class) != null;
    }

    @Override
    public int targetingFlags() {
        return -1;
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (Dungeon.level.solid[target] || !Dungeon.level.mapped[target] && !Dungeon.level.visited[target] || Dungeon.level.distance(hero.pos, target) > 2 + hero.pointsInTalent(Talent.FLASH)) {
            GLog.w(Messages.get(this, "invalid_target", new Object[0]), new Object[0]);
            return;
        }
        if (ScrollOfTeleportation.teleportToLocation(hero, target)) {
            hero.spendAndNext(1.0f);
            this.onSpellCast(tome, hero);
            ++hero.buff(AscendedForm.AscendBuff.class).flashCasts;
        }
    }
}

