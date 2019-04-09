package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

@IgnoreExtraProperties
public class Exam extends FirebaseModel {
    public static final String TAG = "Exam";
    public static final String COLUMN_SHORT_NAME = "shortName";

    //
    public static final String SUBJECT_ENGLISH = "English";
    public static final String SUBJECT_MATHEMATICS = "Mathematics";
    public static final String SUBJECT_APTITUDE = "Aptitude";
    public static final String SUBJECT_CIVICS = "Civics";
    //
    public static final String SUBJECT_ECONOMICS = "Economics";
    public static final String SUBJECT_HISTORY = "Mathematics";
    public static final String SUBJECT_GEOGRAPHY = "Geography";
    //
    public static final String SUBJECT_CHEMISTRY = "Chemistry";
    public static final String SUBJECT_PHYSICS = "Physics";
    public static final String SUBJECT_BIOLOGY = "Biology";
    //
    private String iconChar;
    private String iconUrl;
    private String shortName;//max of 16 chars including spaces
    private String fullName;
    //
    private String grade;
    private String subject;
    private Integer numberOfSections;
    private Integer numberOfQuestions;
    private Long allowedTime;//in millis
    private String defaultInstruction;
    private String bookletCode;
    //
    private String version;
    private String examGivenDate;
    private String aboutExam;
    private Long createdAt;
    private Long updatedAt;
    private Integer totalDownloads;
    private String remark;
    private String targetCountry = "ET";
    //
    private String sponsorCompany;

    //
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(Exam.TAG.toLowerCase());
    }
    private static final String FIRESTORE_DOCUMENT_NAME = "instruction";

    public static CollectionReference getCollectionReference() {
        return getFirestoreInstance().collection(FIRESTORE_DOCUMENT_NAME);//.document("2017_01_01");
    }

    public static String createDocumentUid() {
        return getCollectionReference().document().getId();
    }

    @Override @Exclude
    public String getTitle() {
        return subject;
    }

    @Override @Exclude
    public String getSubTitle() {
        return fullName;
    }

    //
    public String getIconChar() {
        return iconChar;
    }

    public void setIconChar(String iconChar) {
        this.iconChar = iconChar;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getNumberOfSections() {
        return numberOfSections;
    }

    public void setNumberOfSections(Integer numberOfSections) {
        this.numberOfSections = numberOfSections;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Long getAllowedTime() {
        return allowedTime;
    }

    public void setAllowedTime(Long allowedTime) {
        this.allowedTime = allowedTime;
    }

    public String getBookletCode() {
        return bookletCode;
    }

    public void setBookletCode(String bookletCode) {
        this.bookletCode = bookletCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getExamGivenDate() {
        return examGivenDate;
    }

    public void setExamGivenDate(String examGivenDate) {
        this.examGivenDate = examGivenDate;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(Integer totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTargetCountry() {
        return targetCountry;
    }

    public void setTargetCountry(String targetCountry) {
        this.targetCountry = targetCountry;
    }

    public String getSponsorCompany() {
        return sponsorCompany;
    }

    public void setSponsorCompany(String sponsorCompany) {
        this.sponsorCompany = sponsorCompany;
    }

    public String getDefaultInstruction() {
        return defaultInstruction;
    }

    public void setDefaultInstruction(String defaultInstruction) {
        this.defaultInstruction = defaultInstruction;
    }

    public String getAboutExam() {
        return aboutExam;
    }

    public void setAboutExam(String aboutExam) {
        this.aboutExam = aboutExam;
    }
}