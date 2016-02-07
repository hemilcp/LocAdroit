package com.example.locationassistant;

import com.example.DBHelper.MyDBHelper;
import com.example.BackgroundWorks.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RemiDetailsActivity extends ActionBarActivity implements DialogInterface.OnClickListener{

	EventsFragment evefrag = new EventsFragment();
	String caller;
	String title,note,location,date;
	int id,logid=-1;
	TextView tdata,ndata,ldata,ddata;
	
	MyDBHelper dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remi_details);
		caller=getIntent().getStringExtra("Caller");
		
		if(caller.equals("MainActivity"))
		{
			Button b1=(Button)findViewById(R.id.snoozeb);
			Button b2=(Button)findViewById(R.id.dismissb);
			b1.setVisibility(View.GONE);
			b2.setVisibility(View.GONE);
		}
		else if(caller.equals("Notification"))
		{
			Button b1=(Button)findViewById(R.id.delbut);
			b1.setVisibility(View.GONE);
			logid=getIntent().getIntExtra("LOG_ID", -1);
		}
		id=getIntent().getIntExtra("ID", -1);
		title=getIntent().getStringExtra("Title");
		note=getIntent().getStringExtra("Note");
		location=getIntent().getStringExtra("Location");
		date=getIntent().getStringExtra("Date");
		tdata=(TextView)findViewById(R.id.title_in);
		tdata.setText(title);
		ndata=(TextView)findViewById(R.id.note_in);
		ndata.setText(note);
		ldata=(TextView)findViewById(R.id.loc_in);
		ldata.setText(location);
		ddata=(TextView)findViewById(R.id.day_in);
		if(date==null || date.equals(""))
		{	ddata.setText("Daily");	}
		else
		{	ddata.setText(date);	}
		
		dbhelper=new MyDBHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remi_details, menu);
		return true;
	}
	
	public void delremi(View view)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Deletion");
		alert.setMessage("Do you want to delete ?");
		alert.setPositiveButton("Yes",this);
		alert.setNegativeButton("No",this);
		alert.show();
	}
	
	public void onSnooze(View v1)
	{
		dbhelper.changeLogStatus(logid, Constants.ACTION_SNOOZED);
		closeActivity();
	}

	public void onDismiss(View v1)
	{
		dbhelper.changeLogStatus(logid, Constants.ACTION_DISMISSED);
		closeActivity();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		if(logid!=-1)
		{	dbhelper.changeLogStatus(logid, Constants.ACTION_DISMISSED);		}
	}
	@Override
	public void onClick(DialogInterface arg0, int arg1) 
	{	switch(arg1)
		{	case DialogInterface.BUTTON_POSITIVE : 	Log.d("Reminder activity","Delete confirmed");
													MyDBHelper obj = new MyDBHelper(this);
													obj.deleteReminder(id);
													closeActivity();
													break;
								
			default: break;
		}
	}
	public void closeActivity()
	{
		if(logid==-1)
		{	Intent i = new Intent(this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		else
		{
			finish();
		}
	}
}
