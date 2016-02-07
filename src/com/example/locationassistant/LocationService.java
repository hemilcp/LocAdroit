package com.example.locationassistant;

import java.util.*;

import com.example.DBClasses.Location_Alias;
import com.example.BackgroundWorks.*;
import com.example.DBHelper.*;

import android.app.Service;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener
{
	Thread locth;
	MyDBHelper dbhelper;
	protected LocationManager locationManager;
	private static final int RADIUS=6371;
	private static final int MAX_TIMEOUT=5*60*1000;
	int TIME_OUT;
	Vector<Location_Alias> locations,locreached;
	private static LocationService self=null;
	String locProvider=null;
	Location currentLocation=null;
	final int decisionMat[][]={{1000,5*1000,100},{5000,20*1000,500},{10000,2*60*1000,1000},{20000,5*60*1000,2000},{50000,10*60*1000,10000}};
	final int decisionMatDrive[][]={{1000,2*1000,50},{5000,10*1000,250},{10000,30*1000,500},{20000,2*60*1000,1000},{50000,5*60*1000,10000}};
	@Override
	public void onCreate()
	{
		Log.d("Location Service","Service created");
		dbhelper=new MyDBHelper(this);
		if(getData())
		{	startLocationListener();	}
		locreached=new Vector<Location_Alias>();
		self=this;
		TIME_OUT=0;
	}
	
	public static LocationService getInstance()
	{	return self;	}
	
	@Override
	public IBinder onBind(Intent intent)
	{	return null;	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		Log.d("Location Service","Service started");
		if(getData())
		{	startLocationListener();
			TIME_OUT=0;
		}
		return START_STICKY;
	}
	
	public boolean getData()
	{		
		if(locations!=null)
		{	locations.removeAllElements();	}
		locations=dbhelper.getLocations();
		if(locations==null)
		{	if(locationManager!=null)
			{	locationManager.removeUpdates(this);	}
			return false;	
		}
		else
		{	
			/*for(int i=0;i<locations.size();i++)
			{
				Log.d("Location Service",locations.get(i).getAlias());
				Log.d("Location Service",""+locations.get(i).getLat());
				Log.d("Location Service",""+locations.get(i).getLng());
			}*/
			return true;		
		}
	}
	
	public void restartAgain()
	{
		Log.d("LocationSerivce","Restart Again");
		if(getData())
		{	TIME_OUT=0;
			if(currentLocation!=null && locreached!=null && locProvider!=null)
			{	Log.d("LocationSerivce","Checking current location");
				
				boolean flag=false;
				Location_Alias loc1;
				for(int i=0;i<locreached.size();i++)
				{	loc1=locreached.get(i);
					flag=false;
					for(int j=0;j<locations.size();j++)
					{
						if(locations.get(j).getAlias().equals(loc1.getAlias()))
						{	flag=true;	break;}
					}
					if(!flag)
					{	locreached.remove(i);	}
				}
				double d;
				int THRESHOLD=(currentLocation.getAccuracy()>200)?500:200;
				for(int i=0;i<locreached.size();i++)
				{
					loc1=locations.get(i);
					d=findDistance(loc1.getLat(),loc1.getLng(),currentLocation.getLatitude(),currentLocation.getLongitude());
					if(d<THRESHOLD)
					{	goBroadcast(loc1);	}
				}
			}
			startLocationListener();
		}
	}
	
	public void startLocationListener()
	{
		Log.d("Location Service","Location Listner working");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 10, this);
			locProvider=LocationManager.NETWORK_PROVIDER;
		}
		else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 10, this);
			locProvider=LocationManager.GPS_PROVIDER;
		}
		else
		{	locProvider=null;	}
		Log.d("Location Service","Location_Provider : "+locProvider);
	}
	
	public void goBroadcast(Location_Alias loc)
	{
		Intent serviceintent=new Intent();
		serviceintent.setAction(Constants.LOC_REACHED);
		serviceintent.putExtra("NextLocation",loc.getAlias());
		sendBroadcast(serviceintent);
		Log.d("Location Service","Location Reached Broadcast");
	}
	
	public void locationLeftBroadcast(Location_Alias loc)
	{
		Intent serviceintent=new Intent();
		serviceintent.setAction(Constants.LOC_LEFT);
		serviceintent.putExtra("Location",loc.getAlias());
		sendBroadcast(serviceintent);
		Log.d("Location Service","Location Left Broadcast");
	}
	
	@Override
	public void onDestroy() 
	{	super.onDestroy();
		locationManager.removeUpdates(this);
		Log.d("Location Service","Service destroyed");	
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		Log.d("LocationService","latitude : "+location.getLatitude());
		Log.d("LocationService","Longitude : "+location.getLongitude());
		/*Log.d("LocationService","Accuracy : "+location.getAccuracy());
		Log.d("LocationService","Speed : "+location.getSpeed());
		Log.d("LocationService","Time_out : "+TIME_OUT);*/
		
		currentLocation=location;
		double mindist=checkForMin(location);
		mindist*=1000;
		double speed=location.getSpeed();
		if(mindist<1000)												//100 meters
		{	
			if(speed==0)
			{	
				if(TIME_OUT==0)
				{	TIME_OUT=decisionMat[0][1];	}
				else if(TIME_OUT<MAX_TIMEOUT)
				{	TIME_OUT*=2;	}
				else
				{	TIME_OUT=MAX_TIMEOUT;	}
			}
			else
			{	TIME_OUT=decisionMatDrive[0][1];	}
			if(!locProvider.equals(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{	locProvider=LocationManager.GPS_PROVIDER;	}
			if(locProvider!=null)
			{	locationManager.requestLocationUpdates(locProvider,TIME_OUT, decisionMatDrive[0][2],this);	}
		}
		else if(mindist>20000)
		{
			if(speed==0)
			{	
				if(TIME_OUT==0)
				{	TIME_OUT=decisionMat[4][1];	}
				else if(TIME_OUT<MAX_TIMEOUT)
				{	TIME_OUT*=2;	}
				else
				{	TIME_OUT=MAX_TIMEOUT;	}
			}
			else
			{	TIME_OUT=decisionMatDrive[4][1];	}
			if(locProvider!=null)
			{	locationManager.requestLocationUpdates(locProvider,TIME_OUT, decisionMatDrive[4][2],this);	}
		}
		else	
		{
			for(int i=1;i<decisionMat.length-1;i++)
			{
				if(mindist<decisionMat[i][0] && mindist>decisionMat[i-1][0])
				{	if(speed==0)
					{	
						if(TIME_OUT==0)
						{	TIME_OUT=decisionMat[i][1];	}
						else if(TIME_OUT<MAX_TIMEOUT)
						{	TIME_OUT*=2;	}
						else
						{	TIME_OUT=MAX_TIMEOUT;	}
					}
					else
					{	TIME_OUT=decisionMatDrive[i][1];	}
					if(locProvider!=null)
					{	locationManager.requestLocationUpdates(locProvider,TIME_OUT, decisionMatDrive[i][2],this);	}
				}
			}
		}
	}
	
	double checkForMin(Location location)
	{
		Location_Alias loc1=locations.get(0);
		double d;
		double mind=0;
		int THRESHOLD=(location.getAccuracy()>200)?500:200;
		for(int i=0;i<locations.size();i++)
		{	loc1=locations.get(i);
			d=findDistance(loc1.getLat(),loc1.getLng(),location.getLatitude(),location.getLongitude());
			if(i==0)
			{ 	mind=d;	}
			else
			{	if(d<mind)
				{	mind=d;	}
			}
			d*=1000;
			if(d<THRESHOLD)
			{	if(locreached!=null)
				{	boolean flag=false;
					for(int j=0;j<locreached.size();j++)
					{	
						if(locreached.get(j).getAlias().equals(loc1.getAlias()))
						{	flag=true;	break;}
					}
					if(!flag)
					{	locreached.add(loc1);
						goBroadcast(loc1);
					}
				}
				else if(locreached==null)
				{	locreached.add(loc1);	goBroadcast(loc1);		}
			}
			else if(d>THRESHOLD)
			{	if(locreached!=null)
				{	int flag=-1;
					for(int j=0;j<locreached.size();j++)
					{	
						if(locreached.get(j).getAlias().equals(loc1.getAlias()))
						{	flag=j;	break;}
					}
					if(flag!=-1)
					{	locationLeftBroadcast(loc1);
						locreached.remove(flag);	
					}
				}
			}
		}
		return mind;
	}
	
	private double findDistance(double lat1,double lng1,double lat2,double lng2)
	{
		double distance;
		double dlat=Math.toRadians(lat2-lat1);
		double dlng=Math.toRadians(lng2-lng1);
		lat1=Math.toRadians(lat1);
		lat2=Math.toRadians(lat2);
		double a=Math.sin(dlat/2)*Math.sin(dlat/2)+Math.cos(lat1)*Math.cos(lat2)*Math.sin(dlng/2)*Math.sin(dlng/2);
		double c=2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		distance=c*RADIUS;
		return distance;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{	
		if(locProvider==null && provider.equals(LocationManager.GPS_PROVIDER) && status==LocationProvider.AVAILABLE)
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 5,this);
			locProvider=LocationManager.GPS_PROVIDER;
		}
		Log.d("LocationService","Provider : "+locProvider);
	}
	@Override
	public void onProviderEnabled(String provider) 
	{	if(provider.equals(LocationManager.NETWORK_PROVIDER) && locProvider==null)
		{	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 5,this);
			locProvider=LocationManager.NETWORK_PROVIDER;
		}
		else if(provider.equals(LocationManager.GPS_PROVIDER) && locProvider==null)
		{	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 5,this);
			locProvider=LocationManager.GPS_PROVIDER;
		}
		Log.d("LocationService","Provider : "+locProvider);
	}

	@Override
	public void onProviderDisabled(String provider) 
	{	if(provider.equals(LocationManager.NETWORK_PROVIDER) && locProvider.equals(LocationManager.NETWORK_PROVIDER))
		{	if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 5,this);
				locProvider=LocationManager.GPS_PROVIDER;
			} 
			else
			{	locProvider=null;}		
		}
		else if(provider.equals(LocationManager.GPS_PROVIDER) && locProvider.equals(LocationManager.GPS_PROVIDER))
		{	
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			{	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 5,this);
				locProvider=LocationManager.NETWORK_PROVIDER;
			} 
			else
			{	locProvider=null;}
		}
		Log.d("LocationService","Provider : "+locProvider);
	}
}
