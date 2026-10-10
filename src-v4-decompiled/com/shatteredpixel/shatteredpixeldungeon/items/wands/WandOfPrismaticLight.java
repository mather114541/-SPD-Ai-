/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfPrismaticLight
extends DamageWand {
    public WandOfPrismaticLight() {
        this.image = ItemSpriteSheet.WAND_PRISMATIC_LIGHT;
        this.collisionProperties = 6;
    }

    @Override
    public int min(int lvl) {
        return 1 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 5 + 3 * lvl;
    }

    @Override
    public void onZap(Ballistica beam) {
        Char ch;
        this.affectMap(beam);
        if (Dungeon.level.viewDistance < 6) {
            if (Dungeon.isChallenged(32)) {
                Buff.prolong(curUser, Light.class, 2.0f + (float)this.buffedLvl());
            } else {
                Buff.prolong(curUser, Light.class, 10.0f + (float)(this.buffedLvl() * 5));
            }
        }
        if ((ch = Actor.findChar(beam.collisionPos)) != null) {
            this.wandProc(ch, this.chargesPerCast());
            this.affectTarget(ch);
        }
    }

    private void affectTarget(Char ch) {
        int dmg = this.damageRoll();
        if (Random.Int(5 + this.buffedLvl()) >= 3) {
            Buff.prolong(ch, Blindness.class, 2.0f + (float)this.buffedLvl() * 0.333f);
            ch.sprite.emitter().burst(Speck.factory(2), 6);
        }
        if (ch.properties().contains((Object)Char.Property.DEMONIC) || ch.properties().contains((Object)Char.Property.UNDEAD)) {
            ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + this.buffedLvl());
            Sample.INSTANCE.play("sounds/burning.mp3");
            ch.damage(Math.round((float)dmg * 1.333f), this);
        } else {
            ch.sprite.centerEmitter().burst(RainbowParticle.BURST, 10 + this.buffedLvl());
            ch.damage(dmg, this);
        }
    }

    private void affectMap(Ballistica beam) {
        boolean noticed = false;
        for (int c : beam.subPath(0, beam.dist)) {
            if (!Dungeon.level.insideMap(c)) continue;
            for (int n : PathFinder.NEIGHBOURS9) {
                int terr;
                int cell = c + n;
                if (Dungeon.level.discoverable[cell]) {
                    Dungeon.level.mapped[cell] = true;
                }
                if ((Terrain.flags[terr = Dungeon.level.map[cell]] & 8) == 0) continue;
                Dungeon.level.discover(cell);
                GameScene.discoverTile(cell, terr);
                ScrollOfMagicMapping.discover(cell);
                noticed = true;
            }
            CellEmitter.center(c).burst(RainbowParticle.BURST, Random.IntRange(1, 2));
        }
        if (noticed) {
            Sample.INSTANCE.play("sounds/secret.mp3");
        }
        GameScene.updateFog();
    }

    @Override
    public String upgradeStat2(int level) {
        return Messages.decimalFormat("#", 100.0f * (1.0f - 3.0f / (float)(5 + level))) + "%";
    }

    @Override
    public String upgradeStat3(int level) {
        if (Dungeon.isChallenged(32)) {
            return Integer.toString(2 + level);
        }
        return Integer.toString(10 + 5 * level);
    }

    @Override
    public void fx(Ballistica beam, Callback callback) {
        WandOfPrismaticLight.curUser.sprite.parent.add(new Beam.LightRay(WandOfPrismaticLight.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play("sounds/ray.mp3");
        callback.call();
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Cripple.class, Math.round((float)(1 + staff.buffedLvl()) * WandOfPrismaticLight.procChanceMultiplier(attacker)));
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(Random.Int(0x1000000));
        particle.am = 0.5f;
        particle.setLifespan(1.0f);
        particle.speed.polar(Random.Float(6.283185f), 2.0f);
        particle.setSize(1.0f, 2.0f);
        particle.radiateXY(0.5f);
    }
}

