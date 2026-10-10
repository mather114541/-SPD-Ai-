/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.HashSet;

public class Tengu
extends Mob {
    boolean loading;
    private static final String LAST_ABILITY = "last_ability";
    private static final String ABILITIES_USED = "abilities_used";
    private static final String ARENA_JUMPS = "arena_jumps";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";
    private static Char throwingChar;
    private int lastAbility;
    private int abilitiesUsed;
    private int arenaJumps;
    private int abilityCooldown;
    private static final int BOMB_ABILITY = 0;
    private static final int FIRE_ABILITY = 1;
    private static final int SHOCKER_ABILITY = 2;

    public Tengu() {
        this.spriteClass = TenguSprite.class;
        this.HT = Dungeon.isChallenged(256) ? 250 : 200;
        this.HP = this.HT;
        this.EXP = 20;
        this.defenseSkill = 15;
        this.HUNTING = new Hunting();
        this.properties.add(Char.Property.BOSS);
        this.viewDistance = 12;
        this.loading = false;
        this.immunities.add(Roots.class);
        this.immunities.add(Blindness.class);
        this.immunities.add(Dread.class);
        this.immunities.add(Terror.class);
        this.lastAbility = -1;
        this.abilitiesUsed = 0;
        this.arenaJumps = 0;
        this.abilityCooldown = 2;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 12);
    }

    @Override
    public int attackSkill(Char target) {
        if (Dungeon.level.adjacent(this.pos, target.pos)) {
            return 10;
        }
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }

    @Override
    public boolean add(Buff buff) {
        if (Actor.chars().contains(this) || buff instanceof Doom || this.loading) {
            return super.add(buff);
        }
        return false;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!Dungeon.level.mobs.contains(this)) {
            return;
        }
        PrisonBossLevel.State state = ((PrisonBossLevel)Dungeon.level).state();
        int hpBracket = this.HT / 8;
        int curbracket = this.HP / hpBracket;
        int beforeHitHP = this.HP;
        super.damage(dmg, src);
        if (this.HP <= (curbracket - 1) * hpBracket) {
            this.HP = (curbracket - 1) * hpBracket + 1;
        }
        int newBracket = this.HP / hpBracket;
        dmg = beforeHitHP - this.HP;
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)(2 * dmg) / 3.0f);
            } else {
                lock.addTime(dmg);
            }
        }
        if (this.HP == 0 && state == PrisonBossLevel.State.FIGHT_ARENA) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    ((PrisonBossLevel)Dungeon.level).progress();
                    return true;
                }
            });
            return;
        }
        if (state == PrisonBossLevel.State.FIGHT_START && this.HP <= this.HT / 2) {
            this.HP = this.HT / 2;
            this.yell(Messages.get(this, "interesting", new Object[0]));
            ((PrisonBossLevel)Dungeon.level).progress();
            BossHealthBar.bleed(true);
        } else if (newBracket != curbracket) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    Tengu.this.jump();
                    return true;
                }
            });
            return;
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || Dungeon.level.mobs.contains(this);
    }

    @Override
    public void die(Object cause) {
        if (Dungeon.hero.subClass == HeroSubClass.NONE) {
            Dungeon.level.drop((Item)new TengusMask(), (int)this.pos).sprite.drop();
        }
        GameScene.bossSlain();
        super.die(cause);
        Badges.validateBossSlain();
        if (Statistics.qualifiedForBossChallengeBadge) {
            Badges.validateBossChallengeCompleted();
        }
        Statistics.bossScores[1] = Statistics.bossScores[1] + 2000;
        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }
        this.yell(Messages.get(this, "defeated", new Object[0]));
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica((int)this.pos, (int)enemy.pos, (int)7).collisionPos == enemy.pos;
    }

    private void jump() {
        if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView(this, this.fieldOfView);
        }
        if (this.enemy == null) {
            this.enemy = this.chooseEnemy();
        }
        if (this.enemy == null) {
            this.enemy = Dungeon.hero;
        }
        if (Dungeon.level instanceof PrisonBossLevel) {
            PrisonBossLevel level = (PrisonBossLevel)Dungeon.level;
            if (level.state() == PrisonBossLevel.State.FIGHT_START) {
                int newPos;
                level.cleanTenguCell();
                int tries = 100;
                do {
                    newPos = ((PrisonBossLevel)Dungeon.level).randomTenguCellPos();
                } while (--tries > 0 && (level.trueDistance(newPos, this.enemy.pos) <= 3.5f || level.trueDistance(newPos, Dungeon.hero.pos) <= 3.5f || Actor.findChar(newPos) != null));
                if (tries <= 0) {
                    newPos = this.pos;
                }
                if (level.heroFOV[this.pos]) {
                    CellEmitter.get(this.pos).burst(Speck.factory(7), 6);
                }
                this.sprite.move(this.pos, newPos);
                this.move(newPos);
                if (level.heroFOV[newPos]) {
                    CellEmitter.get(newPos).burst(Speck.factory(7), 6);
                }
                Sample.INSTANCE.play("sounds/puff.mp3");
                float fill = 0.9f - 0.5f * (((float)this.HP - (float)this.HT / 2.0f) / ((float)this.HT / 2.0f));
                level.placeTrapsInTenguCell(fill);
            } else {
                int newPos;
                int tries = 100;
                do {
                    newPos = Random.Int(level.length());
                } while (--tries > 0 && (level.solid[newPos] || level.distance(newPos, this.enemy.pos) < 5 || level.distance(newPos, this.enemy.pos) > 7 || level.distance(newPos, Dungeon.hero.pos) < 5 || level.distance(newPos, Dungeon.hero.pos) > 7 || level.distance(newPos, this.pos) < 5 || Actor.findChar(newPos) != null || Dungeon.level.heaps.get(newPos) != null));
                if (tries <= 0) {
                    newPos = this.pos;
                }
                if (level.heroFOV[this.pos]) {
                    CellEmitter.get(this.pos).burst(Speck.factory(7), 6);
                }
                this.sprite.move(this.pos, newPos);
                this.move(newPos);
                if (this.arenaJumps < 4) {
                    ++this.arenaJumps;
                }
                if (level.heroFOV[newPos]) {
                    CellEmitter.get(newPos).burst(Speck.factory(7), 6);
                }
                Sample.INSTANCE.play("sounds/puff.mp3");
            }
        } else {
            Level level = Dungeon.level;
            int newPos = level.randomRespawnCell(this);
            if (level.heroFOV[this.pos]) {
                CellEmitter.get(this.pos).burst(Speck.factory(7), 6);
            }
            this.sprite.move(this.pos, newPos);
            this.move(newPos);
            if (level.heroFOV[newPos]) {
                CellEmitter.get(newPos).burst(Speck.factory(7), 6);
            }
            Sample.INSTANCE.play("sounds/puff.mp3");
        }
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            if (this.HP <= this.HT / 2) {
                BossHealthBar.bleed(true);
            }
            if (this.HP == this.HT) {
                this.yell(Messages.get(this, "notice_gotcha", Dungeon.hero.name()));
                for (Char ch : Actor.chars()) {
                    if (!(ch instanceof DriedRose.GhostHero)) continue;
                    ((DriedRose.GhostHero)ch).sayBoss();
                }
            } else {
                this.yell(Messages.get(this, "notice_have", Dungeon.hero.name()));
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LAST_ABILITY, this.lastAbility);
        bundle.put(ABILITIES_USED, this.abilitiesUsed);
        bundle.put(ARENA_JUMPS, this.arenaJumps);
        bundle.put(ABILITY_COOLDOWN, this.abilityCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.loading = true;
        super.restoreFromBundle(bundle);
        this.loading = false;
        this.lastAbility = bundle.getInt(LAST_ABILITY);
        this.abilitiesUsed = bundle.getInt(ABILITIES_USED);
        this.arenaJumps = bundle.getInt(ARENA_JUMPS);
        this.abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
        BossHealthBar.assignBoss(this);
        if (this.HP <= this.HT / 2) {
            BossHealthBar.bleed(true);
        }
    }

    public boolean canUseAbility() {
        if (this.HP > this.HT / 2) {
            return false;
        }
        if (this.abilitiesUsed >= this.targetAbilityUses()) {
            return false;
        }
        --this.abilityCooldown;
        if (this.targetAbilityUses() - this.abilitiesUsed >= 4 && !Dungeon.isChallenged(256)) {
            this.abilityCooldown = 0;
        } else if (this.targetAbilityUses() - this.abilitiesUsed >= 3) {
            if (this.abilityCooldown == -1 || this.abilityCooldown > 1) {
                this.abilityCooldown = 1;
            }
        } else if (this.abilityCooldown == -1) {
            this.abilityCooldown = Random.IntRange(1, 4);
        }
        return this.abilityCooldown == 0;
    }

    private int targetAbilityUses() {
        int targetAbilityUses = 1 + 2 * this.arenaJumps;
        return targetAbilityUses += Math.max(0, this.arenaJumps - 2);
    }

    public boolean useAbility() {
        boolean abilityUsed = false;
        int abilityToUse = -1;
        while (!abilityUsed) {
            abilityToUse = this.abilitiesUsed == 0 ? 0 : (this.abilitiesUsed == 1 ? 2 : (Dungeon.isChallenged(256) ? Random.Int(2) * 2 : Random.Int(3)));
            if (abilityToUse == this.lastAbility && Random.Int(10) != 0) continue;
            switch (abilityToUse) {
                default: {
                    abilityUsed = Tengu.throwBomb(this, Dungeon.hero);
                    if (this.abilitiesUsed != 0 || abilityUsed) break;
                    abilityToUse = 1;
                    abilityUsed = Tengu.throwFire(this, Dungeon.hero);
                    break;
                }
                case 1: {
                    abilityUsed = Tengu.throwFire(this, Dungeon.hero);
                    break;
                }
                case 2: {
                    abilityUsed = Tengu.throwShocker(this, Dungeon.hero);
                    if (this.abilitiesUsed != 1 || abilityUsed) break;
                    abilityToUse = 1;
                    abilityUsed = Tengu.throwFire(this, Dungeon.hero);
                }
            }
            if (!abilityUsed || abilityToUse == 1 || !Dungeon.isChallenged(256)) continue;
            Tengu.throwFire(this, Dungeon.hero);
        }
        if (Dungeon.isChallenged(256)) {
            if (this.targetAbilityUses() - this.abilitiesUsed < 4) {
                this.spend(1.0f);
            }
        } else if (this.targetAbilityUses() - this.abilitiesUsed >= 4) {
            this.spend(1.0f);
        } else {
            this.spend(2.0f);
        }
        this.lastAbility = abilityToUse;
        ++this.abilitiesUsed;
        return this.lastAbility == 1;
    }

    public static boolean throwBomb(final Char thrower, Char target) {
        int targetCell = -1;
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = target.pos + i;
            boolean bombHere = false;
            for (BombAbility b : thrower.buffs(BombAbility.class)) {
                if (b.bombPos != cell) continue;
                bombHere = true;
            }
            if (bombHere || Dungeon.level.solid[cell] || targetCell != -1 && !(Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos))) continue;
            targetCell = cell;
        }
        if (targetCell == -1) {
            return false;
        }
        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final BombAbility.BombItem item = new BombAbility.BombItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite)thrower.sprite.parent.recycle(MissileSprite.class)).reset((Visual)thrower.sprite, finalTargetCell, (Item)item, new Callback(){

            @Override
            public void call() {
                item.onThrow(finalTargetCell);
                thrower.next();
            }
        });
        return true;
    }

    public static boolean throwFire(Char thrower, Char target) {
        Ballistica aim = new Ballistica(thrower.pos, target.pos, 0);
        for (int i = 0; i < PathFinder.CIRCLE8.length; ++i) {
            if (aim.sourcePos + PathFinder.CIRCLE8[i] != aim.path.get(1)) continue;
            thrower.sprite.zap(target.pos);
            Buff.append((Char)thrower, FireAbility.class).direction = i;
            thrower.sprite.emitter().start(Speck.factory(13), 0.03f, 10);
            return true;
        }
        return false;
    }

    public static boolean throwShocker(final Char thrower, Char target) {
        int targetCell = -1;
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = target.pos + i;
            if (Dungeon.level.distance(cell, thrower.pos) < 2 || Dungeon.level.solid[cell]) continue;
            boolean validTarget = true;
            for (ShockerAbility s : thrower.buffs(ShockerAbility.class)) {
                if (Dungeon.level.distance(cell, s.shockerPos) >= 2) continue;
                validTarget = false;
                break;
            }
            if (!validTarget || !(Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos))) continue;
            targetCell = cell;
        }
        if (targetCell == -1) {
            return false;
        }
        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final ShockerAbility.ShockerItem item = new ShockerAbility.ShockerItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite)thrower.sprite.parent.recycle(MissileSprite.class)).reset((Visual)thrower.sprite, finalTargetCell, (Item)item, new Callback(){

            @Override
            public void call() {
                item.onThrow(finalTargetCell);
                thrower.next();
            }
        });
        return true;
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Tengu.this.enemySeen = enemyInFOV;
            if (enemyInFOV && !Tengu.this.isCharmedBy(Tengu.this.enemy) && Tengu.this.canAttack(Tengu.this.enemy)) {
                if (Tengu.this.canUseAbility()) {
                    return Tengu.this.useAbility();
                }
                Tengu.this.recentlyAttackedBy.clear();
                Tengu.this.target = Tengu.this.enemy.pos;
                return Tengu.this.doAttack(Tengu.this.enemy);
            }
            return this.handleUnreachableTarget(enemyInFOV, justAlerted);
        }

        @Override
        protected boolean handleUnreachableTarget(boolean enemyInFOV, boolean justAlerted) {
            Char oldEnemy = Tengu.this.enemy;
            Tengu.this.enemy = null;
            Tengu.this.enemy = Tengu.this.chooseEnemy();
            if (Tengu.this.enemy != null && Tengu.this.enemy != oldEnemy) {
                this.recursing = true;
                boolean result = this.act(enemyInFOV, justAlerted);
                this.recursing = false;
                return result;
            }
            if (Tengu.this.canUseAbility()) {
                return Tengu.this.useAbility();
            }
            Tengu.this.spend(1.0f);
            return true;
        }
    }

    public static class BombAbility
    extends Buff {
        public int bombPos = -1;
        private int timer = 3;
        private ArrayList<Emitter> smokeEmitters = new ArrayList();
        private static final String BOMB_POS = "bomb_pos";
        private static final String TIMER = "timer";

        @Override
        public boolean act() {
            if (this.smokeEmitters.isEmpty()) {
                this.fx(true);
            }
            PointF p = DungeonTilemap.raisedTileCenterToWorld(this.bombPos);
            if (this.timer == 3) {
                FloatingText.show(p.x, p.y, this.bombPos, "3...", 0xFF8800);
            } else if (this.timer == 2) {
                FloatingText.show(p.x, p.y, this.bombPos, "2...", 0xFF8800);
            } else if (this.timer == 1) {
                FloatingText.show(p.x, p.y, this.bombPos, "1...", 0xFF8800);
            } else {
                PathFinder.buildDistanceMap(this.bombPos, BArray.not(Dungeon.level.solid, null), 2);
                for (int cell = 0; cell < PathFinder.distance.length; ++cell) {
                    Char ch;
                    if (PathFinder.distance[cell] >= Integer.MAX_VALUE || (ch = Actor.findChar(cell)) == null || ch instanceof Tengu) continue;
                    int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
                    if ((dmg -= ch.drRoll()) > 0) {
                        ch.damage(dmg, Bomb.class);
                    }
                    if (ch != Dungeon.hero) continue;
                    Statistics.qualifiedForBossChallengeBadge = false;
                    Statistics.bossScores[1] = Statistics.bossScores[1] - 100;
                    if (ch.isAlive()) continue;
                    Dungeon.fail(Tengu.class);
                }
                Heap h = (Heap)Dungeon.level.heaps.get(this.bombPos);
                if (h != null) {
                    for (Item i : h.items.toArray(new Item[0])) {
                        if (!(i instanceof BombItem)) continue;
                        h.remove(i);
                    }
                }
                Sample.INSTANCE.play("sounds/blast.mp3");
                this.detach();
                return true;
            }
            --this.timer;
            this.spend(1.0f);
            return true;
        }

        @Override
        public void fx(boolean on) {
            block3: {
                block2: {
                    if (!on || this.bombPos == -1) break block2;
                    PathFinder.buildDistanceMap(this.bombPos, BArray.not(Dungeon.level.solid, null), 2);
                    for (int i = 0; i < PathFinder.distance.length; ++i) {
                        if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
                        Emitter e = CellEmitter.get(i);
                        e.pour(SmokeParticle.FACTORY, 0.25f);
                        this.smokeEmitters.add(e);
                    }
                    break block3;
                }
                if (on) break block3;
                for (Emitter e : this.smokeEmitters) {
                    e.burst(BlastParticle.FACTORY, 2);
                }
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOMB_POS, this.bombPos);
            bundle.put(TIMER, this.timer);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.bombPos = bundle.getInt(BOMB_POS);
            this.timer = bundle.getInt(TIMER);
        }

        public static class BombItem
        extends Item {
            public BombItem() {
                this.dropsDownHeap = true;
                this.unique = true;
                this.image = ItemSpriteSheet.TENGU_BOMB;
            }

            @Override
            public boolean doPickUp(Hero hero, int pos) {
                GLog.w(Messages.get(this, "cant_pickup", new Object[0]), new Object[0]);
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                super.onThrow(cell);
                if (throwingChar != null) {
                    Buff.append((Char)Tengu.throwingChar, BombAbility.class).bombPos = cell;
                    throwingChar = null;
                } else {
                    Buff.append((Char)BombItem.curUser, BombAbility.class).bombPos = cell;
                }
            }

            @Override
            public Emitter emitter() {
                Emitter emitter = new Emitter();
                emitter.pos(7.5f, 3.5f);
                emitter.fillTarget = false;
                emitter.pour(SmokeParticle.SPEW, 0.05f);
                return emitter;
            }
        }
    }

    public static class FireAbility
    extends Buff {
        public int direction;
        private int[] curCells;
        HashSet<Integer> toCells = new HashSet();
        private static final String DIRECTION = "direction";
        private static final String CUR_CELLS = "cur_cells";

        @Override
        public boolean act() {
            Integer c;
            int n;
            int n2;
            int[] nArray;
            this.toCells.clear();
            if (this.curCells == null) {
                this.curCells = new int[1];
                this.curCells[0] = this.target.pos;
                this.spreadFromCell(this.curCells[0]);
            } else {
                nArray = this.curCells;
                n2 = nArray.length;
                for (n = 0; n < n2; ++n) {
                    c = nArray[n];
                    if (FireBlob.volumeAt(c, FireBlob.class) <= 0) continue;
                    this.spreadFromCell(c);
                }
            }
            nArray = this.curCells;
            n2 = nArray.length;
            for (n = 0; n < n2; ++n) {
                c = nArray[n];
                this.toCells.remove(c);
            }
            if (this.toCells.isEmpty()) {
                this.detach();
            } else {
                this.curCells = new int[this.toCells.size()];
                int i = 0;
                for (Integer c2 : this.toCells) {
                    GameScene.add(Blob.seed(c2, 2, FireBlob.class));
                    this.curCells[i] = c2;
                    ++i;
                }
            }
            this.spend(1.0f);
            return true;
        }

        private void spreadFromCell(int cell) {
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[this.left(this.direction)]]) {
                this.toCells.add(cell + PathFinder.CIRCLE8[this.left(this.direction)]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[this.direction]]) {
                this.toCells.add(cell + PathFinder.CIRCLE8[this.direction]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[this.right(this.direction)]]) {
                this.toCells.add(cell + PathFinder.CIRCLE8[this.right(this.direction)]);
            }
        }

        private int left(int direction) {
            return direction == 0 ? 7 : direction - 1;
        }

        private int right(int direction) {
            return direction == 7 ? 0 : direction + 1;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DIRECTION, this.direction);
            if (this.curCells != null) {
                bundle.put(CUR_CELLS, this.curCells);
            }
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.direction = bundle.getInt(DIRECTION);
            if (bundle.contains(CUR_CELLS)) {
                this.curCells = bundle.getIntArray(CUR_CELLS);
            }
        }

        public static class FireBlob
        extends Blob {
            public FireBlob() {
                this.actPriority = -31;
                this.alwaysVisible = true;
            }

            @Override
            protected void evolve() {
                boolean observe = false;
                boolean burned = false;
                for (int i = this.area.left; i < this.area.right; ++i) {
                    for (int j = this.area.top; j < this.area.bottom; ++j) {
                        Plant plant;
                        Heap heap;
                        int cell = i + j * Dungeon.level.width();
                        this.off[cell] = (int)GameMath.gate(0.0f, this.cur[cell] - 1, 1.0f);
                        if (this.off[cell] > 0) {
                            this.volume += this.off[cell];
                        }
                        if (this.cur[cell] <= 0 || this.off[cell] != 0) continue;
                        Char ch = Actor.findChar(cell);
                        if (ch != null && !ch.isImmune(Fire.class) && !(ch instanceof Tengu)) {
                            Buff.affect(ch, Burning.class).reignite(ch);
                        }
                        if (ch == Dungeon.hero) {
                            Statistics.qualifiedForBossChallengeBadge = false;
                            Statistics.bossScores[1] = Statistics.bossScores[1] - 100;
                        }
                        if ((heap = (Heap)Dungeon.level.heaps.get(cell)) != null) {
                            heap.burn();
                        }
                        if ((plant = (Plant)Dungeon.level.plants.get(cell)) != null) {
                            plant.wither();
                        }
                        if (Dungeon.level.flamable[cell]) {
                            Dungeon.level.destroy(cell);
                            observe = true;
                            GameScene.updateMap(cell);
                        }
                        burned = true;
                        CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.03f, 10);
                    }
                }
                if (observe) {
                    Dungeon.observe();
                }
                if (burned) {
                    Sample.INSTANCE.play("sounds/burning.mp3");
                }
            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);
                emitter.pour(Speck.factory(13), 0.2f);
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc", new Object[0]);
            }
        }
    }

    public static class ShockerAbility
    extends Buff {
        public int shockerPos;
        private Boolean shockingOrdinals = null;
        private static final String SHOCKER_POS = "shocker_pos";
        private static final String SHOCKING_ORDINALS = "shocking_ordinals";

        @Override
        public boolean act() {
            if (this.shockingOrdinals == null) {
                this.shockingOrdinals = Random.Int(2) == 1;
                this.spreadblob();
            } else if (this.shockingOrdinals.booleanValue()) {
                this.target.sprite.parent.add(new Lightning(this.shockerPos - 1 - Dungeon.level.width(), this.shockerPos + 1 + Dungeon.level.width(), null));
                this.target.sprite.parent.add(new Lightning(this.shockerPos - 1 + Dungeon.level.width(), this.shockerPos + 1 - Dungeon.level.width(), null));
                if (Dungeon.level.distance(Dungeon.hero.pos, this.shockerPos) <= 1) {
                    Sample.INSTANCE.play("sounds/lightning.mp3");
                }
                this.shockingOrdinals = false;
                this.spreadblob();
            } else {
                this.target.sprite.parent.add(new Lightning(this.shockerPos - Dungeon.level.width(), this.shockerPos + Dungeon.level.width(), null));
                this.target.sprite.parent.add(new Lightning(this.shockerPos - 1, this.shockerPos + 1, null));
                if (Dungeon.level.distance(Dungeon.hero.pos, this.shockerPos) <= 1) {
                    Sample.INSTANCE.play("sounds/lightning.mp3");
                }
                this.shockingOrdinals = true;
                this.spreadblob();
            }
            this.spend(1.0f);
            return true;
        }

        private void spreadblob() {
            int i;
            GameScene.add(Blob.seed(this.shockerPos, 1, ShockerBlob.class));
            int n = i = this.shockingOrdinals != false ? 0 : 1;
            while (i < PathFinder.CIRCLE8.length) {
                if (!Dungeon.level.solid[this.shockerPos + PathFinder.CIRCLE8[i]]) {
                    GameScene.add(Blob.seed(this.shockerPos + PathFinder.CIRCLE8[i], 2, ShockerBlob.class));
                }
                i += 2;
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SHOCKER_POS, this.shockerPos);
            if (this.shockingOrdinals != null) {
                bundle.put(SHOCKING_ORDINALS, this.shockingOrdinals);
            }
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.shockerPos = bundle.getInt(SHOCKER_POS);
            if (bundle.contains(SHOCKING_ORDINALS)) {
                this.shockingOrdinals = bundle.getBoolean(SHOCKING_ORDINALS);
            }
        }

        public static class ShockerBlob
        extends Blob {
            public ShockerBlob() {
                this.actPriority = -31;
                this.alwaysVisible = true;
            }

            @Override
            protected void evolve() {
                boolean shocked = false;
                for (int i = this.area.left; i < this.area.right; ++i) {
                    for (int j = this.area.top; j < this.area.bottom; ++j) {
                        int cell = i + j * Dungeon.level.width();
                        int n = this.off[cell] = this.cur[cell] > 0 ? this.cur[cell] - 1 : 0;
                        if (this.off[cell] > 0) {
                            this.volume += this.off[cell];
                        }
                        if (this.cur[cell] <= 0 || this.off[cell] != 0) continue;
                        shocked = true;
                        Char ch = Actor.findChar(cell);
                        if (ch == null || ch instanceof Tengu) continue;
                        ch.damage(2 + Dungeon.scalingDepth(), new Electricity());
                        if (ch != Dungeon.hero) continue;
                        Statistics.qualifiedForBossChallengeBadge = false;
                        Statistics.bossScores[1] = Statistics.bossScores[1] - 100;
                        if (ch.isAlive()) continue;
                        Dungeon.fail(Tengu.class);
                        GLog.n(Messages.get(Electricity.class, "ondeath", new Object[0]), new Object[0]);
                    }
                }
                if (shocked) {
                    Sample.INSTANCE.play("sounds/lightning.mp3");
                }
            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);
                emitter.pour(SparkParticle.STATIC, 0.1f);
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc", new Object[0]);
            }
        }

        public static class ShockerItem
        extends Item {
            public ShockerItem() {
                this.dropsDownHeap = true;
                this.unique = true;
                this.image = ItemSpriteSheet.TENGU_SHOCKER;
            }

            @Override
            public boolean doPickUp(Hero hero, int pos) {
                GLog.w(Messages.get(this, "cant_pickup", new Object[0]), new Object[0]);
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                ShockerAbility buff;
                super.onThrow(cell);
                if (throwingChar != null) {
                    buff = Buff.append(throwingChar, ShockerAbility.class);
                    throwingChar = null;
                } else {
                    buff = Buff.append(curUser, ShockerAbility.class);
                }
                buff.shockerPos = cell;
                buff.spendToWhole();
            }

            @Override
            public Emitter emitter() {
                Emitter emitter = new Emitter();
                emitter.pos(5.0f, 5.0f);
                emitter.fillTarget = false;
                emitter.pour(SparkParticle.FACTORY, 0.1f);
                return emitter;
            }
        }
    }
}

