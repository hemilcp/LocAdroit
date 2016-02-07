package com.example.locationassistant;

import com.example.DBClasses.*;
import com.example.DBHelper.*;
import java.util.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LocationsFragment extends Fragment implements OnItemClickListener,DialogInterface.OnClickListener {
	
	ArrayAdapter<String> adapter;
	Vector<Location_Alias> locations;
	ListView listView;
	MyDBHelper dbhelp;
	View rootView;
	String selectedLoc=null;
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_locations, container, false);
        dbhelp=new MyDBHelper(getActivity());
        getLocations();
        return rootView;
    }
	
	public void getLocations()
	{
		String locs[];
		locations=dbhelp.getLocations();
		if(locations==null)
		{
			locs=new String[1];
			locs[0]="<Add New>";
		}
		else
		{	locs=new String[locations.size()+1];
			locs[0]="<Add New>";
			for(int i=0;i<locations.size();i++)
			{
				locs[i+1]=locations.get(i).getAlias();
			}
		}
		adapter = new ArrayAdapter<String>(getActivity(),R.layout.locations_single, locs);
		listView = (ListView) rootView.findViewById(R.id.loc_listview);
  		listView.setAdapter(adapter);
  		listView.setOnItemClickListener(this);
	}
    
	@Override
	public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
	         selectedLoc =(String) (listView.getItemAtPosition(myItemInt));
	     //   Toast.makeText(getActivity(), selectedLoc, Toast.LENGTH_LONG).show();
	        if(selectedLoc.equals("<Add New>"))
	        {
	        	Intent intent=new Intent(getActivity(),PickLocationActivity.class);
	    		startActivityForResult(intent,1);
	    		Log.d("LocationFragment","Start Map Activity");
	        }
	        else
	        {
	        	AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
	    		alert.setTitle("Delete location");
	    		alert.setMessage("Are you sure you want to delete this location?\n(You will lose all your event data for this location)");
	    		alert.setPositiveButton("Delete",this);
	    		alert.setNegativeButton("Cancel",this);
	    		alert.show();
	        }
	    }    

@Override
public void onClick(DialogInterface dialog, int which) 
{
	switch(which)
	{	case DialogInterface.BUTTON_POSITIVE : 	int i=dbhelp.deleteloc(selectedLoc);
											    if(i!=0)
											    {   getLocations();	   }
											    else
											    {   Toast.makeText(getActivity(), "DELETE INVALID", Toast.LENGTH_LONG).show();	  }
		default: break;
	}	
}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(getActivity(), "onActivityResult", Toast.LENGTH_LONG).show();
		if(data==null)	
		{	return;		}
		double lat,lng;
		String alias;
		lat=data.getDoubleExtra("lattitude",0);
		lng=data.getDoubleExtra("longitude", 0);
		alias=data.getStringExtra("alias");
		
		//Database Entry
		Location_Alias loc=new Location_Alias(alias,lat,lng);
		if(!dbhelp.addLocation(loc))
		{	Toast.makeText(getActivity(), "Location name must be unique", Toast.LENGTH_LONG).show(); }
		else
		{	getLocations();	}
}


}