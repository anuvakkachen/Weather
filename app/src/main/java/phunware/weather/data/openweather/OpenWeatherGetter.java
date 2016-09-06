package phunware.weather.data.openweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;

import java.io.IOException;

import phunware.weather.data.NetworkNotConnectedException;
import phunware.weather.data.WeatherData;
import phunware.weather.data.WeatherGetter;
import phunware.weather.util.HttpHelper;

/**
 * This class implements the methods required for getting weather data from Open Weather Map
 * It implements WeatherGetter which defines the generic method for weather data retreival
 * Created by Anu
 */
public class OpenWeatherGetter implements WeatherGetter {

    private static final String API_KEY = "c359a9ec83db84367041a5f61c69209a";

    //Example current weather Uri
    //http://api.openweathermap.org/data/2.5/weather?q=Austin&units=imperial&mode=json&APPID=1111
    //Query parameters are explained below
    //q - city, or city,Country code or zip code
    //mode - response format
    //Units=imperial returns temperature in Farenheit
    private static final String currentURI = "http://api.openweathermap.org/data/2.5/weather?units=imperial&mode=json&appid=" + API_KEY;

    //Example url : http://openweathermap.org/img/w/01n.png
    private static final String ICON_BASE_URL = "http://openweathermap.org/img/w/";

    private static final String IMG_EXTENSION=".png";

    private Context context = null;

    public OpenWeatherGetter(Context context) {
        this.context = context;
    }


    /**
     * Returns current weather data from Open Weather Map
     *
     * @param zipCode zipcode for which the weather is to be fetched
     * @return Weater data object
     * @throws IOException                  If any IO error occurs while sending request and receiving response
     * @throws NetworkNotConnectedException if the network is not currently in the connected sate
     */
    @Override
    public WeatherData getCurrentWeatherData(String zipCode) throws IOException, NetworkNotConnectedException {

        //check whether there's network connection
        if (!isNetworkConnected()) {
            throw new NetworkNotConnectedException("No network connection");
        }

        // send request
        String jsonResponse = getCurrentWeatherDataAsJSON(zipCode);

        //parse response
        JSONWeatherParser jParser = new JSONWeatherParser();
        WeatherData weatherData = null;
        try {
            weatherData = jParser.parseCurrentWeatherData(jsonResponse, zipCode);

            //now set icon bitmap
            HttpHelper httpHelper = new HttpHelper();
            weatherData.setIconBitmap(httpHelper.getBitMap(ICON_BASE_URL + weatherData.getIconID() + IMG_EXTENSION));
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
        return weatherData;
    }//end getCurrentWeatherData

    /**
     * Checks whether the device is currently connected to a network
     *
     * @return true if network connectivity exists, false otherwise
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }//end isNetworkConnected

    /**
     * Send a request to weather service provider and get the response JSON
     *
     * @param zipCode zipcode for which weather info is to be retreived
     * @return json response
     * @throws IOException if any error occurs while getting weather data
     */
    private String getCurrentWeatherDataAsJSON(String zipCode) throws IOException {
        //Complete the current weather url by adding zip code and number of days
        StringBuilder urlBuilder = new StringBuilder(currentURI);
        urlBuilder.append("&");
        urlBuilder.append("q=");
        urlBuilder.append(zipCode);

        //send this request
        HttpHelper httpHelper = new HttpHelper();
        String jsonResponse = httpHelper.sendGetRequest(urlBuilder.toString());

        return jsonResponse;
    }//end method getWeatherForecastDataAsJSON


}//end class OpenWeatherGetter
