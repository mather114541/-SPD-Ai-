/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfSirensSong
extends ExoticScroll {
    protected static boolean identifiedByUse = false;
    private CellSelector.Listener targeter;

    public ScrollOfSirensSong() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_SIREN;
        this.targeter = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                Char ch;
                if (cell == null && ScrollOfSirensSong.this.isKnown()) {
                    return;
                }
                Mob target = null;
                if (cell != null && (ch = Actor.findChar(cell)) != null && ch.alignment != Char.Alignment.ALLY && ch instanceof Mob) {
                    target = (Mob)ch;
                }
                if (target == null && !ScrollOfSirensSong.this.anonymous && !identifiedByUse) {
                    GLog.w(Messages.get(ScrollOfSirensSong.class, "cancel", new Object[0]), new Object[0]);
                    return;
                }
                curUser.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
                Sample.INSTANCE.play("sounds/charms.mp3");
                Sample.INSTANCE.playDelayed("sounds/lullaby.mp3", 0.1f);
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (!Dungeon.level.heroFOV[mob.pos] || mob == target || mob.alignment == Char.Alignment.ALLY) continue;
                    Buff.affect((Char)mob, Charm.class, (float)10.0f).object = curUser.id();
                    mob.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
                }
                if (target != null) {
                    if (!target.isImmune(Enthralled.class)) {
                        AllyBuff.affectAndLoot(target, curUser, Enthralled.class);
                    } else {
                        Buff.affect((Char)target, Charm.class, (float)10.0f).object = curUser.id();
                    }
                    target.sprite.centerEmitter().burst(Speck.factory(11), 10);
                } else {
                    GLog.w(Messages.get(ScrollOfSirensSong.class, "no_target", new Object[0]), new Object[0]);
                }
                if (!identifiedByUse) {
                    curItem.detach(curUser.belongings.backpack);
                }
                identifiedByUse = false;
                ScrollOfSirensSong.this.readAnimation();
            }

            @Override
            public String prompt() {
                return Messages.get(ScrollOfSirensSong.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public void doRead() {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(ScrollOfSirensSong.curUser.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.selectCell(this.targeter);
    }

    public static class Enthralled
    extends AllyBuff {
        public Enthralled() {
            this.type = Buff.buffType.NEGATIVE;
            this.announced = true;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.HEARTS);
            } else {
                this.target.sprite.remove(CharSprite.State.HEARTS);
            }
        }

        @Override
        public int icon() {
            return 21;
        }
    }
}

