/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class SuperNovaTracker
extends Buff {
    public int pos;
    private int depth = Dungeon.depth;
    private int branch = Dungeon.branch;
    private int turnsLeft = 10;
    public boolean harmsAllies = true;
    private boolean[] fieldOfView;
    private NovaVFX halo;
    private static final int DIST = 8;
    public static final String POS = "pos";
    public static final String DEPTH = "depth";
    public static final String BRANCH = "branch";
    public static final String LEFT = "left";
    public static final String HARMS_ALLIES = "harms_allies";

    @Override
    public boolean act() {
        if (this.branch != Dungeon.branch || this.depth != Dungeon.depth) {
            this.spend(1.0f);
            return true;
        }
        PointF p = DungeonTilemap.raisedTileCenterToWorld(this.pos);
        if (this.fieldOfView == null) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
        }
        if (this.halo == null) {
            this.halo = new NovaVFX();
            this.halo.point(p.x, p.y);
            this.halo.hardlight(1.0f, 1.0f, 0.0f);
            GameScene.effect(this.halo);
        }
        if (this.turnsLeft > 0) {
            FloatingText.show(p.x, p.y, this.pos, this.turnsLeft + "...", 0xFF8800);
            this.halo.radius(5 + 2 * (10 - this.turnsLeft));
            this.halo.alpha(1.25f - 0.075f * (float)this.turnsLeft);
            this.halo.point(p.x, p.y);
        }
        Point c = Dungeon.level.cellToPoint(this.pos);
        ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), this.fieldOfView, Dungeon.level.solid, Math.min(8, 11 - this.turnsLeft));
        if (this.turnsLeft <= 0) {
            this.detach();
            this.halo.killAndErase();
            if (!this.harmsAllies) {
                for (Char ch : Actor.chars()) {
                    if (ch.alignment != Char.Alignment.ALLY) continue;
                    Buff.affect(ch, NovaBombImmune.class, 0.0f);
                }
            }
            Sample.INSTANCE.play("sounds/blast.mp3");
            Sample.INSTANCE.playDelayed("sounds/blast.mp3", 0.25f);
            Sample.INSTANCE.playDelayed("sounds/blast.mp3", 0.5f);
            PixelScene.shake(5.0f, 2.0f);
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (!this.fieldOfView[i] || Dungeon.level.solid[i]) continue;
                new Bomb.ConjuredBomb().explode(i);
                Dungeon.level.destroy(i);
                if (Actor.findChar(i) != Dungeon.hero) continue;
                GameScene.flash(-2130706433);
            }
            GameScene.updateMap();
        } else {
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (!this.fieldOfView[i] || Dungeon.level.solid[i]) continue;
                GameScene.targetedCell(i, 1.0f);
            }
        }
        --this.turnsLeft;
        this.spend(1.0f);
        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on && this.depth == Dungeon.depth && this.branch == Dungeon.branch && (this.halo == null || this.halo.parent == null)) {
            this.halo = new NovaVFX();
            PointF p = DungeonTilemap.raisedTileCenterToWorld(this.pos);
            this.halo.hardlight(1.0f, 1.0f, 0.0f);
            this.halo.radius(5 + 2 * (10 - this.turnsLeft));
            this.halo.alpha(1.25f - 0.075f * (float)this.turnsLeft);
            this.halo.point(p.x, p.y);
            GameScene.effect(this.halo);
        }
        super.fx(on);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POS, this.pos);
        bundle.put(DEPTH, this.depth);
        bundle.put(BRANCH, this.branch);
        bundle.put(LEFT, this.turnsLeft);
        bundle.put(HARMS_ALLIES, this.harmsAllies);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.pos = bundle.getInt(POS);
        this.depth = bundle.getInt(DEPTH);
        this.branch = bundle.getInt(BRANCH);
        this.turnsLeft = bundle.getInt(LEFT);
        this.harmsAllies = bundle.getBoolean(HARMS_ALLIES);
    }

    public class NovaVFX
    extends Halo {
        @Override
        public void update() {
            this.am = this.brightness + 0.1f * (float)Math.cos(20.0f * Game.timeTotal);
            this.scale.set((this.radius + (float)Math.cos(20.0f * Game.timeTotal)) / 128.0f);
            PointF p = DungeonTilemap.raisedTileCenterToWorld(SuperNovaTracker.this.pos);
            this.point(p.x, p.y);
            super.update();
        }
    }

    public static class NovaBombImmune
    extends FlavourBuff {
        public NovaBombImmune() {
            this.immunities.add(Bomb.ConjuredBomb.class);
        }
    }
}

