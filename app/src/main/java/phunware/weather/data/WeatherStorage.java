package phunware.weather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import phunware.weather.R;
import phunware.weather.WeatherConstants;

/**
 * Class that stores weather data persistently
 * Here data is stored in SharedPreferences
 * Created by Anu
 */
public class WeatherStorage implements WeatherConstants, SharedPreferences.OnSharedPreferenceChangeListener {

    //Data to be used to prepopulate in case there's nothing saved yet
    //This data is taken from string resouce
    private static String[] ZIP_CODES = null;
    private static String[] PLACES = null;


    //Shared Preference file that stores zip codes
    private static final String SHARED_PREF_FILE_ZIP_CODES = "FILE_ZIP_CODES";

    //This is set to true after we check shared preferences file to see if it has any data
    //If there isn't any data, then we will add 3 zip codes to it
    private static boolean dataInitialized = false;

    private Context context = null;


    private static ArrayList<SharedPreferences.OnSharedPreferenceChangeListener> zipAddedListeners = new ArrayList<
            SharedPreferences.OnSharedPreferenceChangeListener>();

    public WeatherStorage(Context context) {
        this.context = context;
        if (!dataInitialized) {
            prepopulateZipCodes();
            dataInitialized = true;
        }
    }

    /**
     * This method opens preferences files that contains all zip codes and checks if it's empty
     * If it is then add 3 pre-defined zip codes and it's place names
     * Does nothing if the file is non-empty
     */
    private void prepopulateZipCodes() {

        Resources res = context.getResources();
        ZIP_CODES = res.getStringArray(R.array.zip_code_array);
        PLACES = res.getStringArray(R.array.places_array);

        //check if there's any data in preferences already
        //if not then populate
        SharedPreferences sharedPrefsZipCodes = context.getSharedPreferences(SHARED_PREF_FILE_ZIP_CODES, Context.MODE_PRIVATE);

        //Get all values from zip code file
        Map allZipCodes = sharedPrefsZipCodes.getAll();

        if (allZipCodes.isEmpty()) {
            //populate data
            SharedPreferences.Editor prefEditor = sharedPrefsZipCodes.edit();
            for (int i = 0; i < ZIP_CODES.length; ++i) {
                prefEditor.putString(ZIP_CODES[i], PLACES[i]);
            }
            prefEditor.commit();
        }

        sharedPrefsZipCodes.registerOnSharedPreferenceChangeListener(this);
    }//end prepopulateZipCodes

    /**
     * Get available weather data from storage
     *
     * @return weather information as WeatherData objects
     */
    public ArrayList<WeatherData> getInitialWeatherData() {
        //Read from SharedPreferences
        SharedPreferences sharedPrefsZipCodes = context.getSharedPreferences(SHARED_PREF_FILE_ZIP_CODES, Context.MODE_PRIVATE);
        //Get all values from zip code file
        Map allZipCodes = sharedPrefsZipCodes.getAll();

        ArrayList<WeatherData> allWeatherData = new ArrayList<WeatherData>();

        Set zipCodes = allZipCodes.keySet();
        for (Object zip : zipCodes) {

            String place = (String) allZipCodes.get(zip);

            WeatherData weatherData = new WeatherData();
            weatherData.setZipCode((String) zip);
            weatherData.setPlace(place);

            //Get the preference file for the zip code if any
            SharedPreferences sharedPrefsForSelectedZipCode = context.getSharedPreferences((String) zip, Context.MODE_PRIVATE);
            weatherData.setCurrentTemp(sharedPrefsForSelectedZipCode.getFloat(PREF_KEY_CURRENT_TEMP, 0));
            weatherData.setTime(sharedPrefsForSelectedZipCode.getLong(PREF_KEY_TIME, 0));

            //add to arraylist
            allWeatherData.add(weatherData);
        }
        return allWeatherData;
    }//end getInitialWeatherData


    /**
     * Add newly retreived weather information to SharedPreferences
     *
     * @param weatherData
     */
    public void addOrUpdateWeatherData(WeatherData weatherData) {
        //add it to shared preferences file FILE_ZIP_CODES
        SharedPreferences sharedPrefsAllZipCodes = context.getSharedPreferences(SHARED_PREF_FILE_ZIP_CODES, Context.MODE_PRIVATE);

        if (!sharedPrefsAllZipCodes.contains(weatherData.getZipCode())) {
            SharedPreferences.Editor prefEditor = sharedPrefsAllZipCodes.edit();
            prefEditor.putString(weatherData.getZipCode(), weatherData.getPlace());
            prefEditor.commit();
        }

        //get shared preference file corresponding to this zip code
        SharedPreferences sharedPrefsZipCode = context.getSharedPreferences(weatherData.getZipCode(), Context.MODE_PRIVATE);
        SharedPreferences.Editor zipPrefEditor = sharedPrefsZipCode.edit();

        zipPrefEditor.putString(PREF_KEY_ZIP_CODE, weatherData.getZipCode());
        zipPrefEditor.putString(PREF_KEY_PLACE, weatherData.getPlace());
        zipPrefEditor.putLong(PREF_KEY_TIME, weatherData.getTime());
        zipPrefEditor.putString(PREF_KEY_DESCRIPTION, weatherData.getDescription());
        zipPrefEditor.putString(PREF_KEY_ICON_ID, weatherData.getIconID());
        zipPrefEditor.putFloat(PREF_KEY_CURRENT_TEMP, weatherData.getCurrentTemp());
        zipPrefEditor.putFloat(PREF_KEY_HIGH_TEMP, weatherData.getHighTemp());
        zipPrefEditor.putFloat(PREF_KEY_LOW_TEMP, weatherData.getLowTemp());
        zipPrefEditor.putFloat(PREF_KEY_HUMIDITY, weatherData.getHumidity());
        zipPrefEditor.putFloat(PREF_KEY_PRESSURE, weatherData.getPressure());
        zipPrefEditor.putFloat(PREF_KEY_WINDSPEED, weatherData.getWindSpeed());
        zipPrefEditor.putLong(PREF_KEY_SUNRISE, weatherData.getSunrise());
        zipPrefEditor.putLong(PREF_KEY_SUNSET, weatherData.getSunset());
        zipPrefEditor.commit();

    }//end addOrUpdateWeatherData


    public void setZipAddedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        zipAddedListeners.add(listener);
    }//end setZipAddedListener


    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : zipAddedListeners) {
            listener.onSharedPreferenceChanged(sharedPreferences, key);
        }
    }
}//end class WeatherStorage
