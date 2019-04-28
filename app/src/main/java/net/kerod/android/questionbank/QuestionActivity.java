package net.kerod.android.questionbank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.arclayout.ArcLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import net.kerod.android.questionbank.adapter.QuestionAdapter;
import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.FirebaseModel;
import net.kerod.android.questionbank.model.Instruction;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.model.UserAttemptSummary;
import net.kerod.android.questionbank.utility.Constants;
import net.kerod.android.questionbank.utility.DeviceUtil;
import net.kerod.android.questionbank.utility.GraphicsUtil;
import net.kerod.android.questionbank.utility.SocialUtil;
import net.kerod.android.questionbank.widget.AnimatorUtils;
import net.kerod.android.questionbank.widget.ClipRevealFrame;
import net.kerod.android.questionbank.widget.CustomView;
import net.kerod.android.questionbank.widget.MessageBottomSheetDialog;
import net.kerod.android.questionbank.widget.RemarkBottomSheetDialog;
import net.kerod.android.questionbank.widget.ReportErrorBottomSheetDialog;
import net.kerod.android.questionbank.widget.TagsBottomSheetDialog;
import net.kerod.android.questionbank.widget.toast.LoadToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.emilsjolander.flipview.FlipView;
import se.emilsjolander.flipview.OverFlipMode;


public class QuestionActivity extends AppCompatActivity {

    private static final int VIBRATION_DURATION_IN_MILLS = 200;
    private static final String TAG = "ShowQuestionActivity";
    private static final String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static final String TAG_SELECTED_QUESTION = "selectedQuestion";
    private Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();
    private UserAttemptSummary mAttemptSummary = ApplicationManager.CurrentSession.getSelectedExamAttemptSummary();
    private final DatabaseReference mAttemptDatabaseReference = UserAttempt.getDatabaseReference(mUserId, mCurrentExam.getUid());
    //
    private View mMainContent;
    private RelativeLayout mRootLayout;
    private ClipRevealFrame mArcRevealFrame; //parent of mArcLayout
    private ArcLayout mArcLayout;
    private View mCenterItem;
    private FlipView mFlipView;
    private QuestionAdapter mAdapter;
    private FloatingActionButton mFab;
    private TextView mFabBackGround;
    private Interpolator mInterpolator;
    //
    private ProgressBar mPrgbExamQuestionsProgress;
    private Chronometer mCronExamTimeTaken;
    private TextView mProgressQuestion;
    //
    private List<Question> mQuestionList = new ArrayList<>();
    private Question mCurrentQuestion;
    private Long mStartTimeStamp;
    private Toolbar mToolbar;
    private boolean mCurrentQuestionAttempted = false;
    private long mExamPrevTimeTaken = 0;
    private boolean mFinishedLoadingQuestions = false;
    private LoadToast mLoadToast;

    private static final OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mInterpolator = new FastOutSlowInInterpolator();
        mMenuFragmentManager = getSupportFragmentManager();
        mMainContent = findViewById(R.id.main_content);
        mLoadToast = LoadToast.createLoadToast(this, getString(R.string.loading));
        //
//        Bundle bundle=getIntent().getExtras();
//        if (bundle!=null  ) {//it is launched not from exam list but from subject icon,
//            String examUid = getIntent().getExtras().getString(FirebaseModel.FIELD_UID);
//            fetchExam(examUid);
//            Log.e(TAG, "onCreate: EXAM UID :::: " + getIntent().getExtras().getString(FirebaseModel.FIELD_UID));
//        }
        initProgress();
        initToolbarAndDrawer();
        initArcLayout();
        initFlipView();
        initMenuFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExamQuestions();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mFinishedLoadingQuestions) {
            mCronExamTimeTaken.stop();
            if (ApplicationManager.CurrentSession.getSelectedExam() != null) {//if the exam is deleted, it will throw NPE

                saveExamSummary();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int selectedQuestion = intent.getIntExtra(TAG_SELECTED_QUESTION, -1);
        if (selectedQuestion > 0 && mFlipView != null && mQuestionList != null) {
            mFlipView.flipTo(selectedQuestion - 1);
        } else if (mAttemptSummary != null && mAttemptSummary.getLastOpenedQuestionIndex() > 0 && mFlipView != null) {

            mFlipView.flipTo(mAttemptSummary.getLastOpenedQuestionIndex());
        } else {
        }
    }

    protected void startTimer() {
        if (mAttemptSummary != null && mAttemptSummary.getTotalTimeUsed() != null) {
            mExamPrevTimeTaken = mAttemptSummary.getTotalTimeUsed();
            Log.e(TAG, "startTimer:mAttemptSummary \n11111C COOOOOOOOOOOOOOOOOOOOOOOOOL  " + mAttemptSummary.getTotalTimeUsed());
        } else {

        }

        mStartTimeStamp = SystemClock.elapsedRealtime() - mExamPrevTimeTaken;
        mCronExamTimeTaken.setBase(mStartTimeStamp);
        mCronExamTimeTaken.start();
    }


    private void loadExamQuestions() {
        mLoadToast.setText("Loading questions...");
        mLoadToast.show();
        final Query query = Question.getDatabaseReference(mCurrentExam.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLoadToast.success();
                mQuestionList.clear();
                mFinishedLoadingQuestions = true;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Question question = snapshot.getValue(Question.class);
                        question.setUid(snapshot.getKey());
                        mQuestionList.add(question);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (mQuestionList.size() > 0) {
                    //Collections.sort(mQuestionList);
                    mCurrentQuestion = mQuestionList.get(0);
                    mProgressQuestion.setText("1/" + mQuestionList.size());
                    mCurrentQuestion = mQuestionList.get(0);

                }
                mAdapter.notifyDataSetChanged();
                if (SettingsManager.isFirstTimeToSeeQuestionActivity()) {
                    mFlipView.peakNext(true);
                    SettingsManager.setFirstTimeToSeeQuestionActivity(false);
                }
                flipToLastSeenQuestion();
                startTimer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLoadToast.error();
            }
        });
        //

    }

    private void openAttemptSummary(boolean showStatistics) {
        if (mQuestionList.size() > 0) {
            Intent intent = new Intent(QuestionActivity.this, AttemptSummaryActivity.class);
            intent.putExtra(AttemptSummaryActivity.ARG_SHOW_STATISTICS, showStatistics);

            startActivity(intent);
        } else {
            CustomView.makeSnackBar(mMainContent, getString(R.string.questions_not_loaded_yet), CustomView.SnackBarStyle.INFO).setAction("OK", null).show();
        }
    }

    private void openAboutExam() {
        Intent intent = new Intent(QuestionActivity.this, AboutExamActivity.class);
        startActivity(intent);
    }

    private void initFlipView() {
        mFlipView = findViewById(R.id.flip_view);
        mFlipView.setOnFlipListener(mOnFlipListener);
        mFlipView.peakNext(true);
        mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        //
        mAdapter = new QuestionAdapter(this, mQuestionList);
        mAdapter.setAttemptCallback(mAttemptCallback);
        mFlipView.setAdapter(mAdapter);
    }

    private void flipToLastSeenQuestion() {
        int selectedQuestion = getIntent().getIntExtra(TAG_SELECTED_QUESTION, -1);
        if (selectedQuestion > 0 && mFlipView != null && mQuestionList != null) {
            mFlipView.flipTo(selectedQuestion - 1);
        } else if (mAttemptSummary != null && mAttemptSummary.getLastOpenedQuestionIndex() > 0 && mFlipView != null) {
            mFlipView.flipTo(mAttemptSummary.getLastOpenedQuestionIndex());
        }
    }


    private FlipView.OnFlipListener mOnFlipListener = new FlipView.OnFlipListener() {
        @Override
        public void onFlippedToPage(FlipView v, int position, long id) {
            mCurrentQuestion = mQuestionList.get(position);
            showFabForHistory(mUserId, mCurrentQuestion);
            updateProgress();
            saveExamSummary();
            remindToReadInstruction(v, mCurrentQuestion);


        }
    };

    private void remindToReadInstruction(View v, final Question currentQuestion) {

        try {
            if (currentQuestion.getQuestionNumber() > 25 && currentQuestion.getQuestionNumber() < 30) {
                Log.e(TAG, "START OF SECTION ::: : " + currentQuestion.getQuestionNumber() + "\n:::: " + currentQuestion.getStartOfSection());
            }
            if (currentQuestion.getQuestionNumber() == 12 || currentQuestion.getStartOfSection()) {
                CustomView.makeSnackBar(v, "Read instruction for this question.", CustomView.SnackBarStyle.INFO).setAction("Read now", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String instructionUid = currentQuestion.getInstructionCode();
                        if (instructionUid != null) {
                            showInstruction(instructionUid);
                        }
                    }
                }).show();
//
            }
        } catch (Exception e) {
            Log.e(TAG, "\n\n>>>>>>>>\nremindToReadInstruction: " + e);
            e.printStackTrace();
        }
    }


    private QuestionAdapter.AttemptCallback mAttemptCallback = new QuestionAdapter.AttemptCallback() {

        @Override
        public void onAttempt(final View[] webViewArray, final int clickedIndex) {
            if (!mCurrentQuestionAttempted) {
                showFabAndAttemptForAction(webViewArray, clickedIndex, mUserId, mCurrentQuestion);
            }
        }
    };


    private void showFabAndAttemptForAction(View[] webViewArray, int clickedIndex, final String userUid, Question selectedQuestion) {
        mCurrentQuestionAttempted = true; //clicking a choice will not have effect for the current question afterwards
        //
        boolean correctAttempt = (Question.getChoiceLetterIndex(mCurrentQuestion.getCorrectAnswer()) == clickedIndex);
        int color = correctAttempt ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT;
        colorizeAttempt(webViewArray, clickedIndex, correctAttempt);
        showFab(color);
        if (SettingsManager.isVibrateOnWrongAttempt() && !correctAttempt) {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(VIBRATION_DURATION_IN_MILLS);
        }
        //
        saveUserAction(webViewArray[clickedIndex], clickedIndex, correctAttempt, null, null, null);

    }

    int mUserAttemptColor = -1;

    private void showFabForHistory(final String userUid, final Question selectedQuestion) {
        mCurrentQuestionAttempted = false;
        Query query = UserAttempt.getDatabaseReference(userUid, selectedQuestion.getExamUid(), selectedQuestion.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);

                if (attempt != null && mCurrentQuestion.getUid().equals(dataSnapshot.getKey())) {
                    mCurrentQuestionAttempted = true;
                    mUserAttemptColor = attempt.getScore() ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT;
                    showFab(mUserAttemptColor);
                } else {
                    hideFab();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void saveExamSummary() {
        final DatabaseReference summaryReference = UserAttemptSummary.getDatabaseReference();
        final long totalTimeUsed = SystemClock.elapsedRealtime() - mStartTimeStamp;

        summaryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttemptSummary summary = dataSnapshot.getValue(UserAttemptSummary.class);
                if (summary == null) {
                    summary = new UserAttemptSummary();
                }
                summary.setLastOpenedTime(new Date().getTime());
                summary.setTotalTimeUsed(totalTimeUsed);
                summary.setLastOpenedQuestionIndex(mCurrentQuestion.getQuestionNumber() - 1);
                //
                Log.e(TAG, "\n\n\n>>>>>>\nonDataChange saved setLastOpenedQuestionIndex: " + (mCurrentQuestion.getQuestionNumber() - 1));
                //
                summaryReference.setValue(summary);
                ///Log.e(TAG, "\n\n\n\n\n------------ current.getKey() onDataChange: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void saveUserAction(final View coordinatorLayout, final Integer attemptChoiceIndex, final Boolean scoreCorrect, final Boolean markedFavorite, final Boolean markedVague, final String remark) {
        final DatabaseReference attemptReference = mAttemptDatabaseReference.child(mCurrentQuestion.getUid());
        attemptReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);
                if (attempt == null) {
                    if (attemptChoiceIndex == null) {
                        return;//we have to save progress only after we save the score of the user
                    } else {
                        attempt = new UserAttempt();
                        attempt.setAttemptChoiceIndex(attemptChoiceIndex);
                        attempt.setQuestionNumber(mCurrentQuestion.getQuestionNumber());
                    }

                }
                // ---------------------------------------------------------------------------------


                if (scoreCorrect != null) {
                    attempt.setScore(scoreCorrect);
                    attempt.setAttemptScoreColor(scoreCorrect ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT);
                }
                if (markedFavorite != null) {
                    CustomView.makeSnackBar(coordinatorLayout, "The question is tagged as favorite.", CustomView.SnackBarStyle.SUCCESS).show();
                }
                if (markedVague != null) {
                    attempt.setMarkedAsError(markedVague);
                    CustomView.makeSnackBar(coordinatorLayout, "Your report has been sent. Thank you.", CustomView.SnackBarStyle.SUCCESS).show();
                }
                if (remark != null) {
                    attempt.setRemark(remark);
                    CustomView.makeSnackBar(coordinatorLayout, "Your remark is saved.", CustomView.SnackBarStyle.SUCCESS).show();

                }
                attemptReference.setValue(attempt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void colorizeAttempt(View[] webViewArray, int attemptedChoiceIndex, boolean correctAttempt) {
        //clearChoiceColor(webViewArray);
        if (attemptedChoiceIndex < 0 || attemptedChoiceIndex > QuestionAdapter.NUMBER_OF_CHOICES) {
            Log.e(TAG, "\n----------------------------\n" + "111 colorizeAttempt: clickedIndex " + attemptedChoiceIndex);
            return;
        }
        for (int i = 0; i < QuestionAdapter.NUMBER_OF_CHOICES; i++) {
            if (i == attemptedChoiceIndex) {
                int backgroundResource = correctAttempt ? R.drawable.bg_choice_correct : R.drawable.bg_choice_incorrect;
                Log.e(TAG, "\n----------------------------\n" + "222 colorizeAttempt: ");
                webViewArray[i].setBackgroundResource(backgroundResource);

            } else {
                webViewArray[i].setBackgroundResource(R.drawable.bg_choice_neutral);
            }
        }

    }

    private void initProgress() {
        mPrgbExamQuestionsProgress = findViewById(R.id.prog_question);
        mCronExamTimeTaken = findViewById(R.id.txtv_time_used);
//        mCronExamTimeTaken.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                long duration=SystemClock.elapsedRealtime()-chronometer.getBase();
//                chronometer.setText("");
//            }
//        });

        //
        mProgressQuestion = findViewById(R.id.txtv_progress_count);
        mPrgbExamQuestionsProgress.setProgress(1);
        mProgressQuestion.setText("1/" + mQuestionList.size());
        mProgressQuestion.setOnClickListener(v -> openAttemptSummary(false));
        mCronExamTimeTaken.setOnClickListener(v -> openAttemptSummary(true));
        mCronExamTimeTaken.setText("  00:00  ");
    }

    private void updateProgress() {
        int totalQuestions = mQuestionList.size();
        int questionNumber = mFlipView.getCurrentPage() + 1;
        int percent = (questionNumber * 100) / (totalQuestions);
        mPrgbExamQuestionsProgress.setProgress(percent);
        mProgressQuestion.setText(questionNumber + "/" + totalQuestions);
        //
    }


    //-------flip view
    private void initArcLayout() {
        mRootLayout = findViewById(R.id.root_layout);
        mArcRevealFrame = findViewById(R.id.arc_reveal_frame);
        mArcLayout = findViewById(R.id.arc_layout);
        mCenterItem = findViewById(R.id.center_item);

        mCenterItem.setOnClickListener(mRevelItemCLickListener);
        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(mRevelItemCLickListener);
        }
        mFab = findViewById(R.id.fab);
        mFabBackGround = findViewById(R.id.fab_bg);
        mFab.setOnClickListener(mRevelItemCLickListener);
        mArcRevealFrame.setOnClickListener(mRevelItemCLickListener);
    }

    private void showFab(int color) {
        if (color != -1 || ViewCompat.isLaidOut(mFab) || (View.VISIBLE == mFab.getVisibility() && mFab.getRippleColorStateList().getDefaultColor() == color)) {

            mFab.setBackgroundTintList(ColorStateList.valueOf(color));
            mFab.show();
            mFabBackGround.setVisibility(View.VISIBLE);
            return;
        } else {
            mFab.setScaleX(0f);
            mFab.setScaleY(0f);
            mFab.setAlpha(0f);
            mFab.setBackgroundTintList(ColorStateList.valueOf(color));
            mFab.show();
            ViewCompat.animate(mFab)
                    .scaleX(1)
                    .scaleY(1)
                    .alpha(1)
                    .setInterpolator(mInterpolator)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onAnimationEnd(View view) {

                            mFabBackGround.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();

        }

    }

    void hideFab() {
        if (View.GONE == mFab.getVisibility() || View.INVISIBLE == mFab.getVisibility()) {
            return;
        }

        ViewCompat.animate(mFab)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(mInterpolator)
                .setStartDelay(100)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onAnimationEnd(View view) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        mFab.hide();
                        mFabBackGround.setVisibility(View.INVISIBLE);
                    }
                })
                .start();
    }


    private void initToolbarAndDrawer() {

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //final TextView aboutExam = (TextView) findViewById(R.id.nav_about_exam);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mCurrentExam.getShortName());
            actionBar.setSubtitle(mCurrentExam.getFullName());
        }
        //prepareShare();
    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


    private void showDeleteHistoryDialog() {
        new AlertDialog.Builder(this)
                //.setIcon(R.drawable.alert_dialog_icon)
                .setMessage("All your score, tags and remarks made on this exam will be deleted." +
                        "\nYour activity on other exams will not be affected.")
                .setPositiveButton(R.string.alert_dialog_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                doDeleteAttemptHistory();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel, null)
                .create()
                .show();
    }

    private void doDeleteAttemptHistory() {
        //start showing dialog
        UserAttemptSummary.getDatabaseReference().setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //hide dialog
                        UserAttempt.getDatabaseReference().setValue(null);
                        //may be we need to put the following in a call back
                        ApplicationManager.CurrentSession.setSelectedExam(null);
                        ApplicationManager.CurrentSession.setSelectedExamAttemptSummary(null);
                        MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Delete Successful", "All your score, tags and remarks made on the exam are deleted completely.");
                        finish();
                    }
                });

    }

    private void shareQuestion() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/html")
                .setHtmlText(mCurrentQuestion.getStatement())
                .setSubject("Definitely read this")//to make email app compatible
                //.addEmailTo(importantPersonEmailAddress)//to make email app compatible
                .getIntent();


        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }


    //----------------------

    int mFabCenterX;
    int mFabCenterY;
    float mFabDistanceFromRoot;
    float mFabRadius;

    private void onRevealFabClick(View view) {
        int x = (view.getLeft() + view.getRight()) / 2;
        int y = (view.getTop() + view.getBottom()) / 2;
        //
        float radiusOfFab = 1f * view.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, mRootLayout.getWidth() - x),
                Math.max(y, mRootLayout.getHeight() - y));
        mFabCenterX = x;
        mFabCenterY = y;
        mFabDistanceFromRoot = radiusFromFabToRoot;
        mFabRadius = radiusOfFab;
        if (view.isSelected()) {
            hideRevealMenu(x, y, radiusFromFabToRoot, radiusOfFab);
        } else {
            showRevealMenu(x, y, radiusOfFab, radiusFromFabToRoot);
        }
        view.setSelected(!view.isSelected());
    }

    View.OnClickListener mRevelItemCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mFab || view == mArcRevealFrame) {
                onRevealFabClick(mFab);
                return;
            }
            hideRevealMenu(mFabCenterX, mFabCenterY, mFabDistanceFromRoot, mFabRadius);//added for better
            mFab.setSelected(false);
            //
            if (view.getId() == R.id.fab_tags) {
                TagsBottomSheetDialog.createAndShow(QuestionActivity.this, mCurrentQuestion, mMainContent);
            } else if (view.getId() == R.id.fab_error) {
                ReportErrorBottomSheetDialog.createAndShow(QuestionActivity.this, mLoadToast, mMainContent);
            } else if (view.getId() == R.id.fab_share) {
                shareQuestion();
            } else if (view.getId() == R.id.fab_answer) {
                MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Answer :: ", " \n " + mCurrentQuestion.getCorrectAnswer() + ": " + getChoiceStatement(mCurrentQuestion.getCorrectAnswer()));
            } else if (view.getId() == R.id.fab_note) {
                RemarkBottomSheetDialog.createAndShow(QuestionActivity.this, mLoadToast, mMainContent);
            }

        }
    };


    private String getChoiceStatement(String choiceLetter) {
        if ("A".equals(choiceLetter)) {
            return mCurrentQuestion.getChoiceA();
        } else if ("B".equals(choiceLetter)) {
            return mCurrentQuestion.getChoiceB();
        } else if ("C".equals(choiceLetter)) {
            return mCurrentQuestion.getChoiceC();
        } else if ("D".equals(choiceLetter)) {
            return mCurrentQuestion.getChoiceD();
        } else
            return choiceLetter;
    }

    private void showRevealMenu(int cx, int cy, float startRadius, float endRadius) {
        mArcRevealFrame.setVisibility(View.VISIBLE);


        Animator revealAnim = createCircularReveal(mArcRevealFrame, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);

        List<Animator> childAnimList = new ArrayList<>();
        childAnimList.add(revealAnim);
        childAnimList.add(createShowItemAnimator(mCenterItem));
        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            childAnimList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(childAnimList);
        animSet.start();
        //
        ViewCompat.animate(mFab).
                rotation(135f).
                withLayer().
                setDuration(300).
                setInterpolator(overshootInterpolator).
                start();
    }

    private void hideRevealMenu(int cx, int cy, float startRadius, float endRadius) {
        List<Animator> animList = new ArrayList<>();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }

        animList.add(createHideItemAnimator(mCenterItem));

        Animator revealAnim = createCircularReveal(mArcRevealFrame, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mArcRevealFrame.setVisibility(View.INVISIBLE);
                if (mFab.getVisibility() == View.INVISIBLE || mFab.getVisibility() == View.GONE) {
                    showFab(mUserAttemptColor);
                }
            }
        });

        animList.add(revealAnim);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
        //
        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(mFab).
                rotation(0f).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();

    }

    private Animator createShowItemAnimator(View item) {
        float dx = mCenterItem.getX() - item.getX();
        float dy = mCenterItem.getY() - item.getY();

        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(50);
        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        final float dx = mCenterItem.getX() - item.getX();
        final float dy = mCenterItem.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(50);
        return anim;
    }

    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        return reveal;
    }


    ////--------start-yalantis-context-menu---------------------------------------------------------
    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager mMenuFragmentManager;
    //
    private int indexMenuClose = 0;
    private int indexMenuInstruction = 1;
    private int indexMenuAllQuestion = 2;
    private int indexMenuDeleteExam = 3;
    private int indexCreateShortCut = 4;
    private int indexMenuAboutExam = 5;

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(createMenuObjects());
        menuParams.setClosableOutside(true);
        menuParams.setAnimationDelay(48);
        menuParams.setAnimationDuration(48);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(mOnMenuItemClickListener);
    }

    private List<MenuObject> createMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();
        //
        MenuObject menuClose = new MenuObject();
        MenuObject menuInstruction = new MenuObject("Instruction");
        MenuObject menuAllQuestion = new MenuObject("Questions");
        MenuObject menuDeleteExam = new MenuObject("Delete history");
        MenuObject menuAddShortCut = new MenuObject("Add shortcut");
        MenuObject menuAboutExam = new MenuObject("About Exam");
        //
        menuClose.setResource(R.drawable.ic_close_24dp);
        menuInstruction.setResource(R.drawable.ic_info_outline_24dp);
        menuAllQuestion.setResource(R.drawable.ic_list_24dp);
        menuDeleteExam.setResource(R.drawable.ic_cloud_off_24dp);
        menuAddShortCut.setResource(R.drawable.ic_add_shortcut);
        menuAboutExam.setResource(R.drawable.ic_copyright_24dp);
        //
        menuObjects.add(menuClose);
        menuObjects.add(menuInstruction);
        menuObjects.add(menuAllQuestion);
        menuObjects.add(menuDeleteExam);
        menuObjects.add(menuAddShortCut);
        menuObjects.add(menuAboutExam);
        return menuObjects;
    }

    OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public void onMenuItemClick(View clickedView, int position) {
            if (position == indexMenuClose) {
                return;
            } else if (position == indexMenuAllQuestion) {
                openAttemptSummary(false);
            } else if (position == indexMenuDeleteExam) {
                showDeleteHistoryDialog();
            } else if (position == indexMenuAboutExam) {//about exam
                openAboutExam();
                //MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "About the exam", mCurrentExam.getAboutExam());
            } else if (position == indexCreateShortCut) {//about exam
                int subjectImageResource = GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject());
                addShortcut(mCurrentExam.getUid(), mCurrentExam.getShortName(), subjectImageResource);
            } else if (position == indexMenuInstruction) {
                if (mCurrentQuestion != null) {
                    String instructionUid = mCurrentQuestion.getInstructionCode();
                    if (instructionUid != null) {
                        showInstruction(instructionUid);
                    } else if (mCurrentQuestion.getInstructionCode() != null) {// && (mCurrentExam.getDefaultInstruction() == null)) {
                        MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Instruction", mCurrentExam.getDefaultInstruction());
                    } else {
                        MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Instruction", "Choose the correct answer from the give choices.");
                    }
                } else {
                    CustomView.makeSnackBar(mMainContent, getString(R.string.questions_not_loaded_yet), CustomView.SnackBarStyle.INFO).setAction("OK", null).show();
                }


            }
        }
    };

    private void addShortcut(String examUid, String examShortName, int iconDrawableId) {
        //
//        if (SettingsManager.isLauncherIconAdded(examUid)) {
//            return;
//        }
        Intent shortcutIntent = new Intent(getApplicationContext(), ExamHomeActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
//        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.putExtra(FirebaseModel.FIELD_UID, examUid);
        //shortcutIntent.setData(ContentUris.withAppendedId(BASE_URI, rowId));

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, examShortName);

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), iconDrawableId));
        addIntent.putExtra("duplicate", false);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
        SettingsManager.setLauncherIconAdded(examUid, true);
        CustomView.makeSnackBar(mMainContent, "Shortcut added to your home screen", CustomView.SnackBarStyle.SUCCESS).show();

    }

    private void showInstruction(String instructionUid) {

        final DatabaseReference instructionReference = Instruction.getDatabaseReference(mCurrentExam.getUid(), instructionUid);

        instructionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Instruction instruction = dataSnapshot.getValue(Instruction.class);
                if (instruction == null) {
                    return;
                }
                if (MessageBottomSheetDialog.isVisible()) {
                    MessageBottomSheetDialog.setBody(instruction.getStatement());
                } else {
                    MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Instruction", (instruction.getStatement()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.context_menu:
                if (mMenuFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(mMenuFragmentManager, ContextMenuDialogFragment.TAG);

                }
                break;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // //--------end-yalantis-menu


    public void saveQuestionBookMarks(final String bookMark) {

        final DatabaseReference bookMarkReference = UserAttempt.getDatabaseReference(mUserId, mCurrentExam.getUid(), mCurrentQuestion.getUid());

        bookMarkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);
                if (attempt == null) {
                    attempt = new UserAttempt();
                }
                attempt.setBookMark(bookMark);
                bookMarkReference.setValue(attempt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            CustomView.makeSnackBar(mMainContent, "Sorry, Your bookmark is not saved.\nAre you connected to internet?", CustomView.SnackBarStyle.WARNING).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
