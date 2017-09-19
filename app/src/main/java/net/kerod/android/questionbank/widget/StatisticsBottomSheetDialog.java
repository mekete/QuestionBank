package net.kerod.android.questionbank.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.kerod.android.questionbank.R;

public class StatisticsBottomSheetDialog {
    static BottomSheetDialog dialog;
    static TextView txtvTitle;
    static TextView txtvBody;

    private StatisticsBottomSheetDialog() {
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
            dialog.setContentView(viewGroup);//
            //
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                    FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog  .findViewById(android.support.design.R.id.design_bottom_sheet);
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
        //final Instruction selectedInstruction = ApplicationManager.CurrentSession.getSelectedInstruction();
        txtvTitle.setText(title);
        txtvBody.setText( Html.fromHtml(body));
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