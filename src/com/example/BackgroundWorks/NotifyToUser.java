package com.example.BackgroundWorks;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.example.DBHelper.MyDBHelper;

import android.content.Context;
import android.util.Log;

public class NotifyToUser implements Runnable 
{
	Thread t;
	Context context;
	int eventid;
	public NotifyToUser(Context con,int eid)
	{
		context=con;
		eventid=eid;
		t=new Thread(this);
		t.run();
	}
	@Override
	public void run()
	{
		if(Constants.isConnected(context))
		{
			try
			{	Log.d("Connection Status","Trying to connect to server");
				URL url=new URL(Constants.notifyUser);
				URLConnection con=url.openConnection();
				con.setDoOutput(true);
				HttpURLConnection hpcon=(HttpURLConnection)con;
				hpcon.connect();
				
				OutputStream os=hpcon.getOutputStream();
				PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
				JSONObject jobj=new JSONObject();
				jobj.put("eventid", eventid);
				out.write(jobj.toString());
				out.close();
				Log.d("Server","Output Done");
				
				Log.d("Server","Going for Input");
				Log.d("Connection Status",""+hpcon.getResponseCode());
				if(hpcon.getResponseCode()==200)
				{
					MyDBHelper dbhelper=new MyDBHelper(context);
					dbhelper.updateStatus(eventid, Constants.EVENT_DONE);
				}
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
		else
		{
			MyDBHelper dbhelper=new MyDBHelper(context);
			dbhelper.updateStatus(eventid, Constants.EVENT_PENDING);
		}
	}
}
