package net.kerod.android.questionbank;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import net.kerod.android.questionbank.manager.SettingManager;
import net.kerod.android.questionbank.widget.CustomSlide;
import net.kerod.android.questionbank.widget.EditProfileSlide;
import net.kerod.android.questionbank.widget.LoginSlide;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class TempIntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorAccentTranslucent)
                        .buttonsColor(R.color.colorPrimaryDark)
                        .image(R.drawable.img_splash)
                        .title("Matriculation exams on your hand")
                        .description("All free!")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("All exams of 1990EC and beyond are included with answer");
                    }
                }, "Tell me more"));

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .title("Works offline")
                .description("You need to download exams only once")
                .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("Especially designed for areas with low connectivity.");
                    }
                }, "Tell me more"));



        addSlide(new LoginSlide());
        addSlide(new EditProfileSlide());
        addSlide(new CustomSlide());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.fourth_slide_background)
                        .buttonsColor(R.color.colorAccent)
                        .possiblePermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                        .neededPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.GET_ACCOUNTS})
                        .image(R.drawable.img_splash)
                        .title("All for you")
                        .description("We need those permission to give you the best experience")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("Try us!");
                    }
                }, "Tools"));

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.fourth_slide_buttons)
                .title("That's it")
                .description("Lets go!")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        SettingManager.setFirstTimeLaunch(false);
        Toast.makeText(this, "Try this library in your project! :)", Toast.LENGTH_SHORT).show();
    }
}