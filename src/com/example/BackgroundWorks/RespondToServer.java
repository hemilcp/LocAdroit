package com.example.BackgroundWorks;

import java.io.*;
import java.net.*;

import org.json.JSONObject;

import com.example.DBHelper.MyDBHelper;

import android.content.Context;
import android.util.Log;

public class RespondToServer implements Runnable
{	Context context;
	int eventid,answer;
	Thread t;
	public RespondToServer(Context con,int eid,int ans)
	{
		context=con;
		eventid=eid;
		answer=ans;
		t=new Thread(this);
		t.start();
	}
	
	public void run()
	{
		try
		{	Log.d("Connection Status","Trying to connect to server");
			URL url=new URL(Constants.respondToServer);
			URLConnection con=url.openConnection();
			con.setDoOutput(true);
			HttpURLConnection hpcon=(HttpURLConnection)con;
			hpcon.connect();
			
			OutputStream os=hpcon.getOutputStream();
			PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
			JSONObject jobj=new JSONObject();
			jobj.put("eventid", eventid);
			jobj.put("answer",answer);
			out.write(jobj.toString());
			out.close();
			Log.d("Server","Output Done");
			
			Log.d("Server","Going for Input");
			Log.d("Connection Status",""+hpcon.getResponseCode());
			MyDBHelper dbhelper=new MyDBHelper(context);
			if(hpcon.getResponseCode()==200)
			{	dbhelper.updateStatus(eventid, Constants.SENT_U2RESPONSE);			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}
