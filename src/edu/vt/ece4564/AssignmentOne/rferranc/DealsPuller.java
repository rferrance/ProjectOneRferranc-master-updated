package edu.vt.ece4564.AssignmentOne.rferranc;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/*
 * This code is based on the code given at 
 * http://stackoverflow.com/questions/14418021/get-text-from-web-page-to-string
 */
public class DealsPuller extends AsyncTask<String, String, String> {
	String resultStr;
	ArrayList<WootEvent> eventList;
	public AsyncResponse interfaceNotify;
	
	@Override
    protected String doInBackground(String... urls) {
		String response = "";
		StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    sb.append(s);
                }
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	/*
	 * Makes the array of deals to send to maintask (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        
		eventList = new ArrayList<WootEvent>();
		resultStr = result;
		
		interfaceNotify.dealsFinish();
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
	


