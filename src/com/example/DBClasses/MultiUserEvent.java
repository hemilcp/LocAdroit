package com.example.DBClasses;

public class MultiUserEvent 
{	int mid,mtype,status;
	String title,note,date,email,loc_alias;
	public MultiUserEvent()
	{	}
	public MultiUserEvent(int m,String t,String n,String lcid,String dt,String em,int stat)
	{	mtype=m;
		loc_alias=lcid;
		title=t;
		note=n;
		date=dt;
		email=em;
		status=stat;
	}
	public void setId(int i)
	{	mid=i;	}
	public int getId()
	{	return mid;	}
	public int getType()
	{	return mtype;	}
	public String getLoc()
	{	return loc_alias;	}
	public String getTitle()
	{	return title;	}
	public String getNote()
	{	return note;	}
	public String getDate()
	{	return date;	}
	public String getEmail()
	{	return email;	}
	public int getStatus()
	{	return status;	}
}
