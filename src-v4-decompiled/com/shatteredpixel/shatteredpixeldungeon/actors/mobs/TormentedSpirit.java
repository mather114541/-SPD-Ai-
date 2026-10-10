/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TormentedSpiritSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class TormentedSpirit
extends Wraith {
    public TormentedSpirit() {
        this.spriteClass = TormentedSpiritSprite.class;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1 + Math.round(1.5f * (float)this.level) / 2, 2 + Math.round(1.5f * (float)this.level));
    }

    @Override
    public int attackSkill(Char target) {
        return 10 + Math.round(1.5f * (float)this.level);
    }

    public void cleanse() {
        EquipableItem prize;
        Sample.INSTANCE.play("sounds/ghost.mp3");
        this.yell(Messages.get(this, "thank_you", new Object[0]));
        if (Random.Int(2) == 0) {
            prize = Generator.randomWeapon(true);
            ((Weapon)prize).enchant();
        } else {
            prize = Generator.randomArmor();
            ((Armor)prize).inscribe();
        }
        prize.cursed = false;
        prize.cursedKnown = true;
        if (prize.level() == 0 && Random.Int(2) == 0) {
            prize.upgrade();
        }
        Dungeon.level.drop((Item)prize, (int)this.pos).sprite.drop();
        this.destroy();
        this.sprite.die();
        this.sprite.tint(1.0f, 1.0f, 1.0f, 1.0f);
        this.sprite.emitter().start(ShaftParticle.FACTORY, 0.3f, 4);
        this.sprite.emitter().start(Speck.factory(2), 0.2f, 3);
    }
}

