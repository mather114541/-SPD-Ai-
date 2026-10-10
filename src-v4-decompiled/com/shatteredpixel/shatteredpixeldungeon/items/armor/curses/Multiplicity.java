/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Multiplicity
extends Armor.Glyph {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        float procChance = 0.05f * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance) {
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = defender.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && !Dungeon.level.avoid[p]) continue;
                spawnPoints.add(p);
            }
            if (spawnPoints.size() > 0) {
                Mob m = null;
                if (Random.Int(2) == 0 && defender instanceof Hero) {
                    m = new MirrorImage();
                    m.duplicate((Hero)defender);
                } else {
                    Char toDuplicate = attacker;
                    if (toDuplicate instanceof Ratmogrify.TransmogRat) {
                        toDuplicate = ((Ratmogrify.TransmogRat)attacker).getOriginal();
                    }
                    m = !(toDuplicate instanceof Mob) || toDuplicate.properties().contains((Object)Char.Property.BOSS) || toDuplicate.properties().contains((Object)Char.Property.MINIBOSS) || toDuplicate instanceof Mimic || toDuplicate instanceof Statue || toDuplicate instanceof NPC ? Dungeon.level.createMob() : Multiplicity.duplicate((Mob)toDuplicate);
                }
                if (m != null) {
                    if (Char.hasProp(m, Char.Property.LARGE)) {
                        Integer[] integerArray = spawnPoints.toArray(new Integer[0]);
                        int n = integerArray.length;
                        for (int i = 0; i < n; ++i) {
                            int i2 = integerArray[i];
                            if (Dungeon.level.openSpace[i2]) continue;
                            spawnPoints.remove((Object)i2);
                        }
                    }
                    if (!spawnPoints.isEmpty()) {
                        m.pos = (Integer)Random.element(spawnPoints);
                        GameScene.add(m);
                        ScrollOfTeleportation.appear(m, m.pos);
                    }
                }
            }
        }
        return damage;
    }

    public static Mob duplicate(Mob toDuplicate) {
        if (toDuplicate instanceof Ratmogrify.TransmogRat) {
            toDuplicate = ((Ratmogrify.TransmogRat)toDuplicate).getOriginal();
        }
        Actor.fixTime();
        Mob m = (Mob)Reflection.newInstance(toDuplicate.getClass());
        if (m != null) {
            Bundle store = new Bundle();
            toDuplicate.storeInBundle(store);
            m.restoreFromBundle(store);
            m.pos = 0;
            m.HP = m.HT;
            m.remove(PinCushion.class);
            m.remove(DwarfKing.KingDamager.class);
            m.remove(Ghoul.GhoulLifeLink.class);
            if (m instanceof Thief) {
                ((Thief)m).item = null;
            }
        }
        return m;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}

