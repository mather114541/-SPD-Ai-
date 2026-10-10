/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class TalismanOfForesight
extends Artifact {
    public static final String AC_SCRY = "SCRY";
    public CellSelector.Listener scry;
    private static final String WARN = "warn";
    private boolean warn;

    public TalismanOfForesight() {
        this.image = ItemSpriteSheet.ARTIFACT_TALISMAN;
        this.exp = 0;
        this.levelCap = 10;
        this.charge = 0;
        this.partialCharge = 0.0f;
        this.chargeCap = 100;
        this.defaultAction = AC_SCRY;
        this.scry = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer target) {
                if (target != null && target != curUser.pos) {
                    float dist;
                    if (Dungeon.level.adjacent(target, curUser.pos)) {
                        target = target + (target - curUser.pos);
                    }
                    if ((dist = Dungeon.level.trueDistance(curUser.pos, target)) >= 3.0f && dist > TalismanOfForesight.this.maxDist()) {
                        Ballistica trajectory = new Ballistica(curUser.pos, target, 1);
                        for (int i = 0; i < trajectory.path.size() && Dungeon.level.trueDistance(curUser.pos, trajectory.path.get(i)) <= TalismanOfForesight.this.maxDist(); ++i) {
                            target = trajectory.path.get(i);
                        }
                        dist = Dungeon.level.trueDistance(curUser.pos, target);
                    }
                    float angle = Math.round(200.0f * (float)Math.pow(0.92, dist));
                    ConeAOE cone = new ConeAOE(new Ballistica(curUser.pos, target, 1), angle);
                    int earnedExp = 0;
                    boolean noticed = false;
                    for (int cell : cone.cells) {
                        Heap h;
                        Char ch;
                        GameScene.checkedCell(cell, curUser.pos);
                        if (Dungeon.level.discoverable[cell] && !Dungeon.level.mapped[cell] && !Dungeon.level.visited[cell]) {
                            Dungeon.level.mapped[cell] = true;
                            ++earnedExp;
                        }
                        if (Dungeon.level.secret[cell]) {
                            int oldValue = Dungeon.level.map[cell];
                            GameScene.discoverTile(cell, oldValue);
                            Dungeon.level.discover(cell);
                            ScrollOfMagicMapping.discover(cell);
                            noticed = true;
                            if (oldValue == 17) {
                                earnedExp += 10;
                            } else if (oldValue == 16) {
                                earnedExp += 100;
                            }
                        }
                        if ((ch = Actor.findChar(cell)) != null && (ch.alignment != Char.Alignment.NEUTRAL || ch instanceof Mimic) && ch.alignment != curUser.alignment) {
                            Buff.append((Char)curUser, CharAwareness.class, (float)((float)(5 + 2 * TalismanOfForesight.this.level()))).charID = ch.id();
                            Artifact.artifactProc(ch, TalismanOfForesight.this.visiblyUpgraded(), (int)(3.0f + dist * 1.08f));
                            if (!curUser.fieldOfView[ch.pos]) {
                                earnedExp += 10;
                            }
                        }
                        if ((h = (Heap)Dungeon.level.heaps.get(cell)) == null) continue;
                        Buff.append((Char)curUser, HeapAwareness.class, (float)((float)(5 + 2 * TalismanOfForesight.this.level()))).pos = h.pos;
                        if (h.seen) continue;
                        earnedExp += 10;
                    }
                    TalismanOfForesight.this.exp += earnedExp;
                    if (TalismanOfForesight.this.exp >= 100 + 50 * TalismanOfForesight.this.level() && TalismanOfForesight.this.level() < TalismanOfForesight.this.levelCap) {
                        TalismanOfForesight.this.exp -= 100 + 50 * TalismanOfForesight.this.level();
                        TalismanOfForesight.this.upgrade();
                        Catalog.countUse(TalismanOfForesight.class);
                        GLog.p(Messages.get(TalismanOfForesight.class, "levelup", new Object[0]), new Object[0]);
                    }
                    Item.updateQuickslot();
                    TalismanOfForesight.this.charge = (int)((float)TalismanOfForesight.this.charge - (3.0f + dist * 1.08f));
                    TalismanOfForesight.this.partialCharge -= dist * 1.08f % 1.0f;
                    if (TalismanOfForesight.this.partialCharge < 0.0f && TalismanOfForesight.this.charge > 0) {
                        TalismanOfForesight.this.partialCharge += 1.0f;
                        --TalismanOfForesight.this.charge;
                    }
                    while (TalismanOfForesight.this.charge < 0) {
                        ++TalismanOfForesight.this.charge;
                        TalismanOfForesight.this.partialCharge -= 1.0f;
                    }
                    Invisibility.dispel(curUser);
                    Talent.onArtifactUsed(Dungeon.hero);
                    Item.updateQuickslot();
                    Dungeon.observe();
                    Dungeon.hero.checkVisibleMobs();
                    GameScene.updateFog();
                    curUser.sprite.zap(target);
                    curUser.spendAndNext(1.0f);
                    Sample.INSTANCE.play("sounds/scan.mp3");
                    if (noticed) {
                        Sample.INSTANCE.play("sounds/secret.mp3");
                    }
                }
            }

            @Override
            public String prompt() {
                return Messages.get(TalismanOfForesight.class, "prompt", new Object[0]);
            }
        };
        this.warn = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_SCRY);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_SCRY)) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.charge < 5) {
                GLog.i(Messages.get(this, "low_charge", new Object[0]), new Object[0]);
            } else {
                GameScene.selectCell(this.scry);
            }
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new Foresight();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.charge < this.chargeCap) {
            this.partialCharge += 2.0f * amount;
            while (this.partialCharge >= 1.0f) {
                ++this.charge;
                this.partialCharge -= 1.0f;
            }
            if (this.charge >= this.chargeCap) {
                this.charge = this.chargeCap;
                this.partialCharge = 0.0f;
                GLog.p(Messages.get(TalismanOfForesight.class, "full_charge", new Object[0]), new Object[0]);
            }
            TalismanOfForesight.updateQuickslot();
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            desc = !this.cursed ? (String)desc + "\n\n" + Messages.get(this, "desc_worn", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]);
        }
        return desc;
    }

    private float maxDist() {
        return Math.min((float)(5 + 2 * this.level()), (float)(this.charge - 3) / 1.08f);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WARN, this.warn);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.warn = bundle.getBoolean(WARN);
    }

    public class Foresight
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            this.spend(1.0f);
            this.checkAwareness();
            if (TalismanOfForesight.this.charge < TalismanOfForesight.this.chargeCap && !TalismanOfForesight.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                float chargeGain = 0.05f + (float)TalismanOfForesight.this.level() * 0.005f;
                TalismanOfForesight.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (TalismanOfForesight.this.partialCharge >= 1.0f) {
                    TalismanOfForesight.this.partialCharge -= 1.0f;
                    ++TalismanOfForesight.this.charge;
                    if (TalismanOfForesight.this.charge >= TalismanOfForesight.this.chargeCap) {
                        TalismanOfForesight.this.partialCharge = 0.0f;
                        GLog.p(Messages.get(TalismanOfForesight.class, "full_charge", new Object[0]), new Object[0]);
                    }
                    Item.updateQuickslot();
                }
            }
            return true;
        }

        public void checkAwareness() {
            int by;
            int ay;
            int bx;
            boolean smthFound = false;
            int distance = 3;
            int cx = this.target.pos % Dungeon.level.width();
            int cy = this.target.pos / Dungeon.level.width();
            int ax = cx - distance;
            if (ax < 0) {
                ax = 0;
            }
            if ((bx = cx + distance) >= Dungeon.level.width()) {
                bx = Dungeon.level.width() - 1;
            }
            if ((ay = cy - distance) < 0) {
                ay = 0;
            }
            if ((by = cy + distance) >= Dungeon.level.height()) {
                by = Dungeon.level.height() - 1;
            }
            for (int y = ay; y <= by; ++y) {
                int x = ax;
                int p = ax + y * Dungeon.level.width();
                while (x <= bx) {
                    if (Dungeon.level.heroFOV[p] && Dungeon.level.secret[p] && Dungeon.level.map[p] != 16 && Dungeon.level.traps.get(p) != null && ((Trap)Dungeon.level.traps.get((int)p)).canBeSearched) {
                        smthFound = true;
                    }
                    ++x;
                    ++p;
                }
            }
            if (smthFound && !TalismanOfForesight.this.cursed && this.target.buff(MagicImmune.class) == null) {
                if (!TalismanOfForesight.this.warn) {
                    GLog.w(Messages.get(this, "uneasy", new Object[0]), new Object[0]);
                    if (this.target instanceof Hero) {
                        ((Hero)this.target).interrupt();
                    }
                    TalismanOfForesight.this.warn = true;
                }
            } else {
                TalismanOfForesight.this.warn = false;
            }
        }

        public void charge(int boost) {
            if (!TalismanOfForesight.this.cursed && this.target.buff(MagicImmune.class) == null) {
                TalismanOfForesight.this.charge = Math.min(TalismanOfForesight.this.charge + boost, TalismanOfForesight.this.chargeCap);
                Item.updateQuickslot();
            }
        }

        @Override
        public int icon() {
            if (TalismanOfForesight.this.warn) {
                return 32;
            }
            return 127;
        }
    }

    public static class HeapAwareness
    extends FlavourBuff {
        public int pos;
        public int depth = Dungeon.depth;
        public int branch = Dungeon.branch;
        private static final String POS = "pos";
        private static final String DEPTH = "depth";
        private static final String BRANCH = "branch";

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.pos = bundle.getInt(POS);
            this.depth = bundle.getInt(DEPTH);
            this.branch = bundle.getInt(BRANCH);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, this.pos);
            bundle.put(DEPTH, this.depth);
            bundle.put(BRANCH, this.branch);
        }
    }

    public static class CharAwareness
    extends FlavourBuff {
        public int charID;
        private static final String CHAR_ID = "char_id";

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.charID = bundle.getInt(CHAR_ID);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CHAR_ID, this.charID);
        }
    }
}

