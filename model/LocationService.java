package com.example.paul.allergytravelcardapp.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.userInterface.CardActivity;
import com.example.paul.allergytravelcardapp.userInterface.MainActivity;

import java.util.List;
import java.util.Locale;

/**
 * Location Service. Class that runs as a start service and listens to location changes every hour and 1km.
 * If the country changes to one that is supported by the app a notification is displayed advising the user that
 * they can make cards for their current country in the relevant language.
 */
public class LocationService extends Service {

    private static final String COUNTRY_FILE = "MyCountryFile";
    private static final String TAG = "LOCATIONSERVICE";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 360f;
    protected Location mLastLocation;
    protected String oldCountry, newCountry = null;
    protected Geocoder geocoder;
    protected LocationListener mLocationListener =
            new LocationListener(LocationManager.GPS_PROVIDER);

    private Context context;
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        context = this;

        geocoder = new Geocoder(this, Locale.getDefault());
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "failed to remove location listeners", ex);
            }

        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            if (geocoder == null) Log.e(TAG, "geocoder is null");
            try {
                Log.e(TAG, "onLocationChanged: " + location);
                mLastLocation.set(location);
            } catch (Exception e) {
                Log.d(TAG, "Connected" + String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude()));
            }
            List<Address> addresses = null;
            try {
                if (geocoder != null) {
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        newCountry = addresses.get(0).getCountryName();
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception");
                e.printStackTrace();
            }
            SharedPreferences countryLoc = getSharedPreferences(COUNTRY_FILE, 0);
            oldCountry = countryLoc.getString("oldCountry", "");
            Log.d(TAG, oldCountry + " " + newCountry);

            if (!oldCountry.equals(newCountry)) {
                String language = CardManager.getLanguage(newCountry, context);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.appl_icon_2)
                        .setContentTitle("ATC: Welcome to " + newCountry)
                        .setContentText("Tap to create allergy cards in " + language);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(context, MainActivity.class);
                // The stack builder object will contain an artificial back stack for the started Activity.
                // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(CardActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(notificationIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(1, mBuilder.build());

                //set the location to stop new notifications each time the activity is connected
                oldCountry = newCountry;
                //persist the country data
                countryLoc = getSharedPreferences(COUNTRY_FILE, 0);
                SharedPreferences.Editor editor = countryLoc.edit();
                editor.putString("oldCountry", oldCountry);
                editor.apply();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}