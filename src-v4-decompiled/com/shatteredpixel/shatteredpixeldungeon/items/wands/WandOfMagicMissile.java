/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class WandOfMagicMissile
extends DamageWand {
    public WandOfMagicMissile() {
        this.image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
    }

    @Override
    public int min(int lvl) {
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 8 + 2 * lvl;
    }

    @Override
    public void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(this.damageRoll(), this);
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, Random.Float(0.87f, 1.15f));
            ch.sprite.burst(-1, this.buffedLvl() / 2 + 2);
            for (Wand.Charger wandCharger : curUser.buffs(Wand.Charger.class)) {
                if (wandCharger.wand().buffedLvl() >= this.buffedLvl() && curUser.buff(MagicCharge.class) == null) continue;
                Buff.prolong(curUser, MagicCharge.class, MagicCharge.DURATION).setup(this);
                break;
            }
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        SpellSprite.show(attacker, 2);
        for (Wand.Charger c : attacker.buffs(Wand.Charger.class)) {
            if (c.wand() == this) continue;
            c.gainCharge(0.5f * WandOfMagicMissile.procChanceMultiplier(attacker));
        }
    }

    @Override
    public int initialCharges() {
        return 3;
    }

    public static class MagicCharge
    extends FlavourBuff {
        public static float DURATION = 4.0f;
        private int level;
        private Wand wandJustApplied;
        private static final String LEVEL = "level";

        public MagicCharge() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
            this.level = 0;
        }

        public void setup(Wand wand) {
            if (this.level < wand.buffedLvl()) {
                this.level = wand.buffedLvl();
                this.wandJustApplied = wand;
            }
        }

        @Override
        public void detach() {
            super.detach();
            Item.updateQuickslot();
        }

        public int level() {
            return this.level;
        }

        public Wand wandJustApplied() {
            Wand result = this.wandJustApplied;
            this.wandJustApplied = null;
            return result;
        }

        @Override
        public int icon() {
            return 50;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.2f, 0.6f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.level(), this.dispTurns());
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEVEL, this.level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.level = bundle.getInt(LEVEL);
        }
    }
}

