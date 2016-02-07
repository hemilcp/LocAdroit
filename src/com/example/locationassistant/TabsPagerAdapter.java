package com.example.locationassistant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

	public class TabsPagerAdapter extends FragmentPagerAdapter  {
		 
	    public TabsPagerAdapter(FragmentManager fm) {
	        super(fm);	 }
	 
	    @Override
	    public Fragment getItem(int index) {
	 
	        switch (index) {
	        case 0:
	            // Reminders fragment activity
	        	   return new EventsFragment();
	        case 1:
	        	//Systask Fragment
	        	return new SysTaskFragment();
	        case 2:
	        	//Muluser fragment
	           return new MulUserFragment();
	        case 3:
	        	 // Locations fragment activity
	            return new LocationsFragment();
	        case 4:
	        	// Add-Ons fragment activity
	            return new AddOnsFragment();
	        }
	        return null;
	    }
	 
	    @Override
	    public int getCount() {
	        // get item count - equal to number of tabs
	        return 5;
	    }
	}


