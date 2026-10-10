/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

public class RatKing
extends NPC {
    public RatKing() {
        this.spriteClass = RatKingSprite.class;
        this.state = this.SLEEPING;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public float speed() {
        return 2.0f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected void onAdd() {
        super.onAdd();
        if (this.firstAdded && Dungeon.depth != 5) {
            this.yell(Messages.get(this, "confused", new Object[0]));
        }
    }

    @Override
    public Notes.Landmark landmark() {
        return Dungeon.depth == 5 ? Notes.Landmark.RAT_KING : null;
    }

    @Override
    protected boolean act() {
        if (Dungeon.depth < 5) {
            if (this.pos == Dungeon.level.exit()) {
                this.destroy();
                this.sprite.killAndErase();
            } else {
                this.target = Dungeon.level.exit();
            }
        } else if (Dungeon.depth > 5) {
            if (this.pos == Dungeon.level.entrance()) {
                this.destroy();
                this.sprite.killAndErase();
            } else {
                this.target = Dungeon.level.entrance();
            }
        }
        return super.act();
    }

    @Override
    public boolean interact(Char c) {
        this.sprite.turnTo(this.pos, c.pos);
        if (c != Dungeon.hero) {
            return super.interact(c);
        }
        final KingsCrown crown = Dungeon.hero.belongings.getItem(KingsCrown.class);
        if (this.state == this.SLEEPING) {
            this.notice();
            this.yell(Messages.get(this, "not_sleeping", new Object[0]));
            this.state = this.WANDERING;
        } else if (crown != null) {
            if (Dungeon.hero.belongings.armor() == null) {
                this.yell(Messages.get(RatKing.class, "crown_clothes", new Object[0]));
            } else {
                Badges.validateRatmogrify();
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndOptions(RatKing.this.sprite(), Messages.titleCase(RatKing.this.name()), Messages.get(RatKing.class, "crown_desc", new Object[0]), new String[]{Messages.get(RatKing.class, "crown_yes", new Object[0]), Messages.get(RatKing.class, "crown_info", new Object[0]), Messages.get(RatKing.class, "crown_no", new Object[0])}){

                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    crown.upgradeArmor(Dungeon.hero, Dungeon.hero.belongings.armor(), new Ratmogrify());
                                    Statistics.qualifiedForRandomVictoryBadge = false;
                                    ((RatKingSprite)RatKing.this.sprite).resetAnims();
                                    RatKing.this.yell(Messages.get(RatKing.class, "crown_thankyou", new Object[0]));
                                } else if (index == 1) {
                                    GameScene.show(new WndInfoArmorAbility(Dungeon.hero.heroClass, new Ratmogrify()));
                                } else {
                                    RatKing.this.yell(Messages.get(RatKing.class, "crown_fine", new Object[0]));
                                }
                            }
                        });
                    }
                });
            }
        } else if (Dungeon.hero.armorAbility instanceof Ratmogrify) {
            this.yell(Messages.get(RatKing.class, "crown_after", new Object[0]));
        } else {
            this.yell(Messages.get(this, "what_is_it", new Object[0]));
        }
        return true;
    }

    @Override
    public String description() {
        if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify) {
            return Messages.get(this, "desc_crown", new Object[0]);
        }
        if (Holiday.getCurrentHoliday() == Holiday.APRIL_FOOLS) {
            return Messages.get(this, "desc_birthday", new Object[0]);
        }
        if (Holiday.getCurrentHoliday() == Holiday.WINTER_HOLIDAYS) {
            return Messages.get(this, "desc_winter", new Object[0]);
        }
        return super.description();
    }
}

