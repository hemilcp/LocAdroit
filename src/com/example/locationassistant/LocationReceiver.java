package com.example.locationassistant;

import java.util.*;

import com.example.BackgroundWorks.*;
import com.example.DBClasses.*;
import com.example.DBHelper.*;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver 
{
	Context context;
	String action;
	Intent intent;
	CheckForReminders cfr;
	CheckSystasks cst;
	CheckMulUserTasks cmu;
	String date;
	MyDBHelper dbhelper;
	@Override
	public void onReceive(Context con, Intent int1) 
	{
		context=con;
		intent=int1;
		Log.d("LocationReceiver","Broadcast received");
		Log.d("LocationReceiver",intent.getAction());
		action=intent.getAction();
		
		dbhelper=new MyDBHelper(context);
		if(action.equals("LOCATION_REACHED"))
		{	locationEntered();	}
		else if(action.equals("LOCATION_LEFT"))
		{	locationLeft();	}
		else if(action.equals("DATABASE_MODIFIED"))
		{	databaseChanged();		}
		else if(action.equals(Intent.ACTION_DATE_CHANGED))
		{	getPrevDate();
			dbhelper.deleteLastDayTasks(date);
			//restarting the service
			ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
			{
				if (LocationService.class.getName().equals(service.service.getClassName())) 
			    {	LocationService myServ=LocationService.getInstance();
			    	if(myServ!=null)
			    	{	context.stopService(new Intent(context,LocationService.class));   	}
			    }
			}
			Intent serviceintent=new Intent(context,LocationService.class);
			context.startService(serviceintent);
		}
		else if(action.equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION))
		{
			if(Constants.isConnected(context))
			{	makeDate();
				CheckMulUserTasks cmu=new CheckMulUserTasks(context,null,date,false);
			}
		}
	}
	
	void makeDate()
	{	Calendar calendar=Calendar.getInstance();
		int dd,mm,yy;
		dd=calendar.get(Calendar.DAY_OF_MONTH);
		mm=calendar.get(Calendar.MONTH)+1;
		yy=calendar.get(Calendar.YEAR);
		String mon=(mm<10)?("0"+mm):(""+mm);
		String day=(dd<10)?("0"+dd):(""+dd);
		date=day+"/"+mon+"/"+yy;
	}
	
	void getPrevDate()
	{
		Calendar calendar=Calendar.getInstance();
		int dd,mm,yy;
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		dd=calendar.get(Calendar.DAY_OF_MONTH);
		mm=calendar.get(Calendar.MONTH)+1;
		yy=calendar.get(Calendar.YEAR);
		
		String mon=(mm<10)?("0"+mm):(""+mm);
		String day=(dd<10)?("0"+dd):(""+dd);
		date=day+"/"+mon+"/"+yy;
	}
	
	public void locationEntered()
	{
		String nextLocation;
		makeDate();
		nextLocation=intent.getStringExtra("NextLocation");
		cfr=new CheckForReminders(context,nextLocation);
		cst=new CheckSystasks(context,nextLocation,Constants.LOC_ENTERED,false,null);
		cmu=new CheckMulUserTasks(context,nextLocation,date,false);
	}	
	
	public void locationLeft()
	{	String location=intent.getStringExtra("Location");
		makeDate();
		dbhelper.changeSnoozeStatus(date,location);
		String nextLocation=intent.getStringExtra("NextLocation");
		cst=new CheckSystasks(context,nextLocation,Constants.LOC_LEAVING,false,null);
	}
	public void databaseChanged()
	{
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
		{
			if (LocationService.class.getName().equals(service.service.getClassName())) 
		    {	LocationService myServ=LocationService.getInstance();
		    	if(myServ!=null)
		    	{	Log.d("Service","Already Running");
		    		String table=intent.getStringExtra("Table");
		    		boolean flag=false;
		    		if(table.equals(Constants.EVENT_SYSTASK))
		    		{
		    			flag=false;
		    			MyDBHelper dbhelper=new MyDBHelper(context);
		    			SystemTasks stask=dbhelper.getLastSTask();
		    			Vector<Location_Alias> locreached=myServ.locreached;
		    			for(int i=0;i<locreached.size();i++)
		    			{
		    				if(locreached.get(i).getAlias().equals(stask.getLoc()) && stask.getCond().equals(Constants.LOC_ENTERED))
		    				{	flag=true;	break;		}
		    			}
		    			if(flag)
		    			{	cst=new CheckSystasks(context,stask.getLoc(),Constants.LOC_ENTERED,true,stask);  }
		    			else
			    		{	myServ.restartAgain();	}
		    		}
		    		else if(table.equals(Constants.EVENT_REMINDER))
		    		{	flag=false;	
		    			LocationReminder rem=dbhelper.getLastReminder();
		    			Vector<Location_Alias> locreached=myServ.locreached;
		    			for(int i=0;i<locreached.size();i++)
		    			{
		    				if(locreached.get(i).getAlias().equals(rem.getLoc()))
		    				{	flag=true;	break;	}
		    			}
		    			if(flag)
		    			{	cfr=new CheckForReminders(context,rem.getLoc());	}
		    			else
			    		{	myServ.restartAgain();	}
		    		}
		    		else if(table.equals(Constants.EVENT_USER))
		    		{	
		    			MultiUserEvent mevent=dbhelper.getLastMEvent();
		    			Vector<Location_Alias> locreached=myServ.locreached;
		    			for(int i=0;i<locreached.size();i++)
		    			{
		    				if(locreached.get(i).getAlias().equals(mevent.getLoc()))
		    				{	flag=true;	break;	}
		    			}
		    			if(flag)
		    			{	cmu=new CheckMulUserTasks(context,mevent.getLoc(),date,false);	}
		    			else
			    		{	myServ.restartAgain();	}
		    		}
		    		else
		    		{	myServ.restartAgain();	}
		    	}
				return;
		    }
		}
		Intent serviceintent=new Intent(context,LocationService.class);
		context.startService(serviceintent);
	}
}