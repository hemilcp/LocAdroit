package com.example.locationassistant;

import com.example.DBClasses.*;
import com.example.DBHelper.*;

import java.util.Calendar;

import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;


public class AddReminderActivity extends ActionBarActivity
{

	Calendar c;
	MyDBHelper dbhelp;
	ChooseLoc chooseloc;
	TextView loc_new;
	RadioGroup map_group;
	int flag=0;
	String locf,newloc;
	double newlat,newlng;
    PopupWindow popupWindowDogs;   
	//ListView listViewDogs;
	ArrayAdapter<String> myadapter;
	Button loc_change;
	boolean dateflag=false,datechecked;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_reminder);
		c = Calendar.getInstance();
		dbhelp=new MyDBHelper(this);
		
        /*   initialize pop up window     */
		
//        popupWindowDogs = popupWindowDogs();
		  chooseloc = new ChooseLoc(this);
		  popupWindowDogs = chooseloc.popupWindowLocs();
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_reminder, menu);
		return true;
	}

	public void onRadioButtonClicked(View view)
	{
		 // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	    	case R.id.radio_favs:
	            if (checked)
	            {  	chooseloc.showLocLists(view);
	            	//chooseloc.listView.setAdapter(chooseloc.locsAdapter(locs));
	            	chooseloc.listView.setOnItemClickListener(new LocsOnItemClickListener());
	            	popupWindowDogs.showAtLocation(view, Gravity.AXIS_SPECIFIED,10,10);
	            }
	            break;
	            
	        case R.id.radio_maps:
	            if (checked)
	            {  Intent intent=new Intent(this,PickLocationActivity.class);
	    		   startActivityForResult(intent,1);   }
	    }
	}
	
	 public class LocsOnItemClickListener implements OnItemClickListener {
		   
		    @Override
		    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		        // get the text and set it as the button text
		    	//Toast.makeText(AddReminderActivity.this, "Selected Positon is: " + locs[arg2], 100).show();	       
		    	flag=0;
				popupWindowDogs.dismiss();
				
				map_group = (RadioGroup) findViewById(R.id.radio_group);
				map_group.setVisibility(View.GONE);
				
				loc_new = (TextView) findViewById(R.id.loc_alias);
				loc_new.setVisibility(View.VISIBLE);
				loc_new.setText(chooseloc.locs[arg2]);
				
				newloc = chooseloc.locs[arg2];
				
				loc_change = (Button) findViewById(R.id.loc_change);
				loc_change.setVisibility(View.VISIBLE);
				loc_change.setGravity(Gravity.RIGHT);
		    }

		}
	 
	 public void locChangeBut(View view)
	 {
		 loc_new.setText(null);
		 loc_new.setVisibility(View.GONE);
		 loc_change.setVisibility(View.GONE);
		 map_group.setVisibility(View.VISIBLE);
		 map_group.clearCheck();
	 }
	
	public void enterDetails(View v) {
    	String t1,nt,loc="",dt;
    	t1= ((EditText) findViewById(R.id.editText1)).getText().toString();
    	nt = ((EditText) findViewById(R.id.editText2)).getText().toString();
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
    	 
    	EditText date=(EditText) findViewById(R.id.editTextDate);
    	dt = date.getText().toString();
    	if(!(t1.equals("")) && !(nt.equals("")) && !(loc.equals(""))  && dateflag)
    	{
    		if(datechecked)
    		{	dt = date.getText().toString();
    			if(dt.equals(""))
    			{	Toast.makeText(this,"Enter the Date ", Toast.LENGTH_SHORT).show();}
    			else
    			{	addToDB(t1,nt,loc,dt);	}
    		}
    		else
    		{	dt=null;
    			addToDB(t1,nt,loc,dt);	
    		}    	
    	}
    	else
    	{	Toast.makeText(this,"Fill Up the Empty Fields", Toast.LENGTH_SHORT).show();	}
    }
    void addToDB(String t1,String nt,String loc,String dt)
    {
    	LocationReminder lmi=new LocationReminder(t1,nt,loc,dt);
    	dbhelp.addReminder(lmi);
    	Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    public void showDatePickerDialog(View v) {
    	android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void radioChanged(View v) {
    	TextView tv1 = (TextView) findViewById(R.id.textViewDate);
    	EditText et1 = (EditText) findViewById(R.id.editTextDate);
    	et1.setText("");
    	dateflag=true;
    	switch(v.getId()) {
    	case R.id.radioButtonOnce:
    		tv1.setVisibility(View.VISIBLE); et1.setVisibility(View.VISIBLE);
    		datechecked=true;
    		break;
    	case R.id.radioButtonEveryday:
    		tv1.setVisibility(View.GONE); et1.setVisibility(View.GONE);
    		datechecked=false;
    		break;
    	
    	}
    }
	
	
	@Override
	public void onActivityResult(int requestcode,int resultcode,Intent data)
	{
		if(data==null)	{ flag=0;   return; }
		
		newloc  = data.getStringExtra("alias");
		newlat=data.getDoubleExtra("latitude",0);
		newlng=data.getDoubleExtra("longitude", 0);
		map_group = (RadioGroup) findViewById(R.id.radio_group);
		map_group.setVisibility(View.GONE);
		loc_new = (TextView) findViewById(R.id.loc_alias);
		loc_new.setVisibility(View.VISIBLE);
		loc_new.setText(newloc);
		loc_change = (Button) findViewById(R.id.loc_change);
		loc_change.setVisibility(View.VISIBLE);
		loc_change.setGravity(Gravity.RIGHT);
		
		flag=1;
		
	}
}
