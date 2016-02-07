package com.example.SysTasks;

import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.*;
import com.example.BackgroundWorks.*;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TaskBatteryActivity extends ActionBarActivity implements OnSeekBarChangeListener {

	private SeekBar seekbar;
	TextView textview;
	int progress = 0;
	ChooseLoc chooseloc;
	PopupWindow popupWindowDogs;
	RadioGroup bat_loc_group;
	TextView bat_loc_alias; 
	Button loc_change;
	int flag=0;
    String newloc;
	double newlat,newlng;
	int state;
	MyDBHelper dbhelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_battery);
		seekbar = (SeekBar) findViewById(R.id.batterybar);
		textview = (TextView) findViewById(R.id.battery_level);
		chooseloc = new ChooseLoc(this);
		 popupWindowDogs = chooseloc.popupWindowLocs();
			dbhelp=new MyDBHelper(this);

		// Initialize the textview with '0'
		 textview.setText(seekbar.getProgress() + "%");
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				progress = arg1;
				textview.setText(progress + "%");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_battery, menu);
		return true;
	}
	
	public void selectLocation(View view)
	{
		 // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	    	case R.id.choosefav:
	            if (checked)
	            {  	chooseloc.showLocLists(view);
	            	//chooseloc.listView.setAdapter(chooseloc.locsAdapter(locs));
	            	chooseloc.listView.setOnItemClickListener(new LocsOnItemClickListener());
	            	popupWindowDogs.showAtLocation(view, Gravity.AXIS_SPECIFIED,10,10);
	            }
	            break;
	            
	        case R.id.addnew:
	            if (checked)
	            {   Intent intent=new Intent(this,PickLocationActivity.class);
	    			startActivityForResult(intent,2);  }
	    }
	}

	
	public class LocsOnItemClickListener implements OnItemClickListener {
		    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		        // get the text and set it as the button text
		    	//Toast.makeText(AddReminderActivity.this, "Selected Positon is: " + locs[arg2], 100).show();	       
		    	flag=0;
				popupWindowDogs.dismiss();
				
				bat_loc_group = (RadioGroup) findViewById(R.id.bat_loc_group);
				bat_loc_group.setVisibility(View.GONE);
				
				bat_loc_alias = (TextView) findViewById(R.id.bat_loc_alias);
				bat_loc_alias.setVisibility(View.VISIBLE);
				bat_loc_alias.setText(chooseloc.locs[arg2]);
				
			    newloc = chooseloc.locs[arg2];
				
				loc_change = (Button) findViewById(R.id.loc_change);
				loc_change.setVisibility(View.VISIBLE);
				loc_change.setGravity(Gravity.RIGHT);
		    }
	}
	
	public void locChangeBut(View view)
	 {
		 bat_loc_alias.setText(null);
		 bat_loc_alias.setVisibility(View.GONE);
		 loc_change.setVisibility(View.GONE);
		 bat_loc_group.setVisibility(View.VISIBLE);
		 bat_loc_group.clearCheck();
	 }

	@Override
	public void onActivityResult(int requestcode,int resultcode,Intent data)
	{
		if(data==null)	{ flag=0;   return; }
		
		newloc  = data.getStringExtra("alias");
		newlat=data.getDoubleExtra("latitude",0);
		newlng=data.getDoubleExtra("longitude", 0);
		bat_loc_group = (RadioGroup) findViewById(R.id.bat_loc_group);
		bat_loc_group.setVisibility(View.GONE);
		bat_loc_alias = (TextView) findViewById(R.id.bat_loc_alias);
		bat_loc_alias.setVisibility(View.VISIBLE);
		bat_loc_alias.setText(newloc);
		loc_change = (Button) findViewById(R.id.loc_change);
		loc_change.setVisibility(View.VISIBLE);
		loc_change.setGravity(Gravity.RIGHT);
		
		flag=1;
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		}

	public void selectWhen(View view)
	{
		 boolean checked = ((RadioButton) view).isChecked();
		    switch(view.getId())
		    {
		    case R.id.entering : if(checked) state = 0; break;
		    case R.id.exiting : if(checked) state = 1; break;
		    default : break;
		    }
	}


	public void saveTask(View view)
	{
		Log.d("state",""+state);
		Log.d("Progress", ""+progress);
		String loc = null;
		
		if(flag==1)
    	{
    		Location_Alias loc1=new Location_Alias(newloc,newlat,newlng);
    		if(!dbhelp.addLocation(loc1))
    		{	Toast.makeText(this, "Location name must be unique", Toast.LENGTH_LONG).show(); }
    		else
    		{	loc = newloc;  		}
    	}
    	else
    	{	loc=newloc;    	}
		
		String cnd="";
		if(state==0)
		{	cnd=Constants.LOC_ENTERED;	}
		else
		{	cnd=Constants.LOC_LEAVING;	}
		SystemTasks sys = new SystemTasks(Constants.BATTERY,""+progress,loc,cnd);
		dbhelp.addSysTask(sys);
		Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}

}

