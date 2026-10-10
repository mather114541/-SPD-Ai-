/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class CapeOfThorns
extends Artifact {
    public CapeOfThorns() {
        this.image = ItemSpriteSheet.ARTIFACT_CAPE;
        this.levelCap = 10;
        this.charge = 0;
        this.chargeCap = 100;
        this.cooldown = 0;
        this.defaultAction = "NONE";
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new Thorns();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cooldown == 0) {
            this.charge += Math.round(4.0f * amount);
            CapeOfThorns.updateQuickslot();
        }
        if (this.charge >= this.chargeCap) {
            target.buff(Thorns.class).proc(0, null, null);
        }
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        if (this.isEquipped(Dungeon.hero)) {
            desc = (String)desc + "\n\n";
            desc = this.cooldown == 0 ? (String)desc + Messages.get(this, "desc_inactive", new Object[0]) : (String)desc + Messages.get(this, "desc_active", new Object[0]);
        }
        return desc;
    }

    public class Thorns
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (CapeOfThorns.this.cooldown > 0) {
                --CapeOfThorns.this.cooldown;
                if (CapeOfThorns.this.cooldown == 0) {
                    GLog.w(Messages.get(this, "inert", new Object[0]), new Object[0]);
                }
                Item.updateQuickslot();
            }
            this.spend(1.0f);
            return true;
        }

        public int proc(int damage, Char attacker, Char defender) {
            if (CapeOfThorns.this.cooldown == 0) {
                CapeOfThorns.this.charge = (int)((double)CapeOfThorns.this.charge + (double)damage * (0.5 + (double)CapeOfThorns.this.level() * 0.05));
                if (CapeOfThorns.this.charge >= CapeOfThorns.this.chargeCap) {
                    CapeOfThorns.this.charge = 0;
                    CapeOfThorns.this.cooldown = 10 + CapeOfThorns.this.level();
                    GLog.p(Messages.get(this, "radiating", new Object[0]), new Object[0]);
                }
            }
            if (CapeOfThorns.this.cooldown != 0) {
                int deflected = Random.NormalIntRange(0, damage);
                damage -= deflected;
                if (attacker != null && Dungeon.level.adjacent(attacker.pos, defender.pos)) {
                    attacker.damage(deflected, this);
                }
                CapeOfThorns.this.exp += deflected;
                if (CapeOfThorns.this.exp >= (CapeOfThorns.this.level() + 1) * 5 && CapeOfThorns.this.level() < CapeOfThorns.this.levelCap) {
                    CapeOfThorns.this.exp -= (CapeOfThorns.this.level() + 1) * 5;
                    CapeOfThorns.this.upgrade();
                    Catalog.countUse(CapeOfThorns.class);
                    GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
                }
            }
            Item.updateQuickslot();
            return damage;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.dispTurns(CapeOfThorns.this.cooldown));
        }

        @Override
        public int icon() {
            if (CapeOfThorns.this.cooldown == 0) {
                return 127;
            }
            return 31;
        }

        @Override
        public void detach() {
            CapeOfThorns.this.cooldown = 0;
            CapeOfThorns.this.charge = 0;
            super.detach();
        }
    }
}

