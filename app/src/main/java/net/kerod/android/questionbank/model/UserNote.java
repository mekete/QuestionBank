package net.kerod.android.questionbank.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import net.kerod.android.questionbank.manager.ApplicationManager;


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

    private static final String FIRESTORE_DOCUMENT_NAME = "userNote";

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
        return ""+userRemark;
    }


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