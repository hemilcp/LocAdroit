package com.example.BackgroundWorks;

import java.util.*;

import com.example.DBClasses.*;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.*;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CheckForReminders implements Runnable  
{	public Thread t;
	Context context;
	MyDBHelper dbhelper;
	Calendar calendar;
	String date;
	String loc_alias;
	long pattern[]={0,1000};
	public CheckForReminders(Context con,String loc)
	{
		context=con;
		loc_alias=loc;
		dbhelper=new MyDBHelper(context);
		Log.d("CheckforReminders","In thread");
		t=new Thread(this);
		t.start();		
	}
	
	public void run()
	{
		calendar=Calendar.getInstance();
		int dd,mm,yy;
		dd=calendar.get(Calendar.DAY_OF_MONTH);
		mm=calendar.get(Calendar.MONTH)+1;
		yy=calendar.get(Calendar.YEAR);
		String mon=(mm<10)?("0"+mm):(""+mm);
		String day=(dd<10)?("0"+dd):(""+dd);
		date=day+"/"+mon+"/"+yy;
		Log.d("CheckforReminders",date);
		ArrayList<LocationReminder> rems=dbhelper.getMatchedReminders(loc_alias, date);
		ArrayList<LogRecord> logs=dbhelper.getTodaysLog(date,Constants.EVENT_REMINDER);
		LocationReminder reminder;
		LogRecord log;
		boolean flag;
		if(logs!=null && rems!=null)
		{	for(int i=0;i<rems.size();i++)
			{	flag=false;
				reminder=rems.get(i);
				log=null;
				for(int j=0;j<logs.size();j++)
				{
					if(logs.get(j).getEventId()==reminder.getId())
					{	flag=true;	log=logs.get(i);	break;	}
				}
				if(flag)
				{	Log.d("LogRecord","Record already exists \n Type : "+log.getEventType()+"\n Status : "+log.getStatus());
					if(log.getStatus().equals(Constants.ACTION_NOT_DONE))
					{	goReminderNotification(reminder,log.getId());	}
				}
				else
				{
					log=new LogRecord(Constants.EVENT_REMINDER,reminder.getId(),Constants.ACTION_NOT_DONE,date);
					dbhelper.addLogRecord(log);
					int lid=dbhelper.getLastLog();
					goReminderNotification(reminder,lid);
				}
			}
		}
		else if(logs==null && rems!=null)
		{
			
			for(int i=0;i<rems.size();i++)
			{	reminder=rems.get(i);
				log=new LogRecord(Constants.EVENT_REMINDER,reminder.getId(),Constants.ACTION_NOT_DONE,date);
				dbhelper.addLogRecord(log);
				int lid=dbhelper.getLastLog();
				goReminderNotification(reminder,lid);
			}
		}
		
	}
	void goReminderNotification(LocationReminder reminder,int lid)
	{
		int noteId;
		noteId=(int)System.currentTimeMillis();
		Intent remIntent=new Intent(context,RemiDetailsActivity.class);
		remIntent.putExtra("Caller", "Notification");
		remIntent.putExtra("ID",reminder.getId());
       	remIntent.putExtra("Title",reminder.getTitle());
       	remIntent.putExtra("Note",reminder.getNote());
       	remIntent.putExtra("Location",reminder.getLoc());
       	remIntent.putExtra("Date",reminder.getDate());
       	remIntent.putExtra("LOG_ID",lid);
       	
       	PendingIntent pi=PendingIntent.getActivity(context, noteId, remIntent, 0);
       	NotificationCompat.Builder note=new NotificationCompat.Builder(context);
		note.setContentTitle("Reminder : "+reminder.getTitle());
		note.setContentText(reminder.getNote());
		note.setSmallIcon(R.drawable.ic_launcher);
		note.setVibrate(pattern);
		note.setContentIntent(pi);
		note.setWhen(System.currentTimeMillis());
		note.setAutoCancel(true);
		NotificationManager nfm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.notify(noteId, note.build());
		Log.d("Reminder",""+reminder.getId()+" "+reminder.getTitle());
	}
}

