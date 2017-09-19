package net.kerod.android.questionbank.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.kerod.android.questionbank.R;

public class ConfirmDeleteBottomSheetDialog {
    static BottomSheetDialog dialog;
    static TextView txtvTitle;
    static TextView txtvBody;

    private ConfirmDeleteBottomSheetDialog() {
    }

    public static void createAndShow(Context context, String title, String body) {
        if (dialog == null) {
            dialog = new BottomSheetDialog(context);
            View viewGroup = LayoutInflater.from(context).inflate(R.layout.dialog_instruction, null);
            txtvTitle = (TextView) viewGroup.findViewById(R.id.txtv_dialog_title);
            txtvBody = (TextView) viewGroup.findViewById(R.id.txtv_dialog_body);
            //txtvInstruction.setText("    "+selectedInstruction.getSortOrder());
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog = null;
                }
            });
            dialog.setContentView(viewGroup);
        }
        //final Instruction selectedInstruction = ApplicationManager.CurrentSession.getSelectedInstruction();
        txtvTitle.setText(title);
        txtvBody.setText(body);
        dialog.show();
    }

    public static boolean isVisible() {
        return dialog != null && dialog.isShowing();

    }

    public static void setTitle(String title) {
        txtvTitle.setText(title);

    }

    public static void setBody(String body) {
        txtvBody.setText(body);
    }
}