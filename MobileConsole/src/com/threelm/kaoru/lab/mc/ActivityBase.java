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

import android.app.ListActivity;
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