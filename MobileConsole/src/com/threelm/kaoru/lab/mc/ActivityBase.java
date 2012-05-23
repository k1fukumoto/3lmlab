package com.threelm.kaoru.lab.mc;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityBase extends ListActivity {
	private final String TAG = this.getClass().getName();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			Intent i;
			switch(item.getItemId()) {
			case R.id.setting:
				i = new Intent(this,PreferenceWindow.class);
				startActivity(i);
				return true;
			case R.id.threelm_dm:
				i = new Intent();
				String p = "com.threelm.dm";
				String c = ".UserLoginActivity";
				i.setComponent(new ComponentName(p,p+c));
				startActivity(i);
				return true;
			case R.id.threelm_setting:
				i = new Intent(this,SettingList.class);
				startActivity(i);
				return true;
				
			}
		} catch (Exception e) {
			Log.e(TAG,e.toString());
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
