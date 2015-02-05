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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

public class TestClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		String url = "https://api.zoom.us/v1/user/custcreate";
//		Map<String, String> inputs = new HashMap<String, String>();
//		String email = "john.doe@test.com";
//		String firstName = "John";
//		String lastName = "Doe";
//		inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
//		inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
//		inputs.put("email", email);
//		inputs.put("type", "2");
//		inputs.put("first_name", firstName);
//		inputs.put("last_name", lastName);
//		inputs.put("disable_recording", "true");
//		inputs.put("disable_feedback", "true");
//		String result = makeRestCall(url, inputs);
//		System.out.println("result = " + result);
		
		//JSONObject j =  parseJson("{\"id\":\"Y14jedlyTjSO8Dx_HFAJtQ\",\"email\":\"john.doe@test.com\",\"first_name\":\"John\",\"last_name\":\"Doe\",\"pic_url\":\"\",\"type\":2,\"disable_chat\":false,\"disable_private_chat\":false,\"enable_e2e_encryption\":false,\"enable_silent_mode\":false,\"disable_group_hd\":false,\"disable_recording\":true,\"enable_large\":false,\"enable_webinar\":false,\"disable_feedback\":true,\"disable_jbh_reminder\":false,\"enable_cmr\":false,\"verified\":1,\"pmi\":3572488956,\"meeting_capacity\":0,\"dept\":null,\"timezone\":null,\"created_at\":\"2015-01-19T14:47:22Z\",\"token\":\"\"}");
		JSONObject j =  parseJson("{\"error\": {\"code\": 1010,\"message\": \"User not belong to this account\"}}");
		System.out.println(j.getJSONObject("error").getInt("code"));
		//System.out.println(j.getString("code"));
	}
	
	private static String makeRestCall(String url, Map<String, String> inputs)
	{
		try{ 
			URL myURL = new URL(url);
			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			//String userCredentials = "username:password";
			//String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
			//myURLConnection.setRequestProperty ("Authorization", basicAuth);
			
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
			writer.write(getQuery(inputs));
			
			writer.flush();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			writer.close();
			reader.close();

			myURLConnection.connect();
			return line;
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		
	}


	private static String getQuery(Map<String, String> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
	
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    if (first)
	            first = false;
	        else
	            result.append("&");
		    
		    result.append(URLEncoder.encode(key, "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(value, "UTF-8"));
		}
	    

	    return result.toString();
	}
	
	private static JSONObject parseJson(String jsonString)
	{
		try
		{
			JSONObject obj = new JSONObject(jsonString);
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
