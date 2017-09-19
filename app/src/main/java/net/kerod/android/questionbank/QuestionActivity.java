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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import net.kerod.android.questionbank.manager.SettingManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.Instruction;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.model.UserAttemptSummary;
import net.kerod.android.questionbank.utility.Constants;
import net.kerod.android.questionbank.widget.AnimatorUtils;
import net.kerod.android.questionbank.widget.ClipRevealFrame;
import net.kerod.android.questionbank.widget.CustomView;
import net.kerod.android.questionbank.widget.MessageBottomSheetDialog;
import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kexanie.library.MathView;
import se.emilsjolander.flipview.FlipView;
import se.emilsjolander.flipview.OverFlipMode;

import static net.kerod.android.questionbank.R.id.fab;

public class QuestionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int VIBRATION_DURATION_IN_MILLS = 200;
    private static final String TAG = "ShowQuestionActivity";
    private static final String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static final String TAG_SELECTED_QUESTION = "selectedQuestion";
    private Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();
    private UserAttemptSummary mAttemptSummary = ApplicationManager.CurrentSession.getSelectedExamAttemptSummary();
    private final DatabaseReference mAttemptDatabaseReference = UserAttempt.getDatabaseReference(mUserId, mCurrentExam.getUid());
    //
    private DrawerLayout mDrawer;
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
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private boolean mCurrentQuestionAttempted = false;
    private long mExamPrevTimeTaken = 0;
    private boolean mFinishedLoadingQuestions = false;
    private LoadToast mLoadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mInterpolator = new FastOutSlowInInterpolator();
        mMenuFragmentManager = getSupportFragmentManager();
        //
        initLoadToast();
        initProgress();
        initToolbarAndDrawer();
        initArcLayout();
        initFlipView();
        initMenuFragment();
        showLoggedInUserProfile();
        //

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
            saveExamSummary();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int selectedQuestion = intent.getIntExtra(TAG_SELECTED_QUESTION, -1);
        Log.e(TAG, "initFlipView:>>>>>>>>>\n>>>>>>>> selectedQuestion :::: " + selectedQuestion);
        if (selectedQuestion > 0 && mFlipView != null && mQuestionList != null) {
            mFlipView.flipTo(selectedQuestion - 1);
        } else if (mAttemptSummary != null && mAttemptSummary.getLastOpenedQuestionIndex() > 0 && mFlipView != null) {
            Log.e(TAG, "\n\n\n>>>>>>\nflipToLastSeenQuestion opening  setLastOpenedQuestionIndex: " + (mAttemptSummary.getLastOpenedQuestionIndex()));

            mFlipView.flipTo(mAttemptSummary.getLastOpenedQuestionIndex());
            Log.e(TAG, "startTimer:mAttemptSummary not null but time is null null ????  " + mAttemptSummary);
        } else {
            Log.e(TAG, "\n\n\n\n>>>>>>QQQQstartTimer:mAttemptSummary null ????  " + mAttemptSummary);
        }
    }

    protected void startTimer() {
        if (mAttemptSummary != null && mAttemptSummary.getTotalTimeUsed() != null) {
            mExamPrevTimeTaken = mAttemptSummary.getTotalTimeUsed();
            Log.e(TAG, "startTimer:mAttemptSummary \n11111C COOOOOOOOOOOOOOOOOOOOOOOOOL  " + mAttemptSummary.getTotalTimeUsed());
        } else {
            // Log.e(TAG, "startTimer:mAttemptSummary \n22222C NOT ---- COOOOOOOOOOOOOOOOL  " + mAttemptSummary.getTotalTimeUsed());

        }

        mStartTimeStamp = SystemClock.elapsedRealtime() - mExamPrevTimeTaken;
        mCronExamTimeTaken.setBase(mStartTimeStamp);
        mCronExamTimeTaken.start();
    }

    private void initLoadToast() {
        mLoadToast = new LoadToast(this)
                .setText("Sending...")
                .setTranslationY(200)
                .setTextColor(ContextCompat.getColor(QuestionActivity.this,R.color.colorPrimaryDark))
                .setBackgroundColor(ContextCompat.getColor(QuestionActivity.this,R.color.colorAccent))
                .setProgressColor(ContextCompat.getColor(QuestionActivity.this,R.color.colorPrimary));
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
                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange:query.getRef()  :::: " + query.getRef());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e(TAG, "\n\n\n\n\n------------ current.getKey() current: " + current);
                    //Log.e(TAG, "\n\n\n\n\n------------ current.getKey() getKey: " + current.getKey());
                    try {
                        Question question = snapshot.getValue(Question.class);
                        question.setUid(snapshot.getKey());
                        mQuestionList.add(question);
                    } catch (Exception e) {
                        Log.e(TAG, "\n\n\n\n\n------------ EXCEPTION ::::  " + e);
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange: mQuestionList.size() :::: " + mQuestionList.size());
                if (mQuestionList.size() > 0) {
                    Collections.sort(mQuestionList);
                    mCurrentQuestion = mQuestionList.get(0);
                    mProgressQuestion.setText("1/" + mQuestionList.size());
                    mCurrentQuestion = mQuestionList.get(0);

                }
                mAdapter.notifyDataSetChanged();
                flipToLastSeenQuestion();
                showFabForHistory(mUserId, mCurrentQuestion);
                startTimer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLoadToast.error();
            }
        });
        //

    }

    private void openAttemptSummary() {
        Intent intent = new Intent(QuestionActivity.this, AttemptSummaryActivity.class);
        startActivity(intent);
    }


    private void openAboutExam() {
        Intent intent = new Intent(QuestionActivity.this, AboutExamActivity.class);
        startActivity(intent);
    }

    private void initFlipView() {
        mFlipView = (FlipView) findViewById(R.id.flip_view);
        mFlipView.setOnFlipListener(mOnFlipListener);
        mFlipView.peakNext(false);
        mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        //
        mAdapter = new QuestionAdapter(this, mQuestionList);
        mAdapter.setAttemptCallback(mAttemptCallback);
        mFlipView.setAdapter(mAdapter);
    }

    private void flipToLastSeenQuestion() {
        int selectedQuestion = getIntent().getIntExtra(TAG_SELECTED_QUESTION, -1);
        Log.e(TAG, "initFlipView:>>>>>>>>>\n>>>>>>>> selectedQuestion :::: " + selectedQuestion);
        if (selectedQuestion > 0 && mFlipView != null && mQuestionList != null) {
            mFlipView.flipTo(selectedQuestion - 1);
        } else if (mAttemptSummary != null && mAttemptSummary.getLastOpenedQuestionIndex() > 0 && mFlipView != null) {
            Log.e(TAG, "\n\n\n>>>>>>\nflipToLastSeenQuestion opening  setLastOpenedQuestionIndex: " + (mAttemptSummary.getLastOpenedQuestionIndex()));

            mFlipView.flipTo(mAttemptSummary.getLastOpenedQuestionIndex());
            Log.e(TAG, "startTimer:mAttemptSummary not null but time is null null ????  " + mAttemptSummary);
        } else {
            Log.e(TAG, "\n\n\n\n>>>>>>QQQQstartTimer:mAttemptSummary null ????  " + mAttemptSummary);
        }
    }


    private FlipView.OnFlipListener mOnFlipListener = new FlipView.OnFlipListener() {
        @Override
        public void onFlippedToPage(FlipView v, int position, long id) {
            mCurrentQuestion = mQuestionList.get(position);
            Log.e(TAG, ">>>>>>> On page flip Page:>>>>  " + position + "   " + " Question Number ::: " + mCurrentQuestion.getQuestionNumber());
            Log.e(TAG, "mOnFlipListener ::: mCurrentQuestion.getCorrectAnswer()::: " + mCurrentQuestion.getCorrectAnswer());

            showFabForHistory(mUserId, mCurrentQuestion);
            updateProgress();
            saveExamSummary();
            remindToReadInstruction(v, mCurrentQuestion);


        }
    };

    private void remindToReadInstruction(View v, final Question currentQuestion) {

        try {
            if(currentQuestion.getQuestionNumber()>25 && currentQuestion.getQuestionNumber()<30 ){
                Log.e(TAG, "START OF SECTION ::: : "+currentQuestion.getQuestionNumber()+"\n:::: "+currentQuestion.getStartOfSection() );
            }
            if (currentQuestion.getQuestionNumber()==12 || currentQuestion.getStartOfSection()) {
                CustomView.makeSnackbar(v, "Read instruction for this question.", Snackbar.LENGTH_LONG).setAction("Read now", new View.OnClickListener() {
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
            Log.e(TAG, "\n\n>>>>>>>>\nremindToReadInstruction: "+e );
            e.printStackTrace();
        }
    }


    private QuestionAdapter.AttemptCallback mAttemptCallback = new QuestionAdapter.AttemptCallback() {

        @Override
        public void onAttempt(final MathView[] webViewArray, final int clickedIndex) {
            Log.e(TAG, "\n\n\n\n\nonTou attempt Callback: GGGGGGGGGGG :::::");
            //colorize it only if it is not attempted previously (first time attempt). Else, already colored on page flipped
            if (!mCurrentQuestionAttempted) {
                showFabAndAttemptForAction(webViewArray, clickedIndex, mUserId, mCurrentQuestion);
            }
        }
    };


    private void showFabAndAttemptForAction(MathView[] webViewArray, int clickedIndex, final String userUid, Question selectedQuestion) {
        mCurrentQuestionAttempted = true; //clicking a choice will not have effect for the current question afterwards
        //
        boolean correctAttempt = (Question.getChoiceLetterIndex(mCurrentQuestion.getCorrectAnswer()) == clickedIndex);
        int color = correctAttempt ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT;
        colorizeAttempt(webViewArray, clickedIndex, correctAttempt);
        showFab(color);
        if(SettingManager.isVibrateOnWrongAttempt() && !correctAttempt){
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(VIBRATION_DURATION_IN_MILLS);
        }
        //
        saveUserAction(webViewArray[clickedIndex], clickedIndex, correctAttempt, null, null, null);

    }

    private void showFabForHistory(final String userUid, Question selectedQuestion) {
        mCurrentQuestionAttempted = false;
        Query query = UserAttempt.getDatabaseReference(userUid, selectedQuestion.getExamUid(), selectedQuestion.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);

                if (attempt != null && mCurrentQuestion.getUid().equals(dataSnapshot.getKey())) {
                    mCurrentQuestionAttempted = true;
                    int color = attempt.getScore() ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT;
                    showFab(color);
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

    public void saveUserAction(final View view, final Integer attemptChoiceIndex, final Boolean scoreCorrect, final Boolean markedFavorite, final Boolean markedVague, final String remark) {
        Log.e(TAG, ">>>>>>> aaaa Check Attempr: ");
        final DatabaseReference attemptReference = mAttemptDatabaseReference.child(mCurrentQuestion.getUid());
        attemptReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, ">>>>>>> bbb Check Attempr: ");
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);
                if (attempt == null) {
                    Log.e(TAG, ">>>>>>> ccc Check Attempr: ");
                    if (attemptChoiceIndex == null) {
                        Log.e(TAG, ">>>>>>> ddd Check Attempr: ");
                        return;//we have to save progress only after we save the score of the user
                    } else {
                        attempt = new UserAttempt();
                        attempt.setAttemptChoiceIndex(attemptChoiceIndex);
                        attempt.setQuestionNumber(mCurrentQuestion.getQuestionNumber());
                    }
                    Log.e(TAG, ">>>>>>> eee Check Attempr: ");

                }
                // ---------------------------------------------------------------------------------


                if (scoreCorrect != null) {
                    Log.e(TAG, ">>>>>>> iii Check Attempr: ");
                    attempt.setScore(scoreCorrect);
                    attempt.setAttemptScoreColor(scoreCorrect ? Constants.COLOR_CHOICE_BACKGROUND_CORRECT : Constants.COLOR_CHOICE_BACKGROUND_INCORRECT);
                }
                if (markedFavorite != null) {
                    Log.e(TAG, ">>>>>>> jjj Check Attempr: ");
                    attempt.setMarkedAsFavorite(markedFavorite);
                    CustomView.makeSnackbar(view, "The question is tagged as favorite.", Snackbar.LENGTH_LONG).show();
                }
                if (markedVague != null) {
                    Log.e(TAG, ">>>>>>> kkk Check Attempr: ");
                    attempt.setMarkedAsError(markedVague);
                    //Snackbar.make(view, "The question is tagged as vague.", Snackbar.LENGTH_LONG).show();
                    CustomView.makeSnackbar(view, "The question is tagged as vague.", Snackbar.LENGTH_LONG).show();
                }
                if (remark != null) {
                    Log.e(TAG, ">>>>>>> mmm Check Attempr: ");
                    attempt.setRemark(remark);
                }
                Log.e(TAG, ">>>>>>> nnn Check Attempr: ");
                attemptReference.setValue(attempt);
                //Log.e(TAG, "\n\n\n\n\n------------ current.getKey() onDataChange: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, ">>>>>>> ooo Check Attempr: ");
            }
        });

    }


    private void colorizeAttempt(MathView[] webViewArray, int attemptedChoiceIndex, boolean correctAttempt) {
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
        mPrgbExamQuestionsProgress = (ProgressBar) findViewById(R.id.prog_question);
        mCronExamTimeTaken = (Chronometer) findViewById(R.id.txtv_time_used);
        //
        mProgressQuestion = (TextView) findViewById(R.id.txtv_progress_count);
        mPrgbExamQuestionsProgress.setProgress(1);
        mProgressQuestion.setText("1/" + mQuestionList.size());
        mProgressQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAttemptSummary();
            }
        });
        mCronExamTimeTaken.setText("00:00");
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
        mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mArcRevealFrame = (ClipRevealFrame) findViewById(R.id.arc_reveal_frame);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        mCenterItem = findViewById(R.id.center_item);

        mCenterItem.setOnClickListener(mRevelItemCLickListener);
        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(mRevelItemCLickListener);
        }
        mFab = (FloatingActionButton) findViewById(fab);
        mFabBackGround = (TextView) findViewById(R.id.fab_bg);
        mFab.setOnClickListener(mRevelItemCLickListener);
        mArcRevealFrame.setOnClickListener(mRevelItemCLickListener);
    }

    private void showFab(int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
        //mFab.setRippleColor();BackgroundTintList(ColorStateList.valueOf(color));
        mFabBackGround.setVisibility(View.VISIBLE);
        mFab.setVisibility(View.VISIBLE);
        //mFab.setBackgroundTintList(new ColorStateList());
        mFab.setScaleX(0f);
        mFab.setScaleY(0f);
        ViewCompat.animate(mFab)
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(mInterpolator)
                .setListener(null)
                .start();
    }

    void hideFab() {
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
                        mFab.setVisibility(View.GONE);
                        mFabBackGround.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private void initToolbarAndDrawer() {

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //final TextView aboutExam = (TextView) findViewById(R.id.nav_about_exam);
        final TextView setting = (TextView) findViewById(R.id.nav_setting);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mCurrentExam.getShortName());
            actionBar.setSubtitle(mCurrentExam.getFullName());
        }
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        {
//            @Override
//            public void onDrawerOpened(View drawerView) {
////                 setting.bringToFront();
////                aboutExam.bringToFront();
//                mDrawer.requestLayout();
//            }
//        }
                ;


        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                Log.e(TAG, "onClick: " + "VVVVVVVVVVV----------------");
                Intent intent = new Intent(QuestionActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        //

        //

        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_share) {
//            // Handle the camera action
//        } else
        if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_account) {
            navigateToEditAccount();
            //FirebaseAuth.getInstance().signOut();
            finish();
        } else if (id == R.id.nav_all) {
            openAttemptSummary();
        } else {
            mDrawer.closeDrawer(GravityCompat.START);
            return false;
        }
        //setting and contact us are not handled here!
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //
    private void showLoggedInUserProfile() {

        View headerView = mNavigationView.inflateHeaderView(R.layout.drawer_question_header);
        CircleImageView imgvUserProfile = (CircleImageView) headerView.findViewById(R.id.imgv_user_photo);
         imgvUserProfile.setImageResource(Constants.AVATAR_RESOURCE_IDS[SettingManager.getAvatarIndex()] );

        TextView txtvUserName = (TextView) headerView.findViewById(R.id.txtv_user_name);
        TextView txtvUserEmail = (TextView) headerView.findViewById(R.id.txtv_user_email);
        txtvUserName.setText(SettingManager.getDisplayName());
        txtvUserEmail.setText(SettingManager.getEmail());
        //
        imgvUserProfile.setOnClickListener(actionEDitProfile );
        txtvUserName.setOnClickListener(actionEDitProfile );
        txtvUserEmail.setOnClickListener(actionEDitProfile );

    }

    public void launchWebView(String url, String title) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(Constants.INTENT_TITLE, title);
        intent.putExtra(Constants.INTENT_URL, url);
        startActivity(intent);
    }


    View.OnClickListener actionEDitProfile=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawer.closeDrawer(GravityCompat.START);
            navigateToEditAccount();
        }
    };

    private void navigateToEditAccount() {
        Intent intent = new Intent(QuestionActivity.this, EditAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
//    private   void shareQuestion( ){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, mCurrentQuestion.getStatement());
//            sendIntent.setType("text/plain");
//            this.startActivity(sendIntent);
//        }else {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/*");
//            intent.putExtra(Intent.EXTRA_TEXT, "Content");
////            BottomSheet share = BottomSheet.createShareBottomSheet(MainActivity.this, intent, "Title");
////            share.show();
//        }
//    }


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
            Log.e(TAG, "\nAAAAAA 222 hideRevealMenu ");
            hideRevealMenu(x, y, radiusFromFabToRoot, radiusOfFab);
        } else {
            Log.e(TAG, "\nAAAAAA 111 showRevealMenu ");
            showRevealMenu(x, y, radiusOfFab, radiusFromFabToRoot);
        }
        view.setSelected(!view.isSelected());
    }

    View.OnClickListener mRevelItemCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick: " + "0000 mRevelItemCLickListener");
            if (view == mFab || view == mArcRevealFrame) {
                Log.e(TAG, "onClick: " + "1111 mRevelItemCLickListener");
                onRevealFabClick(mFab);
                return;
            }
            Log.e(TAG, "onClick: " + "2222 mRevelItemCLickListener");
            hideRevealMenu(mFabCenterX, mFabCenterY, mFabDistanceFromRoot, mFabRadius);//added for better

            if (view.getId() == R.id.fab_favorite) {
                Log.e(TAG, "onClick: " + "3333 mRevelItemCLickListener");
                saveUserAction(view, null, null, true, null, null);
            } else if (view.getId() == R.id.fab_error) {
                Log.e(TAG, "onClick: " + "4444 mRevelItemCLickListener");
                saveUserAction(view, null, null, null, true, null);
            } else if (view.getId() == R.id.fab_share) {
                Log.e(TAG, "onClick: " + "55555 mRevelItemCLickListener");
                shareQuestion();
            } else if (view.getId() == R.id.fab_answer) {
                Log.e(TAG, "onClick: " + "6666 mRevelItemCLickListener");
                Log.e(TAG, "onClick: mCurrentQuestion.getCorrectAnswer()::: " + mCurrentQuestion.getCorrectAnswer());
                MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Answer :: ", " \n " + mCurrentQuestion.getCorrectAnswer() + ": " + getChoiceStatement(mCurrentQuestion.getCorrectAnswer()));
            } else if (view.getId() == R.id.fab_note) {
                Log.e(TAG, "onClick: " + "7777 mRevelItemCLickListener");
//                final DatabaseReference attemptReference = mAttemptDatabaseReference.child(mCurrentQuestion.getUid());
//                NoteBottomSheetDialog.createAndShow(attemptReference, QuestionActivity.this, view, "   ");
                //
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
        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(mFab).
                rotation(135f).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
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
    private int indexMenuAboutExam = 4;

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
        MenuObject menuAboutExam = new MenuObject("About Exam");
        //
        menuClose.setResource(R.drawable.ic_close_24dp);
        menuInstruction.setResource(R.drawable.ic_info_outline_24dp);
        menuAllQuestion.setResource(R.drawable.ic_list_24dp);
        menuDeleteExam.setResource(R.drawable.ic_cloud_off_24dp);
        menuAboutExam.setResource(R.drawable.ic_copyright_24dp);
        //
        menuObjects.add(menuClose);
        menuObjects.add(menuInstruction);
        menuObjects.add(menuAllQuestion);
        menuObjects.add(menuDeleteExam);
        menuObjects.add(menuAboutExam);
        return menuObjects;
    }

    OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public void onMenuItemClick(View clickedView, int position) {
            if (position == indexMenuClose) {
                return;
            } else if (position == indexMenuAllQuestion) {
                openAttemptSummary();
            } else if (position == indexMenuDeleteExam) {
                showDeleteHistoryDialog();
            } else if (position == indexMenuAboutExam) {//about exam
                openAboutExam();
                //MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "About the exam", mCurrentExam.getAboutExam());
            } else if (position == indexMenuInstruction) {
                String instructionUid = mCurrentQuestion.getInstructionCode();
                if (instructionUid != null) {
                    showInstruction(instructionUid);
                } else if (mCurrentQuestion.getInstructionCode() != null) {// && (mCurrentExam.getDefaultInstruction() == null)) {
                    MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Instruction", mCurrentExam.getDefaultInstruction());
                } else {
                    MessageBottomSheetDialog.createAndShow(QuestionActivity.this, "Instruction", "Choose the correct answer from the give choices.");
                }

            }
        }
    };

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

        }
        return super.onOptionsItemSelected(item);
    }

    // //--------end-yalantis-menu

}
