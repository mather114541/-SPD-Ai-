/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ArmoredStatue
extends Statue {
    protected Armor armor;
    private static final String ARMOR = "armor";

    public ArmoredStatue() {
        this.spriteClass = StatueSprite.class;
        this.HP = this.HT = 30 + Dungeon.depth * 10;
    }

    @Override
    public void createWeapon(boolean useDecks) {
        super.createWeapon(useDecks);
        this.armor = Generator.randomArmor();
        this.armor.cursed = false;
        this.armor.inscribe(Armor.Glyph.random(new Class[0]));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARMOR, this.armor);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.armor = (Armor)bundle.get(ARMOR);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(this.armor.DRMin(), this.armor.DRMax());
    }

    public Armor armor() {
        return this.armor;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        damage = this.armor.proc(enemy, this, damage);
        return super.defenseProc(enemy, damage);
    }

    @Override
    public int glyphLevel(Class<? extends Armor.Glyph> cls) {
        if (this.armor != null && this.armor.hasGlyph(cls, this)) {
            return Math.max(super.glyphLevel(cls), this.armor.buffedLvl());
        }
        return super.glyphLevel(cls);
    }

    @Override
    public CharSprite sprite() {
        CharSprite sprite = super.sprite();
        if (this.armor != null) {
            ((StatueSprite)sprite).setArmor(this.armor.tier);
        } else {
            ((StatueSprite)sprite).setArmor(3);
        }
        return sprite;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return Math.round(this.armor.evasionFactor(this, super.defenseSkill(enemy)));
    }

    @Override
    public void die(Object cause) {
        if (this.buff(Corrupting.CorruptingTracker.class) == null) {
            this.armor.identify(false);
            Dungeon.level.drop((Item)this.armor, (int)this.pos).sprite.drop();
        }
        super.die(cause);
    }

    @Override
    public String description() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        if (this.weapon != null && this.armor != null) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_arm_wep", this.weapon.name(), this.armor.name());
        }
        return desc;
    }
}

