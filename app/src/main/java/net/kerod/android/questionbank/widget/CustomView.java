package net.kerod.android.questionbank.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.kerod.android.questionbank.R;


public class CustomView {
    public enum SnackBarStyle {
        DEFAULT(3000, R.color.colorPrimaryLight, R.color.colorBrokenWhite),
        SUCCESS(3000, R.color.colorSuccess, R.color.colorPrimaryLight),
        INFO(5000, R.color.colorPrimaryLight, R.color.colorBrokenWhite),
        INFO_SHORT(2000, R.color.colorPrimaryLight, R.color.colorBrokenWhite),
        WARNING(5000, R.color.colorWarning, R.color.colorBrokenWhite),
        ERROR(8000, R.color.colorError, R.color.colorBrokenWhite),
        INDEFINITE_SUCCESS(-1, R.color.colorPrimaryLight, R.color.colorBrokenWhite);//
        //
        int duration;
        int backgroundColor;
        int textColor;

        //
        SnackBarStyle(int duration, int backgroundColor, int textColor) {
            this.duration = duration;
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
        }
    }

    @NonNull
    public static Snackbar makeSnackBar(@NonNull View layout, @NonNull String text, SnackBarStyle style) {

        final Snackbar snackBarView = Snackbar.make(layout, text, style.duration);
        if (SnackBarStyle.ERROR == style || SnackBarStyle.WARNING == style) {
            snackBarView.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBarView.dismiss();
                }
            });
            snackBarView.setActionTextColor(ContextCompat.getColor(layout.getContext(),style.textColor));
        }

        //
        TextView textView =  (snackBarView.getView()).findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(layout.getContext(), style.textColor));
        View view = snackBarView.getView();
        view.setBackgroundColor(ContextCompat.getColor(layout.getContext(), style.backgroundColor));
        //
        return snackBarView;
    }


    @NonNull
    public static Toast makeToast(@NonNull Activity activity, String message, SnackBarStyle style) {

        Toast toast = new Toast(activity.getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        TextView textView = (TextView) layout.findViewById(R.id.txtv_toast_text);
        textView.setText(message);
        textView.setTextColor(style.textColor);
        layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(), style.backgroundColor));

        if (SnackBarStyle.ERROR == style||SnackBarStyle.WARNING == style||SnackBarStyle.INFO == style||SnackBarStyle.INDEFINITE_SUCCESS == style) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {//if (SnackBarStyle.SUCCESS == style || SnackBarStyle.INFO_SHORT == style) {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setView(layout);
        return toast;
    }
//


}
