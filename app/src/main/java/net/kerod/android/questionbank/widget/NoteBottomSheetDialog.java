package net.kerod.android.questionbank.widget;

import android.content.DialogInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.QuestionActivity;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.UserAttempt;

public class NoteBottomSheetDialog {
    static BottomSheetDialog dialog;
    static TextView btnnSaveNote;
    static EditText txtvQuestionNote;

    private NoteBottomSheetDialog() {
    }

    public static void createAndShow(final DatabaseReference attemptReference, final QuestionActivity questionActivity, final View view, final String remark) {
        if (dialog == null) {
            dialog = new BottomSheetDialog(questionActivity);
            View viewGroup = LayoutInflater.from(questionActivity).inflate(R.layout.dialog_note, null);
            txtvQuestionNote = (EditText) viewGroup.findViewById(R.id.txte_note);
            showPreviousRemark(txtvQuestionNote, attemptReference);
            btnnSaveNote = (TextView) viewGroup.findViewById(R.id.btnn_save_note);
            btnnSaveNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // questionActivity.saveUserAction(view, null, null, null, null, remark);
                }
            });
            //txtvInstruction.setText("    "+selectedInstruction.getSortOrder());
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog = null;
                }
            });
            dialog.setContentView(viewGroup);//
            //
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                    FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
        //final Instruction selectedInstruction = ApplicationManager.CurrentSession.getSelectedInstruction();
        //txtvTitle.setText(title);
        txtvQuestionNote.setText((remark));
        dialog.show();
    }


    private static void showPreviousRemark(final EditText txtvQuestionNote, final DatabaseReference attemptReference) {
        //Log.e(TAG, ">>>>>>> aaaa Check Attempr: ");
        //final DatabaseReference attemptReference = mAttemptDatabaseReference.child(mCurrentQuestion.getUid());
        attemptReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAttempt attempt = dataSnapshot.getValue(UserAttempt.class);
                try {
                    if (attempt == null) {
                        txtvQuestionNote.setText("");
                    } else {
                        txtvQuestionNote.setText(attempt.getRemark());
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static boolean isVisible() {
        return dialog != null && dialog.isShowing();

    }

    public static void setTitle(String title) {
        btnnSaveNote.setText(title);

    }

    public static void setBody(String body) {
        txtvQuestionNote.setText(body);
    }
}