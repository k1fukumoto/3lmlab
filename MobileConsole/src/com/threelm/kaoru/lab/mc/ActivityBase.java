package com.threelm.kaoru.lab.mc;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityBase extends ListActivity {
	@SuppressWarnings("unused")
	private final String TAG = this.getClass().getName();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.setting:
			Intent i = new Intent(this,PreferenceWindow.class);
			startActivity(i);
			return true;
		case R.id.threelm_dm:
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
