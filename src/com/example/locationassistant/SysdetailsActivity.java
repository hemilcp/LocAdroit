package com.example.locationassistant;

import com.example.BackgroundWorks.Constants;
import com.example.DBHelper.MyDBHelper;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SysdetailsActivity extends ActionBarActivity implements DialogInterface.OnClickListener {

	SysTaskFragment sysf = new SysTaskFragment();
	String mode,loc,when;
	int id,type;
	TextView tdata,mdata,wdata,ldata,occu;
	ImageView im;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sysdetails);
		id = getIntent().getIntExtra("ID", -1);
		type = getIntent().getIntExtra("Type",-1);
		mode =  getIntent().getStringExtra("Attributes");
		when =  getIntent().getStringExtra("Condition");
		if(when.equals(Constants.LOC_ENTERED))
		{ when = "Entering the Location"; }
		else { when = "Levaing the Location"; }
		loc  =  getIntent().getStringExtra("Location");
		im = (ImageView) findViewById(R.id.img);
		tdata = (TextView) findViewById(R.id.type);
		switch(type)
		{
		case Constants.SOUND : tdata.setText("Sound Status"); break;
		case Constants.BATTERY : tdata.setText("Battery Status"); break;
		case Constants.WIFI : tdata.setText("Wif-Fi Status"); break;
		case Constants.WALLPAPER : tdata.setText("Wallpaper Status"); break;
		default : break;
		}
		mdata = (TextView) findViewById(R.id.mode_in);
		switch(type)
		{
		case Constants.SOUND : im.setVisibility(View.GONE);
								if(!(mode.equals("2") || mode.equals("3")))
								{
								Uri path = Uri.parse(mode);
								String title = RingtoneManager.getRingtone(this, path).getTitle(this).toString()+".oog";
								mdata.setText("New Ringtone : "+"\n\n "+ title);
								break;
								}
								if(mode.equals("2"))
								{	mdata.setText("Silent Mode");	break;	}
								mdata.setText(" Vibrate Mode");     break;
		case Constants.BATTERY : im.setVisibility(View.GONE);   mdata.setText("Battery level : "+mode+"%"); break;
		case Constants.WIFI :im.setVisibility(View.GONE);   String abc; if (mode.equals("0")){ abc = "ON";}else{abc = "OFF";}mdata.setText("Wif-Fi status : "+abc); break;
		case Constants.WALLPAPER : {	im.setVisibility(View.VISIBLE);
										im.setImageURI(Uri.parse(mode));
										mdata.setText(getRealName(this,Uri.parse(mode))); break; }
		default : break;
		}
		wdata = (TextView) findViewById(R.id.when_in);
		wdata.setText("When You "+when);		
		ldata = (TextView) findViewById(R.id.loc_in);
		ldata.setText(loc);
	}
	
	public String getRealName(Context context , Uri uri)
    {
    	Cursor cursor = null;
    		    String[] proj = { MediaStore.Images.Media.DATA };
    		    cursor = context.getContentResolver().query(uri,proj, null, null, null);
    		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
    		    Log.d("Column Index", ""+column_index);
    		    cursor.moveToFirst();
    		    Log.d("Returned",cursor.getString(column_index).toString() );
    		    return cursor.getString(column_index);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sysdetails, menu);
		return true;
	}

	public void delremi(View view)
	{
		Log.d(ACTIVITY_SERVICE, "DELETE PRESSED");AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Deletion");
		alert.setMessage("Do you want to delete ?");
		alert.setPositiveButton("Yes",this);
		alert.setNegativeButton("No",this);
		alert.show();
	}

	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		switch(arg1)
		{	case DialogInterface.BUTTON_POSITIVE : 	Log.d("System_Task activity","Delete confirmed");
													MyDBHelper obj = new MyDBHelper(this);
													obj.deleteSysTask(id);
													Intent i = new Intent(this,MainActivity.class);
													i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(i);
												//	finish();
												break;
								
			default: break;
		}
	}
}
