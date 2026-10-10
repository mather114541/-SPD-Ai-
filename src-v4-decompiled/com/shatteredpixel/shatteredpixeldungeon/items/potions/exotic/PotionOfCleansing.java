/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class PotionOfCleansing
extends ExoticPotion {
    public PotionOfCleansing() {
        this.icon = ItemSpriteSheet.Icons.POTION_CLEANSE;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        PotionOfCleansing.cleanse(hero);
        new Flare(6, 32.0f).color(16731346, true).show(PotionOfCleansing.curUser.sprite, 2.0f);
    }

    @Override
    public void shatter(int cell) {
        if (Actor.findChar(cell) == null) {
            super.shatter(cell);
        } else {
            this.splash(cell);
            if (Dungeon.level.heroFOV[cell]) {
                Sample.INSTANCE.play("sounds/shatter.mp3");
                this.identify();
            }
            if (Actor.findChar(cell) != null) {
                PotionOfCleansing.cleanse(Actor.findChar(cell));
            }
        }
    }

    public static void cleanse(Char ch) {
        PotionOfCleansing.cleanse(ch, 5.0f);
    }

    public static void cleanse(Char ch, float duration) {
        for (Buff b : ch.buffs()) {
            if (b.type == Buff.buffType.NEGATIVE && !(b instanceof AllyBuff) && !(b instanceof LostInventory)) {
                b.detach();
            }
            if (!(b instanceof Hunger)) continue;
            ((Hunger)b).satisfy(450.0f);
        }
        Buff.prolong(ch, Cleanse.class, duration);
    }

    public static class Cleanse
    extends FlavourBuff {
        public static final float DURATION = 5.0f;

        public Cleanse() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 25;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.0f, 2.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
        }
    }
}

