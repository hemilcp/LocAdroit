package com.example.locationassistant;

import com.example.SysTasks.*;
import com.example.MultiUserPack.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AddOnsFragment extends Fragment {
	 
	View rootView;
    ListView syslistview;
    PopupWindow popupWindowSysList;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
          rootView = inflater.inflate(R.layout.fragment_addons, container, false);
          addremi();
          popupWindowSysList = popupWindowSysList();
          return rootView; 
    }
    
    public void addremi()
    {
       Button text1 = (Button) rootView.findViewById(R.id.addremitext);
       Button text2 = (Button) rootView.findViewById(R.id.addsystext);
       Button text3 = (Button) rootView.findViewById(R.id.addmulusertext);
       text1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(),AddReminderActivity.class);
    			startActivity(i);
			}
    	});
       
       text2.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				showTasksPopUp(view);			}
       	}); 
    	
       text3.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(),AddMultiuserTaskActivity.class);
				startActivity(i);
			}
       	});
    }
    
		    public void showTasksPopUp(View view)
		    {
		    	String systaskslist[] = {"Sound ","Battery" ,"Wi-fi" ,"Wallpaper" };
		    	syslistview.setAdapter(listAdapter(systaskslist));
		    	syslistview.setOnItemClickListener(new systasklistOnItemClickListener());
		    	popupWindowSysList.showAtLocation(view,Gravity.AXIS_SPECIFIED,10,10);

		    }
		    
		    public PopupWindow popupWindowSysList()
		    {
		    	 // initialize a pop up window type
		        PopupWindow popupWindow = new PopupWindow(getActivity());

		        // the drop down list is a list view
		        syslistview = new ListView(getActivity());
		        syslistview.setBackgroundColor(Color.BLACK);

		        // some other visual settings
		        popupWindow.setFocusable(true);
		        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		        //popupWindow.setHeight(400);
		        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		      
		        // set the list view as pop up window content
		        popupWindow.setContentView(syslistview);

		        return popupWindow;
		    }
		    
		    private ArrayAdapter<String> listAdapter(String list[])
		    {
		    	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list) {

			            @Override
			            public View getView(int position, View convertView, ViewGroup parent) {

			                // setting the ID and text for every items in the list
			                          
			                String text = getItem(position);              

			                // visual settings for the list item
			                TextView listItem = new TextView(getActivity());
			               // listItem.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
			               // listItem.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			                listItem.setText(text);
			                listItem.setTag(position);
			                listItem.setTextSize(25);
			                listItem.setPadding(15, 15, 15, 15);
			                listItem.setTextColor(Color.WHITE);
			              
			                return listItem;
			            }
			        };
			      
			        return adapter;
		    }
		    
		    public class systasklistOnItemClickListener implements OnItemClickListener{
		    	Intent i;
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					popupWindowSysList.dismiss();
					switch(arg2)
						{
						case 0:	 i = new Intent(getActivity(),TaskSoundActivity.class);
								startActivity(i);
								break;
						
						case 1:  i = new Intent(getActivity(),TaskBatteryActivity.class);
								startActivity(i);		
								break;
						case 2:  i = new Intent(getActivity(),TaskWiFiActivity.class);
								startActivity(i);	
								break;
						case 3: i = new Intent(getActivity(),TaskWallPaperActivity.class);
								startActivity(i);
						default: break;		
						}
					  }
				}

}

