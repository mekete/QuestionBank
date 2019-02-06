package net.kerod.android.questionbank.widget.toast;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import net.kerod.android.questionbank.R;


/**
 *
 * https://github.com/code-mc/loadtoast
 *
 * In this modification
 * 1) Removed nineoldandroid dependency,
 * 2) added simple hide() in addition to error() and success()
 */
public class LoadToast {

    private String mText = "";
    private LoadToastView mView;
    private ViewGroup mParentView;
    private int mTranslationY = 0;
    private boolean mShowCalled = false;
    private boolean mToastCanceled = false;
    private boolean mInflated = false;
    private boolean mVisible = false;

    public static LoadToast createLoadToast(Context context) {
        return createLoadToast(context, "");
    }

    public static LoadToast createLoadToast(Context context, String text) {
        LoadToast mLoadToast = new LoadToast(context)
                .setText(text)
                .setTranslationY(context.getResources().getDimensionPixelSize(R.dimen.loading_dialog_vertical_displacement))
                .setTextColor(ContextCompat.getColor(context, R.color.brokenWhite))
                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setProgressColor(ContextCompat.getColor(context, R.color.brokenWhite));
        return mLoadToast;
    }

    private LoadToast(Context context) {
        mView = new LoadToastView(context);
        mParentView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //ViewHelper.setAlpha(mView, 0);
        mView.setAlpha(0f);
        mParentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setTranslationX((mParentView.getWidth() - mView.getWidth()) / 2);
                mView.setTranslationY(-mView.getHeight() + mTranslationY);
                mInflated = true;
                if (!mToastCanceled && mShowCalled) show();
            }
        }, 1);

        mParentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkZPosition();
            }
        });
    }

    public LoadToast setTranslationY(int pixels) {
        mTranslationY = pixels;
        return this;
    }

    public LoadToast setText(String message) {
        mText = message;
        mView.setText(mText);
        return this;
    }

    public LoadToast setTextColor(int color) {
        mView.setTextColor(color);
        return this;
    }

    public LoadToast setBackgroundColor(int color) {
        mView.setBackgroundColor(color);
        return this;
    }

    public LoadToast setProgressColor(int color) {
        mView.setProgressColor(color);
        return this;
    }

    public LoadToast show(String message) {
        setText(message).show();
        return this;
    }

    public LoadToast show() {
        if (!mInflated) {
            mShowCalled = true;
            return this;
        }
        mView.show();
        mView.setTranslationX((mParentView.getWidth() - mView.getWidth()) / 2);
        //ViewHelper.setAlpha(mView, 0);//
        mView.setAlpha(0f);
        mView.setTranslationY(-mView.getHeight() + mTranslationY);
        //mView.setVisibility(View.VISIBLE);
        mView.animate().alpha(1f).translationY(25 + mTranslationY)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(300).setStartDelay(0).start();

        mVisible = true;
        checkZPosition();

        return this;
    }


    public void success() {
        if (!mInflated) {
            mToastCanceled = true;
            return;
        }
        mView.success();
        slideUp();
    }

    public void error() {
        if (!mInflated) {
            mToastCanceled = true;
            return;
        }
        mView.error();
        slideUp();
    }

    public boolean isVisible(){
        return mVisible;
    }

    public void hide() {
        if (!mInflated) {
            mToastCanceled = true;
            return;
        }
        slideUp();
    }

    private void checkZPosition() {
        // If the toast isn't visible, no point in updating all the views
        if (!mVisible) return;

        int pos = mParentView.indexOfChild(mView);
        int count = mParentView.getChildCount();
        if (pos != count - 1 && null != mView.getParent()) {
            ((ViewGroup) mView.getParent()).removeView(mView);
            mParentView.requestLayout();
            mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public void slideUp() {
        mVisible = false;
        mView.animate().setStartDelay(1000).alpha(0f)
                .translationY(-mView.getHeight() + mTranslationY)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .start();


    }
}
