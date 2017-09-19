package net.kerod.android.questionbank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import net.kerod.android.questionbank.widget.ClipRevealFrame;
import net.kerod.android.questionbank.widget.CustomView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExamHomeActivity extends AppCompatActivity {
    private Exam mCurrentExam = ApplicationManager.CurrentSession.getSelectedExam();
    private static final String TAG = "ExamHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_home);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null  ) {//it is launched not from exam list but from subject icon,
            String examUid = getIntent().getExtras().getString(FirebaseModel.FIELD_UID);
            fetchExam(examUid);
            Log.e(TAG, "onCreate: EXAM UID :::: " + getIntent().getExtras().getString(FirebaseModel.FIELD_UID));
        } else if (mCurrentExam != null) {
            initToolbar();
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
                Log.e(TAG, "\n\n\n\n>>>>>>>onDataChange:query.getRef()  :::: " + query.getRef()
                        + "\n>>>> dataSnapshot       :: " + "" + dataSnapshot + "\n\n"
                        + "\n>>>> dataSnapshotValue  :: " + "" + dataSnapshot.getValue() + "\n\n"
                );
                try {
                    mCurrentExam = dataSnapshot.getValue(Exam.class);
                    if (mCurrentExam != null) {
                        mCurrentExam.setUid(examUid);
                        ApplicationManager.CurrentSession.setSelectedExamWithSummary(mCurrentExam);
                        initComponents();
                        initFab();
                    } else {
                        Log.e(TAG, "onDataChange:\n\n NULLLLLLLLLLLLLLLLLLLLLLLLLLLLL ");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "\n\n\n\n\n------------\n\n\n\n\n EXCEPTION ::::  " + e + "\n\n\n\n\n");
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
                } else {
                    showRevealMenu(fab);
                }
            }
        });
    }

    private void initComponents() {

        CircleImageView imgv = (CircleImageView) findViewById(R.id.cimg_subject_icon);
        imgv.setImageResource(GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject()));
        //
        Button btnnAboutExam = (Button) findViewById(R.id.btnn_about_exam);
        btnnAboutExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fab) {
                Intent intent = new Intent(ExamHomeActivity.this, AboutExamActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Button btnnAddShortcut = (Button) findViewById(R.id.btnn_add_shortcut);
        btnnAddShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fab) {
                int subjectImageResource = GraphicsUtil.getImageResourceForSubject(mCurrentExam.getSubject());
                addShortcut(mCurrentExam.getUid(), mCurrentExam.getShortName(), subjectImageResource);
            }
        });
        TextView txtvTitle = (TextView) findViewById(R.id.txtv_main_title);
        txtvTitle.setText(mCurrentExam.getShortName());
    }


    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(mCurrentExam.getShortName());
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


    //-----------


    private void showRevealMenu(View fab) {
        RelativeLayout mRootLayout = (RelativeLayout) findViewById(R.id.content_exam_home);
        ClipRevealFrame mArcRevealFrame = (ClipRevealFrame) findViewById(R.id.arc_reveal_frame);
        mArcRevealFrame.setVisibility(View.VISIBLE);

        int cx = (fab.getLeft() + fab.getRight()) / 2;
        int cy = (fab.getTop() + fab.getBottom()) / 2;
        //
        float radiusOfFab = 1f * fab.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(Math.max(cx, mRootLayout.getWidth() - cx), Math.max(cy, mRootLayout.getHeight() - cy));
        Animator revealAnim = createCircularReveal(mArcRevealFrame, cx, cy, radiusOfFab, radiusFromFabToRoot);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(ExamHomeActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        revealAnim.start();
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


//
//    private void startQuizActivityWithTransition(Activity activity, View toolbar ) {
//
//        final Pair[] pairs =  createSafeTransitionParticipants(activity, false, new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
//        @SuppressWarnings("unchecked")
//        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat .makeSceneTransitionAnimation(activity, pairs);
//
//        // Start the activity with the participants, animating from one to the other.
//        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
//        Intent startIntent = new Intent(this, QuestionActivity.class);
//        ActivityCompat.startActivity(activity,   startIntent,    transitionBundle);
//    }
//
//
//    public static Pair<View, String>[] createSafeTransitionParticipants(@NonNull Activity activity,
//                                                                        boolean includeStatusBar,
//                                                                        @Nullable Pair... otherParticipants) {
//        // Avoid system UI glitches as described here:
//        // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
//        View decor = activity.getWindow().getDecorView();
//        View statusBar = null;
//        if (includeStatusBar) {
//            statusBar = decor.findViewById(android.R.id.statusBarBackground);
//        }
//        View navBar = decor.findViewById(android.R.id.navigationBarBackground);
//
//        // Create pair of transition participants.
//        List<Pair> participants = new ArrayList<>(3);
////        addNonNullViewToTransitionParticipants(statusBar, participants);
////        addNonNullViewToTransitionParticipants(navBar, participants);
//        // only add transition participants if there's at least one none-null element
//        if (otherParticipants != null && !(otherParticipants.length == 1
//                && otherParticipants[0] == null)) {
//            participants.addAll(Arrays.asList(otherParticipants));
//        }
//        //noinspection unchecked
//        return participants.toArray(new Pair[participants.size()]);
//    }
}
