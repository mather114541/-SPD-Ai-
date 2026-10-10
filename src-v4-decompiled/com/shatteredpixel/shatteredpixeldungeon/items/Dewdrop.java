/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Dewdrop
extends Item {
    public Dewdrop() {
        this.image = ItemSpriteSheet.DEWDROP;
        this.stackable = true;
        this.dropsDownHeap = true;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Waterskin flask = hero.belongings.getItem(Waterskin.class);
        Catalog.setSeen(this.getClass());
        Statistics.itemTypesDiscovered.add(this.getClass());
        if (flask != null && !flask.isFull()) {
            flask.collectDew(this);
            GameScene.pickUp(this, pos);
        } else {
            int terr = Dungeon.level.map[pos];
            if (!Dewdrop.consumeDew(1, hero, terr == 7 || terr == 37 || terr == 8 || terr == 22)) {
                return false;
            }
            Catalog.countUse(this.getClass());
        }
        Sample.INSTANCE.play("sounds/dewdrop.mp3");
        hero.spendAndNext(this.pickupDelay());
        return true;
    }

    public static boolean consumeDew(int quantity, Hero hero, boolean force) {
        int effect = Math.round((float)hero.HT * 0.05f * (float)quantity);
        int heal = Math.min(hero.HT - hero.HP, effect);
        int shield = 0;
        if (hero.hasTalent(Talent.SHIELDING_DEW)) {
            if (quantity > 1 && heal < effect && VialOfBlood.delayBurstHealing()) {
                heal = Math.round((float)heal / VialOfBlood.totalHealMultiplier());
            }
            shield = effect - heal;
            int maxShield = Math.round((float)hero.HT * 0.2f * (float)hero.pointsInTalent(Talent.SHIELDING_DEW));
            int curShield = 0;
            if (hero.buff(Barrier.class) != null) {
                curShield = hero.buff(Barrier.class).shielding();
            }
            shield = Math.min(shield, maxShield - curShield);
        }
        if (heal > 0 || shield > 0) {
            if (heal > 0 && quantity > 1 && VialOfBlood.delayBurstHealing()) {
                Healing healing = Buff.affect(hero, Healing.class);
                healing.setHeal(heal, 0.0f, VialOfBlood.maxHealPerTurn(), true);
            } else {
                hero.HP += heal;
                if (heal > 0) {
                    hero.sprite.showStatusWithIcon(65280, Integer.toString(heal), FloatingText.HEALING, new Object[0]);
                }
            }
            if (shield > 0) {
                Buff.affect(hero, Barrier.class).incShield(shield);
                hero.sprite.showStatusWithIcon(65280, Integer.toString(shield), FloatingText.SHIELDING, new Object[0]);
            }
        } else if (!force) {
            GLog.i(Messages.get(Dewdrop.class, "already_full", new Object[0]), new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item merge(Item other) {
        if (this.isSimilar(other)) {
            this.quantity = 1;
            other.quantity = 0;
        }
        return this;
    }

    @Override
    public Item quantity(int value) {
        this.quantity = Math.min(value, 1);
        return this;
    }
}

