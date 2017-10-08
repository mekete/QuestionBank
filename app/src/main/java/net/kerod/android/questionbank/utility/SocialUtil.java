package net.kerod.android.questionbank.utility;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.manager.ApplicationManager;

/**
 * Created by makata on 8/20/17.
 */

public class SocialUtil {
    public static final String PACKAGE_VIBER = "com.viber.voip";
    public static final String PACKAGE_WHATS_APP = "com.whatsapp";
    public static final String SHARE_BODY = ApplicationManager.getAppContext().getString(R.string.share_app_content);
    public static final String SHARE_BODY_SMS = ApplicationManager.getAppContext().getString(R.string.share_app_content_sms);

    public static final String FACEBOOK_APP_INVITE_ID = "162782194297114";

    public static void shareGooglePlay(Activity activity) {
        try {
            //CustomView.makeToast(CONTEXT,CONTEXT. getString(R.string.give_us_five_star), CustomView.SnackBarStyle.INFO).show();
            String url = "market://details?id=" + activity.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {//if GooglePlay app is not installed
            String url = "http://play.google.com/store/apps/details?id=" + activity.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        }
    }

    public static void shareFacebookAppInvite(Activity activity) {
        // https://developers.facebook.com/quickstarts/162772290964771/?platform=app-links-host
        String appLinkUrl = "https://fb.me/" + FACEBOOK_APP_INVITE_ID;
        String previewImageUrl = "https://lh3.googleusercontent.com/zDrhR1yp-MB5DFuFpV-CYJF8mF1bJ8YXMYbqzv-Q4M2r8g5l88vWs9mpXQ6qn9SzvRk=w300";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(activity, content);
        }
    }

    public static void shareEmail(@NonNull Context context, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ethiopian Calendar App");
//                    intent.setType( "plain/text");
//                    intent.setType( "message/rfc822");
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kerod.apps@gmail.com"});
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void shareSms(Activity activity, String message) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity);
        Intent shareIntent;
        shareIntent = builder
                .setType("vnd.android-dir/mms-sms")
                .getIntent()
                .setAction(Intent.ACTION_VIEW)
                .putExtra("sms_body", message);

        if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(shareIntent);
        }

    }

    public static void shareIntent(Activity activity, String appPackage, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(appPackage);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        activity.startActivity(intent);
    }

    public static void shareOther(Activity activity, String messageBody) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setChooserTitle("Select app")
                .setType("text/html")
                .setHtmlText(messageBody)
                .setText(messageBody)
                //.setSubject("Location of house")//will make email app compatible
                .getIntent()
                .putExtra(Intent.EXTRA_TEXT, messageBody);


        if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(shareIntent);
        }

    }

    void ddd() {
//    ShareCompat.IntentBuilder
//            // getActivity() or activity field if within Fragment
//            .from(this)
//            // The text that will be shared
//            .setText(textToShare)
//            // most general text sharing MIME type
//            .setType("text/plain")
//            .setStream(uriToContentThatMatchesTheArgumentOfSetType)
//        /*
//         * [OPTIONAL] Designate a URI to share. Your type that
//         * is set above will have to match the type of data
//         * that your designating with this URI. Not sure
//         * exactly what happens if you don't do that, but
//         * let's not find out.
//         *
//         * For example, to share an image, you'd do the following:
//         *     File imageFile = ...;
//         *     Uri uriToImage = ...; // Convert the File to URI
//         *     Intent shareImage = ShareCompat.IntentBuilder.from(activity)
//         *       .setType("image/png")
//         *       .setStream(uriToImage)
//         *       .getIntent();
//         */
//            .setEmailTo(arrayOfStringEmailAddresses)
//            .setEmailTo(singleStringEmailAddress)
//        /*
//         * [OPTIONAL] Designate the email recipients as an array
//         * of Strings or a single String
//         */
//            .setEmailTo(arrayOfStringEmailAddresses)
//            .setEmailTo(singleStringEmailAddress)
//        /*
//         * [OPTIONAL] Designate the email addresses that will be
//         * BCC'd on an email as an array of Strings or a single String
//         */
//            .addEmailBcc(arrayOfStringEmailAddresses)
//            .addEmailBcc(singleStringEmailAddress)
//        /*
//         * The title of the chooser that the system will show
//         * to allow the user to select an app
//         */
//            .setChooserTitle(yourChooserTitle)
//            .startChooser();
    }

    public static void shareMessageIntent(Activity activity, String appPackage, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(appPackage);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        activity.startActivity(intent);
    }

}
