/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BruteSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.Iterator;

public class Brute
extends Mob {
    protected boolean hasRaged;
    private BruteRage rage;
    private static final String HAS_RAGED = "has_raged";

    public Brute() {
        this.spriteClass = BruteSprite.class;
        this.HT = 40;
        this.HP = 40;
        this.defenseSkill = 15;
        this.EXP = 8;
        this.maxLvl = 16;
        this.loot = Gold.class;
        this.lootChance = 0.5f;
        this.hasRaged = false;
    }

    @Override
    public int damageRoll() {
        return this.buff(BruteRage.class) != null ? Random.NormalIntRange(15, 40) : Random.NormalIntRange(5, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 8);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (cause == Chasm.class) {
            this.hasRaged = true;
        }
    }

    @Override
    public boolean isAlive() {
        if (super.isAlive()) {
            return true;
        }
        if (!this.hasRaged) {
            this.triggerEnrage();
        }
        if (this.rage == null) {
            Iterator<BruteRage> iterator = this.buffs(BruteRage.class).iterator();
            while (iterator.hasNext()) {
                BruteRage b;
                this.rage = b = iterator.next();
            }
        }
        return this.rage != null && this.rage.shielding() > 0;
    }

    protected void triggerEnrage() {
        this.rage = Buff.affect(this, BruteRage.class);
        this.rage.setShield(this.HT / 2 + 4);
        this.sprite.showStatusWithIcon(65280, Integer.toString(this.HT / 2), FloatingText.SHIELDING, new Object[0]);
        if (Dungeon.level.heroFOV[this.pos]) {
            SpellSprite.show(this, 3);
        }
        this.spend(1.0f);
        this.hasRaged = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HAS_RAGED, this.hasRaged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.hasRaged = bundle.getBoolean(HAS_RAGED);
    }

    public static class BruteRage
    extends ShieldBuff {
        public BruteRage() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            if (this.target.HP > 0) {
                this.detach();
                return true;
            }
            this.absorbDamage(Math.round(4.0f * AscensionChallenge.statModifier(this.target)));
            if (this.shielding() <= 0) {
                this.target.die(null);
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void detach() {
            super.detach();
            this.decShield(this.shielding());
        }

        @Override
        public int icon() {
            return 18;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.shielding());
        }
    }
}

