package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;

/** 验证 RL 用药动作（9/10/11）可学习生效：无物品失效、有物品成功。 */
public class RLPolicySpike implements ApplicationListener {
    public static void main(String[] args) { new HeadlessApplication(new RLPolicySpike()); }
    @Override public void create() {
        try {
            RLController env = new RLController();
            env.reset(7L);
            Hero h = Dungeon.hero;

            // 无物品时用药动作应失效（不消耗、STR/HP/武器不变）
            int str0 = h.STR;
            env.step(RLItemPolicy.A_DRINK_STRENGTH);
            System.out.println("no-item strength action -> STR=" + h.STR + " (expect " + str0 + " 未变)");

            // 力量药水动作：有已鉴定力量药水则 STR++
            PotionOfStrength ps = new PotionOfStrength(); ps.identify();
            ps.collect(h.belongings.backpack);
            env.step(RLItemPolicy.A_DRINK_STRENGTH);
            System.out.println("strength action -> STR=" + h.STR + " (expect " + (str0 + 1) + "), stillHas=" + h.belongings.backpack.contains(ps));

            // 治疗药水动作：压低血 + 已鉴定治疗药水则回血
            h.HP = 10;
            PotionOfHealing ph = new PotionOfHealing(); ph.identify();
            ph.collect(h.belongings.backpack);
            int hp0 = h.HP;
            env.step(RLItemPolicy.A_DRINK_HEAL);
            System.out.println("heal action -> HP " + hp0 + " -> " + h.HP + " (持续回血，应 >hp0), stillHas=" + h.belongings.backpack.contains(ph));

            // 升级卷轴动作：有已鉴定升级卷轴则武器等级++
            int w0 = h.belongings.weapon.level();
            ScrollOfUpgrade su = new ScrollOfUpgrade(); su.identify();
            su.collect(h.belongings.backpack);
            env.step(RLItemPolicy.A_UPGRADE_WEAPON);
            System.out.println("upgrade action -> weapon " + w0 + " -> " + h.belongings.weapon.level() + ", stillHas=" + h.belongings.backpack.contains(su));

            // 移动动作仍正常
            int pos0 = h.pos;
            env.step(4);
            System.out.println("move action -> pos " + pos0 + " -> " + h.pos);
        } catch (Throwable t) { t.printStackTrace(); }
        com.badlogic.gdx.Gdx.app.exit();
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
