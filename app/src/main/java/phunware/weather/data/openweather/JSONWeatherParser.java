package phunware.weather.data.openweather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phunware.weather.data.WeatherData;


/**
 * Class that parses the json response from OpenWeatherMap
 *
 * @author Anu
 */
public class JSONWeatherParser {

    public JSONWeatherParser() {

    }

    /**
     * Parses the response and returns WeatherData
     *
     * @param jsonWeatherResponse response from open weather map
     * @param zipCode             zipcode for which weather data is to be retreived
     * @return weatherdata object
     * @throws JSONException if there's any error while parsing
     */
    public WeatherData parseCurrentWeatherData(String jsonWeatherResponse, String zipCode) throws JSONException {

        WeatherData currentWData = new WeatherData();

        //Current weather response is a JSON object
        JSONObject jsonObj = new JSONObject(jsonWeatherResponse);

        JSONArray weatherArray = jsonObj.getJSONArray("weather");
        JSONObject weatherObj = weatherArray.getJSONObject(0);
        currentWData.setDescription(weatherObj.getString("description"));
        currentWData.setIconID(weatherObj.getString("icon"));

        JSONObject main = jsonObj.getJSONObject("main");
        currentWData.setCurrentTemp((float) main.getDouble("temp"));
        currentWData.setLowTemp((float) main.getDouble("temp_min"));
        currentWData.setHighTemp((float) main.getDouble("temp_max"));
        currentWData.setHumidity((float) main.getDouble("humidity"));
        currentWData.setPressure((float) main.getDouble("pressure"));

        JSONObject wind = jsonObj.getJSONObject("wind");
        currentWData.setWindSpeed((float) wind.getDouble("speed"));

        JSONObject sys = jsonObj.getJSONObject("sys");
        currentWData.setSunrise(sys.getLong("sunrise"));
        currentWData.setSunset(sys.getLong("sunset"));

        currentWData.setTime(System.currentTimeMillis());
        currentWData.setPlace(jsonObj.getString("name"));

        currentWData.setZipCode(zipCode);

        return currentWData;

    }//end parseCurrentWeatherData


    /**
     * @param args
     */
    public static void main(String[] args) throws JSONException {
        String jsonWeatherResponse = "{\"coord\":{\"lon\":-97.81,\"lat\":30.45},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\"," +
                "\"icon\":\"02d\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":88.02,\"pressure\":1016,\"humidity\":59,\"temp_min\":84.2,\"temp_max\":90}," +
                "\"wind\":{\"speed\":5.73,\"deg\":118.5},\"clouds\":{\"all\":20},\"dt\":1472931359,\"sys\":{\"type\":1,\"id\":2558,\"message\":0.0113,\"country\":\"US\",\"sunrise\":1472904586,\"sunset\":1472950197},\"id\":4670783,\"name\":\"Anderson Mill\",\"cod\":200}";
        JSONWeatherParser jsonWeatherParser = new JSONWeatherParser();
        WeatherData currentWeatherData = jsonWeatherParser.parseCurrentWeatherData(jsonWeatherResponse, "78613");
        System.out.println(currentWeatherData);

    }

}//end JSONWeatherParser
