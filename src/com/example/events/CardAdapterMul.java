package com.example.events;

import java.util.List;

import com.example.DBClasses.MultiUserEvent;
import com.example.DBHelper.MyDBHelper;
import com.example.locationassistant.MulUserFragment;

import android.view.*;
import android.widget.*;
import android.content.Context;
import android.graphics.Color;


public class CardAdapterMul extends BaseAdapter {
    
	private Context mContext;
    private MyDBHelper dbhelp;
    public List<MultiUserEvent> mulist;
    
    public CardAdapterMul(Context c) {
        mContext = c;
    }

    public CardAdapterMul(MulUserFragment topRatedFragment) {
		// TODO Auto-generated constructor stub
    	mContext = topRatedFragment.getActivity();
    	dbhelp = new MyDBHelper(mContext);
    	mulist = dbhelp.getAllMuserTasks(); 
    	}

	public int getCount() {
		if(mulist!=null)
		{ return mulist.size();  }
		else return 0;
	}

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	if(mulist!=null)
    	{
    	FrameLayout ll1;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	ll1 = new FrameLayout(mContext);
        	TextView note = new TextView(mContext);
         	TextView desc = new TextView(mContext);
         	TextView date = new TextView(mContext);
         	note.setText(mulist.get(position).getTitle().toString());
  			desc.setText("At "+mulist.get(position).getLoc().toString());
        	note.setTextSize(20.0f);
        	note.setPadding(10, 5, 5, 5);
        	note.setMaxLines(2);
        	note.setGravity(Gravity.TOP);
        	note.setTextColor(Color.WHITE);  
        	desc.setPadding(20,0, 0, 0);
        	desc.setGravity(Gravity.CENTER_VERTICAL);
        	desc.setTextColor(Color.WHITE);
        	date.setText(mulist.get(position).getEmail());
        	date.setPadding(20, 65, 20, 5);
        	date.setGravity(Gravity.BOTTOM);
        	date.setTextColor(Color.WHITE);
        	ll1.addView(note);
        	ll1.addView(desc);
        	ll1.addView(date);
        	final float scale = mContext.getResources().getDisplayMetrics().density;
        	int height=(int)(150*scale+0.5f);
        	ll1.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
        	ll1.setBackgroundColor(Color.parseColor("#333388"));
       	}
        	else {   ll1 = (FrameLayout) convertView;       }
        return ll1;
    	}
    else return null; }
}