package net.kerod.android.questionbank.utility;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.BuildConfig;
import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.AppVersion;
import net.kerod.android.questionbank.widget.CustomView;

/**
 * Created by makata on 10/31/17.
 */

public class AppUtil {
    public static void checkForUpdate(final Activity activity, final View mainContent) {
        final Query query = AppVersion.getDatabaseReference();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    AppVersion appVersion = snapshot.getValue(AppVersion.class);
                    if (appVersion != null && appVersion.getLatestVersionCode() != null && appVersion.getLatestVersionCode() > BuildConfig.VERSION_CODE) {
                        if (AppVersion.UpdateLevel.BigFeature.toString().equals(appVersion.getUpdateLevel()) || AppVersion.UpdateLevel.Critical.toString().equals(appVersion.getUpdateLevel())) {
                            showUpgradeDialog(activity, appVersion.getUpdateSummary());
                        } else if (AppVersion.UpdateLevel.MinorUpgrade.toString().equals(appVersion.getUpdateLevel()) ) {
                            CustomView.makeSnackBar(mainContent, appVersion.getUpdateSummary(), CustomView.SnackBarStyle.INFO).setAction(activity.getString(R.string.update), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SocialUtil.shareGooglePlay(activity, activity.getString(R.string.update_toast_message));
                                }
                            }).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private static void showUpgradeDialog(final Activity activity, final String updateSummary) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder
                .setTitle("Update App")
                .setMessage(updateSummary)
                .setPositiveButton(activity.getString(R.string.update),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SocialUtil.shareGooglePlay(activity, activity.getString(R.string.update_toast_message));
                            }
                        }
                );
        alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
