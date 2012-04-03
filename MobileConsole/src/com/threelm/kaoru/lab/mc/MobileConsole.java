package com.threelm.kaoru.lab.mc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class WebClient extends DefaultHttpClient {
	private final String TAG = "WebClient";

	WebClient() {
		super();
		SSLSocketFactory sf = (SSLSocketFactory)this.getConnectionManager()
			.getSchemeRegistry().getScheme("https").getSocketFactory();
		sf.setHostnameVerifier(new AllowAllHostnameVerifier());
	}

	class PostParam {
		String url;
		JSONObject json;
		PostParam(String u,JSONObject j) {url=u; json=j;}
	}

	class PostTask extends AsyncTask<PostParam, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(PostParam... args) {
			PostParam p = args[0];
			
			HttpClient client = (HttpClient)new WebClient();
			HttpPost post = new HttpPost(p.url);
			try {
				List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>(1);
				pairs.add(new BasicNameValuePair("json",p.json.toString()));
				post.setEntity(new UrlEncodedFormEntity(pairs));					
					
				HttpResponse execute = client.execute(post);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

				String resp = "";
				String s = "";
				while ((s = buffer.readLine()) != null) { resp += s; }
				Log.d(TAG,resp);
				return (JSONObject) new JSONTokener(resp).nextValue();
			} catch (Exception e) {
				Log.e(TAG,e.toString());
			}
			return null;
		}
	}
}

class Enterprise {
	int id;
	String name;
	String domain;
	int userActivations;
	int maxActivations;
}

class EnterpriseServer extends WebClient {
	private final String TAG = "EnterpriseServer";

	private String sessionToken = null;
	private String host = null;
	private int port = 0;
	private String urlbase = null;
	
	EnterpriseServer(String h, int p) {
		host = h;
		port = p;
		urlbase = String.format("https://%s:%d/api",host,port);
	}
	
	
	String getUrl(String cmd) {
		return String.format("%s/%s?",urlbase,cmd);
	}
	
	void login(String user,String pass) {
		PostTask pt = new PostTask();

		JSONObject param = new JSONObject();
		try {
			param.put("username", user);
			param.put("password", pass);

			pt.execute(new PostParam(getUrl("getSessionToken"),param));
			sessionToken = pt.get().getString("sessionToken");
		} catch (Exception e) {
			Log.e(TAG,e.toString());
		}
	}
	
	ArrayList<Enterprise> searchForEnterprise(String domain) {
		ArrayList<Enterprise> ret = new ArrayList<Enterprise>();
		PostTask pt = new PostTask();

		JSONObject param = new JSONObject();
		JSONObject ent = new JSONObject();
		try {
			param.put("sessionToken", sessionToken);
			if(domain != null) {
				ent.put("domain",domain);
			}
			param.put("enterprise",ent);

			pt.execute(new PostParam(getUrl("searchForEnterprise"),param));
			JSONArray arr = pt.get().getJSONArray("enterprises");
			for(int i=0; i<arr.length(); i++){
				JSONObject j = arr.getJSONObject(i);
				Enterprise e = new Enterprise();
				e.name = j.getString("name");
				e.domain = j.getString("domain");
				ret.add(e);
			}
		} catch(Exception e) {
			Log.e(TAG,e.toString());
		}
		return ret;
	}
}

public class MobileConsole extends ListActivity {
	private final String TAG = "APITesterActivity";

	static class C {
		static class KEY {
			static final String HOST = "hostname";
			static final String SUPERUSER_NAME = "superuser_name";
			static final String SUPERUSER_PASS = "superuser_pass";
		}
		static class DEFAULT {
			static final String HOST = "192.168.1.10";
			static final int PORT = 8443;
			static final String SUPERUSER_NAME = "superuser@3lm.com";
			static final String SUPERUSER_PASS = "threelm";
		}
	}
	private ArrayAdapter<String> mData;

	OnItemClickListener mClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
                	int position, long id) {
      	  Log.d(TAG,String.format("id:%d, pos:%d, text:%s",id,position,mData.getItem(position)));
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(0 == prefs.getAll().size()) {
        	Editor e = prefs.edit();
        	e.putString(C.KEY.HOST, C.DEFAULT.HOST);
        	e.putString(C.KEY.SUPERUSER_NAME, C.DEFAULT.SUPERUSER_NAME);
        	e.putString(C.KEY.SUPERUSER_PASS, C.DEFAULT.SUPERUSER_PASS);
        	e.commit();
        }

        EnterpriseServer es = new EnterpriseServer
        	(prefs.getString(C.KEY.HOST, C.DEFAULT.HOST),8443);
        es.login(prefs.getString(C.KEY.SUPERUSER_NAME,C.DEFAULT.SUPERUSER_NAME),
        		prefs.getString(C.KEY.SUPERUSER_PASS, C.DEFAULT.SUPERUSER_PASS));
        
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
			i.setClassName(this, "com.threelm.kaoru.lab.mc.Preference");
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
