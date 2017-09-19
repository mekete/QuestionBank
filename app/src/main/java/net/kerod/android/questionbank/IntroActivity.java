package net.kerod.android.questionbank;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import net.kerod.android.questionbank.adapter.IntroAdapter;
import net.kerod.android.questionbank.model.IntroTemplate;
import net.kerod.android.questionbank.utility.DeviceUtil;
import net.kerod.android.questionbank.widget.StepPagerStrip;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private StepPagerStrip mStepPagerStrip;
    private ViewPager mViewPager;
    @NonNull
    List<IntroTemplate> mIntroList= new ArrayList<>();
    private static final String TAG = "IntroActivity";
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mIntroList.add(new IntroTemplate(getString(R.string.intro_antibiogram_title_one), getString(R.string.intro_antibiogram_body_one), R.drawable.img_antibiogram_one));
        mIntroList.add(new IntroTemplate(getString(R.string.intro_antibiogram_title_two),getString(R.string.intro_antibiogram_body_two), R.drawable.img_antibiogram_two));
        mIntroList.add(new IntroTemplate(getString(R.string.intro_antibiogram_title_three), getString(R.string.intro_antibiogram_body_three), R.drawable.img_antibiogram_two));
        mIntroList.add(new IntroTemplate(getString(R.string.intro_antibiogram_title_four), getString(R.string.intro_antibiogram_body_four), R.drawable.img_antibiogram_one));
        initViewPager();
        resizePagerTabStrip(mIntroList.size());

        initFab();

    }

    private void initFab() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_via_bottom);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_via_bottom);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        IntroAdapter adapter = new IntroAdapter(IntroActivity.this, mIntroList);
        mViewPager.setAdapter(adapter);

        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(-50);//show right and left a bit

        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                mViewPager.setCurrentItem(position);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);
                //mSelectedCard = mCardList.get(position);
                if (position == mIntroList.size() - 1) {
                    Log.e(TAG, "onPageSelected: " + position);
                    mFab.setVisibility(View.VISIBLE);
                } else {
                    mFab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {
            }
        });

    }

    private void resizePagerTabStrip(int cardCount) {
        mStepPagerStrip.setPageCount(cardCount);
        int minWidth = 5, maxWidth = 24;
        int dp = DeviceUtil.calculateDp(this);
        int widthCalculated = 320 / cardCount;
        if (widthCalculated < minWidth) {
            mStepPagerStrip.setTabWidth(minWidth * dp);
            mStepPagerStrip.setOnPageSelectedListener(null);//we don't want action on condensed strip
            return;
        } else if (widthCalculated > maxWidth) {
            mStepPagerStrip.setTabWidth(maxWidth * dp);
        } else {
            mStepPagerStrip.setTabWidth(widthCalculated * dp);
        }

    }
}
