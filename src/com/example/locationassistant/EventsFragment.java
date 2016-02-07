package com.example.locationassistant;

import com.example.events.CardAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class EventsFragment extends Fragment {
	 
	CardAdapter cardadp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
    //Set The EVENTS page with Grid-view
       final CardAdapter cardadp = new CardAdapter(this);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(cardadp);
        gridview.setOnItemClickListener(new OnItemClickListener() {
   	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
   	            	//Toast.makeText(getActivity(),cardadp.remlist.get(position).getTitle().toString(), Toast.LENGTH_SHORT).show();
   	            	//getActivity().finish();
   	            	Intent intent = new Intent(getActivity(),RemiDetailsActivity.class);
   	            	intent.putExtra("Caller", "MainActivity");
   	            	intent.putExtra("ID",cardadp.remlist.get(position).getId());
   	            	intent.putExtra("Title",cardadp.remlist.get(position).getTitle());
   	            	intent.putExtra("Note",cardadp.remlist.get(position).getNote());
   	            	intent.putExtra("Location",cardadp.remlist.get(position).getLoc());
   	            	intent.putExtra("Date",cardadp.remlist.get(position).getDate());
   	            	startActivity(intent);
   	            	//getActivity().finishFromChild(new RemiDetailsActivity());
   	        }
   	    });

   
        return rootView;
    }
}


