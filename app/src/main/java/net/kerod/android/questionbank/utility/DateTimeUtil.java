package net.kerod.android.questionbank.utility;

import com.google.firebase.Timestamp;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    //private static final long ONE_MONTH_IN_MILLIS = 30 * 24 * 60 * 60 * 1000L;

    public static final long ONE_MONTH_IN_MILLIS = 30 * 24 * 60 * 60 * 1000;

    public static final String PATTERN_DATE_FORMAT_SIMPLE = ("MMM dd, yyyy");
    public static final String PATTERN_DATE_FORMAT_REPORT_MONTH_CODE= ("yyyy_MM");
     public static final String PATTERN_DATE_FORMAT_SIMPLE_WITH_TIME = ("MMM dd, HH:mm");
    public static final String PATTERN_DATE_FORMAT_CONCATENATED = ("yyMMddHHmmssSSS");
    //
    public static final SimpleDateFormat DATE_FORMAT_SIMPLE = new SimpleDateFormat(PATTERN_DATE_FORMAT_SIMPLE);
    public static final SimpleDateFormat DATE_FORMAT_CONCATENATED = new SimpleDateFormat(PATTERN_DATE_FORMAT_CONCATENATED);
    public static final SimpleDateFormat DATE_FORMAT_SIMPLE_WITH_TIME = new SimpleDateFormat(PATTERN_DATE_FORMAT_SIMPLE_WITH_TIME);
    public static final SimpleDateFormat DATE_FORMAT_REPORT_MONTH_CODE = new SimpleDateFormat(PATTERN_DATE_FORMAT_REPORT_MONTH_CODE);
     //
    public static final long MINIMUM_DATE_TIME_IN_MILLIS = DateTimeUtil.minimumDateTime();

    public static String toReadableString(Long timeStamp, boolean showTime) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        Date dateTime = new Date(timeStamp);
        return toReadableString(dateTime, showTime);
    }

    public static long getTimeFromString(String inputDateTime) {
        return getTimeFromString(inputDateTime, "yyyy-MM-dd");

    }

    public static long getTimeFromString(String inputDateTime, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(inputDateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getTimeStampAsConcatenatedString(Date date) {

        return DATE_FORMAT_CONCATENATED.format((date == null) ? new Date() : date);

    }
    public static String getMonthCodeForReport() {
        return DATE_FORMAT_REPORT_MONTH_CODE.format(new Date());
    }

    public static String getWeekCodeForReport() {

        Calendar cal = Calendar.getInstance();
        int year= cal.get(Calendar.YEAR);
        int week= cal.get(Calendar.WEEK_OF_YEAR);
         return year+"_"+(week<10?"0":"")+week;
    }
    public static String getTimeStampAsConcatenatedString() {
        return getTimeStampAsConcatenatedString(new Date());

    }

    public static String toReadableString(Timestamp timestamp, boolean showTime) {
        if (timestamp == null) {
            return "";
         } else {
            return toReadableString(timestamp.toDate(),showTime);

        }
    }
    public static String toReadableString(Date dateTime, boolean showTime) {
        if (dateTime == null) {
            return "";
        } else if (showTime) {
            return DATE_FORMAT_SIMPLE_WITH_TIME.format(dateTime);
        } else {
            return DATE_FORMAT_SIMPLE.format(dateTime);

        }
    }

    public static final Calendar minimumCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);//january 1 1970
        return calendar;
    }

    private static final long minimumDateTime() {
        return 0L;
    }

    public static final long currentDateTime() {
        return new Date().getTime();
    }
    public static final Timestamp currentTimestamp() {
        return  Timestamp.now() ;
    }

    public static Calendar toCalendar(Long timeInMillis) {
        if(timeInMillis==null || timeInMillis<0){timeInMillis=MINIMUM_DATE_TIME_IN_MILLIS;}
        return toCalendar(new Date(timeInMillis));
    }

    public static boolean isFutureDate(long timeInMillis) {

        return currentDateTime() < timeInMillis;
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime((date));
        return cal;
    }

    @NonNull
    public static Calendar getCalendar(int year, int monthOfYear, int dayOfMonth) {
        Calendar calInstallationDate = Calendar.getInstance();
        calInstallationDate.set(Calendar.YEAR, year);
        calInstallationDate.set(Calendar.MONTH, monthOfYear);
        calInstallationDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calInstallationDate;
    }
}
