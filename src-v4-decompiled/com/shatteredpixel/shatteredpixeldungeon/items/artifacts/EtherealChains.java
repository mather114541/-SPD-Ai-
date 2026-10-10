/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class EtherealChains
extends Artifact {
    public static final String AC_CAST = "CAST";
    public CellSelector.Listener caster;

    public EtherealChains() {
        this.image = ItemSpriteSheet.ARTIFACT_CHAINS;
        this.levelCap = 5;
        this.exp = 0;
        this.charge = 5;
        this.defaultAction = AC_CAST;
        this.usesTargeting = true;
        this.caster = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer target) {
                if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])) {
                    PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
                    if (!(Dungeon.level instanceof MiningLevel) && PathFinder.distance[curUser.pos] == Integer.MAX_VALUE) {
                        GLog.w(Messages.get(EtherealChains.class, "cant_reach", new Object[0]), new Object[0]);
                        return;
                    }
                    Ballistica chain = new Ballistica(curUser.pos, target, 1);
                    if (Actor.findChar(chain.collisionPos) != null) {
                        EtherealChains.this.chainEnemy(chain, curUser, Actor.findChar(chain.collisionPos));
                    } else {
                        EtherealChains.this.chainLocation(chain, curUser);
                    }
                }
            }

            @Override
            public String prompt() {
                return Messages.get(EtherealChains.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && this.charge > 0 && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_CAST);
        }
        return actions;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return dst;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_CAST)) {
            curUser = hero;
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else if (this.charge < 1) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else if (this.cursed) {
                GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else {
                this.usesTargeting = true;
                GameScene.selectCell(this.caster);
            }
        }
    }

    @Override
    public void resetForTrinity(int visibleLevel) {
        super.resetForTrinity(visibleLevel);
        this.charge = 5 + this.level() * 2;
    }

    private void chainEnemy(Ballistica chain, final Hero hero, final Char enemy) {
        if (enemy.properties().contains((Object)Char.Property.IMMOVABLE)) {
            GLog.w(Messages.get(this, "cant_pull", new Object[0]), new Object[0]);
            return;
        }
        int bestPos = -1;
        for (int i : chain.subPath(1, chain.dist)) {
            if (Dungeon.level.solid[i] || Actor.findChar(i) != null || Char.hasProp(enemy, Char.Property.LARGE) && !Dungeon.level.openSpace[i]) continue;
            bestPos = i;
            break;
        }
        if (bestPos == -1) {
            GLog.i(Messages.get(this, "does_nothing", new Object[0]), new Object[0]);
            return;
        }
        final int pulledPos = bestPos;
        final int chargeUse = Dungeon.level.distance(enemy.pos, pulledPos);
        if (chargeUse > this.charge) {
            GLog.w(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
            return;
        }
        hero.busy();
        this.throwSound();
        Sample.INSTANCE.play("sounds/chains.mp3");
        hero.sprite.parent.add(new Chains(hero.sprite.center(), enemy.sprite.center(), Effects.Type.ETHEREAL_CHAIN, new Callback(){

            @Override
            public void call() {
                Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback(){

                    @Override
                    public void call() {
                        enemy.pos = pulledPos;
                        EtherealChains.this.charge -= chargeUse;
                        Invisibility.dispel(hero);
                        Talent.onArtifactUsed(hero);
                        Item.updateQuickslot();
                        Dungeon.level.occupyCell(enemy);
                        Dungeon.observe();
                        GameScene.updateFog();
                        hero.spendAndNext(1.0f);
                        Artifact.artifactProc(enemy, EtherealChains.this.visiblyUpgraded(), chargeUse);
                    }
                }));
                hero.next();
            }
        }));
    }

    private void chainLocation(Ballistica chain, final Hero hero) {
        if (hero.rooted) {
            PixelScene.shake(1.0f, 1.0f);
            GLog.w(Messages.get(EtherealChains.class, "rooted", new Object[0]), new Object[0]);
            return;
        }
        if (Dungeon.level.solid[chain.collisionPos] || !Dungeon.level.passable[chain.collisionPos] && !Dungeon.level.avoid[chain.collisionPos]) {
            GLog.i(Messages.get(this, "inside_wall", new Object[0]), new Object[0]);
            return;
        }
        boolean solidFound = false;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.solid[chain.collisionPos + i]) continue;
            solidFound = true;
            break;
        }
        if (!solidFound) {
            GLog.i(Messages.get(EtherealChains.class, "nothing_to_grab", new Object[0]), new Object[0]);
            return;
        }
        final int newHeroPos = chain.collisionPos;
        final int chargeUse = Dungeon.level.distance(hero.pos, newHeroPos);
        if (chargeUse > this.charge) {
            GLog.w(Messages.get(EtherealChains.class, "no_charge", new Object[0]), new Object[0]);
            return;
        }
        hero.busy();
        this.throwSound();
        Sample.INSTANCE.play("sounds/chains.mp3");
        hero.sprite.parent.add(new Chains(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(newHeroPos), Effects.Type.ETHEREAL_CHAIN, new Callback(){

            @Override
            public void call() {
                Actor.add(new Pushing(hero, hero.pos, newHeroPos, new Callback(){

                    @Override
                    public void call() {
                        hero.pos = newHeroPos;
                        EtherealChains.this.charge -= chargeUse;
                        Invisibility.dispel(hero);
                        Talent.onArtifactUsed(hero);
                        Item.updateQuickslot();
                        Dungeon.level.occupyCell(hero);
                        hero.spendAndNext(1.0f);
                        Dungeon.observe();
                        GameScene.updateFog();
                    }
                }));
                hero.next();
            }
        }));
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new chainsRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        int chargeTarget = 5 + this.level() * 2;
        if (this.charge < chargeTarget * 2) {
            this.partialCharge += 0.5f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
                EtherealChains.updateQuickslot();
            }
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            desc = (String)desc + "\n\n";
            desc = this.cursed ? (String)desc + Messages.get(this, "desc_cursed", new Object[0]) : (String)desc + Messages.get(this, "desc_equipped", new Object[0]);
        }
        return desc;
    }

    public class chainsRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            int chargeTarget = 5 + EtherealChains.this.level() * 2;
            if (EtherealChains.this.charge < chargeTarget && !EtherealChains.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                float chargeGain = 1.0f / (40.0f - (float)(chargeTarget - EtherealChains.this.charge) * 2.0f);
                EtherealChains.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
            } else if (EtherealChains.this.cursed && Random.Int(100) == 0) {
                Buff.prolong(this.target, Cripple.class, 10.0f);
            }
            while (EtherealChains.this.partialCharge >= 1.0f) {
                EtherealChains.this.partialCharge -= 1.0f;
                ++EtherealChains.this.charge;
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }

        public void gainExp(float levelPortion) {
            if (EtherealChains.this.cursed || this.target.buff(MagicImmune.class) != null || levelPortion == 0.0f) {
                return;
            }
            EtherealChains.this.exp += Math.round(levelPortion * 100.0f);
            if (EtherealChains.this.charge > 5 + EtherealChains.this.level() * 2) {
                levelPortion *= (5.0f + (float)EtherealChains.this.level() * 2.0f) / (float)EtherealChains.this.charge;
            }
            EtherealChains.this.partialCharge += levelPortion * 6.0f;
            if (EtherealChains.this.exp > 100 + EtherealChains.this.level() * 100 && EtherealChains.this.level() < EtherealChains.this.levelCap) {
                EtherealChains.this.exp -= 100 + EtherealChains.this.level() * 100;
                GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
                Catalog.countUses(EtherealChains.class, 2);
                EtherealChains.this.upgrade();
            }
        }
    }
}

