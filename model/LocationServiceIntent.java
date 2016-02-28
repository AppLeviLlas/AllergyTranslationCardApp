package com.example.paul.allergytravelcardapp.model;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Paul on 27/02/2016.
 */
public class LocationServiceIntent extends IntentService {

        /**
         * A constructor is required, and must call the super IntentService(String)
         * constructor with a name for the worker thread.
         */
        public LocationServiceIntent() {
            super("LocationIntentService");
        }

        /**
         * The IntentService calls this method from the default worker thread with
         * the intent that started the service. When this method returns, IntentService
         * stops the service, as appropriate.
         */
        @Override
        protected void onHandleIntent(Intent intent) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
        }
    }
