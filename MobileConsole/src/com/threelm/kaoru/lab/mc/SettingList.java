package com.threelm.kaoru.lab.mc;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingList extends ActivityBase {
	private final String TAG = this.getClass().getName();

	public static final String AUTHORITY = "com.threelm.dm";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/settings");
    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.threelm.setting";
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.threelm.setting";
    public static final String KEY = "key";
    public static final String VALUE = "value";
	
	private ArrayAdapter<String> mData;

	OnItemClickListener mClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayAdapter<String>(this,R.layout.list_item);

        try {
	        ContentResolver cr = this.getContentResolver();

	        ContentValues vals = new ContentValues();
	        vals.put(VALUE, "1");
	        int n = cr.update(CONTENT_URI, vals,"key = ?", new String[] {"bluetooth_enable"});
	        Log.d(TAG,String.format("Update result: %d",n));
	        
	        String[] cols = {KEY,VALUE};
	        Cursor c = cr.query(CONTENT_URI, cols, null, null, null);
	        c.moveToFirst();
	        while(c.isAfterLast() == false) {
	        	mData.add(String.format("%s = %s",c.getString(0),c.getString(1)));
	        	c.moveToNext();
	        }
	        c.close();

        } catch (Exception e) {
        	Log.e(TAG,e.toString());
        }
        setListAdapter(mData);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(mClickListener);
    }
}
