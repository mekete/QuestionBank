package net.kerod.android.questionbank.widget;

/**
 * Created by makata on 11/24/17.
 */

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.kerod.android.questionbank.QuestionActivity;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.utility.StringUtil;
import net.kerod.android.questionbank.widget.toast.LoadToast;

public class RemarkBottomSheetDialog {
    private static final String TAG = "ContactBottomSheetDialo";

    private RemarkBottomSheetDialog(@NonNull final QuestionActivity activity, final LoadToast loadToast, final View mainContent) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);

        final View viewGroup = LayoutInflater.from(activity).inflate(R.layout.dialog_save_remark, null);
        final EditText txteRemark = viewGroup.findViewById(R.id.txte_remark);
        Button btnnSave = viewGroup.findViewById(R.id.btnn_save_remark);
        btnnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark = txteRemark.getText().toString().trim();
                if (!StringUtil.isNullOrEmpty(remark)) {
                    activity.saveUserAction(mainContent, -1, null, null, null, remark);
                    dialog.hide();
                }
            }
        });
        dialog.setContentView(viewGroup);
        dialog.show();
    }

    public static void createAndShow(@NonNull QuestionActivity activity, final LoadToast loadToast, final View mainContent) {
        new RemarkBottomSheetDialog(activity, loadToast, mainContent);
    }
}