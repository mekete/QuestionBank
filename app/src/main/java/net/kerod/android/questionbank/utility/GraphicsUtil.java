package net.kerod.android.questionbank.utility;

import android.content.res.Resources;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.Exam;

/**
 * Created by bruce on 14-11-6.
 */
public class GraphicsUtil {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int getImageResourceForSubject(String subject) {
        if (Exam.SUBJECT_APTITUDE.equals(subject)) {
            return R.drawable.cimg_subject_aptitude;
        } else if (Exam.SUBJECT_ENGLISH.equals(subject)) {
            return R.drawable.cimg_subject_english;
        } else if (Exam.SUBJECT_MATHEMATICS.equals(subject)) {
            return R.drawable.cimg_subject_mathematics;
        } else if (Exam.SUBJECT_CIVICS.equals(subject)) {
            return R.drawable.cimg_subject_civics;
        }
        //
        else if (Exam.SUBJECT_CHEMISTRY.equals(subject)) {
            return R.drawable.cimg_subject_chemistry;
        } else if (Exam.SUBJECT_BIOLOGY.equals(subject)) {
            return R.drawable.cimg_subject_biology;
        } else if (Exam.SUBJECT_PHYSICS.equals(subject)) {
            return R.drawable.cimg_subject_physics;
        }
        //
        else if (Exam.SUBJECT_HISTORY.equals(subject)) {
            return R.drawable.cimg_subject_history;
        } else if (Exam.SUBJECT_GEOGRAPHY.equals(subject)) {
            return R.drawable.cimg_subject_geography;
        } else if (Exam.SUBJECT_ECONOMICS.equals(subject)) {
            return R.drawable.cimg_subject_economics;
        }
        return R.drawable.cimg_subject_unknown;
    }
}
