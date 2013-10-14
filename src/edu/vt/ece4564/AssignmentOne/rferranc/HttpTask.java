package edu.vt.ece4564.AssignmentOne.rferranc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.AsyncTask;

/*
 * This code is based on the code given at 
 * http://stackoverflow.com/questions/14418021/get-text-from-web-page-to-string
 */
public class HttpTask extends AsyncTask<String, String, List<WootEvent>> {
	String resultStr;
	ArrayList<WootEvent> eventList;
	public AsyncResponse interfaceNotify;

	@Override
	protected List<WootEvent> doInBackground(String... urls) {
		WootEventParser wootParser = new WootEventParser();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urls[0]);

		HttpResponse execute;
		try {
			execute = client.execute(httpGet);
			JSONParser parser = new JSONParser();
			parser.parse(new BufferedReader(new InputStreamReader(execute
					.getEntity().getContent())), wootParser);
			List<WootEvent> events = wootParser.getEvents();

			return events;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new ArrayList<WootEvent>();
	}

	/*
	 * Makes the array of deals to send to maintask (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(List<WootEvent> result) {
		super.onPostExecute(result);
	
		eventList = (ArrayList<WootEvent>) result;
		resultStr = "";
		
		interfaceNotify.processFinish();
	}

	public ArrayList<WootEvent> getEvents() {
		return eventList;
	}

	public String getResult() {
		return resultStr;
	}

	public void clearVars() {
		resultStr = null;
		eventList = null;
	}
}
