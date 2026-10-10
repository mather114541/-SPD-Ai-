/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.HashMap;

public class SecretLaboratoryRoom
extends SecretRoom {
    private static HashMap<Class<? extends Potion>, Float> potionChances = new HashMap();

    @Override
    public void paint(Level level) {
        int pos;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        this.entrance().set(Room.Door.Type.HIDDEN);
        Point pot = this.center();
        Painter.set(level, pot, 28);
        Blob.seed(pot.x + level.width() * pot.y, 1, Alchemy.class, level);
        while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
        }
        level.drop(new EnergyCrystal().quantity(Random.IntRange(3, 5)), pos);
        while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
        }
        level.drop(new EnergyCrystal().quantity(Random.IntRange(3, 5)), pos);
        int n = Random.IntRange(2, 3);
        HashMap<Class<? extends Potion>, Float> chances = new HashMap<Class<? extends Potion>, Float>(potionChances);
        for (int i = 0; i < n; ++i) {
            while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
            }
            Class<? extends Potion> potionCls = Random.chances(chances);
            chances.put(potionCls, Float.valueOf(0.0f));
            if (ExoticPotion.regToExo.containsKey(potionCls) && Random.Float() < ExoticCrystals.consumableExoticChance()) {
                potionCls = ExoticPotion.regToExo.get(potionCls);
            }
            level.drop(Reflection.newInstance(potionCls), pos);
        }
    }

    static {
        potionChances.put(PotionOfHealing.class, Float.valueOf(1.0f));
        potionChances.put(PotionOfMindVision.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfFrost.class, Float.valueOf(3.0f));
        potionChances.put(PotionOfLiquidFlame.class, Float.valueOf(3.0f));
        potionChances.put(PotionOfToxicGas.class, Float.valueOf(3.0f));
        potionChances.put(PotionOfHaste.class, Float.valueOf(4.0f));
        potionChances.put(PotionOfInvisibility.class, Float.valueOf(4.0f));
        potionChances.put(PotionOfLevitation.class, Float.valueOf(4.0f));
        potionChances.put(PotionOfParalyticGas.class, Float.valueOf(4.0f));
        potionChances.put(PotionOfPurity.class, Float.valueOf(4.0f));
        potionChances.put(PotionOfExperience.class, Float.valueOf(6.0f));
    }
}

