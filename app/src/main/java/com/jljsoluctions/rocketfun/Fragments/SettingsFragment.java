package com.jljsoluctions.rocketfun.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jljsoluctions.rocketfun.R;

/**
 * Created by jever on 19/11/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource

        addPreferencesFromResource(R.xml.settings);
    }
}
