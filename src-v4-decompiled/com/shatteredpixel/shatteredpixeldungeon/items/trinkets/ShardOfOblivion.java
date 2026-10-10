/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class ShardOfOblivion
extends Trinket {
    public static final String AC_IDENTIFY = "IDENTIFY";
    public static WndBag.ItemSelector identifySelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(ShardOfOblivion.class, "identify_prompt", new Object[0]);
        }

        @Override
        public boolean itemSelectable(Item item) {
            return !item.isIdentified() && item.isUpgradable();
        }

        @Override
        public void onSelect(Item item) {
            if (item == null) {
                return;
            }
            boolean ready = false;
            if (item instanceof Weapon) {
                ready = ((Weapon)item).readyToIdentify();
                if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.ADVENTURERS_INTUITION) == 2) {
                    ready = true;
                }
            } else if (item instanceof Armor) {
                ready = ((Armor)item).readyToIdentify();
                if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.VETERANS_INTUITION) == 2) {
                    ready = true;
                }
            } else if (item instanceof Ring) {
                ready = ((Ring)item).readyToIdentify();
                if (item.isEquipped(curUser) && curUser.pointsInTalent(Talent.THIEFS_INTUITION) == 2) {
                    ready = true;
                }
            } else if (item instanceof Wand) {
                ready = ((Wand)item).readyToIdentify();
            }
            if (ready) {
                item.identify();
                Badges.validateItemLevelAquired(item);
                curUser.sprite.operate(curUser.pos);
                Sample.INSTANCE.play("sounds/teleport.mp3");
                curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0.0f, -16.0f)));
                GLog.p(Messages.get(ShardOfOblivion.class, "identify", new Object[0]), new Object[0]);
            } else {
                GLog.w(Messages.get(ShardOfOblivion.class, "identify_not_yet", new Object[0]), new Object[0]);
            }
        }
    };

    public ShardOfOblivion() {
        this.image = ItemSpriteSheet.OBLIVION_SHARD;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", this.buffedLvl() + 1);
        }
        return Messages.get(this, "stats_desc", 1);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_IDENTIFY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_IDENTIFY)) {
            curUser = hero;
            curItem = this;
            GameScene.selectItem(identifySelector);
        } else {
            super.execute(hero, action);
        }
    }

    public static boolean passiveIDDisabled() {
        return ShardOfOblivion.trinketLevel(ShardOfOblivion.class) >= 0;
    }

    public static float lootChanceMultiplier() {
        return ShardOfOblivion.lootChanceMultiplier(ShardOfOblivion.trinketLevel(ShardOfOblivion.class));
    }

    public static float lootChanceMultiplier(int level) {
        if (level < 0) {
            return 1.0f;
        }
        int wornUnIDed = 0;
        if (Dungeon.hero.belongings.weapon() != null && !Dungeon.hero.belongings.weapon().isIdentified()) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.belongings.secondWep() != null && !Dungeon.hero.belongings.secondWep().isIdentified()) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.belongings.armor() != null && !Dungeon.hero.belongings.armor().isIdentified()) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.belongings.ring() != null && !Dungeon.hero.belongings.ring().isIdentified()) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.belongings.misc() != null && !Dungeon.hero.belongings.misc().isIdentified()) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.buff(WandUseTracker.class) != null) {
            ++wornUnIDed;
        }
        if (Dungeon.hero.buff(ThrownUseTracker.class) != null) {
            ++wornUnIDed;
        }
        wornUnIDed = Math.min(wornUnIDed, level + 1);
        return 1.0f + 0.2f * (float)wornUnIDed;
    }

    public static class WandUseTracker
    extends FlavourBuff {
        public static float DURATION = 50.0f;

        public WandUseTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 72;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.6f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }
    }

    public static class ThrownUseTracker
    extends FlavourBuff {
        public static float DURATION = 50.0f;

        public ThrownUseTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 85;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.6f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }
    }
}

