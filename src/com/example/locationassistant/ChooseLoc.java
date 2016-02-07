package com.example.locationassistant;

import java.util.Vector;

import com.example.DBClasses.Location_Alias;
import com.example.DBHelper.MyDBHelper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseLoc {
		
		private Context mContext;
		public ListView listView;
		MyDBHelper dbhelp;
		public String locs[];
		PopupWindow popupWindow;
		int flag;
		
		public ChooseLoc(Context context)
		{
			mContext = context;
			dbhelp = new MyDBHelper(mContext);
		}
		
	 public PopupWindow popupWindowLocs() {

	        // initialize a pop up window type
	        PopupWindow popupWindow = new PopupWindow(mContext);

	        // the drop down list is a list view
	        listView = new ListView(mContext);
	      

	        // some other visual settings
	        popupWindow.setFocusable(true);
	        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
	        //popupWindow.setHeight(400);
	        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	      
	        // set the list view as pop up window content
	        popupWindow.setContentView(listView);

	        return popupWindow;
	    }
	 
	 public void showLocLists(View view)
	    {	
	    	Vector<Location_Alias> locations=dbhelp.getLocations();
			if(locations==null)
			{	Toast.makeText(mContext,"No Locations Exist",Toast.LENGTH_SHORT).show();
				return;
			}
			else
			{	locs=new String[locations.size()];
				for(int i=0;i<locations.size();i++)
				{
					locs[i]=locations.get(i).getAlias();
				}
			}
		//	return locs;
	        // set our adapter and pass our pop up window contents
	      listView.setAdapter(locsAdapter(locs));
	      
	        // set the item click listener
	   //   listView.setOnItemClickListener(new LocsDropdownOnItemClickListener());
	   // 	popupWindow.showAtLocation(view,Gravity.AXIS_SPECIFIED,10,10);
	    }
	 	
	 public ArrayAdapter<String> locsAdapter(String locs[]) {

	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, locs) {

	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) {

	                // setting the ID and text for every items in the list
	                          
	                String text = getItem(position);              

	                // visual settings for the list item
	                TextView listItem = new TextView(mContext);

	                listItem.setText(text);
	                listItem.setTag(position);
	                listItem.setTextSize(22);
	                listItem.setPadding(10, 10, 10, 10);
	                listItem.setTextColor(Color.WHITE);
	              
	                return listItem;
	            }
	        }; 
	        return adapter;
	    }
}
