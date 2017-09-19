package net.kerod.android.questionbank;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.Instruction;
import net.kerod.android.questionbank.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExamUploaderActivity extends AppCompatActivity {
    private static final String TAG = "ExamListActivity";
    public static final String EXAM_LIST_NODE = "examList";

    DatabaseReference mExamListDatabaseReference = FirebaseDatabase.getInstance().getReference().child(EXAM_LIST_NODE.toLowerCase());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        try {
            int [] aaaa={
                    R.raw.nn_bio_2005_12,
                    R.raw.nn_bio_2006_12,
                    R.raw.nn_bio_2007_12,
                    R.raw.nn_bio_2008_12,
                    //
                    R.raw.nn_cib_2005_12,
                    R.raw.nn_cib_2006_12,
                    R.raw.nn_cib_2007_12,
                    R.raw.nn_cib_2008_12,
                    //
                    R.raw.nn_eng_2005_12,
//                    R.raw.nn_eng_2005_12,
//                    R.raw.nn_eng_2005_12,
//                    R.raw.nn_eng_2005_12,
                    //
                    R.raw.nnn_civic_2000_10,
                    R.raw.nnn_civic_2001_10,
                    R.raw.nnn_civic_2002_10,
                    //
                    R.raw.nnn_econ_2000_12,
                    R.raw.nnn_econ_2001_12,
                    R.raw.nnn_econ_2002_12,
                    R.raw.nnn_econ_2003_12,
                    R.raw.nnn_econ_2004_12,
                    //
                    R.raw.nnn_hist_2000_12,
                    R.raw.nnn_hist_2001_12,
                    R.raw.nnn_hist_2002_12,
                    R.raw.nnn_hist_2003_12,
                    //
                    R.raw.nnn_hist_2001_12,
                    R.raw.nnn_hist_2002_12,
                    R.raw.nnn_hist_2003_12,
            };
//            loadFromResource(R.raw.old_eng_2005_10);
            for(int i=0;i<aaaa.length;i++){
                loadFromResource(aaaa[i]);
            }
            //loadFromResource(R.raw.nn_eng_2005_12);
        } catch (JSONException e) {
            Log.e(TAG, "getQuestionListFromJson \n\n\n: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "getQuestionListFromJson \n\n\n: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadFromResource(int resourceId) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(stringFromResources(resourceId));
        String examSummaryKey = mExamListDatabaseReference.push().getKey();
        //
        Exam exam = getExamFromJson(jsonObject, examSummaryKey);
        mExamListDatabaseReference.child(examSummaryKey).setValue(exam);
        try {
            Log.e(TAG, "loadFromResource: " + exam.getFullName());
        } catch (Exception e) {
            Log.e(TAG, "\n\n\n\n----------------" +
                    "loadFromResource QN : " + e);
            e.printStackTrace();
        }
        //
        List<Question> questionList = getQuestionListFromJson(jsonObject, examSummaryKey);
        for (Question question : questionList) {
            Question.getDatabaseReference(examSummaryKey).push().setValue(question);
            Log.e(TAG, "\n\n\n\n----------------" +   "loadFromResource QN : " + question.getQuestionNumber());
            Log.e(TAG, "loadFromResource CA : " + question.getCorrectAnswer());
            Log.e(TAG, "loadFromResource QN : " + question.getStatement());
        }
        //
        List<Instruction> instructionList = getInstructionListFromJson(jsonObject, examSummaryKey);
        try{
        for (Instruction instruction : instructionList) {
            //Instruction.getDatabaseReference(examSummaryKey).push().setValue(instruction);
            Instruction.getDatabaseReference(examSummaryKey).child(instruction.getInstructionCode()).setValue(instruction);
            Log.e(TAG, "\n\n\n\nINSTRUCTION\n" +
                    "----------------" +   "getStatement  : " + instruction.getStatement());
            Log.e(TAG, "getInstructionCode  : " + instruction.getInstructionCode());
            Log.e(TAG, "getStartQuestion  : " + instruction.getStartQuestion());
        }
        }catch(Exception ex){}
    }

    private List<Question> getQuestionListFromJson(JSONObject jsonObject, String examKey) {
        try {
            JSONArray questionJsonArray = jsonObject.getJSONArray("questionList");
            return getQuestionListFromJson(questionJsonArray, examKey);
        } catch (JSONException e) {
            Log.e(TAG, "getQuestionListFromJson \n\n\n: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private List<Instruction> getInstructionListFromJson(JSONObject jsonObject, String examKey) {
        try {
            JSONArray questionJsonArray = jsonObject.getJSONArray("instructionList");
            return getInstructionListFromJson(questionJsonArray, examKey);
        } catch (JSONException e) {
            Log.e(TAG, "getQuestionListFromJson \n\n\n: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<Question> getQuestionListFromJson(JSONArray questionJsonArray, String examKey) throws JSONException {

        List<Question> questionList = new ArrayList<>();
        for (int i = 0; i < questionJsonArray.length(); i++) {
            try {
                Log.e(TAG, "getQuestionListFromJson:  index ::: " + i);
                Question question = new Question();
                JSONObject jsonObject = questionJsonArray.getJSONObject(i);
                question.setExamUid(examKey);
                //
                question.setChoiceA(jsonObject.getString("choiceA"));
                question.setChoiceB(jsonObject.getString("choiceB"));
                question.setChoiceC(jsonObject.getString("choiceC"));
                question.setChoiceD(jsonObject.getString("choiceD"));
                question.setCorrectAnswer(jsonObject.getString("correctAnswer"));
                question.setInstructionCode(jsonObject.getString("instructionCode"));
                question.setQuestionNumber(jsonObject.getInt("questionNumber"));
                question.setStatement(jsonObject.getString("questionStatement"));
                questionList.add(question);
            } catch (JSONException e) {
                Log.e(TAG, "getQuestionListFromJson: index of question :::: " + e.getMessage());
                e.printStackTrace();
            }

        }
        return questionList;
    }

    private List<Instruction> getInstructionListFromJson(JSONArray questionJsonArray, String examKey) throws JSONException {

        List<Instruction> questionList = new ArrayList<>();
        for (int i = 0; i < questionJsonArray.length(); i++) {
            try {
                Log.e(TAG, "getInstructionListFromJson:  index ::: " + i);
                Instruction instruction = new Instruction();
                JSONObject jsonObject = questionJsonArray.getJSONObject(i);
                instruction.setExamUid(examKey);
                //
                instruction.setCategory(jsonObject.getString("category"));
                instruction.setEndQuestion(jsonObject.getInt("endQuestion"));
                instruction.setStartQuestion(jsonObject.getInt("startQuestion"));
                instruction.setInstructionCode(jsonObject.getString("instructionCode"));
                instruction.setStatement(jsonObject.getString("statement"));
                questionList.add(instruction);
            } catch (JSONException e) {
                Log.e(TAG, "getInstructionListFromJson: index of instruction :::: " + e.getMessage());
                e.printStackTrace();
            }

        }
        return questionList;
    }
    private static Exam getExamFromJson(JSONObject jsonParentObject, String examKey) throws JSONException {


        Exam exam = new Exam();
        JSONObject jsonObject= jsonParentObject.getJSONObject("examSummary");
        try {
            exam.setIconUrl(jsonObject.getString("iconUrl"));
            exam.setShortName(jsonObject.getString("shortName"));
            exam.setFullName(jsonObject.getString("fullName"));
            //
            exam.setGrade(jsonObject.getString("grade"));
            exam.setSubject(jsonObject.getString("subject"));
            exam.setNumberOfSections(jsonObject.getInt("numberOfSections"));
            exam.setNumberOfQuestions(jsonObject.getInt("numberOfQuestions"));
            exam.setAllowedTime(jsonObject.getLong("allowedTime"));
            exam.setBookletCode(jsonObject.getString("bookletCode"));
            //
            exam.setVersion(jsonObject.getString("version"));
            exam.setExamGivenDate(jsonObject.getString("examGivenDate"));
            exam.setIconChar(jsonObject.getString("iconChar"));
            //
            Log.e(TAG, "getExamFromJson: \n\n" +
                    "\n\n\n==========================================" );
            Log.e(TAG, "  " +exam.getIconUrl());
            Log.e(TAG, "  " +exam.getFullName());
            Log.e(TAG, "  " +exam.getAllowedTime());
            Log.e(TAG, "  " +exam.getExamGivenDate());
            Log.e(TAG, "  " +exam.getBookletCode());
            Log.e(TAG, "  " +exam.getRemark());
            Log.e(TAG, "  " +exam.getIconChar());
            Log.e(TAG, "  " +exam.getShortName());
            Log.e(TAG, "  " +exam.getSponsorCompany());
            Log.e(TAG, "  " +exam.getSubject());
            Log.e(TAG, "  " +exam.getGrade());
            Log.e(TAG, "  " +exam.getAllowedTime());
            Log.e(TAG, "getExamFromJson: \n\n" +
                    "\n\n\n==========================================" );
            return exam;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private String stringFromResources(int rawResourceId) throws IOException {
        Resources mResources = getResources();
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = mResources.openRawResource(rawResourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        return categoriesJson.toString();
    }



}
