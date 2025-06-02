package justclick.panels;

import basemod.EasyConfigPanel;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConfigPanel extends EasyConfigPanel {

    public static boolean when_only_attacking_one = true;
    public static boolean when_attacking_all = true;
    public static boolean when_playing_skill = true;
    public static boolean when_playing_power = true;
    public static boolean when_playing_status = true;
    public static boolean when_playing_curse = true;

    public ConfigPanel() {
        super("justclick", getUIStrings(), "config");
    }

    private static UIStrings getUIStrings() {
        String langPackDir = "justclickResources" + File.separator + "localization" + File.separator + Settings.language.toString().toLowerCase();
        String uiPath = langPackDir + File.separator + "ui.json";
        Gson gson = new Gson();
        Type uiType = (new TypeToken<Map<String, UIStrings>>() {
        }).getType();
        Map<String, UIStrings> ui = gson.fromJson(loadJson(uiPath), uiType);
        return ui.get("justclick:Config");
    }

    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

}

