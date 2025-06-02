package justclick;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
        clz = AbstractCard.class,
        method = "update"
)
public class clickCardPatch {


    public static final Logger logger = LogManager.getLogger(clickCardPatch.class.getName());

    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance) {
        if (__instance.hb.clicked) {
            logger.info("点击了卡牌: {}", __instance.name);
        }
    }
}
