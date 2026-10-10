/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfWarding
extends Wand {
    private boolean wardAvailable;

    public WandOfWarding() {
        this.image = ItemSpriteSheet.WAND_WARDING;
        this.usesTargeting = false;
        this.wardAvailable = true;
    }

    @Override
    public int collisionProperties(int target) {
        if (this.cursed) {
            return super.collisionProperties(target);
        }
        if (!Dungeon.level.heroFOV[target]) {
            return 7;
        }
        return 1;
    }

    @Override
    public void execute(Hero hero, String action) {
        this.usesTargeting = this.cursed && this.cursedKnown;
        super.execute(hero, action);
    }

    @Override
    public boolean tryToZap(Hero owner, int target) {
        int currentWardEnergy = 0;
        for (Char char_ : Actor.chars()) {
            if (!(char_ instanceof Ward)) continue;
            currentWardEnergy += ((Ward)char_).tier;
        }
        if (Stasis.getStasisAlly() instanceof Ward) {
            currentWardEnergy += ((Ward)Stasis.getStasisAlly()).tier;
        }
        int maxWardEnergy = 0;
        for (Buff buff : curUser.buffs()) {
            if (!(buff instanceof Wand.Charger) || !(((Wand.Charger)buff).wand() instanceof WandOfWarding)) continue;
            maxWardEnergy += 2 + ((Wand.Charger)buff).wand().level();
        }
        this.wardAvailable = currentWardEnergy < maxWardEnergy;
        Char char_ = Actor.findChar(target);
        if (char_ instanceof Ward) {
            if (!this.wardAvailable && ((Ward)char_).tier <= 3) {
                GLog.w(Messages.get(this, "no_more_wards", new Object[0]), new Object[0]);
                return false;
            }
        } else if (currentWardEnergy + 1 > maxWardEnergy) {
            GLog.w(Messages.get(this, "no_more_wards", new Object[0]), new Object[0]);
            return false;
        }
        return super.tryToZap(owner, target);
    }

    @Override
    public void onZap(Ballistica bolt) {
        int target = bolt.collisionPos;
        Char ch = Actor.findChar(target);
        if (ch != null && !(ch instanceof Ward)) {
            if (bolt.dist > 1) {
                target = bolt.path.get(bolt.dist - 1);
            }
            if ((ch = Actor.findChar(target)) != null && !(ch instanceof Ward)) {
                GLog.w(Messages.get(this, "bad_location", new Object[0]), new Object[0]);
                Dungeon.level.pressCell(bolt.collisionPos);
                return;
            }
        }
        if (ch != null) {
            if (ch instanceof Ward) {
                if (this.wardAvailable) {
                    ((Ward)ch).upgrade(this.buffedLvl());
                } else {
                    ((Ward)ch).wandHeal(this.buffedLvl());
                }
                ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward)ch).tier);
            } else {
                GLog.w(Messages.get(this, "bad_location", new Object[0]), new Object[0]);
                Dungeon.level.pressCell(target);
            }
        } else if (!Dungeon.level.passable[target]) {
            GLog.w(Messages.get(this, "bad_location", new Object[0]), new Object[0]);
            Dungeon.level.pressCell(target);
        } else {
            Ward ward = new Ward();
            ward.pos = target;
            ward.wandLevel = this.buffedLvl();
            GameScene.add(ward, 1.0f);
            Dungeon.level.occupyCell(ward);
            ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
            Dungeon.level.pressCell(target);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile m = MagicMissile.boltFromChar(WandOfWarding.curUser.sprite.parent, 10, WandOfWarding.curUser.sprite, bolt.collisionPos, callback);
        if (bolt.dist > 10) {
            m.setSpeed(bolt.dist * 20);
        }
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        int level = Math.max(0, staff.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 5.0f) * WandOfWarding.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof Ward)) continue;
                ((Ward)ch).wandHeal(staff.buffedLvl(), powerMulti);
                ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward)ch).tier);
            }
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x8822FF);
        particle.am = 0.3f;
        particle.setLifespan(3.0f);
        particle.speed.polar(Random.Float(6.283185f), 0.3f);
        particle.setSize(1.0f, 2.0f);
        particle.radiateXY(2.5f);
    }

    @Override
    public String statsDesc() {
        if (this.levelKnown) {
            return Messages.get(this, "stats_desc", this.level() + 2);
        }
        return Messages.get(this, "stats_desc", 2);
    }

    @Override
    public String upgradeStat1(int level) {
        return 2 + level + "-" + (8 + 4 * level);
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(level + 2);
    }

    public static class Ward
    extends NPC {
        public int tier = 1;
        private int wandLevel = 1;
        public int totalZaps = 0;
        private static final String TIER = "tier";
        private static final String WAND_LEVEL = "wand_level";
        private static final String TOTAL_ZAPS = "total_zaps";

        public Ward() {
            this.spriteClass = WardSprite.class;
            this.alignment = Char.Alignment.ALLY;
            this.properties.add(Char.Property.IMMOVABLE);
            this.properties.add(Char.Property.INORGANIC);
            this.viewDistance = 4;
            this.state = this.WANDERING;
            this.immunities.add(Sleep.class);
            this.immunities.add(Terror.class);
            this.immunities.add(Dread.class);
            this.immunities.add(Vertigo.class);
            this.immunities.add(AllyBuff.class);
        }

        @Override
        public String name() {
            return Messages.get(this, "name_" + this.tier, new Object[0]);
        }

        public void upgrade(int wandLevel) {
            if (this.wandLevel < wandLevel) {
                this.wandLevel = wandLevel;
            }
            switch (this.tier) {
                default: {
                    break;
                }
                case 3: {
                    this.HT = 35;
                    this.HP = 15 + (5 - this.totalZaps) * 4;
                    break;
                }
                case 4: {
                    this.HT = 54;
                    this.HP += 19;
                    break;
                }
                case 5: {
                    this.HT = 84;
                    this.HP += 30;
                    break;
                }
                case 6: {
                    this.wandHeal(wandLevel);
                }
            }
            if (Actor.chars().contains(this) && this.tier >= 3) {
                Bestiary.setSeen(WardSentry.class);
            }
            if (this.tier < 6) {
                ++this.tier;
                ++this.viewDistance;
                if (this.sprite != null) {
                    ((WardSprite)this.sprite).updateTier(this.tier);
                    this.sprite.place(this.pos);
                }
                GameScene.updateFog(this.pos, this.viewDistance + 1);
            }
        }

        public void wandHeal(int wandLevel) {
            this.wandHeal(wandLevel, 1.0f);
        }

        public void wandHeal(int wandLevel, float healFactor) {
            int heal;
            if (this.wandLevel < wandLevel) {
                this.wandLevel = wandLevel;
            }
            switch (this.tier) {
                default: {
                    return;
                }
                case 2: {
                    heal = Math.round(1.0f * healFactor);
                    break;
                }
                case 3: {
                    heal = Math.round((float)Random.IntRange(1, 2) * healFactor);
                    break;
                }
                case 4: {
                    heal = Math.round(9.0f * healFactor);
                    break;
                }
                case 5: {
                    heal = Math.round(12.0f * healFactor);
                    break;
                }
                case 6: {
                    heal = Math.round(16.0f * healFactor);
                }
            }
            if (this.tier <= 3) {
                this.totalZaps = Math.max(0, this.totalZaps - heal);
            } else {
                this.HP = Math.min(this.HT, this.HP + heal);
            }
            if (this.sprite != null) {
                this.sprite.showStatusWithIcon(65280, Integer.toString(heal), FloatingText.HEALING, new Object[0]);
            }
        }

        @Override
        public int defenseSkill(Char enemy) {
            if (this.tier > 3) {
                this.defenseSkill = 4 + Dungeon.scalingDepth();
            }
            return super.defenseSkill(enemy);
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            if (this.tier > 3) {
                return dr + Math.round((float)Random.NormalIntRange(0, 3 + Dungeon.scalingDepth() / 2) / (7.0f - (float)this.tier));
            }
            return dr;
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
        }

        @Override
        protected boolean doAttack(Char enemy) {
            boolean visible;
            boolean bl = visible = this.fieldOfView[this.pos] || this.fieldOfView[enemy.pos];
            if (visible) {
                this.sprite.zap(enemy.pos);
            } else {
                this.zap();
            }
            return !visible;
        }

        private void zap() {
            this.spend(1.0f);
            int dmg = Hero.heroDamageIntRange(2 + this.wandLevel, 8 + 4 * this.wandLevel);
            Char enemy = this.enemy;
            enemy.damage(dmg, this);
            if (enemy.isAlive()) {
                Wand.wandProc(enemy, this.wandLevel, 1);
            }
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Badges.validateDeathFromFriendlyMagic();
                GLog.n(Messages.capitalize(Messages.get(this, "kill", this.name())), new Object[0]);
                Dungeon.fail(WandOfWarding.class);
            }
            ++this.totalZaps;
            switch (this.tier) {
                default: {
                    if (this.totalZaps < 2 * this.tier - 1) break;
                    this.die(this);
                    break;
                }
                case 4: {
                    this.damage(5, this);
                    break;
                }
                case 5: {
                    this.damage(6, this);
                    break;
                }
                case 6: {
                    this.damage(7, this);
                }
            }
        }

        public void onZapComplete() {
            this.zap();
            this.next();
        }

        @Override
        protected boolean getCloser(int target) {
            return false;
        }

        @Override
        protected boolean getFurther(int target) {
            return false;
        }

        @Override
        public CharSprite sprite() {
            WardSprite sprite = (WardSprite)super.sprite();
            sprite.linkVisuals(this);
            return sprite;
        }

        @Override
        public void updateSpriteState() {
            super.updateSpriteState();
            ((WardSprite)this.sprite).updateTier(this.tier);
            this.sprite.place(this.pos);
        }

        @Override
        public void destroy() {
            super.destroy();
            Dungeon.observe();
            GameScene.updateFog(this.pos, this.viewDistance + 1);
        }

        @Override
        public boolean canInteract(Char c) {
            return true;
        }

        @Override
        public boolean interact(Char c) {
            if (c != Dungeon.hero) {
                return true;
            }
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    GameScene.show(new WndOptions(this.sprite(), Messages.get(this, "dismiss_title", new Object[0]), Messages.get(this, "dismiss_body", new Object[0]), new String[]{Messages.get(this, "dismiss_confirm", new Object[0]), Messages.get(this, "dismiss_cancel", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                this.die(null);
                            }
                        }
                    });
                }
            });
            return true;
        }

        @Override
        public String description() {
            if (!Actor.chars().contains(this)) {
                if (this.tier < 4) {
                    return Messages.get(this, "desc_generic_ward", new Object[0]);
                }
                return Messages.get(this, "desc_generic_sentry", new Object[0]);
            }
            return Messages.get(this, "desc_" + this.tier, 2 + this.wandLevel, 8 + 4 * this.wandLevel, this.tier);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TIER, this.tier);
            bundle.put(WAND_LEVEL, this.wandLevel);
            bundle.put(TOTAL_ZAPS, this.totalZaps);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.tier = bundle.getInt(TIER);
            this.viewDistance = 3 + this.tier;
            this.wandLevel = bundle.getInt(WAND_LEVEL);
            this.totalZaps = bundle.getInt(TOTAL_ZAPS);
        }

        public static class WardSentry
        extends Ward {
        }
    }
}

