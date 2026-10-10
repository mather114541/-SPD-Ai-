/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class Challenge
extends ArmorAbility {
    public Challenge() {
        this.baseChargeUse = 50.0f;
    }

    @Override
    public int icon() {
        return 28;
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
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(EliminationMatchTracker.class) != null) {
            chargeUse = (float)((double)chargeUse * Math.pow(0.84, hero.pointsInTalent(Talent.ELIMINATION_MATCH)));
        }
        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Char targetCh = Actor.findChar(target);
        if (targetCh == null || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        if (hero.buff(DuelParticipant.class) != null) {
            GLog.w(Messages.get(this, "already_dueling", new Object[0]), new Object[0]);
            return;
        }
        if (!(targetCh.alignment == Char.Alignment.ENEMY || targetCh instanceof Mimic && targetCh.alignment == Char.Alignment.NEUTRAL)) {
            GLog.w(Messages.get(this, "ally_target", new Object[0]), new Object[0]);
            return;
        }
        boolean[] passable = BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null);
        for (Char c : Actor.chars()) {
            if (c == hero) continue;
            passable[c.pos] = false;
        }
        PathFinder.buildDistanceMap(targetCh.pos, passable);
        int[] reachable = (int[])PathFinder.distance.clone();
        int blinkpos = hero.pos;
        if (hero.hasTalent(Talent.CLOSE_THE_GAP) && !hero.rooted) {
            int blinkrange = 1 + hero.pointsInTalent(Talent.CLOSE_THE_GAP);
            PathFinder.buildDistanceMap(hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), blinkrange);
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] == Integer.MAX_VALUE || reachable[i] == Integer.MAX_VALUE || !Dungeon.level.passable[i] && (!hero.flying || !Dungeon.level.avoid[i]) || i == targetCh.pos) continue;
                if (Dungeon.level.distance(i, targetCh.pos) < Dungeon.level.distance(blinkpos, targetCh.pos)) {
                    blinkpos = i;
                    continue;
                }
                if (Dungeon.level.distance(i, targetCh.pos) != Dungeon.level.distance(blinkpos, targetCh.pos) || !(Dungeon.level.trueDistance(i, hero.pos) < Dungeon.level.trueDistance(blinkpos, hero.pos))) continue;
                blinkpos = i;
            }
        }
        if (reachable[blinkpos] == Integer.MAX_VALUE) {
            GLog.w(Messages.get(this, "unreachable_target", new Object[0]), new Object[0]);
            if (hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
            }
            return;
        }
        if (Dungeon.level.distance(blinkpos, targetCh.pos) > 5) {
            GLog.w(Messages.get(this, "distant_target", new Object[0]), new Object[0]);
            if (hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
            }
            return;
        }
        if (blinkpos != hero.pos) {
            Dungeon.hero.pos = blinkpos;
            Dungeon.level.occupyCell(Dungeon.hero);
            Dungeon.observe();
            GameScene.updateFog();
            Dungeon.hero.checkVisibleMobs();
            Dungeon.hero.sprite.place(Dungeon.hero.pos);
            CellEmitter.get(Dungeon.hero.pos).burst(Speck.factory(7), 6);
            Sample.INSTANCE.play("sounds/puff.mp3");
        }
        boolean bossTarget = Char.hasProp(targetCh, Char.Property.BOSS);
        for (Char toFreeze : Actor.chars()) {
            if (toFreeze == targetCh || toFreeze.alignment == Char.Alignment.ALLY || toFreeze instanceof NPC || bossTarget && (Char.hasProp(targetCh, Char.Property.BOSS) || Char.hasProp(targetCh, Char.Property.BOSS_MINION))) continue;
            Actor.delayChar(toFreeze, DuelParticipant.DURATION);
            Buff.affect(toFreeze, SpectatorFreeze.class, DuelParticipant.DURATION);
        }
        Buff.affect(targetCh, DuelParticipant.class);
        Buff.affect(hero, DuelParticipant.class);
        if (targetCh instanceof Mob) {
            ((Mob)targetCh).aggro(hero);
        }
        GameScene.flash(-2130706433);
        Sample.INSTANCE.play("sounds/descend.mp3");
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.sprite.zap(target);
        hero.next();
        if (hero.buff(EliminationMatchTracker.class) != null) {
            hero.buff(EliminationMatchTracker.class).detach();
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.CLOSE_THE_GAP, Talent.INVIGORATING_VICTORY, Talent.ELIMINATION_MATCH, Talent.HEROIC_ENERGY};
    }

    public static class EliminationMatchTracker
    extends FlavourBuff {
    }

    public static class DuelParticipant
    extends Buff {
        public static float DURATION = 10.0f;
        private int left = (int)DURATION;
        private int takenDmg = 0;
        private static final String LEFT = "left";
        private static final String TAKEN_DMG = "taken_dmg";

        @Override
        public int icon() {
            return 67;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - (float)this.left) / DURATION);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.left);
        }

        public void addDamage(int dmg) {
            this.takenDmg += dmg;
        }

        @Override
        public boolean act() {
            --this.left;
            if (this.left == 0) {
                this.detach();
            } else {
                Char other = null;
                for (Char ch : Actor.chars()) {
                    if (ch == this.target || ch.buff(DuelParticipant.class) == null) continue;
                    other = ch;
                }
                if (other == null || this.target.alignment == other.alignment || Dungeon.level.distance(this.target.pos, other.pos) > 5) {
                    this.detach();
                }
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void detach() {
            super.detach();
            if (this.target != Dungeon.hero) {
                if (!this.target.isAlive() || this.target.alignment == Dungeon.hero.alignment) {
                    Sample.INSTANCE.play("sounds/boss.mp3");
                    if (Dungeon.hero.hasTalent(Talent.INVIGORATING_VICTORY)) {
                        DuelParticipant heroBuff = Dungeon.hero.buff(DuelParticipant.class);
                        int hpToHeal = 0;
                        if (heroBuff != null) {
                            hpToHeal = heroBuff.takenDmg;
                        }
                        hpToHeal = (int)Math.round((double)hpToHeal * (1.0 - Math.pow(0.707f, Dungeon.hero.pointsInTalent(Talent.INVIGORATING_VICTORY))));
                        hpToHeal += 5 * Dungeon.hero.pointsInTalent(Talent.INVIGORATING_VICTORY);
                        if ((hpToHeal = Math.min(hpToHeal, Dungeon.hero.HT - Dungeon.hero.HP)) > 0) {
                            Dungeon.hero.HP += hpToHeal;
                            Dungeon.hero.sprite.emitter().start(Speck.factory(0), 0.33f, 6);
                            Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(hpToHeal), FloatingText.HEALING, new Object[0]);
                        }
                    }
                }
            } else if (Dungeon.hero.isAlive()) {
                GameScene.flash(-2130706433);
                if (Dungeon.hero.hasTalent(Talent.ELIMINATION_MATCH)) {
                    Buff.affect(this.target, EliminationMatchTracker.class, 3.0f);
                }
            }
            for (Char ch : Actor.chars()) {
                if (ch.buff(SpectatorFreeze.class) != null) {
                    ch.buff(SpectatorFreeze.class).detach();
                }
                if (ch.buff(DuelParticipant.class) == null || ch == this.target) continue;
                ch.buff(DuelParticipant.class).detach();
            }
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.left);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
            bundle.put(TAKEN_DMG, this.takenDmg);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.left = bundle.getInt(LEFT);
            this.takenDmg = bundle.getInt(TAKEN_DMG);
        }
    }

    public static class SpectatorFreeze
    extends FlavourBuff {
        public SpectatorFreeze() {
            this.immunities.addAll(new BlobImmunity().immunities());
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.DARKENED);
                this.target.sprite.add(CharSprite.State.PARALYSED);
            } else {
                if (this.target.buff(Doom.class) == null) {
                    this.target.sprite.remove(CharSprite.State.DARKENED);
                }
                if (this.target.paralysed == 0) {
                    this.target.sprite.remove(CharSprite.State.PARALYSED);
                }
            }
        }

        @Override
        public void detach() {
            super.detach();
            if (this.cooldown() > 0.0f) {
                Actor.delayChar(this.target, -this.cooldown());
            }
        }
    }
}

