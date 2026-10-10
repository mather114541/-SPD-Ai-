/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class VaultShaman
extends Shaman {
    int type;
    public static final String TYPE = "type";

    public VaultShaman() {
        this.activateSteathGameplayBehaviour();
        this.spriteClass = ShamanSprite.Vault.class;
        this.defenseSkill = 18;
        this.maxLvl = 30;
        this.EXP = 0;
        this.loot = DwarfToken.class;
        this.lootChance = 1.0f;
        this.type = Random.Int(5);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public float lootChance() {
        return 1.0f;
    }

    @Override
    public Item createLoot() {
        return new DwarfToken();
    }

    @Override
    protected void debuff(Char enemy) {
        switch (this.type) {
            case 0: 
            case 1: {
                Buff.prolong(enemy, Weakness.class, 20.0f);
                break;
            }
            case 2: 
            case 3: {
                Buff.prolong(enemy, Vulnerable.class, 20.0f);
                break;
            }
            case 4: {
                Buff.prolong(enemy, Hex.class, 30.0f);
            }
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 25);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + 5;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TYPE, this.type);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.type = bundle.getInt(TYPE);
    }
}

