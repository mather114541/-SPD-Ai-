/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultBossElementalSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class VaultBossElemental
extends Mob {
    private ElementalForm form;
    private float[] formChances;
    protected int envAttackCooldown;
    protected int spAttackCooldown;
    protected int spTargetCell;
    protected int lastEnemyPos;
    private boolean weakAnnounced;
    private static final String FORM = "elemental_form";
    private static final String ENV_ATK_COOLDOWN = "env_atk_cooldown";
    private static final String SP_ATK_COOLDOWN = "sp_atk_cooldown";
    private static final String SP_TARGET_CELL = "sp_target_cell";
    private static final String LAST_ENEMY_POS = "last_enemy_pos";
    private static final String LIGHTNING_OFS = "lightning_ofs";
    private int lightningOfs;

    public VaultBossElemental() {
        this.HT = 600;
        this.HP = 600;
        this.spriteClass = VaultBossElementalSprite.class;
        this.EXP = 30;
        this.defenseSkill = 20;
        this.properties.add(Char.Property.BOSS);
        this.form = null;
        this.formChances = new float[]{2.0f, 2.0f, 2.0f};
        this.envAttackCooldown = Random.NormalIntRange(10, 15);
        this.spAttackCooldown = Random.NormalIntRange(6, 10);
        this.spTargetCell = -1;
        this.lastEnemyPos = -1;
        this.weakAnnounced = false;
        this.form = ElementalForm.values()[Random.Int(3)];
        int n = this.form.ordinal();
        this.formChances[n] = this.formChances[n] - 1.0f;
    }

    @Override
    public int damageRoll() {
        if (this.form == ElementalForm.FROST) {
            return Random.NormalIntRange(15, 20);
        }
        return Random.NormalIntRange(20, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.form == ElementalForm.FIRE && enemy instanceof Hero && ((Hero)enemy).belongings.attackingWeapon() instanceof MissileWeapon && !Dungeon.level.adjacent(this.pos, enemy.pos)) {
            return 0;
        }
        if (this.form == ElementalForm.FROST && enemy instanceof Hero && ((Hero)enemy).belongings.attackingWeapon() instanceof MeleeWeapon) {
            return super.defenseSkill(enemy) / 2;
        }
        return super.defenseSkill(enemy);
    }

    public void changeForm() {
        ElementalForm newForm;
        while ((newForm = ElementalForm.values()[Random.chances(this.formChances)]) == this.form) {
        }
        int n = newForm.ordinal();
        this.formChances[n] = this.formChances[n] - 1.0f;
        this.setElementalForm(newForm);
    }

    public ElementalForm curForm() {
        return this.form;
    }

    public void setElementalForm(ElementalForm form) {
        Buff.affect((Char)this, PinCushionRemover.class).preferGrouping = this.form == ElementalForm.FIRE;
        this.form = form;
        boolean wasTurned = this.sprite.flipHorizontal;
        ((VaultBossElementalSprite)this.sprite).updateForm();
        AttackIndicator.target(this);
        Emitter e = this.sprite.emitter();
        e.fillTarget = false;
        e.pos(this.sprite, 4.0f, 4.0f, 24.0f, 24.0f);
        if (form == ElementalForm.FIRE) {
            e.burst(FlameParticle.FACTORY, 50);
            for (Buff b : this.buffs()) {
                if (!(b instanceof Chill) && !(b instanceof Frost)) continue;
                b.detach();
            }
        } else if (form == ElementalForm.FROST) {
            e.burst(MagicMissile.MagicParticle.FACTORY, 50);
            for (Buff b : this.buffs()) {
                if (!(b instanceof Burning)) continue;
                b.detach();
            }
        } else if (form == ElementalForm.SHOCK) {
            e.burst(SparkParticle.FACTORY, 50);
        }
        if (this.spTargetCell != -1) {
            this.spTargetCell = -1;
            this.spAttackCooldown = 0;
        }
        this.envAttackCooldown /= 2;
        this.sprite.flipHorizontal = wasTurned;
        BossHealthBar.assignBoss(this, true);
        this.weakAnnounced = false;
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        this.enemySeen = true;
    }

    @Override
    protected boolean act() {
        if (this.spTargetCell != -1 && this.paralysed == 0) {
            if (this.sprite != null && (this.sprite.visible || this.enemy.sprite.visible)) {
                this.sprite.zap(this.spTargetCell);
                this.lastEnemyPos = this.enemy.pos;
                return false;
            }
            this.zap();
            this.lastEnemyPos = this.enemy.pos;
            return true;
        }
        --this.spAttackCooldown;
        --this.envAttackCooldown;
        if (this.state == this.HUNTING && this.paralysed == 0) {
            if (this.spAttackCooldown <= 0) {
                this.spend(GameMath.gate(this.attackDelay(), (int)Math.ceil(Dungeon.hero.cooldown()), 3.0f * this.attackDelay()));
                if (this.form == ElementalForm.FIRE) {
                    this.setupFireBall(this.enemy);
                } else if (this.form == ElementalForm.FROST) {
                    this.setupFrostCone(this.enemy);
                } else if (this.form == ElementalForm.SHOCK) {
                    this.setupLightningBolt(this.enemy);
                }
                Dungeon.hero.interrupt();
                this.lastEnemyPos = this.enemy.pos;
                return true;
            }
            if (this.envAttackCooldown <= 0) {
                this.spend(1.0f);
                if (this.form == ElementalForm.FIRE) {
                    this.setupFireWall();
                } else if (this.form == ElementalForm.FROST) {
                    this.setupFrostVortex();
                } else if (this.form == ElementalForm.SHOCK) {
                    this.setupLightningChase();
                }
                this.sprite.operate(this.enemy.pos);
                this.envAttackCooldown = Random.NormalIntRange(10, 15);
                if (this.form == ElementalForm.SHOCK) {
                    this.spAttackCooldown = (int)((float)this.spAttackCooldown * 0.67f);
                }
                Dungeon.hero.interrupt();
                this.lastEnemyPos = this.enemy.pos;
                return true;
            }
        }
        Mob.AiState lastState = this.state;
        boolean result = super.act();
        if (lastState != this.WANDERING || this.state != this.HUNTING) {
            this.lastEnemyPos = this.enemy != null ? this.enemy.pos : Dungeon.hero.pos;
        }
        return result;
    }

    protected void zap() {
        this.spend(1.0f);
        Invisibility.dispel(this);
        if (this.form == ElementalForm.FIRE) {
            this.doFireBall(this.spTargetCell);
        } else if (this.form == ElementalForm.FROST) {
            this.doFrostCone(this.spTargetCell);
        } else if (this.form == ElementalForm.SHOCK) {
            this.doLightningBolt(this.spTargetCell);
        }
        this.spAttackCooldown = Random.NormalIntRange(6, 10);
        if (this.form == ElementalForm.SHOCK) {
            this.spAttackCooldown = (int)((float)this.spAttackCooldown * 0.67f);
        }
        this.spTargetCell = -1;
    }

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (this.form == ElementalForm.SHOCK && enemy == Dungeon.hero && !(Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon)) {
            enemy.sprite.parent.addToFront(new Lightning(this.sprite.center(), enemy.sprite.center(), null));
            enemy.damage(Random.IntRange(5, 10), new Shocking());
            Sample.INSTANCE.play("sounds/lightning.mp3");
            PixelScene.shake(2.0f, 0.3f);
            enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            enemy.sprite.flash();
            if (!enemy.isAlive()) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
            } else {
                GLog.w(Messages.get(this, "shock_resist", new Object[0]), new Object[0]);
            }
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public void damage(int dmg, Object src) {
        if (this.form == ElementalForm.FIRE) {
            if (AntiMagic.RESISTS.contains(src.getClass())) {
                dmg /= 4;
                if (src instanceof Wand || src instanceof ClericSpell) {
                    GLog.w(Messages.get(this, "fire_resist", new Object[0]), new Object[0]);
                    this.spAttackCooldown -= 3;
                    this.envAttackCooldown -= 5;
                }
            } else if (src == Dungeon.hero && Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                if (!this.weakAnnounced) {
                    GLog.p(Messages.get(this, "fire_weak", new Object[0]), new Object[0]);
                    this.weakAnnounced = true;
                }
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                dmg += 10;
            }
        } else if (this.form == ElementalForm.FROST) {
            if (src == Dungeon.hero && Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                GLog.w(Messages.get(this, "frost_resist", new Object[0]), new Object[0]);
                dmg /= 4;
            } else if (src == Dungeon.hero && !(Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon)) {
                if (!this.weakAnnounced) {
                    GLog.p(Messages.get(this, "frost_weak", new Object[0]), new Object[0]);
                    this.weakAnnounced = true;
                }
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                dmg += 10;
            }
        } else if (this.form == ElementalForm.SHOCK) {
            if (AntiMagic.RESISTS.contains(src.getClass())) {
                if (!this.weakAnnounced) {
                    GLog.p(Messages.get(this, "shock_weak", new Object[0]), new Object[0]);
                    this.weakAnnounced = true;
                }
                if (src instanceof Wand || src instanceof ClericSpell) {
                    Dungeon.hero.belongings.charge(0.2f);
                }
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                dmg += 10;
            } else if (src instanceof Char && (src != Dungeon.hero || !(Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon))) {
                dmg /= 4;
            }
        }
        int hpBracket = this.HT / 5;
        int curbracket = (int)Math.ceil((float)this.HP / (float)hpBracket);
        int preHP = this.HP;
        super.damage(dmg, src);
        int dmgTaken = preHP - this.HP;
        if (dmgTaken > 0) {
            this.envAttackCooldown = (int)((float)this.envAttackCooldown - (float)dmgTaken / 24.0f);
            this.spAttackCooldown = (int)((float)this.spAttackCooldown - (float)dmgTaken / 12.0f);
        }
        if (this.HP <= (curbracket - 1) * hpBracket && this.isAlive()) {
            ElementalForm newForm;
            this.HP = Math.max(this.HP, (curbracket - 2) * hpBracket);
            while ((newForm = ElementalForm.values()[Random.Int(3)]) == this.form) {
            }
            this.changeForm();
            this.spend(1.0f);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.unseal();
        GameScene.bossSlain();
    }

    @Override
    public CharSprite sprite() {
        if (this.form == null) {
            this.changeForm();
        }
        CharSprite sprite = super.sprite();
        if (this.form != null) {
            ((VaultBossElementalSprite)sprite).setForm(this.form);
        }
        return sprite;
    }

    @Override
    public boolean add(Buff buff) {
        if (buff instanceof PinCushion && this.form != ElementalForm.FROST) {
            Buff.affect((Char)this, PinCushionRemover.class).preferGrouping = this.form == ElementalForm.FIRE;
        }
        boolean harmful = false;
        if (this.form == ElementalForm.FIRE) {
            harmful = buff instanceof Frost || buff instanceof Chill;
        } else if (this.form == ElementalForm.FROST) {
            harmful = buff instanceof Burning;
        }
        if (harmful) {
            this.damage(Random.NormalIntRange(5, 10), buff);
            return false;
        }
        return super.add(buff);
    }

    @Override
    public HashSet<Char.Property> properties() {
        HashSet<Char.Property> props = new HashSet<Char.Property>(this.properties);
        if (this.form == ElementalForm.FIRE) {
            props.add(Char.Property.FIERY);
        } else if (this.form == ElementalForm.FROST) {
            props.add(Char.Property.ICY);
        } else if (this.form == ElementalForm.SHOCK) {
            props.add(Char.Property.ELECTRIC);
        }
        return props;
    }

    @Override
    public float resist(Class effect) {
        if (this.form == ElementalForm.SHOCK && effect == WandOfLightning.class) {
            return 1.0f;
        }
        return super.resist(effect);
    }

    @Override
    public String description() {
        String desc = super.description();
        if (this.form != null) {
            switch (this.form.ordinal()) {
                default: {
                    return desc + "\n\n" + Messages.get(this, "desc_fire", new Object[0]);
                }
                case 1: {
                    return desc + "\n\n" + Messages.get(this, "desc_frost", new Object[0]);
                }
                case 2: 
            }
            return desc + "\n\n" + Messages.get(this, "desc_shock", new Object[0]);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FORM, this.form);
        bundle.put(ENV_ATK_COOLDOWN, this.envAttackCooldown);
        bundle.put(SP_ATK_COOLDOWN, this.spAttackCooldown);
        bundle.put(SP_TARGET_CELL, this.spTargetCell);
        bundle.put(LAST_ENEMY_POS, this.lastEnemyPos);
        bundle.put(LIGHTNING_OFS, this.lightningOfs);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.form = bundle.getEnum(FORM, ElementalForm.class);
        this.envAttackCooldown = bundle.getInt(ENV_ATK_COOLDOWN);
        this.spAttackCooldown = bundle.getInt(SP_ATK_COOLDOWN);
        this.spTargetCell = bundle.getInt(SP_TARGET_CELL);
        this.lastEnemyPos = bundle.getInt(LAST_ENEMY_POS);
        this.lightningOfs = bundle.getInt(LIGHTNING_OFS);
        BossHealthBar.assignBoss(this);
    }

    public void setupFireBall(Char enemy) {
        this.spTargetCell = Dungeon.level.adjacent(enemy.pos, this.lastEnemyPos) ? enemy.pos + (enemy.pos - this.lastEnemyPos) : enemy.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
        FireWall wall = this.buff(FireWall.class);
        if (wall != null) {
            boolean valid;
            int ofs = 0;
            block0: do {
                valid = true;
                for (int i : wall.cells) {
                    if (i != this.spTargetCell + ofs) continue;
                    while (Actor.findChar(ofs = PathFinder.NEIGHBOURS8[Random.Int(4)]) != null) {
                    }
                    valid = false;
                    continue block0;
                }
            } while (!valid);
            this.spTargetCell += ofs;
        }
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[this.spTargetCell + i]) continue;
            GameScene.targetedCell(this.spTargetCell + i, this.cooldown());
        }
    }

    public void doFireBall(int cell) {
        Sample.INSTANCE.play("sounds/burning.mp3");
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[cell + i]) continue;
            CellEmitter.get(cell + i).burst(FlameParticle.FACTORY, 10);
            GameScene.add(Blob.seed(cell + i, 2, Fire.class));
            Char ch = Actor.findChar(cell + i);
            if (ch == null || ch instanceof VaultBossElemental) continue;
            Buff.affect(ch, Burning.class).reignite(ch);
            if (ch != Dungeon.hero) continue;
            Statistics.questScores[3] = Statistics.questScores[3] - 100;
        }
    }

    public void setupFireWall() {
        int maxSkipDist;
        int minSkipDist;
        FireWall wall = Buff.append(this, FireWall.class);
        wall.cells = new int[10];
        Room r = ((RegularLevel)Dungeon.level).room(this.pos);
        Point c = r.center();
        int i = 0;
        ArrayList<Integer> wallDistances = new ArrayList<Integer>();
        Point heroPos = Dungeon.level.cellToPoint(Dungeon.hero.pos);
        wallDistances.add(0, heroPos.x - (c.x - 6));
        wallDistances.add(1, heroPos.y - (c.y - 6));
        wallDistances.add(2, c.x + 6 - heroPos.x);
        wallDistances.add(3, c.y + 6 - heroPos.y);
        ArrayList sortedDistances = (ArrayList)wallDistances.clone();
        Collections.shuffle(sortedDistances);
        Collections.sort(sortedDistances);
        int wallFrom = 0;
        if (this.HP > this.HT / 2) {
            minSkipDist = 1;
            maxSkipDist = 3;
            while ((Integer)wallDistances.get(wallFrom = Random.Int(4)) < (Integer)sortedDistances.get(1)) {
            }
        } else {
            minSkipDist = 4;
            maxSkipDist = 6;
            if (((Integer)sortedDistances.get(0)).equals(sortedDistances.get(1)) && ((Integer)sortedDistances.get(2)).equals(sortedDistances.get(3))) {
                wallFrom = Random.Int(4);
            } else {
                while (((Integer)wallDistances.get(wallFrom = Random.Int(4))).equals(sortedDistances.get(0)) || ((Integer)wallDistances.get(wallFrom)).equals(sortedDistances.get(3))) {
                }
            }
        }
        if (wallFrom == 1 || wallFrom == 3) {
            int skip;
            int y;
            if (wallFrom == 1) {
                y = c.y - 6;
                wall.direction = Dungeon.level.width();
            } else {
                y = c.y + 6;
                wall.direction = -Dungeon.level.width();
            }
            while (Math.abs((skip = Random.IntRange(c.x - 5, c.x + 5)) - heroPos.x) > maxSkipDist || Math.abs(skip - heroPos.x) < minSkipDist) {
            }
            for (int x = c.x - 5; x <= c.x + 5; ++x) {
                if (x == skip) continue;
                wall.cells[i] = x + y * Dungeon.level.width();
                ++i;
            }
        } else {
            int skip;
            int x;
            if (wallFrom == 0) {
                x = c.x - 6;
                wall.direction = 1;
            } else {
                x = c.x + 6;
                wall.direction = -1;
            }
            while (Math.abs((skip = Random.IntRange(c.y - 5, c.y + 5)) - heroPos.y) > maxSkipDist || Math.abs(skip - heroPos.y) < minSkipDist) {
            }
            for (int y = c.y - 5; y <= c.y + 5; ++y) {
                if (y == skip) continue;
                wall.cells[i] = x + y * Dungeon.level.width();
                ++i;
            }
        }
    }

    public void setupFrostCone(Char enemy) {
        this.spTargetCell = enemy.pos;
        Ballistica core = new Ballistica(this.pos, enemy.pos, 0);
        ConeAOE cone = new ConeAOE(core, 10.0f, 50.0f, 4);
        for (int cell : cone.cells) {
            if (!(Dungeon.level.trueDistance(cell, this.pos) <= 2.0f)) continue;
            GameScene.targetedCell(cell, this.cooldown());
        }
    }

    public void doFrostCone(int cell) {
        FrostCone cone = Buff.append(this, FrostCone.class);
        Ballistica core = new Ballistica(this.pos, cell, 0);
        cone.cells = new ConeAOE((Ballistica)core, (float)10.0f, (float)50.0f, (int)4).cells;
        cone.startPos = this.pos;
    }

    public void setupFrostVortex() {
        FrostVortex vortex = Buff.append(this, FrostVortex.class);
        vortex.targetCell = Dungeon.hero.pos;
    }

    public void setupLightningBolt(Char enemy) {
        this.spTargetCell = enemy.pos;
        this.lightningOfs = Random.Int(2);
        Ballistica bolt = new Ballistica(this.pos, this.spTargetCell, 4);
        for (int cell : bolt.subPath(0, bolt.dist)) {
            GameScene.targetedCell(cell, this.cooldown());
        }
        for (int i = this.lightningOfs % 2; i < PathFinder.CIRCLE8.length; i += 2) {
            GameScene.targetedCell(this.spTargetCell + PathFinder.CIRCLE8[i], this.cooldown());
        }
    }

    public void doLightningBolt(int cell) {
        HashSet<Integer> affectedCells = new HashSet<Integer>();
        Ballistica bolt = new Ballistica(this.pos, cell, 4);
        for (int c : bolt.subPath(0, bolt.dist)) {
            CellEmitter.get(c).burst(SparkParticle.FACTORY, 5);
            affectedCells.add(c);
        }
        for (int i = this.lightningOfs % 2; i < PathFinder.CIRCLE8.length; i += 2) {
            CellEmitter.get(cell + PathFinder.CIRCLE8[i]).burst(SparkParticle.FACTORY, 5);
            affectedCells.add(cell + PathFinder.CIRCLE8[i]);
        }
        Iterator iterator = affectedCells.iterator();
        while (iterator.hasNext()) {
            int c;
            c = (Integer)iterator.next();
            Char ch = Actor.findChar(c);
            if (ch == null || ch instanceof VaultBossElemental || ch.buff(ShockResist.class) != null) continue;
            ch.damage(Random.NormalIntRange(20, 30), new Electricity());
            Buff.prolong(ch, Paralysis.class, 1.0f);
            Buff.affect(ch, ShockResist.class);
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
            if (ch != Dungeon.hero) continue;
            Sample.INSTANCE.play("sounds/lightning.mp3");
            PixelScene.shake(2.0f, 0.3f);
            Statistics.questScores[3] = Statistics.questScores[3] - 100;
            if (ch.isAlive()) continue;
            Badges.validateDeathFromEnemyMagic();
            Dungeon.fail(this);
        }
        this.sprite.parent.add(new Lightning(cell + PathFinder.CIRCLE8[this.lightningOfs], cell + PathFinder.CIRCLE8[this.lightningOfs + 4], null));
        this.sprite.parent.add(new Lightning(cell + PathFinder.CIRCLE8[this.lightningOfs + 2], cell + PathFinder.CIRCLE8[this.lightningOfs + 6], null));
    }

    public void setupLightningChase() {
        LightningChase chase;
        boolean alt;
        Room r = ((RegularLevel)Dungeon.level).room(this.pos);
        Point c = r.center();
        boolean bl = alt = Random.Int(2) == 0;
        if (alt || this.HP <= this.HT / 2) {
            chase = Buff.append(this, LightningChase.class);
            chase.curCell = c.x + (c.y - 5) * Dungeon.level.width();
            chase = Buff.append(this, LightningChase.class);
            chase.curCell = c.x + (c.y + 5) * Dungeon.level.width();
        }
        if (!alt || this.HP <= this.HT / 2) {
            chase = Buff.append(this, LightningChase.class);
            chase.curCell = c.x - 5 + c.y * Dungeon.level.width();
            chase = Buff.append(this, LightningChase.class);
            chase.curCell = c.x + 5 + c.y * Dungeon.level.width();
        }
    }

    public static enum ElementalForm {
        FIRE,
        FROST,
        SHOCK,
        UNSTABLE;

    }

    public static class PinCushionRemover
    extends Buff {
        public boolean preferGrouping;

        public PinCushionRemover() {
            this.actPriority = 100;
            this.preferGrouping = false;
        }

        @Override
        public boolean act() {
            if (this.target.buff(PinCushion.class) == null) {
                this.detach();
                return true;
            }
            PathFinder.buildDistanceMap(this.target.pos, Dungeon.level.passable, 2);
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            int furthestDist = 1;
            int closestDist = 100;
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                int dist;
                if (PathFinder.distance[i] != 2 || (dist = Dungeon.level.distance(i, Dungeon.hero.pos)) <= 1) continue;
                if (dist < closestDist) {
                    closestDist = dist;
                }
                if (dist > furthestDist) {
                    furthestDist = dist;
                }
                candidates.add(i);
            }
            if (Math.abs(furthestDist - closestDist) <= 1) {
                ++furthestDist;
                --closestDist;
            }
            ArrayList<Integer> existingStacks = new ArrayList<Integer>();
            Integer[] dist = candidates.toArray(new Integer[0]);
            int n = dist.length;
            for (int i = 0; i < n; ++i) {
                int i2 = dist[i];
                int dist2 = Dungeon.level.distance(i2, Dungeon.hero.pos);
                if (dist2 <= closestDist || dist2 >= furthestDist) {
                    candidates.remove((Object)i2);
                    continue;
                }
                if (Dungeon.level.heaps.get(i2) == null) continue;
                existingStacks.add(i2);
            }
            if (this.preferGrouping && !existingStacks.isEmpty()) {
                candidates = existingStacks;
            }
            while (this.target.buff(PinCushion.class) != null) {
                Item item = this.target.buff(PinCushion.class).grabOne();
                Dungeon.level.drop((Item)item, (int)((Integer)Random.element(candidates)).intValue()).sprite.drop(this.target.pos);
            }
            this.detach();
            return true;
        }
    }

    public static class FireWall
    extends Buff {
        private int[] cells = new int[0];
        private int direction;
        private int left = 11;
        private ArrayList<Emitter> emitters = new ArrayList();
        private static String CELLS = "cells";
        private static String DIRECTION = "direction";
        private static String LEFT = "left";

        @Override
        public boolean act() {
            int i = 0;
            while (i < this.cells.length) {
                for (int j = 0; j < 2; ++j) {
                    if (!Dungeon.level.insideMap(this.cells[i] + j * this.direction) || Dungeon.level.solid[this.cells[i] + j * this.direction]) continue;
                    CellEmitter.get(this.cells[i] + j * this.direction).burst(FlameParticle.FACTORY, 10);
                    Char ch = Actor.findChar(this.cells[i] + j * this.direction);
                    if (ch == null || ch instanceof VaultBossElemental) continue;
                    Buff.affect(ch, Burning.class).reignite(ch, 5.0f);
                    if (ch != Dungeon.hero) continue;
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    Statistics.questScores[3] = Statistics.questScores[3] - 100;
                }
                int n = i++;
                this.cells[n] = this.cells[n] + this.direction;
            }
            Sample.INSTANCE.play("sounds/burning.mp3", 0.5f);
            if (this.left-- <= 0) {
                this.detach();
            } else {
                this.updateFX();
            }
            this.spend(1.0f);
            return true;
        }

        private void updateFX() {
            for (Emitter e : this.emitters) {
                e.on = false;
            }
            this.emitters.clear();
            block1: for (Object cell : (Object)this.cells) {
                Emitter pour;
                int j;
                boolean oneOpen = false;
                for (j = 0; j < 2; ++j) {
                    if (!Dungeon.level.insideMap((int)(cell + j * this.direction)) || Dungeon.level.solid[cell + j * this.direction]) continue;
                    pour = CellEmitter.get((int)(cell + j * this.direction));
                    pour.pour(FlameParticle.FACTORY, 0.1f);
                    this.emitters.add(pour);
                    oneOpen = true;
                }
                if (oneOpen) continue;
                for (j = 1; j <= this.left; ++j) {
                    if (!Dungeon.level.insideMap((int)(cell + j * this.direction)) || Dungeon.level.solid[cell + this.direction * j]) continue;
                    pour = CellEmitter.center((int)(cell + this.direction * j));
                    pour.pour(FlameParticle.FACTORY, 0.5f);
                    this.emitters.add(pour);
                    continue block1;
                }
            }
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.updateFX();
            } else {
                for (Emitter e : this.emitters) {
                    e.on = false;
                }
                this.emitters.clear();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CELLS, this.cells);
            bundle.put(DIRECTION, this.direction);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.cells = bundle.getIntArray(CELLS);
            this.direction = bundle.getInt(DIRECTION);
            this.left = bundle.getInt(LEFT);
        }
    }

    public static class FrostCone
    extends Buff {
        private int startPos;
        private HashSet<Integer> cells = new HashSet();
        private int distance = 2;
        private HashSet<Emitter> emitters = new HashSet();
        public static final String START_POS = "start_pos";
        public static final String CELLS = "cells";
        public static final String DISTANCE = "distance";

        @Override
        public boolean act() {
            Object object = this.cells.toArray(new Integer[0]);
            int n = ((Integer[])object).length;
            for (int i = 0; i < n; ++i) {
                int cell = object[i];
                if (!(Dungeon.level.trueDistance(cell, this.startPos) <= (float)this.distance)) continue;
                CellEmitter.get(cell).burst(MagicMissile.WhiteParticle.FACTORY, 10);
                Char ch = Actor.findChar(cell);
                if (ch != null && !(ch instanceof VaultBossElemental) && ch.buff(FrostResist.class) == null) {
                    ch.damage(Random.NormalIntRange(10, 15), new Frost());
                    Buff.affect(ch, Frost.class, 5.0f);
                    Buff.affect(ch, FrostResist.class);
                    if (ch == Dungeon.hero) {
                        Statistics.questScores[3] = Statistics.questScores[3] - 100;
                        Sample.INSTANCE.play("sounds/shatter.mp3");
                        if (!ch.isAlive()) {
                            Badges.validateDeathFromEnemyMagic();
                            Dungeon.fail(this.target);
                        }
                    }
                }
                if (!(Dungeon.level.trueDistance(cell, this.startPos) <= (float)(this.distance - 4))) continue;
                this.cells.remove(cell);
            }
            this.updateFX();
            this.distance += 2;
            if (this.cells.isEmpty()) {
                this.detach();
            } else {
                this.spend(1.0f);
                object = this.cells.iterator();
                while (object.hasNext()) {
                    int cell = (Integer)object.next();
                    if (!(Dungeon.level.trueDistance(cell, this.startPos) <= (float)this.distance) || !(Dungeon.level.trueDistance(cell, this.startPos) > (float)(this.distance - 2))) continue;
                    GameScene.targetedCell(cell, this.cooldown());
                }
            }
            return true;
        }

        private void updateFX() {
            for (Emitter e : this.emitters) {
                e.on = false;
            }
            this.emitters.clear();
            Iterator<Object> iterator = this.cells.iterator();
            while (iterator.hasNext()) {
                int cell = (Integer)iterator.next();
                if (!(Dungeon.level.trueDistance(cell, this.startPos) <= (float)this.distance)) continue;
                Emitter e = CellEmitter.get(cell);
                e.pour(SnowParticle.FACTORY, 0.1f);
                this.emitters.add(e);
            }
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.updateFX();
            } else {
                for (Emitter e : this.emitters) {
                    e.on = false;
                }
                this.emitters.clear();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(START_POS, this.startPos);
            int[] bundleCells = new int[this.cells.size()];
            int i = 0;
            Iterator<Integer> iterator = this.cells.iterator();
            while (iterator.hasNext()) {
                int cell;
                bundleCells[i] = cell = iterator.next().intValue();
                ++i;
            }
            bundle.put(CELLS, bundleCells);
            bundle.put(DISTANCE, this.distance);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.startPos = bundle.getInt(START_POS);
            for (int cell : bundle.getIntArray(CELLS)) {
                this.cells.add(cell);
            }
            this.distance = bundle.getInt(DISTANCE);
        }
    }

    public static class FrostVortex
    extends Buff {
        private int targetCell = -1;
        private int distance = -1000;
        private boolean fullVortex = false;
        private boolean altDirection = false;
        private ArrayList<Emitter> emitters = new ArrayList();
        private static String TARGET_CELL = "target_cell";
        private static String DISTANCE = "distance";
        private static String FULL_VORTEX = "full_fortex";
        private static String ALT_DIR = "alt_direction";

        @Override
        public boolean act() {
            if (this.distance == -1000) {
                int furthestDist = 0;
                boolean valid = true;
                int dist = this.getFurthestValid(this.targetCell, -1, -1);
                if (dist > furthestDist) {
                    furthestDist = dist;
                }
                if ((dist = this.getFurthestValid(this.targetCell, -1, 1)) > furthestDist) {
                    furthestDist = dist;
                }
                if ((dist = this.getFurthestValid(this.targetCell, 1, 1)) > furthestDist) {
                    furthestDist = dist;
                }
                if ((dist = this.getFurthestValid(this.targetCell, 1, -1)) > furthestDist) {
                    furthestDist = dist;
                }
                this.distance = Math.min(furthestDist, 5);
                this.fullVortex = this.target.HP <= this.target.HT / 2;
                this.altDirection = Random.Int(2) == 0;
            }
            HashSet<Integer> cells = new HashSet<Integer>();
            if (this.fullVortex) {
                cells.addAll(this.getCells(this.targetCell, this.distance, -1, -1));
                cells.addAll(this.getCells(this.targetCell, this.distance, -1, 1));
                cells.addAll(this.getCells(this.targetCell, this.distance, 1, 1));
                cells.addAll(this.getCells(this.targetCell, this.distance, 1, -1));
            } else if (this.altDirection) {
                cells.addAll(this.getCells(this.targetCell, this.distance, -1, -1));
                cells.addAll(this.getCells(this.targetCell, this.distance, 1, 1));
            } else {
                cells.addAll(this.getCells(this.targetCell, this.distance, -1, 1));
                cells.addAll(this.getCells(this.targetCell, this.distance, 1, -1));
            }
            --this.distance;
            this.updateFX();
            if (cells.isEmpty()) {
                this.detach();
                return true;
            }
            for (Integer cell : cells) {
                CellEmitter.get(cell).burst(MagicMissile.WhiteParticle.FACTORY, 10);
                Char ch = Actor.findChar(cell);
                if (ch == null || ch instanceof VaultBossElemental || ch.buff(FrostResist.class) != null) continue;
                Buff.affect(ch, Frost.class, 5.0f);
                Buff.affect(ch, FrostResist.class);
                if (ch != Dungeon.hero) continue;
                Sample.INSTANCE.play("sounds/shatter.mp3");
                Statistics.questScores[3] = Statistics.questScores[3] - 100;
            }
            Sample.INSTANCE.play("sounds/gas.mp3", 0.25f);
            this.spend(1.0f);
            return true;
        }

        private void updateFX() {
            for (Emitter e : this.emitters) {
                e.on = false;
            }
            this.emitters.clear();
            if (this.targetCell != -1) {
                HashSet<Integer> cells = new HashSet<Integer>();
                if (this.fullVortex) {
                    cells.addAll(this.getCells(this.targetCell, this.distance, -1, -1));
                    cells.addAll(this.getCells(this.targetCell, this.distance, -1, 1));
                    cells.addAll(this.getCells(this.targetCell, this.distance, 1, 1));
                    cells.addAll(this.getCells(this.targetCell, this.distance, 1, -1));
                } else if (this.altDirection) {
                    cells.addAll(this.getCells(this.targetCell, this.distance, -1, -1));
                    cells.addAll(this.getCells(this.targetCell, this.distance, 1, 1));
                } else {
                    cells.addAll(this.getCells(this.targetCell, this.distance, -1, 1));
                    cells.addAll(this.getCells(this.targetCell, this.distance, 1, -1));
                }
                for (Integer cell : cells) {
                    Emitter pour = CellEmitter.get(cell);
                    pour.pour(SnowParticle.FACTORY, 0.1f);
                    this.emitters.add(pour);
                }
            }
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.updateFX();
            } else {
                for (Emitter e : this.emitters) {
                    e.on = false;
                }
                this.emitters.clear();
            }
        }

        private int getFurthestValid(int start, int dirX, int dirY) {
            int dist = 0;
            if (Dungeon.level.solid[start]) {
                return dist;
            }
            do {
                start = dist % 2 == 0 ? (start += dirX) : (start += dirY * Dungeon.level.width());
                ++dist;
            } while (Dungeon.level.insideMap(start) && !Dungeon.level.solid[start]);
            return dist;
        }

        private HashSet<Integer> getCells(int start, int dist, int dirX, int dirY) {
            dist = Math.abs(dist);
            if (this.getFurthestValid(start, dirX, dirY) < dist) {
                return new HashSet<Integer>();
            }
            int xOfs = dirX * (dist + 1) / 2;
            int yOfs = dirY * dist / 2 * Dungeon.level.width();
            int initialOfsCell = start + xOfs + yOfs;
            HashSet<Integer> cells = new HashSet<Integer>();
            int cell = initialOfsCell;
            do {
                cells.add(cell);
            } while (Dungeon.level.insideMap(cell += -dirX + dirY * Dungeon.level.width()) && !Dungeon.level.solid[cell]);
            cell = initialOfsCell;
            do {
                cells.add(cell);
            } while (Dungeon.level.insideMap(cell += dirX - dirY * Dungeon.level.width()) && !Dungeon.level.solid[cell]);
            return cells;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TARGET_CELL, this.targetCell);
            bundle.put(DISTANCE, this.distance);
            bundle.put(FULL_VORTEX, this.fullVortex);
            bundle.put(ALT_DIR, this.altDirection);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.targetCell = bundle.getInt(TARGET_CELL);
            this.distance = bundle.getInt(DISTANCE);
            this.fullVortex = bundle.getBoolean(FULL_VORTEX);
            this.altDirection = bundle.getBoolean(ALT_DIR);
        }
    }

    public static class ShockResist
    extends Buff {
        public ShockResist() {
            this.actPriority = -31;
        }

        @Override
        public boolean act() {
            if (this.target.buff(Paralysis.class) != null) {
                this.spend(this.target.cooldown());
                return true;
            }
            this.detach();
            return true;
        }
    }

    public static class LightningChase
    extends Buff {
        private static float lastSFXTime = -1.0f;
        float direction = -1.0f;
        int curCell = -1;
        int midCell = -1;
        int endCell = -1;
        ArrayList<Emitter> emitters = new ArrayList();
        private static String DIRECTION = "direction";
        private static String CUR_CELL = "cur_cell";
        private static String MID_CELL = "mid_cell";
        private static String END_CELL = "end_cell";

        /*
         * Enabled aggressive block sorting
         */
        @Override
        public boolean act() {
            PointF curPos;
            block11: {
                block13: {
                    block12: {
                        block10: {
                            if (this.direction != -1.0f) break block10;
                            curPos = new PointF(Dungeon.level.cellToPoint(this.curCell));
                            curPos.x += 0.5f;
                            curPos.y += 0.5f;
                            this.direction = PointF.angle(curPos, new PointF(Dungeon.level.cellToPoint(Dungeon.hero.pos)));
                            int curCell = Dungeon.level.pointToCell(curPos.floor());
                            CellEmitter.get(curCell).burst(SparkParticle.FACTORY, 10);
                            break block11;
                        }
                        CellEmitter.get(this.curCell).burst(SparkParticle.FACTORY, 10);
                        Char ch = Actor.findChar(this.curCell);
                        if (ch != null && !(ch instanceof VaultBossElemental) && ch.buff(ShockResist.class) == null) {
                            this.shockChar(ch);
                        }
                        if (!Dungeon.level.insideMap(this.midCell) || Dungeon.level.solid[this.midCell]) break block12;
                        CellEmitter.get(this.midCell).burst(SparkParticle.FACTORY, 10);
                        ch = Actor.findChar(this.midCell);
                        if (ch != null && !(ch instanceof VaultBossElemental) && ch.buff(ShockResist.class) == null) {
                            this.shockChar(ch);
                        }
                        if (Dungeon.level.insideMap(this.endCell) && !Dungeon.level.solid[this.endCell]) {
                            CellEmitter.get(this.endCell).burst(SparkParticle.FACTORY, 10);
                            ch = Actor.findChar(this.endCell);
                            if (ch != null && !(ch instanceof VaultBossElemental) && ch.buff(ShockResist.class) == null) {
                                this.shockChar(ch);
                            }
                            break block13;
                        } else {
                            this.detach();
                            return true;
                        }
                    }
                    this.detach();
                    return true;
                }
                curPos = new PointF(Dungeon.level.cellToPoint(this.endCell));
                curPos.x += 0.5f;
                curPos.y += 0.5f;
                float targetAngle = PointF.angle(curPos, new PointF(Dungeon.level.cellToPoint(Dungeon.hero.pos)));
                if (Math.abs(this.direction - targetAngle) > 3.1415925f) {
                    targetAngle = this.direction > targetAngle ? (targetAngle += 6.283185f) : (targetAngle -= 6.283185f);
                }
                float maxMove = Random.Float(0.39269906f, 0.52359873f);
                this.direction = this.direction > targetAngle ? (this.direction -= Math.min(this.direction - targetAngle, maxMove)) : (this.direction += Math.min(targetAngle - this.direction, maxMove));
                if (this.direction > 3.1415925f) {
                    this.direction -= 6.283185f;
                } else if (this.direction < -3.1415925f) {
                    this.direction += 6.283185f;
                }
            }
            PointF endPos = new PointF(curPos);
            endPos.offset(new PointF(curPos).polar(this.direction, 2.0f));
            endPos.x = (float)Math.round(2.0f * endPos.x) / 2.0f;
            endPos.y = (float)Math.round(2.0f * endPos.y) / 2.0f;
            PointF midPos = PointF.inter(curPos, endPos, 0.5f);
            this.endCell = Dungeon.level.pointToCell(endPos.floor());
            this.midCell = Dungeon.level.pointToCell(midPos.floor());
            this.curCell = Dungeon.level.pointToCell(curPos.floor());
            if (Actor.now() > lastSFXTime) {
                Sample.INSTANCE.play("sounds/lightning.mp3", 0.5f);
                lastSFXTime = Actor.now();
            }
            this.updateFX();
            this.spend(1.0f);
            return true;
        }

        private void shockChar(Char ch) {
            ch.damage(Random.NormalIntRange(10, 15), new Electricity());
            Buff.prolong(ch, Paralysis.class, 1.0f);
            Buff.affect(ch, ShockResist.class);
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
            if (ch == Dungeon.hero) {
                Sample.INSTANCE.play("sounds/lightning.mp3");
                PixelScene.shake(2.0f, 0.3f);
                Statistics.questScores[3] = Statistics.questScores[3] - 100;
                if (!ch.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this.target);
                }
            }
        }

        private void updateFX() {
            Emitter e;
            for (Emitter e2 : this.emitters) {
                e2.on = false;
            }
            this.emitters.clear();
            if (this.endCell != -1 && Dungeon.level.insideMap(this.endCell) && !Dungeon.level.solid[this.endCell]) {
                e = CellEmitter.get(this.endCell);
                e.pour(SparkParticle.STATIC, 0.1f);
                this.emitters.add(e);
            }
            if (this.midCell != -1 && Dungeon.level.insideMap(this.midCell) && !Dungeon.level.solid[this.midCell]) {
                e = CellEmitter.get(this.midCell);
                e.pour(SparkParticle.STATIC, 0.1f);
                this.emitters.add(e);
            }
            if (this.curCell != -1 && Dungeon.level.insideMap(this.curCell) && !Dungeon.level.solid[this.curCell]) {
                e = CellEmitter.get(this.curCell);
                e.pour(SparkParticle.STATIC, 0.1f);
                this.emitters.add(e);
            }
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.updateFX();
            } else {
                for (Emitter e : this.emitters) {
                    e.on = false;
                }
                this.emitters.clear();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DIRECTION, this.direction);
            bundle.put(CUR_CELL, this.curCell);
            bundle.put(MID_CELL, this.midCell);
            bundle.put(END_CELL, this.endCell);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.direction = bundle.getInt(DIRECTION);
            this.curCell = bundle.getInt(CUR_CELL);
            this.midCell = bundle.getInt(MID_CELL);
            this.endCell = bundle.getInt(END_CELL);
            lastSFXTime = -1.0f;
        }
    }

    public static class FrostResist
    extends Buff {
        public FrostResist() {
            this.actPriority = -31;
        }

        @Override
        public boolean act() {
            if (this.target.buff(Frost.class) != null) {
                this.spend(this.target.cooldown());
                return true;
            }
            this.detach();
            return true;
        }
    }
}

