package com.example.locationassistant;

import java.util.ArrayList;

import com.example.BackgroundWorks.*;
import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener 
{
	private final int ACC_REQ=100;
	String accname;
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Reminder","SysTasks", "Multi-User","Locations", "Add-Ons" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    initTabs();
	    checkFirstTimeTasks();
	    checkServiceStatus();
	}
	
	void initTabs()
	{
		// Initialization
	    viewPager = (ViewPager) findViewById(R.id.pager);
	    actionBar =  getSupportActionBar();
	    mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
	
	    viewPager.setAdapter(mAdapter);
	//    actionBar.setHomeButtonEnabled(false);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);       
	
	    // Adding Tabs
	    for (String tab_name : tabs) {
	        actionBar.addTab(actionBar.newTab().setText(tab_name)
	                .setTabListener(this));
	    }
	    
	   
	    /**
	     * on swiping the viewpager make respective tab selected
	     * */
	    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	
	        public void onPageSelected(int position) {
	            // on changing the page
	            // make respected tab selected
	            actionBar.setSelectedNavigationItem(position);
	        }
	
	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	        }
	
	        @Override
	        public void onPageScrollStateChanged(int arg0) {
	        }
	    });
	}
	
	
	void checkFirstTimeTasks()
	{
		SharedPreferences pref=getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
		String first=pref.getString("FirstTime", "None");
		if(first.equals("None"))
		{
			SharedPreferences.Editor editor=pref.edit();
			editor.putString("FirstTime", "DONE");
			editor.commit();
			Intent accpick=AccountPicker.newChooseAccountIntent(null,null,new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true,null,null,null,null);
			startActivityForResult(accpick,ACC_REQ);
			Toast.makeText(this, "Choose your account to register the application", Toast.LENGTH_LONG).show();
		}
	}
	
	public void checkServiceStatus()
	{
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
		{
			if (LocationService.class.getName().equals(service.service.getClassName())) 
		    {	LocationService myServ=LocationService.getInstance();
		    	return;
		    }
		}
		Intent serviceintent=new Intent(this,LocationService.class);
		startService(serviceintent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	
	
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	    viewPager.setCurrentItem(arg0.getPosition());
	}
	
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	    viewPager.setCurrentItem(arg0.getPosition());
		
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int reqCode,int resCode,Intent data)
	{	if(reqCode==ACC_REQ && resCode==RESULT_OK)
		{
			accname = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			RegisterApp rapp=new RegisterApp(this,accname);			
		}
		else
		{	if(data==null)	
			{	return;		}
			double lat,lng;
			String alias;
			lat=data.getDoubleExtra("latitude",0);
			lng=data.getDoubleExtra("longitude", 0);
			alias=data.getStringExtra("alias");
			
			//Database Entry
			MyDBHelper dbhelper=new MyDBHelper(this);
			Location_Alias loc=new Location_Alias(alias,lat,lng);
			if(!dbhelper.addLocation(loc))
			{	Toast.makeText(this, "Location name must be unique", Toast.LENGTH_LONG).show(); }
			initTabs();
		}
	}
}