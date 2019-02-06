package net.kerod.android.questionbank.widget;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.QuestionActivity;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.utility.StringUtil;

/**
 * Created by makata on 11/24/17.
 */

public class TagsBottomSheetDialog {
    private static final String TAG = "TagsBottomSheetDialog";
    private static final String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private TagsBottomSheetDialog(@NonNull final QuestionActivity activity, final @NonNull Question currentQuestion, final View mainContent) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        final View viewGroup = LayoutInflater.from(activity).inflate(R.layout.dialog_add_tags, null);
        //
        final CheckBox checkBoxArray[] = {
                viewGroup.findViewById(R.id.ckbx_tag_difficult),
                viewGroup.findViewById(R.id.ckbx_tag_easy),
                viewGroup.findViewById(R.id.ckbx_tag_help_needed),
                viewGroup.findViewById(R.id.ckbx_tag_irrelevant),
                //
                viewGroup.findViewById(R.id.ckbx_tag_time_taking),
                viewGroup.findViewById(R.id.ckbx_tag_tricky),
                viewGroup.findViewById(R.id.ckbx_tag_try_later),
                viewGroup.findViewById(R.id.ckbx_tag_vague)
        };

        Query query = UserAttempt.getDatabaseReference(mUserId, currentQuestion.getExamUid(), currentQuestion.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);
                if (attempt != null && !StringUtil.isNullOrEmpty(attempt.getBookMark())) {
                    String currentTags = attempt.getBookMark();
                    for (int i = 0; i < checkBoxArray.length; i++) {
                        checkBoxArray[i].setChecked(currentTags.contains(checkBoxArray[i].getText()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomView.makeSnackBar(mainContent, "Sorry, Your bookmarks are not saved!", CustomView.SnackBarStyle.WARNING).show();
            }
        });
        //
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    StringBuffer tagsArray = new StringBuffer();
                    for (int i = 0; i < checkBoxArray.length; i++) {
                        if (checkBoxArray[i].isChecked())
                            tagsArray.append(",").append(checkBoxArray[i].getText());
                    }
                    activity.saveQuestionBookMarks(tagsArray + "");
                } catch (Exception ex) {
                    Log.e(TAG, "\n\n\n>>>>>>>kkkkk onDismiss: " + ex);
                }

            }
        });
        dialog.setContentView(viewGroup);
        dialog.show();
    }

    public static void createAndShow(@NonNull QuestionActivity activity, final Question question, final View mainContent) {
        new  TagsBottomSheetDialog(activity, question, mainContent);
    }
}