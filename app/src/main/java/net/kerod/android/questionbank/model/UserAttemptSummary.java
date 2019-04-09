package net.kerod.android.questionbank.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import net.kerod.android.questionbank.manager.ApplicationManager;

@IgnoreExtraProperties
public class UserAttemptSummary extends FirebaseModel {//uid is same as that of username
    private static final String TAG = "Summary";
    //
    private String examUid;
    private Integer correctCount=0;
    private Integer incorrectCount=0;
    private Integer totalCount=0;
    //
    private Integer lastOpenedQuestionIndex;
    private Long totalTimeUsed;
    private Long lastOpenedTime;
    private String remark;


    private static final String FIRESTORE_DOCUMENT_NAME = "userAttemptSummary";

    public static CollectionReference getCollectionReference() {
        //return getFirestoreInstance().collection(FIRESTORE_DOCUMENT_NAME);//.document("2017_01_01");
        return getCollectionReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), ApplicationManager.CurrentSession.getSelectedExam().getUid());
    }
    public static CollectionReference getCollectionReference(String userUid, String examUid) {
        return getFirestoreInstance().collection(FIRESTORE_DOCUMENT_NAME).document(userUid).collection(examUid);//.document("2017_01_01");
    }

    public static String createDocumentUid() {
        return getCollectionReference().document().getId();
    }

    @Override @Exclude
    public String getTitle() {
        return ""+examUid;
    }

    @Override @Exclude
    public String getSubTitle() {
        return ""+totalTimeUsed;
    }

    public static DatabaseReference getDatabaseReference(String userUid, String examUid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(UserAttempt.TAG.toLowerCase())
                .child(userUid)
                .child(examUid)
                .child( TAG.toLowerCase());
    }

    public static DatabaseReference getDatabaseReference() {
        return getDatabaseReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), ApplicationManager.CurrentSession.getSelectedExam().getUid());
    }


    public String getExamUid() {
        return examUid;
    }

    public void setExamUid(String examUid) {
        this.examUid = examUid;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(Integer incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getLastOpenedQuestionIndex() {
       try{
           return  new Integer(   lastOpenedQuestionIndex);
       }catch(Exception e){
           return 0;
       }

    }

    public void setLastOpenedQuestionIndex(Integer lastOpenedQuestionIndex) {
        this.lastOpenedQuestionIndex = lastOpenedQuestionIndex;
    }
}