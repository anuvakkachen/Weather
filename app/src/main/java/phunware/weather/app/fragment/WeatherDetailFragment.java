package phunware.weather.app.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import phunware.weather.R;
import phunware.weather.WeatherConstants;
import phunware.weather.data.NetworkNotConnectedException;
import phunware.weather.data.WeatherData;
import phunware.weather.data.WeatherGetter;
import phunware.weather.data.WeatherGetterFactory;
import phunware.weather.data.WeatherStorage;
import phunware.weather.util.TimeHelper;


/**
 * A fragment that fetches real time weather data and displays the details
 * for a selected zip code
 * The zip code is passed as a bundle parameter
 */
public class WeatherDetailFragment extends Fragment implements WeatherConstants {

    private String zipCode = null;
    private WeatherData weatherData = null;
    private WeatherGetterTask weatherGetterTask = null;

    //Views in this fragment
    private TextView txtViewPlace = null;
    private TextView txtViewZipCode = null;
    private ImageView imgViewIcon = null;
    private TextView txtCurrentTemp = null;
    private TextView txtViewDescription = null;
    private TextView txtViewHighTemp = null;
    private TextView txtViewLowTemp = null;
    private TextView txtViewHumidity = null;
    private TextView txtViewPressure = null;
    private TextView txtViewWindSpeed = null;
    private TextView txtViewSunrise = null;
    private TextView txtViewSunset = null;

    private View detailPane = null;
    private TextView errorPane = null;

    public WeatherDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_detail, container, false);


        //Find all views in it
        txtViewPlace = (TextView) view.findViewById(R.id.place);
        txtViewZipCode = (TextView) view.findViewById(R.id.zipCode);
        imgViewIcon = (ImageView) view.findViewById(R.id.icon);
        txtCurrentTemp = (TextView) view.findViewById(R.id.currentTemp);
        txtViewDescription = (TextView) view.findViewById(R.id.description);
        txtViewHighTemp = (TextView) view.findViewById(R.id.highTemp);
        txtViewLowTemp = (TextView) view.findViewById(R.id.lowTemp);
        txtViewHumidity = (TextView) view.findViewById(R.id.humidity);
        txtViewPressure = (TextView) view.findViewById(R.id.pressure);
        txtViewWindSpeed = (TextView) view.findViewById(R.id.windSpeed);
        txtViewSunrise = (TextView) view.findViewById(R.id.sunrise);
        txtViewSunset = (TextView) view.findViewById(R.id.sunset);

        detailPane = view.findViewById(R.id.detail_pane);
        errorPane = (TextView) view.findViewById(R.id.error_pane);

        zipCode = getArguments().getString(EXTRA_ZIP_CODE);

        //Get weather data and display it
        weatherGetterTask = new WeatherGetterTask(getContext());
        weatherGetterTask.execute(zipCode);

        return view;
    }//end onCreateView

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weatherData != null && weatherData.getIconBitmap() != null) {
            weatherData.getIconBitmap().recycle();
        }

        if (weatherGetterTask != null) {
            weatherGetterTask.cancel(true);
        }
    }//end onDestroy

    private class WeatherQueryResponse {
        WeatherData weatherData = null;
        String errorMessage = null;
    }//end WeatherQueryResponse


    /**
     * An AsyncTask that fetches weather data from web
     */
    private class WeatherGetterTask extends AsyncTask<String, Void, WeatherQueryResponse> {

        private Context context = null;

        WeatherGetterTask(Context context) {
            this.context = context;
        }


        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected WeatherQueryResponse doInBackground(String... params) {
            String zip = params[0];

            //get current weather information for the specified zip code
            WeatherGetter weatherGetter = WeatherGetterFactory.getWeatherGetterInstance(context);
            WeatherQueryResponse weatherQueryResponse = new WeatherQueryResponse();
            try {
                WeatherData currentWeatherData = weatherGetter.getCurrentWeatherData(zip);
                weatherQueryResponse.weatherData = currentWeatherData;

                //Add this to Storage
                WeatherStorage wStorage = new WeatherStorage(context);
                wStorage.addOrUpdateWeatherData(currentWeatherData);
            } catch (NetworkNotConnectedException e) {
                weatherQueryResponse.errorMessage = e.getMessage();
            } catch (IOException e) {
                weatherQueryResponse.errorMessage = e.getMessage();
            }

            return weatherQueryResponse;
        }//end doInBackground


        @Override
        protected void onPostExecute(WeatherQueryResponse weatherQueryResponse) {
            if (!isCancelled()) {
                if (weatherQueryResponse.weatherData == null) {
                    handleError(weatherQueryResponse.errorMessage);
                } else {
                    weatherData = weatherQueryResponse.weatherData;
                    displayData(weatherQueryResponse.weatherData);
                }
            }
        }//end onPostExecute

        private void handleError(String errorMessage) {
            //hide detail pane and show error pane
            detailPane.setVisibility(View.GONE);
            errorPane.setVisibility(View.VISIBLE);

            if (errorMessage != null) {
                errorPane.setText(errorMessage);
            } else {
                errorPane.setText(BLANK);
            }

        }

        private void displayData(WeatherData weatherData) {
            //hide error pane & show detail pane
            errorPane.setVisibility(View.GONE);
            detailPane.setVisibility(View.VISIBLE);

            //populate fields
            if (weatherData.getPlace() != null) {
                txtViewPlace.setText(weatherData.getPlace());
            }
            if (weatherData.getZipCode() != null) {
                txtViewZipCode.setText(weatherData.getZipCode());
            }

            //show icon
            if (weatherData.getIconBitmap() != null) {
                imgViewIcon.setImageBitmap(weatherData.getIconBitmap());
            }

            if (weatherData.getCurrentTemp() != 0) {
                txtCurrentTemp.setText(BLANK + (int) weatherData.getCurrentTemp() + " " + '\u2109');
            }
            if (weatherData.getDescription() != null) {
                txtViewDescription.setText(weatherData.getDescription());
            }
            if (weatherData.getHighTemp() != 0) {
                txtViewHighTemp.setText(BLANK + (int) weatherData.getHighTemp() + " " + '\u2109');
            }
            if (weatherData.getLowTemp() != 0) {
                txtViewLowTemp.setText(BLANK + (int) weatherData.getLowTemp() + " " + '\u2109');
            }


            if (weatherData.getHumidity() != 0) {
                txtViewHumidity.setText(BLANK + weatherData.getHumidity());
            }
            if (weatherData.getPressure() != 0) {
                txtViewPressure.setText(BLANK + weatherData.getPressure());
            }
            if (weatherData.getWindSpeed() != 0) {
                txtViewWindSpeed.setText(BLANK + weatherData.getWindSpeed());
            }


            TimeHelper timeHelper=new TimeHelper();

            if (weatherData.getSunrise() != 0) {
                txtViewSunrise.setText(timeHelper.getFormattedTimeString(weatherData.getSunrise()));
            }
            if (weatherData.getSunset() != 0) {
                txtViewSunset.setText(timeHelper.getFormattedTimeString(weatherData.getSunset()));
            }

        }//end displayData

    }//end class WeatherGetterTask


}//end WeatherDetailFragment
