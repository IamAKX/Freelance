package Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by akash on 10/2/17.
 */

public class DateTimeManager {

    public static String[] yearArray()
    {
        int i,j;
        int currentYear = getCurrentYear();
        String[] pastYearFromNow = new String[currentYear-51];
        pastYearFromNow[0] = "Passing Year";
        for (i = currentYear,j = 1; i > currentYear - 51; i--,j++)
            pastYearFromNow[j] = String.valueOf(i);
        return pastYearFromNow;
    }

    public static int getCurrentYear()
    {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Date getCurrentDate()
    {
        return Calendar.getInstance().getTime();
    }
}
