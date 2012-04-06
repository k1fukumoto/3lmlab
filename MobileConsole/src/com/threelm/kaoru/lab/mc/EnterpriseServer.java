package com.threelm.kaoru.lab.mc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

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

public class EnterpriseServer extends WebClient {
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
