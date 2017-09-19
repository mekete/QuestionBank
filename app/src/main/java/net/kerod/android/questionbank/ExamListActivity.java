package net.kerod.android.questionbank;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import net.kerod.android.questionbank.model.Exam;
import net.steamcrafted.loadtoast.LoadToast;

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
        initToolbar();
        initFab();
        initLoadToast();
        initRecyclerView();
    }

    private void initLoadToast() {
        mLoadToast = new LoadToast(this)
                .setText("Downloading...")
                .setTranslationY(200)
                .setTextColor(ContextCompat.getColor(ExamListActivity.this, R.color.colorPrimaryDark))
                .setBackgroundColor(ContextCompat.getColor(ExamListActivity.this, R.color.colorAccent))
                .setProgressColor(ContextCompat.getColor(ExamListActivity.this, R.color.colorPrimary));
    }

    // -------------------------- ------------------------------ -----------------
    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //Intent intent = new Intent(ExamListActivity.this, EditAccountActivity.class);
                Intent intent = new Intent(ExamListActivity.this, TestIntroActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(ExamListActivity.this, TempIntroActivity.class);
                startActivity(intent);
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
//                        String classGroup = SettingManager.getClassGroup();
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

}
