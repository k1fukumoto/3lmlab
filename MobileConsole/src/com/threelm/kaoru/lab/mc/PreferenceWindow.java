package com.threelm.kaoru.lab.mc;


import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferenceWindow extends PreferenceActivity {
	static final String TAG = "Preference";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preferences);
	}
}
