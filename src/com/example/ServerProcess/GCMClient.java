import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.*;
import org.json.simple.parser.*;

public class GCMClient
{	String gcmServerIp="https://android.googleapis.com/gcm/send";
	String authkey="key=AIzaSyDYr2VvZRgRda3MlmbdMeV_KcI4T1ZAc2Y";
	String askp="ask-permission";
	String note="notify-user";
	String yesno="yes-no";
	int ttl=60*60;
	DBEvents dbhelper;
	GCMClient()
	{	dbhelper=new DBEvents();	}
	public void sendEventData(int eid)
	{	MultiUserEvent me=dbhelper.getEventDetails(eid);
		String u1email=dbhelper.getEmail(me.user1);
		String u2email=dbhelper.getEmail(me.user2);
		Location loc=dbhelper.getLocation(me.locid);
		String addr=loc.address;
		double lat=loc.latitude;
		double lng=loc.longitude;
		
		JSONObject dataobj=new JSONObject();
		JSONObject serverobj=new JSONObject();
		JSONArray regarr=new JSONArray();
		try
		{	
			dataobj.put("m-type",new String(askp));
			dataobj.put("eventid",eid);
			dataobj.put("email",u1email);
			dataobj.put("address",addr);
			dataobj.put("longitude",lng);
			dataobj.put("latitude",lat);
			dataobj.put("date",me.date);
			
			Vector<String> regids=dbhelper.getRegistrationIds(u2email);
			for(int i=0;i<regids.size();i++)
			{	regarr.add(regids.elementAt(i));	}
			serverobj.put("registration_ids",regarr);
			serverobj.put("notification_key_name",u2email);
			serverobj.put("data",dataobj);
			serverobj.put("time_to_live",ttl);
			sendToGCM(serverobj);
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
	
	public void notifyOtherUser(int eventid)
	{
		JSONObject dataobj=new JSONObject();
		JSONObject serverobj=new JSONObject();
		JSONArray regarr=new JSONArray();
		MultiUserEvent me;
		try
		{	me=dbhelper.getEventDetails(eventid);
			String u1email=dbhelper.getEmail(me.user1);
			
			dataobj.put("m-type",note);
			dataobj.put("eventid",eventid);
			Vector<String> regids=dbhelper.getRegistrationIds(u1email);
			for(int i=0;i<regids.size();i++)
			{	regarr.add(regids.elementAt(i));	}
			serverobj.put("registration_ids",regarr);
			serverobj.put("notification_key_name",u1email);
			serverobj.put("data",dataobj);
			serverobj.put("time_to_live",ttl);
			//serverobj.put("dry_run",true);
			sendToGCM(serverobj);
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
	public void sendResponseData(int eventid,int answer)
	{	JSONObject dataobj=new JSONObject();
		JSONObject serverobj=new JSONObject();
		JSONArray regarr=new JSONArray();
		MultiUserEvent me;
		try
		{	me=dbhelper.getEventDetails(eventid);
			String u1email=dbhelper.getEmail(me.user1);
			
			dataobj.put("m-type",yesno);
			dataobj.put("eventid",eventid);
			dataobj.put("answer",answer);
			
			Vector<String> regids=dbhelper.getRegistrationIds(u1email);
			for(int i=0;i<regids.size();i++)
			{	regarr.add(regids.elementAt(i));	}
			serverobj.put("registration_ids",regarr);
			serverobj.put("notification_key_name",u1email);
			serverobj.put("data",dataobj);
			serverobj.put("time_to_live",ttl);
			//serverobj.put("dry_run",true);
			sendToGCM(serverobj);
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
	
	public void sendToGCM(JSONObject serverobj)
	{	try
		{	String content=serverobj.toString();
			System.out.println("Content : \n"+content);
			
			System.out.println("Connecting to server");
			URL url=new URL(gcmServerIp);
			URLConnection con=url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			HttpsURLConnection hpcon=(HttpsURLConnection)con;
			hpcon.setRequestMethod("POST");
			hpcon.setRequestProperty("Authorization",authkey);
			hpcon.setRequestProperty("Content-Type","application/json");
			hpcon.connect();
			
			OutputStream os=hpcon.getOutputStream();
			PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
			out.write(content);
			out.close();
			
			System.out.println("\n\nResponse Code : "+hpcon.getResponseCode());
			if(hpcon.getResponseCode()==200)
			{	InputStream is=hpcon.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String line,msg="";
				while((line=br.readLine())!=null)
				{	msg+=line;	}
				br.close();
				System.out.println("\n\nServer response message : "+msg);
				JSONParser parser=new JSONParser();
				JSONObject jobj=(JSONObject)parser.parse(msg);
				int fails=(int)((long)jobj.get("failure"));
				if(fails==0)
				{	System.out.println("\nNo server failures");	}
				else
				{	System.out.println("\nfailures : "+fails);	}
			}
			else
			{	InputStream is=hpcon.getErrorStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String line,msg="";
				while((line=br.readLine())!=null)
				{	msg+=line;	}
				br.close();
				System.out.println("Server response error message : "+msg);
			}
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
	public static void main(String[] args)
	{	GCMClient gc=new GCMClient();
		System.out.println("Send Event data");
		gc.sendEventData(142);
	}
}