package net.kerod.android.questionbank.utility;

import android.content.res.Resources;
import androidx.core.content.ContextCompat;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.manager.ApplicationManager;


public class Constants {
    public static final String CLASS_UNDEFINED = "UND";
    public static final String CLASS_HIGH_SCHOOL = "HS";
    public static final String CLASS_PREP_SOCIAL = "PS";
    public static final String CLASS_PREP_NATURAL = "PN";
    //
    public final static Resources res = ApplicationManager.getAppContext().getResources();
    public final static int ADAPTER_VIEW_TYPE_BODY = 0;
    public final static int ADAPTER_VIEW_TYPE_PINNED_HEADER = 1;
    //
    //public final static int COLOR_CHOICE_BACKGROUND_NEUTRAL   = res.getColor(R.color.choice_color_neutral   );//
    public final static int COLOR_CHOICE_BACKGROUND_NEUTRAL   = ContextCompat.getColor(ApplicationManager.getAppContext(), R.color.choice_color_neutral   );//
    public final static int COLOR_CHOICE_BACKGROUND_CORRECT   = ContextCompat.getColor(ApplicationManager.getAppContext(), R.color.choice_color_correct);// 0xffaeea00;//0xff14e715;//0xffc6ff00 ;//0xff76ff03;
    public final static int COLOR_CHOICE_BACKGROUND_INCORRECT     = ContextCompat.getColor(ApplicationManager.getAppContext(), R.color.choice_color_incorrect);//
    //
    public final static int COLOR_CHOICE_BACKGROUND_GERY = ContextCompat.getColor(ApplicationManager.getAppContext(), R.color.choice_color_untried_grey);//
    //
    public final static String EMPTY_STRING_INDICATOR = "";
    public static final int[] AVATAR_RESOURCE_IDS = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            // R.drawable.avatar_16,
    };

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_URL = "url";
    public static final String EMPTY_STRING = "";
}
