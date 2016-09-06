package phunware.weather.app.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import phunware.weather.R;
import phunware.weather.WeatherConstants;
import phunware.weather.data.WeatherData;
import phunware.weather.data.WeatherStorage;
import phunware.weather.util.TimeHelper;


/**
 * A Fragment that displays a list of zip codes and corresponding place names.
 */
public class WeatherListFragment extends Fragment implements WeatherConstants,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Interface to be implemented in the activity from which zip code will be selected
     */
    public interface ZipCodeSelectionListener {
        abstract void onZipCodeSelected(String zipCode);
    }


    private static final String TAG = "WeatherListFragment";
    private RecyclerView recyclerView = null;
    private WeatherListAdapter weatherListAdapter = null;

    private ZipCodeSelectionListener zipCodeSelectionListener = null;

    public WeatherListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);

        //Get recycler view
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //Set its layout manager
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //set the adapter for this list
        WeatherStorage weatherStorage = new WeatherStorage(getContext());
        weatherListAdapter = new WeatherListAdapter(weatherStorage.getInitialWeatherData());
        recyclerView.setAdapter(weatherListAdapter);

        new WeatherStorage(getContext()).setZipAddedListener(this);

        return view;
    }//end onCreateView

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        zipCodeSelectionListener = (ZipCodeSelectionListener) context;

    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        WeatherData wData = new WeatherData();
        wData.setZipCode(key);
        wData.setPlace(sharedPreferences.getString(key, BLANK));
        weatherListAdapter.addWeatherData(wData);
    }

    /**
     * The adapter that provides data to the weather list
     */
    private class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {


        private ArrayList<WeatherData> arrayWeatherData = new ArrayList<WeatherData>();


        WeatherListAdapter() {
            this.arrayWeatherData = new ArrayList<WeatherData>();
        }

        WeatherListAdapter(ArrayList<WeatherData> initialWeatherData) {
            if (initialWeatherData != null) {
                this.arrayWeatherData = initialWeatherData;
            } else {
                this.arrayWeatherData = new ArrayList<WeatherData>();
            }
        }


        void addWeatherData(WeatherData weatherData) {
            arrayWeatherData.add(weatherData);
            notifyDataSetChanged();
        }


        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p/>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p/>
         * The new ViewHolder will be used to display items of the adapter using
         * onBindViewHolder(ViewHolder, int, List). Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate a new view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_weather_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         * <p/>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p/>
         * Override onBindViewHolder(ViewHolder, int, List) instead if Adapter can
         * handle effcient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            WeatherData wData = arrayWeatherData.get(position);
            if (wData.getPlace() != null) {
                holder.txtViewPlace.setText(wData.getPlace());
            }

            if (wData.getZipCode() != null) {
                holder.txtViewZipCode.setText(wData.getZipCode());
            }

            if (wData.getCurrentTemp() != 0) {
                holder.txtViewCurrentTemp.setText(BLANK + ((int) wData.getCurrentTemp()) + " " + '\u2109');
            }

            if (holder.txtViewLastUpdated != null && wData.getTime() != 0) {
                TimeHelper timeHelper=new TimeHelper();
                holder.txtViewLastUpdated.setText(timeHelper.getFormattedDateString(wData.getTime()));
            }
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return arrayWeatherData.size();
        }

        /**
         * The View holder to hold views in list item
         */
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            //declare the list item views here
            private ImageView imgViewIcon = null;
            private TextView txtViewPlace = null;
            private TextView txtViewZipCode = null;
            private TextView txtViewCurrentTemp = null;
            private TextView txtViewLastUpdated = null;


            public ViewHolder(View itemView) {
                super(itemView);

                //find views and assign them to instance variables
                imgViewIcon = (ImageView) itemView.findViewById(R.id.icon);
                txtViewPlace = (TextView) itemView.findViewById(R.id.place);
                txtViewZipCode = (TextView) itemView.findViewById(R.id.zipCode);
                txtViewCurrentTemp = (TextView) itemView.findViewById(R.id.currentTemp);
                txtViewLastUpdated = (TextView) itemView.findViewById(R.id.lastUpdatedTime);

                //set a click listener for each list item
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                String selectedZip = arrayWeatherData.get(position).getZipCode();

                //Notify the zip code listener
                zipCodeSelectionListener.onZipCodeSelected(selectedZip);
            }

        }//end class ViewHolder

    }//end class WeatherListAdapter

}//end class WeatherListFragment
