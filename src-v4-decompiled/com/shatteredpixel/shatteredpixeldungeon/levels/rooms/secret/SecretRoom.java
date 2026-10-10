/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretArtilleryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretChestChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretHoardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretHoneypotRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLaboratoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLarderRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretMazeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretSummoningRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class SecretRoom
extends SpecialRoom {
    private static final ArrayList<Class<? extends SecretRoom>> ALL_SECRETS = new ArrayList<Class>(Arrays.asList(SecretGardenRoom.class, SecretLaboratoryRoom.class, SecretLibraryRoom.class, SecretLarderRoom.class, SecretWellRoom.class, SecretRunestoneRoom.class, SecretArtilleryRoom.class, SecretChestChasmRoom.class, SecretHoneypotRoom.class, SecretHoardRoom.class, SecretMazeRoom.class, SecretSummoningRoom.class));
    public static ArrayList<Class<? extends SecretRoom>> runSecrets = new ArrayList();
    private static float[] baseRegionSecrets = new float[]{2.0f, 2.25f, 2.5f, 2.75f, 3.0f};
    private static int[] regionSecretsThisRun = new int[5];
    private static final String ROOMS = "secret_rooms";
    private static final String REGIONS = "region_secrets";

    public static void initForRun() {
        float[] regionChances = (float[])baseRegionSecrets.clone();
        for (int i = 0; i < regionSecretsThisRun.length; ++i) {
            SecretRoom.regionSecretsThisRun[i] = (int)regionChances[i];
            if (!(Random.Float() < regionChances[i] % 1.0f)) continue;
            int n = i;
            regionSecretsThisRun[n] = regionSecretsThisRun[n] + 1;
        }
        runSecrets = new ArrayList<Class<? extends SecretRoom>>(ALL_SECRETS);
        Random.shuffle(runSecrets);
    }

    public static int secretsForFloor(int depth) {
        float secrets;
        if (depth == 1) {
            return 0;
        }
        int region = depth / 5;
        int floor = depth % 5;
        int floorsLeft = 5 - floor;
        if (floorsLeft == 0) {
            secrets = regionSecretsThisRun[region];
        } else {
            secrets = (float)regionSecretsThisRun[region] / (float)floorsLeft;
            secrets = Random.Float() < secrets % 1.0f ? (float)Math.ceil(secrets) : (float)Math.floor(secrets);
        }
        int n = region;
        regionSecretsThisRun[n] = regionSecretsThisRun[n] - (int)secrets;
        return (int)secrets;
    }

    public static SecretRoom createRoom() {
        int index;
        for (index = Random.chances(new float[]{6.0f, 3.0f, 1.0f}); index >= runSecrets.size(); --index) {
        }
        SecretRoom r = Reflection.newInstance(runSecrets.get(index));
        runSecrets.add(runSecrets.remove(index));
        return r;
    }

    public static void restoreRoomsFromBundle(Bundle bundle) {
        runSecrets.clear();
        if (bundle.contains(ROOMS)) {
            for (Class type : bundle.getClassArray(ROOMS)) {
                if (type == null) continue;
                runSecrets.add(type);
            }
            regionSecretsThisRun = bundle.getIntArray(REGIONS);
        } else {
            SecretRoom.initForRun();
            ShatteredPixelDungeon.reportException(new Exception("secrets array didn't exist!"));
        }
    }

    public static void storeRoomsInBundle(Bundle bundle) {
        bundle.put(ROOMS, runSecrets.toArray(new Class[0]));
        bundle.put(REGIONS, regionSecretsThisRun);
    }
}

