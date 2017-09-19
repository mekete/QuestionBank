package net.kerod.android.questionbank;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.utility.GraphicsUtil;
import net.kerod.android.questionbank.widget.ContactBottomSheetDialog;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutExamActivity extends AppCompatActivity {
    private static final Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    //
    private TextView mTxtvAboutExam;
    private TextView mTxtvExamShortName;
    private TextView mTxtvExamFullName;
    private ImageView mImgvSponsorCompany;
    private  CircleImageView mCimgSubjectIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_exam);
        initToolbar();
        initComponents();
        initFab();
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void initFab() {
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_continue);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               onBackPressed();
//            }
//        });

    }

    private void initComponents() {
        mTxtvAboutExam = (TextView) findViewById(R.id.txtv_about_exam);
        mTxtvExamShortName = (TextView) findViewById(R.id.txtv_exam_short_name);
        mTxtvExamFullName = (TextView) findViewById(R.id.txtv_exam_full_name);
        mCimgSubjectIcon= (CircleImageView) findViewById(R.id.cimg_subject_icon);
        mImgvSponsorCompany = (ImageView) findViewById(R.id.imgv_sponsor_company);
        mCimgSubjectIcon.setImageResource(GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject()));
        mCimgSubjectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsTheTitleVisible ){
                    onBackPressed();
                }
            }
        });

        mTxtvAboutExam.setText(
                "Number of Question\n\t"+mCurrentExam.getNumberOfQuestions()+
                "\n\nAllowed Time\n\t"+mCurrentExam.getAllowedTime()+
                        "\n\nExam Date\n\t"+mCurrentExam.getExamGivenDate()+
                        "\n\nTotal Downloads\n\t"+27+
                        "\n\nVersion\n\t"+"1.0.3"+
                        "\n\nSponsored by\n\t"+"KerodApps"+
                        "\n\nFlagged questions\n\t"+"9 "+
                        "\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
        );
        mTxtvExamShortName.setText(mCurrentExam.getShortName());
        mTxtvExamFullName.setText(mCurrentExam.getFullName());
        mImgvSponsorCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactBottomSheetDialog.createAndShow("(+251) 91 227 3495","kerod.apps@gmail.com",AboutExamActivity.this);
                //open website
            }
        });


    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle = (TextView) findViewById(R.id.txtv_collapsed_bar_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        //
        mAppBarLayout.addOnOffsetChangedListener(offsetChangedListener);
        mToolbar.inflateMenu(R.menu.menu_about_exam);
        mTitle.setText(mCurrentExam.getShortName());

    }

    AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about_exam, menu);
        return true;
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mCimgSubjectIcon.setImageResource(R.drawable.ic_arrow_back_12dp);
                mCimgSubjectIcon.setBorderWidth(0);

            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mCimgSubjectIcon.setImageResource(GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject()));
                mCimgSubjectIcon.setBorderWidth(2);

            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
