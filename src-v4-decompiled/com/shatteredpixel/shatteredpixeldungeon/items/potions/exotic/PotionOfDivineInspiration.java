/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class PotionOfDivineInspiration
extends ExoticPotion {
    protected static boolean identifiedByUse = false;

    public PotionOfDivineInspiration() {
        this.icon = ItemSpriteSheet.Icons.POTION_DIVINE;
        this.talentFactor = 2.0f;
    }

    @Override
    protected void drink(Hero hero) {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(hero.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        final boolean[] enabled = new boolean[5];
        enabled[4] = true;
        enabled[3] = true;
        enabled[2] = true;
        enabled[1] = true;
        DivineInspirationTracker tracker = hero.buff(DivineInspirationTracker.class);
        if (tracker != null) {
            boolean allBoosted = true;
            for (int i = 1; i <= 4; ++i) {
                if (tracker.isBoosted(i)) {
                    enabled[i] = false;
                    continue;
                }
                allBoosted = false;
            }
            if (allBoosted) {
                GLog.w(Messages.get(this, "no_more_points", new Object[0]), new Object[0]);
                return;
            }
        }
        GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.trueName()), Messages.get(PotionOfDivineInspiration.class, "select_tier", new Object[0]), new String[]{Messages.titleCase(Messages.get(TalentsPane.class, "tier", 1)), Messages.titleCase(Messages.get(TalentsPane.class, "tier", 2)), Messages.titleCase(Messages.get(TalentsPane.class, "tier", 3)), Messages.titleCase(Messages.get(TalentsPane.class, "tier", 4))}){

            @Override
            protected boolean enabled(int index) {
                return enabled[index + 1];
            }

            @Override
            protected void onSelect(int index) {
                super.onSelect(index);
                if (index != -1) {
                    Buff.affect(curUser, DivineInspirationTracker.class).setBoosted(index + 1);
                    if (!identifiedByUse) {
                        curItem.detach(curUser.belongings.backpack);
                    }
                    identifiedByUse = false;
                    curUser.busy();
                    curUser.sprite.operate(curUser.pos);
                    curUser.spendAndNext(1.0f);
                    boolean unspentTalents = false;
                    for (int i = 1; i <= Dungeon.hero.talents.size(); ++i) {
                        if (Dungeon.hero.talentPointsAvailable(i) <= 0) continue;
                        unspentTalents = true;
                        break;
                    }
                    if (unspentTalents) {
                        StatusPane.talentBlink = 10.0f;
                        WndHero.lastIdx = 1;
                    }
                    GameScene.showlevelUpStars();
                    Sample.INSTANCE.play("sounds/drink.mp3");
                    Sample.INSTANCE.playDelayed("sounds/levelup.mp3", 0.3f, 0.7f, 1.2f);
                    Sample.INSTANCE.playDelayed("sounds/levelup.mp3", 0.6f, 0.7f, 1.2f);
                    new Flare(6, 32.0f).color(0xFFFF00, true).show(curUser.sprite, 2.0f);
                    GLog.p(Messages.get(PotionOfDivineInspiration.class, "bonus", new Object[0]), new Object[0]);
                    if (!PotionOfDivineInspiration.this.anonymous) {
                        Catalog.countUse(PotionOfDivineInspiration.class);
                        if (Random.Float() < PotionOfDivineInspiration.this.talentChance) {
                            Talent.onPotionUsed(curUser, curUser.pos, PotionOfDivineInspiration.this.talentFactor);
                        }
                    }
                }
            }

            @Override
            public void onBackPressed() {
                if (!identifiedByUse) {
                    super.onBackPressed();
                }
            }
        });
    }

    public static class DivineInspirationTracker
    extends Buff {
        private boolean[] boostedTiers;
        private static final String BOOSTED_TIERS = "boosted_tiers";

        public DivineInspirationTracker() {
            this.type = Buff.buffType.POSITIVE;
            this.revivePersists = true;
            this.boostedTiers = new boolean[5];
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOOSTED_TIERS, this.boostedTiers);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.boostedTiers = bundle.getBooleanArray(BOOSTED_TIERS);
        }

        public void setBoosted(int tier) {
            this.boostedTiers[tier] = true;
        }

        public boolean isBoosted(int tier) {
            return this.boostedTiers[tier];
        }
    }
}

