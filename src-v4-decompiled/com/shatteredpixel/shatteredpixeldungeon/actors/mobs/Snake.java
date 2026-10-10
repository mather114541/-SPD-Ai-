/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SnakeSprite;
import com.watabou.utils.Random;

public class Snake
extends Mob {
    private static int dodges = 0;

    public Snake() {
        this.spriteClass = SnakeSprite.class;
        this.HT = 4;
        this.HP = 4;
        this.defenseSkill = 25;
        this.EXP = 2;
        this.maxLvl = 7;
        this.loot = Generator.Category.SEED;
        this.lootChance = 0.25f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

    @Override
    public String defenseVerb() {
        if (Dungeon.level.heroFOV[this.pos]) {
            ++dodges;
        }
        if (dodges >= 2 && !Document.ADVENTURERS_GUIDE.isPageRead("Surprise_Attacks") || dodges >= 4 && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1)) {
            GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, "Surprise_Attacks");
            dodges = 0;
        }
        return super.defenseVerb();
    }
}

