/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;

public class Feint
extends ArmorAbility {
    public Feint() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    public int icon() {
        return 30;
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, final Hero hero, final Integer target) {
        if (target == null) {
            return;
        }
        if (!Dungeon.level.adjacent(hero.pos, target)) {
            GLog.w(Messages.get(this, "too_far", new Object[0]), new Object[0]);
            return;
        }
        if (Dungeon.hero.rooted) {
            PixelScene.shake(1.0f, 1.0f);
            GLog.w(Messages.get(this, "bad_location", new Object[0]), new Object[0]);
            return;
        }
        if (Dungeon.level.solid[target] || Actor.findChar(target) != null) {
            GLog.w(Messages.get(this, "bad_location", new Object[0]), new Object[0]);
            return;
        }
        hero.busy();
        Sample.INSTANCE.play("sounds/miss.mp3");
        hero.sprite.jump(hero.pos, target, 0.0f, 0.1f, new Callback(){

            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == 6) {
                    Door.leave(hero.pos);
                }
                hero.pos = target;
                Dungeon.level.occupyCell(hero);
                Invisibility.dispel();
                hero.next();
            }
        });
        hero.spend(1.0f);
        final AfterImage image = new AfterImage();
        image.pos = hero.pos;
        GameScene.add(image);
        image.syncToHero(hero);
        Char enemyTarget = TargetHealthIndicator.instance.target();
        final int imageAttackPos = enemyTarget != null && enemyTarget.alignment == Char.Alignment.ENEMY ? enemyTarget.pos : image.pos + (image.pos - target);
        hero.sprite.parent.add(new Delayer(0.0f){

            @Override
            protected void onComplete() {
                image.sprite.attack(imageAttackPos, new Callback(){

                    @Override
                    public void call() {
                    }
                });
            }
        });
        for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
            if ((!m.isTargeting(hero) || m.state != m.HUNTING) && (m.alignment != Char.Alignment.ENEMY || m.state == m.PASSIVE || m.state == m.SLEEPING || Dungeon.level.distance(m.pos, image.pos) > 2)) continue;
            m.aggro(image);
        }
        armor.charge -= this.chargeUse(hero);
        Item.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.FEIGNED_RETREAT, Talent.EXPOSE_WEAKNESS, Talent.COUNTER_ABILITY, Talent.HEROIC_ENERGY};
    }

    public static class AfterImage
    extends Mob {
        public AfterImage() {
            this.spriteClass = AfterImageSprite.class;
            this.defenseSkill = 0;
            this.properties.add(Char.Property.IMMOVABLE);
            this.alignment = Char.Alignment.ALLY;
            this.state = this.PASSIVE;
            this.HT = 1;
            this.HP = 1;
            this.actPriority = 1;
            this.immunities.addAll(new BlobImmunity().immunities());
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public boolean canInteract(Char c) {
            return false;
        }

        @Override
        protected boolean act() {
            this.destroy();
            this.sprite.die();
            return true;
        }

        public void syncToHero(Hero hero) {
            if (this.cooldown() != hero.cooldown()) {
                this.spendConstant(hero.cooldown() - this.cooldown());
            }
        }

        @Override
        public void damage(int dmg, Object src) {
        }

        @Override
        public int defenseSkill(Char enemy) {
            if (enemy.alignment == Char.Alignment.ENEMY) {
                if (enemy instanceof Mob) {
                    ((Mob)enemy).clearEnemy();
                }
                Buff.affect(enemy, FeintConfusion.class, 1.0f);
                if (enemy.sprite != null) {
                    enemy.sprite.showLost();
                }
                if (Dungeon.hero.hasTalent(Talent.FEIGNED_RETREAT)) {
                    Buff.prolong(Dungeon.hero, Haste.class, 2.0f * (float)Dungeon.hero.pointsInTalent(Talent.FEIGNED_RETREAT));
                }
                if (Dungeon.hero.hasTalent(Talent.EXPOSE_WEAKNESS)) {
                    Buff.prolong(enemy, Vulnerable.class, 2.0f * (float)Dungeon.hero.pointsInTalent(Talent.EXPOSE_WEAKNESS));
                    Buff.prolong(enemy, Weakness.class, 2.0f * (float)Dungeon.hero.pointsInTalent(Talent.EXPOSE_WEAKNESS));
                }
                if (Dungeon.hero.hasTalent(Talent.COUNTER_ABILITY)) {
                    Buff.prolong(Dungeon.hero, Talent.CounterAbilityTacker.class, 3.0f);
                }
            }
            return 0;
        }

        @Override
        public boolean add(Buff buff) {
            return false;
        }

        @Override
        public CharSprite sprite() {
            CharSprite s = super.sprite();
            ((AfterImageSprite)s).updateArmor();
            return s;
        }

        public static class AfterImageSprite
        extends MirrorSprite {
            @Override
            public void updateArmor() {
                this.updateArmor(6);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                this.alpha(0.6f);
            }

            @Override
            public void die() {
                if (this.parent != null) {
                    this.parent.add(new AlphaTweener(this, 0.0f, 3.0f){

                        @Override
                        protected void onComplete() {
                            this.killAndErase();
                        }
                    });
                }
            }
        }

        public static class FeintConfusion
        extends FlavourBuff {
        }
    }
}

