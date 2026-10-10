/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class ScrollOfRetribution
extends Scroll {
    public ScrollOfRetribution() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_RETRIB;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfRetribution.curUser.belongings.backpack);
        GameScene.flash(-2130706433);
        float hpPercent = (float)(ScrollOfRetribution.curUser.HT - ScrollOfRetribution.curUser.HP) / (float)ScrollOfRetribution.curUser.HT;
        float power = Math.min(4.0f, 4.45f * hpPercent);
        Sample.INSTANCE.play("sounds/blast.mp3");
        GLog.i(Messages.get(this, "blast", new Object[0]), new Object[0]);
        ArrayList<Mob> targets = new ArrayList<Mob>();
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (!Dungeon.level.heroFOV[mob.pos]) continue;
            targets.add(mob);
        }
        for (Mob mob : targets) {
            mob.damage(Math.round((float)mob.HT / 10.0f + (float)mob.HP * power * 0.225f), this);
            if (!mob.isAlive()) continue;
            Buff.prolong(mob, Blindness.class, 10.0f);
        }
        Buff.prolong(curUser, Weakness.class, 20.0f);
        Buff.prolong(curUser, Blindness.class, 10.0f);
        Dungeon.observe();
        this.identify();
        this.readAnimation();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

