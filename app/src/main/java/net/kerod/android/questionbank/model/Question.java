package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import net.kerod.android.questionbank.manager.ApplicationManager;

@IgnoreExtraProperties
public class Question extends FirebaseModel {
    public static final String TAG = "Question";
    public static final String FIELD_EXAM_UID = "examUid";
    //
    //
    private String examUid;
    private String instructionCode;
    private String passageUid;
    private Boolean startOfSection ;//we may present instruction before it
    //
    private Integer questionNumber;
    private String statement;
    private String correctAnswer;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    //
    private Integer markedAsErrorCount;

    //
    private static final String FIRESTORE_DOCUMENT_NAME = "question";

    public static CollectionReference getCollectionReference() {
        return getFirestoreInstance().collection(FIRESTORE_DOCUMENT_NAME);//.document("2017_01_01");
    }

    public static String createDocumentUid() {
        return getCollectionReference().document().getId();
    }

    @Override @Exclude
    public String getTitle() {
        return ""+questionNumber;
    }

    @Override @Exclude
    public String getSubTitle() {
        return statement;
    }


    public static DatabaseReference getDatabaseReference() {
        return getDatabaseReference(ApplicationManager.CurrentSession.getSelectedExam().getUid());

    }

    public static DatabaseReference getDatabaseReference(String examUid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(Exam.TAG.toLowerCase())
                .child(examUid)
                .child(Question.TAG.toLowerCase());
    }
    //


    public String getExamUid() {
        return examUid;
    }

    public void setExamUid(String examUid) {
        this.examUid = examUid;
    }

    public String getInstructionCode() {
        return instructionCode;
    }

    public void setInstructionCode(String instructionCode) {
        this.instructionCode = instructionCode;
    }

    public String getPassageUid() {
        return passageUid;
    }

    public void setPassageUid(String passageUid) {
        this.passageUid = passageUid;
    }

    public Boolean getStartOfSection() {
        return startOfSection;
    }

    public void setStartOfSection(Boolean startOfSection) {
        this.startOfSection = startOfSection;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }


    public Integer getMarkedAsErrorCount() {
        return markedAsErrorCount;
    }

    public void setMarkedAsErrorCount(Integer markedAsErrorCount) {
        this.markedAsErrorCount = markedAsErrorCount;
    }



    public static int getChoiceLetterIndex(String choiceLetter) {
        if ("A".equals(choiceLetter)) {
            return 0;
        }else if ("B".equals(choiceLetter)) {
            return 1;
        }else if ("C".equals(choiceLetter)) {
            return 2;
        }else if ("D".equals(choiceLetter)) {
            return 3;
        }else if ("E".equals(choiceLetter)) {
            return 4;
        }
        return -1;

    }
//    public Double getSortOrder() {
//
//        if(sortOrder==null && questionNumber!=null){
//            return new Double(questionNumber);
//        } return sortOrder;
//    }
}