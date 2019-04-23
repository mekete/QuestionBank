package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import net.kerod.android.questionbank.manager.ApplicationManager;

@IgnoreExtraProperties
public class Instruction extends FirebaseModel {
    public  static  final  String TAG="Instruction";


    private String instructionCode;
    private String examUid;
    private String statement;
    private String category;
    private Integer startQuestion;
    private Integer endQuestion;

    private static final String FIRESTORE_DOCUMENT_NAME = "instruction";

    public static CollectionReference getCollectionReference() {
        return getFirestoreInstance().collection(FIRESTORE_DOCUMENT_NAME);//.document("2017_01_01");
    }

    public static String createDocumentUid() {
        return getCollectionReference().document().getId();
    }

    @Override @Exclude
    public String getTitle() {
        return category;
    }

    @Override @Exclude
    public String getSubTitle() {
        return statement;
    }

    //

    public static DatabaseReference getDatabaseReference() {
        return getDatabaseReference(ApplicationManager.CurrentSession.getSelectedExam().getUid());

    }

    public static DatabaseReference getDatabaseReference(String examUid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(Exam.TAG.toLowerCase())
                .child(examUid)
                .child(Instruction.TAG.toLowerCase());
    }
    public static DatabaseReference getDatabaseReference(String examUid,String instructionUid) {
        return getDatabaseReference(  examUid).child(instructionUid);
    }
    //


    public String getExamUid() {
        return examUid;
    }

    public void setExamUid(String examUid) {
        this.examUid = examUid;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStartQuestion() {
        return startQuestion;
    }

    public void setStartQuestion(Integer startQuestion) {
        this.startQuestion = startQuestion;
    }

    public Integer getEndQuestion() {
        return endQuestion;
    }

    public void setEndQuestion(Integer endQuestion) {
        this.endQuestion = endQuestion;
    }

    public String getInstructionCode() {
        return instructionCode;
    }

    public void setInstructionCode(String instructionCode) {
        this.instructionCode = instructionCode;
    }
}

