package com.example.BackgroundWorks;

import java.util.ArrayList;

import com.example.DBClasses.*;
import com.example.DBHelper.MyDBHelper;

import android.content.Context;

public class CheckMulUserTasks implements Runnable 
{
	Thread t;
	Context context;
	String location,date;
	MyDBHelper dbhelper;
	boolean pending;
	public CheckMulUserTasks(Context con,String loc,String dt,boolean pending)
	{
		context=con;
		location=loc;
		date=dt;
		dbhelper=new MyDBHelper(context);
		this.pending=pending;
		t=new Thread(this);
		t.start();
	}
	public void run()
	{
		ArrayList<MultiUserEvent> mevents;
		if(!pending)
		{	mevents=dbhelper.getMatchedMTasks(location, date);	}
		else
		{	mevents=dbhelper.getPendingMTasks(date);	}
		if(mevents!=null)
		{
			for(int i=0;i<mevents.size();i++)
			{
				NotifyToUser note=new NotifyToUser(context,mevents.get(i).getId());
				LogRecord ld=new LogRecord(Constants.EVENT_USER,mevents.get(i).getId(),Constants.ACTION_DONE,date);
				dbhelper.addLogRecord(ld);
			}
		}
	}
}
