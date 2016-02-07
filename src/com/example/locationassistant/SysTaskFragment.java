package com.example.locationassistant;

import com.example.events.CardAdapterSys;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class SysTaskFragment extends Fragment {

	CardAdapterSys card;
	GridView gridview;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
    //Set The EVENTS page with Grid-view
       final CardAdapterSys card = new CardAdapterSys(this);
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(card);
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
   	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
   	            	
   	            	Intent intent = new Intent(getActivity(),SysdetailsActivity.class);
   	            	intent.putExtra("ID",card.syslist.get(position).getId());
   	            	intent.putExtra("Type",card.syslist.get(position).getType());
   	            	intent.putExtra("Attributes",card.syslist.get(position).getAttr());
   	            	intent.putExtra("Location",card.syslist.get(position).getLoc());
   	            	intent.putExtra("Condition",card.syslist.get(position).getCond());
   	            	startActivity(intent);
   	            }
   	    });
     return rootView;
    }


}
