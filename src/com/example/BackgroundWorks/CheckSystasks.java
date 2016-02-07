package com.example.BackgroundWorks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.DBClasses.LogRecord;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.R;
import com.example.locationassistant.SysdetailsActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class CheckSystasks implements Runnable
{
	Context context;
	String location,lcondition;
	MyDBHelper dbhelper;
	String date;
	public Thread t;
	long pattern[]={0,1000};
	boolean locflag;
	SystemTasks last;
	public CheckSystasks(Context con,String loc,String cond,boolean newloc,SystemTasks stask)
	{
		context=con;
		location=loc;
		lcondition=cond;
		dbhelper=new MyDBHelper(context);
		locflag=newloc;
		last=stask;
		Thread t=new Thread(this);
		t.start();
		Log.d("CheckforSystem","In thread");
	}
	@Override
	public void run()
	{
		Calendar calendar=Calendar.getInstance();
		int dd,mm,yy;
		dd=calendar.get(Calendar.DAY_OF_MONTH);
		mm=calendar.get(Calendar.MONTH)+1;
		yy=calendar.get(Calendar.YEAR);
		String mon=(mm<10)?("0"+mm):(""+mm);
		String day=(dd<10)?("0"+dd):(""+dd);
		date=day+"/"+mon+"/"+yy;
		Log.d("CheckforSystask",date);
		if(!locflag)
		{	ArrayList<SystemTasks> syslist=dbhelper.getMatchedTasks(location,lcondition);
			if(syslist!=null)
			{	SystemTasks stask;
				Log.d("GetMatchedTasks", ""+syslist.size());
				for(int i=0;i<syslist.size();i++)
				{	stask=syslist.get(i);
					Log.d("Systask",""+stask.getType());
					switch(stask.getType())
					{	case Constants.BATTERY : checkBatteryStatus(stask);	break;
						case Constants.WIFI : changeWifiStatus(stask);	break;
						case Constants.SOUND : changeRingtone(stask);	break;
						case Constants.WALLPAPER : changeWallpaper(stask);	break;	
					}
					LogRecord ld=new LogRecord(Constants.EVENT_SYSTASK,stask.getId(),Constants.ACTION_DONE,date);
					dbhelper.addLogRecord(ld);
				}
			}
			else
			{	Log.d("GetMatchedTasks", "0");	}
		}
		else
		{
			checkForLastTask(last);
		}
	}
	
	public void checkForLastTask(SystemTasks stask)
	{	switch(stask.getType())
		{	case Constants.BATTERY : checkBatteryStatus(stask);	break;
			case Constants.WIFI : changeWifiStatus(stask);	break;
			case Constants.SOUND : changeRingtone(stask);	break;
			case Constants.WALLPAPER : changeWallpaper(stask);	break;	
		}
		LogRecord ld=new LogRecord(Constants.EVENT_SYSTASK,stask.getId(),Constants.ACTION_DONE,date);
		dbhelper.addLogRecord(ld);
	}
	void checkBatteryStatus(SystemTasks stask)
	{
		IntentFilter ifilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent=context.registerReceiver(null, ifilter);
		int status=intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging=(status==BatteryManager.BATTERY_STATUS_CHARGING) || (status==BatteryManager.BATTERY_STATUS_FULL);
		if(!isCharging)
		{
			int level,scale;
			level =intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			scale =intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

			float batteryPct = level / (float)scale;
			int percent=(int)batteryPct*100;
			int spercent=Integer.parseInt(stask.getAttr());
			if(percent<spercent)
			{	goNotification(stask);	}
		}
	}
	void changeWifiStatus(SystemTasks stask)
	{
		boolean booleanValue ;
		int wifi_state=Integer.parseInt(stask.getAttr());
		if(wifi_state == 1)
		{booleanValue = true; }
		else booleanValue = false;
		WifiManager wManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wManager.setWifiEnabled(booleanValue);
		goNotification(stask);
		Log.d("Change Wifi Status",""+booleanValue);
	}
	void changeRingtone(SystemTasks stask)
	{
		String sound_type=stask.getAttr();
		AudioManager audiomanager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		try
		{
			int type=Integer.parseInt(sound_type);
			switch(type)
			{
				case 2 : audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);  Log.d("Ringtone", "silent done"); break;
				case 3 : audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  Log.d("RingTone", "Vibrate mode on"); break;
				default : audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); Log.d("RingTone", "Started"); break;
			}
			
		}
		catch(NumberFormatException ne)
		{	
			Uri newUri=Uri.parse(sound_type);
			audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); Log.d("RingTone", "Started"); 
	       	RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
	        Log.d("Ringtone",sound_type+"set");
		}
		goNotification(stask);
	}
	void changeWallpaper(SystemTasks stask)
	{
	    String path=stask.getAttr();
	    Uri selectedImage=Uri.parse(path);
	    Log.d("Wallpaper",path);
	    Log.d("Wallpaper",selectedImage.toString());
		InputStream imageStream = null;
		try 
		{
			imageStream = context.getContentResolver().openInputStream(selectedImage);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
			yourSelectedImage = getResizedBitmap(yourSelectedImage);
			Log.d("String", "Done Close");
	            WallpaperManager wm = WallpaperManager.getInstance(context);
	            DisplayMetrics dm = new DisplayMetrics();
	            WindowManager wmg=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	    		wmg.getDefaultDisplay().getMetrics(dm);
	    		int height = dm.heightPixels;
	    		int width = dm.widthPixels;

	            	try         { 	wm.setBitmap(yourSelectedImage);  
	            					wm.suggestDesiredDimensions(width, height);
	            					goNotification(stask);
	            				} 
	            	catch (IOException e) {    Log.e("TAG", "Cannot set image as wallpaper", e);          }
		} 
		catch (FileNotFoundException e) 
		{	e.printStackTrace();	}
		
	}
	
	public Bitmap getResizedBitmap(Bitmap bm)
	{
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wmg=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wmg.getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels << 1;
		Bitmap bmc = Bitmap.createScaledBitmap(bm,width,height,true);
		return bmc;
	}
	
	void goNotification(SystemTasks stask)
	{
		int noteId;
		noteId=(int)System.currentTimeMillis();
		Intent sIntent=new Intent(context,SysdetailsActivity.class);
		sIntent.putExtra("ID",stask.getId());
       	sIntent.putExtra("Type",stask.getType());
       	sIntent.putExtra("Attributes",stask.getAttr());
       	sIntent.putExtra("Location",stask.getLoc());
       	sIntent.putExtra("Condition",stask.getCond());
       	
       	PendingIntent pi=PendingIntent.getActivity(context, noteId, sIntent, 0);
       	NotificationCompat.Builder note=new NotificationCompat.Builder(context);
       	String title="";
       	switch(stask.getType())
       	{	case Constants.BATTERY : title=Constants.BATTERY_N; break;
       		case Constants.WIFI : title=Constants.WIFI_N; break;
       		case Constants.WALLPAPER : title=Constants.WALLPAPER_N; break;
       		case Constants.SOUND : title=Constants.SOUND_N; break;
       	}
		note.setContentTitle("SystemTask Performed");
		note.setContentText(title);
		note.setSmallIcon(R.drawable.ic_launcher);
		note.setVibrate(pattern);
		note.setContentIntent(pi);
		note.setWhen(System.currentTimeMillis());
		note.setAutoCancel(true);
		NotificationManager nfm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.notify(noteId, note.build());
		Log.d("Notificaition:SystemTask",""+stask.getId());
	}
	
}
