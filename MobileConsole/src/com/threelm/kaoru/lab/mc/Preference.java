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

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Preference {
	static final String TAG = "Preference";
	static class KEY {
		static final String HOST = "hostname";
		static final String SUPERUSER_NAME = "superuser_name";
		static final String SUPERUSER_PASS = "superuser_pass";
		static final String WIFI_ENABLED = "wifi_enabled";
		static final String BLUETOOTH_ENABLED = "bluetooth_enabled";
	}
	static class DEFAULT {
		static final String HOST = "docomo-jp.3lm.com";
		static final int PORT = 8443;
		static final String SUPERUSER_NAME = "superadmin@docomo.3lm.com";
		static final String SUPERUSER_PASS = "****";
	}

	Application mApp;
	SharedPreferences mPrefs;

	OnSharedPreferenceChangeListener mPrefChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
		}
	};
	
	public String getValue(String key) {
		return mPrefs.getString(key,null);
	}
	
	private static Preference sPref = null;
	
	public static Preference initialize(Application app) {
		sPref = new Preference(app);
		return sPref;
	}
	
	public static Preference getInstance() {
		return sPref;
	}

	private Preference(Application app) {
		mApp = app;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mApp);
		
		mPrefs.registerOnSharedPreferenceChangeListener(this.mPrefChangeListener);
		Editor e = mPrefs.edit();
		{
			if(0 == mPrefs.getAll().size()) {
				e.putString(KEY.HOST, DEFAULT.HOST);
				e.putString(KEY.SUPERUSER_NAME, DEFAULT.SUPERUSER_NAME);
				e.putString(KEY.SUPERUSER_PASS, DEFAULT.SUPERUSER_PASS);
			}
		}
		e.commit();
	}	
}
