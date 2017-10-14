package net.kerod.android.questionbank;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.FirebaseModel;
import net.kerod.android.questionbank.utility.GraphicsUtil;
import net.kerod.android.questionbank.widget.CustomView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExamHomeActivity extends AppCompatActivity {
    private Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();
    private static final String TAG = "ExamHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_home);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {//it is launched not from exam list but from subject icon,
            String examUid = getIntent().getExtras().getString(FirebaseModel.FIELD_UID);
            fetchExam(examUid);
            Log.e(TAG, "onCreate: EXAM UID :::: " + getIntent().getExtras().getString(FirebaseModel.FIELD_UID));
        } else if (mCurrentExam != null) {
            initComponents();
            initFab();
        }
    }

    void fetchExam(final String examUid) {
        final Query query = FirebaseDatabase.getInstance().getReference()
                .child(ExamListActivity.EXAM_LIST_NODE.toLowerCase())
                .child(examUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    mCurrentExam = dataSnapshot.getValue(Exam.class);
                    if (mCurrentExam != null) {
                        mCurrentExam.setUid(examUid);
                        ApplicationManager.CurrentSession.setSelectedExamWithSummary(mCurrentExam);
                        initComponents();
                        initFab();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //
    //Adding shortcut for This activity on Home screen
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
        CustomView.makeSnackBar(findViewById(R.id.content_exam_home), "Shortcut added to your home screen", CustomView.SnackBarStyle.SUCCESS).show();

    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fab) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(ExamHomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initComponents() {

        CircleImageView imgv = (CircleImageView) findViewById(R.id.cimg_subject_icon);
        imgv.setImageResource(GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject()));
        //
        Button btnnAllExams = (Button) findViewById(R.id.btnn_all_exams);
        btnnAllExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fab) {
                onBackPressed();
            }
        });
        //
        Button btnnAboutExam = (Button) findViewById(R.id.btnn_about_exam);
        btnnAboutExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fab) {

                Intent intent = new Intent(ExamHomeActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();

            }
        });

//        Button btnnAddShortcut = (Button) findViewById(R.id.btnn_add_shortcut);
//        btnnAddShortcut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View fab) {
//                int subjectImageResource = GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject());
//                addShortcut(mCurrentExam.getUid(), mCurrentExam.getShortName(), subjectImageResource);
//            }
//        });
        TextView txtvMainTitle = (TextView) findViewById(R.id.txtv_main_title);
        txtvMainTitle.setText(mCurrentExam.getShortName());
        TextView txtvSubTitle = (TextView) findViewById(R.id.txtv_sub_title);
        txtvSubTitle.setText(mCurrentExam.getFullName());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


}
