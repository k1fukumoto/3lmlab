package com.threelm.kaoru.lab.mc;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AdminUserList extends ActivityBase {
	@SuppressWarnings("unused")
	private final String TAG = this.getClass().getName();
	
	private ArrayAdapter<User> mData;

	OnItemClickListener mClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String domain = this.getIntent().getExtras().getString("domain");
        EnterpriseServer es = EnterpriseServer.getInstance();
        
        mData = new ArrayAdapter<User>(this,R.layout.list_item);
        for(User u : es.getAllAdmins(domain)) {
        	mData.add(u);
        }

        setListAdapter(mData);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(mClickListener);
    }
}
