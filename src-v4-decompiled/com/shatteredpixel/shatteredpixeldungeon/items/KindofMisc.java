/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Random;

public abstract class KindofMisc
extends EquipableItem {
    @Override
    public boolean doEquip(final Hero hero) {
        boolean equipFull = false;
        if (this instanceof Artifact && hero.belongings.artifact != null && hero.belongings.misc != null) {
            if (hero.belongings.misc instanceof Ring && hero.belongings.ring == null) {
                hero.belongings.ring = (Ring)hero.belongings.misc;
                hero.belongings.misc = null;
            } else {
                equipFull = true;
            }
        } else if (this instanceof Ring && hero.belongings.misc != null && hero.belongings.ring != null) {
            if (hero.belongings.misc instanceof Artifact && hero.belongings.artifact == null) {
                hero.belongings.artifact = (Artifact)hero.belongings.misc;
                hero.belongings.misc = null;
            } else {
                equipFull = true;
            }
        }
        if (equipFull) {
            final KindofMisc[] miscs = new KindofMisc[]{hero.belongings.artifact, hero.belongings.misc, hero.belongings.ring};
            final boolean[] enabled = new boolean[3];
            enabled[0] = miscs[0] != null;
            enabled[1] = miscs[1] != null;
            boolean bl = enabled[2] = miscs[2] != null;
            if (this instanceof Ring && hero.belongings.misc instanceof Ring) {
                enabled[0] = false;
            } else if (this instanceof Artifact && hero.belongings.misc instanceof Artifact) {
                enabled[2] = false;
            }
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.get(KindofMisc.class, "unequip_title", new Object[0]), Messages.get(KindofMisc.class, "unequip_message", new Object[0]), new String[]{miscs[0] == null ? "---" : Messages.titleCase(miscs[0].title()), miscs[1] == null ? "---" : Messages.titleCase(miscs[1].title()), miscs[2] == null ? "---" : Messages.titleCase(miscs[2].title())}){

                @Override
                protected void onSelect(int index) {
                    KindofMisc equipped = miscs[index];
                    int slot = Dungeon.quickslot.getSlot(KindofMisc.this);
                    EquipableItem.slotOfUnequipped = -1;
                    Dungeon.hero.belongings.backpack.items.remove(KindofMisc.this);
                    if (equipped.doUnequip(hero, true, false)) {
                        if (index == 0 && KindofMisc.this instanceof Ring) {
                            hero.belongings.artifact = (Artifact)hero.belongings.misc;
                            hero.belongings.misc = null;
                        } else if (index == 2 && KindofMisc.this instanceof Artifact) {
                            hero.belongings.ring = (Ring)hero.belongings.misc;
                            hero.belongings.misc = null;
                        }
                        Dungeon.hero.belongings.backpack.items.add(KindofMisc.this);
                        KindofMisc.this.doEquip(hero);
                    } else {
                        Dungeon.hero.belongings.backpack.items.add(KindofMisc.this);
                    }
                    if (slot != -1) {
                        Dungeon.quickslot.setSlot(slot, KindofMisc.this);
                    } else if (EquipableItem.slotOfUnequipped != -1 && KindofMisc.this.defaultAction() != null) {
                        Dungeon.quickslot.setSlot(EquipableItem.slotOfUnequipped, KindofMisc.this);
                    }
                    Item.updateQuickslot();
                }

                @Override
                protected boolean enabled(int index) {
                    return enabled[index];
                }
            });
            return false;
        }
        if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION) && this.cursed && !this.cursedKnown && Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
            this.cursedKnown = true;
            GLog.p(Messages.get(this, "curse_detected", new Object[0]), new Object[0]);
            return false;
        }
        if (this instanceof Artifact) {
            if (hero.belongings.artifact == null) {
                hero.belongings.artifact = (Artifact)this;
            } else {
                hero.belongings.misc = (Artifact)this;
            }
        } else if (this instanceof Ring) {
            if (hero.belongings.ring == null) {
                hero.belongings.ring = (Ring)this;
            } else {
                hero.belongings.misc = (Ring)this;
            }
        }
        this.detach(hero.belongings.backpack);
        Talent.onItemEquipped(hero, this);
        this.activate(hero);
        this.cursedKnown = true;
        if (this.cursed) {
            KindofMisc.equipCursed(hero);
            GLog.n(Messages.get(this, "equip_cursed", this), new Object[0]);
        }
        hero.spendAndNext(this.timeToEquip(hero));
        return true;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (hero.belongings.artifact == this) {
                hero.belongings.artifact = null;
            } else if (hero.belongings.misc == this) {
                hero.belongings.misc = null;
            } else if (hero.belongings.ring == this) {
                hero.belongings.ring = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isEquipped(Hero hero) {
        return hero != null && (hero.belongings.artifact() == this || hero.belongings.misc() == this || hero.belongings.ring() == this);
    }
}

