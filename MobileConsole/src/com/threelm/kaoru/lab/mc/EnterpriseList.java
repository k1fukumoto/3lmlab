package com.threelm.kaoru.lab.mc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EnterpriseList extends ActivityBase {
	@SuppressWarnings("unused")
	private final String TAG = this.getClass().getName();
	
	private ArrayAdapter<Enterprise> mData;

	OnItemClickListener mClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {
			Intent i = new Intent(EnterpriseList.this, AdminUserList.class);
			i.putExtra("domain",mData.getItem(pos).domain);
			EnterpriseList.this.startActivity(i);
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference pref = Preference.initialize(this.getApplication());
        EnterpriseServer es = EnterpriseServer.initialize(pref);
        
        mData = new ArrayAdapter<Enterprise>(this,R.layout.list_item);
        for(Enterprise e : es.searchForEnterprise(null)) {
        	mData.add(e);
        }

        setListAdapter(mData);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(mClickListener);
    }
}
