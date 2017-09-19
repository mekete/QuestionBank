package net.kerod.android.questionbank.manager;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.Instruction;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttemptSummary;


public class ApplicationManager extends Application {

    private static Context mContext;
    private static final String TAG = "ApplicationManager";

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());//
        //


    }

    public static class CurrentSession {
        private static Question selectedQuestion;
        private static Instruction selectedInstruction;
        private static Exam selectedExam;
        private static UserAttemptSummary selectedExamAttemptSummary;

        //


        public static Question getSelectedQuestion() {
            return selectedQuestion;
        }

        public static void setSelectedQuestion(Question selectedQuestion) {

            CurrentSession.selectedQuestion = selectedQuestion;
        }

        public static Instruction getSelectedInstruction() {
            return selectedInstruction;
        }

        public static void setSelectedInstruction(Instruction selectedInstruction) {
            CurrentSession.selectedInstruction = selectedInstruction;
        }

        public static Exam getSelectedExam() {
            return selectedExam;
        }

        public static void setSelectedExam(Exam selectedExam) {
            CurrentSession.selectedExam = selectedExam;
        }
        public static void setSelectedExamWithSummary(Exam selectedExam) {
            CurrentSession.selectedExam = selectedExam;
            CurrentSession.selectedExamAttemptSummary=null;//we need to make sure that prev data is cleared
            //
            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadUserAttemptSummary(userUid,   selectedExam);
        }

        public static UserAttemptSummary getSelectedExamAttemptSummary() {
            return CurrentSession.selectedExamAttemptSummary;
        }

        public static void setSelectedExamAttemptSummary(UserAttemptSummary selectedExamAttemptSummary) {
            CurrentSession.selectedExamAttemptSummary = selectedExamAttemptSummary;
        }

        public static String getUserUid() {
            return "abebe";
        }
        private static  void loadUserAttemptSummary(String userUid, final Exam selectedExam){
            Query query = UserAttemptSummary.getDatabaseReference(userUid, selectedExam.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   selectedExamAttemptSummary = dataSnapshot.getValue(UserAttemptSummary.class);
                    if(selectedExamAttemptSummary==null){
                        selectedExamAttemptSummary=new UserAttemptSummary();
                        selectedExamAttemptSummary.setTotalCount(selectedExam.getNumberOfQuestions());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




}