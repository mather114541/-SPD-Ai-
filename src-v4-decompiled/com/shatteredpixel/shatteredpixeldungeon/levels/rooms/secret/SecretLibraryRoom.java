/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.HashMap;

public class SecretLibraryRoom
extends SecretRoom {
    private static HashMap<Class<? extends Scroll>, Float> scrollChances = new HashMap();

    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 27);
        Painter.fillEllipse(level, this, 2, 14);
        Room.Door entrance = this.entrance();
        if (entrance.x == this.left || entrance.x == this.right) {
            Painter.drawInside(level, this, entrance, (this.width() - 3) / 2, 14);
        } else {
            Painter.drawInside(level, this, entrance, (this.height() - 3) / 2, 14);
        }
        entrance.set(Room.Door.Type.HIDDEN);
        int n = Random.IntRange(2, 3);
        HashMap<Class<? extends Scroll>, Float> chances = new HashMap<Class<? extends Scroll>, Float>(scrollChances);
        for (int i = 0; i < n; ++i) {
            int pos;
            while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
            }
            Class<? extends Scroll> scrollCls = Random.chances(chances);
            chances.put(scrollCls, Float.valueOf(0.0f));
            if (ExoticScroll.regToExo.containsKey(scrollCls) && Random.Float() < ExoticCrystals.consumableExoticChance()) {
                scrollCls = ExoticScroll.regToExo.get(scrollCls);
            }
            level.drop(Reflection.newInstance(scrollCls), pos);
        }
    }

    static {
        scrollChances.put(ScrollOfIdentify.class, Float.valueOf(1.0f));
        scrollChances.put(ScrollOfRemoveCurse.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfMirrorImage.class, Float.valueOf(3.0f));
        scrollChances.put(ScrollOfRecharging.class, Float.valueOf(3.0f));
        scrollChances.put(ScrollOfTeleportation.class, Float.valueOf(3.0f));
        scrollChances.put(ScrollOfLullaby.class, Float.valueOf(4.0f));
        scrollChances.put(ScrollOfMagicMapping.class, Float.valueOf(4.0f));
        scrollChances.put(ScrollOfRage.class, Float.valueOf(4.0f));
        scrollChances.put(ScrollOfRetribution.class, Float.valueOf(4.0f));
        scrollChances.put(ScrollOfTerror.class, Float.valueOf(4.0f));
        scrollChances.put(ScrollOfTransmutation.class, Float.valueOf(6.0f));
    }
}

