/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GravityChaosTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperNovaTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.io.IOException;
import java.util.ArrayList;

public class CursedWand {
    private static float[] EFFECT_CAT_CHANCES = new float[]{60.0f, 30.0f, 9.0f, 1.0f};
    private static ArrayList<CursedEffect> COMMON_EFFECTS = new ArrayList();
    private static ArrayList<CursedEffect> UNCOMMON_EFFECTS;
    private static ArrayList<CursedEffect> RARE_EFFECTS;
    private static ArrayList<CursedEffect> VERY_RARE_EFFECTS;

    public static void cursedZap(final Item origin, final Char user, final Ballistica bolt, final Callback afterZap) {
        final boolean positiveOnly = user == Dungeon.hero && Random.Float() < WondrousResin.positiveCurseEffectChance();
        final CursedEffect effect = CursedWand.randomValidEffect(origin, user, bolt, positiveOnly);
        effect.FX(origin, user, bolt, new Callback(){

            @Override
            public void call() {
                effect.effect(origin, user, bolt, positiveOnly);
                afterZap.call();
            }
        });
    }

    public static void tryForWandProc(Char target, Item origin) {
        if (target != null && target != Dungeon.hero && origin instanceof Wand) {
            Wand.wandProc(target, origin.buffedLvl(), 1);
        }
    }

    public static CursedEffect randomEffect() {
        switch (Random.chances(EFFECT_CAT_CHANCES)) {
            default: {
                return CursedWand.randomCommonEffect();
            }
            case 1: {
                return CursedWand.randomUncommonEffect();
            }
            case 2: {
                return CursedWand.randomRareEffect();
            }
            case 3: 
        }
        return CursedWand.randomVeryRareEffect();
    }

    public static CursedEffect randomValidEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        switch (Random.chances(EFFECT_CAT_CHANCES)) {
            default: {
                return CursedWand.randomValidCommonEffect(origin, user, bolt, positiveOnly);
            }
            case 1: {
                return CursedWand.randomValidUncommonEffect(origin, user, bolt, positiveOnly);
            }
            case 2: {
                return CursedWand.randomValidRareEffect(origin, user, bolt, positiveOnly);
            }
            case 3: 
        }
        return CursedWand.randomValidVeryRareEffect(origin, user, bolt, positiveOnly);
    }

    public static CursedEffect randomCommonEffect() {
        return Random.element(COMMON_EFFECTS);
    }

    public static CursedEffect randomValidCommonEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        CursedEffect effect;
        while (!(effect = Random.element(COMMON_EFFECTS)).valid(origin, user, bolt, positiveOnly)) {
        }
        return effect;
    }

    public static CursedEffect randomUncommonEffect() {
        return Random.element(UNCOMMON_EFFECTS);
    }

    public static CursedEffect randomValidUncommonEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        CursedEffect effect;
        while (!(effect = Random.element(UNCOMMON_EFFECTS)).valid(origin, user, bolt, positiveOnly)) {
        }
        return effect;
    }

    public static CursedEffect randomRareEffect() {
        return Random.element(RARE_EFFECTS);
    }

    public static CursedEffect randomValidRareEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        CursedEffect effect;
        while (!(effect = Random.element(RARE_EFFECTS)).valid(origin, user, bolt, positiveOnly)) {
        }
        return effect;
    }

    public static CursedEffect randomVeryRareEffect() {
        return Random.element(VERY_RARE_EFFECTS);
    }

    public static CursedEffect randomValidVeryRareEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
        CursedEffect effect;
        while (!(effect = Random.element(VERY_RARE_EFFECTS)).valid(origin, user, bolt, positiveOnly)) {
        }
        return effect;
    }

    static {
        COMMON_EFFECTS.add(new BurnAndFreeze());
        COMMON_EFFECTS.add(new SpawnRegrowth());
        COMMON_EFFECTS.add(new RandomTeleport());
        COMMON_EFFECTS.add(new RandomGas());
        COMMON_EFFECTS.add(new RandomAreaEffect());
        COMMON_EFFECTS.add(new Bubbles());
        COMMON_EFFECTS.add(new RandomWand());
        COMMON_EFFECTS.add(new SelfOoze());
        UNCOMMON_EFFECTS = new ArrayList();
        UNCOMMON_EFFECTS.add(new RandomPlant());
        UNCOMMON_EFFECTS.add(new HealthTransfer());
        UNCOMMON_EFFECTS.add(new Explosion());
        UNCOMMON_EFFECTS.add(new LightningBolt());
        UNCOMMON_EFFECTS.add(new Geyser());
        UNCOMMON_EFFECTS.add(new SummonSheep());
        UNCOMMON_EFFECTS.add(new Levitate());
        UNCOMMON_EFFECTS.add(new Alarm());
        RARE_EFFECTS = new ArrayList();
        RARE_EFFECTS.add(new SheepPolymorph());
        RARE_EFFECTS.add(new CurseEquipment());
        RARE_EFFECTS.add(new InterFloorTeleport());
        RARE_EFFECTS.add(new SummonMonsters());
        RARE_EFFECTS.add(new FireBall());
        RARE_EFFECTS.add(new ConeOfColors());
        RARE_EFFECTS.add(new MassInvuln());
        RARE_EFFECTS.add(new Petrify());
        VERY_RARE_EFFECTS = new ArrayList();
        VERY_RARE_EFFECTS.add(new ForestFire());
        VERY_RARE_EFFECTS.add(new SpawnGoldenMimic());
        VERY_RARE_EFFECTS.add(new AbortRetryFail());
        VERY_RARE_EFFECTS.add(new RandomTransmogrify());
        VERY_RARE_EFFECTS.add(new HeroShapeShift());
        VERY_RARE_EFFECTS.add(new SuperNova());
        VERY_RARE_EFFECTS.add(new SinkHole());
        VERY_RARE_EFFECTS.add(new GravityChaos());
    }

    public static abstract class CursedEffect {
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return true;
        }

        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            MagicMissile.boltFromChar(user.sprite.parent, 8, user.sprite, bolt.collisionPos, callback);
            Sample.INSTANCE.play("sounds/zap.mp3");
        }

        public abstract boolean effect(Item var1, Char var2, Ballistica var3, boolean var4);
    }

    public static class BurnAndFreeze
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char target = Actor.findChar(bolt.collisionPos);
            if (Random.Int(2) == 0) {
                if (target != null) {
                    Buff.affect(target, Burning.class).reignite(target);
                }
                if (!positiveOnly) {
                    Buff.affect(user, Frost.class, 10.0f);
                }
            } else {
                if (!positiveOnly) {
                    Buff.affect(user, Burning.class).reignite(user);
                }
                if (target != null) {
                    Buff.affect(target, Frost.class, 10.0f);
                }
            }
            CursedWand.tryForWandProc(target, origin);
            return true;
        }
    }

    public static class SpawnRegrowth
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean postiveOnly) {
            if (Actor.findChar(bolt.collisionPos) == null) {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
            GameScene.add(Blob.seed(bolt.collisionPos, 30, Regrowth.class));
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            return true;
        }
    }

    public static class RandomTeleport
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char target = Actor.findChar(bolt.collisionPos);
            return !positiveOnly || target != null && !Char.hasProp(target, Char.Property.IMMOVABLE);
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char target = Actor.findChar(bolt.collisionPos);
            if (target != null && !Char.hasProp(target, Char.Property.IMMOVABLE) && (positiveOnly || Random.Int(2) == 0)) {
                ScrollOfTeleportation.teleportChar(target);
                CursedWand.tryForWandProc(target, origin);
                return true;
            }
            if (positiveOnly || user == null || Char.hasProp(user, Char.Property.IMMOVABLE)) {
                return false;
            }
            ScrollOfTeleportation.teleportChar(user);
            return true;
        }
    }

    public static class RandomGas
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Sample.INSTANCE.play("sounds/gas.mp3");
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            if (Actor.findChar(bolt.collisionPos) == null) {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
            switch (Random.Int(3)) {
                default: {
                    GameScene.add(Blob.seed(bolt.collisionPos, 800, ConfusionGas.class));
                    return true;
                }
                case 1: {
                    GameScene.add(Blob.seed(bolt.collisionPos, 500, ToxicGas.class));
                    return true;
                }
                case 2: 
            }
            GameScene.add(Blob.seed(bolt.collisionPos, 200, ParalyticGas.class));
            return true;
        }
    }

    public static class RandomAreaEffect
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            if (Actor.findChar(bolt.collisionPos) == null) {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
            switch (Random.Int(3)) {
                default: {
                    new BurningTrap().set(bolt.collisionPos).activate();
                    return true;
                }
                case 1: {
                    new ChillingTrap().set(bolt.collisionPos).activate();
                    return true;
                }
                case 2: 
            }
            new ShockingTrap().set(bolt.collisionPos).activate();
            return true;
        }
    }

    public static class Bubbles
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            MagicMissile.boltFromChar(user.sprite.parent, 1012, user.sprite, bolt.collisionPos, callback);
            Sample.INSTANCE.play("sounds/zap.mp3");
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (Actor.findChar(bolt.collisionPos) == null) {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Dungeon.level.solid[bolt.collisionPos + i]) continue;
                CellEmitter.get(bolt.collisionPos + i).start(Speck.factory(12), 0.25f, 40);
            }
            return true;
        }
    }

    public static class RandomWand
    extends CursedEffect {
        private Wand wand;

        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return super.valid(origin, user, bolt, positiveOnly) && origin instanceof Wand;
        }

        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            if (this.wand == null) {
                this.wand = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND);
            }
            this.wand.fx(bolt, callback);
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (this.wand == null) {
                this.wand = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND);
            }
            if (origin instanceof Wand) {
                this.wand.upgrade(origin.level());
            } else {
                this.wand.upgrade(Dungeon.scalingDepth() / 5);
            }
            this.wand.levelKnown = false;
            this.wand.onZap(bolt);
            this.wand = null;
            return true;
        }
    }

    public static class SelfOoze
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            PathFinder.buildDistanceMap(user.pos, BArray.not(Dungeon.level.solid, null), 2);
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
                Splash.at(i, 0, 5);
                Char ch = Actor.findChar(i);
                if (ch == null || positiveOnly && ch.alignment == Char.Alignment.ALLY) continue;
                Buff.affect(ch, Ooze.class).set(20.0f);
            }
            Sample.INSTANCE.play("sounds/shatter.mp3");
            return true;
        }
    }

    public static class RandomPlant
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            int pos = bolt.collisionPos;
            return Dungeon.level.map[pos] != 28 && !Dungeon.level.pit[pos] && Dungeon.level.traps.get(pos) == null && !Dungeon.isChallenged(8);
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (this.valid(origin, user, bolt, positiveOnly)) {
                CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
                Dungeon.level.plant((Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED), bolt.collisionPos);
                return true;
            }
            return false;
        }
    }

    public static class HealthTransfer
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return Actor.findChar(bolt.collisionPos) != null;
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char target = Actor.findChar(bolt.collisionPos);
            if (target != null) {
                Char toDamage;
                Char toHeal;
                int damage = Dungeon.scalingDepth() * 2;
                if (positiveOnly || Random.Int(2) == 0) {
                    toHeal = user;
                    toDamage = target;
                } else {
                    toHeal = target;
                    toDamage = user;
                }
                toHeal.HP = Math.min(toHeal.HT, toHeal.HP + damage / 2);
                toHeal.sprite.emitter().burst(Speck.factory(0), 3);
                toHeal.sprite.showStatusWithIcon(65280, Integer.toString(damage / 2), FloatingText.HEALING, new Object[0]);
                toDamage.damage(damage, new CursedWand());
                toDamage.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                if (toDamage == Dungeon.hero) {
                    Sample.INSTANCE.play("sounds/cursed.mp3");
                    if (!toDamage.isAlive()) {
                        if (user == Dungeon.hero && origin != null) {
                            Badges.validateDeathFromFriendlyMagic();
                            Dungeon.fail(origin);
                            GLog.n(Messages.get(CursedWand.class, "ondeath", origin.name()), new Object[0]);
                        } else {
                            Badges.validateDeathFromEnemyMagic();
                            Dungeon.fail(toHeal);
                        }
                    }
                } else {
                    Sample.INSTANCE.play("sounds/burning.mp3");
                }
                CursedWand.tryForWandProc(target, origin);
                return true;
            }
            return false;
        }
    }

    public static class Explosion
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            new Bomb.ConjuredBomb().explode(bolt.collisionPos);
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            return true;
        }
    }

    public static class LightningBolt
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            Char ch = Actor.findChar(bolt.collisionPos);
            if (ch != null) {
                user.sprite.parent.addToFront(new Lightning(user.sprite.center(), ch.sprite.center(), null));
            } else {
                user.sprite.parent.addToFront(new Lightning(user.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos), null));
            }
            Sample.INSTANCE.play("sounds/lightning.mp3");
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            ArrayList<Char> affected = new ArrayList<Char>();
            user.sprite.parent.add(new Lightning(user.pos - 1, user.pos + 1, null));
            user.sprite.parent.add(new Lightning(user.pos - Dungeon.level.width(), user.pos + Dungeon.level.width(), null));
            user.sprite.parent.add(new Lightning(user.pos - 1 - Dungeon.level.width(), user.pos + 1 + Dungeon.level.width(), null));
            user.sprite.parent.add(new Lightning(user.pos - 1 + Dungeon.level.width(), user.pos + 1 - Dungeon.level.width(), null));
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Actor.findChar(user.pos + i) == null) continue;
                affected.add(Actor.findChar(user.pos + i));
            }
            int pos = bolt.collisionPos;
            user.sprite.parent.add(new Lightning(pos - 1, user.pos + 1, null));
            user.sprite.parent.add(new Lightning(pos - Dungeon.level.width(), pos + Dungeon.level.width(), null));
            user.sprite.parent.add(new Lightning(pos - 1 - Dungeon.level.width(), pos + 1 + Dungeon.level.width(), null));
            user.sprite.parent.add(new Lightning(pos - 1 + Dungeon.level.width(), pos + 1 - Dungeon.level.width(), null));
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Actor.findChar(pos + i) == null || affected.contains(Actor.findChar(pos + i))) continue;
                affected.add(Actor.findChar(pos + i));
            }
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            Object object = affected.iterator();
            while (object.hasNext()) {
                Char ch = (Char)object.next();
                if (ch instanceof Hero) {
                    Buff.prolong(ch, Recharging.class, 10.0f);
                    ScrollOfRecharging.charge(ch);
                    SpellSprite.show(ch, 2);
                }
                if (ch.alignment == Char.Alignment.ALLY && positiveOnly) continue;
                ch.damage(Random.NormalIntRange(5 + Dungeon.scalingDepth() / 4, 10 + Dungeon.scalingDepth() / 4), new Electricity());
                if (ch.isAlive()) {
                    Buff.affect(ch, Paralysis.class, 5.0f);
                    continue;
                }
                if (ch != Dungeon.hero) continue;
                if (user == Dungeon.hero && origin != null) {
                    Badges.validateDeathFromFriendlyMagic();
                    Dungeon.fail(origin);
                    GLog.n(Messages.get(CursedWand.class, "ondeath", origin.name()), new Object[0]);
                    continue;
                }
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(user);
            }
            return true;
        }
    }

    public static class Geyser
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            GeyserTrap geyser = new GeyserTrap();
            geyser.pos = bolt.collisionPos;
            geyser.source = origin == null ? user : origin;
            geyser.activate();
            return true;
        }
    }

    public static class SummonSheep
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            new FlockTrap().set(bolt.collisionPos).activate();
            return true;
        }
    }

    public static class Levitate
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return positiveOnly || Actor.findChar(bolt.collisionPos) != null;
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char ch = Actor.findChar(bolt.collisionPos);
            if (!(positiveOnly && !(ch instanceof Piranha) || ch == null || ch.flying || Char.hasProp(ch, Char.Property.IMMOVABLE))) {
                Buff.affect(ch, Levitation.class, 20.0f);
            } else {
                Buff.affect(user, Levitation.class, 20.0f);
            }
            return true;
        }
    }

    public static class Alarm
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            for (Mob mob : Dungeon.level.mobs) {
                mob.beckon(user.pos);
            }
            user.sprite.centerEmitter().start(Speck.factory(5), 0.3f, 3);
            if (positiveOnly) {
                Buff.affect(user, ScrollOfChallenge.ChallengeArena.class).setup(user.pos);
                Sample.INSTANCE.play("sounds/challenge.mp3");
            } else {
                Sample.INSTANCE.play("sounds/alert.mp3");
            }
            return true;
        }
    }

    public static class SheepPolymorph
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char ch = Actor.findChar(bolt.collisionPos);
            return ch != null && !(ch instanceof Hero) && !ch.properties().contains((Object)Char.Property.BOSS) && !ch.properties().contains((Object)Char.Property.MINIBOSS) && (!(ch instanceof NPC) || ch.alignment != Char.Alignment.NEUTRAL);
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (this.valid(origin, user, bolt, positiveOnly)) {
                Char ch = Actor.findChar(bolt.collisionPos);
                Sheep sheep = new Sheep();
                sheep.initialize(10.0f);
                sheep.pos = ch.pos;
                ch.destroy();
                ch.sprite.killAndErase();
                Dungeon.level.mobs.remove(ch);
                TargetHealthIndicator.instance.target(null);
                GameScene.add(sheep);
                CellEmitter.get(sheep.pos).burst(Speck.factory(7), 4);
                Sample.INSTANCE.play("sounds/puff.mp3");
                Sample.INSTANCE.play("sounds/sheep.mp3");
                Dungeon.level.occupyCell(sheep);
                return true;
            }
            return false;
        }
    }

    public static class CurseEquipment
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (positiveOnly || !(user instanceof Hero)) {
                Char ch = Actor.findChar(bolt.collisionPos);
                if (ch != null) {
                    Buff.affect(ch, Hex.class, 30.0f);
                }
                return true;
            }
            CursingTrap.curse((Hero)user);
            return true;
        }
    }

    public static class InterFloorTeleport
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (!positiveOnly && Dungeon.depth > 1 && Dungeon.interfloorTeleportAllowed() && user == Dungeon.hero) {
                int start;
                float[] depths = new float[Dungeon.depth - 1];
                for (int i = start = Math.max(1, Dungeon.depth - 10); i < Dungeon.depth; ++i) {
                    depths[i - 1] = i - start + 1;
                }
                int depth = 1 + Random.chances(depths);
                Level.beforeTransition();
                InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                InterlevelScene.returnDepth = depth;
                InterlevelScene.returnBranch = 0;
                InterlevelScene.returnPos = -1;
                Game.switchScene(InterlevelScene.class);
            } else {
                ScrollOfTeleportation.teleportChar(user);
            }
            return true;
        }
    }

    public static class SummonMonsters
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (positiveOnly && user == Dungeon.hero) {
                ScrollOfMirrorImage.spawnImages(Dungeon.hero, bolt.collisionPos, 2);
            } else {
                new SummoningTrap().set(bolt.collisionPos).activate();
            }
            return true;
        }
    }

    public static class FireBall
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Point c = Dungeon.level.cellToPoint(bolt.collisionPos);
            boolean[] fieldOfView = new boolean[Dungeon.level.length()];
            ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), fieldOfView, Dungeon.level.solid, 3);
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (!fieldOfView[i] || Dungeon.level.solid[i] || positiveOnly && Actor.findChar(i) != null && Actor.findChar((int)i).alignment == Char.Alignment.ALLY) continue;
                CellEmitter.get(i).burst(FlameParticle.FACTORY, 10);
                if (Actor.findChar(i) != null) {
                    Char ch = Actor.findChar(i);
                    Burning burning = Buff.affect(ch, Burning.class);
                    burning.reignite(ch);
                    int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
                    ch.damage(dmg, burning);
                }
                if (!Dungeon.level.flamable[i]) continue;
                GameScene.add(Blob.seed(i, 4, Fire.class));
            }
            WandOfBlastWave.BlastWave.blast(bolt.collisionPos, 6.0f);
            Sample.INSTANCE.play("sounds/blast.mp3");
            Sample.INSTANCE.play("sounds/burning.mp3");
            return false;
        }
    }

    public static class ConeOfColors
    extends CursedEffect {
        private ConeAOE cone = null;

        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            bolt = new Ballistica(bolt.sourcePos, bolt.collisionPos, 4);
            this.cone = new ConeAOE(bolt, 8.0f, 90.0f, 4);
            Ballistica longestRay = null;
            for (Ballistica ray : this.cone.outerRays) {
                if (longestRay == null || ray.dist > longestRay.dist) {
                    longestRay = ray;
                }
                ((MagicMissile)user.sprite.parent.recycle(MagicMissile.class)).reset(108, (Visual)user.sprite, (int)ray.path.get(ray.dist), null);
            }
            MagicMissile.boltFromChar(user.sprite.parent, 108, user.sprite, longestRay.path.get(longestRay.dist / 2), callback);
            Sample.INSTANCE.play("sounds/zap.mp3");
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            ArrayList<Char> affectedChars = new ArrayList<Char>();
            if (this.cone == null && Actor.findChar(bolt.collisionPos) != null) {
                affectedChars.add(Actor.findChar(bolt.collisionPos));
            } else {
                for (Integer cell : this.cone.cells) {
                    if (cell == user.pos || Actor.findChar(cell) == null) continue;
                    affectedChars.add(Actor.findChar(cell));
                }
            }
            CursedWand.tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
            for (Char ch : affectedChars) {
                if (positiveOnly && ch.alignment == Char.Alignment.ALLY) continue;
                int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
                switch (Random.Int(5)) {
                    default: {
                        Burning burning = Buff.affect(ch, Burning.class);
                        burning.reignite(ch);
                        ch.damage(dmg, burning);
                        ch.sprite.emitter().burst(FlameParticle.FACTORY, 20);
                        break;
                    }
                    case 1: {
                        ch.damage(dmg, new Frost());
                        if (ch.isAlive()) {
                            Buff.affect(ch, Frost.class, 10.0f);
                        }
                        Splash.at(ch.sprite.center(), -5056769, 20);
                        break;
                    }
                    case 2: {
                        Poison poison = Buff.affect(ch, Poison.class);
                        poison.set(3 + Dungeon.scalingDepth() / 2);
                        ch.damage(dmg, poison);
                        ch.sprite.emitter().burst(PoisonParticle.SPLASH, 20);
                        break;
                    }
                    case 3: {
                        Ooze ooze = Buff.affect(ch, Ooze.class);
                        ooze.set(20.0f);
                        ch.damage(dmg, ooze);
                        Splash.at(ch.sprite.center(), 0, 20);
                        break;
                    }
                    case 4: {
                        ch.damage(dmg, new Electricity());
                        if (ch.isAlive()) {
                            Buff.affect(ch, Paralysis.class, 10.0f);
                        }
                        ch.sprite.emitter().burst(SparkParticle.FACTORY, 20);
                    }
                }
                if (ch != Dungeon.hero || ch.isAlive()) continue;
                if (user == Dungeon.hero && origin != null) {
                    Badges.validateDeathFromFriendlyMagic();
                    Dungeon.fail(origin);
                    GLog.n(Messages.get(CursedWand.class, "ondeath", origin.name()), new Object[0]);
                    continue;
                }
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(user);
            }
            return true;
        }
    }

    public static class MassInvuln
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            for (Char ch : Actor.chars()) {
                Buff.affect(ch, Invulnerability.class, 10.0f);
                Buff.affect(ch, Bless.class, 30.0f);
            }
            new Flare(5, 48.0f).color(0xFFFF00, true).show(user.sprite, 3.0f);
            GameScene.flash(-2130706624);
            Sample.INSTANCE.play("sounds/teleport.mp3");
            GLog.p(Messages.get(CursedWand.class, "mass_invuln", new Object[0]), new Object[0]);
            return true;
        }
    }

    public static class Petrify
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return user == Dungeon.hero;
        }

        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Buff.affect(user, TimeStasis.class, 100.0f);
            Sample.INSTANCE.play("sounds/teleport.mp3");
            user.sprite.emitter().burst(Speck.factory(13), 10);
            GLog.w(Messages.get(CursedWand.class, "petrify", new Object[0]), new Object[0]);
            return true;
        }
    }

    public static class ForestFire
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                GameScene.add(Blob.seed(i, 15, Regrowth.class));
            }
            new Flare(8, 32.0f).color(0xFFFF66, true).show(user.sprite, 2.0f);
            Sample.INSTANCE.play("sounds/teleport.mp3");
            GLog.p(Messages.get(CursedWand.class, "grass", new Object[0]), new Object[0]);
            if (!positiveOnly) {
                GLog.w(Messages.get(CursedWand.class, "fire", new Object[0]), new Object[0]);
                do {
                    GameScene.add(Blob.seed(Dungeon.level.randomDestination(null), 10, Fire.class));
                } while (Random.Int(5) != 0);
            }
            return true;
        }
    }

    public static class SpawnGoldenMimic
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Char ch = Actor.findChar(bolt.collisionPos);
            int spawnCell = bolt.collisionPos;
            if (ch != null) {
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (int n : PathFinder.NEIGHBOURS8) {
                    int cell = bolt.collisionPos + n;
                    if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
                    candidates.add(cell);
                }
                if (!candidates.isEmpty()) {
                    spawnCell = (Integer)Random.element(candidates);
                } else {
                    return false;
                }
            }
            Mimic mimic = Mimic.spawnAt(spawnCell, GoldenMimic.class, false, new Item[0]);
            mimic.stopHiding();
            mimic.alignment = Char.Alignment.ENEMY;
            Sample.INSTANCE.play("sounds/mimic.mp3", 1.0f, 0.85f);
            CellEmitter.get(mimic.pos).burst(Speck.factory(1), 10);
            mimic.items.clear();
            GameScene.add(mimic);
            if (positiveOnly) {
                Buff.affect(mimic, ScrollOfSirensSong.Enthralled.class);
            } else {
                Item reward;
                while ((reward = Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR, Generator.Category.RING, Generator.Category.WAND))).level() < 1) {
                }
                mimic.items.add(reward);
            }
            Dungeon.level.occupyCell(mimic);
            return true;
        }
    }

    public static class AbortRetryFail
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            try {
                Dungeon.saveAll();
                if (Messages.lang() != Languages.ENGLISH) {
                    return false;
                }
                ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndOptions(Icons.get(Icons.WARNING), "CURSED WAND ERROR", "this application will now self-destruct", new String[]{"abort", "retry", "fail"}){

                            @Override
                            protected void onSelect(int index) {
                                Game.instance.finish();
                            }

                            @Override
                            public void onBackPressed() {
                            }
                        });
                    }
                });
                return false;
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
                return false;
            }
        }
    }

    public static class RandomTransmogrify
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (positiveOnly) {
                return true;
            }
            return origin != null && user == Dungeon.hero && Dungeon.hero.belongings.contains(origin);
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Item result;
            if (positiveOnly) {
                GameScene.show(new ScrollOfMetamorphosis.WndMetamorphChoose());
                return true;
            }
            if (origin == null || user != Dungeon.hero || !Dungeon.hero.belongings.contains(origin)) {
                return false;
            }
            if (origin.isEquipped(Dungeon.hero) && origin instanceof EquipableItem) {
                origin.cursed = false;
                ((EquipableItem)origin).doUnequip(Dungeon.hero, false);
            } else {
                origin.detach(Dungeon.hero.belongings.backpack);
            }
            do {
                result = Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR, Generator.Category.RING, Generator.Category.ARTIFACT));
            } while (result.cursed);
            if (result.isUpgradable()) {
                result.upgrade();
            }
            result.cursedKnown = true;
            result.cursed = true;
            if (origin instanceof Wand) {
                GLog.w(Messages.get(CursedWand.class, "transmogrify_wand", new Object[0]), new Object[0]);
            } else {
                GLog.w(Messages.get(CursedWand.class, "transmogrify_other", new Object[0]), new Object[0]);
            }
            Dungeon.level.drop((Item)result, (int)user.pos).sprite.drop();
            return true;
        }
    }

    public static class HeroShapeShift
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return user instanceof Hero || Actor.findChar(bolt.collisionPos) instanceof Hero;
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            if (user instanceof Hero) {
                Buff.affect(user, HeroDisguise.class, HeroDisguise.DURATION);
                GLog.w(Messages.get(CursedWand.class, "disguise", new Object[0]), new Object[0]);
                return true;
            }
            if (Actor.findChar(bolt.collisionPos) instanceof Hero) {
                Buff.affect(Actor.findChar(bolt.collisionPos), HeroDisguise.class, HeroDisguise.DURATION);
                GLog.w(Messages.get(CursedWand.class, "disguise", new Object[0]), new Object[0]);
                return true;
            }
            return false;
        }
    }

    public static class SuperNova
    extends CursedEffect {
        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            SuperNovaTracker nova = Buff.append(Dungeon.hero, SuperNovaTracker.class);
            nova.pos = bolt.collisionPos;
            boolean bl = nova.harmsAllies = !positiveOnly;
            if (positiveOnly) {
                GLog.p(Messages.get(CursedWand.class, "supernova_positive", new Object[0]), new Object[0]);
            } else {
                GLog.w(Messages.get(CursedWand.class, "supernova", new Object[0]), new Object[0]);
            }
            return true;
        }
    }

    public static class SinkHole
    extends CursedEffect {
        @Override
        public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            return !Dungeon.bossLevel() && Dungeon.depth <= 25 && Dungeon.branch == 0;
        }

        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            boolean[] passable = BArray.not(Dungeon.level.solid, null);
            BArray.or(passable, Dungeon.level.passable, passable);
            PathFinder.buildDistanceMap(user.pos, passable, 5);
            ArrayList<Integer> positions = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] >= Integer.MAX_VALUE || Dungeon.level.solid[i] && !Dungeon.level.passable[i]) continue;
                CellEmitter.floor(i).burst(PitfallParticle.FACTORY4, 8);
                positions.add(i);
            }
            PitfallTrap.DelayedPit p = Buff.append(Dungeon.hero, PitfallTrap.DelayedPit.class, 1.0f);
            p.depth = Dungeon.depth;
            p.branch = Dungeon.branch;
            p.setPositions(positions);
            if (positiveOnly) {
                p.ignoreAllies = true;
                GLog.p(Messages.get(CursedWand.class, "sinkhole_positive", new Object[0]), new Object[0]);
            } else {
                GLog.w(Messages.get(CursedWand.class, "sinkhole", new Object[0]), new Object[0]);
            }
            return true;
        }
    }

    public static class GravityChaos
    extends CursedEffect {
        @Override
        public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
            callback.call();
        }

        @Override
        public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
            Buff.append((Char)user, GravityChaosTracker.class).positiveOnly = positiveOnly;
            Sample.INSTANCE.play("sounds/teleport.mp3");
            if (positiveOnly) {
                GLog.p(Messages.get(CursedWand.class, "gravity_positive", new Object[0]), new Object[0]);
            } else {
                GLog.w(Messages.get(CursedWand.class, "gravity", new Object[0]), new Object[0]);
            }
            return true;
        }
    }
}

