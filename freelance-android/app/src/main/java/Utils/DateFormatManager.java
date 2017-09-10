package Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by akash on 25/4/17.
 */

public class DateFormatManager {

    public static String utcTODate(String date, String format)
    {
        DateFormat dF = null;
        Date d = null;
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            d = utcFormat.parse(date);
            dF = new SimpleDateFormat(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dF.format(d);
    }

    public static String dateToUTC(String date)
    {
        DateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = dF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat.format(d);
    }

    public static String dateFormatter(Date date, String format)
    {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date dateFromUTC(String utc)
    {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return utcFormat.parse(utc);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static long dateDifference(Date now, Date future)
    {
        return future.getTime()-now.getTime();
    }
}
