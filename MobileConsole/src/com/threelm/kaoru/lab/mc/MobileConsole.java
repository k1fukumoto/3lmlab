package com.threelm.kaoru.lab.mc;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MobileConsole extends ListActivity {
	private final String TAG = "MobileConsole";

	private ArrayAdapter<String> mData;
	private Preference mPref;

	OnItemClickListener mClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
                	int position, long id) {
      	  Log.d(TAG,String.format("id:%d, pos:%d, text:%s",id,position,mData.getItem(position)));
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPref = new Preference(this);
        
        EnterpriseServer es = new EnterpriseServer(mPref.getValue(Preference.KEY.HOST),8443);
        es.login(mPref.getValue(Preference.KEY.SUPERUSER_NAME),
        		 mPref.getValue(Preference.KEY.SUPERUSER_PASS));
        
        mData = new ArrayAdapter<String>(this,R.layout.list_item);
        for(Enterprise e : es.searchForEnterprise(null)) {
        	mData.add(e.name);
        }

        setListAdapter(mData);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(mClickListener);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.setting:
			Intent i = new Intent();
			i.setClassName(this, "com.threelm.kaoru.lab.mc.PreferenceWindow");
			startActivity(i);
			return true;
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
