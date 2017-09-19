package net.kerod.android.questionbank.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import net.kerod.android.questionbank.R;

public class ContactBottomSheetDialog {
    private static final String TAG = "ContactBottomSheetDialo";

    private ContactBottomSheetDialog(final String telephoneNumber, final String emailAdress, @NonNull final Context context) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View viewGroup = LayoutInflater.from(context).inflate(R.layout.dialog_contact, null);
        Button btnnPhoneNumber = (Button) viewGroup.findViewById(R.id.btnn_phone_number);
        btnnPhoneNumber.setText("    " + telephoneNumber);
        btnnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                    Log.e(TAG, "onClick: " + "VVVVVVVV 0000");
                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + telephoneNumber));
                    context.startActivity(intent);
                } else {
                    Log.e(TAG, "onClick: " + "VVVVVVVV 1111");
                }

            }
        });
        //
        Button btnnEmail = (Button) viewGroup.findViewById(R.id.btnn_email);
        btnnEmail.setText("    " + emailAdress);
        btnnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Question: ");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }

            }
        });

        dialog.setContentView(viewGroup);
        dialog.show();
    }

    public static void createAndShow(final String telephoneNumber, final String emailAdress, @NonNull final Context context) {
        new ContactBottomSheetDialog(telephoneNumber, emailAdress, context);
    }

}