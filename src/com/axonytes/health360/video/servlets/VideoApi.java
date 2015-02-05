package com.axonytes.health360.video.servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class VideoApi
 */
@WebServlet("/VideoApi/*")
public class VideoApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String operation = "";
    Map<String, Integer> operationsMap = new HashMap<String,Integer>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VideoApi() {
        super();
        // TODO Auto-generated constructor stub
        
        operationsMap.put("createMeeting", 0);
        operationsMap.put("joinMeeting", 1);
        operationsMap.put("endMeeting", 2);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		if ( request.getPathInfo() != null)
		{
			if ( request.getPathInfo().indexOf("/") != -1 )
			{
				operation = request.getPathInfo().replace("/", "");
			}
			else
				operation = request.getPathInfo();
		}
		else
			throw new ServletException("operation path not found");
		
		StringBuffer resultBuffer = null;
		String errorMessage;
		String result = "";
		if ( operation != null && operation.length() > 0)
		{	
			System.out.println("operation=" + operation + " value = " + operationsMap.get(operation));
			
			switch(operationsMap.get(operation))
			{
				case 0:
					result = createMeeting(request,response);
					break;
				case 1:
					result = joinMeeting(request,response);
					break;
				case 2:
					result = endMeeting(request,response);
					break;
				default:
					System.out.println("Unable to understand operation");
					resultBuffer = new StringBuffer();
					errorMessage = "Unable to understand operation";
					resultBuffer.append("{\"service\":{\"name\":\"" + operation + "\",\"status\":{\"code\":\"501\",\"message\":\"" + errorMessage + "\"},\"envelope\":{}}}");
					result =  resultBuffer.toString();
					break;
			}
		}
		
		output(result,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}
	
	private void output(String result, HttpServletResponse response)
	{
		if(result != null){
			//logger.info("result before trim: " + result);
			result = result.trim();
			System.out.println("result after trim: " + result);
		}
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
		
		try {
			PrintWriter out;
		
			out = response.getWriter();
			out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private String createMeeting(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println("in createMeeting");
			String hostid = checkIfUserExists(request, response);
			System.out.println("hostid from checkIfUserExists " + hostid);
			if ( hostid == "")
			{
				System.out.println("before calling createUser");
				hostid = createUser(request, response);
			}
			String result = "";
			if ( hostid != null && hostid.length() > 0)
			{
				System.out.println("before calling createMeeting");
				JSONObject jsonObj = createMeeting(request, response, hostid);
				int meetingId = jsonObj.getInt("id");
				System.out.println("meeting id = " + meetingId);
				//String startUrl = jsonObj.getString("start_url");
				//String joinUrl = jsonObj.getString("join_url");
				//String host = request.getHeader("host");
				String firstName = request.getHeader("firstName");
				String lastName = request.getHeader("lastName");
				System.out.println("firstName=" + firstName + " lastName=" + lastName);
				jsonObj = getUserDetails(request,response);
				System.out.println("after calling getUserDetails");
				String token = "";
				if ( jsonObj.getString("token") != null)
				{
					token = jsonObj.getString("token");
					System.out.println("token=" + token);
				}
				
				String startUrl = "zoommtg://zoom.us/start?confno=" + meetingId + "&zc=0&stype=100&sid=lBGPGzGdT8-2Yf3kjDY5gg&uid=" +hostid+ "&token=" + token + "&uname=" + lastName + "," + firstName;
				System.out.println("startUrl = "  + startUrl);
				result = "{\"service\":{\"name\":\"" + operation + "\",\"status\":{\"code\":\"200\",\"message\":\"success\"},\"envelope\":{\"joinUrl\":\"" + startUrl + "\",\"meetingid\":\"" + meetingId + "\",\"hostid\":\"" + hostid + "\"}}}";
			}
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private String joinMeeting(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println("in joinMeeting");
			String meetingId = request.getHeader("meetingId");
			String firstName = request.getHeader("firstName");
			String lastName = request.getHeader("lastName");
			String hostid = "";
			String email = "";
			String token = "";
			String url = "";
			String uuid = "";
			String host = "";
			if ( request.getHeader("email") != null)
				email = request.getHeader("email");
			if ( request.getHeader("host") != null)
				host = request.getHeader("host");
			
			System.out.println("meetingId=" + meetingId);
			System.out.println("firstName=" + firstName);
			System.out.println("lastName=" + lastName);
			System.out.println("email=" + email);
			JSONObject jsonObj = getUserDetails(request, response);
			System.out.println("after calling userdetails  ");
			if ( jsonObj != null)
			{
				hostid = jsonObj.getString("id");
				token = jsonObj.getString("token");
				System.out.println("hostid="+hostid);
				System.out.println("token="+token);
				JSONObject meetingObject = getMeetingDetails(request,response,hostid);
				
				uuid = meetingObject.getString("uuid");
				
				System.out.println("uuid=" + uuid);
			}
			if (host.length() > 0 && host.equalsIgnoreCase("true") )
				url = "zoommtg://zoom.us/start?confno=" + meetingId + "&zc=0&stype=100&sid=" + uuid + "&uid=" +hostid+ "&token=" + token + "&uname=" + lastName + "," + firstName;
			else
				url = "zoommtg://zoom.us/join?confno="+meetingId+ "&zc=0&uname=" + lastName + "," + firstName;
			System.out.println("url = "  + url);
			String result = "{\"service\":{\"name\":\"" + operation + "\",\"status\":{\"code\":\"200\",\"message\":\"success\"},\"envelope\":{\"joinUrl\":\"" + url + "\"}}}";
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private String endMeeting(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println(" in endMeeting");
			String meetingId = request.getHeader("meetingId");
			
			JSONObject usrObj = getUserDetails(request, response);
			String hostId = usrObj.getString("id");
			
			System.out.println("meetingId="+meetingId + "hostId=" + hostId);
			
			if ( hostId == null || hostId.length() ==0)
			{
				 String result = "{\"service\":{\"name\":\"" + operation + "\",\"status\":{\"code\":\"501\",\"message\":\"check the email address for host.\"},\"envelope\":{}}}";
				 return result;
			}
			String url = "https://api.zoom.us/v1/meeting/end";
			Map<String, String> inputs = new HashMap<String, String>();
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("host_id", hostId);
			inputs.put("id", meetingId);
			makeRestCall(url, inputs);
			String result = "{\"service\":{\"name\":\"" + operation + "\",\"status\":{\"code\":\"200\",\"message\":\"success\"},\"envelope\":{}}}";
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private String makeRestCall(String url, Map<String, String> inputs)
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
			String result = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				result = result + line;
			}
			writer.close();
			reader.close();

			myURLConnection.connect();
			return result;
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		
	}
	
	private  String getQuery(Map<String, String> params) throws UnsupportedEncodingException
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
	
	private JSONObject parseJson(String jsonString)
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
	
	
	
	private String checkIfUserExists(HttpServletRequest request, HttpServletResponse response)
	{
		String id = "";
		try
		{
			System.out.println("in checkIfUserExists");
			String url = "https://api.zoom.us/v1/user/getbyemail";
			Map<String, String> inputs = new HashMap<String, String>();
			String email = request.getHeader("email");
			System.out.println("email=" + email);
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("email", email);
			inputs.put("login_type", "99");
			
			String result = makeRestCall(url, inputs);
			int code = 0;
			JSONObject jsonObj = parseJson(result);
			if ( result.indexOf("error") != -1)
			{
				if ( jsonObj.getJSONObject("error") != null)
				{
					code = jsonObj.getJSONObject("error").getInt("code");
					System.out.println("Error Code = " +code);
					return id;
				}
			}
			
			
			id = jsonObj.getString("id");
			System.out.println("id=" + id);
			return id;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return id;
		}
	}
	
	private String createUser(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println("in createUser");
			Map<String, String> inputs = new HashMap<String, String>();
			String url = "https://api.zoom.us/v1/user/custcreate";
			
			String email = request.getHeader("email");
			String firstName = request.getHeader("firstName");
			String lastName = request.getHeader("lastName");
			System.out.println("email="+ email);
			System.out.println("firstName="+firstName);
			System.out.println("lastName="+lastName);
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("email", email);
			inputs.put("type", "2");
			inputs.put("first_name", firstName);
			inputs.put("last_name", lastName);
			inputs.put("disable_recording", "true");
			inputs.put("disable_feedback", "true");
			String result = makeRestCall(url, inputs);
			
			JSONObject jsonObj = parseJson(result);
			String id = jsonObj.getString("id");
			System.out.println("id=" +id);
			return id;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject createMeeting(HttpServletRequest request, HttpServletResponse response, String id)
	{
		try
		{
			System.out.println("in createMeeting");
			Map<String, String> inputs = new HashMap<String, String>();
			String meetingTitle=request.getHeader("meetingTitle");
			System.out.println("meetingTitle="+ meetingTitle);
			String url = "https://api.zoom.us/v1/meeting/create";
			inputs = null;
			inputs = new HashMap<String, String>();
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("host_id", id);
			inputs.put("topic", meetingTitle);
			inputs.put("type", "1");
			inputs.put("option_host_video", "true");
			inputs.put("option_participants_video", "true");
			inputs.put("option_audio", "both");
			String result = makeRestCall(url, inputs);
			JSONObject jsonObj = null;
			jsonObj = parseJson(result);
			return jsonObj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject getUserDetails(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			System.out.println("in getUserDetails");
			String url = "https://api.zoom.us/v1/user/getbyemail";
			Map<String, String> inputs = new HashMap<String, String>();
			String email = request.getHeader("email");
			System.out.println("email=" + email);
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("email", email);
			inputs.put("login_type", "99");
			
			String result = makeRestCall(url, inputs);
			
			JSONObject jsonObj = parseJson(result);
	
			
			return jsonObj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject getMeetingDetails(HttpServletRequest request, HttpServletResponse response, String hostId)
	{
		try
		{
			System.out.println("in getMeetingDetails");
			String url = "https://api.zoom.us/v1/meeting/get";
			Map<String, String> inputs = new HashMap<String, String>();
			String meetingId = request.getHeader("meetingId");
			
			System.out.println("meetingId=" + meetingId);
			inputs.put("api_key", "M-830EumRxa81r1IMI_FZw");
			inputs.put("api_secret", "uRhiFlbsdBHqtXGXcpIL3cffJtSRrP4ZEBG3");
			inputs.put("id", meetingId);
			inputs.put("host_id", hostId);
			
			
			String result = makeRestCall(url, inputs);
			
			JSONObject jsonObj = parseJson(result);
	
			
			return jsonObj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
