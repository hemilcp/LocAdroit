package com.example.SysTasks;

import java.io.File;

import com.example.BackgroundWorks.Constants;
import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.*;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class TaskSoundActivity extends ActionBarActivity {

	MyDBHelper dbhelp;
	ChooseLoc chooseloc;
	PopupWindow popupWindowDogs;
	int flag=0;
	RadioGroup sound_loc_group;
	TextView sound_new_loc;
	Button sound_loc_change;
	String newloc,newPath;
	double newlat,newlng;
	int state,sound_state;
	AudioManager audiomanager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_sound);
		dbhelp = new MyDBHelper(this);
		 chooseloc = new ChooseLoc(this);
		 popupWindowDogs = chooseloc.popupWindowLocs();
		 audiomanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_sound, menu);
		return true;
	}
	
	
	
	public void modeSelected(View view)
	{
	    // Check which radio button was clicked
	    switch(view.getId()) 
	    {
	    	case R.id.ringermode: 	
	    							sound_state = 1;	    							
	    							ringChange();
	    							break;
	    								    							
	    	case R.id.silentmode:  newPath = "2";
	    							sound_state = 2;	    						
	    							break;
	    							
	    	case R.id.vibratemode: 	newPath = "3";
	    							sound_state = 3;
	    							break;
	    }
	    	
	}      
	    	
		public void ringChange()
		{
			/*		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
					startActivityForResult(intent,1);		
					*/							
			Intent intent1 = new Intent();  
            intent1.setAction(Intent.ACTION_GET_CONTENT);  
            intent1.setType("audio/*"); 
            intent1.addCategory(Intent.CATEGORY_OPENABLE);
            Log.d("Start", "Intent");
            startActivityForResult(intent1, 666);
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
					
					sound_loc_group = (RadioGroup) findViewById(R.id.sound_loc_group);
					sound_loc_group.setVisibility(View.GONE);
					
					sound_new_loc = (TextView) findViewById(R.id.sound_new_loc);
					sound_new_loc.setVisibility(View.VISIBLE);
					sound_new_loc.setText(chooseloc.locs[arg2]);
					
				    newloc = chooseloc.locs[arg2];
					
					sound_loc_change = (Button) findViewById(R.id.sound_loc_change);
					sound_loc_change.setVisibility(View.VISIBLE);
					sound_loc_change.setGravity(Gravity.RIGHT);
			    }
			}
		 		
		 public void locChangeBut(View view)
		 {
			 sound_new_loc.setText(null);
			 sound_new_loc.setVisibility(View.GONE);
			 sound_loc_change.setVisibility(View.GONE);
			 sound_loc_group.setVisibility(View.VISIBLE);
			 sound_loc_group.clearCheck();
		 }
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if (resultCode == RESULT_OK) {
		        switch (requestCode) {
		        case 666:
		               	 Uri i = data.getData();//getDATA
		            	 String s = getRealPathFromURI(this,i);
		            	 
		        	    File k = new File(s); //set File from path
		        	    Log.d("this", s+" "+k.length()+"  "+k.getPath());
		        	
		        	   if(s!=null){  //(file.exists
		        	    	String title = RingtoneManager.getRingtone(this, i).getTitle(this);
		        	    ContentValues values = new ContentValues();
		        	       values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
		        	       values.put(MediaStore.MediaColumns.TITLE, title);
		        	       values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		        	       values.put(MediaStore.MediaColumns.SIZE, k.length());
		        	       values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
		        	       values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		        	       values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		        	       values.put(MediaStore.Audio.Media.IS_ALARM, false);
		        	       values.put(MediaStore.Audio.Media.IS_MUSIC, false);
		        	       Log.d("Values", values.toString());
		       	        Uri uri = MediaStore.Audio.Media.getContentUriForPath(i.getPath());
		       	        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" +k.getAbsolutePath() + "\"", null);
		       	        Uri newUri  = getContentResolver().insert(uri, values);
		       	        newPath = newUri.toString();
		       	  /*      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
		       	        Uri ringuri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);
		        	   Log.d("Ringtone"," "+k.getAbsolutePath()+" "+uri.toString()+" set");
		        	*/
		       	        }
		        	else Log.d("It`s Not Done Yet", "Sorry");
		          break;

		        case 2:  
							if(data==null)	{ flag=0;   return; }
							
							newloc  = data.getStringExtra("alias");
							newlat=data.getDoubleExtra("latitude",0);
							newlng=data.getDoubleExtra("longitude", 0);
							sound_loc_group = (RadioGroup) findViewById(R.id.sound_loc_group);
							sound_loc_group.setVisibility(View.GONE);
							sound_new_loc = (TextView) findViewById(R.id.sound_new_loc);
							sound_new_loc.setVisibility(View.VISIBLE);
							sound_new_loc.setText(newloc);
							sound_loc_change = (Button) findViewById(R.id.sound_loc_change);
							sound_loc_change.setVisibility(View.VISIBLE);
							sound_loc_change.setGravity(Gravity.RIGHT);
							flag=1;
		        default:
		            break;
		        }
		    }
		}

public String getRealPathFromURI(Context context, Uri contentUri) {
  Cursor cursor = null;
  try { 
    String[] proj = { MediaStore.Audio.Media.DATA };
    cursor = context.getContentResolver().query(contentUri,proj, null, null, null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
    Log.d("Column Index", ""+column_index);
    cursor.moveToFirst();
    Log.d("Returned",cursor.getString(column_index).toString() );
    return cursor.getString(column_index);
  } finally {
    if (cursor != null) {
      cursor.close();
    }
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
		Log.d("Sound_state", ""+sound_state);
		
	/*	switch(sound_state)
		{
		case 1 : audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); Log.d("Ringtone", "Started"); break;
		case 2 : audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);  Log.d("Silent Mode", "silent done"); break;
		case 3 : audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  Log.d("Vibrate", "Vibrate mode on"); break;

		default : break;
		}
	*/
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
		SystemTasks sys = new SystemTasks(Constants.SOUND,newPath,loc,""+cnd);
		dbhelp.addSysTask(sys);
		Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
		
}
