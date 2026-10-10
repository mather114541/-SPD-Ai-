/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class GravityChaosTracker
extends Buff {
    public int left;
    public boolean positiveOnly;
    int idx;
    ArrayList<Char> blocked;
    private static final String LEFT = "left";
    private static final String POSITIVE_ONLY = "positive_only";

    public GravityChaosTracker() {
        this.actPriority = -40;
        this.left = Random.NormalIntRange(30, 70);
        this.blocked = new ArrayList();
    }

    @Override
    public int icon() {
        return 33;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.positiveOnly) {
            icon.hardlight(0.0f, 1.0f, 0.0f);
        } else {
            icon.hardlight(1.0f, 0.0f, 0.0f);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean act() {
        // headless: there is no render loop, so sprites never finish their move
        // animation and sprite.wait() below would block forever (no one notifies).
        // Skip the whole "wait for all sprites to stop moving" block.
        if (!rl.RLMode.headless) {
            for (Char char_ : Actor.chars()) {
                try {
                    CharSprite charSprite = char_.sprite;
                    synchronized (charSprite) {
                        if (char_.sprite.isMoving) {
                            char_.sprite.wait();
                        }
                    }
                }
                catch (InterruptedException interruptedException) {
                }
            }
        }
        if (!this.blocked.isEmpty()) {
            boolean blockedremoved = false;
            for (Char ch : this.blocked.toArray(new Char[0])) {
                Ballistica path = new Ballistica(ch.pos, ch.pos + PathFinder.NEIGHBOURS8[this.idx], 6);
                if (path.dist == 1 && Actor.findChar(path.collisionPos) != null) continue;
                if (ch instanceof Hero) {
                    ((Hero)ch).interrupt();
                }
                WandOfBlastWave.throwChar(ch, path, 3, false, false, this);
                this.blocked.remove(ch);
                blockedremoved = true;
            }
            if (!blockedremoved || this.blocked.isEmpty()) {
                this.blocked.clear();
                --this.left;
                if (this.left <= 0) {
                    GLog.w(Messages.get(CursedWand.class, "gravity_end", new Object[0]), new Object[0]);
                    Sample.INSTANCE.play("sounds/degrade.mp3");
                    this.detach();
                } else {
                    this.spend(Random.IntRange(1, 3));
                }
                return true;
            }
            return true;
        }
        this.idx = Random.Int(PathFinder.NEIGHBOURS8.length);
        for (Char char_ : Actor.chars()) {
            if (Char.hasProp(char_, Char.Property.IMMOVABLE) || this.positiveOnly && char_.alignment == Char.Alignment.ALLY) continue;
            if (char_ instanceof Mob && ((Mob)char_).state == ((Mob)char_).SLEEPING) {
                ((Mob)char_).state = ((Mob)char_).WANDERING;
            }
            Ballistica path = new Ballistica(char_.pos, char_.pos + PathFinder.NEIGHBOURS8[this.idx], 6);
            if (path.dist == 1 && Actor.findChar(path.collisionPos) != null) {
                this.blocked.add(char_);
                continue;
            }
            if (char_ instanceof Hero) {
                ((Hero)char_).interrupt();
            }
            WandOfBlastWave.throwChar(char_, path, 3, false, false, this);
        }
        if (this.blocked.isEmpty()) {
            --this.left;
            if (this.left <= 0) {
                GLog.w(Messages.get(CursedWand.class, "gravity_end", new Object[0]), new Object[0]);
                Sample.INSTANCE.play("sounds/degrade.mp3");
                this.detach();
            } else {
                this.spend(Random.IntRange(1, 3));
            }
        }
        return true;
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc_intro", new Object[0]);
        if (this.positiveOnly) {
            desc = desc + " " + Messages.get(this, "desc_positive", new Object[0]);
        }
        desc = desc + "\n\n" + Messages.get(this, "desc_duration", new Object[0]);
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
        bundle.put(POSITIVE_ONLY, this.positiveOnly);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getInt(LEFT);
        this.positiveOnly = bundle.getBoolean(POSITIVE_ONLY);
    }
}

