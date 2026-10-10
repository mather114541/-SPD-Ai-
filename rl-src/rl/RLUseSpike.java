package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;

/** 验证 headless 下用药 API：execute(hero, DRINK/READ) 是否生效、是否消耗回合。 */
public class RLUseSpike implements ApplicationListener {
    public static void main(String[] args) { new HeadlessApplication(new RLUseSpike()); }
    @Override public void create() {
        try {
            RLController env = new RLController();
            env.reset(999L);
            Hero h = Dungeon.hero;

            // 治疗药水：先手动压低血量再喝
            PotionOfHealing ph = new PotionOfHealing(); ph.identify();
            ph.collect(h.belongings.backpack);
            h.HP = 8;
            System.out.println("before drink HP=" + h.HP + " ready=" + h.ready);
            ph.execute(h, Potion.AC_DRINK);
            System.out.println("after execute HP=" + h.HP + " ready=" + h.ready);

            // 力量药水
            PotionOfStrength ps = new PotionOfStrength(); ps.identify();
            ps.collect(h.belongings.backpack);
            System.out.println("STR before=" + h.STR);
            ps.execute(h, Potion.AC_DRINK);
            System.out.println("STR after=" + h.STR + " ready=" + h.ready);

            // 升级卷轴：程序化升级当前武器（绕开 UI 选择）
            ScrollOfUpgrade su = new ScrollOfUpgrade(); su.identify();
            su.collect(h.belongings.backpack);
            int wlvl0 = h.belongings.weapon.level();
            System.out.println("weapon lvl before=" + wlvl0);
            su.upgradeItem(h.belongings.weapon);
            su.detach(h.belongings.backpack);
            System.out.println("weapon lvl after=" + h.belongings.weapon.level() + " ready=" + h.ready + " suDetached=" + (!h.belongings.backpack.contains(su)));
        } catch (Throwable t) { t.printStackTrace(); }
        com.badlogic.gdx.Gdx.app.exit();
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
