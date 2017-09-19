package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class UserNote extends FirebaseModel {

    public static final String TAG = "UserNote";
    //
    private String examShortName;
    private String examUid;
    private String questionUid;
    private Integer questionNumber;
    private String questionStatement;
    private String userRemark;
    private Long timeStamp;
    //
    public UserNote() { }
    //
    public static DatabaseReference getDatabaseReference(String userUid, String examUid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(TAG.toLowerCase())
                .child(userUid)
                .child(examUid)
                .child("notes");
    }

//    public static DatabaseReference getDatabaseReference() {
//        return getDatabaseReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), ApplicationManager.CurrentSession.getSelectedExam().getUid())
//                ;
//    }
//
//    public static DatabaseReference getDatabaseReference(String userUid, String examUid, String questionUid) {
//        return getDatabaseReference(userUid, examUid)
//                .child(questionUid);
//    }
    //

    public String getExamShortName() {
        return examShortName;
    }

    public void setExamShortName(String examShortName) {
        this.examShortName = examShortName;
    }

    public String getQuestionStatement() {
        return questionStatement;
    }

    public void setQuestionStatement(String questionStatement) {
        this.questionStatement = questionStatement;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}