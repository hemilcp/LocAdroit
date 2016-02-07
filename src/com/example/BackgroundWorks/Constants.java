package com.example.BackgroundWorks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Constants 
{
	//SENDER_ID FOR USING GCM
	public static final String SENDER_ID="662882563418";
	
	//Broadcast actions
	public static final String LOC_REACHED="LOCATION_REACHED";
	public static final String LOC_LEFT="LOCATION_LEFT";
	public static final String DB_MODIFIED="DATABASE_MODIFIED";
	public static final String LOC_ADDED="LOC_ADDED";
	public static final String DELETION="DELETION";
	
	//Event type constants
	public static final String EVENT_REMINDER="REMINDER";
	public static final String EVENT_SYSTASK="SYSTEM_TASK";
	public static final String EVENT_USER="MULTI_USER";
	
	//User type
	public static final int SENDER=1;
	public static final int RECEIVER=2;
	
	//Event status
	public static final String ACTION_DONE="DONE";
	public static final String ACTION_NOT_DONE="NOT_DONE";
	public static final String ACTION_SNOOZED="SNOOZED";
	public static final String ACTION_DISMISSED="DISMISSED";
	
	//LOCATION CONDITION
	public static final String LOC_ENTERED="LOCATION_ENTERED";
	public static final String LOC_LEAVING="LOCATION_LEFT";
	
	//Systask Types
	public static final int SOUND=1;
	public static final String SOUND_N="Ringtone Changed";
	public static final int WIFI=2;
	public static final String WIFI_N="Wifi Status Changed";
	public static final int BATTERY=3;
	public static final String BATTERY_N="Charge the Battery";
	public static final int WALLPAPER=4;
	public static final String WALLPAPER_N="Wallpaper Changed";
	
	//Preferences file
	public static final String PREF_FILE="LocAdroid_Pref";
	
	//ServerIP
	public static final String SERVER_URL="http://192.168.1.3:8081";
	public static final String addUser=SERVER_URL+"/AddNewUser";
	public static final String addEvent=SERVER_URL+"/AddNewEvent";
	public static final String checkUsers=SERVER_URL+"/CheckUsers";
	public static final String respondToServer=SERVER_URL+"/YesNoServlet";
	public static final String notifyUser=SERVER_URL+"/NotifyUser";
	public static final String userEmails=SERVER_URL+"/GetEmails";
	
	//MultiUser Event status
	public static final int GOT_EVENTID=1;
	public static final int GOT_U2RESPONSE=2;
	public static final int SENT_U2RESPONSE=3;
	public static final int EVENT_DONE=4;
	public static final int EVENT_PENDING=5;
	
	public static boolean isConnected(Context context)
	{
		ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
		boolean con=activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		return con;
	}
}