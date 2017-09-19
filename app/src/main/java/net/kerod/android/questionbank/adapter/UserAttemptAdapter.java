package net.kerod.android.questionbank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kerod.android.questionbank.QuestionActivity;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.utility.Constants;

import java.util.List;


public class UserAttemptAdapter extends RecyclerView.Adapter<UserAttemptAdapter.ViewHolder> {
    private List<UserAttempt> mUserAttemptList;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtvQuestionNumber;
        TextView mTxtvGradeAndYear;
        public View mRootView;

        public ViewHolder(View parent) {
            super(parent);
            mRootView = parent;

            mTxtvQuestionNumber = (TextView) parent.findViewById(R.id.txtv_short_name);
            mTxtvGradeAndYear = (TextView) parent.findViewById(R.id.txtv_grade_and_year);
        }
    }

    public UserAttemptAdapter(Context context, List<UserAttempt> examList) {
        mUserAttemptList = examList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.adapter_attempt, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserAttempt attempt = mUserAttemptList.get(position);


        holder.mTxtvQuestionNumber.setText(attempt.getQuestionNumber()+"");
        //holder.mTxtvQuestionNumber.setTextColor(attempt.getAttemptScoreColor());
        if(Constants.COLOR_CHOICE_BACKGROUND_CORRECT==attempt.getAttemptScoreColor()){
            holder.mTxtvQuestionNumber.setBackgroundResource(R.drawable.bg_attempt_correct);
            //holder.mTxtvQuestionNumber.setBackgroundResource(R.drawable.bg_attempt_correct);
        }else if(Constants.COLOR_CHOICE_BACKGROUND_INCORRECT==attempt.getAttemptScoreColor()){
            holder.mTxtvQuestionNumber.setBackgroundResource(R.drawable.bg_attempt_incorrect);
        }else{
            holder.mTxtvQuestionNumber.setBackgroundResource(R.drawable.bg_attempt_neutral);
        }
        holder.mTxtvGradeAndYear.setText(attempt.getScore()?"Correct":"Wrong");
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, QuestionActivity.class);
                intent.putExtra(QuestionActivity.TAG_SELECTED_QUESTION,attempt.getQuestionNumber());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                ((Activity) mContext).finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserAttemptList.size();
    }
}
