package com.example.DBClasses;

public class LocationReminder 
{	int id;
	String title,note,loc_alias;
	String date;
	public LocationReminder()
	{	}
	public LocationReminder(String t,String n,String lcid,String dt)
	{	loc_alias=lcid;
		title=t;
		note=n;
		date=dt;
	}
	public void setId(int i)
	{	id=i;	}
	public int getId()
	{	return id;	}
	public String getLoc()
	{	return loc_alias;	}
	public String getTitle()
	{	return title;	}
	public String getNote()
	{	return note;	}
	public String getDate()
	{	return date;	}
}
