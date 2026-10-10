/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Stasis
extends ClericSpell {
    public static Stasis INSTANCE = new Stasis();

    @Override
    public int icon() {
        return 66;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 30 + 30 * Dungeon.hero.pointsInTalent(Talent.STASIS)) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.STASIS) && (PowerOfMany.getPoweredAlly() != null || hero.buff(StasisBuff.class) != null);
    }

    @Override
    public float chargeUse(Hero hero) {
        if (hero.buff(StasisBuff.class) != null) {
            return 0.0f;
        }
        return 2.0f;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        this.onSpellCast(tome, hero);
        if (hero.buff(StasisBuff.class) != null) {
            hero.sprite.operate(hero.pos);
            hero.buff(StasisBuff.class).act();
            return;
        }
        Char ally = PowerOfMany.getPoweredAlly();
        hero.sprite.zap(ally.pos);
        MagicMissile.boltFromChar(hero.sprite.parent, 17, ally.sprite, hero.pos, null);
        LinkedHashSet<Buff> buffs = ally.buffs();
        Actor.remove(ally);
        ally.sprite.killAndErase();
        ally.sprite = null;
        Dungeon.level.mobs.remove(ally);
        for (Buff b : buffs) {
            if (b.type != Buff.buffType.POSITIVE && !b.revivePersists) continue;
            ally.add(b);
        }
        ally.clearTime();
        Buff.prolong((Char)hero, StasisBuff.class, (float)((float)(30 + 30 * hero.pointsInTalent((Talent)Talent.STASIS)))).stasisAlly = (Mob)ally;
        Sample.INSTANCE.play("sounds/teleport.mp3");
        if (hero.buff(LifeLink.class) != null && hero.buff(LifeLink.class).object == ally.id()) {
            hero.buff(LifeLink.class).detach();
        }
        hero.spendAndNext(1.0f);
        Dungeon.observe();
        GameScene.updateFog();
    }

    public static Char getStasisAlly() {
        if (Dungeon.hero != null && Dungeon.hero.buff(StasisBuff.class) != null) {
            return Dungeon.hero.buff(StasisBuff.class).stasisAlly;
        }
        return null;
    }

    public static class StasisBuff
    extends FlavourBuff {
        Mob stasisAlly;
        private static final String ALLY = "ally";

        public StasisBuff() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 83;
        }

        @Override
        public float iconFadePercent() {
            int duration = 30 + 30 * Dungeon.hero.pointsInTalent(Talent.STASIS);
            return Math.max(0.0f, ((float)duration - this.visualcooldown()) / (float)duration);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.titleCase(this.stasisAlly.name()), this.dispTurns());
        }

        @Override
        public boolean act() {
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = this.target.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && (!this.stasisAlly.flying || !Dungeon.level.avoid[p])) continue;
                spawnPoints.add(p);
            }
            if (spawnPoints.isEmpty()) {
                spawnPoints.add(this.target.pos + PathFinder.NEIGHBOURS8[Random.Int(8)]);
            }
            this.stasisAlly.pos = (Integer)Random.element(spawnPoints);
            GameScene.add(this.stasisAlly);
            if (this.stasisAlly instanceof DirectableAlly) {
                ((DirectableAlly)this.stasisAlly).clearDefensingPos();
            }
            if (this.stasisAlly.buff(LifeLink.class) != null) {
                Buff.prolong((Char)Dungeon.hero, LifeLink.class, (float)this.stasisAlly.buff(LifeLink.class).cooldown()).object = this.stasisAlly.id();
            }
            ScrollOfTeleportation.appear(this.stasisAlly, this.stasisAlly.pos);
            Sample.INSTANCE.play("sounds/teleport.mp3");
            return super.act();
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ALLY, this.stasisAlly);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.stasisAlly = (Mob)bundle.get(ALLY);
        }
    }
}

