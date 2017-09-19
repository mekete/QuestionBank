package net.kerod.android.questionbank.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import net.kerod.android.questionbank.R;

import agency.tango.materialintroscreen.SlideFragment;

public class CustomSlide extends SlideFragment {
    private CheckBox mCkbxAcceptTerms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.segment_intro_custom_slide, container, false);
        mCkbxAcceptTerms = (CheckBox) view.findViewById(R.id.checkBox);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        return mCkbxAcceptTerms.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }
}