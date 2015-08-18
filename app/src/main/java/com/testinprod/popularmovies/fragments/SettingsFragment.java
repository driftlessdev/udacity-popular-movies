package com.testinprod.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.testinprod.popularmovies.R;

import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Timber.v("Changed preference for: " + key);
        Preference pref = findPreference(key);
        String valueKey = sharedPreferences.getString(key,"");
        if(pref instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) pref;
            int selected = listPreference.findIndexOfValue(valueKey);
            Timber.v("List Preference with item selected: " + selected);
            if(selected >= 0)
            {
                listPreference.setSummary(listPreference.getEntries()[selected]);
            }
        }
        else
        {
            pref.setSummary(valueKey);
        }
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(LOG_TAG);
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);

        // Prime the pump
        onSharedPreferenceChanged(preferences, getString(R.string.pref_sort_key));
    }

}
