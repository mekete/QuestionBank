package net.kerod.android.questionbank.utility;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import net.kerod.android.questionbank.manager.ApplicationManager;

/**
 * Created by makata on 12/20/2016.
 */

public class FontUtil {
    //FontCreator.createFromAsset("miso_bold.otf")
//    public static final String FONT_AUDIO_WIDE = "AudioWideRegular.ttf";
//    public static final String FONT_ROBOTO_CONDENSED_REGULAR =  "RobotoCondensedRegular.ttf";
//    public static final String FONT_ROBOTO_CONDENSED_LIGHT =  "RobotoCondensedLight.ttf";
//
//    public static final String FONT_MISO = "miso.otf";
//    public static final String FONT_MISO_BOLD = "miso_bold.otf";
//    public static final String FONT_LOTUS_FLOWER = "lotusflower.ttf";
    public static final String FONT_HERO_LIGHT = "HeroLight.otf";
    public static final String FONT_GOTHAM_BOX = "GothamBox.ttf";


    public static Typeface createFromAsset() {
        return createFromAsset(FONT_HERO_LIGHT);
    }

    public static Typeface createFromAsset(String fontName) {
        AssetManager am = ApplicationManager.getAppContext().getAssets();
        return Typeface.createFromAsset(am, "fonts/" + fontName);
    }
}
