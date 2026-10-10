/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Explosive
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);
    private static ItemSprite.Glowing WARM = new ItemSprite.Glowing(0, 0.5f);
    private static ItemSprite.Glowing HOT = new ItemSprite.Glowing(0, 0.25f);
    private int durability = 100;
    private static final String DURABILITY = "durability";

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (weapon instanceof MissileWeapon && ((MissileWeapon)weapon).parent != null && ((MissileWeapon)weapon).parent.enchantment instanceof Explosive) {
            this.durability = ((Explosive)((MissileWeapon)weapon).parent.enchantment).durability;
        }
        int durToReduce = Math.round((float)Random.IntRange(0, 10) * this.procChanceMultiplier(attacker));
        int currentDurability = this.durability;
        this.durability -= durToReduce;
        if (currentDurability > 50 && this.durability <= 50) {
            attacker.sprite.showStatus(0xFF8800, Messages.get(this, "warm", new Object[0]), new Object[0]);
            GLog.w(Messages.get(this, "desc_warm", new Object[0]), new Object[0]);
            attacker.sprite.emitter().burst(SmokeParticle.FACTORY, 4);
            Item.updateQuickslot();
        } else if (currentDurability > 10 && this.durability <= 10) {
            attacker.sprite.showStatus(0xFF8800, Messages.get(this, "hot", new Object[0]), new Object[0]);
            GLog.n(Messages.get(this, "desc_hot", new Object[0]), new Object[0]);
            attacker.sprite.emitter().burst(BlastParticle.FACTORY, 5);
            Item.updateQuickslot();
        } else if (this.durability <= 0) {
            int explosionPos = -1;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.solid[defender.pos + i] || explosionPos != -1 && !(Dungeon.level.trueDistance(attacker.pos, defender.pos + i) < Dungeon.level.trueDistance(attacker.pos, explosionPos))) continue;
                explosionPos = defender.pos + i;
            }
            if (explosionPos == -1) {
                explosionPos = defender.pos;
            }
            new ExplosiveCurseBomb().explode(explosionPos);
            this.durability += 100;
            Item.updateQuickslot();
            if (weapon instanceof MissileWeapon) {
                ((MissileWeapon)weapon).damage(9.0f * ((MissileWeapon)weapon).durabilityPerUse());
            }
        }
        if (weapon instanceof MissileWeapon && ((MissileWeapon)weapon).parent != null && ((MissileWeapon)weapon).parent.enchantment instanceof Explosive) {
            ((Explosive)((MissileWeapon)weapon).parent.enchantment).durability = this.durability;
            this.durability = 100;
        }
        return damage;
    }

    public void merge(Explosive other) {
        int diff = 100 - other.durability;
        this.durability -= diff;
    }

    public void clear() {
        this.durability = 100;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.durability > 50) {
            return BLACK;
        }
        if (this.durability > 10) {
            return WARM;
        }
        return HOT;
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        desc = this.durability > 50 ? (String)desc + " " + Messages.get(this, "desc_cool", new Object[0]) : (this.durability > 10 ? (String)desc + " " + Messages.get(this, "desc_warm", new Object[0]) : (String)desc + " _" + Messages.get(this, "desc_hot", new Object[0]) + "_");
        return desc;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.durability = bundle.getInt(DURABILITY);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(DURABILITY, this.durability);
    }

    public static class ExplosiveCurseBomb
    extends Bomb.ConjuredBomb {
    }
}

