/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class WandOfLightning
extends DamageWand {
    private ArrayList<Char> affected;
    private ArrayList<Lightning.Arc> arcs;

    public WandOfLightning() {
        this.image = ItemSpriteSheet.WAND_LIGHTNING;
        this.affected = new ArrayList();
        this.arcs = new ArrayList();
    }

    @Override
    public int min(int lvl) {
        return 5 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 10 + 5 * lvl;
    }

    @Override
    public void onZap(Ballistica bolt) {
        for (Char ch : this.affected.toArray(new Char[0])) {
            if (ch != curUser && ch.alignment == WandOfLightning.curUser.alignment && ch.pos != bolt.collisionPos) {
                this.affected.remove(ch);
                continue;
            }
            if (ch.buff(LightningCharge.class) == null) continue;
            this.affected.remove(ch);
        }
        float multiplier = 0.4f + 0.6f / (float)this.affected.size();
        if (Dungeon.level.water[bolt.collisionPos]) {
            multiplier = 1.0f;
        }
        for (Char ch : this.affected) {
            if (ch == Dungeon.hero) {
                PixelScene.shake(2.0f, 0.3f);
            }
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
            this.wandProc(ch, this.chargesPerCast());
            if (ch == curUser && ch.isAlive()) {
                ch.damage(Math.round((float)this.damageRoll() * multiplier * 0.5f), this);
                if (curUser.isAlive()) continue;
                Badges.validateDeathFromFriendlyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                continue;
            }
            ch.damage(Math.round((float)this.damageRoll() * multiplier), this);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        float procChance = ((float)this.buffedLvl() + 1.0f) / ((float)this.buffedLvl() + 4.0f) * WandOfLightning.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            FlavourBuff.prolong(attacker, LightningCharge.class, powerMulti * LightningCharge.DURATION);
            attacker.sprite.centerEmitter().burst(SparkParticle.FACTORY, 10);
            attacker.sprite.flash();
            Sample.INSTANCE.play("sounds/lightning.mp3");
        }
    }

    private void arc(Char ch) {
        int dist;
        int n = dist = Dungeon.level.water[ch.pos] ? 2 : 1;
        if (curUser.buff(LightningCharge.class) != null) {
            ++dist;
        }
        ArrayList<Char> hitThisArc = new ArrayList<Char>();
        PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            Char n2;
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || (n2 = Actor.findChar(i)) == Dungeon.hero && PathFinder.distance[i] > 1 || n2 == null || this.affected.contains(n2)) continue;
            hitThisArc.add(n2);
        }
        this.affected.addAll(hitThisArc);
        for (Char hit : hitThisArc) {
            this.arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
            this.arc(hit);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        this.affected.clear();
        this.arcs.clear();
        int cell = bolt.collisionPos;
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            if (ch instanceof DwarfKing) {
                Statistics.qualifiedForBossChallengeBadge = false;
            }
            this.affected.add(ch);
            this.arcs.add(new Lightning.Arc(WandOfLightning.curUser.sprite.center(), ch.sprite.center()));
            this.arc(ch);
        } else {
            this.arcs.add(new Lightning.Arc(WandOfLightning.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
        }
        WandOfLightning.curUser.sprite.parent.addToFront(new Lightning(this.arcs, null));
        Sample.INSTANCE.play("sounds/lightning.mp3");
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xFFFFFF);
        particle.am = 0.6f;
        particle.setLifespan(0.6f);
        particle.acc.set(0.0f, 10.0f);
        particle.speed.polar(-Random.Float(3.1415925f), 6.0f);
        particle.setSize(0.0f, 1.5f);
        particle.sizeJitter = 1.0f;
        particle.shuffleXY(1.0f);
        float dst = Random.Float(1.0f);
        particle.x -= dst;
        particle.y += dst;
    }

    public static class LightningCharge
    extends FlavourBuff {
        public static float DURATION = 10.0f;

        public LightningCharge() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 55;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        }
    }
}

