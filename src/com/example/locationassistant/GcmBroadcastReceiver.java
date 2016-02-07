package com.example.locationassistant;

import com.example.DBClasses.MultiUserEvent;
import com.example.DBHelper.MyDBHelper;
import com.example.MultiUserPack.MulUserDetailsActivity;
import com.example.MultiUserPack.MultiUserNotifyActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmBroadcastReceiver extends BroadcastReceiver 
{
	Context context;
	Intent intent;
	long pattern[]={0,1000};
	String mtype;
	MyDBHelper dbhelper;
	Bundle data;
	@Override
	public void onReceive(Context con,Intent i1)
	{
		context=con;
		intent=i1;
		dbhelper=new MyDBHelper(context);
		GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(context);
		Log.d("GcmBroadcastReceiver","Message_Type : "+gcm.getMessageType(intent));		
		Log.d("GcmBroadcastReceiver","Received Message : "+intent.getExtras().toString());
		data=intent.getExtras();
		
		mtype=data.getString("m-type");
		Log.d("GcmBroadcastReceiver","Message_Type : "+mtype);
		if(mtype.equals("ask-permission"))
		{	Log.d("EventId",""+Integer.parseInt(data.getString("eventid")));
			Log.d("Address",data.getString("address"));
			Log.d("Latitude",""+Double.parseDouble(data.getString("latitude")));
			Log.d("Longitude",""+Double.parseDouble(data.getString("longitude")));
			sendPermissionNote();	
		}
		else if(mtype.equals("yes-no"))
		{	
			int ans=Integer.parseInt(data.getString("answer"));
			int eid=Integer.parseInt(data.getString("eventid"));
			String msg;
			MultiUserEvent me=dbhelper.getMEventData(eid);
			Log.d("EventId",""+eid);
			if(ans==2)
			{	dbhelper.deleteMTask(eid);
				msg="Other user resplied 'No',Event Discarded";
			}
			else
			{	msg="Event accepted by other user";	}
			sendNotifyNote(msg,me);
		}
		else if(mtype.equals("notify-user"))
		{	int eid=Integer.parseInt(data.getString("eventid"));
			MultiUserEvent me=dbhelper.getMEventData(eid);
			sendNotifyNote("Other user has reached the location",me);	
		}
	}
	
	public void sendNotifyNote(String msg,MultiUserEvent me)
	{	
		Intent nIntent=new Intent(context,MulUserDetailsActivity.class);
		nIntent.putExtra("Email", me.getEmail());
		nIntent.putExtra("Title", me.getTitle());
		nIntent.putExtra("Note", me.getNote());
		nIntent.putExtra("Location", me.getLoc());
		nIntent.putExtra("Date", me.getDate());
		
		int noteId=(int)System.currentTimeMillis();
		PendingIntent pi=PendingIntent.getActivity(context, noteId, nIntent, 0);
	   	NotificationCompat.Builder note=new NotificationCompat.Builder(context);
		note.setContentTitle("Multi-User Event");
		note.setContentText(msg);
		note.setSmallIcon(R.drawable.ic_launcher);
		note.setVibrate(pattern);
		note.setContentIntent(pi);
		note.setWhen(System.currentTimeMillis());
		note.setAutoCancel(true);
		NotificationManager nfm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.notify(noteId, note.build());
	}
	public void sendPermissionNote()
	{
		Intent nIntent=new Intent(context,MultiUserNotifyActivity.class);
		nIntent.putExtra("eventid", Integer.parseInt(data.getString("eventid")));
		nIntent.putExtra("email", data.getString("email"));
		nIntent.putExtra("address", data.getString("address"));
		nIntent.putExtra("date", data.getString("date"));
		nIntent.putExtra("longitude",Double.parseDouble(data.getString("longitude")));
		nIntent.putExtra("latitude",Double.parseDouble(data.getString("latitude")));
		
		int noteId=(int)System.currentTimeMillis();
		PendingIntent pi=PendingIntent.getActivity(context, noteId, nIntent, 0);
	   	NotificationCompat.Builder note=new NotificationCompat.Builder(context);
		note.setContentTitle("Notification");
		note.setContentText("New Multiuser Task");
		note.setSmallIcon(R.drawable.ic_launcher);
		note.setVibrate(pattern);
		note.setContentIntent(pi);
		note.setWhen(System.currentTimeMillis());
		note.setAutoCancel(true);
		NotificationManager nfm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.notify(noteId, note.build());
	}
}
