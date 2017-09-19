package net.kerod.android.questionbank.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.utility.Constants;

import java.util.List;

import io.github.kexanie.library.MathView;

public class QuestionAdapter extends BaseAdapter {//implements OnClickListener {
    private static final String TAG = "QuestionAdapter";
    private static final int TEXT_ZOOM = 112;
    //
    private List<Question> mQuestionList;
    private AttemptCallback attemptCallback;
    private static final String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //
    int[] choiceViewId = {R.id.math_choice_a, R.id.math_choice_b, R.id.math_choice_c, R.id.math_choice_d};//, R.id.math_choice_e};
    //static final String[] choiceLetters = {"A", "B", "C", "D"};//, "E"};
    //private Question currentQuestion;
    //int correctAnswerIndex;
    private ViewHolder mViewHolderCacheForActivity;

    public static final int NUMBER_OF_CHOICES = 4;

    public interface AttemptCallback {
        void onAttempt(MathView[] webViewArray, int choiceIndex);
    }


    public AttemptCallback getAttemptCallback() {
        return attemptCallback;
    }

    public void setAttemptCallback(AttemptCallback attemptCallback) {
        this.attemptCallback = attemptCallback;
    }


    private LayoutInflater inflater;

    public QuestionAdapter(Context context, List<Question> questionList) {
        inflater = LayoutInflater.from(context);
        this.mQuestionList = questionList;
    }

    @Override
    public int getCount() {
        return mQuestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mQuestionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mQuestionList.get(position).getQuestionNumber();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_question, parent, false);
            holder.mViewStatement = (MathView) convertView.findViewById(R.id.math_statement);
            holder.mViewStatement.getSettings().setTextZoom(TEXT_ZOOM);
            for (int choiceIndex = 0; choiceIndex < NUMBER_OF_CHOICES; choiceIndex++) {
                holder.mViewChoices[choiceIndex] = (MathView) convertView.findViewById(choiceViewId[choiceIndex]);
                holder.mViewChoices[choiceIndex].getSettings().setTextZoom(TEXT_ZOOM);
            }
            convertView.setTag(holder);
            mViewHolderCacheForActivity = holder;

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mQuestion=mQuestionList.get(position);
        holder.mViewStatement.setText(holder.mQuestion.getStatement());

        String[] choiceStatements = {holder.mQuestion.getChoiceA(), holder.mQuestion.getChoiceB(), holder.mQuestion.getChoiceC(), holder.mQuestion.getChoiceD()};
        for (int i = 0; i < NUMBER_OF_CHOICES; i++) {
            final int choiceIndex = i;

            holder.mViewChoices[i].setText(choiceStatements[i]);
            holder.mViewChoices[i].setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (attemptCallback != null) {
                            attemptCallback.onAttempt(holder.mViewChoices, choiceIndex);
                        }
                    }
                    return false;
                }
            });
        }
        showAttemptColor( holder.mViewChoices,  holder.mQuestion);
        return convertView;
    }

    private void showAttemptColor( final MathView[] mWebViewArray, final Question selectedQuestion) {
        Query query = UserAttempt.getDatabaseReference(mUserId, selectedQuestion.getExamUid(), selectedQuestion.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);

                if (attempt != null && selectedQuestion.getUid().equals(dataSnapshot.getKey())) {
                   // mCurrentQuestionAttempted = true;//when choice is clicked, it will have no effect as it is already attempted
                    int color = attempt.getScore() ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT;
                    colorizeAttempt(mWebViewArray, attempt.getAttemptChoiceIndex(), color);
                    //showFab(color);
                } else {
                    colorizeAttempt(mWebViewArray, 0, Constants.COLOR_CHOICE_BACKGROUND_NEUTRAL);
                    //hideFab();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void colorizeAttempt(MathView[] mWebViewArray, int attemptedChoiceIndex, int color) {
        //clearChoiceColor(mWebViewArray);
        if (attemptedChoiceIndex < 0 || attemptedChoiceIndex > QuestionAdapter.NUMBER_OF_CHOICES) {
            Log.e(TAG, "\n----------------------------\n" + "111 colorizeAttempt: clickedIndex " + attemptedChoiceIndex);
            return;
        }
        for (int i = 0; i < QuestionAdapter.NUMBER_OF_CHOICES; i++) {
            if(i==attemptedChoiceIndex){
                Log.e(TAG, "\n----------------------------\n" + "222 colorizeAttempt: ");
                mWebViewArray[i].setBackgroundColor(color);
                Log.e(TAG, "\n----------------------------\n" + "333 colorizeAttempt: " + (color == Constants.COLOR_CHOICE_BACKGROUND_CORRECT? "Color.GREEN" : " Color.RED") + " " +  attemptedChoiceIndex);
                mWebViewArray[i].setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

            }else {
                mWebViewArray[i].setBackgroundColor(Color.WHITE);
            }
        }

    }

    public static class ViewHolder {
        Question mQuestion;
        MathView mViewStatement;
        MathView[] mViewChoices = new MathView[NUMBER_OF_CHOICES];

        public MathView[] getViewChoices() {
            return mViewChoices;
        }



    }

    public ViewHolder getCachedViewHolder() {
        return mViewHolderCacheForActivity;
    }
}
