package com.example.BackgroundWorks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RegisterApp implements Runnable
{	Thread t;
	Context context;
	String accname;
	String regId="";
	public RegisterApp(Context con,String acc)
	{
		context=con;
		accname=acc;
		t=new Thread(this);
		t.start();
	}
	public void run()
	{
		Log.d("RegisterApp","public void run");
		if(Constants.isConnected(context) && !isRegistered())
		{	if(registerOnGCM())
			{	registerOnMyServer();	}
		}
	}
	boolean registerOnGCM()
	{	//register the app
		GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(context);
		try
		{	regId=gcm.register(Constants.SENDER_ID);
			if(regId==null || regId.equals(""))
			{	regId="";
				return false;
			}
			else
			{	SharedPreferences.Editor editor=context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit();
				editor.putString("registrationId", regId);
				editor.commit();
				Log.d("RegisterApp","RegistrationID : "+regId);
				return true;
			}
		}
		catch(Exception e)
		{	e.printStackTrace();	return false;	}		
	}
	void registerOnMyServer()
	{	try
		{	Log.d("Connection Status","Trying to connect to server");
			URL url=new URL(Constants.addUser);
			URLConnection con=url.openConnection();
			con.setDoOutput(true);
			HttpURLConnection hpcon=(HttpURLConnection)con;
			hpcon.connect();
			
			OutputStream os=hpcon.getOutputStream();
			PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
			JSONObject jobj=new JSONObject();
			jobj.put("email", accname);
			jobj.put("registrationId",regId);
			out.write(jobj.toString());
			out.close();
			Log.d("Server","Output Done");
			
			Log.d("Server","Going for Input");
			Log.d("Connection Status",""+hpcon.getResponseCode());
			if(hpcon.getResponseCode()==200)
			{	InputStream is=hpcon.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String line,msg="";
				while((line=br.readLine())!=null)
				{	msg+=line;	}
				br.close();
				Log.d("Server-Message",msg);
				jobj=new JSONObject(msg);
				int appid=jobj.getInt("appid");
				Log.d("APP-ID",""+appid);
				registerAppDone(appid);
			}
		}
		catch(Exception e){ e.printStackTrace(); }
    }
	void registerAppDone(int appid)
	{	SharedPreferences pref=context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
		String email=pref.getString("Account", null);
		if(email==null)
		{
			SharedPreferences.Editor editor=pref.edit();
			editor.putString("Account", accname);
			editor.commit();
			Log.d("RegisterApp","AccName : "+accname);
		}
		int aid=pref.getInt("AppID", -1);
		if(aid==-1)
		{	SharedPreferences.Editor editor=pref.edit();
			editor.putInt("AppID", appid);
			editor.commit();
			Log.d("RegisterApp","AppID : "+appid);
		}
	}
	boolean isRegistered()
	{
		SharedPreferences pref=context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
		String reg=pref.getString("registrationId", null);
		if(reg==null || reg.equals(""))
		{	return false;	}
		else
		{	return true;	}
	}
}