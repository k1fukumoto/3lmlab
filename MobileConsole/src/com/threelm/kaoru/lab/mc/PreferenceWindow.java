/**************************************************************************************
 Copyright 2012 Kaoru Fukumoto All Rights Reserved

 You may freely use and redistribute this script as long as this
 copyright notice remains intact

 DISCLAIMER. THIS SCRIPT IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, WHETHER ORAL OR WRITTEN, EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY
 DISCLAIMS ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY
 QUALITY, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
***************************************************************************************/
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
