/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EarthGuardianSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfLivingEarth
extends DamageWand {
    public WandOfLivingEarth() {
        this.image = ItemSpriteSheet.WAND_LIVING_EARTH;
    }

    @Override
    public int min(int lvl) {
        return 4;
    }

    @Override
    public int max(int lvl) {
        return 6 + 2 * lvl;
    }

    @Override
    public void onZap(Ballistica bolt) {
        int damage;
        Char ch = Actor.findChar(bolt.collisionPos);
        int armorToAdd = damage = this.damageRoll();
        EarthGuardian guardian = null;
        for (Mob m : Dungeon.level.mobs) {
            if (!(m instanceof EarthGuardian)) continue;
            guardian = (EarthGuardian)m;
            break;
        }
        if (Stasis.getStasisAlly() instanceof EarthGuardian) {
            guardian = (EarthGuardian)Stasis.getStasisAlly();
        }
        RockArmor buff = curUser.buff(RockArmor.class);
        if (!(guardian != null && ch == guardian || ch != null && ch.alignment != Char.Alignment.ALLY && (ch.alignment != Char.Alignment.NEUTRAL || ch instanceof Mimic))) {
            armorToAdd = 0;
        } else {
            if (buff == null && guardian == null) {
                buff = Buff.affect(curUser, RockArmor.class);
            }
            if (buff != null) {
                buff.addArmor(this.buffedLvl(), armorToAdd);
            }
        }
        if (guardian != null && guardian == ch) {
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
            guardian.setInfo(curUser, this.buffedLvl(), armorToAdd);
            this.wandProc(guardian, this.chargesPerCast());
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, 0.9f * Random.Float(0.87f, 1.15f));
        } else if (guardian == null && buff != null && buff.armor >= buff.armorToGuardian()) {
            guardian = new EarthGuardian();
            guardian.setInfo(curUser, this.buffedLvl(), buff.armor);
            if (buff.powerOfManyTurns > 0.0f) {
                Buff.affect(guardian, PowerOfMany.PowerBuff.class, buff.powerOfManyTurns);
            }
            if (ch != null) {
                ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, 5 + this.buffedLvl() / 2);
                this.wandProc(ch, this.chargesPerCast());
                ch.damage(damage, this);
                int closest = -1;
                boolean[] passable = Dungeon.level.passable;
                for (int n : PathFinder.NEIGHBOURS9) {
                    int c = bolt.collisionPos + n;
                    if (!passable[c] || Actor.findChar(c) != null || closest != -1 && !(Dungeon.level.trueDistance(c, WandOfLivingEarth.curUser.pos) < Dungeon.level.trueDistance(closest, WandOfLivingEarth.curUser.pos))) continue;
                    closest = c;
                }
                if (closest == -1) {
                    if (armorToAdd > 0) {
                        WandOfLivingEarth.curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
                    }
                    return;
                }
                guardian.pos = closest;
                GameScene.add(guardian, 1.0f);
                Dungeon.level.occupyCell(guardian);
                if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
                    guardian.aggro(ch);
                }
            } else {
                guardian.pos = bolt.collisionPos;
                GameScene.add(guardian, 1.0f);
                Dungeon.level.occupyCell(guardian);
            }
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
            buff.detach();
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, 0.9f * Random.Float(0.87f, 1.15f));
        } else if (ch != null) {
            ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, 5 + this.buffedLvl() / 2);
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(damage, this);
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, 0.8f * Random.Float(0.87f, 1.15f));
            if (guardian == null) {
                if (armorToAdd > 0) {
                    WandOfLivingEarth.curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
                }
            } else {
                if (guardian.sprite != null) {
                    guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
                }
                guardian.setInfo(curUser, this.buffedLvl(), armorToAdd);
                if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
                    guardian.aggro(ch);
                }
            }
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(16 + 8 * level);
    }

    @Override
    public String upgradeStat3(int level) {
        if (Dungeon.isChallenged(2)) {
            return level + "-" + (2 + level);
        }
        return level + "-" + (3 + 3 * level);
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(WandOfLivingEarth.curUser.sprite.parent, 9, WandOfLivingEarth.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        EarthGuardian guardian = null;
        for (Mob m : Dungeon.level.mobs) {
            if (!(m instanceof EarthGuardian)) continue;
            guardian = (EarthGuardian)m;
            break;
        }
        int armor = Math.round((float)damage * 0.33f * WandOfLivingEarth.procChanceMultiplier(attacker));
        if (guardian != null) {
            guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
            guardian.setInfo(Dungeon.hero, this.buffedLvl(), armor);
        } else {
            attacker.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + this.buffedLvl() / 2);
            Buff.affect(attacker, RockArmor.class).addArmor(this.buffedLvl(), armor);
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        if (Random.Int(10) == 0) {
            particle.color(ColorMath.random(16774504, 8419610));
        } else {
            particle.color(ColorMath.random(0x805500, 3351808));
        }
        particle.am = 1.0f;
        particle.setLifespan(2.0f);
        particle.setSize(1.0f, 2.0f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11.0f);
        particle.x -= dst;
        particle.y += dst;
    }

    public static class EarthGuardian
    extends NPC {
        private int wandLevel;
        private static final String DEFENSE = "defense";
        private static final String WAND_LEVEL = "wand_level";

        public EarthGuardian() {
            this.spriteClass = EarthGuardianSprite.class;
            this.alignment = Char.Alignment.ALLY;
            this.state = this.HUNTING;
            this.intelligentAlly = true;
            this.properties.add(Char.Property.INORGANIC);
            this.WANDERING = new Wandering();
            this.actPriority = -19;
            this.HT = 0;
            this.HP = 0;
            this.wandLevel = -1;
            this.immunities.add(AllyBuff.class);
        }

        public void setInfo(Hero hero, int wandLevel, int healthToAdd) {
            if (wandLevel > this.wandLevel) {
                this.wandLevel = wandLevel;
                this.HT = 16 + 8 * wandLevel;
            }
            if (this.HP != 0 && this.sprite != null) {
                this.sprite.showStatusWithIcon(65280, Integer.toString(healthToAdd), FloatingText.HEALING, new Object[0]);
            }
            this.HP = Math.min(this.HT, this.HP + healthToAdd);
            this.defenseSkill = (hero.lvl + 4) / 2;
        }

        @Override
        public int attackSkill(Char target) {
            return 2 * this.defenseSkill + 5;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            if (enemy instanceof Mob) {
                ((Mob)enemy).aggro(this);
            }
            return super.attackProc(enemy, damage);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(2, 4 + Dungeon.scalingDepth() / 2);
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            if (Dungeon.isChallenged(2)) {
                return dr + Random.NormalIntRange(this.wandLevel, 2 + this.wandLevel);
            }
            return dr + Random.NormalIntRange(this.wandLevel, 3 + 3 * this.wandLevel);
        }

        @Override
        public String description() {
            Object desc = Messages.get(this, "desc", new Object[0]);
            if (Actor.chars().contains(this)) {
                desc = Dungeon.isChallenged(2) ? (String)desc + "\n\n" + Messages.get(this, "wand_info", this.wandLevel, 2 + this.wandLevel) : (String)desc + "\n\n" + Messages.get(this, "wand_info", this.wandLevel, 3 + 3 * this.wandLevel);
            }
            return desc;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DEFENSE, this.defenseSkill);
            bundle.put(WAND_LEVEL, this.wandLevel);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.defenseSkill = bundle.getInt(DEFENSE);
            this.wandLevel = bundle.getInt(WAND_LEVEL);
        }

        private class Wandering
        extends Mob.Wandering {
            private Wandering() {
            }

            @Override
            public boolean act(boolean enemyInFOV, boolean justAlerted) {
                if (!enemyInFOV) {
                    Buff.affect(Dungeon.hero, RockArmor.class).addArmor(EarthGuardian.this.wandLevel, EarthGuardian.this.HP);
                    if (EarthGuardian.this.buff(PowerOfMany.PowerBuff.class) != null) {
                        Buff.affect((Char)Dungeon.hero, RockArmor.class).powerOfManyTurns = EarthGuardian.this.buff(PowerOfMany.PowerBuff.class).cooldown() + 1.0f;
                    }
                    Dungeon.hero.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + EarthGuardian.this.wandLevel / 2);
                    EarthGuardian.this.destroy();
                    EarthGuardian.this.sprite.die();
                    return true;
                }
                return super.act(enemyInFOV, justAlerted);
            }
        }
    }

    public static class RockArmor
    extends Buff {
        private int wandLevel;
        private int armor;
        private float powerOfManyTurns;
        private static final String WAND_LEVEL = "wand_level";
        private static final String ARMOR = "armor";
        private static final String POWER_TURNS = "power_turns";

        public RockArmor() {
            this.type = Buff.buffType.POSITIVE;
            this.powerOfManyTurns = 0.0f;
        }

        @Override
        public boolean act() {
            if (this.powerOfManyTurns > 0.0f) {
                this.powerOfManyTurns -= 1.0f;
                if (this.powerOfManyTurns <= 0.0f) {
                    this.powerOfManyTurns = 0.0f;
                    BuffIndicator.refreshHero();
                }
            }
            this.spend(1.0f);
            return true;
        }

        private void addArmor(int wandLevel, int toAdd) {
            this.wandLevel = Math.max(this.wandLevel, wandLevel);
            this.armor += toAdd;
            this.armor = Math.min(this.armor, 2 * this.armorToGuardian());
        }

        private int armorToGuardian() {
            return 8 + this.wandLevel * 4;
        }

        public int absorb(int damage) {
            int block = damage - damage / 2;
            if (this.armor <= block) {
                this.detach();
                return damage - this.armor;
            }
            this.armor -= block;
            return damage - block;
        }

        public boolean isEmpowered() {
            return this.powerOfManyTurns > 0.0f;
        }

        @Override
        public int icon() {
            return 20;
        }

        @Override
        public void tintIcon(Image icon) {
            if (this.isEmpowered()) {
                icon.hardlight(1.8f, 1.8f, 0.6f);
            } else {
                icon.brightness(0.6f);
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (float)(this.armorToGuardian() - this.armor) / (float)this.armorToGuardian());
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.armor);
        }

        @Override
        public String desc() {
            Object desc = Messages.get(this, "desc", this.armor, this.armorToGuardian());
            if (this.isEmpowered()) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_many", (int)this.powerOfManyTurns);
            }
            return desc;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WAND_LEVEL, this.wandLevel);
            bundle.put(ARMOR, this.armor);
            bundle.put(POWER_TURNS, this.powerOfManyTurns);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.wandLevel = bundle.getInt(WAND_LEVEL);
            this.armor = bundle.getInt(ARMOR);
            this.powerOfManyTurns = bundle.getFloat(POWER_TURNS);
        }
    }
}

