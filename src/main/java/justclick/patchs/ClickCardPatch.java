package justclick.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import justclick.panels.ConfigPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "clickAndDragCards"
)
public class ClickCardPatch {


    public static final Logger logger = LogManager.getLogger(ClickCardPatch.class.getName());

    @SpireInsertPatch(
            rlocs = {28, 15}
    )
    public static void Insert(AbstractPlayer __instance) {
        // 支持原生数字键热键：从手牌按组内顺序选第 N 张卡
        AbstractCard hotkeyCard = null;
        try {
            hotkeyCard = InputHelper.getCardSelectedByHotkey(__instance.hand);
        } catch (Throwable ignored) { }

        boolean clickTriggered = (InputHelper.justClickedLeft || CInputHelper.isJustPressed(0));
        boolean hotkeyTriggered = (hotkeyCard != null);

        if (((clickTriggered && __instance.hoveredCard != null) || hotkeyTriggered) && !AbstractDungeon.isScreenUp) {
            try {

                AbstractCard clickedCard = hotkeyTriggered ? hotkeyCard : __instance.hoveredCard;

                // 热键只改变选中卡，是否出牌由下方逻辑决定
                if (hotkeyTriggered) {
                    __instance.hoveredCard = clickedCard;
                }

                // 按作者建议：区分日志来源（热键/点击）
                if (hotkeyTriggered) {
                    logger.info("热键选择了卡牌: {}", clickedCard);
                } else if (clickTriggered) {
                    logger.info("点击了卡牌: {}", clickedCard);
                }

                Method playCardMethod = AbstractPlayer.class.getDeclaredMethod("playCard");
                playCardMethod.setAccessible(true);

                ArrayList<AbstractMonster> availableMonsters = new ArrayList<>();

                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.escaped && !m.isDead && !m.halfDead) {
                        availableMonsters.add(m);
                    }
                }

                logger.info("怪物: {}", availableMonsters);

                if (
                        ( clickedCard.target == AbstractCard.CardTarget.ENEMY && availableMonsters.size() == 1 && ConfigPanel.when_only_targeting_one) ||
                        ( clickedCard.target == AbstractCard.CardTarget.ALL_ENEMY && ConfigPanel.when_targeting_all) ||
                        ( clickedCard.target == AbstractCard.CardTarget.SELF && ConfigPanel.when_targeting_self) ||
                        ( clickedCard.target == AbstractCard.CardTarget.NONE && ConfigPanel.when_targeting_none) ||
                        ( clickedCard.target == AbstractCard.CardTarget.SELF_AND_ENEMY && ConfigPanel.when_targeting_self_and_enemy)
                ) {

                    AbstractMonster hoveredMonster = availableMonsters.get(0);

                    Field field = AbstractPlayer.class.getDeclaredField("hoveredMonster");
                    field.setAccessible(true);
                    field.set(__instance, hoveredMonster);

                    playCardMethod.invoke(__instance);
                } else if (hotkeyTriggered) {
                    // 热键仅选中但不满足自动出牌：保持选中，交由原生空格/拖拽/取消
                    return;
                }
            } catch (NoSuchMethodException e) {
                logger.error("找不到playCard方法", e);
            } catch (IllegalAccessException e) {
                logger.error("无法访问playCard方法", e);
            } catch (java.lang.reflect.InvocationTargetException e) {
                logger.error("调用playCard方法失败", e);
            } catch (NoSuchFieldException e) {
                logger.error("找不到hoveredMonster字段", e);
            }
        }
    }
}
