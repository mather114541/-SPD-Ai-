/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class Cleanse
extends ClericSpell {
    public static Cleanse INSTANCE = new Cleanse();

    @Override
    public int icon() {
        return 49;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public String desc() {
        int immunity = 2 * (Dungeon.hero.pointsInTalent(Talent.CLEANSE) - 1);
        if (immunity > 0) {
            ++immunity;
        }
        int shield = 10 * Dungeon.hero.pointsInTalent(Talent.CLEANSE);
        return Messages.get(this, "desc", immunity, shield) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.CLEANSE);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        ArrayList<Char> affected = new ArrayList<Char>();
        affected.add(hero);
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (!Dungeon.level.heroFOV[mob.pos] || mob.alignment != Char.Alignment.ALLY) continue;
            affected.add(mob);
        }
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null && !affected.contains(ally)) {
            affected.add(ally);
        }
        for (Char ch : affected) {
            for (Buff b : ch.buffs()) {
                if (b.type != Buff.buffType.NEGATIVE || b instanceof AllyBuff || b instanceof LostInventory) continue;
                b.detach();
            }
            if (hero.pointsInTalent(Talent.CLEANSE) > 1) {
                Buff.prolong(ch, PotionOfCleansing.Cleanse.class, 2 * (Dungeon.hero.pointsInTalent(Talent.CLEANSE) - 1));
            }
            Buff.affect(ch, Barrier.class).setShield(10 * hero.pointsInTalent(Talent.CLEANSE));
            new Flare(6, 32.0f).color(16731346, true).show(ch.sprite, 2.0f);
        }
        hero.spend(1.0f);
        hero.busy();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play("sounds/read.mp3");
        this.onSpellCast(tome, hero);
    }
}

