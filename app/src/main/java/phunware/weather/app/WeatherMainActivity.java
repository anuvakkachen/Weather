package phunware.weather.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import phunware.weather.R;
import phunware.weather.WeatherConstants;
import phunware.weather.app.fragment.WeatherDetailFragment;
import phunware.weather.app.fragment.WeatherListFragment;


/**
 * This activity display a zip code list in portrait mode and an additional details pane in landscape
 * When a zip code is selected, realt time weather data is fetched and displayed in the detail pane
 */
public class WeatherMainActivity extends AppCompatActivity implements WeatherConstants, WeatherListFragment.ZipCodeSelectionListener {

    private boolean isDualPaneMode = false;

    private String selectedZip = "";

    private boolean isShowingDialog = false;
    private ZipCodeAlert zipCodeAlert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);

        //Restore dialog
        if (savedInstanceState != null) {
            selectedZip = savedInstanceState.getString(SELECTED_ZIP);
        }

        //Check whether this activity is in dual pane mode. Will be true if orientation is landscape
        isDualPaneMode = (findViewById(R.id.right_pane_details) != null);

        Fragment leftPaneFragment = getSupportFragmentManager().findFragmentById(R.id.left_pane_zip);
        Fragment detailFragment = getSupportFragmentManager().findFragmentById(R.id.right_pane_details);

        if (isDualPaneMode) {
            if (leftPaneFragment != null) {
                if (leftPaneFragment instanceof WeatherDetailFragment) {
                    getSupportFragmentManager().popBackStackImmediate();
                    if (selectedZip != null) {
                        onZipCodeSelected(selectedZip);
                    }
                } else {
                    showZipCodeList();
                    if (selectedZip != null) {
                        onZipCodeSelected(selectedZip);
                    }
                }
            } else {
                showZipCodeList();
            }
        } else {
            //Add zip code list fragment
            showZipCodeList();
        }

        //Restore dialog
        if (savedInstanceState != null) {
            isShowingDialog = savedInstanceState.getBoolean(IS_SHOWING_DIALOG);
            if (isShowingDialog) {
                showZipCodeInputDialog();
            }
        }

        final Context context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showZipCodeInputDialog();
            }
        });
    }//end onCreate

    @Override
    protected void onPause() {
        super.onPause();
        if (isShowingDialog) {
            if (zipCodeAlert != null) {
                zipCodeAlert.dismiss();
            }
        }
    }//end onPause

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_SHOWING_DIALOG, isShowingDialog);
        outState.putString(SELECTED_ZIP, selectedZip);
    }//end onSaveInstanceState

    private void showZipCodeInputDialog() {
        zipCodeAlert = new ZipCodeAlert(this);
        zipCodeAlert.show();
        isShowingDialog = true;
    }//end showZipCodeInputDialog

    private void showZipCodeList() {
        //Add zip code list fragment
        WeatherListFragment weatherListFragment = new WeatherListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.left_pane_zip, weatherListFragment).commit();
    }//end showZipCodeList


    private void showDetailPane(String zip) {
        selectedZip = zip;

        //for dual pane mode add the second fragment
        WeatherDetailFragment weatherDetailFragment = new WeatherDetailFragment();
        Bundle bundleWithArguments = new Bundle();
        bundleWithArguments.putString(EXTRA_ZIP_CODE, zip);
        weatherDetailFragment.setArguments(bundleWithArguments);

        if (isDualPaneMode) {
            getSupportFragmentManager().beginTransaction().replace(R.id.right_pane_details, weatherDetailFragment).commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.left_pane_zip, weatherDetailFragment);
            //add this transaction to back stack
            transaction = transaction.addToBackStack(null);
            transaction.commit();
        }
    }//end showDetailPane


    @Override
    public void onZipCodeSelected(String zip) {
        showDetailPane(zip);
    }//end onZipCodeSelected

    private class ZipCodeAlert implements DialogInterface.OnClickListener {

        private AlertDialog zipAlertDialog = null;
        private EditText editTextZipCode = null;

        ZipCodeAlert(Context context) {
            //create the zip code alert dialog
            buildZipAlert(context);
        }

        /**
         * Build an AlertDialog that has an input text field in it
         * Don't show this alert yet
         */
        private void buildZipAlert(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(R.string.zip_alert_dialog_title);

            //set the view for this dialog
            editTextZipCode = new EditText(context);
            editTextZipCode.setInputType(InputType.TYPE_CLASS_NUMBER);

            builder.setView(editTextZipCode);

            //Button Add
            builder.setPositiveButton(R.string.add, this);
            //Button Cancel
            builder.setNegativeButton(R.string.cancel, this);

            zipAlertDialog = builder.create();
        }//end buildZipAlert


        void show() {
            if (zipAlertDialog != null) {
                zipAlertDialog.show();
            }
        }//end show

        void dismiss() {
            if (zipAlertDialog != null) {
                zipAlertDialog.dismiss();
            }
            isShowingDialog = false;
        }//end show


        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //get the zip code
                    if (editTextZipCode != null) {
                        String zip = editTextZipCode.getText().toString().trim();
                        if (!zip.equals(BLANK)) {
                            onZipCodeSelected(zip);
                        }
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //do nothing.
                    break;
            }
            isShowingDialog = false;
            dialog.dismiss();
        }//end onClick

    }//end class ZipCodeAlert

}//end class WeatherMainActivity
