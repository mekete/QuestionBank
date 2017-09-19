package net.kerod.android.questionbank.manager;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.model.AppUser;
import net.kerod.android.questionbank.utility.Constants;

import java.util.Date;

public class SettingsManager {
    private static Context context = ApplicationManager.getAppContext();

    public static void setAvatarIndex(int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.pref_key_avatar_index), value).commit();
    }

    public static int getAvatarIndex() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.pref_key_avatar_index), 0);
    }
    public static void setClassGroup(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_class_category), value).commit();
    }

    public static String getClassGroup() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_class_category), Constants.CLASS_UNDEFINED);
    }
    public static void setDisplayName(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_first_name), value).commit();
    }

    public static String getDisplayName() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_first_name), Constants.EMPTY_STRING_INDICATOR);
    }

    public static void setFirstName(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_first_name), value).commit();
    }

    public static String getFirstName() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_first_name), Constants.EMPTY_STRING_INDICATOR);
    }

    public static void setLastName(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_last_name), value).commit();
    }

    public static String getLastName() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_last_name), Constants.EMPTY_STRING_INDICATOR);
    }

    public static void setEmail(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_email), value).commit();
    }

    public static String getEmail() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_email), Constants.EMPTY_STRING_INDICATOR);
    }


    public static void setNotificationKey(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_notification_key), value).commit();
    }

    public static String getNotificationToken() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_notification_key), Constants.EMPTY_STRING_INDICATOR);
    }

    public static void setRegistrationToken(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_notification_key), value).commit();
    }

    public static String getRegistrationKey() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_notification_key), Constants.EMPTY_STRING_INDICATOR);
    }

    public static void setDatabaseVersion(int databaseVersion) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.pref_key_current_database_version), databaseVersion).commit();
    }

    public static int getDatabaseVersion() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.pref_key_current_database_version), 1);
    }

    public static void setLastTimeAccessed(String category, String time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(category, time).commit();
    }

    public static String getLastTimeAccessed(String category) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(category, "Not opened yet!");
    }



    public static long getLastFetchedTime(String parseClassName) {
        long time = PreferenceManager.getDefaultSharedPreferences(context).getLong(parseClassName, -1);
        Log.e("XXXXX", "Time : " + time + "  getLastFetchedTime " + parseClassName + Constants.EMPTY_STRING_INDICATOR);
        return time;
    }

    public static void updateLastFetchedTime(String parseClassName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(parseClassName, new Date().getTime()).commit();
        Log.e("XXXXX", "Time : " + new Date().getTime() + "  updateLastFetchedTime " + parseClassName + Constants.EMPTY_STRING_INDICATOR);
    }

    public static int getCurrentAppVersion() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.pref_key_current_app_version), 0);
    }
    public static void setCurrentAppVersion(int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.pref_key_current_app_version), value).commit();
    }
    //
    public static int getCurrentDatabaseVersion() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.pref_key_current_database_version), 0);
    }
    public static void setCurrentDatabaseVersion(int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.pref_key_current_database_version), value).commit();
    }

    public static boolean isFirstTimeLaunch() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_key_first_time_launch), true);
    }

    public static void setFirstTimeLaunch(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.pref_key_first_time_launch), value).commit();
    }


    public static boolean isVibrateOnWrongAttempt() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_key_vibrate_on_wrong_attempt), true);
    }

    public static void setVibrateOnWrongAttempt(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.pref_key_vibrate_on_wrong_attempt), value).commit();
    }
    public static boolean isLauncherIconAdded(String examUid) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(examUid, true);
    }

    public static void setLauncherIconAdded(String examUid, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(examUid, value).commit();
    }

    // -------------------------------------------------------------------------


    public static void setUserUid(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_user_uid), value).commit();
    }

    public static String getUserUid() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_user_uid), Constants.EMPTY_STRING);
    }

    @Nullable
    public static String getUserRole() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_user_role), AppUser.USER_ROLE_APP_USER);
    }

    public static void setUserRole(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_user_role), value).commit();
    }

    // -------------------------------------------------------------------------

    public static void setAntibiogramPreparedDate(String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_key_antibiogram_prepared_date), value).commit();
    }

    public static String getAntibiogramPreparedDate() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_antibiogram_prepared_date), Constants.EMPTY_STRING);
    }



    public static boolean isPermissionRequested(String permission) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(permission, false);

    }


    public static void setPermissionRequested(String permission, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(permission, value).commit();
    }

    public static int getVersionCode() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.pref_key_version_code), -1);
    }

    public static void setVersionCode(int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.pref_key_version_code), value).commit();
    }

    public static boolean isAgreedTermsOfService() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_key_agreed_terms_of_service), false);
    }

    public static void setAgreedTermsOfService(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.pref_key_agreed_terms_of_service), value).commit();
    }

    public static boolean isFirstTimeToViewAntibiogram() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_key_first_to_view_antibiogram), true);
    }

    public static void setFirstTimeToViewAntibiogram(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.pref_key_first_to_view_antibiogram), value).commit();
    }




    // -------------------------------------------------------------------------

}


