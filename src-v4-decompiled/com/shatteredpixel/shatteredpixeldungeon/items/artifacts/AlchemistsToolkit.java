/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class AlchemistsToolkit
extends Artifact {
    public static final String AC_BREW = "BREW";
    public static final String AC_ENERGIZE = "ENERGIZE";
    private float warmUpDelay;
    private static final String WARM_UP = "warm_up";

    public AlchemistsToolkit() {
        this.image = ItemSpriteSheet.ARTIFACT_TOOLKIT;
        this.defaultAction = AC_BREW;
        this.levelCap = 10;
        this.charge = 0;
        this.partialCharge = 0.0f;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_BREW);
            if (this.level() < this.levelCap) {
                actions.add(AC_ENERGIZE);
            }
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_BREW)) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(this, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else if (this.warmUpDelay > 0.0f) {
                GLog.w(Messages.get(this, "not_ready", new Object[0]), new Object[0]);
            } else {
                AlchemyScene.assignToolkit(this);
                Game.switchScene(AlchemyScene.class);
            }
        } else if (action.equals(AC_ENERGIZE)) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(this, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else if (Dungeon.energy < 6) {
                GLog.w(Messages.get(this, "need_energy", new Object[0]), new Object[0]);
            } else {
                final int maxLevels = Math.min(this.levelCap - this.level(), Dungeon.energy / 6);
                String[] options = maxLevels > 1 ? new String[]{Messages.get(this, "energize_1", new Object[0]), Messages.get(this, "energize_all", 6 * maxLevels, maxLevels)} : new String[]{Messages.get(this, "energize_1", new Object[0])};
                GameScene.show(new WndOptions(new ItemSprite(this.image), Messages.titleCase(this.name()), Messages.get(this, "energize_desc", new Object[0]), options){

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            Dungeon.energy -= 6;
                            Sample.INSTANCE.play("sounds/drink.mp3");
                            Sample.INSTANCE.playDelayed("sounds/puff.mp3", 0.5f);
                            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                            AlchemistsToolkit.this.upgrade();
                            Catalog.countUse(AlchemistsToolkit.class);
                        } else if (index == 1) {
                            Dungeon.energy -= 6 * maxLevels;
                            Sample.INSTANCE.play("sounds/drink.mp3");
                            Sample.INSTANCE.playDelayed("sounds/puff.mp3", 0.5f);
                            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                            AlchemistsToolkit.this.upgrade(maxLevels);
                            Catalog.countUses(AlchemistsToolkit.class, maxLevels);
                        }
                    }

                    @Override
                    protected boolean hasIcon(int index) {
                        return true;
                    }

                    @Override
                    protected Image getIcon(int index) {
                        return new ItemSprite(ItemSpriteSheet.ENERGY);
                    }
                });
            }
        }
        AlchemistsToolkit.updateQuickslot();
    }

    @Override
    public String status() {
        if (this.isEquipped(Dungeon.hero) && this.warmUpDelay > 0.0f && !this.cursed) {
            return Messages.format("%d%%", Math.max(0, 100 - (int)this.warmUpDelay));
        }
        return super.status();
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new kitEnergy();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (target.buff(MagicImmune.class) != null) {
            return;
        }
        this.partialCharge += 0.25f * amount;
        while (this.partialCharge >= 1.0f) {
            this.partialCharge -= 1.0f;
            ++this.charge;
            AlchemistsToolkit.updateQuickslot();
        }
    }

    public int availableEnergy() {
        return this.charge;
    }

    public int consumeEnergy(int amount) {
        int result = amount - this.charge;
        this.charge = Math.max(0, this.charge - amount);
        Talent.onArtifactUsed(Dungeon.hero);
        return Math.max(0, result);
    }

    @Override
    public String desc() {
        Object result = Messages.get(this, "desc", new Object[0]);
        if (this.isEquipped(Dungeon.hero)) {
            result = this.cursed ? (String)result + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]) : (this.warmUpDelay > 0.0f ? (String)result + "\n\n" + Messages.get(this, "desc_warming", new Object[0]) : (String)result + "\n\n" + Messages.get(this, "desc_hint", new Object[0]));
        }
        return result;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            this.warmUpDelay = 101.0f;
            return true;
        }
        return false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WARM_UP, this.warmUpDelay);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.warmUpDelay = bundle.getFloat(WARM_UP);
    }

    public class kitEnergy
    extends Artifact.ArtifactBuff {
        public kitEnergy() {
            super(AlchemistsToolkit.this);
        }

        @Override
        public boolean act() {
            if (AlchemistsToolkit.this.warmUpDelay > 0.0f) {
                if (AlchemistsToolkit.this.level() == 10) {
                    AlchemistsToolkit.this.warmUpDelay = 0.0f;
                } else if (AlchemistsToolkit.this.warmUpDelay == 101.0f) {
                    AlchemistsToolkit.this.warmUpDelay = 100.0f;
                } else if (!AlchemistsToolkit.this.cursed && this.target.buff(MagicImmune.class) == null) {
                    float turnsToWarmUp = (int)Math.pow(10 - AlchemistsToolkit.this.level(), 2.0);
                    AlchemistsToolkit.this.warmUpDelay -= 100.0f / turnsToWarmUp;
                }
                Item.updateQuickslot();
            }
            this.spend(1.0f);
            return true;
        }

        public void gainCharge(float levelPortion) {
            if (AlchemistsToolkit.this.cursed || this.target.buff(MagicImmune.class) != null) {
                return;
            }
            float chargeGain = (float)(2 + AlchemistsToolkit.this.level()) * levelPortion;
            AlchemistsToolkit.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
            while (AlchemistsToolkit.this.partialCharge >= 1.0f) {
                ++AlchemistsToolkit.this.charge;
                AlchemistsToolkit.this.partialCharge -= 1.0f;
                Item.updateQuickslot();
            }
        }
    }
}

