/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VaultFlameTraps;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;

public class BlobImmunity
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public BlobImmunity() {
        this.type = Buff.buffType.POSITIVE;
        this.immunities.add(Blizzard.class);
        this.immunities.add(ConfusionGas.class);
        this.immunities.add(CorrosiveGas.class);
        this.immunities.add(Electricity.class);
        this.immunities.add(Fire.class);
        this.immunities.add(MagicalFireRoom.EternalFire.class);
        this.immunities.add(Freezing.class);
        this.immunities.add(Inferno.class);
        this.immunities.add(ParalyticGas.class);
        this.immunities.add(Regrowth.class);
        this.immunities.add(SmokeScreen.class);
        this.immunities.add(StenchGas.class);
        this.immunities.add(StormCloud.class);
        this.immunities.add(ToxicGas.class);
        this.immunities.add(Web.class);
        this.immunities.add(Tengu.FireAbility.FireBlob.class);
        this.immunities.add(VaultFlameTraps.class);
    }

    @Override
    public int icon() {
        return 25;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }
}

