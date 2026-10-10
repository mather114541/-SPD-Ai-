/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class DriedRose
extends Artifact {
    private boolean talkedTo;
    private boolean firstSummon;
    private GhostHero ghost;
    private int ghostID;
    private MeleeWeapon weapon;
    private Armor armor;
    public int droppedPetals;
    public static final String AC_SUMMON = "SUMMON";
    public static final String AC_DIRECT = "DIRECT";
    public static final String AC_OUTFIT = "OUTFIT";
    private static final String TALKEDTO = "talkedto";
    private static final String FIRSTSUMMON = "firstsummon";
    private static final String GHOSTID = "ghostID";
    private static final String PETALS = "petals";
    private static final String WEAPON = "weapon";
    private static final String ARMOR = "armor";
    public CellSelector.Listener ghostDirector;

    public DriedRose() {
        this.image = ItemSpriteSheet.ARTIFACT_ROSE1;
        this.levelCap = 10;
        this.charge = 100;
        this.chargeCap = 100;
        this.defaultAction = AC_SUMMON;
        this.talkedTo = false;
        this.firstSummon = false;
        this.ghost = null;
        this.ghostID = 0;
        this.weapon = null;
        this.armor = null;
        this.droppedPetals = 0;
        this.ghostDirector = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                if (cell == null) {
                    return;
                }
                Sample.INSTANCE.play("sounds/ghost.mp3");
                DriedRose.this.ghost.directTocell(cell);
            }

            @Override
            public String prompt() {
                return "\"" + Messages.get(GhostHero.class, "direct_prompt", new Object[0]) + "\"";
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!Ghost.Quest.completed()) {
            return actions;
        }
        if (this.isEquipped(hero) && this.charge == this.chargeCap && !this.cursed && hero.buff(MagicImmune.class) == null && this.ghostID == 0) {
            actions.add(AC_SUMMON);
        }
        if (this.ghostID != 0) {
            actions.add(AC_DIRECT);
        }
        if (this.isIdentified() && !this.cursed && !(Dungeon.level instanceof VaultLevel)) {
            actions.add(AC_OUTFIT);
        }
        return actions;
    }

    @Override
    public String defaultAction() {
        if (this.ghost != null) {
            return AC_DIRECT;
        }
        return AC_SUMMON;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SUMMON)) {
            if (hero.buff(MagicImmune.class) != null) {
                return;
            }
            if (!Ghost.Quest.completed()) {
                GameScene.show(new WndUseItem(null, this));
            } else if (this.ghost != null) {
                GLog.i(Messages.get(this, "spawned", new Object[0]), new Object[0]);
            } else if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.charge != this.chargeCap) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.i(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && !Dungeon.level.avoid[p]) continue;
                    spawnPoints.add(p);
                }
                if (spawnPoints.size() > 0) {
                    this.ghost = new GhostHero(this);
                    this.ghostID = this.ghost.id();
                    this.ghost.pos = (Integer)Random.element(spawnPoints);
                    GameScene.add(this.ghost, 1.0f);
                    Dungeon.level.occupyCell(this.ghost);
                    CellEmitter.get(this.ghost.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
                    CellEmitter.get(this.ghost.pos).start(Speck.factory(2), 0.2f, 3);
                    hero.spend(1.0f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    if (!this.firstSummon) {
                        this.ghost.yell(Messages.get(GhostHero.class, "hello", Messages.titleCase(Dungeon.hero.name())));
                        Sample.INSTANCE.play("sounds/ghost.mp3");
                        this.firstSummon = true;
                    } else if (BossHealthBar.isAssigned()) {
                        this.ghost.sayBoss();
                    } else {
                        this.ghost.sayAppeared();
                    }
                    Invisibility.dispel(hero);
                    Talent.onArtifactUsed(hero);
                    this.charge = 0;
                    this.partialCharge = 0.0f;
                    DriedRose.updateQuickslot();
                } else {
                    GLog.i(Messages.get(this, "no_space", new Object[0]), new Object[0]);
                }
            }
        } else if (action.equals(AC_DIRECT)) {
            if (this.ghost == null && this.ghostID != 0) {
                this.findGhost();
            }
            if (this.ghost != null && this.ghost != Stasis.getStasisAlly()) {
                GameScene.selectCell(this.ghostDirector);
            }
        } else if (action.equals(AC_OUTFIT)) {
            GameScene.show(new WndGhostHero(this));
        }
    }

    private void findGhost() {
        Actor a = Actor.findById(this.ghostID);
        if (a != null) {
            this.ghost = (GhostHero)a;
        } else if (Stasis.getStasisAlly() instanceof GhostHero) {
            this.ghost = (GhostHero)Stasis.getStasisAlly();
            this.ghostID = this.ghost.id();
        } else {
            this.ghostID = 0;
        }
    }

    public int ghostStrength() {
        return 13 + this.level() / 2;
    }

    @Override
    public String desc() {
        if (!Ghost.Quest.completed() && (ShatteredPixelDungeon.scene() instanceof GameScene || ShatteredPixelDungeon.scene() instanceof AlchemyScene)) {
            return Messages.get(this, "desc_no_quest", new Object[0]);
        }
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            if (!this.cursed) {
                if (this.level() < this.levelCap) {
                    desc = (String)desc + "\n\n" + Messages.get(this, "desc_hint", new Object[0]);
                }
            } else {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]);
            }
        }
        if (this.weapon != null || this.armor != null) {
            desc = (String)desc + "\n";
            if (this.weapon != null) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_weapon", Messages.titleCase(this.weapon.title()));
            }
            if (this.armor != null) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_armor", Messages.titleCase(this.armor.title()));
            }
            desc = (String)desc + "\n" + Messages.get(this, "desc_strength", this.ghostStrength());
        }
        return desc;
    }

    @Override
    public int value() {
        if (this.weapon != null) {
            return -1;
        }
        if (this.armor != null) {
            return -1;
        }
        return super.value();
    }

    @Override
    public String status() {
        if (this.ghost == null && this.ghostID != 0) {
            try {
                this.findGhost();
            }
            catch (ClassCastException e) {
                ShatteredPixelDungeon.reportException(e);
                this.ghostID = 0;
            }
        }
        if (this.ghost == null) {
            return super.status();
        }
        return this.ghost.HP * 100 / this.ghost.HT + "%";
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new roseRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.ghost == null) {
            if (this.charge < this.chargeCap) {
                this.partialCharge += 4.0f * amount;
                while (this.partialCharge >= 1.0f) {
                    ++this.charge;
                    this.partialCharge -= 1.0f;
                }
                if (this.charge >= this.chargeCap) {
                    this.charge = this.chargeCap;
                    this.partialCharge = 0.0f;
                    GLog.p(Messages.get(DriedRose.class, "charged", new Object[0]), new Object[0]);
                }
                DriedRose.updateQuickslot();
            }
        } else if (this.ghost.HP < this.ghost.HT) {
            int heal = Math.round((1.0f + (float)this.level() / 3.0f) * amount);
            this.ghost.HP = Math.min(this.ghost.HT, this.ghost.HP + heal);
            if (this.ghost.sprite != null) {
                this.ghost.sprite.showStatusWithIcon(65280, Integer.toString(heal), FloatingText.HEALING, new Object[0]);
            }
            DriedRose.updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        if (this.level() >= 9) {
            this.image = ItemSpriteSheet.ARTIFACT_ROSE3;
        } else if (this.level() >= 4) {
            this.image = ItemSpriteSheet.ARTIFACT_ROSE2;
        }
        this.droppedPetals = Math.max(this.level(), this.droppedPetals);
        if (this.ghost != null) {
            this.ghost.updateRose();
            this.ghost.HP = Math.min(this.ghost.HP + 8, this.ghost.HT);
        }
        return super.upgrade();
    }

    public Weapon ghostWeapon() {
        return this.weapon;
    }

    public Armor ghostArmor() {
        return this.armor;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TALKEDTO, this.talkedTo);
        bundle.put(FIRSTSUMMON, this.firstSummon);
        bundle.put(GHOSTID, this.ghostID);
        bundle.put(PETALS, this.droppedPetals);
        if (this.weapon != null) {
            bundle.put(WEAPON, this.weapon);
        }
        if (this.armor != null) {
            bundle.put(ARMOR, this.armor);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.talkedTo = bundle.getBoolean(TALKEDTO);
        this.firstSummon = bundle.getBoolean(FIRSTSUMMON);
        this.ghostID = bundle.getInt(GHOSTID);
        this.droppedPetals = bundle.getInt(PETALS);
        if (bundle.contains(WEAPON)) {
            this.weapon = (MeleeWeapon)bundle.get(WEAPON);
        }
        if (bundle.contains(ARMOR)) {
            this.armor = (Armor)bundle.get(ARMOR);
        }
    }

    public static class GhostHero
    extends DirectableAlly {
        private DriedRose rose;

        public GhostHero() {
            this.spriteClass = GhostSprite.class;
            this.flying = true;
            this.state = this.HUNTING;
            this.properties.add(Char.Property.UNDEAD);
            this.properties.add(Char.Property.INORGANIC);
            this.rose = null;
            this.immunities.add(CorrosiveGas.class);
            this.immunities.add(Burning.class);
            this.immunities.add(ScrollOfRetribution.class);
            this.immunities.add(ScrollOfPsionicBlast.class);
            this.immunities.add(AllyBuff.class);
        }

        public GhostHero(DriedRose rose) {
            this.spriteClass = GhostSprite.class;
            this.flying = true;
            this.state = this.HUNTING;
            this.properties.add(Char.Property.UNDEAD);
            this.properties.add(Char.Property.INORGANIC);
            this.rose = null;
            this.immunities.add(CorrosiveGas.class);
            this.immunities.add(Burning.class);
            this.immunities.add(ScrollOfRetribution.class);
            this.immunities.add(ScrollOfPsionicBlast.class);
            this.immunities.add(AllyBuff.class);
            this.rose = rose;
            this.updateRose();
            this.HP = this.HT;
        }

        @Override
        public void defendPos(int cell) {
            this.yell(Messages.get(this, "directed_position_" + Random.IntRange(1, 5), new Object[0]));
            super.defendPos(cell);
        }

        @Override
        public void followHero() {
            this.yell(Messages.get(this, "directed_follow_" + Random.IntRange(1, 5), new Object[0]));
            super.followHero();
        }

        @Override
        public void targetChar(Char ch) {
            this.yell(Messages.get(this, "directed_attack_" + Random.IntRange(1, 5), new Object[0]));
            super.targetChar(ch);
        }

        private void updateRose() {
            if (this.rose == null) {
                this.rose = Dungeon.hero.belongings.getItem(DriedRose.class);
                if (this.rose != null) {
                    this.rose.ghost = this;
                    this.rose.ghostID = this.id();
                }
            }
            this.defenseSkill = Dungeon.hero.lvl + 4;
            if (this.rose == null) {
                return;
            }
            this.HT = 40 + 10 * this.rose.level();
        }

        public Weapon weapon() {
            if (this.rose != null) {
                return this.rose.weapon;
            }
            return null;
        }

        public void clearWeapon() {
            if (this.rose != null) {
                this.rose.weapon = null;
            }
        }

        public Armor armor() {
            if (this.rose != null) {
                return this.rose.armor;
            }
            return null;
        }

        @Override
        protected boolean act() {
            this.updateRose();
            if (this.rose == null || !this.rose.isEquipped(Dungeon.hero) || Dungeon.hero.buff(MagicImmune.class) != null) {
                this.damage(1, new NoRoseDamage());
            }
            if (!this.isAlive()) {
                return true;
            }
            return super.act();
        }

        @Override
        public int attackSkill(Char target) {
            int acc = Dungeon.hero.lvl + 9;
            if (this.weapon() != null) {
                acc = (int)((float)acc * this.weapon().accuracyFactor(this, target));
            }
            return acc;
        }

        @Override
        public float attackDelay() {
            float delay = super.attackDelay();
            if (this.weapon() != null) {
                delay *= this.weapon().delayFactor(this);
            }
            return delay;
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return super.canAttack(enemy) || this.weapon() != null && this.weapon().canReach(this, enemy.pos);
        }

        @Override
        public int damageRoll() {
            int dmg = 0;
            if (this.weapon() != null) {
                int excessStr;
                dmg += this.weapon().damageRoll(this);
                if (this.rose != null && (excessStr = this.rose.ghostStrength() - this.weapon().STRReq()) > 0) {
                    dmg += Random.NormalIntRange(0, excessStr);
                }
            } else if (this.rose != null) {
                dmg += Random.NormalIntRange(1, this.rose.ghostStrength() - 8);
            }
            return dmg;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);
            if (this.weapon() != null) {
                damage = this.weapon().proc(this, enemy, damage);
                if (!enemy.isAlive() && enemy == Dungeon.hero) {
                    Dungeon.fail(this);
                    GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
                }
            }
            return damage;
        }

        @Override
        public int defenseProc(Char enemy, int damage) {
            if (this.armor() != null) {
                damage = this.armor().proc(enemy, this, damage);
            }
            return super.defenseProc(enemy, damage);
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            Item.updateQuickslot();
        }

        @Override
        public float speed() {
            float speed = super.speed();
            if (this.state == this.WANDERING && this.defendingPos == -1 && Dungeon.level.distance(this.pos, Dungeon.hero.pos) > 1) {
                speed *= 2.0f;
            }
            return speed;
        }

        @Override
        public int defenseSkill(Char enemy) {
            int defense = super.defenseSkill(enemy);
            if (defense != 0 && this.armor() != null) {
                defense = Math.round(this.armor().evasionFactor(this, defense));
            }
            return defense;
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            if (this.armor() != null) {
                dr += Random.NormalIntRange(this.armor().DRMin(), this.armor().DRMax());
            }
            if (this.weapon() != null) {
                dr += Random.NormalIntRange(0, this.weapon().defenseFactor(this));
            }
            return dr;
        }

        @Override
        public int glyphLevel(Class<? extends Armor.Glyph> cls) {
            if (this.armor() != null && this.armor().hasGlyph(cls, this)) {
                return Math.max(super.glyphLevel(cls), this.armor().buffedLvl());
            }
            return super.glyphLevel(cls);
        }

        @Override
        public boolean interact(Char c) {
            this.updateRose();
            if (c == Dungeon.hero && this.rose != null && !this.rose.talkedTo) {
                this.rose.talkedTo = true;
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndQuest(this, Messages.get(this, "introduce", new Object[0])));
                    }
                });
                return true;
            }
            return super.interact(c);
        }

        @Override
        public void die(Object cause) {
            this.sayDefeated();
            super.die(cause);
        }

        @Override
        public void destroy() {
            this.updateRose();
            if (this.rose != null) {
                this.rose.ghost = null;
                this.rose.charge = 0;
                this.rose.partialCharge = 0.0f;
                this.rose.ghostID = -1;
            }
            super.destroy();
        }

        public void sayAppeared() {
            if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
                this.yell(Messages.get(this, "dialogue_ascension_" + Random.IntRange(1, 6), new Object[0]));
            } else {
                int depth = (Dungeon.depth - 1) / 5;
                int variant = Dungeon.depth % 5 == 1 ? Random.IntRange(1, 3) : Random.IntRange(1, 6);
                switch (depth) {
                    case 0: {
                        this.yell(Messages.get(this, "dialogue_sewers_" + variant, new Object[0]));
                        break;
                    }
                    case 1: {
                        this.yell(Messages.get(this, "dialogue_prison_" + variant, new Object[0]));
                        break;
                    }
                    case 2: {
                        this.yell(Messages.get(this, "dialogue_caves_" + variant, new Object[0]));
                        break;
                    }
                    case 3: {
                        this.yell(Messages.get(this, "dialogue_city_" + variant, new Object[0]));
                        break;
                    }
                    default: {
                        this.yell(Messages.get(this, "dialogue_halls_" + variant, new Object[0]));
                    }
                }
            }
            if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                Sample.INSTANCE.play("sounds/ghost.mp3");
            }
        }

        public void sayBoss() {
            int depth = (Dungeon.depth - 1) / 5;
            switch (depth) {
                case 0: {
                    this.yell(Messages.get(this, "seen_goo_" + Random.IntRange(1, 3), new Object[0]));
                    break;
                }
                case 1: {
                    this.yell(Messages.get(this, "seen_tengu_" + Random.IntRange(1, 3), new Object[0]));
                    break;
                }
                case 2: {
                    this.yell(Messages.get(this, "seen_dm300_" + Random.IntRange(1, 3), new Object[0]));
                    break;
                }
                case 3: {
                    this.yell(Messages.get(this, "seen_king_" + Random.IntRange(1, 3), new Object[0]));
                    break;
                }
                default: {
                    this.yell(Messages.get(this, "seen_yog_" + Random.IntRange(1, 3), new Object[0]));
                }
            }
            Sample.INSTANCE.play("sounds/ghost.mp3");
        }

        public void sayDefeated() {
            if (BossHealthBar.isAssigned()) {
                this.yell(Messages.get(this, "defeated_by_boss_" + Random.IntRange(1, 3), new Object[0]));
            } else {
                this.yell(Messages.get(this, "defeated_by_enemy_" + Random.IntRange(1, 3), new Object[0]));
            }
            Sample.INSTANCE.play("sounds/ghost.mp3");
        }

        public void sayHeroKilled() {
            this.yell(Messages.get(this, "player_killed_" + Random.IntRange(1, 3), new Object[0]));
            GLog.newLine();
            Sample.INSTANCE.play("sounds/ghost.mp3");
        }

        public void sayAnhk() {
            this.yell(Messages.get(this, "blessed_ankh_" + Random.IntRange(1, 3), new Object[0]));
            Sample.INSTANCE.play("sounds/ghost.mp3");
        }

        public static class NoRoseDamage {
        }
    }

    private static class WndGhostHero
    extends Window {
        private static final int BTN_SIZE = 32;
        private static final float GAP = 2.0f;
        private static final float BTN_GAP = 12.0f;
        private static final int WIDTH = 116;
        private ItemButton btnWeapon;
        private ItemButton btnArmor;

        WndGhostHero(final DriedRose rose) {
            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(rose));
            titlebar.label(Messages.get(this, "title", new Object[0]));
            titlebar.setRect(0.0f, 0.0f, 116.0f, 0.0f);
            this.add(titlebar);
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "desc", rose.ghostStrength()), 6);
            message.maxWidth(116);
            message.setPos(0.0f, titlebar.bottom() + 2.0f);
            this.add(message);
            this.btnWeapon = new ItemButton(){

                @Override
                protected void onClick() {
                    if (rose.weapon != null) {
                        this.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
                        if (!rose.weapon.doPickUp(Dungeon.hero)) {
                            Dungeon.level.drop(rose.weapon, Dungeon.hero.pos);
                        }
                        rose.weapon = null;
                    } else {
                        GameScene.selectItem(new WndBag.ItemSelector(){

                            @Override
                            public String textPrompt() {
                                return Messages.get(WndGhostHero.class, "weapon_prompt", new Object[0]);
                            }

                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Belongings.Backpack.class;
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof MeleeWeapon;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (item instanceof MeleeWeapon) {
                                    if (item.unique) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_unique", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (item.cursed || !item.cursedKnown) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_cursed", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (!item.levelKnown && ((MeleeWeapon)item).STRReq(0) > rose.ghostStrength()) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_strength_unknown", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (((MeleeWeapon)item).STRReq() > rose.ghostStrength()) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_strength", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else {
                                        if (item.isEquipped(Dungeon.hero)) {
                                            ((MeleeWeapon)item).doUnequip(Dungeon.hero, false, false);
                                        } else {
                                            item.detach(Dungeon.hero.belongings.backpack);
                                        }
                                        rose.weapon = (MeleeWeapon)item;
                                        this.item(rose.weapon);
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                protected boolean onLongClick() {
                    if (this.item() != null && this.item().name() != null) {
                        GameScene.show(new WndInfoItem(this.item()));
                        return true;
                    }
                    return false;
                }
            };
            this.btnWeapon.setRect(20.0f, message.top() + message.height() + 2.0f, 32.0f, 32.0f);
            if (rose.weapon != null) {
                this.btnWeapon.item(rose.weapon);
            } else {
                this.btnWeapon.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
            }
            this.add(this.btnWeapon);
            this.btnArmor = new ItemButton(){

                @Override
                protected void onClick() {
                    if (rose.armor != null) {
                        this.item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
                        if (!rose.armor.doPickUp(Dungeon.hero)) {
                            Dungeon.level.drop(rose.armor, Dungeon.hero.pos);
                        }
                        rose.armor = null;
                    } else {
                        GameScene.selectItem(new WndBag.ItemSelector(){

                            @Override
                            public String textPrompt() {
                                return Messages.get(WndGhostHero.class, "armor_prompt", new Object[0]);
                            }

                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Belongings.Backpack.class;
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Armor;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (item instanceof Armor) {
                                    if (item.unique || ((Armor)item).checkSeal() != null) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_unique", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (item.cursed || !item.cursedKnown) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_cursed", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (!item.levelKnown && ((Armor)item).STRReq(0) > rose.ghostStrength()) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_strength_unknown", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else if (((Armor)item).STRReq() > rose.ghostStrength()) {
                                        GLog.w(Messages.get(WndGhostHero.class, "cant_strength", new Object[0]), new Object[0]);
                                        this.hide();
                                    } else {
                                        if (item.isEquipped(Dungeon.hero)) {
                                            ((Armor)item).doUnequip(Dungeon.hero, false, false);
                                        } else {
                                            item.detach(Dungeon.hero.belongings.backpack);
                                        }
                                        rose.armor = (Armor)item;
                                        this.item(rose.armor);
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                protected boolean onLongClick() {
                    if (this.item() != null && this.item().name() != null) {
                        GameScene.show(new WndInfoItem(this.item()));
                        return true;
                    }
                    return false;
                }
            };
            this.btnArmor.setRect(this.btnWeapon.right() + 12.0f, this.btnWeapon.top(), 32.0f, 32.0f);
            if (rose.armor != null) {
                this.btnArmor.item(rose.armor);
            } else {
                this.btnArmor.item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
            }
            this.add(this.btnArmor);
            this.resize(116, (int)(this.btnArmor.bottom() + 2.0f));
        }
    }

    public class roseRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            this.spend(1.0f);
            if (DriedRose.this.ghost == null && DriedRose.this.ghostID != 0) {
                DriedRose.this.findGhost();
            }
            if (DriedRose.this.ghost != null && !DriedRose.this.ghost.isAlive()) {
                DriedRose.this.ghost = null;
            }
            if (DriedRose.this.ghost != null && !DriedRose.this.cursed && this.target.buff(MagicImmune.class) == null) {
                if (DriedRose.this.ghost.HP < DriedRose.this.ghost.HT && Regeneration.regenOn()) {
                    DriedRose.this.partialCharge += (float)DriedRose.this.ghost.HT / 500.0f * RingOfEnergy.artifactChargeMultiplier(this.target);
                    Item.updateQuickslot();
                    while (DriedRose.this.partialCharge > 1.0f) {
                        ++DriedRose.this.ghost.HP;
                        DriedRose.this.partialCharge -= 1.0f;
                        if (DriedRose.this.ghost.HP != DriedRose.this.ghost.HT) continue;
                        DriedRose.this.partialCharge = 0.0f;
                    }
                } else {
                    DriedRose.this.partialCharge = 0.0f;
                }
                return true;
            }
            if (DriedRose.this.charge < DriedRose.this.chargeCap && !DriedRose.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                DriedRose.this.partialCharge += 0.2f * RingOfEnergy.artifactChargeMultiplier(this.target);
                while (DriedRose.this.partialCharge > 1.0f) {
                    ++DriedRose.this.charge;
                    DriedRose.this.partialCharge -= 1.0f;
                    if (DriedRose.this.charge != DriedRose.this.chargeCap) continue;
                    DriedRose.this.partialCharge = 0.0f;
                    GLog.p(Messages.get(DriedRose.class, "charged", new Object[0]), new Object[0]);
                }
            } else if (DriedRose.this.cursed && Random.Int(100) == 0) {
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                    int p = this.target.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && !Dungeon.level.avoid[p]) continue;
                    spawnPoints.add(p);
                }
                if (spawnPoints.size() > 0) {
                    Wraith.spawnAt((Integer)Random.element(spawnPoints), Wraith.class);
                    Sample.INSTANCE.play("sounds/cursed.mp3");
                }
            }
            Item.updateQuickslot();
            return true;
        }
    }

    public static class Petal
    extends Item {
        public Petal() {
            this.stackable = true;
            this.dropsDownHeap = true;
            this.image = ItemSpriteSheet.PETAL;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            Catalog.setSeen(this.getClass());
            Statistics.itemTypesDiscovered.add(this.getClass());
            DriedRose rose = hero.belongings.getItem(DriedRose.class);
            if (rose == null) {
                GLog.w(Messages.get(this, "no_rose", new Object[0]), new Object[0]);
                return false;
            }
            if (rose.level() >= rose.levelCap) {
                GLog.i(Messages.get(this, "no_room", new Object[0]), new Object[0]);
                hero.spendAndNext(this.pickupDelay());
                return true;
            }
            rose.upgrade();
            Catalog.countUse(rose.getClass());
            if (rose.level() == rose.levelCap) {
                GLog.p(Messages.get(this, "maxlevel", new Object[0]), new Object[0]);
            } else {
                GLog.i(Messages.get(this, "levelup", new Object[0]), new Object[0]);
            }
            Sample.INSTANCE.play("sounds/dewdrop.mp3");
            GameScene.pickUp(this, pos);
            hero.spendAndNext(this.pickupDelay());
            return true;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }
    }
}

