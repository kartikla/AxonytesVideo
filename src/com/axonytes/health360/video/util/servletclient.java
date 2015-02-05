package com.axonytes.health360.video.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.apache.http.NameValuePair;
public class servletclient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String url = "https://ttg-apps-pub-dv.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getOnCallSchedule";
		//String url = "https://ttg-apps-pub-dv.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getFacilityList";
		//String url = "https://apiservice-bus-qa.kp.org/service/ncal/tpmg/woc/ClinicianConnectAPI/v1/getFacilityList";
		//String url = "https://ttg-apps-pub-dv.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getFacilitySpecialtyList";
		
	    //String url = "http://localhost:8080/health360video/VideoApi/createMeeting";
	    //String url = "http://localhost:8080/health360video/VideoApi/joinMeeting";
	    String url = "http://localhost:8080/health360video/VideoApi/endMeeting";
		//String url = "https://ttg-apps-pub-qa.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getFacilityList";
		//String url = "https://ttg-apps-pub-qa.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getFacilitySpecialtyList";
		//String url = "https://ttg-apps-pub-qa.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getOnCallSchedule";
//String url = "https://ttg-apps-pub-pp.appl.kp.org:2443/clinicianconnect/clinicianconnectapi/getFacilityList";
		try{ 
		URL myURL = new URL(url);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
		//String userCredentials = "username:password";
		//String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
		//myURLConnection.setRequestProperty ("Authorization", basicAuth);
		
//		myURLConnection.setRequestProperty ("meetingTitle", "testmeet");
//		myURLConnection.setRequestProperty ("firstName", "john");
//		myURLConnection.setRequestProperty ("lastName", "doe");
//		myURLConnection.setRequestProperty ("email", "test@test.com");
		
//		myURLConnection.setRequestProperty ("meetingId", "808568185");		
//		myURLConnection.setRequestProperty ("firstName", "john");
//		myURLConnection.setRequestProperty ("lastName", "doe");
//		myURLConnection.setRequestProperty ("email", "test@test.com");
//		myURLConnection.setRequestProperty ("host", "false");
//		
		myURLConnection.setRequestProperty ("meetingId", "808568185");		
		myURLConnection.setRequestProperty ("email", "test@test.com");
		
		//myURLConnection.setRequestProperty ("participantNuid", "A444120");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("sessionId", "1234"));
		myURLConnection.setRequestMethod("POST");
		myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		//myURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
		myURLConnection.setRequestProperty("Content-Language", "en-US");
		myURLConnection.setUseCaches(false);
		myURLConnection.setDoInput(true);
		myURLConnection.setDoOutput(true);
		OutputStream os = myURLConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(os, "UTF-8"));
		writer.write(getQuery(params));
		writer.flush();
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		}
		writer.close();
		reader.close();

		myURLConnection.connect();
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
}
