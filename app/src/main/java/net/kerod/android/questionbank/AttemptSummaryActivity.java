package net.kerod.android.questionbank;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.adapter.UserAttemptAdapter;
import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.utility.Constants;
import net.kerod.android.questionbank.widget.ArcProgress;
import net.kerod.android.questionbank.widget.CustomView;

import java.util.ArrayList;
import java.util.List;

public class AttemptSummaryActivity extends AppCompatActivity {
    private static final String TAG = "AttemptSummaryActivity";
    private Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();
    private final DatabaseReference mAttemptDatabaseReference = UserAttempt.getDatabaseReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), mCurrentExam.getUid());
    //final DatabaseReference attemptReference = mAttemptDatabaseReference.child(mCurrentQuestion.getUid());
    private RelativeLayout mRellProgressContainer;
    private RecyclerView mRecyclerView;
    //
    final List<UserAttempt> mAttemptListAll = new ArrayList<>();
    final List<UserAttempt> mAttemptListCorrect = new ArrayList<>();
    final List<UserAttempt> mAttemptListIncorrect = new ArrayList<>();
    final List<UserAttempt> mAttemptListShowing = new ArrayList<>();
    UserAttemptAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_summary);
        initToolbar();
        initRecyclerView();
        initBottomNavigation();
    }


    private void initToolbar() {


        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mCurrentExam.getShortName());
            actionBar.setSubtitle("My Activity");
        }
    }

    int correctCount = 0;
    int incorrectCount = 0;
    int totalCount = 0;

    void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recv_question_grid);
        final Query query = Question.getDatabaseReference(mCurrentExam.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Question> questionList = new ArrayList<>();
                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange:query.getRef()  :::: " + query.getRef());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Question question = snapshot.getValue(Question.class);
                        question.setUid(snapshot.getKey());
                        questionList.add(question);
                    } catch (Exception e) {
                        Log.e(TAG, "\n\n\n\n\n------------ EXCEPTION ::::  " + e);
                        e.printStackTrace();
                    }
                }
                //

                mAttemptDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mAttemptListAll.clear();
                        mAttemptListCorrect.clear();
                        mAttemptListIncorrect.clear();
                        Log.e(TAG, "\n\n\n\n>>>>>>> 222 initRecyclerView :::: ");

                        for (Question question : questionList) {
                            Log.e(TAG, "\n\n\n\n>>>>>>> 333 initRecyclerView :::: ");

                            DataSnapshot snapshot = dataSnapshot.child(question.getUid());
                            if (snapshot == null || snapshot.getValue(UserAttempt.class) == null) {
                                mAttemptListAll.add(new UserAttempt(question.getQuestionNumber(), Constants.COLOR_CHOICE_BACKGROUND_GERY));
                                Log.e(TAG, "\n\n\n\n>>>>>>> 444 initRecyclerView :::: ");
                            } else {
                                Log.e(TAG, "\n\n\n\n>>>>>>> 555 initRecyclerView :::: snapshot length ::: " + snapshot.getChildrenCount());
                                UserAttempt attempt = snapshot.getValue(UserAttempt.class);
                                attempt.setQuestionNumber(question.getQuestionNumber());
                                attempt.setAttemptScoreColor(attempt.getScore() ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT);
                                //
                                mAttemptListAll.add(attempt);
                                if (attempt.getScore()) {
                                    mAttemptListCorrect.add(attempt);
                                    correctCount++;
                                } else {
                                    incorrectCount++;
                                    mAttemptListIncorrect.add(attempt);
                                }
                            }
                            totalCount++;

                        }
                        mAttemptListShowing.clear();
                        mAttemptListShowing.addAll(mAttemptListAll);
                        Log.e(TAG, "\n\n\n\n>>>>>>> 6666 initRecyclerView :::: attemptList " + mAttemptListShowing.size());
                        mAdapter = new UserAttemptAdapter(AttemptSummaryActivity.this, mAttemptListShowing);
                        mRecyclerView.setAdapter(mAdapter);
                        Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange: 7777 mQuestionList.size() :::: " + mAttemptListShowing.size());
                        //handleRecyclerScroll( );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "\n\n\n\n>>>>>>> 8888 initRecyclerView :::: ");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attempt_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_info:
                showStatistics();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showStatistics() {
        if (totalCount > 0) {
            StatisticsBottomSheetDialog.createAndShow(AttemptSummaryActivity.this, correctCount, incorrectCount, totalCount);
        } else {
            CustomView.makeSnackBar(findViewById(R.id.main_content), getString(R.string.statistics_not_available), CustomView.SnackBarStyle.INFO).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (mAdapter != null) {
                switch (item.getItemId()) {
                    case R.id.item_correct:
                        mAttemptListShowing.clear();
                        mAttemptListShowing.addAll(mAttemptListCorrect);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.item_incorrect:
                        mAttemptListShowing.clear();
                        mAttemptListShowing.addAll(mAttemptListIncorrect);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.item_favorite:
                        mAttemptListShowing.clear();
                        mAttemptListShowing.addAll(mAttemptListAll);
                        mAdapter.notifyDataSetChanged();
                        return true;
//                    case R.id.item_stat:
//                        showStatistics();
//                        return true;
                }
            }
            return false;
        }

    };


    protected void initBottomNavigation() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private static class StatisticsBottomSheetDialog {
        static BottomSheetDialog dialog;
        static private ArcProgress mArcpCorrect;
        static private ArcProgress mArcpInCorrect;
        static private ArcProgress mArcpTime;
        //
        static TextView txtvTitle;
//        static TextView txtvBody;

        private StatisticsBottomSheetDialog() {
        }

        public static void createAndShow(Context context, int correctCount, int incorrectCount, int totalCount) {
            if (dialog == null) {
                dialog = new BottomSheetDialog(context);
                View viewGroup = LayoutInflater.from(context).inflate(R.layout.dialog_attempt_summary, null);
                txtvTitle = viewGroup.findViewById(R.id.txtv_dialog_title);
                mArcpCorrect = viewGroup.findViewById(R.id.arcp_correct);
                mArcpInCorrect = viewGroup.findViewById(R.id.arcp_incorrect);
                mArcpTime = viewGroup.findViewById(R.id.arcp_total_correct);
                dialog.setOnDismissListener(dialog -> dialog = null);
                dialog.setContentView(viewGroup);//
                //
                dialog.setOnShowListener(dialogInterface -> {
                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                    FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                });
            }
//            int correctCount= ApplicationManager.CurrentSession.getSelectedExamAttemptSummary().getCorrectCount();
//            int incorrectCount= ApplicationManager.CurrentSession.getSelectedExamAttemptSummary().getIncorrectCount();
//            int totalCount= ApplicationManager.CurrentSession.getSelectedExam().getNumberOfQuestions();
            mArcpCorrect.setProgress(correctCount * 100 / totalCount);
            mArcpInCorrect.setProgress(incorrectCount * 100 / totalCount);
            mArcpTime.setProgress(((totalCount - correctCount - incorrectCount) * 100) / totalCount);
            //
            mArcpCorrect.setCenterText(correctCount + "");
            mArcpInCorrect.setCenterText(incorrectCount + "");
            mArcpTime.setCenterText((totalCount - correctCount - incorrectCount) + "");
            //
            dialog.show();
        }


        public static boolean isVisible() {
            return dialog != null && dialog.isShowing();

        }

    }
}
