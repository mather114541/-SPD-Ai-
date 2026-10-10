/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Chasm
implements Hero.Doom {
    public static boolean jumpConfirmed = false;
    private static int heroPos;

    public static void heroJump(final Hero hero) {
        if (rl.RLMode.headless) {
            hero.resume();
            return;
        }
        heroPos = hero.pos;
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                GameScene.show(new WndOptions(new Image(Dungeon.level.tilesTex(), 176, 16, 16, 16), Messages.get(Chasm.class, "chasm", new Object[0]), Messages.get(Chasm.class, "jump", new Object[0]), new String[]{Messages.get(Chasm.class, "yes", new Object[0]), Messages.get(Chasm.class, "no", new Object[0])}){
                    private float elapsed;
                    {
                        this.elapsed = 0.0f;
                    }

                    @Override
                    public synchronized void update() {
                        super.update();
                        this.elapsed += Game.elapsed;
                    }

                    @Override
                    public void hide() {
                        if (this.elapsed > 0.2f) {
                            super.hide();
                        }
                    }

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0 && this.elapsed > 0.2f && Dungeon.hero.pos == heroPos) {
                            jumpConfirmed = true;
                            hero.resume();
                        }
                    }
                });
            }
        });
    }

    public static void heroFall(int pos) {
        jumpConfirmed = false;
        Sample.INSTANCE.play("sounds/falling.mp3");
        Level.beforeTransition();
        if (Dungeon.hero.isAlive()) {
            Dungeon.hero.interrupt();
            InterlevelScene.mode = InterlevelScene.Mode.FALL;
            if (Dungeon.level instanceof RegularLevel && ((RegularLevel)Dungeon.level).room(pos) instanceof WeakFloorRoom) {
                InterlevelScene.fallIntoPit = true;
                Notes.remove(Notes.Landmark.DISTANT_WELL);
            } else {
                InterlevelScene.fallIntoPit = false;
            }
            Game.switchScene(InterlevelScene.class);
        } else {
            Dungeon.hero.sprite.visible = false;
        }
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromFalling();
        Dungeon.fail(Chasm.class);
        GLog.n(Messages.get(Chasm.class, "ondeath", new Object[0]), new Object[0]);
    }

    public static void heroLand() {
        Hero hero = Dungeon.hero;
        ElixirOfFeatherFall.FeatherBuff b = hero.buff(ElixirOfFeatherFall.FeatherBuff.class);
        if (b != null) {
            hero.sprite.emitter().burst(Speck.factory(106), 20);
            b.processFall();
            return;
        }
        PixelScene.shake(4.0f, 1.0f);
        Dungeon.level.occupyCell(hero);
        Buff.prolong(hero, Cripple.class, 10.0f);
        Buff.affect(hero, Bleeding.class).set(Math.round((float)hero.HT / (6.0f + 6.0f * ((float)hero.HP / (float)hero.HT))), Chasm.class);
        hero.damage(Math.max(hero.HP / 2, Random.NormalIntRange(hero.HP / 2, hero.HT / 4)), new Chasm());
    }

    public static void mobFall(Mob mob) {
        if (mob.isAlive()) {
            Buff.prolong(mob, Trap.HazardAssistTracker.class, 50.0f);
            mob.die(Chasm.class);
        }
        if (mob.sprite != null) {
            ((MobSprite)mob.sprite).fall();
        }
    }

    public static class Falling
    extends Buff {
        public Falling() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            Chasm.heroLand();
            this.detach();
            return true;
        }
    }
}

