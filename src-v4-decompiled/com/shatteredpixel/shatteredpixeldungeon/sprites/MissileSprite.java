/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyLance;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import java.util.HashMap;

public class MissileSprite
extends ItemSprite
implements Tweener.Listener {
    private static final float SPEED = 240.0f;
    private Callback callback;
    private static final int DEFAULT_ANGULAR_SPEED = 720;
    private static final HashMap<Class<? extends Item>, Integer> ANGULAR_SPEEDS = new HashMap();

    public void reset(int from, int to, Item item, Callback listener) {
        this.reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from), Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to), item, listener);
    }

    public void reset(Visual from, int to, Item item, Callback listener) {
        this.reset(from.center(), Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to), item, listener);
    }

    public void reset(int from, Visual to, Item item, Callback listener) {
        this.reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from), to.center(), item, listener);
    }

    public void reset(Visual from, Visual to, Item item, Callback listener) {
        this.reset(from.center(), to.center(), item, listener);
    }

    public void reset(PointF from, PointF to, Item item, Callback listener) {
        this.revive();
        if (item == null) {
            this.view(0, null);
        } else {
            this.view(item);
        }
        this.setup(from, to, item, listener);
    }

    private void setup(PointF from, PointF to, Item item, Callback listener) {
        this.originToCenter();
        from.x -= this.width() / 2.0f;
        to.x -= this.width() / 2.0f;
        from.y -= this.height() / 2.0f;
        to.y -= this.height() / 2.0f;
        this.callback = listener;
        this.point(from);
        PointF d = PointF.diff(to, from);
        this.speed.set(d).normalize().scale(240.0f);
        this.angularSpeed = 720.0f;
        for (Class<? extends Item> cls : ANGULAR_SPEEDS.keySet()) {
            if (!cls.isAssignableFrom(item.getClass())) continue;
            this.angularSpeed = ANGULAR_SPEEDS.get(cls).intValue();
            break;
        }
        this.angle = 135.0f - (float)(Math.atan2(d.x, d.y) / 3.1415926 * 180.0);
        if (d.x >= 0.0f) {
            this.flipHorizontal = false;
            this.updateFrame();
        } else {
            this.angularSpeed = -this.angularSpeed;
            this.angle += 90.0f;
            this.flipHorizontal = true;
            this.updateFrame();
        }
        if (item instanceof GnollGeomancer.Boulder) {
            this.angle = 0.0f;
            this.flipHorizontal = false;
            this.updateFrame();
        }
        float speed = 240.0f;
        if (item instanceof Dart && (Dungeon.hero.belongings.weapon() instanceof Crossbow || Dungeon.hero.belongings.secondWep() instanceof Crossbow)) {
            speed *= 3.0f;
        } else if (item instanceof SpiritBow.SpiritArrow || item instanceof ScorpioSprite.ScorpioShot || item instanceof TenguSprite.TenguShuriken) {
            speed *= 1.5f;
        }
        PosTweener tweener = new PosTweener(this, to, d.length() / speed);
        tweener.listener = this;
        this.parent.add(tweener);
    }

    @Override
    public void onComplete(Tweener tweener) {
        this.kill();
        if (this.callback != null) {
            this.callback.call();
        }
    }

    static {
        ANGULAR_SPEEDS.put(Dart.class, 0);
        ANGULAR_SPEEDS.put(ThrowingKnife.class, 0);
        ANGULAR_SPEEDS.put(ThrowingSpike.class, 0);
        ANGULAR_SPEEDS.put(FishingSpear.class, 0);
        ANGULAR_SPEEDS.put(ThrowingSpear.class, 0);
        ANGULAR_SPEEDS.put(Kunai.class, 0);
        ANGULAR_SPEEDS.put(Javelin.class, 0);
        ANGULAR_SPEEDS.put(Trident.class, 0);
        ANGULAR_SPEEDS.put(SpiritBow.SpiritArrow.class, 0);
        ANGULAR_SPEEDS.put(ScorpioSprite.ScorpioShot.class, 0);
        ANGULAR_SPEEDS.put(HolyLance.HolyLanceVFX.class, 0);
        ANGULAR_SPEEDS.put(GnollGeomancer.Boulder.class, 90);
        ANGULAR_SPEEDS.put(HeavyBoomerang.class, 1440);
        ANGULAR_SPEEDS.put(Bolas.class, 1440);
        ANGULAR_SPEEDS.put(Shuriken.class, 2160);
        ANGULAR_SPEEDS.put(TenguSprite.TenguShuriken.class, 2160);
    }
}

