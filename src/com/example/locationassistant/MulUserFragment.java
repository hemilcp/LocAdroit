package com.example.locationassistant;

import com.example.MultiUserPack.MulUserDetailsActivity;
import com.example.events.CardAdapterMul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MulUserFragment extends Fragment {
	 
	CardAdapterMul cardadp;
	GridView gridview;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_mul_user, container, false);
    //Set The EVENTS page with Grid-view
       final CardAdapterMul cardadp = new CardAdapterMul(this);
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(cardadp);
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
   	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
   	            	
   	            	Intent intent = new Intent(getActivity(),MulUserDetailsActivity.class);
   	            	intent.putExtra("ID",cardadp.mulist.get(position).getId());
   	            	intent.putExtra("Title",cardadp.mulist.get(position).getTitle());
   	            	intent.putExtra("Note",cardadp.mulist.get(position).getNote());
   	            	intent.putExtra("Location",cardadp.mulist.get(position).getLoc());
   	            	intent.putExtra("Date",cardadp.mulist.get(position).getDate());
   	            	intent.putExtra("Type",cardadp.mulist.get(position).getType());
   	            	intent.putExtra("Email",cardadp.mulist.get(position).getEmail());
   	            	startActivity(intent);
   	            }
   	    });
     return rootView;
    }
}


