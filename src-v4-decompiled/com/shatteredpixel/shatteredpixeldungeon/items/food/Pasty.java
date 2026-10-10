/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.watabou.noosa.audio.Sample;

public class Pasty
extends Food {
    public Pasty() {
        this.reset();
        this.energy = 450.0f;
        this.bones = true;
    }

    @Override
    public void reset() {
        super.reset();
        switch (Holiday.getCurrentHoliday()) {
            default: {
                this.image = ItemSpriteSheet.PASTY;
                break;
            }
            case LUNAR_NEW_YEAR: {
                this.image = ItemSpriteSheet.STEAMED_FISH;
                break;
            }
            case APRIL_FOOLS: {
                this.image = ItemSpriteSheet.CHOC_AMULET;
                break;
            }
            case EASTER: {
                this.image = ItemSpriteSheet.EASTER_EGG;
                break;
            }
            case PRIDE: {
                this.image = ItemSpriteSheet.RAINBOW_POTION;
                break;
            }
            case SHATTEREDPD_BIRTHDAY: {
                this.image = ItemSpriteSheet.SHATTERED_CAKE;
                break;
            }
            case HALLOWEEN: {
                this.image = ItemSpriteSheet.PUMPKIN_PIE;
                break;
            }
            case PD_BIRTHDAY: {
                this.image = ItemSpriteSheet.VANILLA_CAKE;
                break;
            }
            case WINTER_HOLIDAYS: {
                this.image = ItemSpriteSheet.CANDY_CANE;
                break;
            }
            case NEW_YEARS: {
                this.image = ItemSpriteSheet.SPARKLING_POTION;
            }
        }
    }

    @Override
    protected void eatSFX() {
        switch (Holiday.getCurrentHoliday()) {
            case PRIDE: 
            case NEW_YEARS: {
                Sample.INSTANCE.play("sounds/drink.mp3");
                return;
            }
        }
        super.eatSFX();
    }

    @Override
    protected void satisfy(Hero hero) {
        if (Holiday.getCurrentHoliday() == Holiday.LUNAR_NEW_YEAR) {
            this.energy = 300.0f;
        }
        super.satisfy(hero);
        switch (Holiday.getCurrentHoliday()) {
            default: {
                break;
            }
            case LUNAR_NEW_YEAR: {
                FishLeftover left = new FishLeftover();
                if (left.collect()) break;
                Dungeon.level.drop((Item)left, (int)hero.pos).sprite.drop();
                break;
            }
            case APRIL_FOOLS: {
                Sample.INSTANCE.play("sounds/mimic.mp3");
            }
            case EASTER: {
                ArtifactRecharge.chargeArtifacts(hero, 2.0f);
                ScrollOfRecharging.charge(hero);
                break;
            }
            case PRIDE: {
                Char target = null;
                for (Char ch : Actor.chars()) {
                    if (Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS) || ch.alignment != Char.Alignment.ENEMY || !Dungeon.level.adjacent(hero.pos, ch.pos) || target != null && ch != TargetHealthIndicator.instance.target()) continue;
                    target = ch;
                }
                if (target != null) {
                    Buff.affect(target, Charm.class, (float)5.0f).object = hero.id();
                }
                hero.sprite.emitter().burst(RainbowParticle.BURST, 15);
                break;
            }
            case SHATTEREDPD_BIRTHDAY: 
            case PD_BIRTHDAY: {
                int expToGive = Math.max(2, hero.maxExp() / 10);
                hero.sprite.showStatusWithIcon(65280, Integer.toString(expToGive), FloatingText.EXPERIENCE, new Object[0]);
                hero.earnExp(expToGive, PotionOfExperience.class);
                break;
            }
            case HALLOWEEN: {
                int toHeal = Math.max(3, hero.HT / 20);
                hero.HP = Math.min(hero.HP + toHeal, hero.HT);
                hero.sprite.showStatusWithIcon(65280, Integer.toString(toHeal), FloatingText.HEALING, new Object[0]);
                break;
            }
            case WINTER_HOLIDAYS: {
                hero.belongings.charge(0.5f);
                ScrollOfRecharging.charge(hero);
                break;
            }
            case NEW_YEARS: {
                int toShield = Math.max(5, hero.HT / 10);
                Buff.affect(hero, Barrier.class).setShield(toShield);
                hero.sprite.showStatusWithIcon(65280, Integer.toString(toShield), FloatingText.SHIELDING, new Object[0]);
            }
        }
    }

    @Override
    public String name() {
        switch (Holiday.getCurrentHoliday()) {
            default: {
                return super.name();
            }
            case LUNAR_NEW_YEAR: {
                return Messages.get(this, "fish_name", new Object[0]);
            }
            case APRIL_FOOLS: {
                return Messages.get(this, "amulet_name", new Object[0]);
            }
            case EASTER: {
                return Messages.get(this, "egg_name", new Object[0]);
            }
            case PRIDE: {
                return Messages.get(this, "rainbow_name", new Object[0]);
            }
            case SHATTEREDPD_BIRTHDAY: {
                return Messages.get(this, "shattered_name", new Object[0]);
            }
            case HALLOWEEN: {
                return Messages.get(this, "pie_name", new Object[0]);
            }
            case PD_BIRTHDAY: {
                return Messages.get(this, "vanilla_name", new Object[0]);
            }
            case WINTER_HOLIDAYS: {
                return Messages.get(this, "cane_name", new Object[0]);
            }
            case NEW_YEARS: 
        }
        return Messages.get(this, "sparkling_name", new Object[0]);
    }

    @Override
    public String desc() {
        switch (Holiday.getCurrentHoliday()) {
            default: {
                return super.desc();
            }
            case LUNAR_NEW_YEAR: {
                return Messages.get(this, "fish_desc", new Object[0]);
            }
            case APRIL_FOOLS: {
                return Messages.get(this, "amulet_desc", new Object[0]);
            }
            case EASTER: {
                return Messages.get(this, "egg_desc", new Object[0]);
            }
            case PRIDE: {
                return Messages.get(this, "rainbow_desc", new Object[0]);
            }
            case SHATTEREDPD_BIRTHDAY: {
                return Messages.get(this, "shattered_desc", new Object[0]);
            }
            case HALLOWEEN: {
                return Messages.get(this, "pie_desc", new Object[0]);
            }
            case PD_BIRTHDAY: {
                return Messages.get(this, "vanilla_desc", new Object[0]);
            }
            case WINTER_HOLIDAYS: {
                return Messages.get(this, "cane_desc", new Object[0]);
            }
            case NEW_YEARS: 
        }
        return Messages.get(this, "sparkling_desc", new Object[0]);
    }

    @Override
    public int value() {
        return 20 * this.quantity;
    }

    public static class FishLeftover
    extends Food {
        public FishLeftover() {
            this.image = ItemSpriteSheet.FISH_LEFTOVER;
            this.energy = 150.0f;
        }

        @Override
        public int value() {
            return 10 * this.quantity;
        }
    }
}

