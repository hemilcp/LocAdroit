package com.example.SysTasks;

import com.example.BackgroundWorks.Constants;
import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.*;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskWiFiActivity extends ActionBarActivity {
	
	MyDBHelper dbhelp;
	ChooseLoc chooseloc;
	PopupWindow popupWindowDogs;
	int flag=0;
	RadioGroup wifi_loc_group;
	TextView wifi_new_loc;
	Button wifi_loc_change;
	String newloc;
	double newlat,newlng;
	int state,wifi_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_wi_fi);
		dbhelp = new MyDBHelper(this); 
		chooseloc = new ChooseLoc(this);
		 popupWindowDogs = chooseloc.popupWindowLocs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_wi_fi, menu);
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
		   
		    @Override
		    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		        // get the text and set it as the button text
		    	//Toast.makeText(AddReminderActivity.this, "Selected Positon is: " + locs[arg2], 100).show();	       
		    	flag=0;
				popupWindowDogs.dismiss();
				
				wifi_loc_group = (RadioGroup) findViewById(R.id.wifi_loc_group);
				wifi_loc_group.setVisibility(View.GONE);
				
				wifi_new_loc = (TextView) findViewById(R.id.wifi_new_loc);
				wifi_new_loc.setVisibility(View.VISIBLE);
				wifi_new_loc.setText(chooseloc.locs[arg2]);
				
			    newloc = chooseloc.locs[arg2];
				
				wifi_loc_change = (Button) findViewById(R.id.wifi_loc_change);
				wifi_loc_change.setVisibility(View.VISIBLE);
				wifi_loc_change.setGravity(Gravity.RIGHT);
		    }
		}
	 		
	 public void locChangeBut(View view)
	 {
		 wifi_new_loc.setText(null);
		 wifi_new_loc.setVisibility(View.GONE);
		 wifi_loc_change.setVisibility(View.GONE);
		 wifi_loc_group.setVisibility(View.VISIBLE);
		 wifi_loc_group.clearCheck();
	 }
	
	 @Override
		public void onActivityResult(int requestcode,int resultcode,Intent data)
		{
			if(data==null)	{ flag=0;   return; }
			
			newloc  = data.getStringExtra("alias");
			newlat=data.getDoubleExtra("latitude",0);
			newlng=data.getDoubleExtra("longitude", 0);
			wifi_loc_group = (RadioGroup) findViewById(R.id.wifi_loc_group);
			wifi_loc_group.setVisibility(View.GONE);
			wifi_new_loc = (TextView) findViewById(R.id.wifi_new_loc);
			wifi_new_loc.setVisibility(View.VISIBLE);
			wifi_new_loc.setText(newloc);
			wifi_loc_change = (Button) findViewById(R.id.wifi_loc_change);
			wifi_loc_change.setVisibility(View.VISIBLE);
			wifi_loc_change.setGravity(Gravity.RIGHT);
			flag=1;
		}

	
	
	public void wifiChange(View view)
	{
		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId())
		{
		case R.id.on: if(checked) wifi_state = 1; break;
		case R.id.off: if(checked) wifi_state = 0; break;
		}
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
		Log.d("Wifi_State", ""+wifi_state);
	/*	boolean booleanValue ;
		if(wifi_state == 1)
		{booleanValue = true; }
		else booleanValue = false;
		WifiManager wManager = (WifiManager)this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
		wManager.setWifiEnabled(booleanValue); //true or false	*/
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
		SystemTasks sys = new SystemTasks(Constants.WIFI,""+wifi_state,loc,""+cnd);
		dbhelp.addSysTask(sys);
		Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
}
