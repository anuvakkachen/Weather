package phunware.weather.data;

import java.io.IOException;


/**
 * Defines the methods that will be used for getting real time weather data
 * Created by Anu on 9/2/2016.
 */
public interface WeatherGetter {

    /**
     * Gets curret weather details for the specified zip code
     *
     * @param zipCode zipcode for which the weather is to be fetched
     * @return weather details as WeatherData object
     * @throws IOException
     */
    WeatherData getCurrentWeatherData(String zipCode) throws NetworkNotConnectedException, IOException;

}//end class WeatherGetter
