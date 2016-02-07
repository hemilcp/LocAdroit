package com.example.MultiUserPack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.example.BackgroundWorks.Constants;
import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.MultiUserEvent;
import com.example.DBHelper.MyDBHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.util.Log;

public class SendEventToServer implements Runnable 
{	
	Context context;
	MultiUserEvent me;
	public Thread t;
	MyDBHelper dbhelper;
	String resmsg;
	public SendEventToServer(Context con,MultiUserEvent me)
	{
		context=con;
		this.me=me;
		dbhelper=new MyDBHelper(context);
		
		t=new Thread(this);
		t.start();
	}
	public void run()
	{
		if(Constants.isConnected(context))
		{	if(addToServer())
			{	resmsg="EVENT_ADDED";	}
			else
			{	resmsg="EVENT_NOT_ADDED";	}
		}
		else
		{	resmsg="PENDING_EVENT";	}
	}
	
	public String getResponseMsg()
	{	return resmsg;	}
	
	public boolean addToServer()
	{	boolean result;
		try
		{	
			SharedPreferences pref=context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
			int appid=pref.getInt("AppID", -1);
			
			Location_Alias loc=dbhelper.getLocation(me.getLoc());
			String locaddr=getLocAddress(loc);
			JSONObject jobj=new JSONObject();
			
			jobj.put("user1",appid);
			jobj.put("address", locaddr);
			jobj.put("latitude", loc.getLat());
			jobj.put("longitude", loc.getLng());
			jobj.put("email", me.getEmail());
			jobj.put("date", me.getDate());
			
			Log.d("Connection Status","Trying to connect to server");
			URL url=new URL(Constants.addEvent);
			URLConnection con=url.openConnection();
			con.setDoOutput(true);
			HttpURLConnection hpcon=(HttpURLConnection)con;
			hpcon.connect();
			
			OutputStream os=hpcon.getOutputStream();
			PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
			out.write(jobj.toString());
			out.close();
			Log.d("Server","Output "+jobj.toString());
			
			Log.d("Server","Going for Input");
			Log.d("Connection Status",""+hpcon.getResponseCode());
			if(hpcon.getResponseCode()==200)
			{	result=true;
				InputStream in=hpcon.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(in));
				String msg="",line;
				while((line=br.readLine())!=null)
				{	msg+=line;	}
				br.close();
				jobj=new JSONObject(msg);
				me.setId(jobj.getInt("eventid"));
				dbhelper.addMultiUserEvent(me);
			}
			else
			{	result=false;	}
		}
		catch(Exception e){ e.printStackTrace(); result=false;}
		return result;
	}
	
	String getLocAddress(Location_Alias loc)
	{
		String address="";
		Geocoder geo=new Geocoder(context,Locale.getDefault());
		try
		{	List<android.location.Address> addr=geo.getFromLocation(loc.getLat(), loc.getLng(), 1);
			if(addr.size()>0)
			{
				android.location.Address addr1=addr.get(0);
				if(addr1.getMaxAddressLineIndex()>=2)
				{	address=addr1.getAddressLine(0)+","+addr1.getAddressLine(1)+","+addr1.getCountryName();	}
				else
				{	address=addr1.getAddressLine(0)+","+addr1.getCountryName();	}
			}
		}
		catch(Exception e)
		{	e.printStackTrace();}
		return address;
	}	
}
