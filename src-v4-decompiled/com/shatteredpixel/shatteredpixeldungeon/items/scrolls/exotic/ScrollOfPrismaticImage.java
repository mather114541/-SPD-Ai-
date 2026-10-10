/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PrismaticGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPrismaticImage
extends ExoticScroll {
    public ScrollOfPrismaticImage() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_PRISIMG;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfPrismaticImage.curUser.belongings.backpack);
        boolean found = false;
        for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (!(m instanceof PrismaticImage)) continue;
            found = true;
            m.HP = m.HT;
            m.sprite.showStatusWithIcon(65280, Integer.toString(m.HT), FloatingText.HEALING, new Object[0]);
        }
        if (!found && Stasis.getStasisAlly() instanceof PrismaticImage) {
            found = true;
            Stasis.getStasisAlly().HP = Stasis.getStasisAlly().HT;
        }
        if (!found) {
            Buff.affect(curUser, PrismaticGuard.class).set(PrismaticGuard.maxHP(curUser));
        }
        this.identify();
        Sample.INSTANCE.play("sounds/read.mp3");
        this.readAnimation();
    }
}

