/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class MirrorImage
extends NPC {
    private Hero hero;
    private int heroID;
    public int armTier;
    private static final String HEROID = "hero_id";

    public MirrorImage() {
        this.spriteClass = MirrorSprite.class;
        this.HT = 1;
        this.HP = 1;
        this.defenseSkill = 1;
        this.alignment = Char.Alignment.ALLY;
        this.state = this.HUNTING;
        this.actPriority = -19;
        this.immunities.add(ToxicGas.class);
        this.immunities.add(CorrosiveGas.class);
        this.immunities.add(Burning.class);
        this.immunities.add(AllyBuff.class);
    }

    @Override
    protected boolean act() {
        if (this.hero == null) {
            this.hero = (Hero)Actor.findById(this.heroID);
            if (this.hero == null) {
                this.die(null);
                this.sprite.killAndErase();
                return true;
            }
        }
        if (this.hero.tier() != this.armTier) {
            this.armTier = this.hero.tier();
            ((MirrorSprite)this.sprite).updateArmor(this.armTier);
        }
        return super.act();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEROID, this.heroID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.heroID = bundle.getInt(HEROID);
    }

    public void duplicate(Hero hero) {
        this.hero = hero;
        this.heroID = this.hero.id();
        Buff.affect(this, MirrorInvis.class, 32767.0f);
    }

    @Override
    public int damageRoll() {
        int damage = this.hero.belongings.weapon() != null ? this.hero.belongings.weapon().damageRoll(this) : this.hero.damageRoll();
        return (damage + 1) / 2;
    }

    @Override
    public int attackSkill(Char target) {
        int attackSkill = 9 + this.hero.lvl;
        attackSkill = (int)((float)attackSkill * RingOfAccuracy.accuracyMultiplier(this.hero));
        if (this.hero.belongings.attackingWeapon() != null) {
            attackSkill = (int)((float)attackSkill * this.hero.belongings.attackingWeapon().accuracyFactor(this, target));
        }
        return attackSkill;
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.hero != null) {
            int baseEvasion = 4 + this.hero.lvl;
            int heroEvasion = (int)((float)(4 + this.hero.lvl) * RingOfEvasion.evasionMultiplier(this.hero));
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        }
        return 0;
    }

    @Override
    public float attackDelay() {
        return this.hero.attackDelay();
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || this.hero.belongings.weapon() != null && this.hero.belongings.weapon().canReach(this, enemy.pos);
    }

    @Override
    public int drRoll() {
        int dr = super.drRoll();
        if (this.hero != null && this.hero.belongings.weapon() != null) {
            return dr + Random.NormalIntRange(0, this.hero.belongings.weapon().defenseFactor(this) / 2);
        }
        return dr;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        boolean wasEnemy;
        damage = super.attackProc(enemy, damage);
        MirrorInvis buff = this.buff(MirrorInvis.class);
        if (buff != null) {
            buff.detach();
        }
        if (enemy instanceof Mob) {
            ((Mob)enemy).aggro(this);
        }
        if (this.hero.belongings.weapon() != null) {
            damage = this.hero.belongings.weapon().proc(this, enemy, damage);
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(this);
                GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
            }
            return damage;
        }
        boolean bl = wasEnemy = enemy.alignment == Char.Alignment.ENEMY;
        if (this.hero.buff(BodyForm.BodyFormBuff.class) != null && this.hero.buff(BodyForm.BodyFormBuff.class).enchant() != null) {
            damage = this.hero.buff(BodyForm.BodyFormBuff.class).enchant().proc(new WornShortsword(), this, enemy, damage);
        }
        if (!(wasEnemy && enemy.alignment != Char.Alignment.ENEMY || this.hero.buff(HolyWeapon.HolyWepBuff.class) == null)) {
            int dmg = this.hero.subClass == HeroSubClass.PALADIN ? 6 : 2;
            enemy.damage(Math.round((float)dmg * Weapon.Enchantment.genericProcChanceMultiplier(this)), HolyWeapon.INSTANCE);
        }
        return damage;
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();
        this.hero = (Hero)Actor.findById(this.heroID);
        this.armTier = this.hero != null ? this.hero.tier() : 1;
        ((MirrorSprite)s).updateArmor(this.armTier);
        return s;
    }

    public static class MirrorInvis
    extends Invisibility {
        public MirrorInvis() {
            this.announced = false;
        }

        @Override
        public int icon() {
            return 127;
        }
    }
}

