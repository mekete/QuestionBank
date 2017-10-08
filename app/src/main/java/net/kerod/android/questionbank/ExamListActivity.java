package net.kerod.android.questionbank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.adapter.ExamAdapter;
import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.widget.CustomView;
import net.kerod.android.questionbank.widget.toast.LoadToast;

import java.util.ArrayList;
import java.util.List;

public class ExamListActivity extends AppCompatActivity {
    private static final String TAG = "ExamListActivity";
    public static final String EXAM_LIST_NODE = "examList";
    private LoadToast mLoadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        mLoadToast = LoadToast.createLoadToast(this, getString(R.string.loading));
        initToolbar();
        initFab();
        initRecyclerView();
        setUpApp();
    }


    private void setUpApp() {
        if (SettingsManager.isFirstTimeLaunch()) {
            //do some db thing!
            //showWelComeDialog();//if so, call showIntroActivity() on dialog dismmiss
            showIntroActivity();
            SettingsManager.setVersionCode(BuildConfig.VERSION_CODE);
            SettingsManager.setFirstTimeLaunch(false);
        } else if (SettingsManager.getVersionCode() != BuildConfig.VERSION_CODE) {
            //show something new introduced
            SettingsManager.setVersionCode(BuildConfig.VERSION_CODE);
        } else if (!SettingsManager.isAgreedTermsOfService()) {
            showWelComeDialog();
        } else {
            checkForUpdate();
        }
    }

    public void showIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_via_top);
    }

    public void showWelComeDialog() {

//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View parent = getLayoutInflater().inflate(R.layout.dialog_welcome, null);
////        CheckBox ckbxConfirm = (CheckBox) parent.findViewById(R.id.ckbx_confirm);
//        builder.setView(parent);
//        builder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //If you want the checkbox to be visible,  comment the below code
//                // and re-implemented after showing!
//                SettingsManager.setAgreedTermsOfService(true);
//            }
//        });
//        builder.setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                CustomView.makeToast(HomeActivity.this, getString(R.string.you_must_agree_on_tos_to_use_the_app), CustomView.SnackBarStyle.ERROR).show();
//                finish();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.show();

    }

    private void checkForUpdate() {
        //
    }

    // -------------------------- ------------------------------ -----------------
    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //Intent intent = new Intent(ExamListActivity.this, EditAccountActivity.class);
//                Intent intent = new Intent(ExamListActivity.this, TestIntroActivity.class);
//                startActivity(intent);
            }
        });

        FloatingActionButton fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Intent intent = new Intent(ExamListActivity.this, TempIntroActivity.class);
//                startActivity(intent);
            }
        });
        FloatingActionButton fabScore = (FloatingActionButton) findViewById(R.id.fab_score);
        fabScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(ExamListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Question Bank");
            actionBar.setSubtitle("Available Exams");
        }
    }


    private void initRecyclerView() {
        mLoadToast.setText("Loading exams...");
        mLoadToast.show();
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recv_exam);
//        Query query = FirebaseDatabase.getInstance().getReference()
//                .child(EXAM_LIST_NODE.toLowerCase())
//                .orderByKey();
//        ExamAdapter adapter = new ExamAdapter(Exam.class, R.layout.adapter_exam, ExamAdapter.ViewHolder.class, query);
//        recyclerView.setAdapter(adapter);
        final List<Exam> mExamList = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recv_exam);
        final Query query = FirebaseDatabase.getInstance().getReference()
                .child(EXAM_LIST_NODE.toLowerCase())
                //.orderByKey()
                .orderByChild(Exam.COLUMN_SHORT_NAME);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLoadToast.success();
                mExamList.clear();

                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange:query.getRef()  :::: " + query.getRef());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.e(TAG, "\n\n\n\n\n------------ current.getKey() current: " + current);
//                    Log.e(TAG, "\n\n\n\n\n------------ current.getKey() getKey: " + current.getKey());
                    try {
                        Exam exam = snapshot.getValue(Exam.class);
                        exam.setUid(snapshot.getKey());
                        //
//                        String classGroup = SettingsManager.getClassGroup();
//                        if (!Constants.CLASS_UNDEFINED.equals(classGroup)) {
//                            if (
//                                    (Constants.CLASS_HIGH_SCHOOL.equals(classGroup)  && classGroup.equals(exam.getGrade())) ||
//                                    (Constants.CLASS_PREP_NATURAL.equals(classGroup) && classGroup.equals(exam.getGrade())) ||
//                                    (Constants.CLASS_PREP_SOCIAL.equals(classGroup)  && classGroup.equals(exam.getGrade()))
//                                    ) {
//                                mExamList.add(exam);
//                            }
//                        } else {
//                            mExamList.add(exam);
//                        }
                        mExamList.add(exam);

                    } catch (Exception e) {
                        Log.e(TAG, "\n\n\n\n\n------------ EXCEPTION ::::  " + e);
                        e.printStackTrace();
                    }
                }
                ExamAdapter adapter = new ExamAdapter(ExamListActivity.this, mExamList);
                recyclerView.setAdapter(adapter);
                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange: mQuestionList.size() :::: " + mExamList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLoadToast.error();
            }
        });

        Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange:query.getRef()  :::: " + query.getRef());

    }

    //
    private static final int REQUEST_CATEGORY = 0x2300;//to be used onActivityResult

    //    private void startQuizActivityWithTransition(Activity activity, View toolbar) {
//
//        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,  new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
//        @SuppressWarnings("unchecked")
//        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat   .makeSceneTransitionAnimation(activity, pairs);
//
//        // Start the activity with the participants, animating from one to the other.
//        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
//        Intent startIntent = null;//QuizActivity.getStartIntent(activity, category);
//        ActivityCompat.startActivityForResult(activity,
//                startIntent,
//                REQUEST_CATEGORY,
//                transitionBundle);
//    }
    boolean backButtonPressedBefore = false;

    @Override
    public void onBackPressed() {

        if (backButtonPressedBefore) {
            finish();
            super.onBackPressed();
        } else {
            this.backButtonPressedBefore = true;
            //Toast.makeText(HomeActivity.this, "Click again to exit", Toast.LENGTH_SHORT).show();
            CustomView.makeSnackBar(findViewById(R.id.main_content), getString(R.string.click_again_to_exit), CustomView.SnackBarStyle.INFO_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressedBefore = false;
                }
            }, 2000);
        }
    }
}
