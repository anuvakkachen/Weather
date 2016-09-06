package phunware.weather.data;

import android.content.Context;

import phunware.weather.data.openweather.OpenWeatherGetter;

/**
 * Factory that generates the WeatherGetter instance
 */
public class WeatherGetterFactory {


    /**
     * Returns a new instance of WeatherGetter instance
     */
    public static WeatherGetter getWeatherGetterInstance(Context context) {
        return new OpenWeatherGetter(context);
    }

}//end class WeatherGetterFactory
