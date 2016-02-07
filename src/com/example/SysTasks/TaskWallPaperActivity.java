package com.example.SysTasks;

import com.example.BackgroundWorks.Constants;
import com.example.DBClasses.Location_Alias;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.*;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskWallPaperActivity extends ActionBarActivity {
	
	MyDBHelper dbhelp;
	ChooseLoc chooseloc;
	PopupWindow popupWindowDogs;
	int flag=0;
	RadioGroup wallp_loc_group;
	TextView wallp_new_loc,wallptext;
	ImageView img;
	Button wallp_loc_change,button;
	String newloc;
	double newlat,newlng;
	int state; // 1 for exiting & 0 for entering
	String path;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_wall_paper);
		dbhelp = new MyDBHelper(this);
		chooseloc = new ChooseLoc(this);
		 popupWindowDogs = chooseloc.popupWindowLocs();
		 button = (Button) findViewById(R.id.wallp);
		 wallptext = (TextView) findViewById(R.id.mode_in);
		 img = (ImageView) findViewById(R.id.img);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_wall_paper, menu);
		return true;
	}
	
	public void chooseWallp(View view)
	{
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    startActivityForResult(i,888); 
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	    super.onActivityResult(requestCode, resultCode, data); 

	    switch(requestCode) { 
	    case 888:
	        if(resultCode == RESULT_OK){  
	    
	            Uri selectedImage = data.getData();
	            path="content://media"+selectedImage.getPath();
	            button.setVisibility(View.GONE);
	            img.setVisibility(View.VISIBLE);
	            wallptext.setVisibility(View.VISIBLE);
	            img.setImageURI(Uri.parse(path));
	            String text = getRealName(this,Uri.parse(path));
	            wallptext.setText(text);
/*	            InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
	            yourSelectedImage = getResizedBitmap(yourSelectedImage);
	            Log.d("String", "Done Close");
	            WallpaperManager wm = WallpaperManager.getInstance(this);
	          
	            	try         { 	wm.setBitmap(yourSelectedImage);      } 
	            	catch (IOException e) {    Log.e("TAG", "Cannot set image as wallpaper", e);          }
	        }
	*/
	        break;	}
	        
	    case 2:
	    	if(data==null)	{ flag=0;   return; }
			
			newloc  = data.getStringExtra("alias");
			newlat=data.getDoubleExtra("latitude",0);
			newlng=data.getDoubleExtra("longitude", 0);
			wallp_loc_group = (RadioGroup) findViewById(R.id.wallp_loc_group);
			wallp_loc_group.setVisibility(View.GONE);
			wallp_new_loc = (TextView) findViewById(R.id.wallp_new_loc);
			wallp_new_loc.setVisibility(View.VISIBLE);
			wallp_new_loc.setText(newloc);
			
			wallp_loc_change.setVisibility(View.VISIBLE);
			wallp_loc_change.setGravity(Gravity.RIGHT);
			flag=1;
			break;
			
		default: break;
	    }
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
				
				wallp_loc_group = (RadioGroup) findViewById(R.id.wallp_loc_group);
				wallp_loc_group.setVisibility(View.GONE);
				
				wallp_new_loc = (TextView) findViewById(R.id.wallp_new_loc);
				wallp_new_loc.setVisibility(View.VISIBLE);
				wallp_new_loc.setText(chooseloc.locs[arg2]);
				
			    newloc = chooseloc.locs[arg2];
				
			    wallp_loc_change = (Button) findViewById(R.id.wallp_loc_change);
			    wallp_loc_change.setVisibility(View.VISIBLE);
			    wallp_loc_change.setGravity(Gravity.RIGHT);
		    }
		}
	 		
	 public void locChangeBut(View view)
	 {
		 wallp_new_loc.setText(null);
		 wallp_new_loc.setVisibility(View.GONE);
		 wallp_loc_change.setVisibility(View.GONE);
		 wallp_loc_group.setVisibility(View.VISIBLE);
		 wallp_loc_group.clearCheck();
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
	    Log.d("state", ""+state);
	    Log.d("Path", path);

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
		SystemTasks sys = new SystemTasks(Constants.WALLPAPER,path,loc,""+cnd);
		dbhelp.addSysTask(sys);
		Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
}
