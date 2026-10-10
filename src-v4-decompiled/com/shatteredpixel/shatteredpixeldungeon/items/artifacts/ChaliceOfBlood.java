/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ChaliceOfBlood
extends Artifact {
    public static final String AC_PRICK = "PRICK";

    public ChaliceOfBlood() {
        this.image = ItemSpriteSheet.ARTIFACT_CHALICE1;
        this.levelCap = 10;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && this.level() < this.levelCap && !this.cursed && !hero.isInvulnerable(this.getClass()) && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_PRICK);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_PRICK)) {
            int minDmg = this.minPrickDmg();
            int maxDmg = this.maxPrickDmg();
            int totalHeroHP = hero.HP + hero.shielding();
            float deathChance = 0.0f;
            if (totalHeroHP < maxDmg) {
                deathChance = (float)(maxDmg - totalHeroHP) / (float)(maxDmg - minDmg);
                if (deathChance < 0.5f) {
                    deathChance = (float)Math.pow(2.0f * deathChance, 2.0) / 2.0f;
                } else if (deathChance < 1.0f) {
                    deathChance = 1.0f - deathChance;
                    deathChance = (float)Math.pow(2.0f * deathChance, 2.0) / 2.0f;
                    deathChance = 1.0f - deathChance;
                } else {
                    deathChance = 1.0f;
                }
            }
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(this, "prick_warn", minDmg, maxDmg, Messages.decimalFormat("#.##", 100.0f * deathChance)), new String[]{Messages.get(this, "yes", new Object[0]), Messages.get(this, "no", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        ChaliceOfBlood.this.prick(Dungeon.hero);
                    }
                }
            });
        }
    }

    private int minPrickDmg() {
        return (int)Math.ceil(3.0f + 2.5f * (float)(this.level() * this.level()));
    }

    private int maxPrickDmg() {
        return (int)Math.floor(7.0f + 3.5f * (float)(this.level() * this.level()));
    }

    private void prick(Hero hero) {
        WandOfLivingEarth.RockArmor rockArmor;
        int damage = Random.NormalIntRange(this.minPrickDmg(), this.maxPrickDmg());
        Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
        if (armor != null) {
            damage = armor.absorb(damage);
        }
        if (hero.buff(MagicImmune.class) != null && hero.buff(HolyWard.HolyArmBuff.class) != null) {
            damage -= hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
        }
        if ((rockArmor = hero.buff(WandOfLivingEarth.RockArmor.class)) != null) {
            damage = rockArmor.absorb(damage);
        }
        hero.sprite.operate(hero.pos);
        hero.busy();
        hero.spend(1.0f);
        GLog.w(Messages.get(this, "onprick", new Object[0]), new Object[0]);
        if ((damage -= hero.drRoll()) <= 0) {
            damage = 1;
        } else {
            Sample.INSTANCE.play("sounds/cursed.mp3");
            hero.sprite.emitter().burst(ShadowParticle.CURSE, 4 + damage / 10);
        }
        hero.damage(damage, this);
        if (!hero.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
        } else {
            this.upgrade();
            Catalog.countUse(this.getClass());
        }
    }

    @Override
    public Item upgrade() {
        if (this.level() >= 6) {
            this.image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        } else if (this.level() >= 2) {
            this.image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        }
        return super.upgrade();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (this.level() >= 7) {
            this.image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        } else if (this.level() >= 3) {
            this.image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new chaliceRegen();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (target.isStarving()) {
            return;
        }
        float healDelay = 10.0f - (1.33f + (float)this.level() * 0.667f);
        float heal = 5.0f / (healDelay /= amount);
        if (Random.Float() < heal % 1.0f) {
            heal += 1.0f;
        }
        if (heal >= 1.0f && target.HP < target.HT) {
            target.HP = Math.min(target.HT, target.HP + (int)heal);
            target.sprite.showStatusWithIcon(65280, Integer.toString((int)heal), FloatingText.HEALING, new Object[0]);
            if (target.HP == target.HT && target instanceof Hero) {
                target.resting = false;
            }
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            desc = (String)desc + "\n\n";
            desc = this.cursed ? (String)desc + Messages.get(this, "desc_cursed", new Object[0]) : (this.level() == 0 ? (String)desc + Messages.get(this, "desc_1", new Object[0]) : (this.level() < this.levelCap ? (String)desc + Messages.get(this, "desc_2", new Object[0]) : (String)desc + Messages.get(this, "desc_3", new Object[0])));
        }
        return desc;
    }

    public class chaliceRegen
    extends Artifact.ArtifactBuff {
    }
}

