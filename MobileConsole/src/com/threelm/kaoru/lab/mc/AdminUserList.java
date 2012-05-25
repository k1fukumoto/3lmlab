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
			/* XXX: Show Device List Window */
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_list);

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
