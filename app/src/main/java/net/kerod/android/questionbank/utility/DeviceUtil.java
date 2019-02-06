package net.kerod.android.questionbank.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import net.kerod.android.questionbank.manager.ApplicationManager;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by makata on 6/15/17.
 */

public class DeviceUtil {


    void check(@NonNull Context context) {
        if (context.getResources().getConfiguration().smallestScreenWidthDp >= 600) {
        }
        if (context.getResources().getConfiguration().smallestScreenWidthDp < 600) {
        }
    }


    public static boolean isGpsFeatureAvailable() {
        return ApplicationManager.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public static boolean isGpsEnabled() {
        return ((LocationManager) ApplicationManager.getAppContext().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static int calculateDp(@NonNull Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (displayMetrics.widthPixels < displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels) / activity.getResources().getConfiguration().smallestScreenWidthDp;
    }

//    public static long getAvailableInternalMemorySize() {
//        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
//        return ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
//    }

    public static long getTotalInternalMemorySize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
    }

    public static boolean isNoSensorOrientationLandscape(@NonNull Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels > displayMetrics.heightPixels;
    }

    public static int getNoSensorOrientation(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Configuration config = context.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();
        if ((rotation == 0 || rotation == 2) && config.orientation == 2) {
            return 2;
        }
        if ((rotation == 1 || rotation == 3) && config.orientation == 1) {
            return 2;
        }
        return 1;
    }

    public static int getLandscapePixelOrientation(@NonNull Activity activity, int percentage, boolean fromWidth) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static final void performConfig(Context context) {
        displayOverflowActionbarMenu(context);
    }

    public static void displayOverflowActionbarMenu(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getCurrentScreenshot(@NonNull View view) {
        if ((view.getWidth() > 0 && view.getHeight() > 0)) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            view.draw(new Canvas(bitmap));
            return bitmap;
        }
        return null;
    }

//    public static int calculateMargin(Activity activity, int percentage, boolean fromHeight) {
//        return percentage;
//    }

//    public static boolean isGooglePlayServicesAvailable(@NonNull Context context) {
//        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
//    }
//
//    boolean isResolvableGooglePlayServicesAvailable(@NonNull Activity context) {
//        int someCodeValue = 7;
//
//        GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
//        int result = instance.isGooglePlayServicesAvailable(context);
//        if (ConnectionResult.SUCCESS != result) {
//            if (instance.isUserResolvableError(result)) {
//                instance.getErrorDialog(context, result, someCodeValue).show();
//            }
//            return false;
//        }
//        return true;
//    }


    public static String getDeviceInfo() {
        Context context = ApplicationManager.getAppContext();
        PackageManager packageManager = context.getPackageManager();
        StringBuilder builder = new StringBuilder();
        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            builder.append("versionName  \t: ").append(packageInfo.versionName).append("\n");
            builder.append("packageName  \t: ").append(packageInfo.packageName).append("\n").append("\n");
            //
            builder.append("androidVersion  : ").append(VERSION.RELEASE).append("\n");
            builder.append("androidCodeName : ").append(VERSION.CODENAME).append("\n");
            //
            builder.append("phoneModel   \t: ").append(Build.MODEL).append("\n");
            builder.append("Manufacturer    : ").append(Build.MANUFACTURER).append("\n");
            builder.append("board  \t\t\t: ").append(Build.BOARD).append("\n");
            builder.append("brand  \t\t\t: ").append(Build.BRAND).append("\n");
            builder.append("device  \t\t: ").append(Build.DEVICE).append("\n");
            builder.append("display  \t\t: ").append(Build.DISPLAY).append("\n");
            builder.append("fingerPrint  \t: ").append(Build.FINGERPRINT).append("\n");
            builder.append("host  \t\t\t: ").append(Build.HOST).append("\n");
            builder.append("model  \t\t\t: ").append(Build.ID).append("\n");
            builder.append("ID  \t\t\t: ").append(Build.MODEL).append("\n");
            builder.append("product  \t\t: ").append(Build.PRODUCT).append("\n");
            builder.append("tags  \t\t\t: ").append(Build.TAGS).append("\n");
            builder.append("TIME  \t\t\t: ").append(new Date(Build.TIME)).append("\n");
            builder.append("USER  \t\t\t: ").append(Build.USER).append("\n");
            builder.append("TYPE  \t\t\t: ").append(Build.TYPE).append("\n").append("\n");
            //
            builder.append("RadioVersion  \t\t\t: ").append(Build.getRadioVersion()).append("\n");

            builder.append("TYPE  \t\t\t: ").append(Build.getRadioVersion()).append("\n");
            //android.os.MemoryFile.
            builder.append("StorageState  : ").append(Environment.getExternalStorageState()).append("\n");
            builder.append("TotalIntMemory\t: ").append(getTotalInternalMemorySize()).append("\n");
            //builder.append("AvailIntMemory\t: ").append(getAvailableInternalMemorySize()).append("\n");

            DisplayMetrics displayMetrics = new DisplayMetrics();
            builder.append("displayPixels  : ").append(displayMetrics.xdpi + "X" + displayMetrics.ydpi).append("\n");
            builder.append("displayDensity  : ").append(displayMetrics.density  +""  ).append("\n");

        } catch (NameNotFoundException e) {
            Log.v("VM", e.getMessage());
        }
        return builder.toString();
    }

    public static int getPortraitsWidthInPixel(@NonNull Activity context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.e("CCCC", "height : " + height + " >>> width : " + width + " >>> smallestScreenWidthDp : " + context.getResources().getConfiguration().smallestScreenWidthDp);
        int toReturn;
        if (width < height) {
            toReturn = width;
        } else {
            toReturn = height;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int smallestPixel;
        if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
            smallestPixel = displayMetrics.widthPixels;
        } else {
            smallestPixel = displayMetrics.heightPixels;
        }
        Log.e("CCCC", " .heightPixels : " + displayMetrics.heightPixels + " >>> widthPixels : " + displayMetrics.widthPixels + " \n >>> density : " + displayMetrics.density + ">>> ydpi : " + displayMetrics.scaledDensity + " >>> densityDpi : " + displayMetrics.densityDpi + " \n >>> xdpi : " + displayMetrics.xdpi + ">>> ydpi : " + displayMetrics.ydpi);
        return smallestPixel;//displayMetrics.widthPixels;
    }

    public static boolean isAppInstalled(String packageName) {
        PackageManager packageManager = ApplicationManager.getAppContext().getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
