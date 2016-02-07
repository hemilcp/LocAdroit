package com.example.MultiUserPack;

import com.example.BackgroundWorks.*;
import com.example.DBClasses.*;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.R;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.support.v7.app.ActionBarActivity;

public class MultiUserNotifyActivity extends ActionBarActivity 
{
	TextView emailtext,loctext,datetext;
	Intent intent;
	String loc;
	double lat,lng;
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiuser_notify);
		intent=getIntent();
		
		emailtext=(TextView)findViewById(R.id.emailtext1);
		loctext=(TextView)findViewById(R.id.addrtext1);
		datetext=(TextView)findViewById(R.id.datetext1);
		emailtext.setText(getIntent().getStringExtra("email"));
		loctext.setText(getIntent().getStringExtra("address"));
		datetext.setText(getIntent().getStringExtra("date"));
		
		Log.d("Email",intent.getStringExtra("email"));
		Log.d("Address",intent.getStringExtra("address"));
		Log.d("Latitude",""+intent.getDoubleExtra("latitude",0));
		Log.d("Longitude",""+intent.getDoubleExtra("longitude",0));
		lat=intent.getDoubleExtra("latitude",0);
		lng=intent.getDoubleExtra("longitude",0);
	}
	public void onPermitClick(View view)
	{
		switch(view.getId())
		{
			case R.id.okButton1 : Log.d("NotificationActivity","Permission Granted");	sendResponseToServer(1);	break;
			case R.id.cancelButton1 : Log.d("NotificationActivity","Permission Denied");	sendResponseToServer(2);	break;
		}
	}
	
	public void sendResponseToServer(int ans)
	{
		if(ans==1)
		{	Location_Alias loc=new Location_Alias(intent.getStringExtra("address"),lat,lng);
			MultiUserEvent me=new MultiUserEvent(Constants.RECEIVER,null,null,loctext.getText().toString(),datetext.getText().toString(),emailtext.getText().toString(),Constants.GOT_EVENTID);
			me.setId(intent.getIntExtra("eventid", -1));
			MyDBHelper dbhelper=new MyDBHelper(this);
			dbhelper.addLocation(loc);
			dbhelper.addMultiUserEvent(me);
		}
		RespondToServer rs=new RespondToServer(this,intent.getIntExtra("eventid", -1),ans);
		finish();
	}
}
