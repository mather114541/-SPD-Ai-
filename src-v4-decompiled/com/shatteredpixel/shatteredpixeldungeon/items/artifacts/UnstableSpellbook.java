/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class UnstableSpellbook
extends Artifact {
    public static final String AC_READ = "READ";
    public static final String AC_ADD = "ADD";
    private final ArrayList<Class> scrolls;
    private static final String SCROLLS = "scrolls";
    protected WndBag.ItemSelector itemSelector;

    public UnstableSpellbook() {
        this.image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;
        this.levelCap = 10;
        this.charge = (int)((float)this.level() * 0.6f) + 2;
        this.partialCharge = 0.0f;
        this.chargeCap = (int)((float)this.level() * 0.6f) + 2;
        this.defaultAction = AC_READ;
        this.scrolls = new ArrayList();
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(UnstableSpellbook.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return ScrollHolder.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof Scroll && item.isIdentified() && UnstableSpellbook.this.scrolls.contains(item.getClass());
            }

            @Override
            public void onSelect(Item item) {
                if (item != null && item instanceof Scroll && item.isIdentified()) {
                    Hero hero = Dungeon.hero;
                    for (int i = 0; i <= 1 && i < UnstableSpellbook.this.scrolls.size(); ++i) {
                        if (!UnstableSpellbook.this.scrolls.get(i).equals(item.getClass())) continue;
                        hero.sprite.operate(hero.pos);
                        hero.busy();
                        hero.spend(2.0f);
                        Sample.INSTANCE.play("sounds/burning.mp3");
                        hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
                        UnstableSpellbook.this.scrolls.remove(i);
                        item.detach(hero.belongings.backpack);
                        UnstableSpellbook.this.upgrade();
                        Catalog.countUse(UnstableSpellbook.class);
                        GLog.i(Messages.get(UnstableSpellbook.class, "infuse_scroll", new Object[0]), new Object[0]);
                        return;
                    }
                    GLog.w(Messages.get(UnstableSpellbook.class, "unable_scroll", new Object[0]), new Object[0]);
                } else if (item instanceof Scroll && !item.isIdentified()) {
                    GLog.w(Messages.get(UnstableSpellbook.class, "unknown_scroll", new Object[0]), new Object[0]);
                }
            }
        };
        this.setupScrolls();
    }

    private void setupScrolls() {
        this.scrolls.clear();
        Class<?>[] scrollClasses = Generator.Category.SCROLL.classes;
        float[] probs = (float[])Generator.Category.SCROLL.defaultProbsTotal.clone();
        int i = Random.chances(probs);
        while (i != -1) {
            this.scrolls.add(scrollClasses[i]);
            probs[i] = 0.0f;
            i = Random.chances(probs);
        }
        this.scrolls.remove(ScrollOfTransmutation.class);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && this.charge > 0 && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_READ);
        }
        if (this.isEquipped(hero) && this.level() < this.levelCap && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_ADD);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_READ)) {
            if (hero.buff(Blindness.class) != null) {
                GLog.w(Messages.get(this, "blinded", new Object[0]), new Object[0]);
            } else if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.charge <= 0) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.i(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                this.doReadEffect(hero);
            }
        } else if (action.equals(AC_ADD)) {
            GameScene.selectItem(this.itemSelector);
        }
    }

    public void doReadEffect(Hero hero) {
        Scroll scroll;
        --this.charge;
        while ((scroll = (Scroll)Generator.randomUsingDefaults(Generator.Category.SCROLL)) == null || (scroll instanceof ScrollOfIdentify || scroll instanceof ScrollOfRemoveCurse || scroll instanceof ScrollOfMagicMapping) && Random.Int(2) == 0 || scroll instanceof ScrollOfTransmutation) {
        }
        scroll.anonymize();
        scroll.talentChance = 0.0f;
        curItem = scroll;
        curUser = hero;
        if (this.charge > 0 && !this.scrolls.contains(scroll.getClass())) {
            final Scroll fScroll = scroll;
            final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
            handler.scroll = scroll;
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.get(this, "prompt", new Object[0]), Messages.get(this, "read_empowered", new Object[0]), new String[]{scroll.trueName(), Messages.get(ExoticScroll.regToExo.get(scroll.getClass()), "name", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    handler.detach();
                    if (index == 1) {
                        Scroll scroll = Reflection.newInstance(ExoticScroll.regToExo.get(fScroll.getClass()));
                        curItem = scroll;
                        --UnstableSpellbook.this.charge;
                        scroll.anonymize();
                        scroll.talentChance = 0.0f;
                        UnstableSpellbook.this.checkForArtifactProc(curUser, scroll);
                        scroll.doRead();
                        Talent.onArtifactUsed(Dungeon.hero);
                    } else {
                        UnstableSpellbook.this.checkForArtifactProc(curUser, fScroll);
                        fScroll.doRead();
                        Talent.onArtifactUsed(Dungeon.hero);
                    }
                    Item.updateQuickslot();
                }

                @Override
                public void onBackPressed() {
                }
            });
        } else {
            this.checkForArtifactProc(curUser, scroll);
            scroll.doRead();
            Talent.onArtifactUsed(Dungeon.hero);
        }
        UnstableSpellbook.updateQuickslot();
    }

    private void checkForArtifactProc(Hero user, Scroll scroll) {
        block3: {
            block2: {
                if (!(scroll instanceof ScrollOfLullaby) && !(scroll instanceof ScrollOfRemoveCurse) && !(scroll instanceof ScrollOfTerror)) break block2;
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (!Dungeon.level.heroFOV[mob.pos]) continue;
                    UnstableSpellbook.artifactProc(mob, this.visiblyUpgraded(), 1);
                }
                break block3;
            }
            if (!(scroll instanceof ScrollOfRage)) break block3;
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                UnstableSpellbook.artifactProc(mob, this.visiblyUpgraded(), 1);
            }
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new bookRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.charge < this.chargeCap && !this.cursed && target.buff(MagicImmune.class) == null) {
            this.partialCharge += 0.1f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
            }
            UnstableSpellbook.updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        this.chargeCap = (int)((float)(this.level() + 1) * 0.6f) + 2;
        while (!this.scrolls.isEmpty() && this.scrolls.size() > this.levelCap - 1 - this.level()) {
            this.scrolls.remove(0);
        }
        return super.upgrade();
    }

    @Override
    public void resetForTrinity(int visibleLevel) {
        super.resetForTrinity(visibleLevel);
        this.setupScrolls();
        while (!this.scrolls.isEmpty() && this.scrolls.size() > this.levelCap - 1 - this.level()) {
            this.scrolls.remove(0);
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            if (this.cursed) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]);
            }
            if (this.level() < this.levelCap && this.scrolls.size() > 0) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_index", new Object[0]);
                desc = (String)desc + "\n_" + Messages.get(this.scrolls.get(0), "name", new Object[0]) + "_";
                if (this.scrolls.size() > 1) {
                    desc = (String)desc + "\n_" + Messages.get(this.scrolls.get(1), "name", new Object[0]) + "_";
                }
            }
        }
        if (this.level() > 0) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_empowered", new Object[0]);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SCROLLS, this.scrolls.toArray(new Class[this.scrolls.size()]));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.scrolls.clear();
        if (bundle.contains(SCROLLS) && bundle.getClassArray(SCROLLS) != null) {
            for (Class scroll : bundle.getClassArray(SCROLLS)) {
                if (scroll == null) continue;
                this.scrolls.add(scroll);
            }
        }
    }

    public static class ExploitHandler
    extends Buff {
        public Scroll scroll;

        public ExploitHandler() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            curUser = Dungeon.hero;
            curItem = this.scroll;
            this.scroll.anonymize();
            this.scroll.talentChance = 0.0f;
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    scroll.doRead();
                    Item.updateQuickslot();
                }
            });
            this.detach();
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("scroll", this.scroll);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.scroll = (Scroll)bundle.get("scroll");
        }
    }

    public class bookRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (UnstableSpellbook.this.charge < UnstableSpellbook.this.chargeCap && !UnstableSpellbook.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                float chargeGain = 1.0f / (120.0f - (float)(UnstableSpellbook.this.chargeCap - UnstableSpellbook.this.charge) * 5.0f);
                UnstableSpellbook.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (UnstableSpellbook.this.partialCharge >= 1.0f) {
                    UnstableSpellbook.this.partialCharge -= 1.0f;
                    ++UnstableSpellbook.this.charge;
                    if (UnstableSpellbook.this.charge != UnstableSpellbook.this.chargeCap) continue;
                    UnstableSpellbook.this.partialCharge = 0.0f;
                }
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }
    }
}

