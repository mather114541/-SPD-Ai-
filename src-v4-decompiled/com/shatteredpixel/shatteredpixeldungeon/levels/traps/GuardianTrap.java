/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GuardianTrap
extends Trap {
    public GuardianTrap() {
        this.color = 0;
        this.shape = 3;
    }

    @Override
    public void activate() {
        for (Mob mob : Dungeon.level.mobs) {
            mob.beckon(this.pos);
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            GLog.w(Messages.get(this, "alarm", new Object[0]), new Object[0]);
            CellEmitter.center(this.pos).start(Speck.factory(5), 0.3f, 3);
        }
        Sample.INSTANCE.play("sounds/alert.mp3");
        for (int i = 0; i < (this.scalingDepth() - 5) / 5; ++i) {
            Guardian guardian = new Guardian();
            guardian.createWeapon(false);
            guardian.state = guardian.WANDERING;
            guardian.pos = Dungeon.level.randomRespawnCell(guardian);
            if (guardian.pos == -1) continue;
            GameScene.add(guardian);
            guardian.beckon(Dungeon.hero.pos);
        }
    }

    public static class Guardian
    extends Statue {
        public Guardian() {
            this.spriteClass = GuardianSprite.class;
            this.EXP = 0;
            this.state = this.WANDERING;
            this.levelGenStatue = false;
        }

        @Override
        public void createWeapon(boolean useDecks) {
            this.weapon = (MeleeWeapon)Generator.randomUsingDefaults(Generator.Category.WEAPON);
            this.weapon.cursed = false;
            this.weapon.enchant(null);
            this.weapon.level(0);
        }

        @Override
        public void beckon(int cell) {
            this.notice();
            if (this.state != this.HUNTING) {
                this.state = this.WANDERING;
            }
            this.target = cell;
        }
    }

    public static class GuardianSprite
    extends StatueSprite {
        public GuardianSprite() {
            this.tint(0.0f, 0.0f, 1.0f, 0.2f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            this.tint(0.0f, 0.0f, 1.0f, 0.2f);
        }
    }
}

