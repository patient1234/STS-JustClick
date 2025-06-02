package justclick.subscribes;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import justclick.panels.ConfigPanel;

@SpireInitializer
public class PostInitializeSubscribe implements
        PostInitializeSubscriber
{
    public PostInitializeSubscribe() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new PostInitializeSubscribe();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = ImageMaster.loadImage("justclickResources/images/badge.png");
        BaseMod.registerModBadge(badgeTexture, "点击打牌", "河童", "点一下直接打出牌，不需要拖动。", new ConfigPanel());
    }
}
