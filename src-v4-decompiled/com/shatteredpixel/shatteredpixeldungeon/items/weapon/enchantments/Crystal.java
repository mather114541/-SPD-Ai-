/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Crystal
extends Weapon.Enchantment {
    private static ItemSprite.Glowing LIGHT_BLUE = new ItemSprite.Glowing(35071);
    private static ItemSprite.Glowing FLAW = new ItemSprite.Glowing(35071, 0.5f);
    private static ItemSprite.Glowing CRACK = new ItemSprite.Glowing(35071, 0.25f);
    private float durability = 100.0f;
    private float visualDurability = 100.0f;
    private boolean thrownWeapon = false;
    private static final String DURABILITY = "durability";
    private static final String VISUAL_DUR = "visual_dur";
    private static final String THROWN_WEP = "thrown_wep";

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (attacker == Dungeon.hero || attacker instanceof DriedRose.GhostHero) {
            if (weapon instanceof MissileWeapon) {
                this.setThrownWep();
            } else {
                this.thrownWeapon = false;
                this.durability -= Random.Float(2.0f, 4.0f) * weapon.delayFactor(attacker);
                float prevHeuristicDur = this.visualDurability;
                this.visualDurability = GameMath.gate(this.durability - 10.0f, this.visualDurability, this.durability);
                if (prevHeuristicDur > 50.0f && this.visualDurability <= 50.0f) {
                    if (attacker instanceof Hero) {
                        GLog.w(Messages.get(this, "alert_flawed", new Object[0]), new Object[0]);
                    } else if (attacker instanceof DriedRose.GhostHero) {
                        GLog.w(Messages.get(this, "alert_flawed_ghost", new Object[0]), new Object[0]);
                    }
                } else if (prevHeuristicDur > 10.0f && this.visualDurability <= 10.0f) {
                    this.durability = Math.max(this.durability, 1.0f);
                    this.visualDurability = Math.max(this.visualDurability, 1.0f);
                    Sample.INSTANCE.play("sounds/shatter.mp3");
                    if (attacker instanceof Hero) {
                        GLog.n(Messages.get(this, "alert_cracked", new Object[0]), new Object[0]);
                    } else if (attacker instanceof DriedRose.GhostHero) {
                        GLog.n(Messages.get(this, "alert_cracked_ghost", new Object[0]), new Object[0]);
                    }
                } else if (this.visualDurability <= 0.0f) {
                    Sample.INSTANCE.play("sounds/shatter.mp3");
                    Splash.at(attacker.pos, 35071, 15);
                    if (attacker instanceof Hero) {
                        if (weapon.isEquipped((Hero)attacker)) {
                            weapon.doUnequip((Hero)attacker, false);
                        } else {
                            weapon.detachAll(((Hero)attacker).belongings.backpack);
                        }
                        GLog.n(Messages.get(this, "alert_shattered", new Object[0]), new Object[0]);
                    } else if (attacker instanceof DriedRose.GhostHero) {
                        ((DriedRose.GhostHero)attacker).clearWeapon();
                        GLog.n(Messages.get(this, "alert_shattered_ghost", new Object[0]), new Object[0]);
                    }
                }
            }
        }
        if (attacker instanceof Hero) {
            Buff.affect(attacker, CrystalRepair.class);
        } else if (attacker instanceof DriedRose.GhostHero) {
            Buff.affect(Dungeon.hero, CrystalRepair.class);
        }
        int magicDmg = (int)Math.ceil((float)damage * 0.25f * Crystal.genericProcChanceMultiplier(attacker));
        defender.damage(magicDmg, this);
        return damage;
    }

    public void setThrownWep() {
        this.thrownWeapon = true;
    }

    public void setDurability(float amount) {
        this.durability = this.visualDurability = amount;
    }

    public void repair(Weapon w, boolean inRose, float amount) {
        if (w instanceof MissileWeapon) {
            amount /= 2.0f;
            if (((MissileWeapon)w).durabilityLeft() < 100.0f) {
                ((MissileWeapon)w).repair(amount);
                if (((MissileWeapon)w).durabilityLeft() == 100.0f) {
                    GLog.p(Messages.get(this, "alert_fixed", new Object[0]), new Object[0]);
                }
            }
        } else {
            this.durability = Math.min(100.0f, this.durability + amount);
            float prevHeuristicDur = this.visualDurability;
            this.visualDurability = Math.max(this.visualDurability, this.durability - 10.0f);
            if (this.durability == 100.0f) {
                if (this.visualDurability == 90.0f) {
                    if (!inRose) {
                        GLog.p(Messages.get(this, "alert_fixed", new Object[0]), new Object[0]);
                    } else {
                        GLog.p(Messages.get(this, "alert_fixed_ghost", new Object[0]), new Object[0]);
                    }
                }
                this.visualDurability = 100.0f;
            } else if (prevHeuristicDur < 50.0f && this.visualDurability >= 50.0f) {
                if (!inRose) {
                    GLog.p(Messages.get(this, "alert_noflaw", new Object[0]), new Object[0]);
                } else {
                    GLog.p(Messages.get(this, "alert_noflaw_ghost", new Object[0]), new Object[0]);
                }
            } else if (prevHeuristicDur < 10.0f && this.visualDurability >= 10.0f) {
                if (!inRose) {
                    GLog.p(Messages.get(this, "alert_nocrack", new Object[0]), new Object[0]);
                } else {
                    GLog.p(Messages.get(this, "alert_nocrack_ghost", new Object[0]), new Object[0]);
                }
            }
        }
        Item.updateQuickslot();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.visualDurability > 50.0f || this.thrownWeapon) {
            return LIGHT_BLUE;
        }
        if (this.visualDurability > 10.0f) {
            return FLAW;
        }
        return CRACK;
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        desc = this.thrownWeapon ? (String)desc + " " + Messages.get(this, "desc_thrown", new Object[0]) : (this.visualDurability == 100.0f ? (String)desc + " " + Messages.get(this, "desc_perfect", new Object[0]) : (this.visualDurability > 50.0f ? (String)desc + " " + Messages.get(this, "desc_fine", new Object[0]) : (this.visualDurability > 10.0f ? (String)desc + " " + Messages.get(this, "desc_flawed", new Object[0]) : (String)desc + " _" + Messages.get(this, "desc_cracked", new Object[0]) + "_")));
        return desc;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.durability = bundle.getFloat(DURABILITY);
        this.visualDurability = bundle.getFloat(VISUAL_DUR);
        this.thrownWeapon = bundle.getBoolean(THROWN_WEP);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(DURABILITY, this.durability);
        bundle.put(VISUAL_DUR, this.visualDurability);
        bundle.put(THROWN_WEP, this.thrownWeapon);
    }

    public static class CrystalRepair
    extends Buff {
        public CrystalRepair() {
            this.revivePersists = true;
        }

        @Override
        public boolean act() {
            if (Regeneration.regenOn() && this.target instanceof Hero) {
                for (Weapon w : ((Hero)this.target).belongings.getAllItems(Weapon.class)) {
                    if (!(w.enchantment instanceof Crystal)) continue;
                    ((Crystal)w.enchantment).repair(w, false, 0.2f);
                }
                DriedRose rose = ((Hero)this.target).belongings.getItem(DriedRose.class);
                if (rose != null && rose.ghostWeapon() != null && rose.ghostWeapon().enchantment instanceof Crystal) {
                    ((Crystal)rose.ghostWeapon().enchantment).repair(rose.ghostWeapon(), true, 0.2f);
                }
            }
            this.spend(1.0f);
            return true;
        }
    }
}

