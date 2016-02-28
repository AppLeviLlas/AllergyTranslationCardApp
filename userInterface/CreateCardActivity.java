package com.example.paul.allergytravelcardapp.userInterface;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.paul.alergytravelcardapp.R;

public class CreateCardActivity extends AppCompatActivity implements CreateCardFragment.CreateCardListener {

    Configuration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = getResources().getConfiguration();

        //Add the app oicon to the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.app_bar_drawable);

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent newCardIntent = new Intent(CreateCardActivity.this, MainActivity.class);
            startActivity(newCardIntent);
        }else {
            setContentView(R.layout.activity_create_card);
        }
    }

    public void onCreateCardTouched() {
        CreateCardFragment createCardFragment = (CreateCardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_create_card);
    }
}

