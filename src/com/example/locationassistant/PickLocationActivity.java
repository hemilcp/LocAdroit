package com.example.locationassistant;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class PickLocationActivity extends ActionBarActivity implements OnMapLongClickListener,DialogInterface.OnClickListener{
	private GoogleMap mMap;
	private double lat=0,lng=0;
	private final int EDITTEXT_ID=0;
	EditText input;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_location);
		mMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setOnMapLongClickListener(this);
		Log.d("Pick Location","Started Activity");
		Toast.makeText(this, "Long press on the map to pick location",Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pick_location, menu);
		return true;
	}
	
	@Override
	public void onMapLongClick(LatLng point)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Location Name");
		alert.setMessage("Enter name for chosen location");

		// Set an EditText view to get user input
		lat=point.latitude;
		lng=point.longitude;
		input = new EditText(this);
		input.setId(EDITTEXT_ID);
		alert.setView(input);
		alert.setPositiveButton("Ok", this);
		alert.setNegativeButton("Cancel", this);
		alert.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.gmap_normal:
	            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            return true;
	        case R.id.gmap_satellite:
	            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which)
		{	case DialogInterface.BUTTON_POSITIVE : 	Intent intent=new Intent();
													intent.putExtra("latitude",lat);
													intent.putExtra("longitude",lng);
													intent.putExtra("alias",input.getText().toString());
													setResult(RESULT_OK,intent);
													Log.d("Pick Location Activty","Activity Finished");
													finish();
													break;
			default: break;
		}
	}
}