package com.threelm.kaoru.lab.mc;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiManager;
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
		static final String HOST = "192.168.1.10";
		static final int PORT = 8443;
		static final String SUPERUSER_NAME = "superuser@3lm.com";
		static final String SUPERUSER_PASS = "threelm";
	}

	Context  mCtxt;
	SharedPreferences mPrefs;

	OnSharedPreferenceChangeListener mPrefChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
			if(key.equals(KEY.WIFI_ENABLED)) {
				((WifiManager) mCtxt.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(
						prefs.getBoolean(key, false)
						);
			} else if(key.equals(KEY.BLUETOOTH_ENABLED)) {
				BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
				if(prefs.getBoolean(key,false)) {
					ba.enable();
				} else {
					ba.disable();
				}
			}
		}
	};
	
	public String getValue(String key) {
		return mPrefs.getString(key,null);
	}
	
	Preference(Context ctxt) {
		mCtxt = ctxt;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtxt);
		
		mPrefs.registerOnSharedPreferenceChangeListener(this.mPrefChangeListener);
		Editor e = mPrefs.edit();
		{
			if(0 == mPrefs.getAll().size()) {
				e.putString(KEY.HOST, DEFAULT.HOST);
				e.putString(KEY.SUPERUSER_NAME, DEFAULT.SUPERUSER_NAME);
				e.putString(KEY.SUPERUSER_PASS, DEFAULT.SUPERUSER_PASS);
			}
    
			e.putBoolean(KEY.WIFI_ENABLED,((WifiManager)mCtxt.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled());
			e.putBoolean(KEY.BLUETOOTH_ENABLED,BluetoothAdapter.getDefaultAdapter().isEnabled());
		}
		e.commit();
	}	
}
