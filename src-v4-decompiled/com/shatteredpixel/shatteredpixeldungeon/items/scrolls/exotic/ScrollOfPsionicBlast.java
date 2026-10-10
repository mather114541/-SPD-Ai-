/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class ScrollOfPsionicBlast
extends ExoticScroll {
    public ScrollOfPsionicBlast() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_PSIBLAST;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfPsionicBlast.curUser.belongings.backpack);
        GameScene.flash(-2130706433);
        Sample.INSTANCE.play("sounds/blast.mp3");
        GLog.i(Messages.get(ScrollOfRetribution.class, "blast", new Object[0]), new Object[0]);
        ArrayList<Mob> targets = new ArrayList<Mob>();
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (!Dungeon.level.heroFOV[mob.pos]) continue;
            targets.add(mob);
        }
        for (Mob mob : targets) {
            mob.damage(Math.round((float)mob.HT / 2.0f + (float)mob.HP / 2.0f), this);
            if (!mob.isAlive()) continue;
            Buff.prolong(mob, Blindness.class, 10.0f);
        }
        curUser.damage(Math.max(0, Math.round((float)ScrollOfPsionicBlast.curUser.HT * (0.5f * (float)Math.pow(0.9, targets.size())))), this);
        if (curUser.isAlive()) {
            Buff.prolong(curUser, Blindness.class, 10.0f);
            Buff.prolong(curUser, Weakness.class, 100.0f);
            Dungeon.observe();
            this.readAnimation();
        } else {
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
        }
        this.identify();
    }
}

