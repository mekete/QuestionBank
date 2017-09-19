package net.kerod.android.questionbank.widget;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.TextView;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.manager.ApplicationManager;


public class CustomView {

//    public static void makeToast(Activity activity,  String message) {
//
//        Toast toast = new Toast(activity.getApplicationContext());
//        {
//            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
//            TextView txtvToastText = (TextView) toastLayout.findViewById(R.id.txtv_toast_text);
//            txtvToastText.setText(message);
//            toastLayout.setBackgroundColor(Constants.COLOR_PRIMARY);
//            txtvToastText.setTextColor(Color.WHITE);
//            toast.setDuration(Toast.LENGTH_LONG);
//
//            toast.setGravity(Gravity.TOP, 0, 50);
//            toast.setView(toastLayout);
//        }
//        toast.show();
//    }

    @NonNull
    public static Snackbar makeSnackbar(@NonNull View layout, @NonNull CharSequence text, int duration) {
        Snackbar snackBarView = Snackbar.make(layout, text, duration);
        snackBarView.setActionTextColor(ContextCompat.getColor(layout.getContext(), R.color.choice_color_correct));
        snackBarView.getView().setBackgroundColor(ContextCompat.getColor(layout.getContext(),R.color.colorPrimary));
        TextView tv = (TextView) snackBarView.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(layout.getContext(),R.color.brokenWhite));

        return snackBarView;
    }
}
