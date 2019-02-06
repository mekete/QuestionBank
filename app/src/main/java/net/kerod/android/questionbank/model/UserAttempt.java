package net.kerod.android.questionbank.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import net.kerod.android.questionbank.manager.ApplicationManager;
import net.kerod.android.questionbank.utility.Constants;


@IgnoreExtraProperties
public class UserAttempt extends FirebaseModel {

    public static final String TAG = "UserAttempt";
    //UserAction/userName_jhghjgj/examUid_hghzjcg/exam-info
    //UserAction/userName_jhghjgj/examUid_hghzjcg/question-info/question-uid
    //
//    private String userUid;
//    private String examUid;
//    private String questionUid;
    //current user activity
    private Integer questionNumber;//we need it to show on
    private String bookMark="";
    private Boolean markedAsError=false;
    private Integer attemptChoiceIndex;
    private Integer attemptScoreColor = Constants.COLOR_CHOICE_BACKGROUND_GERY;
    //
    private String remark;
    private Boolean score = null;
    private Long totalTimeUsed;
    private Long lastOpenedTime;

    public UserAttempt(Integer questionNumber, int attemptScoreColor) {
        this.attemptScoreColor = attemptScoreColor;
        this.questionNumber=questionNumber;
        this.score=false;
    }

    public UserAttempt() {

    }

    public UserAttempt(String bookMark, Boolean markedAsError, String remark, Long totalTimeUsed, Long lastOpenedTime, Boolean score) {
        this.bookMark = bookMark;
        this.markedAsError = markedAsError;
        this.remark = remark;
        this.totalTimeUsed = totalTimeUsed;
        this.lastOpenedTime = lastOpenedTime;
        this.score = score;
    }


    public static DatabaseReference getDatabaseReference(String userUid, String examUid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(TAG.toLowerCase())
                .child(userUid)
                .child(examUid)
                .child("questions");
    }

    public static DatabaseReference getDatabaseReference() {
        return getDatabaseReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), ApplicationManager.CurrentSession.getSelectedExam().getUid())
                ;
    }

    public static DatabaseReference getDatabaseReference(String userUid, String examUid, String questionUid) {
        return getDatabaseReference(userUid, examUid)
                .child(questionUid);
    }
    //


    public String getBookMark() {
        return bookMark;
    }

    public void setBookMark(String bookMark) {
        this.bookMark = bookMark;
    }

    public Boolean getMarkedAsError() {
        return markedAsError;
    }

    public void setMarkedAsError(Boolean markedAsError) {
        this.markedAsError = markedAsError;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTotalTimeUsed() {
        return totalTimeUsed;
    }

    public void setTotalTimeUsed(Long totalTimeUsed) {
        this.totalTimeUsed = totalTimeUsed;
    }

    public Long getLastOpenedTime() {
        return lastOpenedTime;
    }

    public void setLastOpenedTime(Long lastOpenedTime) {
        this.lastOpenedTime = lastOpenedTime;
    }

    public Boolean getScore() {
        return score;
    }

    public void setScore(Boolean score) {
        this.score = score;
    }


    public Integer getAttemptChoiceIndex() {
        return attemptChoiceIndex;
    }

    public void setAttemptChoiceIndex(Integer attemptChoiceIndex) {
        this.attemptChoiceIndex = attemptChoiceIndex;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getAttemptScoreColor() {
        return attemptScoreColor;
    }

    public void setAttemptScoreColor(Integer attemptScoreColor) {
        this.attemptScoreColor = attemptScoreColor;
    }
}