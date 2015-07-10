package com.testinprod.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.testinprod.popularmovies.R;
import com.testinprod.popularmovies.fragments.SettingsFragment;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.flSettings, new SettingsFragment())
                    .commit();
        }


    }


}
