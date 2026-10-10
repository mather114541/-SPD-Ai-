/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;

public class HolyWeapon
extends ClericSpell {
    public static final HolyWeapon INSTANCE = new HolyWeapon();

    @Override
    public int icon() {
        return 41;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        Buff.affect(hero, HolyWepBuff.class, 50.0f);
        Item.updateQuickslot();
        Sample.INSTANCE.play("sounds/read.mp3");
        hero.sprite.operate(hero.pos);
        if (hero.belongings.weapon() != null) {
            Enchanting.show(hero, hero.belongings.weapon());
        }
        this.onSpellCast(tome, hero);
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        if (Dungeon.hero.subClass == HeroSubClass.PALADIN) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_paladin", new Object[0]);
        }
        return (String)desc + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class HolyWepBuff
    extends FlavourBuff {
        public static final float DURATION = 50.0f;

        public HolyWepBuff() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 73;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (50.0f - this.visualcooldown()) / 50.0f);
        }

        @Override
        public String desc() {
            if (Dungeon.hero.subClass == HeroSubClass.PALADIN) {
                return Messages.get(this, "desc_paladin", this.dispTurns());
            }
            return Messages.get(this, "desc", this.dispTurns());
        }

        @Override
        public void detach() {
            super.detach();
            Item.updateQuickslot();
        }

        public void extend(float extension) {
            if (this.cooldown() + extension <= 100.0f) {
                this.spend(extension);
            } else {
                this.postpone(100.0f);
            }
        }
    }
}

