/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LotusSprite;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class WandOfRegrowth
extends Wand {
    private int totChrgUsed;
    private int chargesOverLimit;
    ConeAOE cone;
    int target;
    private static final String TOTAL = "totChrgUsed";
    private static final String OVER = "chargesOverLimit";

    public WandOfRegrowth() {
        this.image = ItemSpriteSheet.WAND_REGROWTH;
        this.collisionProperties = 0;
        this.totChrgUsed = 0;
        this.chargesOverLimit = 0;
    }

    @Override
    public boolean tryToZap(Hero owner, int target) {
        if (super.tryToZap(owner, target)) {
            this.target = target;
            return true;
        }
        return false;
    }

    @Override
    public void onZap(Ballistica bolt) {
        Object l;
        int cell4;
        ArrayList<Integer> cells = new ArrayList<Integer>(this.cone.cells);
        float furrowedChance = 0.0f;
        if (this.totChrgUsed >= this.chargeLimit(Dungeon.hero.lvl)) {
            furrowedChance = (float)(this.chargesOverLimit + 1) / 5.0f;
        }
        int chrgUsed = this.chargesPerCast();
        int grassToPlace = Math.round((3.67f + (float)this.buffedLvl() / 3.0f) * (float)chrgUsed);
        Iterator<Integer> i = cells.iterator();
        while (i.hasNext()) {
            Char ch;
            cell4 = i.next();
            int terr = Dungeon.level.map[cell4];
            if (terr != 1 && terr != 9 && terr != 20 && terr != 2 && terr != 15 && terr != 30) {
                i.remove();
                continue;
            }
            if (Char.hasProp(Actor.findChar(cell4), Char.Property.IMMOVABLE)) {
                i.remove();
                continue;
            }
            if (Dungeon.level.plants.get(cell4) != null) {
                i.remove();
                continue;
            }
            if (terr != 15 && terr != 30) {
                Level.set(cell4, 2);
                GameScene.updateMap(cell4);
            }
            if ((ch = Actor.findChar(cell4)) == null) continue;
            if (ch instanceof DwarfKing) {
                Statistics.qualifiedForBossChallengeBadge = false;
            }
            this.wandProc(ch, this.chargesPerCast());
            Buff.prolong(ch, Roots.class, 4.0f * (float)chrgUsed);
        }
        Random.shuffle(cells);
        if (this.chargesPerCast() >= 3) {
            l = new Lotus();
            ((Lotus)l).setLevel(this.buffedLvl());
            if (cells.contains(this.target) && Actor.findChar(this.target) == null) {
                cells.remove((Object)this.target);
                ((Lotus)l).pos = this.target;
                GameScene.add((Mob)l);
            } else {
                for (int i2 = bolt.path.size() - 1; i2 >= 0; --i2) {
                    int c = bolt.path.get(i2);
                    if (!cells.contains(c) || Actor.findChar(c) != null) continue;
                    cells.remove((Object)c);
                    ((Lotus)l).pos = c;
                    GameScene.add((Mob)l);
                    break;
                }
            }
        }
        l = bolt.path.iterator();
        while (l.hasNext()) {
            cell4 = (Integer)l.next();
            if (grassToPlace <= 0 || !cells.contains(cell4)) continue;
            if (Random.Float() > furrowedChance) {
                Level.set(cell4, 15);
            } else {
                Level.set(cell4, 30);
            }
            GameScene.updateMap(cell4);
            --grassToPlace;
            cells.remove((Object)cell4);
            cells.add(cell4);
        }
        if (!cells.isEmpty() && Random.Float() > furrowedChance && Random.Int(6) < chrgUsed) {
            int cell2 = cells.remove(0);
            Dungeon.level.plant(Random.Int(2) == 0 ? new Seedpod.Seed() : new Dewcatcher.Seed(), cell2);
        }
        if (!cells.isEmpty() && Random.Float() > furrowedChance && Random.Int(3) < chrgUsed) {
            int cell3 = cells.remove(0);
            Dungeon.level.plant((Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED), cell3);
        }
        for (int cell4 : cells) {
            if (grassToPlace <= 0 || bolt.path.contains(cell4)) break;
            if (Dungeon.level.map[cell4] == 15) continue;
            if (Random.Float() > furrowedChance) {
                Level.set(cell4, 15);
            } else {
                Level.set(cell4, 30);
            }
            GameScene.updateMap(cell4);
            --grassToPlace;
        }
        if (this.totChrgUsed < this.chargeLimit(Dungeon.hero.lvl)) {
            this.chargesOverLimit = 0;
            this.totChrgUsed += chrgUsed;
            if (this.totChrgUsed > this.chargeLimit(Dungeon.hero.lvl)) {
                this.chargesOverLimit = this.totChrgUsed - this.chargeLimit(Dungeon.hero.lvl);
                this.totChrgUsed = this.chargeLimit(Dungeon.hero.lvl);
            }
        } else {
            this.chargesOverLimit += chrgUsed;
        }
    }

    private int chargeLimit(int heroLvl) {
        return this.chargeLimit(heroLvl, this.level());
    }

    private int chargeLimit(int heroLvl, int wndLvl) {
        if (wndLvl >= 10) {
            return Integer.MAX_VALUE;
        }
        return Math.round(20.0f + (float)(heroLvl * (2 + wndLvl)) * (1.0f + (float)(wndLvl / (50 - 5 * wndLvl))));
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        boolean grass = false;
        int terr = Dungeon.level.map[attacker.pos];
        if (terr == 2 || terr == 15 || terr == 30) {
            grass = true;
        }
        if ((terr = Dungeon.level.map[defender.pos]) == 2 || terr == 15 || terr == 30) {
            grass = true;
        }
        if (grass) {
            int level = Math.max(0, staff.buffedLvl());
            int healing = Math.round((float)damage * ((float)level + 2.0f) / ((float)level + 6.0f) / 2.0f);
            healing = Math.round((float)healing * WandOfRegrowth.procChanceMultiplier(attacker));
            Buff.affect(attacker, Sungrass.Health.class).boost(healing);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        int maxDist = 2 + 2 * this.chargesPerCast();
        this.cone = new ConeAOE(bolt, maxDist, 20 + 10 * this.chargesPerCast(), 5);
        Ballistica longestRay = null;
        for (Ballistica ray : this.cone.outerRays) {
            if (longestRay == null || ray.dist > longestRay.dist) {
                longestRay = ray;
            }
            ((MagicMissile)WandOfRegrowth.curUser.sprite.parent.recycle(MagicMissile.class)).reset(104, (Visual)WandOfRegrowth.curUser.sprite, (int)ray.path.get(ray.dist), null);
        }
        MagicMissile.boltFromChar(WandOfRegrowth.curUser.sprite.parent, 104, WandOfRegrowth.curUser.sprite, longestRay.path.get(longestRay.dist / 2), callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    protected int chargesPerCast() {
        if (this.cursed || this.charger != null && this.charger.target != null && this.charger.target.buff(WildMagic.WildMagicTracker.class) != null) {
            return 1;
        }
        return (int)GameMath.gate(1.0f, (int)Math.ceil((float)this.curCharges * 0.3f), 3.0f);
    }

    @Override
    public String statsDesc() {
        int chargeLeft;
        Object desc = Messages.get(this, "stats_desc", this.chargesPerCast());
        if (this.isIdentified() && (chargeLeft = this.chargeLimit(Dungeon.hero.lvl) - this.totChrgUsed) < 10000) {
            desc = (String)desc + " " + Messages.get(this, "degradation", Math.max(chargeLeft, 0));
        }
        return desc;
    }

    @Override
    public String upgradeStat1(int level) {
        return Messages.decimalFormat("#.##", 3.0f + (float)(2 + level) / 3.0f);
    }

    @Override
    public String upgradeStat2(int level) {
        if (level >= 10) {
            return "\u221e";
        }
        return Integer.toString(this.chargeLimit(Dungeon.hero.lvl, level));
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(ColorMath.random(17408, 0x88CC44));
        particle.am = 1.0f;
        particle.setLifespan(1.0f);
        particle.setSize(1.0f, 1.5f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11.0f);
        particle.x -= dst;
        particle.y += dst;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TOTAL, this.totChrgUsed);
        bundle.put(OVER, this.chargesOverLimit);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.totChrgUsed = bundle.getInt(TOTAL);
        this.chargesOverLimit = bundle.getInt(OVER);
    }

    public static class Lotus
    extends NPC {
        private int wandLvl;
        private static final String WAND_LVL = "wand_lvl";

        public Lotus() {
            this.alignment = Char.Alignment.NEUTRAL;
            this.properties.add(Char.Property.IMMOVABLE);
            this.properties.add(Char.Property.STATIC);
            this.spriteClass = LotusSprite.class;
            this.viewDistance = 1;
            this.wandLvl = 0;
            this.immunities.add(Doom.class);
        }

        private void setLevel(int lvl) {
            this.wandLvl = lvl;
            this.HP = this.HT = 25 + 3 * lvl;
        }

        public boolean inRange(int pos) {
            return Dungeon.level.trueDistance(this.pos, pos) <= (float)this.wandLvl;
        }

        public float seedPreservation() {
            return Math.min(1.0f, 0.4f + 0.04f * (float)this.wandLvl);
        }

        @Override
        public boolean canInteract(Char c) {
            return false;
        }

        @Override
        protected boolean act() {
            super.act();
            if (--this.HP <= 0) {
                this.destroy();
                this.sprite.die();
            }
            return true;
        }

        @Override
        public void damage(int dmg, Object src) {
        }

        @Override
        public boolean add(Buff buff) {
            return false;
        }

        @Override
        public void destroy() {
            super.destroy();
            Dungeon.observe();
            GameScene.updateFog(this.pos, this.viewDistance + 1);
        }

        @Override
        public boolean isInvulnerable(Class effect) {
            return true;
        }

        @Override
        public String description() {
            Object desc = Messages.get(this, "desc", new Object[0]);
            if (Actor.chars().contains(this)) {
                int preservation = Math.round(this.seedPreservation() * 100.0f);
                desc = (String)desc + "\n\n" + Messages.get(this, "wand_info", this.wandLvl, preservation, preservation);
            }
            return desc;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WAND_LVL, this.wandLvl);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.wandLvl = bundle.getInt(WAND_LVL);
        }
    }

    public static class Seedpod
    extends Plant {
        public Seedpod() {
            this.image = 14;
        }

        @Override
        public void activate(Char ch) {
            int nSeeds = Random.NormalIntRange(2, 4);
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.passable[this.pos + i] || this.pos + i == Dungeon.level.entrance() || this.pos + i == Dungeon.level.exit()) continue;
                candidates.add(this.pos + i);
            }
            for (int i = 0; i < nSeeds && !candidates.isEmpty(); ++i) {
                Integer c = (Integer)Random.element(candidates);
                Dungeon.level.drop((Item)Generator.randomUsingDefaults((Generator.Category)Generator.Category.SEED), (int)c.intValue()).sprite.drop(this.pos);
                candidates.remove(c);
            }
        }

        public static class Seed
        extends Plant.Seed {
            public Seed() {
                this.plantClass = Seedpod.class;
            }
        }
    }

    public static class Dewcatcher
    extends Plant {
        public Dewcatcher() {
            this.image = 13;
        }

        @Override
        public void activate(Char ch) {
            int nDrops = Random.NormalIntRange(3, 6);
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.passable[this.pos + i] || this.pos + i == Dungeon.level.entrance() || this.pos + i == Dungeon.level.exit()) continue;
                candidates.add(this.pos + i);
            }
            for (int i = 0; i < nDrops && !candidates.isEmpty(); ++i) {
                Integer c = (Integer)Random.element(candidates);
                if (Dungeon.level.heaps.get(c) == null) {
                    Dungeon.level.drop((Item)new Dewdrop(), (int)c.intValue()).sprite.drop(this.pos);
                } else {
                    Dungeon.level.drop((Item)new Dewdrop(), (int)c.intValue()).sprite.drop(c);
                }
                candidates.remove(c);
            }
        }

        public static class Seed
        extends Plant.Seed {
            public Seed() {
                this.plantClass = Dewcatcher.class;
            }
        }
    }
}

