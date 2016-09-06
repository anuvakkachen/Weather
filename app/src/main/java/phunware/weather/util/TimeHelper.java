package phunware.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for time conversions
 */
public class TimeHelper {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, h:mm a", Locale.US);
    private final SimpleDateFormat dateFormatter2 = new SimpleDateFormat("h:mm a", Locale.US);

    public TimeHelper(){
    }


    public String getFormattedDateString(long timeInMillisecs){
        Date date = new Date();
        date.setTime(timeInMillisecs);
        String formattedDate=dateFormatter.format(date);
        return formattedDate;
    }//end getFormattedDateString

    public String getFormattedTimeString(long timeInMillisecs){
        Date date = new Date();
        date.setTime(timeInMillisecs);
        String formattedDate=dateFormatter2.format(date);
        return formattedDate;
    }//end getFormattedTimeString


}//end class
