/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CleansingDart
extends TippedDart {
    public CleansingDart() {
        this.image = ItemSpriteSheet.CLEANSING_DART;
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero && ((Hero)owner).attackTarget().alignment == owner.alignment) {
            return 0;
        }
        return super.damageRoll(owner);
    }

    @Override
    public int proc(Char attacker, final Char defender, int damage) {
        if (!this.processingChargedShot || defender != attacker) {
            if (attacker.alignment == defender.alignment) {
                PotionOfCleansing.cleanse(defender, 10.0f);
                return 0;
            }
            for (Buff b : defender.buffs()) {
                if (b instanceof ChampionEnemy || b.type != Buff.buffType.POSITIVE || b instanceof Crossbow.ChargedShot) continue;
                b.detach();
            }
            if (!defender.isAlive()) {
                defender.die(attacker);
                return super.proc(attacker, defender, damage);
            }
            if (defender instanceof Mob) {
                new FlavourBuff(){
                    {
                        this.actPriority = 100;
                    }

                    @Override
                    public boolean act() {
                        if (((Mob)defender).state == ((Mob)defender).HUNTING || ((Mob)defender).state == ((Mob)defender).FLEEING) {
                            ((Mob)defender).state = ((Mob)defender).WANDERING;
                        }
                        ((Mob)defender).beckon(Dungeon.level.randomDestination(defender));
                        defender.sprite.showLost();
                        return super.act();
                    }
                }.attachTo(defender);
            }
        }
        return super.proc(attacker, defender, damage);
    }
}

