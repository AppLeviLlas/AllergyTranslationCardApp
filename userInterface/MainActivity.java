package com.example.paul.allergytravelcardapp.userInterface;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.LocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity implements CreateCardFragment.CreateCardListener, CardListFragment.CardListListener {

    public static final String COUNTRY_FILE = "MyCountryFile";
    public static boolean wideLayout;
    protected Button createNewCardButton;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;
    protected ServiceConnection connection = null;
    protected Geocoder gcd;
    protected Handler handler;
    protected Context context;
    private Spinner languageSpinner = null;
    private Spinner allergySpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration configuration = getResources().getConfiguration();
        context = this;

        //Add the app icon to the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.app_bar_drawable);

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
            wideLayout = true;
            languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
            languageSpinner.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spinner_landscape_height);

            allergySpinner = (Spinner) findViewById(R.id.allergySpinner);
            allergySpinner.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spinner_landscape_height);

        } else {
            setContentView(R.layout.activity_main_portrait);
            wideLayout = false;
            createNewCardButton = (Button) findViewById(R.id.createNewCardButton);
            createNewCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newCardIntent = new Intent(MainActivity.this, CreateCardActivity.class);
                    startActivity(newCardIntent, new Bundle());
                }
            });
        }
        //start the location service
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    @Override
    public void onCreateCardTouched() {
        CardListFragment cardListFragment = (CardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_create_card);
    }

    @Override
    public void onCardListTouched() {
        CardListFragment cardListFragment = (CardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_card_list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }
}
