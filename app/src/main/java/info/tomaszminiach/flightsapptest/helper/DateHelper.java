package info.tomaszminiach.flightsapptest.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tomek on 2016-06-22.
 */
public class DateHelper {


    public static final String serverDateFormatString =  "yyyy-MM-dd";
    public static final SimpleDateFormat serverDateFormat = new SimpleDateFormat(serverDateFormatString, Locale.getDefault());
    public static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static String formatToDisplay(Calendar calendar){
        if(calendar==null){
            return "-";
        }
        return displayDateFormat.format(calendar.getTime());
    }

    public static String formatForServer(Calendar calendar){
        if(calendar==null){
            return null;
        }
        return serverDateFormat.format(calendar.getTime());
    }

    public static String formatToDisplay(Date date){
        if(date==null){
            return "-";
        }
        return displayDateFormat.format(date);
    }

    public static String formatForServer(Date date){
        if(date==null){
            return null;
        }
        return serverDateFormat.format(date);
    }

    public static Calendar getCurrentDateMidnight(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

}
