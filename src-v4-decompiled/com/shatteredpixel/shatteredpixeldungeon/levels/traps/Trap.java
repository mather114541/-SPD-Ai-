/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTrap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Trap
implements Bundlable {
    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int TEAL = 4;
    public static final int VIOLET = 5;
    public static final int WHITE = 6;
    public static final int GREY = 7;
    public static final int BLACK = 8;
    public static final int DOTS = 0;
    public static final int WAVES = 1;
    public static final int GRILL = 2;
    public static final int STARS = 3;
    public static final int DIAMOND = 4;
    public static final int CROSSHAIR = 5;
    public static final int LARGE_DOT = 6;
    public int color;
    public int shape;
    public int pos;
    public boolean reclaimed = false;
    public boolean visible;
    public boolean active = true;
    public boolean disarmedByActivation = true;
    public boolean canBeHidden = true;
    public boolean canBeSearched = true;
    public boolean avoidsHallways = false;
    private static final String POS = "pos";
    private static final String VISIBLE = "visible";
    private static final String ACTIVE = "active";

    public Trap set(int pos) {
        this.pos = pos;
        return this;
    }

    public Trap reveal() {
        this.visible = true;
        GameScene.updateMap(this.pos);
        return this;
    }

    public Trap hide() {
        if (this.canBeHidden) {
            this.visible = false;
            GameScene.updateMap(this.pos);
            return this;
        }
        return this.reveal();
    }

    public void trigger() {
        if (this.active) {
            if (Dungeon.level.heroFOV[this.pos]) {
                Sample.INSTANCE.play("sounds/trap.mp3");
            }
            if (this.disarmedByActivation) {
                this.disarm();
            }
            Dungeon.level.discover(this.pos);
            Bestiary.setSeen(this.getClass());
            Bestiary.countEncounter(this.getClass());
            this.activate();
        }
    }

    public abstract void activate();

    public void disarm() {
        this.active = false;
        Dungeon.level.disarmTrap(this.pos);
    }

    protected int scalingDepth() {
        return this.reclaimed || Dungeon.level.traps.get(this.pos) != this ? Dungeon.scalingDepth() : Dungeon.depth;
    }

    public String name() {
        return Messages.get(this, "name", new Object[0]);
    }

    public String desc() {
        Object desc = "";
        if (!this.active) {
            desc = (String)desc + Messages.get(WndInfoTrap.class, "inactive", new Object[0]) + "\n\n";
        }
        desc = (String)desc + Messages.get(this, "desc", new Object[0]);
        return desc;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.pos = bundle.getInt(POS);
        this.visible = bundle.getBoolean(VISIBLE);
        if (bundle.contains(ACTIVE)) {
            this.active = bundle.getBoolean(ACTIVE);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, this.pos);
        bundle.put(VISIBLE, this.visible);
        bundle.put(ACTIVE, this.active);
    }

    public static class HazardAssistTracker
    extends FlavourBuff {
        public static final float DURATION = 50.0f;
    }
}

