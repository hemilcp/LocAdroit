package com.example.events;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.example.BackgroundWorks.Constants;
import com.example.DBClasses.SystemTasks;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.SysTaskFragment;

public class CardAdapterSys extends BaseAdapter {

		private Context mContext;
	    private MyDBHelper dbhelp;
	    public List<SystemTasks> syslist;
	    public CardAdapterSys(Context sysTaskFragment) {
	        mContext = sysTaskFragment;
	    }

		public CardAdapterSys(SysTaskFragment systask) {
			mContext = systask.getActivity();
	    	dbhelp = new MyDBHelper(mContext);
	    	syslist = dbhelp.getSysTasks();
		}

		public int getCount() {
		   if(syslist!=null)
			{
	    	   return syslist.size();
		   }
	       else
	       {	return 0;	}
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    
	    
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	
	    	if(syslist!=null)
	    	{
	    	FrameLayout ll1;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	        	ll1 = new FrameLayout(mContext);
	        	TextView note = new TextView(mContext);
	        	note.setTextSize(20.0f);
	        	note.setPadding(10, 5, 5, 5);
	        	note.setGravity(Gravity.TOP);
	        	note.setTextColor(Color.WHITE);
	        	TextView desc = new TextView(mContext);
	        	switch(syslist.get(position).getType())
	        	{
	        	case Constants.SOUND:  	note.setText("Sound");
	        				if(!(syslist.get(position).getAttr().equals("2") || syslist.get(position).getAttr().equals("3")))
	        				{
	        				Uri path = Uri.parse(syslist.get(position).getAttr().toString());
	        				String title = RingtoneManager.getRingtone(mContext, path).getTitle(mContext).toString();
			        		desc.setText( title+".mp3"+"\n @   "+syslist.get(position).getLoc());
	        				break;
	        				}
	        				if(syslist.get(position).getAttr().equals("2"))
	        				{	desc.setText("Silent Mode "+"\n @ "+syslist.get(position).getLoc());	break;	}
	        				desc.setText("Vibrate Mode "+"\n @ "+syslist.get(position).getLoc());
	        				break;
	        	case Constants.BATTERY:     note.setText("Battery");
	        				desc.setText("Alert if battery < "+syslist.get(position).getAttr().toString()+"%"+"\n @ "+syslist.get(position).getLoc());
        					break;
	           	case Constants.WIFI:     note.setText("Wi-Fi");
	           				String onoff;  if (syslist.get(position).getAttr().toString().equals("0")){onoff = "ON";} else onoff = "OFF";
							desc.setText("Turn Wi-fi "+onoff+" \n @ "+syslist.get(position).getLoc());
							break;
	           	case Constants.WALLPAPER:     note.setText("Wallpaper");
	           				desc.setText("set new Wallpaper"+" \n @ "+syslist.get(position).getLoc());
	           				break;
	        	}
	        	desc.setGravity(Gravity.CENTER_VERTICAL);
	        	desc.setPadding(20,0, 5, 5);
	        	desc.setTextColor(Color.WHITE);
	        	ll1.addView(note);
	        	ll1.addView(desc);
	        	final float scale = mContext.getResources().getDisplayMetrics().density;
	        	int height=(int)(150*scale+0.5f);
	        	ll1.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
	        
	        	switch(syslist.get(position).getType())
	        	{
	        	case 1 : ll1.setBackgroundColor(Color.parseColor("#333388")); break;
	        	case 2 : ll1.setBackgroundColor(Color.parseColor("#009900")); break;
	        	case 3 : ll1.setBackgroundColor(Color.parseColor("#cc1100")); break;
	        	case 4 : ll1.setBackgroundColor(Color.parseColor("#dc0477")); break;
	        	}
	       	}
	        	else {   ll1 = (FrameLayout) convertView;       }
	        return ll1;
	    	}
	    else return null; }
	
}
