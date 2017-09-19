package net.kerod.android.questionbank.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.IntroTemplate;

import java.util.List;


public class IntroAdapter extends PagerAdapter {
    private static final String TAG = "IntroAdapter";
    private Activity mActivity;
    private List<IntroTemplate> mIntroList;
    private View mViewGroup;
    private ImageView mImgvIntro;
    private TextView mTxtvMessage;
    private TextView mTxtvTitle;

    public IntroAdapter(Activity activity, List<IntroTemplate> introList) {
        super();
        mActivity = activity;
        mIntroList = introList;
    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final IntroTemplate currentIntro = mIntroList.get(position);
        mViewGroup = LayoutInflater.from(mActivity).inflate(R.layout.adapter_intro, container, false);
        mImgvIntro = (ImageView) mViewGroup.findViewById(R.id.imgv_card);
        mTxtvMessage = (TextView) mViewGroup.findViewById(R.id.txtv_message);
        mTxtvTitle = (TextView) mViewGroup.findViewById(R.id.txtv_title);
        //
        mTxtvMessage.setText(currentIntro.getMessage());
        mTxtvTitle.setText(currentIntro.getTitle() );
        mImgvIntro.setImageResource(currentIntro.getImageResourceId());
        container.addView(mViewGroup);
        return mViewGroup;
    }


    @Override
    public int getCount() {
        return mIntroList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
