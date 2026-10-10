/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Statue
extends Mob {
    protected Weapon weapon;
    public boolean levelGenStatue;
    private static final String WEAPON = "weapon";

    public Statue() {
        this.spriteClass = StatueSprite.class;
        this.EXP = 0;
        this.state = this.PASSIVE;
        this.properties.add(Char.Property.INORGANIC);
        this.levelGenStatue = true;
        this.resistances.add(Grim.class);
        this.HP = this.HT = 15 + Dungeon.depth * 5;
        this.defenseSkill = 4 + Dungeon.depth;
    }

    public void createWeapon(boolean useDecks) {
        this.weapon = useDecks ? (MeleeWeapon)Generator.random(Generator.Category.WEAPON) : (MeleeWeapon)Generator.randomUsingDefaults(Generator.Category.WEAPON);
        this.levelGenStatue = useDecks;
        this.weapon.cursed = false;
        this.weapon.enchant(Weapon.Enchantment.random(new Class[0]));
    }

    public Weapon weapon() {
        return this.weapon;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WEAPON, this.weapon);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.weapon = (Weapon)bundle.get(WEAPON);
    }

    @Override
    public int damageRoll() {
        return this.weapon.damageRoll(this);
    }

    @Override
    public int attackSkill(Char target) {
        return (int)((float)(9 + Dungeon.depth) * this.weapon.accuracyFactor(this, target));
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * this.weapon.delayFactor(this);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || this.weapon.canReach(this, enemy.pos);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, Dungeon.depth + this.weapon.defenseFactor(this));
    }

    @Override
    public boolean add(Buff buff) {
        if (super.add(buff)) {
            if (this.state == this.PASSIVE && buff.type == Buff.buffType.NEGATIVE) {
                this.state = this.HUNTING;
            }
            return true;
        }
        return false;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (this.state == this.PASSIVE) {
            this.state = this.HUNTING;
        }
        super.damage(dmg, src);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        damage = this.weapon.proc(this, enemy, damage);
        if (!enemy.isAlive() && enemy == Dungeon.hero) {
            Dungeon.fail(this);
            GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
        }
        return damage;
    }

    @Override
    public void beckon(int cell) {
        if (this.state != this.PASSIVE) {
            super.beckon(cell);
        }
    }

    @Override
    public void die(Object cause) {
        if (this.buff(Corrupting.CorruptingTracker.class) == null) {
            this.weapon.identify(false);
            Dungeon.level.drop((Item)this.weapon, (int)this.pos).sprite.drop();
        }
        super.die(cause);
    }

    @Override
    public Notes.Landmark landmark() {
        return this.levelGenStatue ? Notes.Landmark.STATUE : null;
    }

    @Override
    public void destroy() {
        if (this.landmark() != null) {
            Notes.remove(this.landmark());
        }
        super.destroy();
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public String description() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        if (this.weapon != null) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_weapon", this.weapon.name());
        }
        return desc;
    }

    public static Statue random() {
        return Statue.random(true);
    }

    public static Statue random(boolean useDecks) {
        float altChance = 0.1f * RatSkull.exoticChanceMultiplier();
        if (altChance > 0.1f) {
            altChance = (altChance + 0.1f) / 2.0f;
        }
        Statue statue = Random.Float() < altChance ? new ArmoredStatue() : new Statue();
        statue.createWeapon(useDecks);
        return statue;
    }
}

