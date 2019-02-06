package net.kerod.android.questionbank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import net.kerod.android.questionbank.ExamHomeActivity;
import net.kerod.android.questionbank.LoginActivity;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.utility.GraphicsUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {
    private List<Exam> mExamList;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtvShortName;
        TextView mTxtvGradeAndYear;
        CircleImageView mCimgSubjectIcon;
        public View mRootView;

        public ViewHolder(View parent) {
            super(parent);
            mRootView = parent;

            mTxtvShortName = (TextView) parent.findViewById(R.id.txtv_short_name);
            mTxtvGradeAndYear = (TextView) parent.findViewById(R.id.txtv_grade_and_year);
            mCimgSubjectIcon= (CircleImageView) parent.findViewById(R.id.cimg_subject_icon);
        }
    }

    public ExamAdapter(Context context, List<Exam> examList) {
        mExamList = examList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.adapter_exam, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Exam exam = mExamList.get(position);


        holder.mTxtvShortName.setText(exam.getShortName());
        holder.mTxtvGradeAndYear.setText(exam.getFullName());
        holder.mCimgSubjectIcon.setBackgroundResource(GraphicsUtil.getImageResourceForSubject(exam.getSubject()));
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                } else {
                    ApplicationManager.CurrentSession.setSelectedExamWithSummary(exam);
                    Intent intent = new Intent(mContext, ExamHomeActivity.class);
                    mContext.startActivity(intent);

                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExamList.size();
    }
}
